package com.github.ungoodman.dnp3.service.logger;

import io.stepfunc.dnp3.LogLevel;
import io.stepfunc.dnp3.Logger;

public class OutstationLogger implements Logger {
    @Override
    public void onMessage(LogLevel level, String message) {
        System.out.print(message);
    }
}
