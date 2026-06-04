/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.MQTT.MqttQualityOfServiceLevel;

public class MqttSubscribeMessage {
    public MqttQualityOfServiceLevel QualityOfServiceLevel = MqttQualityOfServiceLevel.AtMostOnce;
    public int Identifier = 0;
    public String[] Topics = null;

    public String toString() {
        return "MqttSubcribeMessage" + SoftBasic.ArrayFormat(this.Topics) + "}";
    }
}

