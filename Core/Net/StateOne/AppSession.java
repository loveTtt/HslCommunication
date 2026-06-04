/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.StateOne;

import HslCommunication.BasicFramework.SoftBasic;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AppSession {
    private String IpAddress = null;
    private InetAddress IpEndPoint = null;
    private String LoginAlias = null;
    private Date HeartTime = null;
    private String ClientType = null;
    private String ClientUniqueID = SoftBasic.GetUniqueStringByGuidAndRandom();
    private byte[] BytesHead = null;
    private byte[] BytesContent = null;
    private String KeyGroup = null;
    private Socket WorkSocket = null;
    private Lock HybirdLockSend = new ReentrantLock();

    public AppSession() {
        this.HeartTime = new Date();
        this.BytesHead = new byte[32];
    }

    public String getIpAddress() {
        return this.IpAddress;
    }

    void setIpAddress(String ipAddress) {
        this.IpAddress = ipAddress;
    }

    public InetAddress getIpEndPoint() {
        return this.IpEndPoint;
    }

    public void setIpEndPoint(InetAddress ipEndPoint) {
        this.IpEndPoint = ipEndPoint;
    }

    public String getLoginAlias() {
        return this.LoginAlias;
    }

    public void setLoginAlias(String loginAlias) {
        this.LoginAlias = loginAlias;
    }

    public Date getHeartTime() {
        return this.HeartTime;
    }

    public void setHeartTime(Date date) {
        this.HeartTime = date;
    }

    public String getClientType() {
        return this.ClientType;
    }

    public void setClientType(String clientType) {
        this.ClientType = clientType;
    }

    public String getClientUniqueID() {
        return this.ClientUniqueID;
    }

    public byte[] getBytesHead() {
        return this.BytesHead;
    }

    public void setBytesHead(byte[] bytesHead) {
        this.BytesHead = bytesHead;
    }

    public byte[] getBytesContent() {
        return this.BytesContent;
    }

    public void setBytesContent(byte[] bytesContent) {
        this.BytesContent = bytesContent;
    }

    public String getKeyGroup() {
        return this.KeyGroup;
    }

    public void setKeyGroup(String keyGroup) {
        this.KeyGroup = keyGroup;
    }

    public Socket getWorkSocket() {
        return this.WorkSocket;
    }

    public void setWorkSocket(Socket workSocket) {
        this.WorkSocket = workSocket;
    }

    public Lock getHybirdLockSend() {
        return this.HybirdLockSend;
    }

    public void UpdateHeartTime() {
        this.HeartTime = new Date();
    }

    public void Clear() {
        this.BytesHead = new byte[32];
        this.BytesContent = null;
    }

    public String toString() {
        if (this.LoginAlias.isEmpty()) {
            return "[" + this.IpEndPoint.toString() + "]";
        }
        return "[" + this.IpEndPoint + "] [" + this.LoginAlias + "]";
    }
}

