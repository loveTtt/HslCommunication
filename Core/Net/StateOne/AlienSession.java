/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.StateOne;

import java.net.Socket;
import java.util.Date;

public class AlienSession {
    private Socket socket = null;
    private String DTU = "";
    private boolean isStatusOk = true;
    private String PWD = "";
    private Date OnlineTime = new Date();

    public Socket getSocket() {
        return this.socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getDTU() {
        return this.DTU;
    }

    public void setDTU(String DTU) {
        this.DTU = DTU;
    }

    public boolean getIsStatusOk() {
        return this.isStatusOk;
    }

    public void setIsStatusOk(boolean isStatusOk) {
        this.isStatusOk = isStatusOk;
    }

    public String getPWD() {
        return this.PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    public void Offline() {
        if (this.isStatusOk) {
            this.isStatusOk = false;
        }
    }
}

