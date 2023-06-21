package com.github.ungoodman.dnp3.handler;

import com.github.ungoodman.dnp3.service.util.OutstationTimeService;
import io.stepfunc.dnp3.*;
import org.joou.UShort;

import static org.joou.Unsigned.ulong;
import static org.joou.Unsigned.ushort;

public class SimulateControlHandler implements ControlHandler {

    @Override
    public void beginFragment() {}

    @Override
    public void endFragment(DatabaseHandle database) {}

    @Override
    public CommandStatus selectG12v1(Group12Var1 control, UShort index, DatabaseHandle database) {
        if (index.compareTo(ushort(10)) < 0 && (control.code.opType == OpType.LATCH_ON || control.code.opType == OpType.LATCH_OFF)) {
            return CommandStatus.SUCCESS;
        } else {
            return CommandStatus.NOT_SUPPORTED;
        }
    }

    @Override
    public CommandStatus operateG12v1(Group12Var1 control, UShort index, OperateType opType, DatabaseHandle database) {
        if (index.compareTo(ushort(10)) < 0 && (control.code.opType == OpType.LATCH_ON || control.code.opType == OpType.LATCH_OFF)) {
            boolean status = control.code.opType == OpType.LATCH_ON;
            database.transaction(db -> db.updateBinaryOutputStatus(new BinaryOutputStatus(index, status, new Flags(Flag.ONLINE), OutstationTimeService.now()), UpdateOptions.detectEvent()));
            return CommandStatus.SUCCESS;
        } else {
            return CommandStatus.NOT_SUPPORTED;
        }
    }

    @Override
    public CommandStatus selectG41v1(int value, UShort index, DatabaseHandle database) {
        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v1(
            int value, UShort index, OperateType opType, DatabaseHandle database) {
        return operateAnalogOutput(value, index, database);
    }

    @Override
    public CommandStatus selectG41v2(short value, UShort index, DatabaseHandle database) {
        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v2(
            short value, UShort index, OperateType opType, DatabaseHandle database) {
        return operateAnalogOutput(value, index, database);
    }

    @Override
    public CommandStatus selectG41v3(float value, UShort index, DatabaseHandle database) {
        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v3(
            float value, UShort index, OperateType opType, DatabaseHandle database) {
        return operateAnalogOutput(value, index, database);
    }

    @Override
    public CommandStatus selectG41v4(double value, UShort index, DatabaseHandle database) {
        return selectAnalogOutput(index);
    }

    @Override
    public CommandStatus operateG41v4(
            double value, UShort index, OperateType opType, DatabaseHandle database) {
        return operateAnalogOutput(value, index, database);
    }

    private CommandStatus selectAnalogOutput(UShort index) {
        return index.compareTo(ushort(10)) < 0 ? CommandStatus.SUCCESS : CommandStatus.NOT_SUPPORTED;
    }

    private CommandStatus operateAnalogOutput(double value, UShort index, DatabaseHandle database) {
        if (index.compareTo(ushort(10)) < 0) {
            database.transaction(db -> db.updateAnalogOutputStatus(new AnalogOutputStatus(index, value, new Flags(Flag.ONLINE), OutstationTimeService.now()), UpdateOptions.detectEvent()));

            return CommandStatus.SUCCESS;
        }
        else
        {
            return CommandStatus.NOT_SUPPORTED;
        }
    }
}