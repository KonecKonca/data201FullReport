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

}
