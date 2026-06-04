/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.NetMessageBase;
import HslCommunication.Utilities;

public class SpecifiedCharacterMessage
extends NetMessageBase
implements INetMessage {
    private int protocolHeadBytesLength = -1;

    public SpecifiedCharacterMessage(int endCode) {
        byte[] buffer = new byte[4];
        buffer[3] = (byte)(buffer[3] | 0x80);
        buffer[3] = (byte)(buffer[3] | 1);
        buffer[1] = (byte) endCode;
        this.protocolHeadBytesLength = Utilities.getInt(buffer, 0);
    }

    public SpecifiedCharacterMessage(int endCode1, int endCode2) {
        byte[] buffer = new byte[4];
        buffer[3] = (byte)(buffer[3] | 0x80);
        buffer[3] = (byte)(buffer[3] | 2);
        buffer[1] = (byte) endCode1;
        buffer[0] = (byte) endCode2;
        this.protocolHeadBytesLength = Utilities.getInt(buffer, 0);
    }

    public byte getEndLength() {
        return Utilities.getBytes(this.protocolHeadBytesLength)[2];
    }

    public void setEndLength(byte value) {
        byte[] buffer = Utilities.getBytes(this.protocolHeadBytesLength);
        buffer[2] = value;
        this.protocolHeadBytesLength = Utilities.getInt(buffer, 0);
    }

    @Override
    public int ProtocolHeadBytesLength() {
        return this.protocolHeadBytesLength;
    }

    @Override
    public int GetContentLengthByHeadBytes() {
        return 0;
    }
}

