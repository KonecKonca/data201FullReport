package com.kozitski.spark.domain

case class Twit(
                 rateLimitStatus: String,
                 accessLevel: Long,
                 createdAt: Long,
                 id: String,
                 text: String,
                 source: String,
                 inReplyToStatusId: Long,
                 inReplyToUserId: Long,
                 favoriteCount: Long,
                 inReplyToScreenName: String,
                 geoLocation: String,
                 place: String,
                 retweetCount: Long,
                 lang: String,
                 retweetedStatus: String,
                 userMentionEntities: String,
                 hashtag: String,
                 mediaEntities: String,
                 extendedMediaEntities: String,
                 symbolEntities: String,
                 currentUserRetweetId: Long,
                 scopes: String,
                 user: String,
                 withheldInCountries: String,
                 possiblySensitive: Boolean,
                 truncated: Boolean,
                 retweeted: Boolean,
                 contributors: String,
                 retweet: Boolean,
                 urlentities: String,
                 favorited: Boolean
               ){

  /**
    * @return [[String]] in separated format
    */
  override def toString: String = {
    rateLimitStatus + ",, " +
      accessLevel + ",, " +
      createdAt + ",, " +
      id + ",, " +
      text + ",, " +
      source + ",, " +
      inReplyToStatusId + ",, " +
      inReplyToUserId + ",, " +
      favoriteCount + ",, " +
      inReplyToScreenName + ",, " +
      geoLocation + ",, " +
      place + ",, " +
      retweetCount + ",, " +
      lang + ",, " +
      retweetedStatus + ",, " +
      userMentionEntities + ",, " +
      hashtag + ",, " +
      mediaEntities + ",, " +
      extendedMediaEntities + ",, " +
      symbolEntities + ",, " +
      currentUserRetweetId + ",, " +
      scopes + ",, " +
      user + ",, " +
      withheldInCountries + ",, " +
      possiblySensitive + ",, " +
      truncated + ",, " +
      retweeted + ",, " +
      contributors + ",, " +
      retweet + ",, " +
      urlentities + ",, " +
      favorited
  }

  /**
    * @return [[Int]] defined by [[id]]
    */
  override def hashCode(): Int = this.id.substring(13, this.id.length - 1).toInt

  /**
    * @return [[Boolean]] defined by is it equal by [[id]]
    */
  override def equals(obj: Any): Boolean = {
    val twit = obj.asInstanceOf[Twit]

    this.id.equals(twit.id)
  }

  /**
    * @return [[Boolean]] defined by is it equal by [[id]]
    */
  override def canEqual(that: Any): Boolean = {
    val twit = that.asInstanceOf[Twit]

    this.id.equals(twit.id)
  }

}
