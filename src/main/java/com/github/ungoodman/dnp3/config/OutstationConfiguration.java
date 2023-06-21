package com.github.ungoodman.dnp3.config;

import io.stepfunc.dnp3.*;

import static org.joou.Unsigned.ubyte;
import static org.joou.Unsigned.ushort;

public class OutstationConfiguration {
    // ANCHOR: event_buffer_config
    public static EventBufferConfig getEventBufferConfig() {
        return new EventBufferConfig(
                ushort(10), // binary
                ushort(10), // double-bit binary
                ushort(10), // binary output status
                ushort(5), // counter
                ushort(5), // frozen counter
                ushort(5), // analog
                ushort(5), // analog output status
                ushort(3) // octet string
        );
    }
    // ANCHOR_END: event_buffer_config

    public static OutstationConfig getOutstationConfig() {
        // ANCHOR: outstation_config
        // create an outstation configuration with default values
        OutstationConfig config =
                new OutstationConfig(
                        // outstation address
                        ushort(1024),
                        // master address
                        ushort(1),
                        // event buffer sizes
                        getEventBufferConfig())
                        .withDecodeLevel(new DecodeLevel().withApplication(AppDecodeLevel.OBJECT_VALUES));
        // ANCHOR_END: outstation_config
        return config;
    }

    public static TlsServerConfig getSelfSignedTlsConfig() {
        // ANCHOR: tls_self_signed_config
        TlsServerConfig config =
                new TlsServerConfig(
                        "test.com",
                        "./certs/self_signed/entity1_cert.pem",
                        "./certs/self_signed/entity2_cert.pem",
                        "./certs/self_signed/entity2_key.pem",
                        "" // no password
                ).withCertificateMode(CertificateMode.SELF_SIGNED);
        // ANCHOR_END: tls_self_signed_config
        return config;
    }

    public static TlsServerConfig getCaTlsConfig() {
        // ANCHOR: tls_ca_chain_config
        TlsServerConfig config =
                new TlsServerConfig(
                        "test.com",
                        "./certs/ca_chain/ca_cert.pem",
                        "./certs/ca_chain/entity2_cert.pem",
                        "./certs/ca_chain/entity2_key.pem",
                        "" // no password
                );
        // ANCHOR_END: tls_ca_chain_config
        return config;
    }

    // ANCHOR: database_init_function
    public static void initializeDatabase(Database db) {
        // add 10 points of each type
        for (int i = 0; i < 10; i++) {
            // you can explicitly specify the configuration for each point ...
            db.addBinaryInput(ushort(i), EventClass.CLASS1, new BinaryInputConfig(StaticBinaryInputVariation.GROUP1_VAR1, EventBinaryInputVariation.GROUP2_VAR2));
            // ... or just use the defaults
            db.addDoubleBitBinaryInput(ushort(i), EventClass.CLASS1, new DoubleBitBinaryInputConfig());
            db.addBinaryOutputStatus(ushort(i), EventClass.CLASS1, new BinaryOutputStatusConfig());
            db.addCounter(ushort(i), EventClass.CLASS1, new CounterConfig());
            db.addFrozenCounter(ushort(i), EventClass.CLASS1, new FrozenCounterConfig());
            db.addAnalogInput(ushort(i), EventClass.CLASS1, new AnalogInputConfig());
            db.addAnalogOutputStatus(ushort(i), EventClass.CLASS1, new AnalogOutputStatusConfig());
            db.addOctetString(ushort(i), EventClass.CLASS1);
        }

        // define device attributes made available to the master
        db.defineStringAttr(ubyte(0), false, AttributeVariations.DEVICE_MANUFACTURERS_NAME, "Step Function I/O");
        db.defineStringAttr(ubyte(0), true, AttributeVariations.USER_ASSIGNED_LOCATION, "Bend, OR");
    }
    // ANCHOR_END: database_init_function

}
