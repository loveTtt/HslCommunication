/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Instrument.DLT.Helper.DLT645Helper;

public class DLT645Message
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 10;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        return (this.getHeadBytes()[9] & 0xFF) + 2;
    }

    @Override
    public int PependedUselesByteLength(byte[] headByte) {
        int index = DLT645Helper.FindHeadCode68H(headByte);
        if (index < 0) {
            return 10;
        }
        return index;
    }

    @Override
    public boolean CheckReceiveDataComplete(byte[] send, MemoryStream ms) {
        return DLT645Helper.CheckReceiveDataComplete(ms);
    }
}

