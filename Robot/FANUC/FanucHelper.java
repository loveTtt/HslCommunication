/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Robot.FANUC;

import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

public class FanucHelper {
    public static final byte SELECTOR_Q = 72;
    public static final byte SELECTOR_I = 70;
    public static final byte SELECTOR_AQ = 12;
    public static final byte SELECTOR_AI = 10;
    public static final byte SELECTOR_M = 76;
    public static final byte SELECTOR_D = 8;
    public static final byte SELECTOR_G = 56;
    public final String NotAllowedBool = ", Current address not support bool read/write";
    public final String NotAllowedWord = ", Current address not support word read/write";

    public static OperateResultExTwo<Byte, Integer> AnalysisFanucAddress(String address, boolean isBit) {
        try {
            if (isBit) {
                if (address.startsWith("sdo") || address.startsWith("SDO")) {
                    int add = Integer.parseInt(address.substring(3));
                    if (add < 11001) {
                        return OperateResultExTwo.CreateSuccessResult((byte)70, add);
                    }
                    return OperateResultExTwo.CreateSuccessResult((byte)76, add - 11000);
                }
                if (address.startsWith("sdi") || address.startsWith("SDI")) {
                    return OperateResultExTwo.CreateSuccessResult((byte)72, Integer.parseInt(address.substring(3)));
                }
                if (address.startsWith("rdi") || address.startsWith("RDI")) {
                    return OperateResultExTwo.CreateSuccessResult((byte)72, Integer.parseInt(address.substring(3)) + 5000);
                }
                if (address.startsWith("rdo") || address.startsWith("RDO")) {
                    return OperateResultExTwo.CreateSuccessResult((byte)70, Integer.parseInt(address.substring(3)) + 5000);
                }
                if (address.startsWith("ui") || address.startsWith("UI")) {
                    return OperateResultExTwo.CreateSuccessResult((byte)72, Integer.parseInt(address.substring(2)) + 6000);
                }
                if (address.startsWith("uo") || address.startsWith("UO")) {
                    return OperateResultExTwo.CreateSuccessResult((byte)70, Integer.parseInt(address.substring(2)) + 6000);
                }
                if (address.startsWith("si") || address.startsWith("SI")) {
                    return OperateResultExTwo.CreateSuccessResult((byte)72, Integer.parseInt(address.substring(2)) + 7000);
                }
                if (address.startsWith("so") || address.startsWith("SO")) {
                    return OperateResultExTwo.CreateSuccessResult((byte)70, Integer.parseInt(address.substring(2)) + 7000);
                }
            } else {
                if (address.startsWith("gi") || address.startsWith("GI")) {
                    return OperateResultExTwo.CreateSuccessResult((byte)12, Integer.parseInt(address.substring(2)));
                }
                if (address.startsWith("go") || address.startsWith("GO")) {
                    int add = Integer.parseInt(address.substring(2));
                    if (add >= 10001) {
                        add -= 6000;
                    }
                    return OperateResultExTwo.CreateSuccessResult((byte)10, add);
                }
            }
            if (address.startsWith("aq") || address.startsWith("AQ")) {
                return OperateResultExTwo.CreateSuccessResult((byte)12, Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("ai") || address.startsWith("AI")) {
                return OperateResultExTwo.CreateSuccessResult((byte)10, Integer.parseInt(address.substring(2)));
            }
            if (address.startsWith("sr") || address.startsWith("SR")) {
                int offset = Integer.parseInt(address.substring(2));
                if (offset < 1 || offset > 6) {
                    return new OperateResultExTwo<Byte, Integer>("SR type address only support SR1 - SR6");
                }
                return OperateResultExTwo.CreateSuccessResult((byte)8, 5891 + (offset - 1) * 40);
            }
            if (address.startsWith("i") || address.startsWith("I")) {
                return OperateResultExTwo.CreateSuccessResult((byte)70, Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("q") || address.startsWith("Q")) {
                return OperateResultExTwo.CreateSuccessResult((byte)72, Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("m") || address.startsWith("M")) {
                return OperateResultExTwo.CreateSuccessResult((byte)76, Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("d") || address.startsWith("D")) {
                return OperateResultExTwo.CreateSuccessResult((byte)8, Integer.parseInt(address.substring(1)));
            }
            if (address.startsWith("r") || address.startsWith("R")) {
                int offset = Integer.parseInt(address.substring(1));
                if (offset < 1 || offset > 10) {
                    return new OperateResultExTwo<Byte, Integer>("R type address only support R1 - R10");
                }
                return OperateResultExTwo.CreateSuccessResult((byte)8, 3451 + (offset - 1) * 2);
            }
            return new OperateResultExTwo<Byte, Integer>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExTwo<Byte, Integer>(ex.getMessage());
        }
    }

    public static byte[] BulidReadData(byte sel, int address, short length) {
        byte[] read_req = new byte[]{2, 0, 6, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, -64, 0, 0, 0, 0, 16, 14, 0, 0, 1, 1, 4, 8, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        read_req[43] = sel;
        read_req[44] = Utilities.getBytes(address - 1)[0];
        read_req[45] = Utilities.getBytes(address - 1)[1];
        read_req[46] = Utilities.getBytes(length)[0];
        read_req[47] = Utilities.getBytes(length)[1];
        return read_req;
    }

    public static byte[] BuildWriteData(byte sel, int address, byte[] value, int length) {
        if (value == null) {
            value = new byte[]{};
        }
        if (value.length > 6) {
            byte[] buffer = new byte[56 + value.length];
            byte[] write_req = new byte[]{2, 0, 9, 0, 50, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, -128, 0, 0, 0, 0, 16, 14, 0, 0, 1, 1, 50, 0, 0, 0, 0, 0, 1, 1, 7, 8, 49, 0, 25, 0};
            System.arraycopy(write_req, 0, buffer, 0, write_req.length);
            System.arraycopy(value, 0, buffer, 56, value.length);
            buffer[4] = Utilities.getBytes(value.length)[0];
            buffer[5] = Utilities.getBytes(value.length)[1];
            buffer[51] = sel;
            buffer[52] = Utilities.getBytes(address - 1)[0];
            buffer[53] = Utilities.getBytes(address - 1)[1];
            buffer[54] = Utilities.getBytes(length)[0];
            buffer[55] = Utilities.getBytes(length)[1];
            return buffer;
        }
        byte[] write_req = new byte[]{2, 0, 8, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, -64, 0, 0, 0, 0, 16, 14, 0, 0, 1, 1, 7, 8, 9, 0, 4, 0, 1, 0, 2, 0, 3, 0, 4, 0};
        write_req[43] = sel;
        write_req[44] = Utilities.getBytes(address - 1)[0];
        write_req[45] = Utilities.getBytes(address - 1)[1];
        write_req[46] = Utilities.getBytes(length)[0];
        write_req[47] = Utilities.getBytes(length)[1];
        System.arraycopy(value, 0, write_req, 48, value.length);
        return write_req;
    }

    public static String[] GetFanucCmds() {
        return new String[]{"CLRASG", "SETASG 1 500 ALM[E1] 1", "SETASG 501 100 ALM[1] 1", "SETASG 601 100 ALM[P1] 1", "SETASG 701 50 POS[15] 0.0", "SETASG 751 50 POS[15] 0.0", "SETASG 801 50 POS[G2: 15] 0.0", "SETASG 851 50 POS[G3: 0] 0.0", "SETASG 901 50 POS[G4:0] 0.0", "SETASG 951 50 POS[G5:0] 0.0", "SETASG 1001 18 PRG[1] 1", "SETASG 1019 18 PRG[M1] 1", "SETASG 1037 18 PRG[K1] 1", "SETASG 1055 18 PRG[MK1] 1", "SETASG 1073 500 PR[1] 0.0", "SETASG 1573 200 PR[G2:1] 0.0", "SETASG 1773 500 PR[G3:1] 0.0", "SETASG 2273 500 PR[G4: 1] 0.0", "SETASG 2773 500 PR[G5: 1] 0.0", "SETASG 3273 2 $FAST_CLOCK 1", "SETASG 3275 2 $TIMER[10].$TIMER_VAL 1", "SETASG 3277 2 $MOR_GRP[1].$CURRENT_ANG[1] 0", "SETASG 3279 2 $DUTY_TEMP 0", "SETASG 3281 40 $TIMER[10].$COMMENT 1", "SETASG 3321 40 $TIMER[2].$COMMENT 1", "SETASG 3361 50 $MNUTOOL[1,1] 0.0", "SETASG 3411 40 $[HTTPKCL]CMDS[1] 1", "SETASG 3451 10 R[1] 1.0", "SETASG 3461 10 R[6] 0", "SETASG 3471 250 PR[1]@1.25 0.0", "SETASG 3721 250 PR[1]@1.25 0.0", "SETASG 3971 120 PR[G2:1]@27.12 0.0", "SETASG 4091 120 DI[C1] 1", "SETASG 4211 120 DO[C1] 1", "SETASG 4331 120 RI[C1] 1", "SETASG 4451 120 RO[C1] 1", "SETASG 4571 120 UI[C1] 1", "SETASG 4691 120 UO[C1] 1", "SETASG 4811 120 SI[C1] 1", "SETASG 4931 120 SO[C1] 1", "SETASG 5051 120 WI[C1] 1", "SETASG 5171 120 WO[C1] 1", "SETASG 5291 120 WSI[C1] 1", "SETASG 5411 120 AI[C1] 1", "SETASG 5531 120 AO[C1] 1", "SETASG 5651 120 GI[C1] 1", "SETASG 5771 120 GO[C1] 1", "SETASG 5891 120 SR[1] 1", "SETASG 6011 120 SR[C1] 1", "SETASG 6131 10 R[1] 1.0", "SETASG 6141 2 $TIMER[1].$TIMER_VAL 1", "SETASG 6143 2 $TIMER[2].$TIMER_VAL 1", "SETASG 6145 2 $TIMER[3].$TIMER_VAL 1", "SETASG 6147 2 $TIMER[4].$TIMER_VAL 1", "SETASG 6149 2 $TIMER[5].$TIMER_VAL 1", "SETASG 6151 2 $TIMER[6].$TIMER_VAL 1", "SETASG 6153 2 $TIMER[7].$TIMER_VAL 1", "SETASG 6155 2 $TIMER[8].$TIMER_VAL 1", "SETASG 6157 2 $TIMER[9].$TIMER_VAL 1", "SETASG 6159 2 $TIMER[10].$TIMER_VAL 1"};
    }
}

