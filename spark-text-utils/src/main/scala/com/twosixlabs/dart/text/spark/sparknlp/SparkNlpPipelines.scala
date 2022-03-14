package com.twosixlabs.dart.text.spark.sparknlp

import org.apache.spark.ml.Pipeline

object SparkNlpPipelines {

    def tokenize( inputColumn : String, outputColumn : String, modelsDir : String ) : Pipeline = {
        new SparkNlpPipelineBuilder()
          .start( inputColumn, "_document" )
          .tokenize( "_document", "_tokenized" )
          .finish( "_tokenized", outputColumn )
          .build()
    }

    def bagOfWords( inputColumn : String, outputColumn : String, modelsDir : String ) : Pipeline = {
        new SparkNlpPipelineBuilder()
          .start( inputColumn, "_document" )
          .tokenize( "_document", "_tokenized" )
          .removeStopWords( "_tokenized", "_no_stop_word" )
          .lemmatize( "_no_stop_word", "_lemmatized", modelsDir )
          .finish( "_lemmatized", outputColumn )
          .build()
    }

}