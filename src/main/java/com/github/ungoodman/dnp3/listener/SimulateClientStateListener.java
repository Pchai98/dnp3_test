package com.github.ungoodman.dnp3.listener;

import io.stepfunc.dnp3.ClientState;
import io.stepfunc.dnp3.ClientStateListener;

public class SimulateClientStateListener implements ClientStateListener {
    @Override
    public void onChange(ClientState clientState) {
        System.out.println(clientState);
    }
}
