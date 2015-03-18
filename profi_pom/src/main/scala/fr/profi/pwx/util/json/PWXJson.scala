package fr.profi.pwx.util.json

import scala.collection.mutable.ArrayBuffer

import play.api.libs.json._

import fr.profi.pwx.util.mongodb.IMongoDbEntity

object PWXJson {
  
  // Json Form for serialization/de- of classes extending IMongoDbIdentity (BsonId concern)
  def entityFormat[M <: IMongoDbEntity](baseFormat: Format[M]): Format[M] = {

    new Format[M] {

      private val publicIdPath: JsPath = JsPath \ 'id
      private val privateIdPath: JsPath = JsPath \ '_id

      private val jsonPrivateToPublicId = JsonExtensions.moveKey(privateIdPath, publicIdPath)
      private val jsonPublicToPrivateId = JsonExtensions.moveKey(publicIdPath, privateIdPath)

      def reads(json: JsValue): JsResult[M] = {
        val idOpt = (json \ "_id").asOpt[JsObject]
        if (idOpt.isEmpty) { baseFormat.reads(json) }
        else {
          baseFormat.compose(jsonPrivateToPublicId).reads(json)
        }
      }

      def writes(o: M): JsValue = {
        val jsonWriter = baseFormat.transform { js =>

          val idOpt = (js \ "id").asOpt[JsObject]
          if (idOpt.isEmpty) { js.as[JsObject] }
          else {
            js.transform(jsonPublicToPrivateId).asOpt.get
          }
        }

        jsonWriter.writes(o)
      }

    }

  }

  // Serialize instance into JSON with custom BsonId description
  def toJson[T](o: T)(implicit tjs: Writes[T]): JsValue = {
    transformBsonIdToString(Json.toJson(o))
  }

  // Transform BsonId description in JsValue
  def transformBsonIdToString(jsValue: JsValue): JsValue = {
    jsValue match {

      case jsObject: JsObject => _transformBsonIdToString(jsObject)

      case jsArray: JsArray => {
        var arrayBuffer = ArrayBuffer[JsObject]()
        jsArray.as[Array[JsObject]].foreach { obj =>
          arrayBuffer += _transformBsonIdToString(obj).as[JsObject]
        }

        Json.toJson(arrayBuffer)
      }

      case _ => jsValue //throw new Exception ?
    }
  }

  // Transform { "_id" : { "$oid" : <someId> }} into  { "id" : <someId> }
  private def _transformBsonIdToString(jsValue: JsValue): JsValue = {

    val idOpt = (jsValue \ "_id" \ "$oid").asOpt[String]

    if (idOpt.isEmpty) jsValue
    else {
      val jsValueWithoutId = jsValue.as[JsObject] - "_id"
      Json.obj("id" -> idOpt.get) ++ jsValueWithoutId
    }
  }

}
