package fr.profi.util

import scala.collection.TraversableLike
import scala.collection.mutable.LongMap
import scala.collection.generic.FilterMonadic
import scala.language.implicitConversions

/**
 * @author David Bouyssie
 *
 */
package object collection {
  
  trait LongMapBuilder extends Any {
    @inline final protected def initLongMap[A, V](xs: TraversableOnce[A]): LongMap[V] = {
      if (xs.isTraversableAgain) new LongMap[V](xs.size) else new LongMap[V]()
    }
  }

  trait LongMapBuilderFromTuplesOps extends Any with LongMapBuilder {
    @inline final protected def fillLongMap[V](xs: TraversableOnce[(Long, V)], longMap: LongMap[V]) = {
      longMap ++= xs
    }
    @inline final protected def toLongMap[V](xs: TraversableOnce[(Long, V)]): LongMap[V] = {
      val longMap = this.initLongMap[(Long, V), V](xs)
      this.fillLongMap(xs, longMap)
      longMap
    }
  }
  
  class LongMapBuilderFromTraversableTuples[A](val xs: TraversableOnce[(Long, A)]) extends AnyVal with LongMapBuilderFromTuplesOps {
    def toLongMap(): LongMap[A] = this.toLongMap(xs)
  }
  implicit def traversableTuples2longMapBuilder[A]( xs: TraversableOnce[(Long, A)] ): LongMapBuilderFromTraversableTuples[A] = {
    new LongMapBuilderFromTraversableTuples[A](xs)
  }
  implicit def tuplesArray2longMapBuilder[A]( xs: Array[(Long, A)] ): LongMapBuilderFromTraversableTuples[A] = {
    new LongMapBuilderFromTraversableTuples[A](xs)
  }

  trait LongMapBuilderFromTraversableOnceOps extends Any with LongMapBuilder {
    
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

  class LongMapBuilderFromTraversableOnce[A](val xs: TraversableOnce[A]) extends AnyVal with LongMapBuilderFromTraversableOnceOps {
    def mapByLong(byKey: A => Long): LongMap[A] = {
      this.mapByLong(xs, byKey)
    }
    
    def toLongMapWith[V](kvMapping: A => (Long, V)): LongMap[V] = {
      this.toLongMap(xs, kvMapping)
    }
  }
  // Note this conversion is conlicting filterMonadic2longMapBuilder
  // TODO: find a way to combine FilterMonadic with TraversableOnce (use TraversableLike ?)
  // DBO => I think that initializing using a match/case in FilterMonadic is simple enough, we can keep the current solution
  /*implicit def traversableOnce2longMapBuilder[A]( xs: TraversableOnce[A] ): LongMapBuilderFromTraversableOnce[A] = {
    new LongMapBuilderFromTraversableOnce[A](xs)
  }*/
  implicit def array2longMapBuilder[A]( xs: Array[A] ): LongMapBuilderFromTraversableOnce[A] = {
    new LongMapBuilderFromTraversableOnce[A](xs)
  }
  
  class LongMapBuilderFromFilterMonadic[A, Repr](val fm: FilterMonadic[A, Repr]) extends AnyVal with LongMapBuilder {
    
    protected def buildLongMapFromLongMapping[V](longMapping: A => (Long, V)): LongMap[V] = {
      
      // Initialize the map
      val longMap = fm match {
        case xs: TraversableOnce[A] => this.initLongMap[A, V](xs)
        case _ => new LongMap[V]
      }

      // Fill the map
      fm.foreach { x =>
        longMap += longMapping(x)
      }

      longMap
    }
    
    def mapByLong(byKey: A => Long): LongMap[A] = {
      this.buildLongMapFromLongMapping({ x: A => (byKey(x), x) })
    }
    
    def toLongMapWith[V](kvMapping: A => (Long, V)): LongMap[V] = {
      this.buildLongMapFromLongMapping(kvMapping)
    }
  }
  
  implicit def filterMonadic2longMapBuilder[A, Repr]( fm: FilterMonadic[A, Repr] ): LongMapBuilderFromFilterMonadic[A, Repr] = {
    new LongMapBuilderFromFilterMonadic[A, Repr](fm)
  }

  trait LongMapGrouperFromTraversableLikeOps extends Any {
    @inline final protected def groupByLong[A, Repr](xs: TraversableLike[A, Repr], byLong: A => Long): LongMap[Repr] = {

      val tmpMap = xs.groupBy(byLong(_))

      val longMap = new LongMap[Repr](tmpMap.size)
      longMap ++= tmpMap

      longMap
    }
  }

  class LongMapGrouperFromTraversableLike[A, Repr](val xs: TraversableLike[A, Repr]) extends AnyVal with LongMapGrouperFromTraversableLikeOps {
    def groupByLong(byLong: A => Long): LongMap[Repr] = {
      this.groupByLong(xs, byLong)
    }
  }
  implicit def traversableOnce2longMapGrouper[A,Repr]( xs: TraversableLike[A, Repr] ): LongMapGrouperFromTraversableLike[A, Repr] = {
    new LongMapGrouperFromTraversableLike[A,Repr](xs)
  }
  implicit def array2longMapGrouper[A]( xs: Array[A] ): LongMapGrouperFromTraversableLike[A, Array[A]] = {
    new LongMapGrouperFromTraversableLike[A, Array[A]](xs)
  }
  
}