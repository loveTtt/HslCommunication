/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net;

import HslCommunication.Core.Types.ActionOperateExTwo;
import HslCommunication.Core.Types.RemoteCloseException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class NetSupport {
    public static final int SocketBufferSize = 8192;

    public static byte[] ReadBytesFromSocket(Socket socket, int receive, ActionOperateExTwo<Long, Long> reportProgress) throws IOException, RemoteCloseException {
        int count;
        byte[] bytes_receive = new byte[receive];
        for (int count_receive = 0; count_receive < receive; count_receive += count) {
            int receive_length = Math.min(receive - count_receive, 8192);
            InputStream input = socket.getInputStream();
            count = input.read(bytes_receive, count_receive, receive_length);
            if (count < 0) {
                throw new RemoteCloseException();
            }
            if (count > 0) continue;
            throw new RemoteCloseException();
        }
        return bytes_receive;
    }

    public static void CloseSocket(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

