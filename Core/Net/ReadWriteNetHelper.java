/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.IReadWriteNet;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Utilities;

public class ReadWriteNetHelper {
    public static OperateResult WriteBoolWithWord(IReadWriteNet readWrite, String address, boolean[] values, int addLength) {
        return ReadWriteNetHelper.WriteBoolWithWord(readWrite, address, values, addLength, false, null);
    }

    public static OperateResult WriteBoolWithWord(IReadWriteNet readWrite, String address, boolean[] values, int addLength, boolean reverseWord, String bitStr) {
        boolean[] array;
        String[] adds = Utilities.SplitDot(address);
        int bit = 0;
        try {
            if (Utilities.IsStringNullOrEmpty(bitStr)) {
                if (adds.length > 1) {
                    bit = Convert.ToInt32(adds[1]);
                }
            } else {
                bit = Convert.ToInt32(bitStr);
            }
        }
        catch (Exception ex) {
            return new OperateResult(address + " Bit index input wrong: " + ex.getMessage());
        }
        short wordLength = (short)((bit + values.length + addLength - 1) / addLength);
        OperateResultExOne<byte[]> read = readWrite.Read(adds[0], wordLength);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        boolean[] blArray = array = reverseWord ? SoftBasic.ByteToBoolArray(SoftBasic.BytesReverseByWord((byte[])read.Content)) : SoftBasic.ByteToBoolArray((byte[])read.Content);
        if (bit + values.length <= array.length) {
            Utilities.ByteArrayCopyTo(values, array, bit);
        }
        return readWrite.Write(adds[0], reverseWord ? SoftBasic.BytesReverseByWord(SoftBasic.BoolArrayToByte(array)) : SoftBasic.BoolArrayToByte(array));
    }

    public static OperateResult WriteBoolWithWord(IReadWriteNet readWrite, String address, boolean[] values) {
        return ReadWriteNetHelper.WriteBoolWithWord(readWrite, address, values, 16);
    }
}

