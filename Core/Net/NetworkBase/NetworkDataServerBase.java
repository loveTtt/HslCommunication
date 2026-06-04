/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.IReadWriteNet;
import HslCommunication.Core.Net.NetworkBase.NetworkServerBase;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Thread.SimpleHybirdLock;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Types.ActionOperateExTwo;
import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.IDataTransfer;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.LogNet.Core.ILogNet;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetworkDataServerBase
extends NetworkServerBase
implements IReadWriteNet {
    private String ConnectionId = SoftBasic.GetUniqueStringByGuidAndRandom();
    private ArrayList<AppSession> listSessions;
    private SimpleHybirdLock listLock = new SimpleHybirdLock();
    private ILogNet logNet;
    public boolean EnableWrite = true;
    public ActionOperateExTwo OnDataReceived;
    public ActionOperateExTwo OnDataSend;
    protected short WordLength = 1;
    protected boolean LogMsgFormatBinary = true;
    private IByteTransform byteTransform;

    public NetworkDataServerBase() {
        this.listSessions = new ArrayList();
    }

    protected void LoadFromBytes(byte[] content) {
    }

    protected byte[] SaveToBytes() {
        return new byte[0];
    }

    @Override
    public String getConnectionId() {
        return this.ConnectionId;
    }

    @Override
    public void setConnectionId(String connectionId) {
        this.ConnectionId = connectionId;
    }

    @Override
    public void setLogNet(ILogNet logNet) {
        this.logNet = logNet;
    }

    public boolean isEnableWrite() {
        return this.EnableWrite;
    }

    public void setEnableWrite(boolean enableWrite) {
        this.EnableWrite = enableWrite;
    }

    protected void RaiseDataReceived(Object source, byte[] receive) {
        if (this.OnDataReceived != null) {
            this.OnDataReceived.Action(source, receive);
        }
    }

    protected void RaiseDataSend(byte[] send) {
        if (this.OnDataSend != null) {
            this.OnDataSend.Action(this, send);
        }
    }

    protected String GetSerialMessageLogText(byte[] data) {
        return this.LogMsgFormatBinary ? SoftBasic.ByteToHexString(data, ' ') : SoftBasic.GetAsciiStringRender(data);
    }

    public IByteTransform getByteTransform() {
        return this.byteTransform;
    }

    public void setByteTransform(IByteTransform transform) {
        this.byteTransform = transform;
    }

    protected short GetWordLength(String address, int length, int dataTypeLength) {
        if (this.WordLength == 0) {
            int len = length * dataTypeLength * 2 / 4;
            return len == 0 ? (short)1 : (short)len;
        }
        return (short)(this.WordLength * length * dataTypeLength);
    }

    protected INetMessage GetNewNetMessage() {
        return null;
    }

    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive) {
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    @Override
    protected void CloseAction() {
        super.CloseAction();
        for (int i = this.listSessions.size() - 1; i >= 0; --i) {
            this.RemoveClient(this.listSessions.get(i));
        }
    }

    public void RemoveClient(AppSession session) {
        this.RemoveClient(session, null);
    }

    public void RemoveClient(AppSession session, String message) {
        this.listLock.Enter();
        boolean remove = this.listSessions.remove(session);
        this.listLock.Leave();
        if (remove) {
            this.CloseSocket(session.getWorkSocket());
            if (this.logNet != null) {
                this.logNet.WriteDebug(this.toString(), session.getIpEndPoint().toString() + " Offline " + (Utilities.IsStringNullOrEmpty(message) ? "" : message));
            }
        }
    }

    public void AddClient(AppSession session) {
        this.listLock.Enter();
        this.listSessions.add(session);
        this.listLock.Leave();
        if (this.logNet != null && session != null) {
            this.logNet.WriteDebug(this.toString(), session.getIpEndPoint().toString() + " Online");
        }
    }

    public int GetOnlineCount() {
        return this.listSessions.size();
    }

    @Override
    protected void ThreadPoolLogin(Socket socket, InetAddress endPoint) {
        AppSession session = new AppSession();
        session.setWorkSocket(socket);
        session.setIpEndPoint(socket.getInetAddress());
        this.AddClient(session);
        while (true) {
            try {
                while (true) {
                    if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
                        this.RemoveClient(session);
                        return;
                    }
                    OperateResultExOne<byte[]> read1 = this.ReceiveByMessage(session.getWorkSocket(), 0x6DDD00, this.GetNewNetMessage());
                    if (!read1.IsSuccess) {
                        this.RemoveClient(session);
                        return;
                    }
                    if (this.logNet != null) {
                        this.logNet.WriteDebug(this.toString(), "[" + session.getIpEndPoint() + "] Tcp Recv\uff1a" + (this.LogMsgFormatBinary ? SoftBasic.ByteToHexString((byte[])read1.Content, ' ') : SoftBasic.GetAsciiStringRender((byte[])read1.Content)));
                    }
                    OperateResultExOne<byte[]> read = this.ReadFromCoreServer(session, (byte[])read1.Content);
                    if (!read.IsSuccess && read.Content != null && ((byte[])read.Content).length > 0) {
                        read.IsSuccess = true;
                    }
                    if (read.IsSuccess) {
                        if (read.Content != null) {
                            this.Send(session.getWorkSocket(), (byte[])read.Content);
                            if (this.logNet != null) {
                                this.logNet.WriteDebug(this.toString(), "[" + session.getIpEndPoint() + "] Tcp Send\uff1a" + (this.LogMsgFormatBinary ? SoftBasic.ByteToHexString((byte[])read.Content, ' ') : SoftBasic.GetAsciiStringRender((byte[])read.Content)));
                            }
                        } else {
                            this.RemoveClient(session);
                            return;
                        }
                    }
                    session.UpdateHeartTime();
                    this.RaiseDataReceived(session, (byte[])read1.Content);
                }
            }
            catch (Exception ex) {
                this.RemoveClient(session, "SocketAsyncCallBack Exception -> {ex.Message}");
            }
        }
    }

    protected void Dispose(boolean disposing) {
        if (disposing) {
            this.OnDataSend = null;
            this.OnDataReceived = null;
        }
    }

    @Override
    public String toString() {
        return "NetworkDataServerBase[" + this.getPort() + "]";
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return new OperateResult(StringResources.Language.NotSupportedFunction());
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedFunction());
    }

    @Override
    public OperateResultExOne<Boolean> ReadBool(String address) {
        OperateResultExOne<boolean[]> read = this.ReadBool(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((boolean[])read.Content)[0]);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return new OperateResult(StringResources.Language.NotSupportedFunction());
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return this.Write(address, new boolean[]{value});
    }

    @Override
    public <T extends IDataTransfer> OperateResultExOne<T> ReadCustomer(String address, Class<T> tClass) {
        IDataTransfer Content;
        OperateResultExOne result = new OperateResultExOne();
        try {
            Content = (IDataTransfer)tClass.newInstance();
        }
        catch (Exception ex) {
            Content = null;
        }
        OperateResultExOne<byte[]> read = this.Read(address, Content.getReadCount());
        if (read.IsSuccess) {
            Content.ParseSource((byte[])read.Content);
            result.Content = Content;
            result.IsSuccess = true;
        } else {
            result.ErrorCode = read.ErrorCode;
            result.Message = read.Message;
        }
        return result;
    }

    @Override
    public <T extends IDataTransfer> OperateResult WriteCustomer(String address, T data) {
        return this.Write(address, data.ToSource());
    }

    @Override
    public OperateResult ConnectClose() {
        this.ServerClose();
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<Short> ReadInt16(String address) {
        OperateResultExOne<short[]> read = this.ReadInt16(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((short[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<short[]> ReadInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength)), new FunctionOperateExOne<byte[], short[]>(){

            @Override
            public short[] Action(byte[] content) {
                return NetworkDataServerBase.this.getByteTransform().TransInt16(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Integer> ReadUInt16(String address) {
        OperateResultExOne<int[]> read = this.ReadUInt16(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((int[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<int[]> ReadUInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength)), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return NetworkDataServerBase.this.getByteTransform().TransUInt16(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Integer> ReadInt32(String address) {
        OperateResultExOne<int[]> read = this.ReadInt32(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((int[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<int[]> ReadInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 2)), new FunctionOperateExOne<byte[], int[]>(){

            @Override
            public int[] Action(byte[] content) {
                return NetworkDataServerBase.this.getByteTransform().TransInt32(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Long> ReadUInt32(String address) {
        OperateResultExOne<long[]> read = this.ReadUInt32(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((long[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<long[]> ReadUInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 2)), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return NetworkDataServerBase.this.getByteTransform().TransUInt32(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Float> ReadFloat(String address) {
        OperateResultExOne<float[]> read = this.ReadFloat(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(Float.valueOf(((float[])read.Content)[0]));
    }

    @Override
    public OperateResultExOne<float[]> ReadFloat(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 2)), new FunctionOperateExOne<byte[], float[]>(){

            @Override
            public float[] Action(byte[] content) {
                return NetworkDataServerBase.this.getByteTransform().TransSingle(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Long> ReadInt64(String address) {
        OperateResultExOne<long[]> read = this.ReadInt64(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((long[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<long[]> ReadInt64(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 4)), new FunctionOperateExOne<byte[], long[]>(){

            @Override
            public long[] Action(byte[] content) {
                return NetworkDataServerBase.this.getByteTransform().TransInt64(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<Double> ReadDouble(String address) {
        OperateResultExOne<double[]> read = this.ReadDouble(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((double[])read.Content)[0]);
    }

    @Override
    public OperateResultExOne<double[]> ReadDouble(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, (short)(length * this.WordLength * 4)), new FunctionOperateExOne<byte[], double[]>(){

            @Override
            public double[] Action(byte[] content) {
                return NetworkDataServerBase.this.getByteTransform().TransDouble(content, 0, length);
            }
        });
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length) {
        return this.ReadString(address, length, StandardCharsets.US_ASCII);
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, final Charset encoding) {
        return ByteTransformHelper.GetResultFromBytes(this.Read(address, length), new FunctionOperateExOne<byte[], String>(){

            @Override
            public String Action(byte[] content) {
                return NetworkDataServerBase.this.getByteTransform().TransString(content, 0, content.length, encoding);
            }
        });
    }

    @Override
    public OperateResult Write(String address, short[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, short value) {
        return this.Write(address, new short[]{value});
    }

    @Override
    public OperateResult Write(String address, int[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, int value) {
        return this.Write(address, new int[]{value});
    }

    @Override
    public OperateResult Write(String address, float[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, float value) {
        return this.Write(address, new float[]{value});
    }

    @Override
    public OperateResult Write(String address, long[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, long value) {
        return this.Write(address, new long[]{value});
    }

    @Override
    public OperateResult Write(String address, double[] values) {
        return this.Write(address, this.getByteTransform().TransByte(values));
    }

    @Override
    public OperateResult Write(String address, double value) {
        return this.Write(address, new double[]{value});
    }

    @Override
    public OperateResult Write(String address, String value) {
        return this.Write(address, value, StandardCharsets.US_ASCII);
    }

    @Override
    public OperateResult Write(String address, String value, Charset encoding) {
        byte[] temp = this.getByteTransform().TransByte(value, encoding);
        if (this.WordLength == 1) {
            temp = SoftBasic.ArrayExpandToLengthEven(temp);
        }
        return this.Write(address, temp);
    }

    @Override
    public OperateResult Write(String address, String value, int length) {
        return this.Write(address, value, length, StandardCharsets.US_ASCII);
    }

    @Override
    public OperateResult Write(String address, String value, int length, Charset encoding) {
        byte[] temp = this.getByteTransform().TransByte(value, encoding);
        if (this.WordLength == 1) {
            temp = SoftBasic.ArrayExpandToLengthEven(temp);
        }
        temp = SoftBasic.ArrayExpandToLength(temp, length);
        return this.Write(address, temp);
    }

    public OperateResult WriteUnicodeString(String address, String value) {
        byte[] temp = Utilities.csharpString2Byte(value);
        return this.Write(address, temp);
    }

    public OperateResult WriteUnicodeString(String address, String value, int length) {
        byte[] temp = Utilities.csharpString2Byte(value);
        temp = SoftBasic.ArrayExpandToLength(temp, length * 2);
        return this.Write(address, temp);
    }
}

