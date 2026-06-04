/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import java.io.ByteArrayOutputStream;

public class MemoryStream {
    private ByteArrayOutputStream ms = new ByteArrayOutputStream();

    public void WriteByte(int value) {
        this.ms.write(value);
    }

    public void Write(byte[] buffer, int offset, int count) {
        this.ms.write(buffer, offset, count);
    }

    public void Write(byte[] buffer) {
        if (buffer != null) {
            this.Write(buffer, 0, buffer.length);
        }
    }

    public byte[] ToArray() {
        return this.ms.toByteArray();
    }

    public int getLength() {
        return this.ms.size();
    }
}

