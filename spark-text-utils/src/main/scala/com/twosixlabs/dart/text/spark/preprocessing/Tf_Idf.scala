package com.twosixlabs.dart.text.spark.preprocessing

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.{col, count, countDistinct, explode, monotonically_increasing_id, udf}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.math.BigDecimal.RoundingMode
import scala.math.log10

object Tf_Idf {
    protected[ preprocessing ] val calculateInverseDocumentFrequency : (Long, Long) => BigDecimal = ( corpusSize : Long, documentFrequency : Long ) => {
        BigDecimal( log10( ( 1 + corpusSize.toDouble ) / ( 1 + documentFrequency.toDouble ) ) ).setScale( 10, RoundingMode.HALF_UP )
    }
}


// hack for selecting sets of output columns is based on this:
// https://stackoverflow.com/questions/34938770/upacking-a-list-to-select-multiple-columns-from-a-spark-data-frame/37725068
class Tf_Idf( spark : SparkSession ) {

    import spark.implicits._

    def expandDocuments( data : DataFrame, inputColumn : String, termColumnName : String, outputColumns : Set[ String ] = Set() ) : DataFrame = {
        if ( !data.columns.toSet.subsetOf( Set( "document_id", inputColumn ) ) ) throw new Exception() // handle this better later

        val output : Seq[ String ] = outputColumns.toSeq :+ termColumnName
        data
          .withColumn( s"${termColumnName}", explode( col( inputColumn ) ) )
          .select( "document_id", output : _* ) // this syntax is to unpack a collection into varargs
    }

    def calculateTermFrequencies( data : DataFrame, termColumn : String, resultColumnName : String, outputColumns : Set[ String ] = Set() ) : DataFrame = {
        if ( !data.columns.toSet.subsetOf( Set( "document_id", termColumn ) ) ) throw new Exception() // handle this better later

        val output : Seq[ String ] = outputColumns.toSeq ++ Seq( termColumn, resultColumnName )
        data
          .withColumn( "id", monotonically_increasing_id() )
          .groupBy( $"document_id", col( s"${termColumn}" ) )
          .agg( count( $"id" ) as s"${resultColumnName}" )
          .select( "document_id", output : _* )
    }

    def calculateDocumentFrequencies( data : DataFrame, termColumn : String, resultColumnName : String, outputColumns : Set[ String ] = Set() ) : DataFrame = {
        if ( !data.columns.toSet.subsetOf( Set( "document_id", termColumn ) ) ) throw new Exception() // handle this better later

        val output : Seq[ String ] = outputColumns.toSeq ++ Seq( resultColumnName, termColumn )
        data
          .groupBy( col( s"${termColumn}" ) )
          .agg( countDistinct( $"document_id" ) as s"${resultColumnName}" )
    }

    def calculateTfIdf( data : DataFrame, inputColumn : String, tfIdfColumnName : String ) : DataFrame = {
        val corpusSize = data.count()
        // this kind of partially applied function seems hacky
        val calculate_idf : UserDefinedFunction = udf( ( docFreq : Long ) => Tf_Idf.calculateInverseDocumentFrequency( corpusSize, docFreq ) )

        val expanded = expandDocuments( data, inputColumn, "term" )

        val termFrequencies = calculateTermFrequencies( expanded, termColumn = "term", resultColumnName = "term_frequency" )

        val documentFrequencies = calculateDocumentFrequencies( expanded, termColumn = "term",
                                                                resultColumnName = "document_frequency" ).withColumn( "idf", calculate_idf( $"document_frequency" ) )

        val output : Seq[ String ] = Set( "term", "term_frequency", "document_frequency", "idf", tfIdfColumnName ).toSeq

        termFrequencies
          .join( documentFrequencies, "term" )
          .withColumn( "tf_idf", $"term_frequency" * $"idf" )
          .select( "document_id", output : _* )
    }

}
