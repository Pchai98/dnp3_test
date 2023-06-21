package com.github.ungoodman.dnp3.service;

import io.stepfunc.dnp3.*;
import org.joou.UByte;
import org.joou.ULong;
import org.joou.UShort;

import static org.joou.Unsigned.ushort;

public class SimulateOutstationApplication implements OutstationApplication {

    @Override
    public UShort getProcessingDelayMs() {
        return ushort(0);
    }

    @Override
    public WriteTimeResult writeAbsoluteTime(ULong time) {
        return WriteTimeResult.NOT_SUPPORTED;
    }

    @Override
    public ApplicationIin getApplicationIin() {
        return new ApplicationIin();
    }

    @Override
    public RestartDelay coldRestart() {
        return RestartDelay.seconds(ushort(60));
    }

    @Override
    public RestartDelay warmRestart() {
        return RestartDelay.notSupported();
    }

    @Override
    public FreezeResult freezeCountersAll(FreezeType freezeType, DatabaseHandle database) {
        return FreezeResult.NOT_SUPPORTED;
    }

    @Override
    public FreezeResult freezeCountersRange(
            UShort start, UShort stop, FreezeType freezeType, DatabaseHandle database) {
        return FreezeResult.NOT_SUPPORTED;
    }

    @Override
    public boolean writeStringAttr(UByte set, UByte variation, StringAttr attrType, String value) {
        // Allow writing any string attributes that have been defined as writable
        return true;
    }

}