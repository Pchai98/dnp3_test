package com.github.ungoodman.dnp3.service;

import com.github.ungoodman.dnp3.config.OutstationConfiguration;
import com.github.ungoodman.dnp3.handler.SimulateControlHandler;
import com.github.ungoodman.dnp3.listener.SimulateConnectionStateListener;
import com.github.ungoodman.dnp3.service.util.OutstationTimeService;
import com.github.ungoodman.dnp3.service.util.SimulateOutstationInformation;
import io.stepfunc.dnp3.*;
import io.stepfunc.dnp3.Runtime;
import org.joou.UByte;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.joou.Unsigned.*;

public class OutstationSimulatorService {
    private Runtime runtime;

    public OutstationSimulatorService(Runtime runtime) {
        this.runtime = runtime;
    }

    public void run(String[] args) {

        if (args.length != 1) {
            System.err.println("You must specify a transport");
            System.err.println("Usage: outstation-example <transport> (tcp, serial, tls-ca, tls-self-signed)");
            return;
        }

        final String type = args[0];
        switch (type) {
            case "tcp":
                runTcp();
                break;
            case "serial":
                runSerial();
                break;
            case "tls-ca":
                runTls(OutstationConfiguration.getCaTlsConfig());
                break;
            case "tls-self-signed":
                runTls(OutstationConfiguration.getSelfSignedTlsConfig());
                break;
            default:
                System.err.printf("Unknown transport: %s%n", type);
        }
    }

    private void runTcp() {
        // ANCHOR: create_tcp_server
        OutstationServer server = OutstationServer.createTcpServer(runtime, LinkErrorMode.CLOSE, "127.0.0.1:20000");
        // ANCHOR_END: create_tcp_server

        try {
            runServer(server);
        } finally {
            server.shutdown();
        }
    }

    private void runTls(TlsServerConfig config) {
        // ANCHOR: create_tls_server
        OutstationServer server = OutstationServer.createTlsServer(runtime, LinkErrorMode.CLOSE, "127.0.0.1:20001", config);
        // ANCHOR_END: create_tls_server

        try {
            runServer(server);
        } finally {
            server.shutdown();
        }
    }

    private void runSerial() {
        // ANCHOR: create_serial_server
        Outstation outstation = Outstation.createSerialSession2(
                runtime,
                "/dev/pts/4",
                new SerialSettings(),
                Duration.ofSeconds(5), // try to open the port every 5 seconds
                OutstationConfiguration.getOutstationConfig(),
                new SimulateOutstationApplication(),
                new SimulateOutstationInformation(),
                new SimulateControlHandler(),
                state -> System.out.println("Port state change: " + state)
        );
        // ANCHOR_END: create_serial_server

        runOutstation(outstation);
    }

    private void runServer(OutstationServer server) {

        // ANCHOR: tcp_server_add_outstation
        final Outstation outstation =
                server.addOutstation(
                        OutstationConfiguration.getOutstationConfig(),
                        new SimulateOutstationApplication(),
                        new SimulateOutstationInformation(),
                        new SimulateControlHandler(),
                        new SimulateConnectionStateListener(),
                        AddressFilter.any());
        // ANCHOR_END: tcp_server_add_outstation

        // ANCHOR: tcp_server_bind
        server.bind();
        // ANCHOR_END: tcp_server_bind

        runOutstation(outstation);
    }

    private void runOutstation(Outstation outstation) {

        // Setup initial points
        // ANCHOR: database_init
        outstation.transaction(OutstationConfiguration::initializeDatabase);
        // ANCHOR_END: database_init

        boolean binaryValue = false;
        DoubleBit doubleBitBinaryValue = DoubleBit.DETERMINED_OFF;
        boolean binaryOutputStatusValue = false;
        long counterValue = 0;
        long frozenCounterValue = 0;
        double analogValue = 0.0;
        double analogOutputStatusValue = 0.0;

        final Flags onlineFlags = new Flags(Flag.ONLINE);
        final UpdateOptions detectEvent = UpdateOptions.detectEvent();

        // Handle user input
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                final String line = reader.readLine();
                switch (line) {
                    case "x":
                        return;
                    case "enable":
                        outstation.enable();
                        break;
                    case "disable":
                        outstation.disable();
                        break;
                    case "bi":
                    {
                        binaryValue = !binaryValue;
                        final boolean pointValue = binaryValue;
                        outstation.transaction(
                                db -> {
                                    BinaryInput value =
                                            new BinaryInput(
                                                    ushort(7),
                                                    pointValue,
                                                    onlineFlags,
                                                    OutstationTimeService.now());
                                    db.updateBinaryInput(value, detectEvent);
                                });
                        break;
                    }
                    case "dbbi":
                    {
                        doubleBitBinaryValue =
                                doubleBitBinaryValue == DoubleBit.DETERMINED_OFF
                                        ? DoubleBit.DETERMINED_ON
                                        : DoubleBit.DETERMINED_OFF;
                        final DoubleBit pointValue = doubleBitBinaryValue;
                        outstation.transaction(
                                db -> {
                                    DoubleBitBinaryInput value =
                                            new DoubleBitBinaryInput(
                                                    ushort(7),
                                                    pointValue,
                                                    onlineFlags,
                                                    OutstationTimeService.now());
                                    db.updateDoubleBitBinaryInput(value, detectEvent);
                                });
                        break;
                    }
                    case "bos":
                    {
                        binaryOutputStatusValue = !binaryOutputStatusValue;
                        final boolean pointValue = binaryOutputStatusValue;
                        outstation.transaction(
                                db -> {
                                    BinaryOutputStatus value =
                                            new BinaryOutputStatus(
                                                    ushort(7),
                                                    pointValue,
                                                    onlineFlags,
                                                    OutstationTimeService.now());
                                    db.updateBinaryOutputStatus(value, detectEvent);
                                });
                        break;
                    }
                    case "co":
                    {
                        counterValue += 1;
                        final long pointValue = counterValue;
                        outstation.transaction(
                                db -> {
                                    Counter value =
                                            new Counter(
                                                    ushort(7),
                                                    uint(pointValue),
                                                    onlineFlags,
                                                    OutstationTimeService.now());
                                    db.updateCounter(value, detectEvent);
                                });
                        break;
                    }
                    case "fco":
                    {
                        frozenCounterValue += 1;
                        final long pointValue = frozenCounterValue;
                        outstation.transaction(
                                db -> {
                                    FrozenCounter value =
                                            new FrozenCounter(
                                                    ushort(7),
                                                    uint(pointValue),
                                                    onlineFlags,
                                                    OutstationTimeService.now());
                                    db.updateFrozenCounter(value, detectEvent);
                                });
                        break;
                    }
                    case "ai":
                    {
                        analogValue += 1;
                        final double pointValue = analogValue;
                        outstation.transaction(
                                db -> {
                                    AnalogInput value =
                                            new AnalogInput(
                                                    ushort(7),
                                                    pointValue,
                                                    onlineFlags,
                                                    OutstationTimeService.now());
                                    db.updateAnalogInput(value, detectEvent);
                                });
                        break;
                    }
                    case "aos":
                    {
                        analogOutputStatusValue += 1;
                        final double pointValue = analogOutputStatusValue;
                        outstation.transaction(
                                db -> {
                                    AnalogOutputStatus value =
                                            new AnalogOutputStatus(
                                                    ushort(7),
                                                    pointValue,
                                                    onlineFlags,
                                                    OutstationTimeService.now());
                                    db.updateAnalogOutputStatus(value, detectEvent);
                                });
                        break;
                    }
                    case "os":
                    {
                        outstation.transaction(
                                db -> {
                                    List<UByte> octetString = new ArrayList<>();
                                    for (byte octet : "Hello".getBytes(StandardCharsets.US_ASCII)) {
                                        octetString.add(ubyte(octet));
                                    }

                                    db.updateOctetString(ushort(7), octetString, detectEvent);
                                });
                        break;
                    }
                    default:
                        System.out.printf("Unknown command: %s%n", line);
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
