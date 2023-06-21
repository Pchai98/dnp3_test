package com.github.ungoodman.dnp3.service.util;

import io.stepfunc.dnp3.BroadcastAction;
import io.stepfunc.dnp3.FunctionCode;
import io.stepfunc.dnp3.OutstationInformation;
import io.stepfunc.dnp3.RequestHeader;
import org.joou.UByte;

public class SimulateOutstationInformation implements OutstationInformation {

    @Override
    public void processRequestFromIdle(RequestHeader header) {}

    @Override
    public void broadcastReceived(FunctionCode functionCode, BroadcastAction action) {}

    @Override
    public void enterSolicitedConfirmWait(UByte ecsn) {}

    @Override
    public void solicitedConfirmTimeout(UByte ecsn) {}

    @Override
    public void solicitedConfirmReceived(UByte ecsn) {}

    @Override
    public void solicitedConfirmWaitNewRequest() {}

    @Override
    public void wrongSolicitedConfirmSeq(UByte ecsn, UByte seq) {}

    @Override
    public void unexpectedConfirm(boolean unsolicited, UByte seq) {}

    @Override
    public void enterUnsolicitedConfirmWait(UByte ecsn) {}

    @Override
    public void unsolicitedConfirmTimeout(UByte ecsn, boolean retry) {}

    @Override
    public void unsolicitedConfirmed(UByte ecsn) {}

    @Override
    public void clearRestartIin() {}
}