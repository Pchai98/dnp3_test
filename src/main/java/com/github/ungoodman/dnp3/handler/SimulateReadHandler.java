package com.github.ungoodman.dnp3.handler;

import io.stepfunc.dnp3.*;
import org.joou.UByte;

import java.util.List;

public class SimulateReadHandler implements ReadHandler {

    @Override
    public void beginFragment(ReadType readType, ResponseHeader header) {
        System.out.println("Beginning fragment (broadcast: " + header.iin.iin1.broadcast + ")");
    }

    @Override
    public void endFragment(ReadType readType, ResponseHeader header) {
        System.out.println("End fragment");
    }

    @Override
    public void handleBinaryInput(HeaderInfo info, List<BinaryInput> it) {
        System.out.println("Binary Inputs:");
        System.out.println("Qualifier: " + info.qualifier);
        System.out.println("Variation: " + info.variation);

        it.forEach(
                val -> {
                    System.out.println(
                            "BI "
                                    + val.index
                                    + ": Value="
                                    + val.value
                                    + " Flags="
                                    + val.flags.value
                                    + " Time="
                                    + val.time.value
                                    + " ("
                                    + val.time.quality
                                    + ")");
                });
    }

    @Override
    public void handleDoubleBitBinaryInput(HeaderInfo info, List<DoubleBitBinaryInput> it) {
        System.out.println("Double Bit Binary Inputs:");
        System.out.println("Qualifier: " + info.qualifier);
        System.out.println("Variation: " + info.variation);

        it.forEach(
                val -> {
                    System.out.println(
                            "DBBI "
                                    + val.index
                                    + ": Value="
                                    + val.value
                                    + " Flags="
                                    + val.flags.value
                                    + " Time="
                                    + val.time.value
                                    + " ("
                                    + val.time.quality
                                    + ")");
                });
    }

    @Override
    public void handleBinaryOutputStatus(HeaderInfo info, List<BinaryOutputStatus> it) {
        System.out.println("Binary Output Statuses:");
        System.out.println("Qualifier: " + info.qualifier);
        System.out.println("Variation: " + info.variation);

        it.forEach(
                val -> {
                    System.out.println(
                            "BOS "
                                    + val.index
                                    + ": Value="
                                    + val.value
                                    + " Flags="
                                    + val.flags.value
                                    + " Time="
                                    + val.time.value
                                    + " ("
                                    + val.time.quality
                                    + ")");
                });
    }

    @Override
    public void handleCounter(HeaderInfo info, List<Counter> it) {
        System.out.println("Counters:");
        System.out.println("Qualifier: " + info.qualifier);
        System.out.println("Variation: " + info.variation);

        it.forEach(
                val -> {
                    System.out.println(
                            "Counter "
                                    + val.index
                                    + ": Value="
                                    + val.value
                                    + " Flags="
                                    + val.flags.value
                                    + " Time="
                                    + val.time.value
                                    + " ("
                                    + val.time.quality
                                    + ")");
                });
    }

    @Override
    public void handleFrozenCounter(HeaderInfo info, List<FrozenCounter> it) {
        System.out.println("Frozen Counters:");
        System.out.println("Qualifier: " + info.qualifier);
        System.out.println("Variation: " + info.variation);

        it.forEach(
                val -> {
                    System.out.println(
                            "Frozen Counter "
                                    + val.index
                                    + ": Value="
                                    + val.value
                                    + " Flags="
                                    + val.flags.value
                                    + " Time="
                                    + val.time.value
                                    + " ("
                                    + val.time.quality
                                    + ")");
                });
    }

    @Override
    public void handleAnalogInput(HeaderInfo info, List<AnalogInput> it) {
        System.out.println("Analog Inputs:");
        System.out.println("Qualifier: " + info.qualifier);
        System.out.println("Variation: " + info.variation);

        it.forEach(
                val -> {
                    System.out.println(
                            "AI "
                                    + val.index
                                    + ": Value="
                                    + val.value
                                    + " Flags="
                                    + val.flags.value
                                    + " Time="
                                    + val.time.value
                                    + " ("
                                    + val.time.quality
                                    + ")");
                });
    }

    @Override
    public void handleAnalogOutputStatus(HeaderInfo info, List<AnalogOutputStatus> it) {
        System.out.println("Analog Output Statuses:");
        System.out.println("Qualifier: " + info.qualifier);
        System.out.println("Variation: " + info.variation);

        it.forEach(
                val -> {
                    System.out.println(
                            "AOS "
                                    + val.index
                                    + ": Value="
                                    + val.value
                                    + " Flags="
                                    + val.flags.value
                                    + " Time="
                                    + val.time.value
                                    + " ("
                                    + val.time.quality
                                    + ")");
                });
    }

    @Override
    public void handleOctetString(HeaderInfo info, List<OctetString> it) {
        System.out.println("Octet Strings:");
        System.out.println("Qualifier: " + info.qualifier);
        System.out.println("Variation: " + info.variation);

        it.forEach(
                val -> {
                    System.out.print("Octet String " + val.index + ": Value=");
                    val.value.forEach(
                            b -> System.out.print(String.format("%02X", b.byteValue()) + " "));
                    System.out.println();
                });
    }

    @Override
    public void handleStringAttr(HeaderInfo info, StringAttr attr, UByte set, UByte variation, String value) {
        System.out.printf("String attribute: %s set: %d var: %d value: %s%n", attr, set.intValue(), variation.intValue(), value);
    }
}
