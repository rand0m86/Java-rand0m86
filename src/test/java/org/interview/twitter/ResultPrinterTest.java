package org.interview.twitter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

class ResultPrinterTest {
    private ListAppender<ILoggingEvent> interceptingAppender;
    private ResultPrinter resultPrinter = new ResultPrinter();
    private static Logger logger = (Logger) LoggerFactory.getLogger(ResultPrinter.class);

    @BeforeEach
    void setUp() {
        interceptingAppender = new ListAppender<>();
        interceptingAppender.start();

        logger.addAppender(interceptingAppender);
    }

    @AfterEach
    void tearDown() {
        interceptingAppender.stop();
        logger.detachAppender(interceptingAppender);
    }

    @Test
    void shouldPrintEmptyResult() {
        resultPrinter.print(new CollectorResult(new HashMap<>()));

        assertThat(interceptingAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(tuple("Messages per user: []", Level.INFO));
    }

    @Test
    void shouldPrintResult() {
        // given
        LinkedHashMap<User, Set<Status>> statusesPerUser = new LinkedHashMap<>();
        statusesPerUser.put(
                user(1, 123, "john", "Johny"),
                new LinkedHashSet<>(Arrays.asList(
                        status(111, 1000, "fancy"),
                        status(222, 2000, "happy")
                )));

        statusesPerUser.put(
                user(2, 321, "bob", "Bobby"),
                Collections.singleton(
                        status(333, 3000, "beautiful")
                )
        );

        // when
        resultPrinter.print(new CollectorResult(statusesPerUser));

        // then
        String expectedJson = "[" +
                "{\"userId\":1,\"userCreatedAt\":123,\"userName\":\"john\",\"userScreenName\":\"Johny\"," +
                    "\"messages\":[{\"id\":111,\"createdAt\":1000,\"text\":\"fancy\"}," +
                        "{\"id\":222,\"createdAt\":2000,\"text\":\"happy\"}]}," +

                "{\"userId\":2,\"userCreatedAt\":321,\"userName\":\"bob\",\"userScreenName\":\"Bobby\"," +
                    "\"messages\":[{\"id\":333,\"createdAt\":3000,\"text\":\"beautiful\"}]}" +
        "]";
        assertThat(interceptingAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .containsExactly(tuple("Messages per user: " + expectedJson, Level.INFO));
    }

    private User user(long id, long timestamp, String name, String screenName) {
        User user = spy(User.class);
        given(user.getId()).willReturn(id);
        given(user.getCreatedAt()).willReturn(new Date(timestamp));
        given(user.getName()).willReturn(name);
        given(user.getScreenName()).willReturn(screenName);
        return user;
    }

    private Status status(long id, long timestamp, String text) {
        Status status = spy(Status.class);
        given(status.getId()).willReturn(id);
        given(status.getCreatedAt()).willReturn(new Date(timestamp));
        given(status.getText()).willReturn(text);
        return status;
    }
}