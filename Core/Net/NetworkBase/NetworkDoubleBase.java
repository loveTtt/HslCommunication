/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkBase;
import HslCommunication.Core.Net.StateOne.AlienSession;
import HslCommunication.Core.Pipe.PipeSocket;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Types.AutoResetEvent;
import HslCommunication.Core.Types.Environment;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.LogNet.Core.ILogNet;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class NetworkDoubleBase
extends NetworkBase {
    protected PipeSocket pipeSocket = new PipeSocket();
    private IByteTransform byteTransform;
    protected boolean isPersistentConn = false;
    private boolean useServerActivePush = false;
    private AutoResetEvent autoResetEvent;
    private byte[] bufferQA = null;
    private Thread threadBackReceive;
    protected boolean LogMsgFormatBinary = true;
    private boolean isUseSpecifiedSocket = false;
    private String connectionId = SoftBasic.GetUniqueStringByGuidAndRandom();
    private int sleepTime = 0;
    public AlienSession AlienSession = null;
    protected boolean isUseAccountCertificate = false;
    private String userName = "";
    private String password = "";

    protected void setUseServerActivePush(boolean value) {
        if (value) {
            if (this.autoResetEvent == null) {
                this.autoResetEvent = new AutoResetEvent(false);
            }
            this.isPersistentConn = true;
        }
        this.useServerActivePush = value;
    }

    protected boolean DecideWhetherQAMessage(Socket socket, OperateResultExOne<byte[]> receive) {
        return true;
    }

    protected INetMessage GetNewNetMessage() {
        return null;
    }

    public IByteTransform getByteTransform() {
        return this.byteTransform;
    }

    public void setByteTransform(IByteTransform transform) {
        this.byteTransform = transform;
    }

    public int getConnectTimeOut() {
        return this.pipeSocket.getConnectTimeOut();
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.pipeSocket.setConnectTimeOut(connectTimeOut);
    }

    public int getReceiveTimeOut() {
        return this.pipeSocket.getReceiveTimeOut();
    }

    public void setReceiveTimeOut(int receiveTimeOut) {
        this.pipeSocket.setReceiveTimeOut(receiveTimeOut);
    }

    public String getIpAddress() {
        return this.pipeSocket.getIpAddress();
    }

    public void setIpAddress(String ipAddress) {
        if (!ipAddress.isEmpty()) {
            this.pipeSocket.setIpAddress(ipAddress);
        }
    }

    public int getPort() {
        return this.pipeSocket.getPort();
    }

    public void setPort(int port) {
        this.pipeSocket.setPort(port);
    }

    public String getConnectionId() {
        return this.connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public int getSleepTime() {
        return this.sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void SetPipeSocket(PipeSocket pipeSocket) {
        if (this.pipeSocket != null) {
            this.pipeSocket = pipeSocket;
            this.SetPersistentConnection();
        }
    }

    public PipeSocket GetPipeSocket() {
        return this.pipeSocket;
    }

    public void SetPersistentConnection() {
        this.isPersistentConn = true;
    }

    public boolean IpAddressPing() throws IOException {
        return InetAddress.getByName(this.getIpAddress()).isReachable(3000);
    }

    public OperateResult ConnectServer() {
        this.isPersistentConn = true;
        this.CloseSocket(this.pipeSocket.getSocket());
        OperateResultExOne<Socket> rSocket = this.CreateSocketAndInitialication();
        if (!rSocket.IsSuccess) {
            this.pipeSocket.setIsSocketError(true);
            rSocket.Content = null;
        } else {
            this.pipeSocket.setSocket((Socket)rSocket.Content);
            this.pipeSocket.setIsSocketError(false);
            ILogNet logNet = this.LogNet;
            if (logNet != null) {
                logNet.WriteDebug(this.toString(), StringResources.Language.NetEngineStart());
            }
        }
        return rSocket;
    }

    public OperateResult ConnectServer(AlienSession session) {
        this.isPersistentConn = true;
        this.isUseSpecifiedSocket = true;
        if (session != null) {
            if (this.AlienSession != null) {
                this.CloseSocket(this.AlienSession.getSocket());
            }
            if (this.connectionId.isEmpty()) {
                this.connectionId = session.getDTU();
            }
            if (this.connectionId.equals(session.getDTU())) {
                if (session.getIsStatusOk()) {
                    OperateResult ini = this.InitializationOnConnect(session.getSocket());
                    if (ini.IsSuccess) {
                        this.pipeSocket.setSocket(session.getSocket());
                        this.pipeSocket.setIsSocketError(!session.getIsStatusOk());
                        this.AlienSession = session;
                    } else {
                        this.pipeSocket.setIsSocketError(true);
                    }
                    return ini;
                }
                return new OperateResult();
            }
            this.pipeSocket.setIsSocketError(true);
            return new OperateResult();
        }
        this.pipeSocket.setIsSocketError(true);
        return new OperateResult();
    }

    public OperateResult ConnectClose() {
        OperateResult result = new OperateResult();
        this.isPersistentConn = false;
        this.pipeSocket.PipeLockEnter();
        try {
            result = this.pipeSocket.getSocket() != null ? this.ExtraOnDisconnect(this.pipeSocket.getSocket()) : OperateResult.CreateSuccessResult();
            this.CloseSocket(this.pipeSocket.getSocket());
            this.pipeSocket.setSocket(null);
            this.pipeSocket.PipeLockLeave();
        }
        catch (Exception ex) {
            this.pipeSocket.PipeLockLeave();
        }
        if (this.LogNet != null) {
            this.LogNet.WriteDebug(this.toString(), StringResources.Language.NetEngineClose());
        }
        return result;
    }

    protected OperateResult InitializationOnConnect(final Socket socket) {
        if (this.useServerActivePush) {
            this.threadBackReceive = new Thread(new Runnable(){

                @Override
                public void run() {
                    while (true) {
                        OperateResultExOne<byte[]> receive = NetworkDoubleBase.this.ReceiveByMessage(socket, -1, null, NetworkDoubleBase.this.GetNewNetMessage());
                        if (!receive.IsSuccess) {
                            NetworkDoubleBase.this.pipeSocket.setIsSocketError(true);
                            return;
                        }
                        if (NetworkDoubleBase.this.LogNet != null) {
                            NetworkDoubleBase.this.LogNet.WriteDebug(this.toString(), StringResources.Language.Receive() + " : " + (NetworkDoubleBase.this.LogMsgFormatBinary ? SoftBasic.ByteToHexString((byte[])receive.Content, ' ') : SoftBasic.GetAsciiStringRender((byte[])receive.Content)));
                        }
                        if (!NetworkDoubleBase.this.DecideWhetherQAMessage(socket, receive)) continue;
                        NetworkDoubleBase.access$002(NetworkDoubleBase.this, (byte[])receive.Content);
                        NetworkDoubleBase.this.autoResetEvent.set();
                    }
                }
            });
            this.threadBackReceive.start();
        }
        return OperateResult.CreateSuccessResult();
    }

    protected OperateResult ExtraOnDisconnect(Socket socket) {
        return OperateResult.CreateSuccessResult();
    }

    protected void ExtraAfterReadFromCoreServer(OperateResult read) {
    }

    public void SetLoginAccount(String userName, String password) {
        if (!Utilities.IsStringNullOrEmpty(userName)) {
            this.isUseAccountCertificate = true;
            this.userName = userName;
            this.password = password;
        } else {
            this.isUseAccountCertificate = false;
        }
    }

    protected OperateResult AccountCertificate(Socket socket) {
        OperateResult send = this.SendAccountAndCheckReceive(socket, 1, this.userName, this.password);
        if (!send.IsSuccess) {
            return send;
        }
        OperateResultExTwo<Integer, String[]> read = this.ReceiveStringArrayContentFromSocket(socket);
        if (!read.IsSuccess) {
            return read;
        }
        if ((Integer)read.Content1 == 0) {
            return new OperateResult(((String[])read.Content2)[0]);
        }
        return OperateResult.CreateSuccessResult();
    }

    protected byte[] PackCommandWithHeader(byte[] command) {
        return command;
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return OperateResultExOne.CreateSuccessResult(response);
    }

    protected OperateResultExOne<Socket> GetAvailableSocket() {
        if (this.isPersistentConn) {
            if (this.isUseSpecifiedSocket) {
                if (this.pipeSocket.getIsSocketError()) {
                    return new OperateResultExOne<Socket>(StringResources.Language.ConnectionIsNotAvailable());
                }
                return OperateResultExOne.CreateSuccessResult(this.pipeSocket.getSocket());
            }
            if (this.pipeSocket.IsConnectitonError()) {
                OperateResult connect = this.ConnectServer();
                if (!connect.IsSuccess) {
                    this.pipeSocket.setIsSocketError(true);
                    return OperateResultExOne.CreateFailedResult(connect);
                }
                this.pipeSocket.setIsSocketError(false);
                return OperateResultExOne.CreateSuccessResult(this.pipeSocket.getSocket());
            }
            return OperateResultExOne.CreateSuccessResult(this.pipeSocket.getSocket());
        }
        return this.CreateSocketAndInitialication();
    }

    private OperateResultExOne<Socket> CreateSocketAndInitialication() {
        OperateResultExOne<Socket> result = this.CreateSocketAndConnect(this.pipeSocket.GetConnectIPEndPoint(), this.getConnectTimeOut());
        if (result.IsSuccess) {
            OperateResult initi = this.InitializationOnConnect((Socket)result.Content);
            if (!initi.IsSuccess) {
                this.CloseSocket((Socket)result.Content);
                result.IsSuccess = initi.IsSuccess;
                result.CopyErrorFromOther(initi);
            }
        }
        return result;
    }

    public OperateResultExOne<byte[]> ReadFromCoreServer(Socket socket, byte[] send, boolean hasResponseData, boolean usePackAndUnpack) {
        OperateResultExOne<byte[]> resultReceive;
        INetMessage netMessage;
        byte[] sendValue;
        block20: {
            sendValue = usePackAndUnpack ? this.PackCommandWithHeader(send) : send;
            ILogNet logNet = this.LogNet;
            if (logNet != null) {
                logNet.WriteDebug(this.toString(), StringResources.Language.Send() + " : " + (this.LogMsgFormatBinary ? SoftBasic.ByteToHexString(sendValue, ' ') : SoftBasic.GetAsciiStringRender(sendValue)));
            }
            if ((netMessage = this.GetNewNetMessage()) != null) {
                netMessage.setSendBytes(sendValue);
            }
            OperateResult resultSend = this.Send(socket, sendValue);
            if (!resultSend.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(resultSend);
            }
            if (this.getReceiveTimeOut() < 0) {
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
            }
            if (!hasResponseData) {
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
            }
            try {
                if (this.sleepTime > 0) {
                    Thread.sleep(this.sleepTime);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            resultReceive = null;
            Date start = new Date();
            int times = 0;
            do {
                int check;
                if (this.useServerActivePush) {
                    try {
                        boolean waitOne = this.autoResetEvent.waitOne(this.getReceiveTimeOut());
                        if (waitOne) {
                            if (netMessage != null) {
                                netMessage.setHeadBytes(this.bufferQA);
                            }
                        } else {
                            this.CloseSocket(socket);
                            this.pipeSocket.setIsSocketError(true);
                            return new OperateResultExOne<byte[]>(-10000, StringResources.Language.ReceiveDataTimeout() + this.getReceiveTimeOut());
                        }
                        resultReceive = OperateResultExOne.CreateSuccessResult(this.bufferQA);
                    }
                    catch (Exception ex) {
                        return new OperateResultExOne<byte[]>(-10000, "tryAcquire failed: " + ex.getMessage());
                    }
                } else {
                    resultReceive = this.ReceiveByMessage(socket, this.getReceiveTimeOut(), sendValue, netMessage);
                }
                if (!resultReceive.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(resultReceive);
                }
                if (logNet != null) {
                    logNet.WriteDebug(this.toString(), StringResources.Language.Receive() + " : " + (this.LogMsgFormatBinary ? SoftBasic.ByteToHexString((byte[])resultReceive.Content, ' ') : SoftBasic.GetAsciiStringRender((byte[])resultReceive.Content)));
                }
                if (netMessage == null || (check = netMessage.CheckMessageMatch(sendValue, (byte[])resultReceive.Content)) == 1) break block20;
                if (check == 0) {
                    return new OperateResultExOne<byte[]>("INetMessage.CheckMessageMatch failed" + Environment.NewLine + StringResources.Language.Send() + ": " + SoftBasic.ByteToHexString(sendValue, ' ') + Environment.NewLine + StringResources.Language.Receive() + ": " + SoftBasic.ByteToHexString((byte[])resultReceive.Content, ' '));
                }
                ++times;
            } while (this.getReceiveTimeOut() < 0 || new Date().getTime() - start.getTime() <= (long)this.getReceiveTimeOut());
            return new OperateResultExOne<byte[]>("Receive Message timeout: " + this.getReceiveTimeOut() + " CheckMessageMatch times:" + times);
        }
        if (netMessage != null && !netMessage.CheckHeadBytesLegal(Utilities.UUID2Byte(this.Token))) {
            this.CloseSocket(socket);
            return new OperateResultExOne<byte[]>(StringResources.Language.CommandHeadCodeCheckFailed());
        }
        return usePackAndUnpack ? this.UnpackResponseContent(sendValue, (byte[])resultReceive.Content) : resultReceive;
    }

    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send, boolean hasResponseData, boolean usePackAndUnpack) {
        OperateResultExOne<byte[]> result = new OperateResultExOne<byte[]>();
        this.GetPipeSocket().PipeLockEnter();
        OperateResultExOne<Socket> resultSocket = this.GetAvailableSocket();
        if (!resultSocket.IsSuccess) {
            this.pipeSocket.setIsSocketError(true);
            if (this.AlienSession != null) {
                this.AlienSession.setIsStatusOk(false);
            }
            this.pipeSocket.PipeLockLeave();
            result.CopyErrorFromOther(resultSocket);
            return result;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((Socket)resultSocket.Content, send, hasResponseData, usePackAndUnpack);
        if (read.IsSuccess) {
            this.GetPipeSocket().setIsSocketError(false);
            result.IsSuccess = true;
            result.Content = read.Content;
            result.Message = StringResources.Language.SuccessText();
        } else {
            this.GetPipeSocket().setIsSocketError(true);
            if (this.AlienSession != null) {
                this.AlienSession.setIsStatusOk(false);
            }
            result.CopyErrorFromOther(read);
        }
        this.ExtraAfterReadFromCoreServer(read);
        this.GetPipeSocket().PipeLockLeave();
        if (!this.isPersistentConn) {
            this.CloseSocket((Socket)resultSocket.Content);
        }
        return result;
    }

    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send) {
        return this.ReadFromCoreServer(send, true, true);
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

    @Override
    public String toString() {
        if (this.GetNewNetMessage() == null) {
            return "NetworkDoubleBase[" + this.getIpAddress() + ":" + this.getPort() + "]";
        }
        return "NetworkDoubleBase<" + this.GetNewNetMessage().getClass().getTypeName() + ">[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }

    static /* synthetic */ byte[] access$002(NetworkDoubleBase x0, byte[] x1) {
        x0.bufferQA = x1;
        return x1;
    }
}

