package org.interview.twitter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.Status;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserStatusListenerTest {

    @Mock
    private StatusCollector statusCollector;

    @Mock
    private Status status;

    private UserStatusListener listener;

    @BeforeEach
    void setUp() {
        listener = new UserStatusListener(statusCollector);
    }

    @Test
    void shouldCollectIncomingStatuses() {
        listener.onStatus(status);

        assertThat(listener.onFulfill()).isNotDone();

        verify(statusCollector).collect(status);
    }

    @Test
    void shouldFulfillOnceCollectorIsFull() {
        CollectorResult collectedStatuses = new CollectorResult(new HashMap<>());
        given(statusCollector.getResult()).willReturn(collectedStatuses);
        given(statusCollector.isFull()).willReturn(true);

        listener.onStatus(status);

        assertThat(listener.onFulfill())
                .isCompletedWithValue(collectedStatuses);
    }

    @Test
    void shouldNotFailOnException() {
        assertThatCode(() ->
                listener.onException(new RuntimeException("whatever"))
        ).doesNotThrowAnyException();
    }
}