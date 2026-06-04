/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.NetworkBase.NetworkBase;
import HslCommunication.Core.Pipe.PipeSocket;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class NetworkUdpBase
extends NetworkBase {
    private PipeSocket pipeSocket = new PipeSocket();
    private int connectErrorCount = 0;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private int receiveTimeOut = 10000;
    private String connectionId = "";
    private int receiveCacheLength = 2048;

    public NetworkUdpBase() {
        this.setConnectionId(SoftBasic.GetUniqueStringByGuidAndRandom());
    }

    public String getIpAddress() {
        return this.pipeSocket.getIpAddress();
    }

    public void setIpAddress(String ipAddress) {
        this.pipeSocket.setIpAddress(ipAddress);
    }

    public int getPort() {
        return this.pipeSocket.getPort();
    }

    public void setPort(int port) {
        this.pipeSocket.setPort(port);
    }

    public int getReceiveTimeOut() {
        return this.receiveTimeOut;
    }

    public void setReceiveTimeOut(int receiveTimeOut) {
        this.receiveTimeOut = receiveTimeOut;
    }

    public String getConnectionId() {
        return this.connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public int getReceiveCacheLength() {
        return this.receiveCacheLength;
    }

    public void setReceiveCacheLength(int receiveCacheLength) {
        this.receiveCacheLength = receiveCacheLength;
    }

    protected byte[] PackCommandWithHeader(byte[] command) {
        return command;
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return OperateResultExOne.CreateSuccessResult(response);
    }

    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send) {
        return this.ReadFromCoreServer(send, true, true);
    }

    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send, boolean hasResponseData, boolean usePackAndUnpack) {
        byte[] sendValue;
        if (!Authorization.nzugaydgwadawdibbas()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.AuthorizationFailed());
        }
        byte[] byArray = sendValue = usePackAndUnpack ? this.PackCommandWithHeader(send) : send;
        if (this.LogNet != null) {
            this.LogNet.WriteDebug(this.toString(), StringResources.Language.Send() + " : " + SoftBasic.ByteToHexString(sendValue));
        }
        this.pipeSocket.PipeLockEnter();
        try {
            if (this.datagramSocket == null) {
                this.datagramSocket = new DatagramSocket();
            }
            InetAddress address = InetAddress.getByName(this.getIpAddress());
            this.datagramPacket = new DatagramPacket(sendValue, sendValue.length, address, this.getPort());
            this.datagramSocket.send(this.datagramPacket);
            if (this.getReceiveTimeOut() > 0) {
                this.datagramSocket.setSoTimeout(this.getReceiveTimeOut());
            }
            if (this.getReceiveTimeOut() < 0) {
                this.pipeSocket.PipeLockLeave();
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
            }
            if (!hasResponseData) {
                this.pipeSocket.PipeLockLeave();
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
            }
            byte[] buffer = new byte[this.getReceiveCacheLength()];
            DatagramPacket recePacket = new DatagramPacket(buffer, buffer.length);
            this.datagramSocket.receive(recePacket);
            byte[] receive = SoftBasic.BytesArraySelectBegin(buffer, recePacket.getLength());
            this.pipeSocket.PipeLockLeave();
            if (this.LogNet != null) {
                this.LogNet.WriteDebug(this.toString(), StringResources.Language.Receive() + " : " + SoftBasic.ByteToHexString(receive));
            }
            this.connectErrorCount = 0;
            this.pipeSocket.setIsSocketError(false);
            try {
                return usePackAndUnpack ? this.UnpackResponseContent(sendValue, receive) : OperateResultExOne.CreateSuccessResult(receive);
            }
            catch (Exception ex) {
                return new OperateResultExOne<byte[]>("UnpackResponseContent failed: " + ex.getMessage());
            }
        }
        catch (Exception ex) {
            this.pipeSocket.ChangePorts();
            this.pipeSocket.setIsSocketError(true);
            if (this.connectErrorCount < 100000000) {
                ++this.connectErrorCount;
            }
            if (this.datagramSocket != null) {
                this.datagramSocket.close();
                this.datagramSocket = null;
            }
            this.pipeSocket.PipeLockLeave();
            return new OperateResultExOne<byte[]>(-this.connectErrorCount, ex.getMessage());
        }
    }

    public OperateResultExOne<byte[]> ReadFromCoreServer(ArrayList<byte[]> send) {
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < send.size(); ++i) {
            byte[] data = send.get(i);
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(data);
            if (!read.IsSuccess) {
                return read;
            }
            if (read.Content == null) continue;
            Utilities.ArrayListAddArray(array, (byte[])read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(array));
    }

    public boolean IpAddressPing() throws IOException {
        return InetAddress.getByName(this.getIpAddress()).isReachable(3000);
    }

    public void SetPipeSocket(PipeSocket pipeSocket) {
        if (this.pipeSocket != null) {
            this.pipeSocket = pipeSocket;
        }
    }

    public void Close() {
        if (this.datagramSocket != null) {
            this.datagramSocket.close();
            this.datagramSocket = null;
        }
    }

    public PipeSocket GetPipeSocket() {
        return this.pipeSocket;
    }

    @Override
    public String toString() {
        return "NetworkUdpBase[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

