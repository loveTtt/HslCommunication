/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

public class MqttControlMessage {
    public static final byte FAILED = 0;
    public static final byte CONNECT = 1;
    public static final byte CONNACK = 2;
    public static final byte PUBLISH = 3;
    public static final byte PUBACK = 4;
    public static final byte PUBREC = 5;
    public static final byte PUBREL = 6;
    public static final byte PUBCOMP = 7;
    public static final byte SUBSCRIBE = 8;
    public static final byte SUBACK = 9;
    public static final byte UNSUBSCRIBE = 10;
    public static final byte UNSUBACK = 11;
    public static final byte PINGREQ = 12;
    public static final byte PINGRESP = 13;
    public static final byte DISCONNECT = 14;
    public static final byte REPORTPROGRESS = 15;
}

