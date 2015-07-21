package fr.profi.util.proxy

import scala.language.experimental.macros

import scala.annotation.StaticAnnotation

/**
 * Sources:
 * - https://github.com/adamw/scala-macro-aop
 * - http://www.warski.org/blog/2013/09/automatic-generation-of-delegate-methods-with-macro-annotations/
 */
class delegate extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro delegateMacro.impl
}