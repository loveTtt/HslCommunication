/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

import HslCommunication.MQTT.MqttQualityOfServiceLevel;

public class MqttApplicationMessage {
    public MqttQualityOfServiceLevel QualityOfServiceLevel = MqttQualityOfServiceLevel.AtMostOnce;
    public String Topic = null;
    public byte[] Payload = null;
    public boolean Retain = false;

    public MqttApplicationMessage() {
    }

    public MqttApplicationMessage(String topic, byte[] payload, boolean retained, MqttQualityOfServiceLevel qualityOfServiceLevel) {
        this.Topic = topic;
        this.Payload = payload;
        this.Retain = retained;
        this.QualityOfServiceLevel = qualityOfServiceLevel;
    }

    public String toString() {
        return this.Topic;
    }
}

