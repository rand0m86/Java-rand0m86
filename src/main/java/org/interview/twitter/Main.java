package org.interview.twitter;

import lombok.extern.slf4j.Slf4j;
import org.interview.twitter.util.FuturesUtility;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Main {
    private static final int DEFAULT_STATUSES_TO_CONSUME = 100;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    public static void main(String[] args) {
        int totalStatuses = parseStatusesProperty();
        int timeoutSeconds = parseTimeout();
        String consumerKey = parseConsumerKey();
        String consumerSecret = parseConsumerSecret();

        proceedWithValues(totalStatuses, timeoutSeconds, consumerKey, consumerSecret);
    }

    private static void proceedWithValues(int totalStatuses, int timeoutSeconds, String consumerKey, String consumerSecret) {
        StatusCollector statusCollector = new StatusCollector(totalStatuses);
        UserStatusListener listener = new UserStatusListener(statusCollector);
        TwitterStreamAdapter adapter = new TwitterStreamAdapter();
        ResultPrinter printer = new ResultPrinter();
        Configuration configuration = new ConfigurationBuilder()
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .build();

        TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();
        twitterStream.addListener(listener);
        adapter.authorizeWithPin(twitterStream);
        twitterStream.addConnectionLifeCycleListener(new ConnectionLifeCycleListenerAdapter() {
            @Override
            public void onConnect() {
                CompletableFuture<CollectorResult> timeout = FuturesUtility.failAfter(Duration.ofSeconds(timeoutSeconds));
                listener.onFulfill()
                        .acceptEither(timeout, result -> {
                            logger.info("Successfully completed");
                            cleanupAndPrintResult(result, twitterStream, printer);
                        }).exceptionally(throwable -> {
                            logger.warn("Terminating by timeout");
                            cleanupAndPrintResult(statusCollector.getResult(), twitterStream, printer);
                            return null;
                });
            }
        });
        twitterStream.filter("bieber"); // can be externalized as well
    }

    private static void cleanupAndPrintResult(CollectorResult result, TwitterStream twitterStream, ResultPrinter printer) {
        twitterStream.clearListeners();
        printer.print(result);
        twitterStream.shutdown();
    }

    private static int parseStatusesProperty() {
        try {
            int totalStatuses = Integer.parseInt(System.getProperty("totalStatuses"));
            logger.info("Total statuses to consume: {}", totalStatuses);
            return totalStatuses;
        } catch (NumberFormatException e) {
            logger.warn("Can't parse 'totalStatuses' property, defaulting to {}", DEFAULT_STATUSES_TO_CONSUME);
        }
        return DEFAULT_STATUSES_TO_CONSUME;
    }

    private static int parseTimeout() {
        try {
            int timeoutSeconds = Integer.parseInt(System.getProperty("timeoutSeconds"));
            logger.info("Timeout to wait for statuses: {} seconds", timeoutSeconds);
            return timeoutSeconds;
        } catch (NumberFormatException e) {
            logger.warn("Can't parse 'timeoutSeconds' property, defaulting to {}", DEFAULT_TIMEOUT_SECONDS);
        }
        return DEFAULT_TIMEOUT_SECONDS;
    }

    private static String parseConsumerKey() {
        String key = System.getProperty("consumerKey");
        if (isEmpty(key)) {
            throw new IllegalArgumentException("Invalid consumer key");
        }
        return key;
    }

    private static String parseConsumerSecret() {
        String secret = System.getProperty("consumerSecret");
        if (isEmpty(secret)) {
            throw new IllegalArgumentException("Invalid consumer secret");
        }
        return secret;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

}
