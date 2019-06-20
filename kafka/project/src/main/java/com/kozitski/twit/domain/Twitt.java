package com.kozitski.twit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Twitt implements Serializable {
    private String created_at;
    private String id;
    private String id_str;
//    private String text;
//    private String source;
//    private Boolean truncated;
//    private String in_reply_to_status_id;
//    private String in_reply_to_status_id_str;
//    private String in_reply_to_user_id;
//    private String in_reply_to_user_id_str;
//    private String in_reply_to_screen_name;
    private User user;
//    private String geo;
//    private String coordinates;
//    private String place;
//    private String contributors;
////    private String retweeted_status;
////    private Boolean is_quote_status;
////    private Integer quote_count;
//    private Integer reply_count;
//    private Integer retweet_count;
//    private Integer favorite_count;
////    private String entities;
////    private String extended_entities;
////    private Boolean favorited;
////    private Boolean retweeted;
////    private Boolean possibly_sensitive;
////    private String filter_level;
////    private String lang;
//    private Long timestamp_ms;

    @Override
    public String toString(){
        return created_at + " " + id + " " /*+ user.toString()*/ + user ;
    }
}
