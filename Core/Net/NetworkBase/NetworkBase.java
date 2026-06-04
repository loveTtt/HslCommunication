/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.SpecifiedCharacterMessage;
import HslCommunication.Core.Net.HslProtocol;
import HslCommunication.Core.Net.NetSupport;
import HslCommunication.Core.Types.ActionOperateExTwo;
import HslCommunication.Core.Types.HslTimeOut;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Core.Types.RemoteCloseException;
import HslCommunication.Enthernet.Redis.RedisHelper;
import HslCommunication.LogNet.Core.ILogNet;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import HslCommunication.WebSocket.WebSocketMessage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public abstract class NetworkBase {
    public ILogNet LogNet = null;
    public UUID Token = UUID.fromString("00000000-0000-0000-0000-000000000000");
    protected Socket CoreSocket = null;
    protected int fileCacheSize = 102400;

    public void setLogNet(ILogNet logNet) {
        this.LogNet = logNet;
    }

    protected void ThreadPoolCheckTimeOut(HslTimeOut timeout) {
        System.out.println("\u8fdb\u5165\u8d85\u65f6\u68c0\u6d4b:" + timeout.DelayTime);
        while (!timeout.IsSuccessful) {
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (new Date().getTime() - timeout.StartTime.getTime() <= (long)timeout.DelayTime) continue;
            if (timeout.IsSuccessful) break;
            System.out.println("\u68c0\u6d4b\u5230\u8d85\u65f6\u4fe1\u606f");
            timeout.IsTimeout = true;
            ILogNet logNet = this.LogNet;
            if (logNet != null) {
                logNet.WriteWarn(this.toString(), "Wait Time Out : " + timeout.DelayTime);
            }
            this.CloseSocket(timeout.WorkSocket);
            break;
        }
    }

    protected OperateResultExOne<byte[]> Receive(Socket socket, int length, int timeout, ActionOperateExTwo<Long, Long> reportProgress) {
        if (length == 0) {
            return OperateResultExOne.CreateSuccessResult(new byte[0]);
        }
        if (!Authorization.nzugaydgwadawdibbas()) {
            return new OperateResultExOne<byte[]>(StringResources.Language.AuthorizationFailed());
        }
        try {
            if (timeout < 0) {
                timeout = 0;
            }
            socket.setSoTimeout(timeout);
            if (length > 0) {
                byte[] data = NetSupport.ReadBytesFromSocket(socket, length, reportProgress);
                return OperateResultExOne.CreateSuccessResult(data);
            }
            byte[] bytes_receive = new byte[1024];
            InputStream input = socket.getInputStream();
            int receive_current = input.read(bytes_receive, 0, bytes_receive.length);
            if (receive_current <= 0) {
                throw new RemoteCloseException();
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectBegin(bytes_receive, receive_current));
        }
        catch (RemoteCloseException ex) {
            this.CloseSocket(socket);
            return new OperateResultExOne<byte[]>(StringResources.Language.RemoteClosedConnection());
        }
        catch (IOException ex) {
            this.CloseSocket(socket);
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    protected OperateResultExOne<byte[]> Receive(Socket socket, int length, int timeout) {
        return this.Receive(socket, length, timeout, null);
    }

    protected OperateResultExOne<byte[]> Receive(Socket socket, int length) {
        return this.Receive(socket, length, 60000, null);
    }

    protected OperateResultExOne<byte[]> ReceiveCommandLineFromSocket(Socket socket, byte endCode, int timeout) {
        ArrayList<Byte> bufferArray = new ArrayList<Byte>();
        try {
            Date st = new Date();
            boolean bOK = false;
            while (new Date().getTime() - st.getTime() < (long)timeout) {
                OperateResultExOne<byte[]> headResult = this.Receive(socket, 1, timeout);
                if (!headResult.IsSuccess) {
                    return headResult;
                }
                bufferArray.add(((byte[])headResult.Content)[0]);
                if (((byte[])headResult.Content)[0] != endCode) continue;
                bOK = true;
                break;
            }
            if (!bOK) {
                return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataTimeout());
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bufferArray));
        }
        catch (Exception ex) {
            this.CloseSocket(socket);
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    protected OperateResultExOne<byte[]> ReceiveCommandLineFromSocket(Socket socket, byte endCode) {
        return this.ReceiveCommandLineFromSocket(socket, endCode, Integer.MAX_VALUE);
    }

    protected OperateResultExOne<byte[]> ReceiveCommandLineFromSocket(Socket socket, byte endCode1, byte endCode2, int timeout) {
        ArrayList<Byte> bufferArray = new ArrayList<Byte>();
        try {
            Date st = new Date();
            boolean bOK = false;
            while (new Date().getTime() - st.getTime() < (long)timeout) {
                OperateResultExOne<byte[]> headResult = this.Receive(socket, 1, timeout);
                if (!headResult.IsSuccess) {
                    return headResult;
                }
                bufferArray.add(((byte[])headResult.Content)[0]);
                if (((byte[])headResult.Content)[0] != endCode2 || bufferArray.size() <= 1 || bufferArray.get(bufferArray.size() - 2) != endCode1) continue;
                bOK = true;
                break;
            }
            if (!bOK) {
                return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataTimeout());
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bufferArray));
        }
        catch (Exception ex) {
            this.CloseSocket(socket);
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    protected OperateResultExOne<byte[]> ReceiveCommandLineFromSocket(Socket socket, byte endCode1, byte endCode2) {
        return this.ReceiveCommandLineFromSocket(socket, endCode1, endCode2, 60000);
    }

    protected OperateResultExOne<byte[]> ReceiveByMessage(Socket socket, int timeOut, INetMessage netMessage, ActionOperateExTwo<Long, Long> reportProgress) {
        if (netMessage == null) {
            return this.Receive(socket, -1, timeOut);
        }
        if (netMessage.ProtocolHeadBytesLength() < 0) {
            byte[] headCode = Utilities.getBytes(netMessage.ProtocolHeadBytesLength());
            int codeLength = headCode[3] & 0xF;
            OperateResultExOne<byte[]> receive = null;
            if (codeLength == 1) {
                receive = this.ReceiveCommandLineFromSocket(socket, headCode[1], timeOut);
            } else if (codeLength == 2) {
                receive = this.ReceiveCommandLineFromSocket(socket, headCode[1], headCode[0], timeOut);
            }
            if (receive == null) {
                return new OperateResultExOne<byte[]>("Receive by specified code failed, length check failed");
            }
            if (!receive.IsSuccess) {
                return receive;
            }
            netMessage.setHeadBytes((byte[])receive.Content);
            if (netMessage instanceof SpecifiedCharacterMessage) {
                SpecifiedCharacterMessage message = (SpecifiedCharacterMessage)netMessage;
                if (message.getEndLength() == 0) {
                    return receive;
                }
                OperateResultExOne<byte[]> endResult = this.Receive(socket, message.getEndLength(), timeOut);
                if (!endResult.IsSuccess) {
                    return endResult;
                }
                return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceTwoByteArray((byte[])receive.Content, (byte[])endResult.Content));
            }
            return receive;
        }
        OperateResultExOne<byte[]> headResult = this.Receive(socket, netMessage.ProtocolHeadBytesLength(), timeOut);
        if (!headResult.IsSuccess) {
            return headResult;
        }
        netMessage.setHeadBytes((byte[])headResult.Content);
        int contentLength = netMessage.GetContentLengthByHeadBytes();
        if (contentLength <= 0) {
            return headResult;
        }
        OperateResultExOne<byte[]> contentResult = this.Receive(socket, contentLength, timeOut, reportProgress);
        if (!contentResult.IsSuccess) {
            return contentResult;
        }
        netMessage.setContentBytes((byte[])contentResult.Content);
        return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceTwoByteArray(netMessage.getHeadBytes(), (byte[])contentResult.Content));
    }

    protected OperateResultExOne<byte[]> ReceiveByMessage(Socket socket, int timeOut, INetMessage netMessage) {
        return this.ReceiveByMessage(socket, timeOut, null, netMessage);
    }

    protected OperateResultExOne<byte[]> ReceiveByMessage(Socket socket, int timeOut, byte[] sendValue, INetMessage netMessage) {
        if (netMessage != null && netMessage.ProtocolHeadBytesLength() == -1) {
            Date startTime = new Date();
            MemoryStream ms = new MemoryStream();
            INetMessage nullTmp = null;
            do {
                OperateResultExOne<byte[]> read = this.ReceiveByMessage(socket, timeOut, nullTmp, null);
                if (!read.IsSuccess) {
                    return read;
                }
                if (read.Content != null && ((byte[])read.Content).length > 0) {
                    ms.Write((byte[])read.Content);
                }
                if (!netMessage.CheckReceiveDataComplete(sendValue, ms)) continue;
                return OperateResultExOne.CreateSuccessResult(ms.ToArray());
            } while (timeOut < 0 || new Date().getTime() - startTime.getTime() <= (long)timeOut);
            return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataTimeout() + timeOut + " Received: " + SoftBasic.ByteToHexString(ms.ToArray(), ' '));
        }
        return this.ReceiveByMessage(socket, timeOut, netMessage, null);
    }

    protected OperateResult Send(Socket socket, byte[] data) {
        if (data == null) {
            return OperateResult.CreateSuccessResult();
        }
        return this.Send(socket, data, 0, data.length);
    }

    protected OperateResult Send(Socket socket, byte[] data, int offset, int size) {
        if (data == null) {
            return OperateResult.CreateSuccessResult();
        }
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.write(data, offset, size);
            return OperateResult.CreateSuccessResult();
        }
        catch (IOException ex) {
            this.CloseSocket(socket);
            return new OperateResult(ex.getMessage());
        }
    }

    protected OperateResultExOne<Socket> CreateSocketAndConnect(SocketAddress endPoint, int timeOut) {
        Socket socket = new Socket();
        try {
            socket.connect(endPoint, timeOut);
            return OperateResultExOne.CreateSuccessResult(socket);
        }
        catch (IOException ex) {
            if (this.LogNet != null) {
                this.LogNet.WriteException("CreateSocketAndConnect", ex);
            }
            this.CloseSocket(socket);
            return new OperateResultExOne<Socket>(ex.getMessage());
        }
    }

    protected OperateResultExOne<Socket> CreateSocketAndConnect(String ipAddress, int port, int timeOut) {
        InetSocketAddress endPoint = new InetSocketAddress(ipAddress, port);
        return this.CreateSocketAndConnect(endPoint, timeOut);
    }

    protected OperateResultExOne<Socket> CreateSocketAndConnect(String ipAddress, int port) {
        InetSocketAddress endPoint = new InetSocketAddress(ipAddress, port);
        return this.CreateSocketAndConnect(endPoint, 10000);
    }

    protected OperateResultExOne<Integer> ReadStream(InputStream stream, byte[] buffer) {
        try {
            int read_count = stream.read(buffer, 0, buffer.length);
            return OperateResultExOne.CreateSuccessResult(read_count);
        }
        catch (IOException ex) {
            return new OperateResultExOne<Integer>(ex.getMessage());
        }
    }

    protected OperateResult WriteStream(OutputStream stream, byte[] buffer) {
        try {
            stream.write(buffer, 0, buffer.length);
            return OperateResult.CreateSuccessResult();
        }
        catch (IOException ex) {
            return new OperateResult(ex.getMessage());
        }
    }

    protected boolean CheckRemoteToken(byte[] headBytes) {
        return SoftBasic.IsByteTokenEquel(headBytes, this.Token);
    }

    protected OperateResult SendBaseAndCheckReceive(Socket socket, int headCode, int customer, byte[] send) {
        send = HslProtocol.CommandBytes(headCode, customer, this.Token, send);
        OperateResult sendResult = this.Send(socket, send);
        if (!sendResult.IsSuccess) {
            return sendResult;
        }
        OperateResultExOne<Long> checkResult = this.ReceiveLong(socket);
        if (!checkResult.IsSuccess) {
            return checkResult;
        }
        if ((Long)checkResult.Content != (long)send.length) {
            this.CloseSocket(socket);
            return new OperateResult(StringResources.Language.CommandLengthCheckFailed());
        }
        return checkResult;
    }

    protected OperateResult SendBytesAndCheckReceive(Socket socket, int customer, byte[] send) {
        return this.SendBaseAndCheckReceive(socket, 1002, customer, send);
    }

    protected OperateResult SendStringAndCheckReceive(Socket socket, int customer, String send) {
        byte[] data = Utilities.IsStringNullOrEmpty(send) ? null : Utilities.csharpString2Byte(send);
        return this.SendBaseAndCheckReceive(socket, 1001, customer, data);
    }

    protected OperateResult SendStringAndCheckReceive(Socket socket, int customer, String[] sends) {
        return this.SendBaseAndCheckReceive(socket, 1005, customer, HslProtocol.PackStringArrayToByte(sends));
    }

    protected OperateResult SendAccountAndCheckReceive(Socket socket, int customer, String name, String pwd) {
        return this.SendBaseAndCheckReceive(socket, 5, customer, HslProtocol.PackStringArrayToByte(new String[]{name, pwd}));
    }

    protected OperateResultExTwo<byte[], byte[]> ReceiveAndCheckBytes(Socket socket, int timeout) {
        OperateResultExOne<byte[]> headResult = this.Receive(socket, 32, timeout);
        if (!headResult.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(headResult);
        }
        if (!this.CheckRemoteToken((byte[])headResult.Content)) {
            this.CloseSocket(socket);
            return new OperateResultExTwo<byte[], byte[]>(StringResources.Language.TokenCheckFailed());
        }
        int contentLength = Utilities.getInt((byte[])headResult.Content, 28);
        OperateResultExOne<byte[]> contentResult = this.Receive(socket, contentLength, timeout);
        if (!contentResult.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(contentResult);
        }
        OperateResult checkResult = this.SendLong(socket, 32 + contentLength);
        if (!checkResult.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(checkResult);
        }
        byte[] head = (byte[])headResult.Content;
        byte[] content = (byte[])contentResult.Content;
        content = HslProtocol.CommandAnalysis(head, content);
        return OperateResultExTwo.CreateSuccessResult(head, content);
    }

    protected OperateResultExTwo<Integer, String> ReceiveStringContentFromSocket(Socket socket) {
        return this.ReceiveStringContentFromSocket(socket, 30000);
    }

    protected OperateResultExTwo<Integer, String> ReceiveStringContentFromSocket(Socket socket, int timeout) {
        OperateResultExTwo<byte[], byte[]> receive = this.ReceiveAndCheckBytes(socket, timeout);
        if (!receive.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(receive);
        }
        if (Utilities.getInt((byte[])receive.Content1, 0) != 1001) {
            if (this.LogNet != null) {
                this.LogNet.WriteError(this.toString(), StringResources.Language.CommandHeadCodeCheckFailed());
            }
            this.CloseSocket(socket);
            return new OperateResultExTwo<Integer, String>(StringResources.Language.CommandHeadCodeCheckFailed());
        }
        if (receive.Content2 == null) {
            receive.Content2 = new byte[0];
        }
        return OperateResultExTwo.CreateSuccessResult(Utilities.getInt((byte[])receive.Content1, 4), Utilities.byte2CSharpString((byte[])receive.Content2));
    }

    protected OperateResultExTwo<Integer, String[]> ReceiveStringArrayContentFromSocket(Socket socket) {
        return this.ReceiveStringArrayContentFromSocket(socket, 30000);
    }

    protected OperateResultExTwo<Integer, String[]> ReceiveStringArrayContentFromSocket(Socket socket, int timeout) {
        OperateResultExTwo<byte[], byte[]> receive = this.ReceiveAndCheckBytes(socket, timeout);
        if (!receive.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(receive);
        }
        if (Utilities.getInt((byte[])receive.Content1, 0) != 1005) {
            if (this.LogNet != null) {
                this.LogNet.WriteError(this.toString(), StringResources.Language.CommandHeadCodeCheckFailed());
            }
            this.CloseSocket(socket);
            return new OperateResultExTwo<Integer, String[]>(StringResources.Language.CommandHeadCodeCheckFailed());
        }
        if (receive.Content2 == null) {
            receive.Content2 = new byte[4];
        }
        return OperateResultExTwo.CreateSuccessResult(Utilities.getInt((byte[])receive.Content1, 4), HslProtocol.UnPackStringArrayFromByte((byte[])receive.Content2));
    }

    protected OperateResultExTwo<Integer, byte[]> ReceiveBytesContentFromSocket(Socket socket) {
        return this.ReceiveBytesContentFromSocket(socket, 30000);
    }

    protected OperateResultExTwo<Integer, byte[]> ReceiveBytesContentFromSocket(Socket socket, int timeout) {
        OperateResultExTwo<byte[], byte[]> receive = this.ReceiveAndCheckBytes(socket, timeout);
        if (!receive.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(receive);
        }
        if (Utilities.getInt((byte[])receive.Content1, 0) != 1002) {
            if (this.LogNet != null) {
                this.LogNet.WriteError(this.toString(), StringResources.Language.CommandHeadCodeCheckFailed());
            }
            this.CloseSocket(socket);
            return new OperateResultExTwo<Integer, byte[]>(StringResources.Language.CommandHeadCodeCheckFailed());
        }
        return OperateResultExTwo.CreateSuccessResult(Utilities.getInt((byte[])receive.Content1, 4), receive.Content2);
    }

    private OperateResultExOne<Long> ReceiveLong(Socket socket) {
        OperateResultExOne<byte[]> read = this.Receive(socket, 8);
        if (read.IsSuccess) {
            return OperateResultExOne.CreateSuccessResult(Utilities.getLong((byte[])read.Content, 0));
        }
        return new OperateResultExOne<Long>(read.Message);
    }

    private OperateResult SendLong(Socket socket, long value) {
        return this.Send(socket, Utilities.getBytes(value));
    }

    protected void CloseSocket(Socket socket) {
        NetSupport.CloseSocket(socket);
    }

    protected OperateResult SendStreamToSocket(Socket socket, InputStream stream, long receive, ActionOperateExTwo<Long, Long> report, boolean reportByPercent) {
        OperateResultExOne<Integer> read;
        byte[] buffer = new byte[this.fileCacheSize];
        long percent = 0L;
        for (long SendTotal = 0L; SendTotal < receive; SendTotal += (long)((Integer)read.Content).intValue()) {
            read = this.ReadStream(stream, buffer);
            if (!read.IsSuccess) {
                this.CloseSocket(socket);
                return read;
            }
            byte[] newBuffer = new byte[((Integer)read.Content).intValue()];
            System.arraycopy(buffer, 0, newBuffer, 0, newBuffer.length);
            OperateResult write = this.SendBytesAndCheckReceive(socket, (Integer)read.Content, newBuffer);
            if (!write.IsSuccess) {
                this.CloseSocket(socket);
                return write;
            }
            if (!reportByPercent) continue;
            long percentCurrent = SendTotal * 100L / receive;
            if (percent == percentCurrent) continue;
            percent = percentCurrent;
            if (report == null) continue;
            report.Action(SendTotal, receive);
        }
        return OperateResult.CreateSuccessResult();
    }

    protected OperateResult WriteStreamFromSocket(Socket socket, OutputStream stream, long totalLength, ActionOperateExTwo<Long, Long> report, boolean reportByPercent) {
        OperateResultExTwo<Integer, byte[]> read;
        long percent = 0L;
        for (long count_receive = 0L; count_receive < totalLength; count_receive += (long)((Integer)read.Content1).intValue()) {
            read = this.ReceiveBytesContentFromSocket(socket, 60000);
            if (!read.IsSuccess) {
                return read;
            }
            OperateResult write = this.WriteStream(stream, (byte[])read.Content2);
            if (!write.IsSuccess) {
                this.CloseSocket(socket);
                return write;
            }
            if (!reportByPercent) continue;
            long percentCurrent = count_receive * 100L / totalLength;
            if (percent == percentCurrent) continue;
            percent = percentCurrent;
            if (report == null) continue;
            report.Action(count_receive, totalLength);
        }
        return OperateResult.CreateSuccessResult();
    }

    protected OperateResultExOne<WebSocketMessage> ReceiveWebSocketPayload(Socket socket) {
        WebSocketMessage message;
        OperateResultExTwo<WebSocketMessage, Boolean> read;
        ArrayList<Byte> data = new ArrayList<Byte>();
        do {
            read = this.ReceiveFrameWebSocketPayload(socket);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            Utilities.ArrayListAddArray(data, ((WebSocketMessage)read.Content1).Payload);
            message = new WebSocketMessage();
            message.HasMask = ((WebSocketMessage)read.Content1).HasMask;
            message.OpCode = ((WebSocketMessage)read.Content1).OpCode;
            message.Payload = Utilities.getBytes(data);
        } while (!((Boolean)read.Content2).booleanValue());
        return OperateResultExOne.CreateSuccessResult(message);
    }

    protected OperateResultExTwo<WebSocketMessage, Boolean> ReceiveFrameWebSocketPayload(Socket socket) {
        OperateResultExOne<byte[]> extended;
        OperateResultExOne<byte[]> head = this.Receive(socket, 2, 5000);
        if (!head.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(head);
        }
        boolean isEof = (((byte[])head.Content)[0] & 0x80) == 128;
        boolean hasMask = (((byte[])head.Content)[1] & 0x80) == 128;
        int opCode = ((byte[])head.Content)[0] & 0xF;
        byte[] mask = null;
        int length = ((byte[])head.Content)[1] & 0x7F;
        if (length == 126) {
            extended = this.Receive(socket, 2, 5000);
            if (!extended.IsSuccess) {
                return OperateResultExTwo.CreateFailedResult(extended);
            }
            Utilities.bytesReverse((byte[])extended.Content);
            length = Utilities.getUShort((byte[])extended.Content, 0);
        } else if (length == 127) {
            extended = this.Receive(socket, 8, 5000);
            if (!extended.IsSuccess) {
                return OperateResultExTwo.CreateFailedResult(extended);
            }
            Utilities.bytesReverse((byte[])extended.Content);
            length = (int)Utilities.getLong((byte[])extended.Content, 0);
        }
        if (hasMask) {
            OperateResultExOne<byte[]> maskResult = this.Receive(socket, 4, 5000);
            if (!maskResult.IsSuccess) {
                return OperateResultExTwo.CreateFailedResult(maskResult);
            }
            mask = (byte[])maskResult.Content;
        }
        OperateResultExOne<byte[]> payload = this.Receive(socket, length);
        if (!payload.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(payload);
        }
        if (hasMask) {
            for (int i = 0; i < ((byte[])payload.Content).length; ++i) {
                ((byte[])payload.Content)[i] = (byte)(((byte[])payload.Content)[i] ^ mask[i % 4]);
            }
        }
        WebSocketMessage message = new WebSocketMessage();
        message.HasMask = hasMask;
        message.OpCode = opCode;
        message.Payload = (byte[])payload.Content;
        return OperateResultExTwo.CreateSuccessResult(message, isEof);
    }

    private OperateResultExOne<Integer> ReceiveMqttRemainingLength(Socket socket) {
        OperateResultExOne<byte[]> read;
        ArrayList<Byte> buffer = new ArrayList<Byte>();
        do {
            read = this.Receive(socket, 1, 5000);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            buffer.add(((byte[])read.Content)[0]);
        } while (((byte[])read.Content)[0] < 0 && buffer.size() < 4);
        if (buffer.size() > 4) {
            return new OperateResultExOne<Integer>("Receive Length is too long!");
        }
        if (buffer.size() == 1) {
            return OperateResultExOne.CreateSuccessResult(Integer.valueOf(((Byte)buffer.get(0)).byteValue()));
        }
        if (buffer.size() == 2) {
            return OperateResultExOne.CreateSuccessResult((Byte)buffer.get(0) + 128 + (Byte)buffer.get(1) * 128);
        }
        if (buffer.size() == 3) {
            return OperateResultExOne.CreateSuccessResult((Byte)buffer.get(0) + 128 + ((Byte)buffer.get(1) + 128) * 128 + (Byte)buffer.get(2) * 128 * 128);
        }
        return OperateResultExOne.CreateSuccessResult((Byte)buffer.get(0) + 128 + ((Byte)buffer.get(1) + 128) * 128 + ((Byte)buffer.get(2) + 128) * 128 * 128 + (Byte)buffer.get(3) * 128 * 128 * 128);
    }

    protected OperateResultExTwo<Byte, byte[]> ReceiveMqttMessage(Socket socket, int timeOut, ActionOperateExTwo<Long, Long> reportProgress) {
        OperateResultExOne<byte[]> readCode = this.Receive(socket, 1, timeOut);
        if (!readCode.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(readCode);
        }
        OperateResultExOne<Integer> readContentLength = this.ReceiveMqttRemainingLength(socket);
        if (!readContentLength.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(readContentLength);
        }
        if ((((byte[])readCode.Content)[0] & 0xF0) >> 4 == 15) {
            reportProgress = null;
        }
        if ((((byte[])readCode.Content)[0] & 0xF0) >> 4 == 0) {
            reportProgress = null;
        }
        OperateResultExOne<byte[]> readContent = this.Receive(socket, (Integer)readContentLength.Content, 60000, reportProgress);
        if (!readContent.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(readContent);
        }
        return OperateResultExTwo.CreateSuccessResult(((byte[])readCode.Content)[0], readContent.Content);
    }

    protected OperateResultExOne<byte[]> ReceiveRedisCommandString(Socket socket, int length) {
        ArrayList<Byte> bufferArray = new ArrayList<Byte>();
        OperateResultExOne<byte[]> receive = this.Receive(socket, length);
        if (!receive.IsSuccess) {
            return receive;
        }
        Utilities.ArrayListAddArray(bufferArray, (byte[])receive.Content);
        OperateResultExOne<byte[]> commandTail = this.ReceiveCommandLineFromSocket(socket, (byte)10);
        if (!commandTail.IsSuccess) {
            return commandTail;
        }
        Utilities.ArrayListAddArray(bufferArray, (byte[])commandTail.Content);
        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bufferArray));
    }

    protected OperateResultExOne<byte[]> ReceiveRedisCommand(Socket socket) {
        ArrayList<Byte> bufferArray = new ArrayList<Byte>();
        OperateResultExOne<byte[]> readCommandLine = this.ReceiveCommandLineFromSocket(socket, (byte)10);
        if (!readCommandLine.IsSuccess) {
            return readCommandLine;
        }
        Utilities.ArrayListAddArray(bufferArray, (byte[])readCommandLine.Content);
        if (((byte[])readCommandLine.Content)[0] == 43 || ((byte[])readCommandLine.Content)[0] == 45 || ((byte[])readCommandLine.Content)[0] == 58) {
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bufferArray));
        }
        if (((byte[])readCommandLine.Content)[0] == 36) {
            OperateResultExOne<Integer> lengthResult = RedisHelper.GetNumberFromCommandLine((byte[])readCommandLine.Content);
            if (!lengthResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(lengthResult);
            }
            if ((Integer)lengthResult.Content < 0) {
                return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bufferArray));
            }
            OperateResultExOne<byte[]> receiveContent = this.ReceiveRedisCommandString(socket, (Integer)lengthResult.Content);
            if (!receiveContent.IsSuccess) {
                return receiveContent;
            }
            Utilities.ArrayListAddArray(bufferArray, (byte[])receiveContent.Content);
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bufferArray));
        }
        if (((byte[])readCommandLine.Content)[0] == 42) {
            OperateResultExOne<Integer> lengthResult = RedisHelper.GetNumberFromCommandLine((byte[])readCommandLine.Content);
            if (!lengthResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(lengthResult);
            }
            for (int i = 0; i < (Integer)lengthResult.Content; ++i) {
                OperateResultExOne<byte[]> receiveCommand = this.ReceiveRedisCommand(socket);
                if (!receiveCommand.IsSuccess) {
                    return receiveCommand;
                }
                Utilities.ArrayListAddArray(bufferArray, (byte[])receiveCommand.Content);
            }
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bufferArray));
        }
        return new OperateResultExOne<byte[]>("Not Supported HeadCode: " + ((byte[])readCommandLine.Content)[0]);
    }

    protected OperateResultExThree<Integer, Integer, byte[]> ReceiveHslMessage(Socket socket) {
        OperateResultExOne<byte[]> receiveHead = this.Receive(socket, 32, 10000);
        if (!receiveHead.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(receiveHead);
        }
        int receive_length = Utilities.getInt((byte[])receiveHead.Content, ((byte[])receiveHead.Content).length - 4);
        OperateResultExOne<byte[]> receiveContent = this.Receive(socket, receive_length);
        if (!receiveContent.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(receiveContent);
        }
        byte[] Content = HslProtocol.CommandAnalysis((byte[])receiveHead.Content, (byte[])receiveContent.Content);
        int protocol = Utilities.getInt((byte[])receiveHead.Content, 0);
        int customer = Utilities.getInt((byte[])receiveHead.Content, 4);
        return OperateResultExThree.CreateSuccessResult(protocol, customer, Content);
    }

    public String toString() {
        return "NetworkBase";
    }
}

