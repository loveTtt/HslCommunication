/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Pipe;

import HslCommunication.Core.Net.NetSupport;
import HslCommunication.Core.Pipe.PipeBase;
import HslCommunication.Core.Types.HslHelper;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class PipeSocket
extends PipeBase {
    private String ipAddress = "127.0.0.1";
    private int[] _port = null;
    private int indexPort = -1;
    private Socket socket;
    private int receiveTimeOut = 5000;
    private int connectTimeOut = 10000;
    private int sleepTime = 0;
    private boolean isSocketError = false;

    public PipeSocket() {
        this._port = new int[]{2000};
    }

    public PipeSocket(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this._port = new int[]{port};
    }

    public boolean IsConnectitonError() {
        return this.getIsSocketError() || this.socket == null;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = HslHelper.GetIpAddressFromInput(ipAddress);
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public int getPort() {
        if (this._port.length == 1) {
            return this._port[0];
        }
        int index = this.indexPort;
        if (index < 0 || index >= this._port.length) {
            index = 0;
        }
        return this._port[index];
    }

    public void setPort(int value) {
        if (this._port.length == 1) {
            this._port[0] = value;
        } else {
            int index = this.indexPort;
            if (index < 0 || index >= this._port.length) {
                index = 0;
            }
            this._port[index] = value;
        }
    }

    public boolean getIsSocketError() {
        return this.isSocketError;
    }

    public void setIsSocketError(boolean value) {
        this.isSocketError = value;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setSocket(Socket value) {
        this.socket = value;
    }

    public int getConnectTimeOut() {
        return this.connectTimeOut;
    }

    public void setConnectTimeOut(int value) {
        this.connectTimeOut = value;
    }

    public int getReceiveTimeOut() {
        return this.receiveTimeOut;
    }

    public void setReceiveTimeOut(int value) {
        this.receiveTimeOut = value;
    }

    public int getSleepTime() {
        return this.sleepTime;
    }

    public void setSleepTime(int value) {
        this.sleepTime = value;
    }

    public void Dispose() {
        NetSupport.CloseSocket(this.socket);
    }

    public void SetMultiPorts(int[] ports) {
        if (ports != null && ports.length > 0) {
            this._port = ports;
            this.indexPort = -1;
        }
    }

    public SocketAddress GetConnectIPEndPoint() {
        if (this._port.length == 1) {
            return new InetSocketAddress(this.getIpAddress(), this._port[0]);
        }
        this.ChangePorts();
        int port = this._port[this.indexPort];
        return new InetSocketAddress(this.getIpAddress(), port);
    }

    public void ChangePorts() {
        if (this._port.length == 1) {
            return;
        }
        this.indexPort = this.indexPort < this._port.length - 1 ? ++this.indexPort : 0;
    }

    public String toString() {
        return "PipeSocket[" + this.ipAddress + ":" + this.getPort() + "]";
    }
}

