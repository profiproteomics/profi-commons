package fr.profi.util

import scala.collection.ArrayOps
import scala.collection.mutable.LongMap
import scala.collection.{IterableOps, WithFilter}
import scala.language.implicitConversions

/**
 * @author David Bouyssie
 *
 */
package object collection {

  trait LongMapBuilder extends Any {
    @inline final def initLongMap[A, V](xs: IterableOnce[A]): LongMap[V] = {
      xs.knownSize match {
        case -1 => new LongMap[V]()
        case size => new LongMap[V](size)
      }
    }
  }

  trait LongMapBuilderFromTuplesOps extends Any with LongMapBuilder {
    @inline final def fillLongMap[V](xs: IterableOnce[(Long, V)], longMap: LongMap[V]): LongMap[V] = {
      longMap ++= xs
    }
    @inline final def toLongMap[V](xs: IterableOnce[(Long, V)]): LongMap[V] = {
      val longMap = this.initLongMap[(Long, V), V](xs)
      this.fillLongMap(xs, longMap)
      longMap
    }
  }

  object LongMapBuilderFromTraversableTuples extends LongMapBuilderFromTuplesOps

  class LongMapBuilderFromTraversableTuples[A](val xs: IterableOnce[(Long, A)]) extends AnyVal {
    def toLongMap(): LongMap[A] = LongMapBuilderFromTraversableTuples.toLongMap(xs)
  }
  implicit def traversableTuples2longMapBuilder[A]( xs: IterableOnce[(Long, A)] ): LongMapBuilderFromTraversableTuples[A] = {
    new LongMapBuilderFromTraversableTuples[A](xs)
  }
  implicit def tuplesArray2longMapBuilder[A]( xs: Array[(Long, A)] ): LongMapBuilderFromTraversableTuples[A] = {
    new LongMapBuilderFromTraversableTuples[A](xs)
  }

  trait LongMapBuilderFromIterableOnceOps extends Any with LongMapBuilder {

    @inline final def toLongMap[A, V](xs: IterableOnce[A], kvMapping: A => (Long, V)): LongMap[V] = {
      val longMap = this.initLongMap[A, V](xs)

      val xsIter = xs.iterator
      while (xsIter.hasNext) {
        longMap += kvMapping(xsIter.next())
      }

      longMap
    }
    @inline final def mapByLong[A](xs: IterableOnce[A], byKey: A => Long): LongMap[A] = {
      this.toLongMap(xs, { x: A => (byKey(x), x) })
    }
  }

  object LongMapBuilderFromIterableOnce extends LongMapBuilderFromIterableOnceOps

  class LongMapBuilderFromIterableOnce[A](val xs: IterableOnce[A]) extends AnyVal {
    def mapByLong(byKey: A => Long): LongMap[A] = {
      LongMapBuilderFromIterableOnce.mapByLong(xs, byKey)
    }

    def toLongMapWith[V](kvMapping: A => (Long, V)): LongMap[V] = {
      LongMapBuilderFromIterableOnce.toLongMap(xs, kvMapping)
    }
  }
  implicit def iterableOnce2longMapBuilder[A]( xs: IterableOnce[A] ): LongMapBuilderFromIterableOnce[A] = {
    new LongMapBuilderFromIterableOnce[A](xs)
  }
  implicit def array2longMapBuilder[A]( xs: Array[A] ): LongMapBuilderFromIterableOnce[A] = {
    new LongMapBuilderFromIterableOnce[A](xs)
  }

  // --- BUILDERS FROM WithFilter (result of xs.withFilter(...)) --- //
  // Note: the size of a WithFilter is unknown, so the LongMap is created with its default size

  trait LongMapBuilderFromWithFilterOps extends Any {

    @inline final def toLongMap[A, V](foreach: (A => Unit) => Unit, kvMapping: A => (Long, V)): LongMap[V] = {
      val longMap = new LongMap[V]()
      foreach { x =>
        longMap += kvMapping(x)
      }
      longMap
    }
  }

  object LongMapBuilderFromWithFilter extends LongMapBuilderFromWithFilterOps

  class LongMapBuilderFromWithFilter[A, CC[_]](val fm: WithFilter[A, CC]) extends AnyVal {

    def mapByLong(byKey: A => Long): LongMap[A] = {
      LongMapBuilderFromWithFilter.toLongMap[A, A](fm.foreach(_), { x: A => (byKey(x), x) })
    }

    def toLongMapWith[V](kvMapping: A => (Long, V)): LongMap[V] = {
      LongMapBuilderFromWithFilter.toLongMap[A, V](fm.foreach(_), kvMapping)
    }
  }
  implicit def withFilter2longMapBuilder[A, CC[_]]( fm: WithFilter[A, CC] ): LongMapBuilderFromWithFilter[A, CC] = {
    new LongMapBuilderFromWithFilter[A, CC](fm)
  }

  // Array.withFilter returns an ArrayOps.WithFilter, which is not a collection.WithFilter
  class LongMapBuilderFromArrayWithFilter[A](val fm: ArrayOps.WithFilter[A]) extends AnyVal {

    def mapByLong(byKey: A => Long): LongMap[A] = {
      LongMapBuilderFromWithFilter.toLongMap[A, A](fm.foreach(_), { x: A => (byKey(x), x) })
    }

    def toLongMapWith[V](kvMapping: A => (Long, V)): LongMap[V] = {
      LongMapBuilderFromWithFilter.toLongMap[A, V](fm.foreach(_), kvMapping)
    }
  }
  implicit def arrayWithFilter2longMapBuilder[A]( fm: ArrayOps.WithFilter[A] ): LongMapBuilderFromArrayWithFilter[A] = {
    new LongMapBuilderFromArrayWithFilter[A](fm)
  }

  trait LongMapGrouperFromTraversableLikeOps extends Any {
    @inline final def groupByLong[A, Repr](xs: IterableOps[A, IterableOnce, Repr], byLong: A => Long): LongMap[Repr] = {

      val tmpMap = xs.groupBy(byLong(_))

      val longMap = new LongMap[Repr](tmpMap.size)
      longMap ++= tmpMap

      longMap
    }
  }

  object LongMapGrouperFromTraversableLike extends LongMapGrouperFromTraversableLikeOps

  class LongMapGrouperFromTraversableLike[A, Repr](val xs: IterableOps[A, IterableOnce, Repr]) extends AnyVal {
    def groupByLong(byLong: A => Long): LongMap[Repr] = {
      LongMapGrouperFromTraversableLike.groupByLong(xs, byLong)
    }
  }
  implicit def IterableOnce2longMapGrouper[A,Repr]( xs: IterableOps[A,Iterable, Repr] ): LongMapGrouperFromTraversableLike[A, Repr] = {
    new LongMapGrouperFromTraversableLike[A,Repr](xs)
  }

  // --- GROUP BY LONG FOR ARRAYS --- //
  trait LongMapGrouperFromArrayOps extends Any {
    @inline final def groupByLong[A](xs: Array[A], byLong: A => Long): LongMap[Array[A]] = {

      val tmpMap = xs.groupBy(byLong(_))

      val longMap = new LongMap[Array[A]](tmpMap.size)
      tmpMap.foreach { case (k,v) =>
        longMap.put(k, v)
      }

      longMap
    }
  }

  object LongMapGrouperFromArray extends LongMapGrouperFromArrayOps

  class LongMapGrouperFromArray[A](val xs: Array[A]) extends AnyVal {
    def groupByLong(byLong: A => Long): LongMap[Array[A]] = {
      LongMapGrouperFromArray.groupByLong(xs, byLong)
    }
  }
  implicit def array2longMapGrouper[A]( xs: Array[A] ): LongMapGrouperFromArray[A] = {
    new LongMapGrouperFromArray[A](xs)
  }

}
