package org.interview.twitter;

import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;
import twitter4j.User;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Slf4j
class StatusCollector {
    private final int capacity;
    private final Map<User, Set<Status>> statusesPerUser;

    StatusCollector(int capacity) {
        this.capacity = capacity;
        this.statusesPerUser = new TreeMap<>(Comparator.comparingLong(o -> o.getCreatedAt().getTime()));
    }

    void collect(Status status) {
        logger.debug("user: {}, text: '{}'", status.getUser().getName(), status.getText());
        if (statusesPerUser.containsKey(status.getUser())) {
            statusesPerUser.get(status.getUser()).add(status);
        } else {
            Set<Status> statuses = initEmptyCollection();
            statuses.add(status);
            statusesPerUser.put(status.getUser(), statuses);
        }
    }

    private TreeSet<Status> initEmptyCollection() {
        return new TreeSet<>(Comparator.comparingLong(s -> s.getCreatedAt().getTime()));
    }

    boolean isFull() {
        long totalStatuses = statusesPerUser.values().stream().mapToLong(Set::size).sum();
        return totalStatuses == capacity;
    }

    CollectorResult getResult() {
        return new CollectorResult(statusesPerUser);
    }

}
