#!/usr/bin/env sh

java -jar -DtotalStatuses=$TOTAL_STATUSES \
          -DtimeoutSeconds=$TIMEOUT_SECONDS \
          -DconsumerKey=$CONSUMER_KEY \
          -DconsumerSecret=$CONSUMER_SECRET \
          /app.jar
