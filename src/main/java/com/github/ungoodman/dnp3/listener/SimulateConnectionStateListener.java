package com.github.ungoodman.dnp3.listener;

import io.stepfunc.dnp3.ConnectionState;
import io.stepfunc.dnp3.ConnectionStateListener;

public class SimulateConnectionStateListener implements ConnectionStateListener {
    @Override
    public void onChange(ConnectionState state) {
        System.out.println("Connection state change: " + state);
    }
}