package org.interview.twitter;

import lombok.extern.slf4j.Slf4j;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.auth.RequestToken;

import java.util.Scanner;

@Slf4j
class TwitterStreamAdapter {

    TwitterStream authorizeWithPin(TwitterStream twitterStream) {
        RequestToken requestToken = generateRequestToken(twitterStream);
        String providedPin = readPin(requestToken);
        acquireAccessToken(twitterStream, requestToken, providedPin);
        logger.info("Successful authorization");

        return twitterStream;
    }

    private RequestToken generateRequestToken(TwitterStream twitterStream) {
        logger.debug("Generating request token");
        try {
            return twitterStream.getOAuthRequestToken();
        } catch (TwitterException e) {
            throw new TwitterAuthenticationException("Unable to acquire temporary token: " + e.getMessage(), e);
        }
    }

    private String readPin(RequestToken requestToken) {
        String providedPin;
        try(Scanner scanner = new Scanner(System.in)) {
            logger.info("Go to the following link in your browser: {}", requestToken.getAuthorizationURL());
            logger.info("Please enter the retrieved PIN:");
            providedPin = scanner.nextLine();
        }

        if (providedPin == null || providedPin.isEmpty()) {
            throw new TwitterAuthenticationException("Invalid PIN received");
        }

        return providedPin;
    }

    private void acquireAccessToken(TwitterStream twitterStream, RequestToken requestToken, String providedPin) {
        logger.debug("Getting access token");
        try {
            twitterStream.getOAuthAccessToken(requestToken, providedPin);
        } catch (TwitterException e) {
            throw new TwitterAuthenticationException("Unable to authorize access: " + e.getMessage(), e);
        }
    }

}
