package com.github.ungoodman.dnp3;

import com.github.ungoodman.dnp3.config.MasterConfiguration;
import com.github.ungoodman.dnp3.service.logger.ConsoleLogger;
import com.github.ungoodman.dnp3.service.MasterSimulatorService;
import io.stepfunc.dnp3.Logging;
import io.stepfunc.dnp3.LoggingConfig;
import io.stepfunc.dnp3.Runtime;

public class MasterSimulator {
    public static void main(String[] args) {
        // Initialize logging with the default configuration
        // This may only be called once during program initialization
        // ANCHOR: logging_init
        Logging.configure(new LoggingConfig(), new ConsoleLogger());
        // ANCHOR_END: logging_init

        // ANCHOR: runtime
        Runtime runtime = new Runtime(MasterConfiguration.getRuntimeConfig());
        // ANCHOR_END: runtime

        MasterSimulatorService master = new MasterSimulatorService(runtime);

        try {
            master.run(args);
        } finally {
            // ANCHOR: runtime_shutdown
            runtime.shutdown();
            // ANCHOR_END: runtime_shutdown
        }
    }
}
