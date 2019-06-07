package org.interview.twitter;

import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;
import twitter4j.StatusAdapter;

import java.util.concurrent.CompletableFuture;

@Slf4j
class UserStatusListener extends StatusAdapter {
    private final StatusCollector collector;
    private final CompletableFuture<CollectorResult> onFulfill;

    UserStatusListener(StatusCollector statusCollector) {
        this.collector = statusCollector;
        onFulfill = new CompletableFuture<>();
    }

    @Override
    public void onStatus(Status status) {
        collector.collect(status);
        if (collector.isFull()) {
            onFulfill.complete(collector.getResult());
        }
    }

    @Override
    public void onException(Exception ex) {
        logger.error("Error occurred while consuming twitter stream", ex);
    }

    CompletableFuture<CollectorResult> onFulfill() {
        return onFulfill;
    }

}
