/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.Core.Types.Encoding;

public class OmronCpuUnitData {
    public String Model = "";
    public String Version = "";
    public int LargestEMNumber = 0;
    public int ProgramAreaSize = 0;
    public int IOMSize = 0;
    public int DMSize = 0;
    public int EMSize = 0;
    public int TCSize = 0;

    public OmronCpuUnitData() {
    }

    public OmronCpuUnitData(byte[] data) {
        this.Model = Encoding.ASCII.GetString(data, 0, 20).trim();
        this.Version = Encoding.ASCII.GetString(data, 20, 10).trim();
        this.LargestEMNumber = data[41] & 0xFF;
        this.ProgramAreaSize = (data[80] & 0xFF) * 256 + data[81];
        this.IOMSize = (data[82] & 0xFF) * 1024;
        this.DMSize = (data[83] & 0xFF) * 256 + data[84];
        this.TCSize = (data[85] & 0xFF) * 1024;
        this.EMSize = data[86] & 0xFF;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Model: " + this.Model + "\r\n");
        stringBuilder.append("Version: " + this.Version + "\r\n");
        stringBuilder.append("LargestEMNumber: " + this.LargestEMNumber + "\r\n");
        stringBuilder.append("ProgramAreaSize: " + this.ProgramAreaSize + "\r\n");
        stringBuilder.append("IOMSize: " + this.IOMSize + "\r\n");
        stringBuilder.append("DMSize: " + this.DMSize + "\r\n");
        stringBuilder.append("TCSize: " + this.TCSize + "\r\n");
        stringBuilder.append("EMSize: " + this.EMSize);
        return stringBuilder.toString();
    }
}

