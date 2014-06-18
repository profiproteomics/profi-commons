package fr.proline.util

import java.util.concurrent.atomic.AtomicLong

/** Miscellaneous helpers */
package object misc {
  
  object IfNotNull {
    def apply[I,O]( i: I )( fn: => O ): Option[O] = {
      if( i != null ) Some( fn ) else None
    }
  }
  
  object MapIfNotNull {
    def apply[I,O]( i: I )( fn: I => O ): Option[O] = {
      if( i != null ) Some( fn(i) ) else None
    }
  }

  trait InMemoryIdGen {

    private val inMemoryIdSequence = new AtomicLong(0)

    def generateNewId(): Long = { inMemoryIdSequence.decrementAndGet() }

  }

}
