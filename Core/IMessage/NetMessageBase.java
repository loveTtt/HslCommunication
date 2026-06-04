/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.IMessage;

import HslCommunication.Core.Types.MemoryStream;

public class NetMessageBase {
    private byte[] HeadBytes = null;
    private byte[] ContentBytes = null;
    private byte[] SendBytes = null;

    public boolean CheckHeadBytesLegal(byte[] token) {
        return true;
    }

    public int GetHeadBytesIdentity() {
        return 0;
    }

    public int PependedUselesByteLength(byte[] headByte) {
        return 0;
    }

    public byte[] getHeadBytes() {
        return this.HeadBytes;
    }

    public byte[] getContentBytes() {
        return this.ContentBytes;
    }

    public byte[] getSendBytes() {
        return this.SendBytes;
    }

    public void setHeadBytes(byte[] headBytes) {
        this.HeadBytes = headBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        this.ContentBytes = contentBytes;
    }

    public void setSendBytes(byte[] sendBytes) {
        this.SendBytes = sendBytes;
    }

    public boolean CheckReceiveDataComplete(byte[] send, MemoryStream ms) {
        return true;
    }

    public int CheckMessageMatch(byte[] send, byte[] receive) {
        return 1;
    }

    public String toString() {
        return "NetMessageBase";
    }
}

