/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

import HslCommunication.MQTT.MqttApplicationMessage;
import java.util.Date;

public class MqttClientApplicationMessage
extends MqttApplicationMessage {
    private String clientId = "";
    private String userName = "";
    private boolean isCancelPublish = false;
    private Date createTime = new Date();

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isCancelPublish() {
        return this.isCancelPublish;
    }

    public void setCancelPublish(boolean cancelPublish) {
        this.isCancelPublish = cancelPublish;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

