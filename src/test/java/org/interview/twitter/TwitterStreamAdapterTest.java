package org.interview.twitter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TwitterStreamAdapterTest {
    private TwitterStreamAdapter adapter = new TwitterStreamAdapter();
    private RequestToken requestToken = new RequestToken("abc", "secret");

    @Mock
    private TwitterStream stream;

    @AfterEach
    void tearDown() {
        System.setIn(System.in);
    }

    @Test
    void shouldFailIfGettingRequestTokenFails() throws TwitterException {
        given(stream.getOAuthRequestToken()).willThrow(new TwitterException("whatever"));

        assertThatThrownBy(() ->
                adapter.authorizeWithPin(stream)
        ).isInstanceOf(TwitterAuthenticationException.class)
                .hasMessage("Unable to acquire temporary token: whatever");
    }

    @Test
    void shouldFailIfAuthorizationPinIsInvalid() throws TwitterException {
        given(stream.getOAuthRequestToken()).willReturn(requestToken);
        ByteArrayInputStream in = new ByteArrayInputStream("\n".getBytes());
        System.setIn(in);

        assertThatThrownBy(() ->
                adapter.authorizeWithPin(stream)
        ).isInstanceOf(TwitterAuthenticationException.class)
                .hasMessage("Invalid PIN received");
    }

    @Test
    void shouldFailIfReadingAccessTokenFails() throws TwitterException {
        given(stream.getOAuthRequestToken()).willReturn(requestToken);
        String pin = "1234567";
        given(stream.getOAuthAccessToken(requestToken, pin)).willThrow(new TwitterException("access token exception"));
        ByteArrayInputStream in = new ByteArrayInputStream(pin.getBytes());
        System.setIn(in);

        assertThatThrownBy(() ->
                adapter.authorizeWithPin(stream)
        ).isInstanceOf(TwitterAuthenticationException.class)
                .hasMessage("Unable to authorize access: access token exception");
    }

    @Test
    void shouldSuccessfullyAcquireAccessToken() throws TwitterException {
        given(stream.getOAuthRequestToken()).willReturn(requestToken);
        String pin = "1234567";
        given(stream.getOAuthAccessToken(requestToken, pin)).willReturn(new AccessToken("token", "secret"));
        ByteArrayInputStream in = new ByteArrayInputStream(pin.getBytes());
        System.setIn(in);

        assertThatCode(() ->
                adapter.authorizeWithPin(stream)
        ).doesNotThrowAnyException();
    }
}