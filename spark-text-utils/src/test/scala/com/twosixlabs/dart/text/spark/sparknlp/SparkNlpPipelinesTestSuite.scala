package com.twosixlabs.dart.text.spark.sparknlp

import com.twosixlabs.dart.spark.test.utils.SparkTestContext
import com.twosixlabs.dart.test.base.ScalaTestBase
import org.apache.spark.sql.{DataFrame, Row}

import scala.collection.mutable

class SparkNlpPipelinesTestSuite extends ScalaTestBase with SparkTestContext {

    import spark.implicits._

    "Bag of Words pipeline" should "produce a bag of words for a document" in {
        val inputDf = Seq( ("doc_1", "my name is michael") ).toDF( "document_id", "extracted_text" )

        val pipeline = SparkNlpPipelines.bagOfWords( inputColumn = "extracted_text", outputColumn = "words", modelsDir = SPARK_NLP_MODELS )

        val model = pipeline.fit( inputDf )
        val results : DataFrame = model.transform( inputDf )

        val actual : Row = results.take( 1 )( 0 )

        actual.getAs[ Array[ String ] ]( "words" ) shouldBe mutable.WrappedArray.make( Array( "name", "michael" ) )
    }

    "Tokenize pipeline" should "tokenize the input column into the tokens column" in {
        val inputDf = Seq( ("doc_1", "yan has a quick question"),
                           ("doc_2", "michael is losing his mind") ).toDF( "document_id", "extracted_text" )

        val pipeline = SparkNlpPipelines.tokenize( inputColumn = "extracted_text", outputColumn = "tokens", modelsDir = SPARK_NLP_MODELS )

        val model = pipeline.fit( inputDf )
        val results : DataFrame = model.transform( inputDf )

        val rowOne : Row = results.take( 2 )( 0 )
        rowOne.getAs[ Array[ String ] ]( "tokens" ) shouldBe mutable.WrappedArray.make( Array( "yan", "has", "a", "quick", "question" ) )

        val rowTwo : Row = results.take( 2 )( 1 )
        rowTwo.getAs[ Array[ String ] ]( "tokens" ) shouldBe mutable.WrappedArray.make( Array( "michael", "is", "losing", "his", "mind" ) )
    }

}
