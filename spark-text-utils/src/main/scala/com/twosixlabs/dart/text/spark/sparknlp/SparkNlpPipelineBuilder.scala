package com.twosixlabs.dart.text.spark.sparknlp

import com.johnsnowlabs.nlp.annotator.LemmatizerModel
import com.johnsnowlabs.nlp.annotators.{StopWordsCleaner, Tokenizer}
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher}
import org.apache.spark.ml.{Pipeline, PipelineStage}

// TODO - make builder pattern more typesafe
sealed trait PipelineState
trait RawTextCapablePipeline extends PipelineState
trait TokenizedPipeline extends PipelineState

class SparkNlpPipelineBuilder( val stages : List[ PipelineStage ] = List() ) {

    def build( ) : Pipeline = new Pipeline().setStages( stages.toArray )

    def start( inputColumn : String, outputColumn : String ) : SparkNlpPipelineBuilder = {
        val assembler : PipelineStage = new DocumentAssembler()
          .setCleanupMode( "shrink_full" )
          .setInputCol( inputColumn )
          .setOutputCol( outputColumn )

        new SparkNlpPipelineBuilder( stages :+ assembler )
    }

    def tokenize( inputColumn : String, outputColumn : String ) : SparkNlpPipelineBuilder = {
        val tokenizer = new Tokenizer()
          .setInputCols( inputColumn )
          .setOutputCol( outputColumn )

        new SparkNlpPipelineBuilder( stages :+ tokenizer )
    }

    def removeStopWords( inputColumn : String, outputColumn : String, caseSensitive : Boolean = false ) : SparkNlpPipelineBuilder = {
        val cleaner = new StopWordsCleaner()
          .setInputCols( inputColumn )
          .setOutputCol( outputColumn )
          .setCaseSensitive( caseSensitive )
        new SparkNlpPipelineBuilder( stages :+ cleaner )
    }

    def lemmatize( inputColumn : String, outputColumn : String, modelDir : String, modelName : String = "lemma_antbnc" ) : SparkNlpPipelineBuilder = {
        val lemmatizer = LemmatizerModel
          .read
          .load( s"${modelDir}/${modelName}" )
          .setInputCols( inputColumn )
          .setOutputCol( outputColumn )

        new SparkNlpPipelineBuilder( stages :+ lemmatizer )
    }

    def finish( inputColumn : String, outputColumn : String ) : SparkNlpPipelineBuilder = {
        val finisher = new Finisher()
          .setInputCols( inputColumn )
          .setOutputCols( outputColumn )
        new SparkNlpPipelineBuilder( stages :+ finisher )
    }
}