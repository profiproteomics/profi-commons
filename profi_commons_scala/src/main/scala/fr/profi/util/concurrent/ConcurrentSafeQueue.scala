package fr.profi.util.concurrent

import java.nio.file._

import scala.collection.JavaConversions._
import scala.collection.mutable.PriorityQueue
import scala.collection.mutable.Queue
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.control.Breaks._

import com.typesafe.scalalogging.LazyLogging


/** Concurrent safe queue **/

// Code inspired from: http://studio.cs.hut.fi/snippets/producer.html
abstract class ConcurrentSafeQueue[T] extends LazyLogging {
  
  def priorityQueueOrdering: Option[Ordering[T]]
  
  protected trait InternalQueueOps[T] {
    def dequeue(): T
    def enqueue(elems: T*): Unit
  }
  protected class InternalPriorityQueue[T]()(implicit ord: Ordering[T]) extends PriorityQueue[T]() with InternalQueueOps[T]
  protected class InternalQueue[T]() extends Queue[T]() with InternalQueueOps[T]

  // To be defined by implementations
  def maxSize: Option[Int]

  private var isStopped = false

  // Here is the queue - not nothing fancy about it. Just a normal scala.collection.mutable.queue
  // In addition to being a queue, it is also the lock for our AbstractQueue
  // It is also the resource we are trying to protect using our AbstractQueue
  protected lazy val internalQueue = {
    if( priorityQueueOrdering.isDefined )
      new InternalPriorityQueue[T]()(priorityQueueOrdering.get)
    else
      new InternalQueue[T]()
  }

  def clear() = internalQueue.clear()
  def hasEntries(): Boolean = !internalQueue.isEmpty
  def stop() = synchronized {
    // Dequeue all entries
    /*while( this.hasEntries() ) {
      this.dequeue()
    }*/

    isStopped = true
  }

  // This method is used to dequeue an entry (T)...
  // Take attentation to the internalQueue.synchronized
  // This says that to enter the code inside synchronized you should holds the lock of the AbstractQueue  
  def dequeue(): T = internalQueue.synchronized {
    if (isStopped) return null.asInstanceOf[T]

    // This is a classic way to implement waiting on a resource.
    // The check is enclosed in a while loop so that if the thread is woken
    // but if there is nothing to do, it goes back to waiting

    // If the queue is empty (no entry in the queue)
    while (internalQueue.isEmpty) {
      logger.trace("Waiting for new entries in the queue...")

      // As it is empty we cannot do anything now and must go to waiting
      // This is done by calling the wait method of the lock object of the queue
      internalQueue.wait()
    }

    // If we are here, it must be possible to pick up a queue entry
    // This will notify a consumer that may waiting for
    internalQueue.notifyAll()

    // Now let's just return our entry and leave the method (and the synchronized piece of code)
    // so that other threads can enter.
    internalQueue.dequeue()
  }

  // This is the exact same thing but for bringing queue entries.
  // We don't have to repeat the explanations here
  def enqueue(queueEntry: T): Unit = internalQueue.synchronized {
    if (isStopped) return ()

    if (maxSize.isDefined) {
      val size = maxSize.get
      while (internalQueue.size >= size) {
        internalQueue.wait()
      }
    }

    internalQueue.notifyAll()

    // Puts a queue entry into the queue
    internalQueue.enqueue(queueEntry)
  }

  protected val exceptionQueue = new Queue[Throwable]()
  def hasExceptions(): Boolean = !exceptionQueue.isEmpty

  def dequeueException(): Throwable = exceptionQueue.synchronized {

    while (exceptionQueue.isEmpty) {
      logger.trace("Watching for new exception in the queue...")
      exceptionQueue.wait()
    }

    exceptionQueue.notifyAll()
    exceptionQueue.dequeue()
  }

  def enqueueException(t: Throwable): Unit = exceptionQueue.synchronized {
    exceptionQueue.notifyAll()
    exceptionQueue.enqueue(t)
  }
}

/** Concurrent safe queue cosummer **/
trait ConcurrentSafeQueueConsumer[T] extends LazyLogging {

  // Methods to be implemented
  def consumerNumber: Int
  def queue: ConcurrentSafeQueue[T]
  protected def consumeQueueEntry(queueEntry: T): Unit
  implicit val execCtx: ExecutionContext

  def consumeQueue(): Future[Unit] = {

    // Here is the File consumer code written in a Future block
    val future = Future {
      var hasFinished = false

      /* Consume the queue while the file is being read and until queue is empty */
      while (hasFinished == false) {
        val queueEntry = queue.dequeue()

        // TODO: find a better way to exit the consumer
        // This example could help: http://stackoverflow.com/questions/16009837/how-to-cancel-future-in-scala
        if (queueEntry == null) {
          hasFinished = true
        } else {
          /* Execute callback */
          consumeQueueEntry(queueEntry)
        }
      }
    }

    /* Error hanlding */
    future.onFailure {
      case e =>
        logger.error(s"Exiting consumer ${consumerNumber} with error", e)

        queue.enqueueException(e)

        // Report exception to the execution context
        execCtx.reportFailure(e)
    }

    future
  }

}