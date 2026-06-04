/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Core.Types.MemoryStream;

public class RkcTemperatureMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return -1;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        return 0;
    }

    @Override
    public boolean CheckReceiveDataComplete(byte[] send, MemoryStream ms) {
        byte[] buffer = ms.ToArray();
        if (buffer.length == 1 ? buffer[0] == 6 || buffer[0] == 21 : buffer.length == 6 && buffer[0] == 4 && buffer[buffer.length - 1] == 5) {
            return true;
        }
        if (buffer.length > 3 && buffer[0] == 2 && buffer[buffer.length - 2] == 3) {
            return true;
        }
        return buffer.length > 6 && buffer[0] == 4 && buffer[3] == 2 && buffer[buffer.length - 2] == 3;
    }
}

