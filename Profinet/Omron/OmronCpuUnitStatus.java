/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.Encoding;

public class OmronCpuUnitStatus {
    public String Status = "";
    public String BatteryStatus = "";
    public String CpuStatus = "";
    public String Mode = "";
    public int ErrorCode = 0;
    public String ErrorMessage = "";

    public OmronCpuUnitStatus() {
    }

    public OmronCpuUnitStatus(byte[] data) {
        this.Status = SoftBasic.BoolOnByteIndex(data[0], 0) ? "Run" : "Stop";
        this.BatteryStatus = SoftBasic.BoolOnByteIndex(data[0], 2) ? "Present" : "No";
        String string = this.CpuStatus = SoftBasic.BoolOnByteIndex(data[0], 7) ? "Standby" : "Normal";
        this.Mode = data[1] == 0 ? "PROGRAM" : (data[1] == 2 ? "MONITOR" : (data[1] == 4 ? "RUN" : ""));
        this.ErrorCode = data[8] * 256 + data[9];
        if (this.ErrorCode > 0) {
            this.ErrorMessage = Encoding.ASCII.GetString(data, 10, 16).trim();
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Status: " + this.Status + "\r\n");
        stringBuilder.append("BatteryStatus: " + this.BatteryStatus + "\r\n");
        stringBuilder.append("CpuStatus: " + this.CpuStatus + "\r\n");
        stringBuilder.append("ErrorCode: " + this.ErrorCode + "\r\n");
        stringBuilder.append("ErrorMessage: " + this.ErrorMessage + "\r\n");
        return stringBuilder.toString();
    }
}

