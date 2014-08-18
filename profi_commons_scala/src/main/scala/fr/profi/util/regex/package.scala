package fr.profi.util

/** Miscellaneous helpers */
package object regex {

  object RegexUtils {
    
    import util.matching.Regex
    import java.util.regex.Pattern
    
    // Private cache of compiled regexes
    val regexCache = new collection.mutable.HashMap[Pair[String,String],Regex]
    
    def getRegex( s: String, groupNames: String* ) = synchronized {
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
    implicit class RichRegex(self: Regex) {
      def ~=(s: String) = self.pattern.matcher(s).matches
    }
    
    /** Match a string object to a given regular expression */
    implicit class RichString(self: String) {
      
      /** Matching with regular expression provided as a Scala Regex object */
      def =~(r: util.matching.Regex): Boolean = r.pattern.matcher(self).matches
      
      /** Matching with regular expression provided as a String */
      //def =~(s: String) = self.matches(s) 
      def =~(s: String): Boolean = RegexUtils.getPattern(s).matcher(self).matches
      
      /** Partial matching with regular expression provided as a Scala String */
      def ~~(s: String): Boolean = RegexUtils.getPattern(s).matcher(self).find
      
      /** Group capture with regular expression provided as a String  */
      def =#(s: String, groupNames: String* ): Option[Regex.Match] = RegexUtils.getRegex(s, groupNames: _* ).findFirstMatchIn(self)
    }
    
  }
  
}
