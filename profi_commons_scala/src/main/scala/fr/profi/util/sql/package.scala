package fr.profi.util

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
  
  object StringOrBoolAsBool {
    
    def asBoolean(boolean: Any): Boolean = boolean match {
      case s: String => SQLStrToBool(s)
      case b: Boolean => b
      case _ => throw new IllegalArgumentException("can't only take a String or a Boolean as input")
    }
    implicit def string2boolean( value: Any ): Boolean = asBoolean(value)
    
  }
  
  def getTimeAsSQLTimestamp(): java.sql.Timestamp = new java.sql.Timestamp(new java.util.Date().getTime)
  
  def escapeStringForPgCopy( s: String ): String = {
    s.replaceAllLiterally("\\", """\\""")
     .replaceAllLiterally("\r", "")
     .replaceAllLiterally("\n", """\n""")
     .replaceAllLiterally("\t", """\t""")
  }
  
  /**
   * Replace empty strings by the '\N' character and convert the record to a byte array.
   * Note: by default '\N' means NULL value for the postgres COPY function
   */
  def encodeRecordForPgCopy( record: List[Any], escape: Boolean = true ): Array[Byte] = {
    
    import fr.profi.util.StringUtils.isEmpty
    
    def stringify( value: Any ): String = {
       value match {
         case s:String => if( escape ) escapeStringForPgCopy(s) else s
         case a:Any => a.toString()
       }
    }
    
    val recordStrings = record.map { case opt: Option[Any] => {
                                       opt match {
                                         case None => """\N"""
                                         case Some(value) => stringify( value )
                                       }
                                     }
                                     case value: Any => stringify( value )
                                   }
    
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
