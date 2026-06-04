/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

public class OmronFinsDataType {
    private byte BitCode = 0;
    private byte WordCode = 0;
    public static final OmronFinsDataType DM = new OmronFinsDataType(2, -126);
    public static final OmronFinsDataType CIO = new OmronFinsDataType(48, -80);
    public static final OmronFinsDataType WR = new OmronFinsDataType(49, -79);
    public static final OmronFinsDataType HR = new OmronFinsDataType(50, -78);
    public static final OmronFinsDataType AR = new OmronFinsDataType(51, -77);
    public static final OmronFinsDataType TIM = new OmronFinsDataType(9, -119);

    public OmronFinsDataType(int bitCode, int wordCode) {
        this.BitCode = (byte) bitCode;
        this.WordCode = (byte) wordCode;
    }

    public byte getBitCode() {
        return this.BitCode;
    }

    public byte getWordCode() {
        return this.WordCode;
    }
}

