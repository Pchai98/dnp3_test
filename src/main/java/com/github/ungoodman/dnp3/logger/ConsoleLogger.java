package com.github.ungoodman.dnp3.logger;

import io.stepfunc.dnp3.LogLevel;
import io.stepfunc.dnp3.Logger;

public class ConsoleLogger implements Logger {
    @Override
    public void onMessage(LogLevel logLevel, String message) {
        System.out.println(message);
    }
}
