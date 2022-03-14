package com.twosixlabs.dart.text.spark.udfs

import java.time.LocalDate
import java.util.regex.Pattern

import scala.collection.mutable.WrappedArray

object SparkTextProcessingUdfs {

    private val ANY_NUMBER = Pattern.compile( "[0-9]+" )

    val filterSingleCharTokens : WrappedArray[ String ] => WrappedArray[ String ] = ( source : WrappedArray[ String ] ) => source.filter( _.length > 1 )

    val removeNumbersThatAreNotYears : WrappedArray[ String ] => WrappedArray[ String ] = ( source : WrappedArray[ String ] ) => {
        source.filter( token => {
            if ( token.matches( ANY_NUMBER.pattern() ) ) {
                if ( BigInt( token ) >= 1900 && BigInt( token ) <= LocalDate.now().getYear ) true
                else false
            }
            else true
        } )
    }

}
