package org.interview.twitter;

import lombok.Value;
import twitter4j.Status;
import twitter4j.User;

import java.util.Map;
import java.util.Set;

@Value
class CollectorResult {
    private final Map<User, Set<Status>> statusesPerUser;
}
