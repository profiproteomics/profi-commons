package fr.profi.util

import scala.collection.TraversableLike
import scala.collection.mutable.LongMap

/**
 * @author David Bouyssie
 *
 */
package object collection {

  trait LongMapBuilderFromTuplesOps {
    @inline final protected def initLongMap[A, V](xs: TraversableOnce[A]): LongMap[V] = {
      if (xs.isTraversableAgain) new LongMap[V](xs.size) else new LongMap[V]()
    }
    @inline final protected def fillLongMap[V](xs: TraversableOnce[(Long, V)], longMap: LongMap[V]) = {
      longMap ++= xs
    }
    @inline final protected def toLongMap[V](xs: TraversableOnce[(Long, V)]): LongMap[V] = {
      val longMap = this.initLongMap[(Long, V), V](xs)
      this.fillLongMap(xs, longMap)
      longMap
    }
  }

  trait LongMapBuilderFromTraversableOnceOps extends LongMapBuilderFromTuplesOps {
    @inline final protected def toLongMap[A, V](xs: TraversableOnce[A], kvMapping: A => (Long, V)): LongMap[V] = {
      val longMap = this.initLongMap[A, V](xs)

      val xsIter = xs.toIterator
      while (xsIter.hasNext) {
        longMap += kvMapping(xsIter.next())
      }

      longMap
    }
    @inline final protected def mapByLong[A](xs: TraversableOnce[A], byKey: A => Long): LongMap[A] = {
      this.toLongMap(xs, { x: A => (byKey(x), x) })
    }
  }

  implicit class LongMapBuilderFromTraversableTuples[A](xs: TraversableOnce[(Long, A)]) extends LongMapBuilderFromTuplesOps {
    def toLongMap(): LongMap[A] = this.toLongMap(xs)
  }

  implicit class LongMapBuilderFromTuplesArray[A](xs: Array[(Long, A)]) extends LongMapBuilderFromTuplesOps {
    def toLongMap(): LongMap[A] = this.toLongMap(xs)
  }

  implicit class LongMapBuilderFromTraversableOnce[A](xs: TraversableOnce[A]) extends LongMapBuilderFromTraversableOnceOps {
    def toLongMap[V](kvMapping: A => (Long, V)): LongMap[V] = {
      this.toLongMap(xs, kvMapping)
    }
    def mapByLong(byKey: A => Long): LongMap[A] = {
      this.mapByLong(xs, byKey)
    }
  }

  implicit class LongMapBuilderFromArray[A](objects: Array[A]) extends LongMapBuilderFromTraversableOnceOps {
    def toLongMap[V](kvMapping: A => (Long, V)): LongMap[V] = {
      this.toLongMap(objects, kvMapping)
    }
    def mapByLong(byKey: A => Long): LongMap[A] = {
      this.mapByLong(objects, byKey)
    }
  }

  trait LongMapGrouperFromTraversableLikeOps {
    @inline final protected def groupByLong[A, Repr](xs: TraversableLike[A, Repr], byLong: A => Long): LongMap[Repr] = {

      val tmpMap = xs.groupBy(byLong(_))

      val longMap = new LongMap[Repr](tmpMap.size)
      longMap ++= tmpMap

      longMap
    }
  }

  implicit class LongMapGrouperFromTraversableLike[A, Repr](xs: TraversableLike[A, Repr]) extends LongMapGrouperFromTraversableLikeOps {
    def groupByLong(byLong: A => Long): LongMap[Repr] = {
      this.groupByLong(xs, byLong)
    }
  }

  implicit class LongMapGrouperFromArray[A](xs: Array[A]) extends LongMapGrouperFromTraversableLikeOps {
    def groupByLong(byLong: A => Long): LongMap[Array[A]] = {
      this.groupByLong(xs, byLong)
    }
  }

}