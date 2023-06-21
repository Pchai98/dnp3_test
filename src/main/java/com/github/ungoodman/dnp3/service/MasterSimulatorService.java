package com.github.ungoodman.dnp3.service;

import com.github.ungoodman.dnp3.handler.SimulateAssociationHandler;
import com.github.ungoodman.dnp3.listener.SimulateClientStateListener;
import com.github.ungoodman.dnp3.service.logger.LoggingFileReader;
import com.github.ungoodman.dnp3.config.MasterConfiguration;
import com.github.ungoodman.dnp3.handler.SimulateReadHandler;
import com.github.ungoodman.dnp3.service.util.SimulateAssociationInformation;
import io.stepfunc.dnp3.*;
import io.stepfunc.dnp3.Runtime;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;

import static org.joou.Unsigned.*;
import static org.joou.Unsigned.ubyte;

public class MasterSimulatorService {
    private Runtime runtime;

    public MasterSimulatorService(Runtime runtime) {
        this.runtime = runtime;
    }

    private void runTcp() {
        // ANCHOR: create_tcp_channel
        MasterChannel channel =
                MasterChannel.createTcpChannel(
                        this.runtime,
                        LinkErrorMode.CLOSE,
                        MasterConfiguration.getMasterChannelConfig(),
                        new EndpointList("127.0.0.1:20000"),
                        new ConnectStrategy(),
                        new SimulateClientStateListener());
        // ANCHOR_END: create_tcp_channel

        try {
            runChannel(channel);
        } finally {
            channel.shutdown();
        }
    }

    private void runTls(TlsClientConfig tlsConfig) {
        // ANCHOR: create_tls_channel
        MasterChannel channel =
                MasterChannel.createTlsChannel(
                        runtime,
                        LinkErrorMode.CLOSE,
                        MasterConfiguration.getMasterChannelConfig(),
                        new EndpointList("127.0.0.1:20001"),
                        new ConnectStrategy(),
                        new SimulateClientStateListener(),
                        tlsConfig);
        // ANCHOR_END: create_tls_channel

        try {
            runChannel(channel);
        } finally {
            channel.shutdown();
        }
    }

    private void runSerial() {
        MasterChannel channel =
                MasterChannel.createSerialChannel(
                        runtime,
                        MasterConfiguration.getMasterChannelConfig(),
                        "/dev/pts/4", // replace with a real port
                        new SerialSettings(),
                        Duration.ofSeconds(5),
                        state -> System.out.println("Port state change: " + state)
                );
        // ANCHOR_END: create_serial_channel

        try {
            runChannel(channel);
        } finally {
            channel.shutdown();
        }
    }

    public void run(String[] args) {
        if (args.length != 1) {
            System.err.println("You must specify a transport");
            System.err.println("Usage: master-example <transport> (tcp, serial, tls-ca, tls-self-signed)");
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
                runTls(MasterConfiguration.getTlsCAConfig());
                break;
            case "tls-self-signed":
                runTls(MasterConfiguration.getTlsSelfSignedConfig());
                break;
            default:
                System.err.printf("Unknown transport: %s%n", type);
        }
    }

    private static void runOneCommand(MasterChannel channel, AssociationId association, PollId poll, String command) throws Exception {
        switch (command) {
            case "enable":
                channel.enable();
                break;
            case "disable":
                channel.disable();
                break;
            case "dln":
                channel.setDecodeLevel(DecodeLevel.nothing());
                break;
            case "dlv":
                channel.setDecodeLevel(DecodeLevel.nothing().withApplication(AppDecodeLevel.OBJECT_VALUES));
                break;
            case "rao": {
                Request request = new Request();
                request.addAllObjectsHeader(Variation.GROUP40_VAR0);
                channel.read(association, request).toCompletableFuture().get();
                break;
            }
            case "rmo": {
                Request request = new Request();
                request.addAllObjectsHeader(Variation.GROUP10_VAR0);
                request.addAllObjectsHeader(Variation.GROUP40_VAR0);
                channel.read(association, request).toCompletableFuture().get();
                break;
            }
            case "cmd": {
                // ANCHOR: assoc_control
                CommandSet commands = new CommandSet();
                Group12Var1 control = Group12Var1.fromCode(ControlCode.fromOpType(OpType.LATCH_ON));
                commands.addG12V1U16(ushort(3), control);
                channel
                        .operate(association, CommandMode.SELECT_BEFORE_OPERATE, commands)
                        .toCompletableFuture()
                        .get();
                // ANCHOR_END: assoc_control
                break;
            }
            case "evt":
                channel.demandPoll(poll);
                break;
            case "lts": {
                channel.synchronizeTime(association, TimeSyncMode.LAN).toCompletableFuture().get();
                break;
            }
            case "nts": {
                channel.synchronizeTime(association, TimeSyncMode.NON_LAN).toCompletableFuture().get();
                break;
            }
            case "wad": {
                WriteDeadBandRequest request = new WriteDeadBandRequest();
                request.addG34v1U8(ubyte(3), ushort(5));
                request.addG34v3U16(ushort(5), 2.5f);
                channel.writeDeadBands(association, request).toCompletableFuture().get();
                break;
            }
            case "fat": {
                Request request = new Request();
                request.addTimeAndInterval(ulong(0), uint(86400000));
                request.addAllObjectsHeader(Variation.GROUP20_VAR0);
                channel.sendAndExpectEmptyResponse(association, FunctionCode.FREEZE_AT_TIME, request).toCompletableFuture().get();
                break;
            }
            case "rda": {
                // ANCHOR: read_attributes
                Request request = new Request();
                request.addSpecificAttribute(AttributeVariations.ALL_ATTRIBUTES_REQUEST, ubyte(0));
                channel.read(association, request).toCompletableFuture().get();
                // ANCHOR_END: read_attributes
                break;
            }
            case "wda": {
                // ANCHOR: write_attribute
                Request request = new Request();
                request.addStringAttribute(AttributeVariations.USER_ASSIGNED_LOCATION, ubyte(0), "Mt. Olympus");
                channel.sendAndExpectEmptyResponse(association, FunctionCode.WRITE, request).toCompletableFuture().get();
                // ANCHOR_END: write_attribute
                break;
            }
            case "ral": {
                Request request = new Request();
                request.addSpecificAttribute(AttributeVariations.LIST_OF_VARIATIONS, ubyte(0));
                channel.read(association, request).toCompletableFuture().get();
                break;
            }
            case "crt": {
                Duration delay = channel.coldRestart(association).toCompletableFuture().get();
                System.out.println("Restart delay: " + delay);
                break;
            }
            case "wrt": {
                Duration delay = channel.warmRestart(association).toCompletableFuture().get();
                System.out.println("Restart delay: " + delay);
                break;
            }
            case "rd": {
                // ANCHOR: read_directory
                List<FileInfo> items = channel
                        .readDirectory(association, ".", DirReadConfig.defaults())
                        .toCompletableFuture().get();
                for (FileInfo info : items) {
                    printFileInfo(info);
                }
                // ANCHOR_END: read_directory
                break;
            }
            case "gfi": {
                // ANCHOR: get_file_info
                FileInfo info = channel.getFileInfo(association, ".").toCompletableFuture().get();
                printFileInfo(info);
                // ANCHOR_END: get_file_info
                break;
            }
            case "rf": {
                // ANCHOR: read_file
                channel.readFile(association, ".", FileReadConfig.defaults(), new LoggingFileReader());
                // ANCHOR_END: read_file
                break;
            }
            case "lsr": {
                channel.checkLinkStatus(association).toCompletableFuture().get();
                break;
            }
            default:
                System.out.println("Unknown command");
                break;
        }
    }

    private static void runChannel(MasterChannel channel) {

        // Create the association
        // ANCHOR: association_create
        AssociationId association =
                channel.addAssociation(
                        ushort(1024),
                        MasterConfiguration.getAssociationConfig(),
                        new SimulateReadHandler(),
                        new SimulateAssociationHandler(),
                        new SimulateAssociationInformation());
        // ANCHOR_END: association_create

        // Create a periodic poll
        // ANCHOR: add_poll
        PollId poll =
                channel.addPoll(
                        association, Request.classRequest(false, true, true, true), Duration.ofSeconds(5));
        // ANCHOR_END: add_poll

        // start communications
        channel.enable();

        // Handle user input
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                final String command = reader.readLine();
                if (command.equals("x")) {
                    System.out.println("exiting");
                    return;
                }
                runOneCommand(channel, association, poll, command);
            } catch (Exception ex) {
                System.out.println("Error: " + ex);
            }
        }
    }

    private static void printFileInfo(FileInfo info) {
        System.out.println("file name: " + info.fileName);
        System.out.println("     type: " + info.fileType);
        System.out.println("     size: " + info.size);
        System.out.println("     created: " + info.timeCreated.toString());
    }
}
