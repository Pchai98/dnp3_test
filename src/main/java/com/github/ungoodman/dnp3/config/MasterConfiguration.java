package com.github.ungoodman.dnp3.config;

import io.stepfunc.dnp3.*;

import java.time.Duration;

import static org.joou.Unsigned.ushort;

public class MasterConfiguration {
    public static MasterChannelConfig getMasterChannelConfig() {
        MasterChannelConfig config = new MasterChannelConfig(ushort(1));
        config.decodeLevel.application = AppDecodeLevel.OBJECT_VALUES;
        return config;
    }
    // ANCHOR_END: master_channel_config

    // ANCHOR: association_config
    public static AssociationConfig getAssociationConfig() {
        return new AssociationConfig(
                // disable unsolicited first (Class 1/2/3)
                EventClasses.all(),
                // after the integrity poll, enable unsolicited (Class 1/2/3)
                EventClasses.all(),
                // perform startup integrity poll with Class 1/2/3/0
                Classes.all(),
                // don't automatically scan Class 1/2/3 when the corresponding IIN bit is asserted
                EventClasses.none())
                .withAutoTimeSync(AutoTimeSync.LAN)
                .withKeepAliveTimeout(Duration.ofSeconds(60));
    }
    // ANCHOR_END: association_config

    // ANCHOR: runtime_config
    public static RuntimeConfig getRuntimeConfig() {
        return new RuntimeConfig().withNumCoreThreads(ushort(4));
    }
    // ANCHOR_END: runtime_config

    public static TlsClientConfig getTlsSelfSignedConfig() {
        // ANCHOR: tls_self_signed_config
        TlsClientConfig config =
                new TlsClientConfig(
                        "test.com",
                        "./certs/self_signed/entity2_cert.pem",
                        "./certs/self_signed/entity1_cert.pem",
                        "./certs/self_signed/entity1_key.pem",
                        "" // no password
                ).withCertificateMode(CertificateMode.SELF_SIGNED);
        // ANCHOR_END: tls_self_signed_config
        return config;
    }

    public static TlsClientConfig getTlsCAConfig() {
        // ANCHOR: tls_ca_chain_config
        TlsClientConfig config =
                new TlsClientConfig(
                        "test.com",
                        "./certs/ca_chain/ca_cert.pem",
                        "./certs/ca_chain/entity1_cert.pem",
                        "./certs/ca_chain/entity1_key.pem",
                        "" // no password
                );
        // ANCHOR_END: tls_ca_chain_config
        return config;
    }
}
