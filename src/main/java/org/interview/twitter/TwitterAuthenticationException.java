package org.interview.twitter;

class TwitterAuthenticationException extends RuntimeException {

    public TwitterAuthenticationException(final String message) {
        super(message);
    }

    public TwitterAuthenticationException(final String message, final Throwable t) {
        super(message, t);
    }

}
