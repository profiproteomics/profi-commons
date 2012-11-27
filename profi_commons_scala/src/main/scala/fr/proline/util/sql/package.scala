package fr.proline.util

package object sql {
  
  class SQLBool( value: Boolean ) {
    
    def this(intValue:Int) = this( intValue match {
                                     case 1 => true
                                     case 0 => false
                                  })
    override def toString = {
       value match {
        case true => "t"
        case false => "f"
      }
    }
    
    def toIntString = {
       value match {
        case true => "1"
        case false => "0"
      }
    }
    
  }
  
  def BoolToSQLStr( value: Boolean, asInt: Boolean = false ): String = {
    val sqlBool = new SQLBool(value)
    if( asInt ) sqlBool.toIntString else sqlBool.toString()
  }
  
  def SQLStrToBool( sqlStr: String ): Boolean = {
    sqlStr match {
      case "true" => true
      case "false" => false
      case "t" => true
      case "f" => false
      case "1" => true
      case "0" => false
    }
  }
  
  def getTimeAsSQLTimestamp(): java.sql.Timestamp = new java.sql.Timestamp(new java.util.Date().getTime)
  
  def escapeStringForPgCopy( s: String ): String = {
    s.replaceAllLiterally("\\","""\\""")
     .replaceAllLiterally("\r","""""")
     .replaceAllLiterally("\n","""\\\n""")
     .replaceAllLiterally("\t","""\\\t""")
  }
  
  /**
   * Replace empty strings by the '\N' character and convert the record to a byte array.
   * Note: by default '\N' means NULL value for the postgres COPY function
   */
  def encodeRecordForPgCopy( record: List[Any], escape: Boolean = true ): Array[Byte] = {
    
    import fr.proline.util.StringUtils.isEmpty
    
    val recordStrings = record.map { case s:String => if( escape ) escapeStringForPgCopy(s) else s
                                     case a:Any => a.toString()
                                   }
                              .map { str => if( isEmpty(str) ) "\\N" else str }
    
    (recordStrings.mkString("\t") + "\n").getBytes("UTF-8")
  }
  
  import java.text.{DecimalFormat,DecimalFormatSymbols}
  private val decimalSymbols = new DecimalFormatSymbols()
  decimalSymbols.setDecimalSeparator('.')
  decimalSymbols.setGroupingSeparator('\0')
  
  def newDecimalFormat( template: String ): DecimalFormat = new DecimalFormat(template: String , decimalSymbols)
    
  object TrailingZerosStripper {
    
    private val decimalParser = """(\d+\.\d*?)0*$""".r
    
    def apply( decimalAsStr: String ): String = {
      val decimalParser(compactDecimal) = decimalAsStr
      compactDecimal
    }
  }
}
