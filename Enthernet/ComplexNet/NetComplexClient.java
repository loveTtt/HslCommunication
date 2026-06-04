/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Enthernet.ComplexNet;

import HslCommunication.Core.Net.HslProtocol;
import HslCommunication.Core.Net.NetHandle;
import HslCommunication.Core.Net.NetworkBase.NetworkXBase;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Types.ActionOperate;
import HslCommunication.Core.Types.ActionOperateExOne;
import HslCommunication.Core.Types.ActionOperateExThree;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Utilities;
import java.net.Socket;
import java.util.Date;

public class NetComplexClient
extends NetworkXBase {
    private AppSession session;
    private int isConnecting = 0;
    private boolean IsQuit = false;
    private Thread thread_heart_check = null;
    private String ipAddress = "";
    private int port = 1000;
    private boolean IsClientStart = false;
    private int ConnectFailedCount = 0;
    private String ClientAlias = "";
    private Date ServerTime = new Date();
    private int DelayTime = 0;
    public ActionOperate LoginSuccess;
    public ActionOperateExOne<Integer> LoginFailed;
    public ActionOperateExOne<String> MessageAlerts;
    public ActionOperate BeforReConnected;
    public ActionOperateExThree<NetComplexClient, NetHandle, String> AcceptString;
    public ActionOperateExThree<NetComplexClient, NetHandle, byte[]> AcceptByte;

    public NetComplexClient() {
        this.session = new AppSession();
        this.ServerTime = new Date();
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getIsClientStart() {
        return this.IsClientStart;
    }

    public void setClientStart(boolean clientStart) {
        this.IsClientStart = clientStart;
    }

    public int getConnectFailedCount() {
        return this.ConnectFailedCount;
    }

    public String getClientAlias() {
        return this.ClientAlias;
    }

    public void setClientAlias(String clientAlias) {
        this.ClientAlias = clientAlias;
    }

    public int getDelayTime() {
        return this.DelayTime;
    }

    public void ClientClose() {
        this.IsQuit = true;
        if (this.IsClientStart) {
            this.SendBytes(this.session, HslProtocol.CommandBytes(2, 0, this.Token, null));
        }
        this.IsClientStart = false;
        this.thread_heart_check = null;
        this.LoginSuccess = null;
        this.LoginFailed = null;
        this.MessageAlerts = null;
        this.AcceptByte = null;
        this.AcceptString = null;
        try {
            this.session.getWorkSocket().close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (this.LogNet != null) {
            this.LogNet.WriteDebug(this.toString(), "Client Close.");
        }
    }

    public void ClientStart() {
        if (this.isConnecting != 0) {
            return;
        }
        this.isConnecting = 1;
        new Thread(){

            @Override
            public void run() {
                NetComplexClient.this.ThreadLogin();
            }
        }.start();
        if (this.thread_heart_check == null) {
            this.thread_heart_check = new Thread(){

                @Override
                public void run() {
                    NetComplexClient.this.ThreadHeartCheck();
                }
            };
            this.thread_heart_check.start();
        }
    }

    private void AwaitToConnect() {
        if (this.ConnectFailedCount == 0) {
            if (this.MessageAlerts != null) {
                this.MessageAlerts.Action("\u6b63\u5728\u8fde\u63a5\u670d\u52a1\u5668...");
            }
        } else {
            int count = 10;
            while (count > 0) {
                if (this.IsQuit) {
                    return;
                }
                --count;
                if (this.MessageAlerts != null) {
                    this.MessageAlerts.Action("\u8fde\u63a5\u65ad\u5f00\uff0c\u7b49\u5f85" + count + "\u79d2\u540e\u91cd\u65b0\u8fde\u63a5");
                }
                try {
                    Thread.sleep(1000L);
                }
                catch (Exception exception) {}
            }
            if (this.MessageAlerts != null) {
                this.MessageAlerts.Action("\u6b63\u5728\u5c1d\u8bd5\u7b2c" + this.ConnectFailedCount + "\u6b21\u8fde\u63a5\u670d\u52a1\u5668...");
            }
        }
    }

    private void ConnectFailed() {
        ++this.ConnectFailedCount;
        this.isConnecting = 0;
        if (this.LoginFailed != null) {
            this.LoginFailed.Action(this.ConnectFailedCount);
        }
        if (this.LogNet != null) {
            this.LogNet.WriteDebug(this.toString(), "Connected Failed, Times: " + this.ConnectFailedCount);
        }
    }

    private OperateResultExOne<Socket> ConnectServer() {
        OperateResultExOne<Socket> connectResult = this.CreateSocketAndConnect(this.ipAddress, this.port, 10000);
        if (!connectResult.IsSuccess) {
            return connectResult;
        }
        OperateResult sendResult = this.SendStringAndCheckReceive((Socket)connectResult.Content, 2, this.ClientAlias);
        if (!sendResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(sendResult);
        }
        if (this.MessageAlerts != null) {
            this.MessageAlerts.Action("\u8fde\u63a5\u670d\u52a1\u5668\u6210\u529f\uff01");
        }
        return connectResult;
    }

    private void LoginSuccessMethod(Socket socket) {
        block2: {
            this.ConnectFailedCount = 0;
            try {
                this.session.setIpEndPoint(socket.getInetAddress());
                this.session.setLoginAlias(this.ClientAlias);
                this.session.setWorkSocket(socket);
                this.session.setHeartTime(new Date());
                this.IsClientStart = true;
                this.BeginReceiveBackground(this.session);
            }
            catch (Exception ex) {
                if (this.LogNet == null) break block2;
                this.LogNet.WriteException(this.toString(), ex);
            }
        }
    }

    private void ThreadLogin() {
        this.AwaitToConnect();
        OperateResultExOne<Socket> connectResult = this.ConnectServer();
        if (!connectResult.IsSuccess) {
            this.ConnectFailed();
            new Thread(){

                @Override
                public void run() {
                    NetComplexClient.this.ReconnectServer(null);
                }
            }.start();
            return;
        }
        this.LoginSuccessMethod((Socket)connectResult.Content);
        if (this.LoginSuccess != null) {
            this.LoginSuccess.Action();
        }
        this.isConnecting = 0;
        try {
            Thread.sleep(200L);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void ReconnectServer(Object obj) {
        if (this.isConnecting == 1) {
            return;
        }
        if (this.IsQuit) {
            return;
        }
        if (this.BeforReConnected != null) {
            this.BeforReConnected.Action();
        }
        if (this.session != null) {
            this.CloseSocket(this.session.getWorkSocket());
        }
        this.ClientStart();
    }

    @Override
    protected void SocketReceiveException(AppSession receive) {
        if (this.LogNet != null) {
            this.LogNet.WriteDebug(this.toString(), "Socket Excepiton Occured.");
        }
        this.ReconnectServer(null);
    }

    public void Send(NetHandle customer, String str) {
        if (this.IsClientStart) {
            this.SendBytes(this.session, HslProtocol.CommandBytes(customer.get_CodeValue(), this.Token, str));
        }
    }

    public void Send(NetHandle customer, byte[] bytes) {
        if (this.IsClientStart) {
            this.SendBytes(this.session, HslProtocol.CommandBytes(customer.get_CodeValue(), this.Token, bytes));
        }
    }

    private void SendBytes(AppSession stateone, byte[] content) {
        super.Send(stateone.getWorkSocket(), content);
    }

    @Override
    protected void DataProcessingCenter(AppSession session, int protocol, int customer, byte[] content) {
        if (protocol == 1) {
            Date dt = new Date(Utilities.getLong(content, 0));
            this.ServerTime = new Date(Utilities.getLong(content, 8));
            this.DelayTime = (int)(new Date().getTime() - dt.getTime());
            this.session.setHeartTime(new Date());
        } else if (protocol != 2) {
            if (protocol == 1002) {
                if (this.AcceptByte != null) {
                    this.AcceptByte.Action(this, new NetHandle(customer), content);
                }
            } else if (protocol == 1001) {
                String str = Utilities.byte2CSharpString(content);
                if (this.AcceptString != null) {
                    this.AcceptString.Action(this, new NetHandle(customer), str);
                }
            }
        }
    }

    private void ThreadHeartCheck() {
        try {
            Thread.sleep(2000L);
        }
        catch (Exception exception) {
            // empty catch block
        }
        while (true) {
            try {
                Thread.sleep(1000L);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (this.IsQuit) break;
            byte[] send = new byte[16];
            byte[] buffer = Utilities.getBytes(new Date().getTime());
            System.arraycopy(buffer, 0, send, 0, buffer.length);
            try {
                this.SendBytes(this.session, HslProtocol.CommandBytes(1, 0, this.Token, send));
                double timeSpan = (new Date().getTime() - this.session.getHeartTime().getTime()) / 1000L;
                if (!(timeSpan > 8.0)) continue;
                if (this.isConnecting == 0) {
                    if (this.LogNet != null) {
                        this.LogNet.WriteDebug(this.toString(), "Heart Check Failed int " + timeSpan + " Seconds.");
                    }
                    new Thread(){

                        @Override
                        public void run() {
                            NetComplexClient.this.ReconnectServer(null);
                        }
                    }.start();
                }
                if (this.IsQuit) continue;
                try {
                    Thread.sleep(1000L);
                }
                catch (Exception exception) {
                }
            }
            catch (Exception ex) {
                System.out.println(ex.getStackTrace());
            }
        }
    }

    @Override
    public String toString() {
        return "NetComplexClient";
    }
}

