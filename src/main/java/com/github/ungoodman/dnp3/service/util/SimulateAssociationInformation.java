package com.github.ungoodman.dnp3.service.util;

import io.stepfunc.dnp3.AssociationInformation;
import io.stepfunc.dnp3.FunctionCode;
import io.stepfunc.dnp3.TaskError;
import io.stepfunc.dnp3.TaskType;
import org.joou.UByte;

public class SimulateAssociationInformation implements AssociationInformation {
    @Override
    public void taskStart(TaskType taskType, FunctionCode fc, UByte seq) {}

    @Override
    public void taskSuccess(TaskType taskType, FunctionCode fc, UByte seq) {}

    @Override
    public void taskFail(TaskType taskType, TaskError error) {}

    @Override
    public void unsolicitedResponse(boolean isDuplicate, UByte seq) {}
}
