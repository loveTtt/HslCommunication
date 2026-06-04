/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.IReadWriteNet;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Types.Array;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HslHelper {
    public static Random HslRandom = new Random();

    public static OperateResultExTwo<Integer, String> ExtractParameter(String address, String paraName, int defaultValue) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, paraName);
        return extra.IsSuccess ? extra : OperateResultExTwo.CreateSuccessResult(defaultValue, address);
    }

    public static OperateResultExTwo<Integer, String> ExtractParameter(String address, String paraName) {
        try {
            int len;
            int numberIndex;
            Pattern r = Pattern.compile(paraName + "=[0-9A-Fa-fx]+;");
            Matcher match = r.matcher(address);
            if (!match.find()) {
                return new OperateResultExTwo<Integer, String>("Address [" + address + "] can't find [" + paraName + "] Parameters. for example : " + paraName + "=1;100");
            }
            String group = match.group();
            String number = group.substring(numberIndex = paraName.length() + 1, numberIndex + (len = group.length() - paraName.length() - 2));
            int value = number.startsWith("0x") || number.startsWith("0X") ? Integer.parseInt(number.substring(2), 16) : (number.startsWith("0") ? Integer.parseInt(number, 8) : Integer.parseInt(number));
            address = address.replace(group, "");
            return OperateResultExTwo.CreateSuccessResult(value, address);
        }
        catch (Exception ex) {
            return new OperateResultExTwo<Integer, String>("Address [" + address + "] Get [" + paraName + "] Parameters failed: " + ex.getMessage());
        }
    }

    public static OperateResultExTwo<Boolean, String> ExtractBooleanParameter(String address, String paraName) {
        try {
            Pattern r = Pattern.compile(paraName + "=[0-1A-Za-z]+;");
            Matcher match = r.matcher(address);
            if (!match.find()) {
                return new OperateResultExTwo<Boolean, String>("Address [" + address + "] can't find [" + paraName + "] Parameters. for example : " + paraName + "=True;100");
            }
            String group = match.group();
            int numberIndex = paraName.length() + 1;
            int len = group.length() - paraName.length() - 2;
            String number = group.substring(numberIndex, len);
            boolean value = false;
            value = Pattern.compile("^[0-1]+$").matcher(number).find() ? Convert.ToInt32(number) != 0 : Convert.ToBoolean(number);
            address = address.replace(group, "");
            return OperateResultExTwo.CreateSuccessResult(value, address);
        }
        catch (Exception ex) {
            return new OperateResultExTwo<Boolean, String>("Address [" + address + "] Get [" + paraName + "] Parameters failed: " + ex.getMessage());
        }
    }

    public static OperateResultExTwo<Integer, String> ExtractStartIndex(String address) {
        try {
            Pattern r = Pattern.compile("\\[[0-9]+\\]$");
            Matcher match = r.matcher(address);
            if (!match.find()) {
                return OperateResultExTwo.CreateSuccessResult(-1, address);
            }
            String group = match.group();
            String number = group.substring(1, group.length() - 1);
            int value = Integer.parseInt(number);
            address = address.substring(0, address.length() - group.length());
            return OperateResultExTwo.CreateSuccessResult(value, address);
        }
        catch (Exception ex) {
            return OperateResultExTwo.CreateSuccessResult(-1, address);
        }
    }

    public static OperateResultExTwo<IByteTransform, String> ExtractTransformParameter(String address, IByteTransform defaultTransform) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return OperateResultExTwo.CreateSuccessResult(defaultTransform, address);
        }
        String paraName = "format";
        Pattern r = Pattern.compile(paraName + "=(ABCD|BADC|DCBA|CDAB);", 2);
        Matcher match = r.matcher(address);
        if (!match.find()) {
            return OperateResultExTwo.CreateSuccessResult(defaultTransform, address);
        }
        String format = match.group(0).substring(paraName.length() + 1, match.group(0).length() - 1);
        DataFormat dataFormat = defaultTransform.getDataFormat();
        switch (format.toUpperCase()) {
            case "ABCD": {
                dataFormat = DataFormat.ABCD;
                break;
            }
            case "BADC": {
                dataFormat = DataFormat.BADC;
                break;
            }
            case "DCBA": {
                dataFormat = DataFormat.DCBA;
                break;
            }
            case "CDAB": {
                dataFormat = DataFormat.CDAB;
                break;
            }
        }
        address = address.replace(match.group(0), "");
        if (dataFormat != defaultTransform.getDataFormat()) {
            return OperateResultExTwo.CreateSuccessResult(defaultTransform.CreateByDateFormat(dataFormat), address);
        }
        return OperateResultExTwo.CreateSuccessResult(defaultTransform, address);
    }

    public static OperateResultExTwo<int[], int[]> SplitReadLength(int address, short length, short segment) {
        int[] segments = SoftBasic.SplitIntegerToArray(length, segment);
        int[] addresses = new int[segments.length];
        for (int i = 0; i < addresses.length; ++i) {
            addresses[i] = i == 0 ? address : addresses[i - 1] + segments[i - 1];
        }
        return OperateResultExTwo.CreateSuccessResult(addresses, segments);
    }

    public static <T> OperateResultExTwo<int[], ArrayList<T[]>> SplitWriteData(Class<T> tClass, int address, T[] value, short segment, int addressLength) {
        ArrayList<T[]> segments = SoftBasic.ArraySplitByLength(tClass, value, segment * addressLength);
        int[] addresses = new int[segments.size()];
        for (int i = 0; i < addresses.length; ++i) {
            addresses[i] = i == 0 ? address : addresses[i - 1] + segments.get(i - 1).length / addressLength;
        }
        return OperateResultExTwo.CreateSuccessResult(addresses, segments);
    }

    public static OperateResultExTwo<Integer, String> GetBitIndexInformation(String address) {
        int bitIndex = 0;
        int lastIndex = address.lastIndexOf(46);
        if (lastIndex > 0 && lastIndex < address.length() - 1) {
            String bit = address.substring(lastIndex + 1);
            bitIndex = bit.contains("A") || bit.contains("B") || bit.contains("C") || bit.contains("D") || bit.contains("E") || bit.contains("F") ? Integer.parseInt(bit, 16) : Integer.parseInt(bit);
            address = address.substring(0, lastIndex);
        }
        return OperateResultExTwo.CreateSuccessResult(bitIndex, address);
    }

    public static String GetIpAddressFromInput(String value) {
        if (!Utilities.IsStringNullOrEmpty(value)) {
            if (value.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$")) {
                return value;
            }
            InetAddress address = null;
            try {
                address = InetAddress.getByName(value);
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return address.getHostAddress();
        }
        return "127.0.0.1";
    }

    public static byte[] GetUTF8Bytes(String message) {
        return Utilities.IsStringNullOrEmpty(message) ? new byte[]{} : message.getBytes(StandardCharsets.UTF_8);
    }

    public static OperateResultExThree<Integer, Short, Integer> CalculateStartBitIndexAndLength(int addressStart, short length) {
        OperateResultExThree<Integer, Short, Integer> result = new OperateResultExThree<Integer, Short, Integer>();
        result.IsSuccess = true;
        result.Message = StringResources.Language.SuccessText();
        short byteLength = (short)((addressStart + length - 1) / 8 - addressStart / 8 + 1);
        int offset = addressStart % 8;
        int newStart = addressStart - offset;
        result.Content1 = newStart;
        result.Content2 = byteLength;
        result.Content3 = offset;
        return result;
    }

    public static int CalculateBitStartIndex(String bit) {
        if (bit.contains("A") || bit.contains("B") || bit.contains("C") || bit.contains("D") || bit.contains("E") || bit.contains("F")) {
            return Integer.parseInt(bit, 16);
        }
        return Integer.parseInt(bit);
    }

    public static int CalculateOccupyLength(int address, int length) {
        return HslHelper.CalculateOccupyLength(address, length, 8);
    }

    public static int CalculateOccupyLength(int address, int length, int hex) {
        return (address + length - 1) / hex - address / hex + 1;
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteNet device, String address, short length) {
        return HslHelper.ReadBool(device, address, length, 16, false);
    }

    public static OperateResultExOne<boolean[]> ReadBool(IReadWriteNet device, String address, short length, int addressLength, boolean reverseByWord) {
        if (address.indexOf(46) > 0) {
            String[] addressSplits = Utilities.SplitDot(address);
            int bitIndex = 0;
            try {
                bitIndex = HslHelper.CalculateBitStartIndex(addressSplits[1]);
            }
            catch (Exception ex) {
                return new OperateResultExOne<boolean[]>("Bit Index format wrong, " + ex.getMessage());
            }
            short len = (short)((length + bitIndex + addressLength - 1) / addressLength);
            OperateResultExOne<byte[]> read = device.Read(addressSplits[0], len);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            if (reverseByWord) {
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(SoftBasic.BytesReverseByWord((byte[])read.Content)), bitIndex, length));
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray((byte[])read.Content), bitIndex, length));
        }
        return device.ReadBool(address, length);
    }

    public static void ThreadSleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static boolean IsAddressEndWithIndex(String address) {
        return address.matches("\\[[0-9]+\\]$");
    }

    private static String[] SplitsAddressDot(String address) {
        int index = address.lastIndexOf(".");
        if (index > 0 && index < address.length() - 1) {
            return new String[]{address.substring(0, index), address.substring(index + 1)};
        }
        return new String[]{address};
    }

    public static OperateResult WriteBool(IReadWriteNet device, String address, boolean[] value) {
        return HslHelper.WriteBool(device, address, value, 16, false);
    }

    public static OperateResult WriteBool(IReadWriteNet device, String address, boolean[] value, int addressLength, boolean reverseByWord) {
        return HslHelper.WriteBool(device, address, value, addressLength, reverseByWord, false);
    }

    public static OperateResult WriteBool(IReadWriteNet device, String address, boolean[] value, int addressLength, boolean reverseByWord, boolean insertPoint) {
        if (address.indexOf(46) > 0) {
            String[] addressSplits = HslHelper.SplitsAddressDot(address);
            int bitIndex = 0;
            try {
                bitIndex = HslHelper.CalculateBitStartIndex(addressSplits[1]);
            }
            catch (Exception ex) {
                return new OperateResultExOne("Bit Index format wrong, " + ex.getMessage());
            }
            int len = (value.length + bitIndex + addressLength - 1) / addressLength;
            OperateResultExOne<byte[]> read = device.Read(addressSplits[0], (short)len);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            boolean[] boolArray = reverseByWord ? SoftBasic.ByteToBoolArray(SoftBasic.BytesReverseByWord((byte[])read.Content)) : SoftBasic.ByteToBoolArray((byte[])read.Content);
            Array.Copy(value, 0, boolArray, bitIndex, value.length);
            byte[] write = reverseByWord ? SoftBasic.BytesReverseByWord(SoftBasic.BoolArrayToByte(boolArray)) : SoftBasic.BoolArrayToByte(boolArray);
            return device.Write(addressSplits[0], write);
        }
        if (insertPoint && address.length() > 1) {
            String addressNew = HslHelper.AddressAddPoint(address);
            return HslHelper.WriteBool(device, addressNew, value, addressLength, reverseByWord, insertPoint);
        }
        return device.Write(address, value);
    }

    private static String AddressAddPoint(String address) {
        return address.substring(0, address.length() - 1) + "." + address.substring(address.length() - 1);
    }
}

