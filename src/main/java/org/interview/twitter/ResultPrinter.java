package org.interview.twitter;

import lombok.extern.slf4j.Slf4j;
import twitter4j.JSONArray;
import twitter4j.JSONObject;
import twitter4j.Status;
import twitter4j.User;

import java.util.Map;
import java.util.Set;

@Slf4j
class ResultPrinter {
    void print(CollectorResult result) {
        JSONArray out = new JSONArray();
        Map<User, Set<Status>> values = result.getStatusesPerUser();
        values.forEach((user, statuses) -> {
            JSONArray authorMessages = statusesToMessages(statuses);
            JSONObject userWithMessages = toUser(user, authorMessages);
            out.put(userWithMessages);
        });

        logger.info("Messages per user: {}", out);
    }

    private JSONArray statusesToMessages(Set<Status> statuses) {
        JSONArray messages = new JSONArray();
        statuses.forEach(status -> messages.put(statusToMessage(status)));

        return messages;
    }

    private JSONObject statusToMessage(Status status) {
        return new JSONObject()
                .put("id", status.getId())
                .put("createdAt", status.getCreatedAt().getTime())
                .put("text", status.getText());
    }

    private JSONObject toUser(User user, JSONArray authorMessages) {
        return new JSONObject()
                .put("userId", user.getId())
                .put("userCreatedAt", user.getCreatedAt().getTime())
                .put("userName", user.getName())
                .put("userScreenName", user.getScreenName())
                .put("messages", authorMessages);
    }
}
