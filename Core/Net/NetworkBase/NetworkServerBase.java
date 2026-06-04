/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.Core.Net.NetworkBase.NetworkBase;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.StringResources;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServerBase
extends NetworkBase {
    private boolean IsStarted = false;
    private int Port = 0;
    private ServerSocket serverSocket;

    public boolean isStarted() {
        return this.IsStarted;
    }

    protected void setStarted(boolean started) {
        this.IsStarted = started;
    }

    public int getPort() {
        return this.Port;
    }

    public void setPort(int port) {
        this.Port = port;
    }

    private void ThreadPoolLogin(Socket socket) {
        OperateResult check = this.SocketAcceptExtraCheck(socket, socket.getInetAddress());
        if (!check.IsSuccess) {
            if (this.LogNet != null) {
                this.LogNet.WriteDebug(this.toString(), "[" + socket.getInetAddress() + "] Socket Accept Extra Check Failed : {check.Message}");
            }
            this.CloseSocket(socket);
        } else {
            this.ThreadPoolLogin(socket, socket.getInetAddress());
        }
    }

    protected void ThreadPoolLogin(Socket socket, InetAddress endPoint) {
        this.CloseSocket(socket);
    }

    protected OperateResult SocketAcceptExtraCheck(Socket socket, InetAddress endPoint) {
        return OperateResult.CreateSuccessResult();
    }

    protected void StartInitialization() {
    }

    public void ServerStart(int port) throws IOException {
        if (!this.IsStarted) {
            this.StartInitialization();
            this.serverSocket = new ServerSocket(port);
            new Thread(this::MethodThreadAccept).start();
            this.IsStarted = true;
            this.Port = port;
            if (this.LogNet != null) {
                this.LogNet.WriteInfo(this.toString(), StringResources.Language.NetEngineStart());
            }
        }
    }

    private void MethodThreadAccept() {
        while (true) {
            Socket sock = null;
            try {
                sock = this.serverSocket.accept();
            }
            catch (IOException e) {
                if (!this.IsStarted) {
                    return;
                }
                e.printStackTrace();
            }
            if (sock == null) continue;
            new Thread(new MultiThreadServer(sock)).start();
        }
    }

    public void ServerStart() throws IOException {
        this.ServerStart(this.Port);
    }

    protected void CloseAction() {
    }

    public void ServerClose() {
        if (this.IsStarted) {
            this.IsStarted = false;
            this.CloseAction();
            try {
                this.serverSocket.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (this.LogNet != null) {
                this.LogNet.WriteInfo(this.toString(), StringResources.Language.NetEngineClose());
            }
        }
    }

    @Override
    public String toString() {
        return "NetworkServerBase[" + this.Port + "]";
    }

    private class MultiThreadServer
    implements Runnable {
        private Socket socket;

        MultiThreadServer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            NetworkServerBase.this.ThreadPoolLogin(this.socket);
        }
    }
}

