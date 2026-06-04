/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Enthernet.PushNet;

import HslCommunication.Core.Net.NetworkBase.NetworkXBase;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Types.ActionOperateExTwo;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Utilities;
import java.net.Socket;

public class NetPushClient
extends NetworkXBase {
    private String ipAddress = "";
    private int port = 1000;
    private String keyWord = "";
    private ActionOperateExTwo<NetPushClient, String> action;

    public NetPushClient(String ipAddress, int port, String key) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.keyWord = key;
        if (key == null || key.isEmpty()) {
            throw new RuntimeException("key \u4e0d\u5141\u8bb8\u4e3a\u7a7a");
        }
    }

    @Override
    protected void DataProcessingCenter(AppSession session, int protocol, int customer, byte[] content) {
        if (protocol == 1001 && this.action != null) {
            this.action.Action(this, Utilities.byte2CSharpString(content));
        }
    }

    @Override
    protected void SocketReceiveException(AppSession session) {
        block4: {
            block3: {
                do {
                    System.out.println("10 \u79d2\u949f\u540e\u5c1d\u8bd5\u91cd\u8fde\u670d\u52a1\u5668");
                    try {
                        Thread.sleep(10000L);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (this.action == null) break block3;
                } while (!this.CreatePush().IsSuccess);
                System.out.println("\u91cd\u8fde\u670d\u52a1\u5668\u6210\u529f");
                break block4;
            }
            this.thread.interrupt();
            System.out.println("\u9000\u51fa\u670d\u52a1\u5668\u3002");
        }
    }

    private OperateResult CreatePush() {
        this.CloseSocket(this.CoreSocket);
        OperateResultExOne<Socket> connect = this.CreateSocketAndConnect(this.ipAddress, this.port, 5000);
        if (!connect.IsSuccess) {
            return connect;
        }
        OperateResult send = this.SendStringAndCheckReceive((Socket)connect.Content, 0, this.keyWord);
        if (!send.IsSuccess) {
            return send;
        }
        OperateResultExTwo<Integer, String> receive = this.ReceiveStringContentFromSocket((Socket)connect.Content);
        if (!receive.IsSuccess) {
            return receive;
        }
        if ((Integer)receive.Content1 != 0) {
            OperateResult result = new OperateResult();
            result.Message = (String)receive.Content2;
            return result;
        }
        AppSession appSession = new AppSession();
        this.CoreSocket = (Socket)connect.Content;
        appSession.setWorkSocket((Socket)connect.Content);
        this.BeginReceiveBackground(appSession);
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult CreatePush(ActionOperateExTwo<NetPushClient, String> pushCallBack) {
        this.action = pushCallBack;
        return this.CreatePush();
    }

    public void ClosePush() {
        this.action = null;
        if (this.CoreSocket != null && this.CoreSocket.isConnected()) {
            this.Send(this.CoreSocket, Utilities.getBytes(100));
        }
        this.CloseSocket(this.CoreSocket);
    }

    public String getKeyWord() {
        return this.keyWord;
    }

    @Override
    public String toString() {
        return "NetPushClient";
    }
}

