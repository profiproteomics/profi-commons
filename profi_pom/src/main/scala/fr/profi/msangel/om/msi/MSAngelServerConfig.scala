package fr.profi.msangel.om

case class MSAngelServerConfig(
  var msconvertPath: String = "", //C:\Program Files\ProteoWizard 3.0.7076\msconvert.exe
  var extractmsnPath: String = "",
  var raw2mzdbPath: String = "",
  var mzdbaccessPath: String = ""
)
