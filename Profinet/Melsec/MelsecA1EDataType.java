/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

public class MelsecA1EDataType {
    private short DataCode = 0;
    private byte DataType = 0;
    private String AsciiCode = "";
    private int FromBase = 0;
    public static final MelsecA1EDataType X = new MelsecA1EDataType(22560, 1, "X*", 16);
    public static final MelsecA1EDataType Y = new MelsecA1EDataType(22816, 1, "Y*", 16);
    public static final MelsecA1EDataType M = new MelsecA1EDataType(19744, 1, "M*", 10);
    public static final MelsecA1EDataType S = new MelsecA1EDataType(21280, 1, "S*", 10);
    public static final MelsecA1EDataType F = new MelsecA1EDataType(17952, 1, "F*", 10);
    public static final MelsecA1EDataType B = new MelsecA1EDataType(16928, 1, "B*", 16);
    public static final MelsecA1EDataType TS = new MelsecA1EDataType(21587, 1, "TS", 10);
    public static final MelsecA1EDataType TC = new MelsecA1EDataType(21571, 1, "TC", 10);
    public static final MelsecA1EDataType TN = new MelsecA1EDataType(21582, 0, "TN", 10);
    public static final MelsecA1EDataType CS = new MelsecA1EDataType(17235, 1, "CS", 10);
    public static final MelsecA1EDataType CC = new MelsecA1EDataType(17219, 1, "CC", 10);
    public static final MelsecA1EDataType CN = new MelsecA1EDataType(17230, 0, "CN", 10);
    public static final MelsecA1EDataType D = new MelsecA1EDataType(17440, 0, "D*", 10);
    public static final MelsecA1EDataType W = new MelsecA1EDataType(22304, 0, "W*", 16);
    public static final MelsecA1EDataType R = new MelsecA1EDataType(21024, 0, "R*", 10);

    public MelsecA1EDataType(int code, int type, String asciiCode, int fromBase) {
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

