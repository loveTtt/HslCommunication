/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

import java.nio.charset.StandardCharsets;

public class FanucSysInfo {
    public String TypeCode = "";
    public String CncType = "";
    public String MtType = "";
    public String Series = "";
    public String Version = "";
    public int Axes = 0;

    public FanucSysInfo() {
    }

    public FanucSysInfo(byte[] buffer) {
        switch (this.TypeCode = new String(buffer, 32, 2, StandardCharsets.US_ASCII)) {
            case "15": {
                this.CncType = "Series 15/15i";
                break;
            }
            case "16": {
                this.CncType = "Series 16/16i";
                break;
            }
            case "18": {
                this.CncType = "Series 18/18i";
                break;
            }
            case "21": {
                this.CncType = "Series 21/210i";
                break;
            }
            case "30": {
                this.CncType = "Series 30i";
                break;
            }
            case "31": {
                this.CncType = "Series 31i";
                break;
            }
            case "32": {
                this.CncType = "Series 32i";
                break;
            }
            case " 0": {
                this.CncType = "Series 0i";
                break;
            }
            case "PD": {
                this.CncType = "Power Mate i-D";
                break;
            }
            case "PH": {
                this.CncType = "Power Mate i-H";
            }
        }
        this.CncType = this.CncType + "-";
        switch (new String(buffer, 34, 2, StandardCharsets.US_ASCII)) {
            case " M": {
                this.MtType = "Machining center";
                break;
            }
            case " T": {
                this.MtType = "Lathe";
                break;
            }
            case "MM": {
                this.MtType = "M series with 2 path control";
                break;
            }
            case "TT": {
                this.MtType = "T series with 2/3 path control";
                break;
            }
            case "MT": {
                this.MtType = "T series with compound machining function";
                break;
            }
            case " P": {
                this.MtType = "Punch press";
                break;
            }
            case " L": {
                this.MtType = "Laser";
                break;
            }
            case " W": {
                this.MtType = "Wire cut";
            }
        }
        this.CncType = this.CncType + new String(buffer, 34, 2, StandardCharsets.US_ASCII).trim();
        switch (buffer[28]) {
            case 1: {
                this.CncType = this.CncType + "A";
                break;
            }
            case 2: {
                this.CncType = this.CncType + "B";
                break;
            }
            case 3: {
                this.CncType = this.CncType + "C";
                break;
            }
            case 4: {
                this.CncType = this.CncType + "D";
                break;
            }
            case 6: {
                this.CncType = this.CncType + "F";
            }
        }
        this.Series = new String(buffer, 36, 4, StandardCharsets.US_ASCII);
        this.Version = new String(buffer, 40, 4, StandardCharsets.US_ASCII);
        this.Axes = Integer.parseInt(new String(buffer, 44, 2, StandardCharsets.US_ASCII));
    }

    public String toString() {
        return "TypeCode: " + this.TypeCode + "\r\nCncType: " + this.CncType + "\r\nMtType: " + this.MtType + "\r\nSeries: " + this.Series + "\r\nVersion: " + this.Version;
    }
}

