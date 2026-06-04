/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net;

import HslCommunication.Utilities;

public final class NetHandle {
    private int m_CodeValue;
    private byte m_CodeMajor;
    private byte m_CodeMinor;
    private short m_CodeIdentifier;

    public NetHandle(int value) {
        byte[] buffer = Utilities.getBytes(value);
        this.m_CodeMajor = buffer[3];
        this.m_CodeMinor = buffer[2];
        this.m_CodeIdentifier = Utilities.getShort(buffer, 0);
        this.m_CodeValue = value;
    }

    public NetHandle(int major, int minor, int identifier) {
        this.m_CodeValue = 0;
        byte[] buffer_major = Utilities.getBytes(major);
        byte[] buffer_minor = Utilities.getBytes(minor);
        byte[] buffer_identifier = Utilities.getBytes(identifier);
        this.m_CodeMajor = buffer_major[0];
        this.m_CodeMinor = buffer_minor[0];
        this.m_CodeIdentifier = Utilities.getShort(buffer_identifier, 0);
        byte[] buffer = new byte[4];
        buffer[3] = this.m_CodeMajor;
        buffer[2] = this.m_CodeMinor;
        buffer[1] = buffer_identifier[1];
        buffer[0] = buffer_identifier[0];
        this.m_CodeValue = Utilities.getInt(buffer, 0);
    }

    public int get_CodeValue() {
        return this.m_CodeValue;
    }

    public byte get_CodeMajor() {
        return this.m_CodeMajor;
    }

    public byte get_CodeMinor() {
        return this.m_CodeMinor;
    }

    public short get_CodeIdentifier() {
        return this.m_CodeIdentifier;
    }
}

