package fr.proline.util

import java.util.concurrent.atomic.AtomicInteger

/** Miscellaneous helpers */
package object misc {

  trait InMemoryIdGen {
    private val inMemoryIdSequence = new AtomicInteger(0)

    def generateNewId(): Int = { inMemoryIdSequence.decrementAndGet() }

  }
  
}
