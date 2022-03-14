package com.twosixlabs.dart.text.spark.sparknlp

import com.twosixlabs.dart.spark.test.utils.SparkTestContext
import com.twosixlabs.dart.test.base.ScalaTestBase
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.DataFrame

import scala.collection.mutable

class SparkNlpPipelineBuilderTestSuite extends ScalaTestBase with SparkTestContext {

    import spark.implicits._

    "Spark Pipeline Builder" should "create a pipeline that tokenizes the input" in {
        val inputDf : DataFrame = Seq( ("1", "this is a sentence") ).toDF( "document_id", "extracted_text" )

        val pipeline : Pipeline =
            new SparkNlpPipelineBuilder()
              .start( "extracted_text", "document" )
              .tokenize( "document", "tokens" )
              .finish( "tokens", "tokenized" )
              .build()

        val model = pipeline.fit( inputDf )
        val results = model.transform( inputDf )

        results.columns.toSet shouldBe Set( "document_id", "extracted_text", "tokenized" )
        val tokenized : mutable.WrappedArray[ String ] = results.collect()( 0 ).getAs[ mutable.WrappedArray[ String ] ]( "tokenized" )
        tokenized shouldBe mutable.WrappedArray.make( Array( "this", "is", "a", "sentence" ) )
    }

    "Spark Pipeline Builder" should "create a pipeline that removes stop words from the input" in {
        val inputDf : DataFrame = Seq( ("1", "this is a sentence that has lots of stop words") ).toDF( "document_id", "extracted_text" )

        val pipeline : Pipeline =
            new SparkNlpPipelineBuilder()
              .start( "extracted_text", "document" )
              .tokenize( "document", "tokens" )
              .removeStopWords( "tokens", "no_stop_words" )
              .finish( "no_stop_words", "stop_words_removed" )
              .build()

        val model = pipeline.fit( inputDf )
        val results = model.transform( inputDf )

        results.columns.toSet shouldBe Set( "document_id", "stop_words_removed", "extracted_text" )
        val stopWordsRemoved : mutable.WrappedArray[ String ] = results.collect()( 0 ).getAs[ mutable.WrappedArray[ String ] ]( "stop_words_removed" )
        stopWordsRemoved shouldBe mutable.WrappedArray.make( Array( "sentence", "lots", "stop", "words" ) )
    }

    "Spark Pipeline Builder" should "create a pipeline that lemmatizes the input" in {
        val inputDf : DataFrame = Seq( ("1", "michael is the best") ).toDF( "document_id", "extracted_text" )

        val pipeline : Pipeline =
            new SparkNlpPipelineBuilder()
              .start( "extracted_text", "document" )
              .tokenize( "document", "tokens" )
              .lemmatize( "tokens", "lemmas", SPARK_NLP_MODELS )
              .finish( "lemmas", "lemmatized" )
              .build()

        val model = pipeline.fit( inputDf )
        val results = model.transform( inputDf )

        results.columns.toSet shouldBe Set( "document_id", "lemmatized", "extracted_text" )
        val stopWordsRemoved : mutable.WrappedArray[ String ] = results.collect()( 0 ).getAs[ mutable.WrappedArray[ String ] ]( "lemmatized" )
        stopWordsRemoved shouldBe mutable.WrappedArray.make( Array( "michael", "be", "the", "good" ) )
    }
}