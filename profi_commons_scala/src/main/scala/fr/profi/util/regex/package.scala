package fr.proline.util

/** Miscellaneous helpers */
package object regex {

  object RegexUtils {
    
    import util.matching.Regex
    import java.util.regex.Pattern
    
    // Private cache of compiled regexes
    val regexCache = new collection.mutable.HashMap[Pair[String,String],Regex]
    
    def getRegex( s: String, groupNames: String* ) = {
      val groupNamesAsStr = groupNames.mkString("%")
      val regexKey = (s, groupNamesAsStr)
      
      if( regexCache.contains(regexKey) ) regexCache(regexKey)
      else {
        val regex = new Regex(s, groupNames: _* )
        regexCache += ( regexKey -> regex)
        regex
      }
    }
    
    def getPattern( s: String ) = this.getRegex( s ).pattern

    /** Use a Regex object to match a given string */
    class RichRegex(self: Regex) {
      def ~=(s: String) = self.pattern.matcher(s).matches
    }
    implicit def regexToRichRegex(r: Regex) = new RichRegex(r)
    
    /** Match a string object to a given regular expression */
    class RichString(self: String) {
      /** The regular expression is provided as a scala Regex object */
      def =~(r: util.matching.Regex) = r.pattern.matcher(self).matches      
      /** The regular expression is provided as a String  */
      //def =~(s: String) = self.matches(s) 
      def =~(s: String) = RegexUtils.getPattern(s).matcher(self).matches   
      /** The regular expression is provided as a scala String */
      //def ~~(s: String) = Pattern.compile(s).matcher(self).find
      def ~~(s: String) = RegexUtils.getPattern(s).matcher(self).find
      
      /** The regular expression is provided as a String  */
      def =#(s: String, groupNames: String* ) = RegexUtils.getRegex(s, groupNames: _* ).findFirstMatchIn(self)
    }
    implicit def strToRichStr(str: String) = new RichString(str)
  }
  
}
