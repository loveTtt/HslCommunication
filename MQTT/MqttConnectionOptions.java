/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

import HslCommunication.MQTT.MqttCredential;

public class MqttConnectionOptions {
    public String IpAddress = "127.0.0.1";
    public int Port = 1883;
    public String ClientId = "";
    public int ConnectTimeout = 5000;
    public MqttCredential Credentials = null;
    public int KeepAlivePeriod = 100;
    public int KeepAliveSendInterval = 30;
    public boolean CleanSession = true;
    public boolean UseRSAProvider = false;
}

