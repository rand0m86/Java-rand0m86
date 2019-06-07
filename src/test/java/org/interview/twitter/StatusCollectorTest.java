package org.interview.twitter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.Status;
import twitter4j.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.util.DateUtil.parse;
import static org.assertj.core.util.DateUtil.parseDatetime;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StatusCollectorTest {
    private StatusCollector collector;

    @Mock
    private User john;

    @Mock
    private User bob;

    @BeforeEach
    void setUp() {
        collector = new StatusCollector(3);
        given(john.getCreatedAt()).willReturn(parse("2018-05-23"));
    }

    @Test
    void shouldCollectStatuses() {
        Status status = status(john);

        collector.collect(status);

        assertThat(collector.getResult().getStatusesPerUser())
                .hasSize(1)
                .contains(entry(john, singleElementSet(status)));
    }

    @Test
    void shouldSortUsersByCreateDate() {
        given(bob.getCreatedAt()).willReturn(parse("2015-01-01"));
        Status statusOfJohn = status(john, parseDatetime("2019-06-06T12:00:15"));
        Status statusOfBob = status(bob, parseDatetime("2019-06-06T12:00:25"));

        collector.collect(statusOfJohn);
        collector.collect(statusOfBob);

        assertThat(collector.getResult().getStatusesPerUser())
                .hasSize(2)
                .containsExactly(
                        entry(bob, singleElementSet(statusOfBob)),
                        entry(john, singleElementSet(statusOfJohn))
                );
    }

    @Test
    void shouldSortStatusesByDate() {
        Status first = status(john, parseDatetime("2019-06-06T12:00:15"));
        Status second = status(john, parseDatetime("2019-06-06T11:00:00"));
        collector.collect(first);
        collector.collect(second);

        assertThat(collector.getResult().getStatusesPerUser())
                .hasSize(1)
                .containsExactly(entry(john, new LinkedHashSet<>(Arrays.asList(second, first))));
    }

    @Test
    void shouldBecameFullOnceCapacityReached() {
        assertThat(collector.isFull()).isFalse();
        collector.collect(status(john));
        collector.collect(status(john));
        assertThat(collector.isFull()).isFalse();
        collector.collect(status(john));

        assertThat(collector.isFull()).isTrue();
    }

    private <T> Set<T> singleElementSet(T element) {
        return Collections.singleton(element);
    }

    private Status status(User user) {
        return status(user, new Date());
    }

    private Status status(User user, Date createdAt) {
        Status status = mock(Status.class);
        given(status.getCreatedAt()).willReturn(createdAt);
        given(status.getUser()).willReturn(user);
        return status;
    }
}