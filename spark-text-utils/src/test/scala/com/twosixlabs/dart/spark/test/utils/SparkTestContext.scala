package com.twosixlabs.dart.spark.test.utils

import com.github.mrpowers.spark.fast.tests.DataFrameComparer
import org.apache.spark.sql.SparkSession

trait SparkTestContext extends DataFrameComparer {

    val SPARK_NLP_MODELS : String = s"${System.getProperty( "user.dir" )}/spark-text-utils/models"

    val spark : SparkSession = {
        SparkSession.builder()
          .master( "local" )
          .appName( "spark-test" )
          .config( "spark.sql.shuffle.partitions", 1 )
          .config( "spark.driver.bindAddress", "localhost" )
          .getOrCreate()
    }

}