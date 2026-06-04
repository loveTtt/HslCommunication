/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;

public class CNCFanucSeriesMessage
extends NetMessageBase
implements INetMessage {
    @Override
    public int ProtocolHeadBytesLength() {
        return 10;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        byte[] HeadBytes = this.getHeadBytes();
        byte[] SendBytes = this.getSendBytes();
        return (HeadBytes[8] & 0xFF) * 256 + (HeadBytes[9] & 0xFF);
    }

    @Override
    public boolean CheckHeadBytesLegal(byte[] token) {
        return true;
    }
}

