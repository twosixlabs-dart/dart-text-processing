package com.twosixlabs.dart.text.cleanup

import java.text.Normalizer
import java.util.regex.Pattern

/**
  *
  * This class is primarily used in corpus level analytics to remove noisy things
  * from the text to make tf_idf scoring more accurate
  *
  */
//TODO - refactor to be more flexible/configurable
object TextCleaner {
    private val DIACRITICS : Pattern = Pattern.compile( "\\p{InCombiningDiacriticalMarks}+" )

    /**
      * Get rid of extra spaces and various extra characters and make it just words
      *
      * For now we are going to simply strip the diacritics from the text and remove them. However, there might be more work to consider
      * based on this: https://stackoverflow.com/a/5697575. There might be a way to compare strings "accent-insensitively" using
      * https://github.com/unicode-org/icu/tree/master/icu4j
      */
    def cleanUp( original : String ) : String = {
        // lively discussion on text normalization: https://stackoverflow.com/a/1453256
        val cleaned = original
          .replaceAll( "\\s", " " )
          .replaceAll( "\n", "" )
          .replaceAll( " {2,}", " " )
          .replaceAll( "[^a-zA-Z0-9À-ž -]+", "" ) // do some basic text cleaning first
          .toLowerCase
          .replaceAll( "[0-9]+(-|.)?([0-9]+)?-?(y(ea)?r(s)?|month(s)?|day(s)?|week(s)?|h(ou)?r(s)?|min(ute)?(s)?|second(s)?)", "" ) // time measurements
          .replaceAll( "[0-9]+(-|.)?([0-9]+)?-?(dozen(s)?|hundred(s)?|thousand(s)?|million(s)?)", "" ) // powers of ten
          .replaceAll( "[0-9]+((-|.)?([0-9]+))?-?(((mg|kg|g)s?|lbs?))", "" ) //weight measurements
          .replaceAll( "[0-9]+(-|.)?([0-9]+)?-?((mm|cm|km|m)s?|f(oo||ee)?t|in(che(s)?)?)", "" ) // height/distance measurements
          .replaceAll( "[0-9]+(-|.)?([0-9]+)?-?(percent)", "" ) // percentage measurements
          .replaceAll( "[0-9]+(-|.)?([0-9]+)?-?(st|nd|rd|th)", "" ) // ordinal values
          .replaceAll( "(\\+?[0-9]{1,3}(-|\\s))?(\\(?[0-9]{3}\\)?(-|\\s))?[0-9]{3}-[0-9]{4}", "" ) // phone numbers
          .replaceAll( "[0-9]+(\\.?[0-9])?-[0-9]+(\\.?[0-9])?", "" ) // ranges

        // decompose the diacritics from the chars (ie: <example>) and then strip them out
        DIACRITICS.matcher( Normalizer.normalize( cleaned, Normalizer.Form.NFKD ) ).replaceAll( "" ).trim()
    }
}
