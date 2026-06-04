/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Beckhoff.Helper;

public class AdsTagItem {
    public String TagName = "";
    public byte[] Buffer = null;
    public int Location = 0;

    public AdsTagItem(String name, byte[] buffer) {
        this.TagName = name;
        this.Buffer = buffer;
    }
}

