/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Beckhoff;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.IMessage.AdsNetMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Thread.SimpleHybirdLock;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.Array;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Beckhoff.AdsDeviceInfo;
import HslCommunication.Profinet.Beckhoff.Helper.AdsHelper;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class BeckhoffAdsNet
extends NetworkDeviceBase {
    private byte[] targetAMSNetId = new byte[8];
    private byte[] sourceAMSNetId = new byte[8];
    private String senderAMSNetId = "";
    private String _targetAmsNetID = "";
    private boolean useAutoAmsNetID = false;
    private boolean useTagCache = false;
    private HashMap<String, Integer> tagCaches = new HashMap();
    private SimpleHybirdLock tagLock = new SimpleHybirdLock();
    private SoftIncrementCount incrementCount = new SoftIncrementCount(Integer.MAX_VALUE, 1L, 1);

    public BeckhoffAdsNet() {
        this.WordLength = (short)2;
        this.targetAMSNetId[4] = 1;
        this.targetAMSNetId[5] = 1;
        this.targetAMSNetId[6] = 83;
        this.targetAMSNetId[7] = 3;
        this.sourceAMSNetId[4] = 1;
        this.sourceAMSNetId[5] = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setUseServerActivePush(true);
    }

    public BeckhoffAdsNet(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new AdsNetMessage();
    }

    @Override
    public void setIpAddress(String ipAddress) {
        super.setIpAddress(ipAddress);
        String[] ip = Utilities.SplitDot(super.getIpAddress());
        for (int i = 0; i < ip.length; ++i) {
            this.targetAMSNetId[i] = (byte)Integer.parseInt(ip[i]);
        }
    }

    public void setUseTagCache(boolean value) {
        this.useTagCache = value;
    }

    public boolean getUseTagCache() {
        return this.useTagCache;
    }

    public boolean getUseAutoAmsNetID() {
        return this.useAutoAmsNetID;
    }

    public void setUseAutoAmsNetID(boolean value) {
        this.useAutoAmsNetID = value;
    }

    public int getAmsPort() {
        return BitConverter.ToUInt16(this.targetAMSNetId, 6);
    }

    public void setAmsPort(int value) {
        this.targetAMSNetId[6] = BitConverter.GetBytes(value)[0];
        this.targetAMSNetId[7] = BitConverter.GetBytes(value)[1];
    }

    public void SetTargetAMSNetId(String amsNetId) {
        if (!Utilities.IsStringNullOrEmpty(amsNetId)) {
            Utilities.ByteArrayCopyTo(AdsHelper.StrToAMSNetId(amsNetId), this.targetAMSNetId, 0);
            this._targetAmsNetID = amsNetId;
        }
    }

    public void SetSenderAMSNetId(String amsNetId) {
        if (!Utilities.IsStringNullOrEmpty(amsNetId)) {
            Utilities.ByteArrayCopyTo(AdsHelper.StrToAMSNetId(amsNetId), this.sourceAMSNetId, 0);
            this.senderAMSNetId = amsNetId;
        }
    }

    public String GetSenderAMSNetId() {
        return AdsHelper.GetAmsNetIdString(this.sourceAMSNetId, 0);
    }

    public String GetTargetAMSNetId() {
        return AdsHelper.GetAmsNetIdString(this.targetAMSNetId, 0);
    }

    @Override
    public byte[] PackCommandWithHeader(byte[] command) {
        int invokeId = (int)this.incrementCount.GetCurrentValue();
        Utilities.ByteArrayCopyTo(this.targetAMSNetId, command, 6);
        Utilities.ByteArrayCopyTo(this.sourceAMSNetId, command, 14);
        command[34] = BitConverter.GetBytes(invokeId)[0];
        command[35] = BitConverter.GetBytes(invokeId)[1];
        command[36] = BitConverter.GetBytes(invokeId)[2];
        command[37] = BitConverter.GetBytes(invokeId)[3];
        return super.PackCommandWithHeader(command);
    }

    @Override
    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        if (response.length >= 38) {
            int commandId = this.getByteTransform().TransUInt16(response, 22);
            OperateResultExOne<Integer> check = AdsHelper.CheckResponse(response);
            if (!check.IsSuccess) {
                if (check.ErrorCode == 1809 && (commandId == 2 || commandId == 3)) {
                    this.tagLock.Enter();
                    this.tagCaches.clear();
                    this.tagLock.Leave();
                }
                return OperateResultExOne.CreateFailedResult(check);
            }
            try {
                if (commandId == 1) {
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 42));
                }
                if (commandId == 2) {
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 46));
                }
                if (commandId == 3) {
                    return OperateResultExOne.CreateSuccessResult(new byte[0]);
                }
                if (commandId == 4) {
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 42));
                }
                if (commandId == 5) {
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 42));
                }
                if (commandId == 6) {
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 42));
                }
                if (commandId == 7) {
                    return OperateResultExOne.CreateSuccessResult(new byte[0]);
                }
                if (commandId == 9) {
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 46));
                }
            }
            catch (Exception ex) {
                return new OperateResultExOne<byte[]>("UnpackResponseContent failed: " + ex.getMessage() + "\r\nSource: " + SoftBasic.ByteToHexString(response, ' '));
            }
        }
        return super.UnpackResponseContent(send, response);
    }

    @Override
    protected void ExtraAfterReadFromCoreServer(OperateResult read) {
        if (!read.IsSuccess && read.ErrorCode < 0 && this.useTagCache) {
            this.tagLock.Enter();
            this.tagCaches.clear();
            this.tagLock.Leave();
        }
        super.ExtraAfterReadFromCoreServer(read);
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        if (Utilities.IsStringNullOrEmpty(this.senderAMSNetId) && Utilities.IsStringNullOrEmpty(this._targetAmsNetID)) {
            this.useAutoAmsNetID = true;
        }
        if (this.useAutoAmsNetID) {
            OperateResultExOne<byte[]> read1 = this.GetLocalNetId();
            if (!read1.IsSuccess) {
                return read1;
            }
            if (((byte[])read1.Content).length >= 12) {
                Array.Copy((byte[])read1.Content, 6, this.targetAMSNetId, 0, 6);
            }
            OperateResult send2 = this.Send(socket, AdsHelper.PackAmsTcpHelper(4096, new byte[2]));
            if (!send2.IsSuccess) {
                return send2;
            }
            OperateResultExOne<byte[]> read2 = this.ReceiveByMessage(socket, this.getReceiveTimeOut(), this.GetNewNetMessage());
            if (!read2.IsSuccess) {
                return read2;
            }
            if (((byte[])read2.Content).length >= 14) {
                Array.Copy((byte[])read2.Content, 6, this.sourceAMSNetId, 0, 8);
            }
            return super.InitializationOnConnect(socket);
        }
        if (Utilities.IsStringNullOrEmpty(this.senderAMSNetId)) {
            InetAddress iPEndPoint = socket.getLocalAddress();
            this.sourceAMSNetId[6] = BitConverter.GetBytes(socket.getLocalPort())[0];
            this.sourceAMSNetId[7] = BitConverter.GetBytes(socket.getLocalPort())[1];
            Utilities.ByteArrayCopyTo(iPEndPoint.getAddress(), this.sourceAMSNetId, 0);
        }
        if (this.useTagCache) {
            this.tagLock.Enter();
            this.tagCaches.clear();
            this.tagLock.Leave();
        }
        return super.InitializationOnConnect(socket);
    }

    private OperateResultExOne<byte[]> GetLocalNetId() {
        OperateResultExOne<Socket> opSocket = this.CreateSocketAndConnect(this.getIpAddress(), this.getPort(), this.getConnectTimeOut());
        if (!opSocket.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(opSocket);
        }
        OperateResult send = this.Send((Socket)opSocket.Content, AdsHelper.PackAmsTcpHelper(4098, new byte[4]));
        if (!send.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(send);
        }
        OperateResultExOne<byte[]> read = this.ReceiveByMessage((Socket)opSocket.Content, this.getReceiveTimeOut(), this.GetNewNetMessage());
        if (!read.IsSuccess) {
            return read;
        }
        this.CloseSocket((Socket)opSocket.Content);
        return read;
    }

    @Override
    protected boolean DecideWhetherQAMessage(Socket socket, OperateResultExOne<byte[]> receive) {
        int headerFlags;
        if (!receive.IsSuccess) {
            if (this.useTagCache) {
                this.tagLock.Enter();
                this.tagCaches.clear();
                this.tagLock.Leave();
            }
            return false;
        }
        byte[] response = (byte[])receive.Content;
        if (response.length >= 2 && (headerFlags = BitConverter.ToUInt16(response, 0)) == 0) {
            int commandId;
            return response.length < 24 || (commandId = this.getByteTransform().TransUInt16(response, 22)) != 8;
        }
        return false;
    }

    public OperateResultExOne<Integer> ReadValueHandle(String address) {
        if (!address.startsWith("s=")) {
            return new OperateResultExOne<Integer>("When read valueHandle, address must startwith 's=', forexample: s=MAIN.A");
        }
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadWriteCommand(address, 4, false, AdsHelper.StrToAdsBytes(address.substring(2)));
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult((int)BitConverter.ToUInt32((byte[])read.Content, 0));
    }

    public OperateResultExOne<String> TransValueHandle(String address) {
        if (address.startsWith("s=") || address.startsWith("S=")) {
            if (this.useTagCache) {
                this.tagLock.Enter();
                if (this.tagCaches.containsKey(address)) {
                    return OperateResultExOne.CreateSuccessResult("i=" + this.tagCaches.get(address));
                }
                this.tagLock.Leave();
            }
            OperateResultExOne<Integer> read = this.ReadValueHandle(address);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            if (this.useTagCache) {
                this.tagLock.Enter();
                if (!this.tagCaches.containsKey(address)) {
                    this.tagCaches.put(address, (Integer)read.Content);
                }
                this.tagLock.Leave();
            }
            return OperateResultExOne.CreateSuccessResult("i=" + read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(address);
    }

    public OperateResultExOne<AdsDeviceInfo> ReadAdsDeviceInfo() {
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadDeviceInfoCommand();
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(new AdsDeviceInfo((byte[])read.Content));
    }

    public OperateResultExTwo<Integer, Integer> ReadAdsState() {
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadStateCommand();
        if (!build.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(build);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        return OperateResultExTwo.CreateSuccessResult(BitConverter.ToUInt16((byte[])read.Content, 0), BitConverter.ToUInt16((byte[])read.Content, 2));
    }

    public OperateResult WriteAdsState(short state, short deviceState, byte[] data) {
        OperateResultExOne<byte[]> build = AdsHelper.BuildWriteControlCommand(state, deviceState, data);
        if (!build.IsSuccess) {
            return build;
        }
        return this.ReadFromCoreServer((byte[])build.Content);
    }

    public OperateResult ReleaseSystemHandle(int handle) {
        OperateResultExOne<byte[]> build = AdsHelper.BuildReleaseSystemHandle(handle);
        if (!build.IsSuccess) {
            return build;
        }
        return this.ReadFromCoreServer((byte[])build.Content);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<String> addressCheck = this.TransValueHandle(address);
        if (!addressCheck.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressCheck);
        }
        address = (String)addressCheck.Content;
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadCommand(address, length, false);
        if (!build.IsSuccess) {
            return build;
        }
        return this.ReadFromCoreServer((byte[])build.Content);
    }

    public OperateResultExOne<byte[]> Read(String[] address, short[] length) {
        if (address.length != length.length) {
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());
        }
        for (int i = 0; i < address.length; ++i) {
            OperateResultExOne<String> addressCheck = this.TransValueHandle(address[i]);
            if (!addressCheck.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(addressCheck);
            }
            address[i] = (String)addressCheck.Content;
        }
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadCommand(address, length);
        if (!build.IsSuccess) {
            return build;
        }
        return this.ReadFromCoreServer((byte[])build.Content);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<String> addressCheck = this.TransValueHandle(address);
        if (!addressCheck.IsSuccess) {
            return addressCheck;
        }
        address = (String)addressCheck.Content;
        OperateResultExOne<byte[]> build = AdsHelper.BuildWriteCommand(address, value, false);
        if (!build.IsSuccess) {
            return build;
        }
        return this.ReadFromCoreServer((byte[])build.Content);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        if (address.matches("^[MIQ][0-9]+\\.[0-7]$") && length > 1) {
            return HslHelper.ReadBool(this, address, length, 8, false);
        }
        OperateResultExOne<String> addressCheck = this.TransValueHandle(address);
        if (!addressCheck.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressCheck);
        }
        address = (String)addressCheck.Content;
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadCommand(address, length, true);
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(Array.GetBoolArrayFromBytes((byte[])read.Content, 1));
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExOne<String> addressCheck = this.TransValueHandle(address);
        if (!addressCheck.IsSuccess) {
            return addressCheck;
        }
        address = (String)addressCheck.Content;
        OperateResultExOne<byte[]> build = AdsHelper.BuildWriteCommand(address, value, true);
        if (!build.IsSuccess) {
            return build;
        }
        return this.ReadFromCoreServer((byte[])build.Content);
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        return ByteTransformHelper.GetByteResultFromBytes(this.Read(address, (short)1), this.getByteTransform());
    }

    public OperateResult Write(String address, byte value) {
        return this.Write(address, new byte[]{value});
    }

    @Override
    public String toString() {
        return "BeckhoffAdsNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

