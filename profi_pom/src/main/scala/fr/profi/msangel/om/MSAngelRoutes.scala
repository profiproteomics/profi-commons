package fr.profi.msangel.om

object MSAngelRoutes { 
  //} extends Enumeration{

  //val URL_PREFIX = "/PWX/msangel"
  val URL_PREFIX = "/PWX/msangel"

  object StaticResource {
    val checkConfig = URL_PREFIX + "/check_config"
    val configurationWindow = URL_PREFIX + "/configuration_window"
    val configure = URL_PREFIX + "/configure"
    val getHostname = URL_PREFIX + "/get_hostname"
    val mainHtml = URL_PREFIX + "/main.html"
  }

  object Task {
    val submit = URL_PREFIX + "/task/submit"
    // val update = URL_PREFIX + "/task/update"
    val stop = URL_PREFIX + "/task/stop"
    val resume = URL_PREFIX + "/task/resume"
    val delete = URL_PREFIX + "/task/delete"
  }

  object ServerConfiguration {
    val getConfig = URL_PREFIX + "/server_config/get/config"
    val setConfig = URL_PREFIX + "/server_config/set/config"
    def getConversionToolPath(path: String) = URL_PREFIX + s"/server_config/get/$path/path"
    def setConversionToolPath(path: String) = URL_PREFIX + s"/server_config/set/$path/path"
    def getSearchEngineUrl(searchEngine: String) = URL_PREFIX + s"/server_config/get/$searchEngine/url"
    def setSearchEngineUrl(searchEngine: String) = URL_PREFIX + s"/server_config/set/$searchEngine/url"
    val getMountPoints = URL_PREFIX + "/server_config/get/mount_points"
    val setMountPoints = URL_PREFIX + "/server_config/set/mount_points"
    val getMountPointsValue = URL_PREFIX + "/server_config/mount_points/get_value"
  }

  object DataMigration {
    val migrateMongoDB = URL_PREFIX + "/data_store/mongo/migrate"
  }

  object MongoDataStore {
    def getDoc(collection: String, id: String) = URL_PREFIX + s"/data_store/mongo/$collection/get_doc/$id"
    def getDocsWhere(collection: String) = URL_PREFIX + s"/data_store/mongo/$collection/get_docs_where"
    def getAllDocs(collection: String) = URL_PREFIX + s"/data_store/mongo/$collection/get_all_docs"
    def getDocsSince(collection: String, date: String) = URL_PREFIX + s"/data_store/mongo/$collection/get_docs_since/$date"
    def save(collection: String) = URL_PREFIX + s"/data_store/mongo/$collection/save"
    def update(collection: String) = URL_PREFIX + s"/data_store/mongo/$collection/update"
    def remove(collection: String, id: String) = URL_PREFIX + s"/data_store/mongo/$collection/remove/$id"
  }

  object UdsDatastore {
    val getAllInstruments = URL_PREFIX + "/data_store/uds/instrument/get_all"
    val getAllInstrumentConfigs = URL_PREFIX + "/data_store/uds/instrument_config/get_all"
    val getAllPeaklistSoftware = URL_PREFIX + "/data_store/uds/peaklist_software/get_all"
    val getAllProjects = URL_PREFIX + "/data_store/uds/project/get_all"
    val getAllProtMatchDecoyRule = URL_PREFIX + "/data_store/uds/prot_match_decoy_rule/get_all"
    val getAllUserAccounts = URL_PREFIX + "/data_store/uds/user_account/get_all"

    // Utility
    def getAll(collection: UdsDbTable.Value): String = collection match {
      case UdsDbTable.INSTRUMENT            => getAllInstruments
      case UdsDbTable.INSTRUMENT_CONFIG     => getAllInstrumentConfigs
      case UdsDbTable.PEAKLIST_SOFTWARE     => getAllPeaklistSoftware
      case UdsDbTable.PROJECT               => getAllProjects
      case UdsDbTable.PROT_MATCH_DECOY_RULE => getAllProtMatchDecoyRule
      case UdsDbTable.USER_ACCOUNT          => getAllUserAccounts
      case _                                => throw new Exception("Unknown value: " + collection)
    }
    
  }

  object FakeHttpServer {
    val clientPl = URL_PREFIX + "/fake_mascot_server/client.pl"
    val nphMascotExe = URL_PREFIX + "/fake_mascot_server/nph-mascot.exe"
  }

}