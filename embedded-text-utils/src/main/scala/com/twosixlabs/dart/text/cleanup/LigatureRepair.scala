package com.twosixlabs.dart.text.cleanup

import better.files.Resource

import scala.collection.JavaConverters._
import scala.util.matching.Regex

trait LigatureRepair {

    def repairLigatureErrors( text : String ) : String

}

class RuleBasedLigatureRepair extends LigatureRepair {

    private lazy val dictionary : Set[ String ] = {
        Resource.getAsString( "dictionary/english-us-ca-au-gb-final.txt" )
          .linesIterator
          .asJava.asScala
          .toSet
    }


    private val ligatureRegex : Regex = raw"\b(\w*(fi|fl|ff|ffi|ffl))[\s]+([\w']+)".r

    override def repairLigatureErrors( text : String ) : String = {
        ligatureRegex.replaceAllIn( text, m => {
            if ( !dictionary.contains( m.group( 1 ) ) || !dictionary.contains( m.group( 3 ) ) ) m.group( 1 ) + m.group( 3 ) else m.matched
        } )
    }
}
