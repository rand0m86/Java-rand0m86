package org.interview.twitter;

import twitter4j.ConnectionLifeCycleListener;

abstract class ConnectionLifeCycleListenerAdapter implements ConnectionLifeCycleListener {
    @Override
    public void onConnect() {
    }

    @Override
    public void onCleanUp() {
    }

    @Override
    public void onDisconnect() {
    }
}
