package fr.profi.pwx.util.mongodb

import reactivemongo.bson.BSONObjectID

// Note: this file is identical to the one in PWX-Common
trait IMongoDbEntity {
  var id: Option[BSONObjectID]
  def getIdAsString(): Option[String] = this.id.map( _.stringify )
  def setIdAsString(idAsStr: String) = { this.id = Some(BSONObjectID(idAsStr)) }
}