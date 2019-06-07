package org.interview.twitter.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;

class FuturesUtilityTest {

    @Test
    void shouldCreateCompletableFutureForGivenDuration() {
        long before = currentTimeMillis();

        try {
            FuturesUtility.failAfter(Duration.ofMillis(200)).get();
        } catch (Exception ignored) {}

        assertThat(currentTimeMillis() - before)
                .isGreaterThanOrEqualTo(200);
    }
}