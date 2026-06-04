/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.MQTT;

public class MqttCredential {
    private String UserName = null;
    private String Password = null;

    public MqttCredential() {
    }

    public MqttCredential(String name, String pwd) {
        this.UserName = name;
        this.Password = pwd;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getPassword() {
        return this.Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String toString() {
        return this.UserName;
    }
}

