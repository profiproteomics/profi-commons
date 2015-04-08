package fr.profi.msangel.om

import fr.profi.pwx.util.mongodb.IMongoDbEntity
import reactivemongo.bson.BSONObjectID
import fr.profi.msangel.om.workflow.operation.conversion.MsConvert

case class MSAngelServerConfig(

  var id: Option[BSONObjectID] = None,
  var msconvertPath: String = "",
  var extractmsnPath: String = "",
  var raw2mzdbPath: String = "",
  var mzdbaccessPath: String = ""
  
) extends IMongoDbEntity {

    require(msconvertPath != null, "msconvertPath must not be null")

    require(extractmsnPath != null, "extractmsnPath must not be null")

    require(raw2mzdbPath != null, "raw2mzdbPath must not be null")

    require(mzdbaccessPath != null, "mzdbaccessPath must not be null")
}
