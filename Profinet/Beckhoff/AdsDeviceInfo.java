/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Beckhoff;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Encoding;

public class AdsDeviceInfo {
    public int Major = 0;
    public int Minor = 0;
    public int Build = 0;
    public String DeviceName = "";

    public AdsDeviceInfo() {
    }

    public AdsDeviceInfo(byte[] data) {
        this.Major = data[0] & 0xFF;
        this.Minor = data[1] & 0xFF;
        this.Build = BitConverter.ToUInt16(data, 2);
        this.DeviceName = Encoding.ASCII.GetString(SoftBasic.BytesArrayRemoveBegin(data, 4)).trim();
    }
}

