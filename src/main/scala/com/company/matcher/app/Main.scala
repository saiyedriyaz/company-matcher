package com.company.matcher.app

import Matcher.ScoredMatch
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

object Main {

  def main(args: Array[String]) {
    println("**********************************************************")

    if (args.length < 4) {
      println("In valid arguments supplied.")
      println("Args Required: {data_file_name_with_path} {input_file_name_with_path} {minThreshold} {location_to_save_processed_file}")
      println("Example: /user/input/companies.csv /user/input/sample_user_records.csv 0.5 /user/output/processed_data.csv")
      return
    }
    val dataSourceFile = args(0)
    val userInputPath = args(1)
    val minThreshold = args(2).toDouble
    val processedDataPath = args(3)

    val conf = new SparkConf().setAppName("company-matcher")//.setMaster("local[*]")
    val sc = new SparkContext(conf)
    val sparkSession = SparkSession.builder.config(sc.getConf).getOrCreate()
    val companiesRDD = sparkSession.sparkContext.textFile(dataSourceFile) //sparkSession.read.format("csv").option("header", "true").load("companies.csv")

    val kayValRDD: RDD[Map[String, String]] = companiesRDD.map(line => {
      val row = line.split(",")
      Map("id" -> row(0), "name" -> (row(1)))
    })

    println(kayValRDD)

    val inputDF = sparkSession.read.format("csv").option("header", "true").load(userInputPath)
    val searchingList: List[String] = inputDF.select("name").collect().map(_ (0).toString).toList

    var scoreMatchList = ListBuffer[ScoredMatch]()
    println("scoredMatches...")
    for (name <- searchingList) {
      val scoredMatches = Matcher.matchCompanies(name, kayValRDD, minThreshold)
      scoreMatchList ++= scoredMatches
    }

    val outputRDD: RDD[ScoredMatch] = sparkSession.sparkContext.parallelize(scoreMatchList)
    outputRDD.foreach(println)

    saveDataFrame(sparkSession, outputRDD, processedDataPath)
  }

  def saveDataFrame(ss: SparkSession, valResRDD: RDD[ScoredMatch], processedDataPath: String): Unit = {
    val rowsRDD = valResRDD.map(to => Row(to.name, to.id))
    val df: DataFrame = ss.createDataFrame(rowsRDD, getSchema)
    df.show()
    df.coalesce(1).write.option("header", "true").mode(SaveMode.Overwrite).csv(processedDataPath)
  }

  private def getSchema: StructType = {
    StructType(Seq(
      StructField("NAME", StringType, nullable = true),
      StructField("ID", StringType, nullable = true)
    ))
  }
}
