package com.company.matcher.app

import org.apache.spark.rdd.RDD

object Matcher {

  case class ScoredMatch(name: String, id: String, score: Double)

  /** @param name        : name provided in the user uploaded record.
   * @param companies    : List of company dictionaries to match to. For simplicity you can pass the full list of companies provided.
   * @param minThreshold : minimum score for acceptable match, 0 <= minThreshold <= 1
   * @return: Option class for a ScoredMatch, populated with the highest scoring match found above minThreshold, None otherwise
   **/
  def matchCompanies(name: String, companies: RDD[Map[String, String]], minThreshold: Double): Option[ScoredMatch] = {
    val recordNameTokens = name.toUpperCase.split(" ").toSet
    val scoredMatches = companies.map(c => {
      val tokens = c("name").toUpperCase.split(" ").toSet
      // Score companies based on Jaccard similarity
      val score = tokens.intersect(recordNameTokens).size / tokens.union(recordNameTokens).size.doubleValue
      ScoredMatch(c("name"), c("id"), score)
    })

    // Pick highest score match (if any)
    scoredMatches.filter(_.score >= minThreshold).sortBy(_.score).collect().reverse.headOption
  }
}
