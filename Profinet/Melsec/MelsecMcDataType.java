/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

public class MelsecMcDataType {
    private short DataCode = 0;
    private byte DataType = 0;
    private String AsciiCode = "";
    private int FromBase = 0;
    public static final MelsecMcDataType X = new MelsecMcDataType(156, 1, "X*", 16);
    public static final MelsecMcDataType Y = new MelsecMcDataType(157, 1, "Y*", 16);
    public static final MelsecMcDataType M = new MelsecMcDataType(144, 1, "M*", 10);
    public static final MelsecMcDataType SM = new MelsecMcDataType(145, 1, "SM", 10);
    public static final MelsecMcDataType S = new MelsecMcDataType(152, 1, "S*", 10);
    public static final MelsecMcDataType L = new MelsecMcDataType(146, 1, "L*", 10);
    public static final MelsecMcDataType F = new MelsecMcDataType(147, 1, "F*", 10);
    public static final MelsecMcDataType V = new MelsecMcDataType(148, 1, "V*", 10);
    public static final MelsecMcDataType B = new MelsecMcDataType(160, 1, "B*", 16);
    public static final MelsecMcDataType SB = new MelsecMcDataType(161, 1, "SB", 16);
    public static final MelsecMcDataType DX = new MelsecMcDataType(162, 1, "DX", 16);
    public static final MelsecMcDataType DY = new MelsecMcDataType(163, 1, "DY", 16);
    public static final MelsecMcDataType D = new MelsecMcDataType(168, 0, "D*", 10);
    public static final MelsecMcDataType SD = new MelsecMcDataType(169, 0, "SD", 10);
    public static final MelsecMcDataType W = new MelsecMcDataType(180, 0, "W*", 16);
    public static final MelsecMcDataType SW = new MelsecMcDataType(181, 0, "SW", 16);
    public static final MelsecMcDataType R = new MelsecMcDataType(175, 0, "R*", 10);
    public static final MelsecMcDataType Z = new MelsecMcDataType(204, 0, "Z*", 10);
    public static final MelsecMcDataType ZR = new MelsecMcDataType(176, 0, "ZR", 10);
    public static final MelsecMcDataType TN = new MelsecMcDataType(194, 0, "TN", 10);
    public static final MelsecMcDataType TS = new MelsecMcDataType(193, 1, "TS", 10);
    public static final MelsecMcDataType TC = new MelsecMcDataType(192, 1, "TC", 10);
    public static final MelsecMcDataType SS = new MelsecMcDataType(199, 1, "SS", 10);
    public static final MelsecMcDataType SC = new MelsecMcDataType(198, 1, "SC", 10);
    public static final MelsecMcDataType SN = new MelsecMcDataType(200, 0, "SN", 10);
    public static final MelsecMcDataType CN = new MelsecMcDataType(197, 0, "CN", 10);
    public static final MelsecMcDataType CS = new MelsecMcDataType(196, 1, "CS", 10);
    public static final MelsecMcDataType CC = new MelsecMcDataType(195, 1, "CC", 10);
    public static final MelsecMcDataType R_X = new MelsecMcDataType(156, 1, "X***", 16);
    public static final MelsecMcDataType R_Y = new MelsecMcDataType(157, 1, "Y***", 16);
    public static final MelsecMcDataType R_M = new MelsecMcDataType(144, 1, "M***", 10);
    public static final MelsecMcDataType R_SM = new MelsecMcDataType(145, 1, "SM**", 10);
    public static final MelsecMcDataType R_L = new MelsecMcDataType(146, 1, "L***", 10);
    public static final MelsecMcDataType R_F = new MelsecMcDataType(147, 1, "F***", 10);
    public static final MelsecMcDataType R_V = new MelsecMcDataType(148, 1, "V***", 10);
    public static final MelsecMcDataType R_S = new MelsecMcDataType(152, 1, "S***", 10);
    public static final MelsecMcDataType R_B = new MelsecMcDataType(160, 1, "B***", 16);
    public static final MelsecMcDataType R_SB = new MelsecMcDataType(161, 1, "SB**", 16);
    public static final MelsecMcDataType R_DX = new MelsecMcDataType(162, 1, "DX**", 16);
    public static final MelsecMcDataType R_DY = new MelsecMcDataType(163, 1, "DY**", 16);
    public static final MelsecMcDataType R_D = new MelsecMcDataType(168, 0, "D***", 10);
    public static final MelsecMcDataType R_SD = new MelsecMcDataType(169, 0, "SD**", 10);
    public static final MelsecMcDataType R_W = new MelsecMcDataType(180, 0, "W***", 16);
    public static final MelsecMcDataType R_SW = new MelsecMcDataType(181, 0, "SW**", 16);
    public static final MelsecMcDataType R_R = new MelsecMcDataType(175, 0, "R***", 10);
    public static final MelsecMcDataType R_Z = new MelsecMcDataType(204, 0, "Z***", 10);
    public static final MelsecMcDataType R_LSTS = new MelsecMcDataType(89, 1, "LSTS", 10);
    public static final MelsecMcDataType R_LSTC = new MelsecMcDataType(88, 1, "LSTC", 10);
    public static final MelsecMcDataType R_LSTN = new MelsecMcDataType(90, 0, "LSTN", 10);
    public static final MelsecMcDataType R_STS = new MelsecMcDataType(199, 1, "STS*", 10);
    public static final MelsecMcDataType R_STC = new MelsecMcDataType(198, 1, "STC*", 10);
    public static final MelsecMcDataType R_STN = new MelsecMcDataType(200, 0, "STN*", 10);
    public static final MelsecMcDataType R_LTS = new MelsecMcDataType(81, 1, "LTS*", 10);
    public static final MelsecMcDataType R_LTC = new MelsecMcDataType(80, 1, "LTC*", 10);
    public static final MelsecMcDataType R_LTN = new MelsecMcDataType(82, 0, "LTN*", 10);
    public static final MelsecMcDataType R_TS = new MelsecMcDataType(193, 1, "TS**", 10);
    public static final MelsecMcDataType R_TC = new MelsecMcDataType(192, 1, "TC**", 10);
    public static final MelsecMcDataType R_TN = new MelsecMcDataType(194, 0, "TN**", 10);
    public static final MelsecMcDataType R_LCS = new MelsecMcDataType(85, 1, "LCS*", 10);
    public static final MelsecMcDataType R_LCC = new MelsecMcDataType(84, 1, "LCC*", 10);
    public static final MelsecMcDataType R_LCN = new MelsecMcDataType(86, 0, "LCN*", 10);
    public static final MelsecMcDataType R_CS = new MelsecMcDataType(196, 1, "CS**", 10);
    public static final MelsecMcDataType R_CC = new MelsecMcDataType(195, 1, "CC**", 10);
    public static final MelsecMcDataType R_CN = new MelsecMcDataType(197, 0, "CN**", 10);
    public static final MelsecMcDataType Keyence_X = new MelsecMcDataType(156, 1, "X*", 16);
    public static final MelsecMcDataType Keyence_Y = new MelsecMcDataType(157, 1, "Y*", 16);
    public static final MelsecMcDataType Keyence_B = new MelsecMcDataType(160, 1, "B*", 16);
    public static final MelsecMcDataType Keyence_M = new MelsecMcDataType(144, 1, "M*", 10);
    public static final MelsecMcDataType Keyence_L = new MelsecMcDataType(146, 1, "L*", 10);
    public static final MelsecMcDataType Keyence_SM = new MelsecMcDataType(145, 1, "SM", 10);
    public static final MelsecMcDataType Keyence_SD = new MelsecMcDataType(169, 0, "SD", 10);
    public static final MelsecMcDataType Keyence_D = new MelsecMcDataType(168, 0, "D*", 10);
    public static final MelsecMcDataType Keyence_R = new MelsecMcDataType(175, 0, "R*", 10);
    public static final MelsecMcDataType Keyence_ZR = new MelsecMcDataType(176, 0, "ZR", 16);
    public static final MelsecMcDataType Keyence_W = new MelsecMcDataType(180, 0, "W*", 16);
    public static final MelsecMcDataType Keyence_TN = new MelsecMcDataType(194, 0, "TN", 10);
    public static final MelsecMcDataType Keyence_TS = new MelsecMcDataType(193, 1, "TS", 10);
    public static final MelsecMcDataType Keyence_TC = new MelsecMcDataType(192, 1, "TC", 10);
    public static final MelsecMcDataType Keyence_CN = new MelsecMcDataType(197, 0, "CN", 10);
    public static final MelsecMcDataType Keyence_CS = new MelsecMcDataType(196, 1, "CS", 10);
    public static final MelsecMcDataType Keyence_CC = new MelsecMcDataType(195, 1, "CC", 10);
    public static final MelsecMcDataType Panasonic_X = new MelsecMcDataType(156, 1, "X*", 10);
    public static final MelsecMcDataType Panasonic_Y = new MelsecMcDataType(157, 1, "Y*", 10);
    public static final MelsecMcDataType Panasonic_L = new MelsecMcDataType(160, 1, "L*", 10);
    public static final MelsecMcDataType Panasonic_R = new MelsecMcDataType(144, 1, "R*", 10);
    public static final MelsecMcDataType Panasonic_DT = new MelsecMcDataType(168, 0, "D*", 10);
    public static final MelsecMcDataType Panasonic_LD = new MelsecMcDataType(180, 0, "W*", 10);
    public static final MelsecMcDataType Panasonic_TN = new MelsecMcDataType(194, 0, "TN", 10);
    public static final MelsecMcDataType Panasonic_TS = new MelsecMcDataType(193, 1, "TS", 10);
    public static final MelsecMcDataType Panasonic_CN = new MelsecMcDataType(197, 0, "CN", 10);
    public static final MelsecMcDataType Panasonic_CS = new MelsecMcDataType(196, 1, "CS", 10);
    public static final MelsecMcDataType Panasonic_SM = new MelsecMcDataType(145, 1, "SM", 10);
    public static final MelsecMcDataType Panasonic_SD = new MelsecMcDataType(169, 0, "SD", 10);

    public MelsecMcDataType(int code, int type, String asciiCode, int fromBase) {
        this.DataCode = (short) code;
        this.AsciiCode = asciiCode;
        this.FromBase = fromBase;
        if (type < 2) {
            this.DataType = (byte) type;
        }
    }

    public short getDataCode() {
        return this.DataCode;
    }

    public byte getDataType() {
        return this.DataType;
    }

    public String getAsciiCode() {
        return this.AsciiCode;
    }

    public int getFromBase() {
        return this.FromBase;
    }
}

