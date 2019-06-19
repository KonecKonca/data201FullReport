package com.kozitski.twit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kozitski.twit.domain.Twitt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParseTwittService {

    public List<Twitt> parseTwitts(List<String> stringTwitts){
        List<Twitt> twitts = new ArrayList<>(stringTwitts.size());

        stringTwitts.forEach(stringTwitt -> {
            Optional<Twitt> twitt = parseTwitt(stringTwitt);
            twitt.ifPresent(twitts::add);
        });

        return twitts;
    }

    public Optional<Twitt> parseTwitt(String stringTwitt){
        Optional<Twitt> twitt = Optional.empty();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            twitt = Optional.of(objectMapper.readValue(stringTwitt, Twitt.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return twitt;
    }

}
