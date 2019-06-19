package com.kozitski.twit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    private Long id;
    private Long id_str;
    private String name;
    private String screen_name;
    private String location;
    private String url;
    private String description;
    private String translator_type;
    @JsonProperty("protected")
    private boolean isProtected;
    private boolean verified;
    private Integer followers_count;
    private Integer statuses_count;
    private String created_at;
    private String utc_offset;
    private String time_zone;
    private Boolean geo_enabled;
    private String lang;
    private Boolean contributors_enabled;
    private Boolean is_translator;
    private String profile_background_color;
    private String profile_background_image_url;
    private String profile_background_image_url_https;
    private Boolean profile_background_tile;
    private String profile_link_color;
    private String profile_sidebar_border_color;
    private String profile_sidebar_fill_color;
    private String profile_text_color;
    private Boolean profile_use_background_image;
    private String profile_image_url;
    private String profile_image_url_https;
    private String profile_banner_url;
    private Boolean default_profile;
    private Boolean default_profile_image;
    private String following;
    private String follow_request_sent;
    private String notifications;

    @Override
    public String toString(){
        return name;
    }
}
