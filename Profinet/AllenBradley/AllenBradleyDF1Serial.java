/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.AllenBradley;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.AllenBradleySLCAddress;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;

public class AllenBradleyDF1Serial {
    private static void AddLengthToMemoryStream(MemoryStream ms, short value) {
        if (value < 255) {
            ms.WriteByte((byte)value);
        } else {
            ms.WriteByte(255);
            ms.WriteByte(BitConverter.GetBytes(value)[0]);
            ms.WriteByte(BitConverter.GetBytes(value)[1]);
        }
    }

    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalReadWithThreeAddressFields(int tns, String address, short length) {
        OperateResultExOne<AllenBradleySLCAddress> analysis = AllenBradleySLCAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        MemoryStream ms = new MemoryStream();
        ms.WriteByte(15);
        ms.WriteByte(0);
        ms.WriteByte(BitConverter.GetBytes(tns)[0]);
        ms.WriteByte(BitConverter.GetBytes(tns)[1]);
        ms.WriteByte(162);
        ms.WriteByte(BitConverter.GetBytes(length)[0]);
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, ((AllenBradleySLCAddress)analysis.Content).DbBlock);
        ms.WriteByte(((AllenBradleySLCAddress)analysis.Content).DataCode);
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, (short)((AllenBradleySLCAddress)analysis.Content).getAddressStart());
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, (short)0);
        return OperateResultExOne.CreateSuccessResult(ms.ToArray());
    }

    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalReadWithThreeAddressFields(byte dstNode, byte srcNode, int tns, String address, short length) {
        OperateResultExOne<byte[]> build = AllenBradleyDF1Serial.BuildProtectedTypedLogicalReadWithThreeAddressFields(tns, address, length);
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceArray(new byte[]{dstNode, srcNode}, (byte[])build.Content));
    }

    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalWriteWithThreeAddressFields(int tns, String address, byte[] data) {
        OperateResultExOne<AllenBradleySLCAddress> analysis = AllenBradleySLCAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        MemoryStream ms = new MemoryStream();
        ms.WriteByte(15);
        ms.WriteByte(0);
        ms.WriteByte(BitConverter.GetBytes(tns)[0]);
        ms.WriteByte(BitConverter.GetBytes(tns)[1]);
        ms.WriteByte(170);
        ms.WriteByte(BitConverter.GetBytes(data.length)[0]);
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, ((AllenBradleySLCAddress)analysis.Content).DbBlock);
        ms.WriteByte(((AllenBradleySLCAddress)analysis.Content).DataCode);
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, (short)((AllenBradleySLCAddress)analysis.Content).getAddressStart());
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, (short)0);
        ms.Write(data);
        return OperateResultExOne.CreateSuccessResult(ms.ToArray());
    }

    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalWriteWithThreeAddressFields(byte dstNode, byte srcNode, int tns, String address, byte[] data) {
        OperateResultExOne<byte[]> build = AllenBradleyDF1Serial.BuildProtectedTypedLogicalWriteWithThreeAddressFields(tns, address, data);
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceArray(new byte[]{dstNode, srcNode}, (byte[])build.Content));
    }

    public static OperateResultExOne<byte[]> BuildProtectedTypedLogicalMaskWithThreeAddressFields(int tns, String address, int bitIndex, boolean value) {
        int mask = 1 << bitIndex;
        OperateResultExOne<AllenBradleySLCAddress> analysis = AllenBradleySLCAddress.ParseFrom(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        MemoryStream ms = new MemoryStream();
        ms.WriteByte(15);
        ms.WriteByte(0);
        ms.WriteByte(BitConverter.GetBytes(tns)[0]);
        ms.WriteByte(BitConverter.GetBytes(tns)[1]);
        ms.WriteByte(171);
        ms.WriteByte(2);
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, ((AllenBradleySLCAddress)analysis.Content).DbBlock);
        ms.WriteByte(((AllenBradleySLCAddress)analysis.Content).DataCode);
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, (short)((AllenBradleySLCAddress)analysis.Content).getAddressStart());
        AllenBradleyDF1Serial.AddLengthToMemoryStream(ms, (short)0);
        ms.WriteByte(BitConverter.GetBytes(mask)[0]);
        ms.WriteByte(BitConverter.GetBytes(mask)[1]);
        if (value) {
            ms.WriteByte(BitConverter.GetBytes(mask)[0]);
            ms.WriteByte(BitConverter.GetBytes(mask)[1]);
        } else {
            ms.WriteByte(0);
            ms.WriteByte(0);
        }
        return OperateResultExOne.CreateSuccessResult(ms.ToArray());
    }

    public static String GetExtStatusDescription(byte code) {
        switch (code) {
            case 1: {
                return "A field has an illegal value";
            }
            case 2: {
                return "Less levels specified in address than minimum for any address";
            }
            case 3: {
                return "More levels specified in address than system supports";
            }
            case 4: {
                return "Symbol not found";
            }
            case 5: {
                return "Symbol is of improper format";
            }
            case 6: {
                return "Address doesn\u2019t point to something usable";
            }
            case 7: {
                return "File is wrong size";
            }
            case 8: {
                return "Cannot complete request, situation has changed since the start of the command";
            }
            case 9: {
                return "Data or file is too large";
            }
            case 10: {
                return "Transaction size plus word address is too large";
            }
            case 11: {
                return "Access denied, improper privilege";
            }
            case 12: {
                return "Condition cannot be generated \u0006 resource is not available";
            }
            case 13: {
                return "Condition already exists \u0006 resource is already available";
            }
            case 14: {
                return "Command cannot be executed";
            }
            case 15: {
                return "Histogram overflow";
            }
            case 16: {
                return "No access";
            }
            case 17: {
                return "Illegal data type";
            }
            case 18: {
                return "Invalid parameter or invalid data";
            }
            case 19: {
                return "Address reference exists to deleted area";
            }
            case 20: {
                return "Command execution failure for unknown reason; possible PLC\u00063 histogram overflow";
            }
            case 21: {
                return "Data conversion error";
            }
            case 22: {
                return "Scanner not able to communicate with 1771 rack adapter";
            }
            case 23: {
                return "Type mismatch";
            }
            case 24: {
                return "1771 module response was not valid";
            }
            case 25: {
                return "Duplicated label";
            }
            case 26: {
                return "File is open; another node owns it";
            }
            case 27: {
                return "Another node is the program owner";
            }
            case 28: {
                return "Reserved";
            }
            case 29: {
                return "Reserved";
            }
            case 30: {
                return "Data table element protection violation";
            }
            case 31: {
                return "Temporary internal problem";
            }
            case 34: {
                return "Remote rack fault";
            }
            case 35: {
                return "Timeout";
            }
            case 36: {
                return "Unknown error";
            }
        }
        return StringResources.Language.UnknownError();
    }
}

