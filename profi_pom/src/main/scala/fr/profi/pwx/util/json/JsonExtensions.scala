package fr.profi.pwx.util.json

import play.api.libs.json._

// Source: http://stackoverflow.com/questions/21044096/play-how-to-transform-json-while-writing-reading-it-to-from-mongodb
object JsonExtensions {
  def withDefault[A](key: String, default: A)(implicit writes: Writes[A]) = __.json.update((__ \ key).json.copyFrom((__ \ key).json.pick orElse Reads.pure(Json.toJson(default))))
  def copyKey(fromPath: JsPath,toPath:JsPath ) = __.json.update(toPath.json.copyFrom(fromPath.json.pick))
  def copyOptKey(fromPath: JsPath,toPath:JsPath ) = __.json.update(toPath.json.copyFrom(fromPath.json.pick orElse Reads.pure(JsNull)))
  def moveKey(fromPath:JsPath, toPath:JsPath) = __.json.update(copyKey(fromPath,toPath) ).andThen( fromPath.json.prune )
}