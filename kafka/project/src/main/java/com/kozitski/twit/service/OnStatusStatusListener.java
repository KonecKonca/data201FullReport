package com.kozitski.twit.service;

import twitter4j.StallWarning;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * The interface On status status listener.
 * Leaves not override only onStatus method.
 */
public interface OnStatusStatusListener extends StatusListener {

    @Override
    default void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { /* NOP */ }
    @Override
    default void onTrackLimitationNotice(int numberOfLimitedStatuses) { /* NOP */ }
    @Override
    default void onScrubGeo(long userId, long upToStatusId) { /* NOP */ }
    @Override
    default void onStallWarning(StallWarning stallWarning) { /* NOP */ }
    @Override
    default void onException(Exception ex) { /* NOP */ }

}
