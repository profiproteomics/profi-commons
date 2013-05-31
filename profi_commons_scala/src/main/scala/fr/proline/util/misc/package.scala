package fr.proline.util

import java.util.concurrent.atomic.AtomicLong

/** Miscellaneous helpers */
package object misc {

  trait InMemoryIdGen {

    private val inMemoryIdSequence = new AtomicLong(0)

    def generateNewId(): Long = { inMemoryIdSequence.decrementAndGet() }

  }

}
