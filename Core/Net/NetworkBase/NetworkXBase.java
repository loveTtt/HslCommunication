/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.Core.Net.HslProtocol;
import HslCommunication.Core.Net.NetworkBase.NetworkBase;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Types.ActionOperateExTwo;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.LogNet.Core.ILogNet;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NetworkXBase
extends NetworkBase {
    protected Thread thread;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void SendBytesAsync(AppSession session, byte[] content) {
        if (content == null) {
            return;
        }
        try {
            session.getHybirdLockSend().lock();
            OutputStream outputStream = session.getWorkSocket().getOutputStream();
            outputStream.write(content);
        }
        catch (Exception ex) {
            if (!ex.getMessage().contains(StringResources.Language.SocketRemoteCloseException()) && this.LogNet != null) {
                this.LogNet.WriteException(this.toString(), StringResources.Language.SocketSendException(), ex);
            }
        }
        finally {
            session.getHybirdLockSend().unlock();
        }
    }

    protected void BeginReceiveBackground(final AppSession session) {
        this.thread = new Thread(){

            @Override
            public void run() {
                while (true) {
                    OperateResultExOne<byte[]> readHeadBytes = NetworkXBase.this.Receive(session.getWorkSocket(), 32);
                    if (!readHeadBytes.IsSuccess) {
                        NetworkXBase.this.SocketReceiveException(session);
                        return;
                    }
                    int length = Utilities.getInt((byte[])readHeadBytes.Content, 28);
                    OperateResultExOne<byte[]> readContent = NetworkXBase.this.Receive(session.getWorkSocket(), length);
                    if (!readContent.IsSuccess) {
                        NetworkXBase.this.SocketReceiveException(session);
                        return;
                    }
                    if (NetworkXBase.this.CheckRemoteToken((byte[])readHeadBytes.Content)) {
                        byte[] head = (byte[])readHeadBytes.Content;
                        byte[] content = HslProtocol.CommandAnalysis(head, (byte[])readContent.Content);
                        int protocol = Utilities.getInt(head, 0);
                        int customer = Utilities.getInt(head, 4);
                        NetworkXBase.this.DataProcessingCenter(session, protocol, customer, content);
                        continue;
                    }
                    if (NetworkXBase.this.LogNet != null) {
                        NetworkXBase.this.LogNet.WriteWarn(this.toString(), StringResources.Language.TokenCheckFailed());
                    }
                    NetworkXBase.this.AppSessionRemoteClose(session);
                }
            }
        };
        this.thread.start();
    }

    protected void DataProcessingCenter(AppSession session, int protocol, int customer, byte[] content) {
    }

    protected void SocketReceiveException(AppSession session) {
    }

    protected void AppSessionRemoteClose(AppSession session) {
    }

    protected OperateResult SendFileStreamToSocket(Socket socket, String filename, long fileLength, ActionOperateExTwo<Long, Long> report) {
        try {
            OperateResult result = new OperateResult();
            FileInputStream stream = new FileInputStream(filename);
            result = this.SendStreamToSocket(socket, stream, fileLength, report, true);
            return result;
        }
        catch (Exception ex) {
            this.CloseSocket(socket);
            ILogNet logNet = this.LogNet;
            if (logNet != null) {
                logNet.WriteException(this.toString(), ex);
            }
            return new OperateResult(ex.getMessage());
        }
    }

    protected boolean DeleteFileByName(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return true;
            }
            file.delete();
            return true;
        }
        catch (Exception ex) {
            if (this.LogNet != null) {
                this.LogNet.WriteException(this.toString(), "delete file failed:" + filename, ex);
            }
            return false;
        }
    }

    protected String PreprocessFolderName(String folder) {
        if (folder.endsWith("\\")) {
            return folder.substring(0, folder.length() - 1);
        }
        return folder;
    }

    @Override
    public String toString() {
        return "NetworkXBase";
    }
}

