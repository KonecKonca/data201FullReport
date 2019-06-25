package com.kozitski.spark.service

import com.kozitski.spark.domain.{KafkaMessage, Twit}
import org.apache.spark.rdd.RDD
import play.api.libs.json.Json

class JsonMapper extends Serializable {

   def mapToArray(rdd: RDD[KafkaMessage]): RDD[Twit] = {

    rdd.map(message =>  {
        val str = message.value

        val jsonObject = Json.parse(str)

        val rateLimitStatus = (jsonObject \ "rateLimitStatus").get.toString
        val accessLevel = (jsonObject \ "accessLevel").get.toString.toLong
        val createdAt = (jsonObject \ "createdAt").get.toString.toLong

        val id = (jsonObject \ "id").get.toString
        val text = (jsonObject \ "text").get.toString
        val source = (jsonObject \ "source").get.toString
        val inReplyToStatusId = (jsonObject \ "inReplyToStatusId").get.toString.toLong
        val inReplyToUserId = (jsonObject \ "inReplyToUserId").get.toString.toLong
        val favoriteCount = (jsonObject \ "favoriteCount").get.toString.toLong
        val inReplyToScreenName = (jsonObject \ "inReplyToScreenName").get.toString
        val geoLocation = (jsonObject \ "geoLocation").get.toString
        val place = (jsonObject \ "place").get.toString
        val retweetCount = (jsonObject \ "retweetCount").get.toString.toLong
        val lang = (jsonObject \ "lang").get.toString
        val retweetedStatus = (jsonObject \ "retweetedStatus").get.toString

        val userMentionEntities = (jsonObject \ "userMentionEntities").get.toString
        val hashtag = mapHashTag((jsonObject \ "hashtagEntities").get.toString)
        val mediaEntities = (jsonObject \ "mediaEntities").get.toString
        val extendedMediaEntities = (jsonObject \ "extendedMediaEntities").get.toString
        val symbolEntities = (jsonObject \ "symbolEntities").get.toString

        val currentUserRetweetId = (jsonObject \ "currentUserRetweetId").get.toString.toLong
        val scopes = (jsonObject \ "scopes").get.toString
        val user = (jsonObject \ "user").get.toString
        val withheldInCountries = (jsonObject \ "withheldInCountries").get.toString
        val possiblySensitive = (jsonObject \ "possiblySensitive").get.toString.toBoolean
        val truncated = (jsonObject \ "truncated").get.toString.toBoolean
        val retweeted = (jsonObject \ "retweeted").get.toString.toBoolean
        val contributors = (jsonObject \ "contributors").get.toString
        val retweet = (jsonObject \ "retweet").get.toString.toBoolean
        val urlentities = (jsonObject \ "urlentities").get.toString
        val favorited = (jsonObject \ "favorited").get.toString.toBoolean

        val twit = Twit(rateLimitStatus, accessLevel, createdAt, id, text, source, inReplyToStatusId, inReplyToUserId, favoriteCount, inReplyToScreenName,
          geoLocation, place, retweetCount, lang, retweetedStatus, userMentionEntities, hashtag, mediaEntities, extendedMediaEntities, symbolEntities,
          currentUserRetweetId, scopes, user, withheldInCountries, possiblySensitive, truncated, retweeted, contributors, retweet, urlentities,
          favorited)

      twit
      })
  }

  private def mapHashTag(string: String): String= {
    var hashTag: String = ""

    val strings = string.split("\"")

    if(strings.size > 10){
      hashTag = strings(strings.size - 2)
    }

    hashTag
  }

}
