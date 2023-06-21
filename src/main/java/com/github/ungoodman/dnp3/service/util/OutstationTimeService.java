package com.github.ungoodman.dnp3.service.util;

import io.stepfunc.dnp3.Timestamp;

import static org.joou.Unsigned.ulong;

public class OutstationTimeService {
    // Need to check accurate of timestamp
    public static Timestamp now() {
        return Timestamp.synchronizedTimestamp(ulong(System.currentTimeMillis()));
    }
}
