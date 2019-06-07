package org.interview.twitter.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@UtilityClass
public class FuturesUtility {
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(
                    1,
                    runnable -> {
                        Thread thread = new Thread(runnable);
                        thread.setDaemon(true);
                        thread.setName("failAfterDaemon");
                        return thread;
                    });

    // Can be dropped if use Java 9+
    public static <T> CompletableFuture<T> failAfter(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration);
            return promise.completeExceptionally(ex);
        }, duration.toMillis(), MILLISECONDS);
        return promise;
    }

}
