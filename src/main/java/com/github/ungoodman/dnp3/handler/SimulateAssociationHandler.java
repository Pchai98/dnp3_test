package com.github.ungoodman.dnp3.handler;

import io.stepfunc.dnp3.AssociationHandler;
import io.stepfunc.dnp3.UtcTimestamp;

import static org.joou.Unsigned.ulong;

public class SimulateAssociationHandler implements AssociationHandler {
    @Override
    public UtcTimestamp getCurrentTime() {
        return UtcTimestamp.valid(ulong(System.currentTimeMillis()));
    }
}
