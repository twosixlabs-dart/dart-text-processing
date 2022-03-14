package com.twosixlabs.dart.text.spark.preprocessing

import com.twosixlabs.dart.spark.test.utils.SparkTestContext
import com.twosixlabs.dart.test.base.ScalaTestBase

import scala.math.BigDecimal.RoundingMode

class Tf_IdfTestSuite extends ScalaTestBase with SparkTestContext {

    import spark.implicits._

    val tfIdf : Tf_Idf = new Tf_Idf( spark )


    "TF-IDF processor" should "produce verified correct IDF scores" in {
        // expected scores are based on values calculated in: https://nlp.stanford.edu/IR-book/html/htmledition/inverse-document-frequency-1.html
        val corpusSize = 806791

        val dfCar = 18165
        val dfAuto = 6723
        val dfInsurance = 19241
        val dfBest = 25235

        val idfCar = Tf_Idf.calculateInverseDocumentFrequency( corpusSize, dfCar ).setScale( 2, RoundingMode.HALF_UP )
        val idfAuto = Tf_Idf.calculateInverseDocumentFrequency( corpusSize, dfAuto ).setScale( 2, RoundingMode.HALF_UP )
        val idfInsurance = Tf_Idf.calculateInverseDocumentFrequency( corpusSize, dfInsurance ).setScale( 2, RoundingMode.HALF_UP )
        val idfBest = Tf_Idf.calculateInverseDocumentFrequency( corpusSize, dfBest ).setScale( 2, RoundingMode.HALF_UP )

        idfCar shouldBe BigDecimal( 1.65 )
        idfAuto shouldBe BigDecimal( 2.08 )
        idfInsurance shouldBe BigDecimal( 1.62 )
        idfBest shouldBe BigDecimal( 1.5 )
    }

    "TF-IDF processor" should "expand the document into terms" in {
        val docOne = ("doc_1", Array( "michael", "be", "good", "testing" ))
        val docTwo = ("doc_2", Array( "yan", "be", "ask", "michael", "quick", "question" ))
        val data = spark.sparkContext.parallelize( Seq( docOne, docTwo ) ).toDF( "document_id", "tokens" )

        val results = tfIdf.expandDocuments( data, inputColumn = "tokens", termColumnName = "term" )

        val expectedDf = Seq( ("doc_1", "michael"),
                              ("doc_1", "be"),
                              ("doc_1", "good"),
                              ("doc_1", "testing"),
                              ("doc_2", "yan"),
                              ("doc_2", "be"),
                              ("doc_2", "ask"),
                              ("doc_2", "michael"),
                              ("doc_2", "quick"),
                              ("doc_2", "question") ).toDF( "document_id", "term" )

        assertSmallDataFrameEquality( results, expectedDf )
    }

    "TF-IDF processor" should "calculate term frequencies" in {
        val explodedCorpus = Seq( ("doc_1", "michael"),
                                  ("doc_1", "be"),
                                  ("doc_1", "good"),
                                  ("doc_1", "testing"),
                                  ("doc_1", "michael"),
                                  ("doc_2", "yan"),
                                  ("doc_2", "yan"),
                                  ("doc_2", "be"),
                                  ("doc_2", "ask"),
                                  ("doc_2", "michael"),
                                  ("doc_2", "quick"),
                                  ("doc_2", "question") ).toDF( "document_id", "term" )

        val results = tfIdf.calculateTermFrequencies( explodedCorpus, termColumn = "term", resultColumnName = "term_freq" )

        val expected = Seq( ("doc_1", "michael", 2l),
                            ("doc_1", "be", 1l),
                            ("doc_1", "good", 1l),
                            ("doc_1", "testing", 1l),
                            ("doc_2", "yan", 2l),
                            ("doc_2", "be", 1l),
                            ("doc_2", "ask", 1l),
                            ("doc_2", "michael", 1l),
                            ("doc_2", "quick", 1l),
                            ("doc_2", "question", 1l) ).toDF( "document_id", "term", "term_freq" )


        assertSmallDataFrameEquality( results, expected )
    }

    "TF-IDF processor" should "calculate document frequencies" in {
        val explodedCorpus = Seq( ("doc_1", "michael"),
                                  ("doc_1", "be"),
                                  ("doc_1", "good"),
                                  ("doc_1", "testing"),
                                  ("doc_1", "michael"),
                                  ("doc_2", "yan"),
                                  ("doc_2", "yan"),
                                  ("doc_2", "be"),
                                  ("doc_2", "ask"),
                                  ("doc_2", "michael"),
                                  ("doc_2", "quick"),
                                  ("doc_2", "question") ).toDF( "document_id", "term" )

        val results = tfIdf.calculateDocumentFrequencies( explodedCorpus, termColumn = "term", resultColumnName = "doc_freq" )

        val expected = Seq( ("michael", 2l),
                            ("be", 2l),
                            ("good", 1l),
                            ("testing", 1l),
                            ("yan", 1l),
                            ("ask", 1l),
                            ("quick", 1l),
                            ("question", 1l) ).toDF( "term", "doc_freq" )

        assertSmallDataFrameEquality( results, expected )
    }

    "TF-IDF processor" should "calculate the correct term doc matrix" in {
        val corpus = Seq( ("doc_1", "text analysis is fun".split( " " )),
                          ("doc_2", "i like doing text analysis".split( " " )),
                          ("doc_3", "i like dogs, they are fun".split( " " )),
                          ("doc_4", "i like this sentence".split( " " )) ).toDF( "document_id", "tokens" )

        val results = tfIdf.calculateTfIdf( corpus, "tokens", "tf_idf" )

        val expectedTfIdfTable =
            """document_id,document_frequency,term,term_frequency,idf,tf_idf
              |doc_1,2,text,1,0.221848749600000000,0.221849
              |doc_1,2,analysis,1,0.221848749600000000,0.221849
              |doc_1,1,is,1,0.397940008700000000,0.397940
              |doc_1,2,fun,1,0.221848749600000000,0.221849
              |doc_2,3,i,1,0.096910013000000000,0.096910
              |doc_2,3,like,1,0.096910013000000000,0.096910
              |doc_2,1,doing,1,0.397940008700000000,0.397940
              |doc_2,2,text,1,0.221848749600000000,0.221849
              |doc_2,2,analysis,1,0.221848749600000000,0.221849
              |doc_3,3,i,1,0.096910013000000000,0.096910
              |doc_3,3,like,1,0.096910013000000000,0.096910
              |doc_3,1,"dogs,",1,0.397940008700000000,0.397940
              |doc_3,1,they,1,0.397940008700000000,0.397940
              |doc_3,1,are,1,0.397940008700000000,0.397940
              |doc_3,2,fun,1,0.221848749600000000,0.221849
              |doc_4,3,i,1,0.096910013000000000,0.096910
              |doc_4,3,like,1,0.096910013000000000,0.096910
              |doc_4,1,this,1,0.397940008700000000,0.397940
              |doc_4,1,sentence,1,0.397940008700000000,0.397940""".stripMargin.split( "\n" ).toList

        val expected = spark
          .read
          .option( "header", "true" )
          .option( "inferSchema", "true" )
          .schema( results.schema ) // should start defining strict schemas, schema inferences infers the wrong type for the expected set
          .csv( expectedTfIdfTable.toDS )

        assertSmallDataFrameEquality( expected, results )
    }

}