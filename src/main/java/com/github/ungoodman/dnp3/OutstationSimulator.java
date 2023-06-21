package com.github.ungoodman.dnp3;

import com.github.ungoodman.dnp3.service.OutstationSimulatorService;
import com.github.ungoodman.dnp3.service.logger.OutstationLogger;
import io.stepfunc.dnp3.Logging;
import io.stepfunc.dnp3.LoggingConfig;
import io.stepfunc.dnp3.Runtime;
import io.stepfunc.dnp3.RuntimeConfig;

public class OutstationSimulator {
    public static void main(String[] args) {
        // Setup logging
        Logging.configure(new LoggingConfig(), new OutstationLogger());

        // Create the Tokio runtime
        Runtime runtime = new Runtime(new RuntimeConfig());
        OutstationSimulatorService simulator = new OutstationSimulatorService(runtime);

        try {
            simulator.run(args);
        } finally {
            runtime.shutdown();
        }
    }
}
