/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.Core.Types.List;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Encoding {
    public static Encoding ASCII = new Encoding();
    public static Encoding UTF8 = new Encoding(){

        @Override
        public Charset getCharSet() {
            return StandardCharsets.UTF_8;
        }
    };

    public String GetString(byte[] buffer, int index, int length) {
        return new String(buffer, index, length, this.getCharSet());
    }

    public String GetString(byte[] buffer) {
        return new String(buffer, this.getCharSet());
    }

    public byte[] GetBytes(String value) {
        if (value == null) {
            return null;
        }
        return value.getBytes(this.getCharSet());
    }

    public List<Byte> GetBytesList(String value) {
        if (value == null) {
            return null;
        }
        byte[] buffer = value.getBytes(this.getCharSet());
        List<Byte> list = new List<Byte>();
        for (int i = 0; i < buffer.length; ++i) {
            list.Add(buffer[i]);
        }
        return list;
    }

    public Charset getCharSet() {
        return StandardCharsets.US_ASCII;
    }
}

