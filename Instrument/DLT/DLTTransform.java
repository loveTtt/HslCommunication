/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Instrument.DLT;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.Environment;
import HslCommunication.Core.Types.List;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Instrument.DLT.Helper.DLT645Type;
import HslCommunication.Utilities;

public class DLTTransform {
    public static OperateResultExOne<String> TransStringFromDLt(byte[] content, short length) {
        OperateResultExOne<String> result;
        try {
            String empty = "";
            byte[] buffer = Utilities.EveryByteAdd(Utilities.ReverseNew(SoftBasic.BytesArraySelectBegin(content, length)), -51);
            result = OperateResultExOne.CreateSuccessResult(Encoding.ASCII.GetString(buffer));
        }
        catch (Exception ex) {
            result = new OperateResultExOne(ex.getMessage() + " Reason: " + SoftBasic.ByteToHexString(content, ' '));
        }
        return result;
    }

    private static byte[] CreateFromStrings(String[] content, boolean reverse) {
        MemoryStream ms = new MemoryStream();
        for (int i = 0; i < content.length; ++i) {
            if (reverse) {
                ms.Write(Utilities.ReverseNew(SoftBasic.HexStringToBytes(content[i])));
                continue;
            }
            ms.Write(SoftBasic.HexStringToBytes(content[i]));
        }
        return ms.ToArray();
    }

    public static OperateResultExOne<byte[]> TransDltFromStrings(DLT645Type type, String content, byte[] dataID, boolean reverse) {
        return DLTTransform.TransDltFromStrings(type, new String[]{content}, dataID, reverse);
    }

    public static OperateResultExOne<byte[]> TransDltFromStrings(DLT645Type type, String[] contents, byte[] dataID, boolean reverse) {
        try {
            int[] formats;
            int[] nArray = formats = type == DLT645Type.DLT2007 ? DLTTransform.GetDLT2007FormatWithDataArea(dataID) : DLTTransform.GetDLT1997FormatWithDataArea(dataID);
            if (formats == null) {
                return OperateResultExOne.CreateSuccessResult(DLTTransform.CreateFromStrings(contents, reverse));
            }
            MemoryStream ms = new MemoryStream();
            for (int i = 0; i < contents.length && i < formats.length; ++i) {
                byte[] buffer;
                byte[] array = BitConverter.GetBytes(formats[i]);
                int byteCount = array[0] & 0xFF;
                int decimalCount = array[1] & 0xFF;
                int length = array[2] & 0xFF;
                String content = contents[i];
                if (array[3] == 1) {
                    if (reverse) {
                        ms.Write(Utilities.ReverseNew(Encoding.ASCII.GetBytes(content)));
                        continue;
                    }
                    ms.Write(Encoding.ASCII.GetBytes(content));
                    continue;
                }
                boolean belowZero = false;
                if (decimalCount >= 128) {
                    decimalCount -= 128;
                }
                if (decimalCount != 0) {
                    try {
                        content = String.valueOf(Convert.ToInt32(Convert.ToDouble(content) * Math.pow(10.0, decimalCount)));
                    }
                    catch (Exception ex) {
                        return new OperateResultExOne<byte[]>(ex.getMessage() + " ID:" + SoftBasic.ByteToHexString(dataID, '-') + " Value:" + content + Environment.NewLine + ex.getStackTrace());
                    }
                }
                if (content.startsWith("-")) {
                    belowZero = true;
                    content = content.substring(1);
                }
                if (content.length() < byteCount * 2) {
                    content = Utilities.PadLeft(content, byteCount * 2, '0');
                }
                if (content.length() > byteCount * 2) {
                    content = content.substring(0, byteCount * 2);
                }
                byte[] byArray = buffer = reverse ? Utilities.ReverseNew(SoftBasic.HexStringToBytes(content)) : SoftBasic.HexStringToBytes(content);
                if (belowZero) {
                    buffer[0] = (byte)(buffer[0] & 0x80);
                }
                ms.Write(buffer);
            }
            return OperateResultExOne.CreateSuccessResult(ms.ToArray());
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage() + " ID:" + SoftBasic.ByteToHexString(dataID, '-') + Environment.NewLine + ex.getStackTrace());
        }
    }

    public static OperateResultExOne<String[]> TransStringsFromDLt(DLT645Type type, byte[] content, byte[] dataID, boolean reverse) {
        List<String> strings = new List<String>();
        try {
            int[] formats;
            int[] nArray = formats = type == DLT645Type.DLT2007 ? DLTTransform.GetDLT2007FormatWithDataArea(dataID) : DLTTransform.GetDLT1997FormatWithDataArea(dataID);
            if (formats == null) {
                if (reverse) {
                    strings.Add(SoftBasic.ByteToHexString(Utilities.EveryByteAdd(Utilities.ReverseNew(content), -51)));
                } else {
                    strings.Add(SoftBasic.ByteToHexString(Utilities.EveryByteAdd(content, -51)));
                }
                return OperateResultExOne.CreateSuccessResult(strings.toStringArray());
            }
            int offset = 0;
            for (int i = 0; i < formats.length; ++i) {
                if (offset >= content.length) {
                    return OperateResultExOne.CreateSuccessResult(strings.toStringArray());
                }
                byte[] array = BitConverter.GetBytes(formats[i]);
                int byteCount = array[0] & 0xFF;
                int decimalCount = array[1] & 0xFF;
                int length = array[2] & 0xFF;
                if (array[3] == 1) {
                    if (reverse) {
                        strings.Add(Encoding.ASCII.GetString(Utilities.EveryByteAdd(Utilities.ReverseNew(SoftBasic.BytesArraySelectMiddle(content, offset, byteCount)), -51)));
                    } else {
                        strings.Add(Encoding.ASCII.GetString(Utilities.EveryByteAdd(SoftBasic.BytesArraySelectMiddle(content, offset, byteCount), -51)));
                    }
                } else {
                    byte[] buffer;
                    double scale = 1.0;
                    byte[] byArray = buffer = reverse ? Utilities.EveryByteAdd(Utilities.ReverseNew(SoftBasic.BytesArraySelectMiddle(content, offset, byteCount)), -51) : Utilities.EveryByteAdd(SoftBasic.BytesArraySelectMiddle(content, offset, byteCount), -51);
                    if (decimalCount >= 128) {
                        decimalCount -= 128;
                        scale = (buffer[0] & 0x80) == 128 ? -1.0 : 1.0;
                        buffer[0] = (byte)(buffer[0] & 0x7F);
                    }
                    String hex = SoftBasic.ByteToHexString(buffer);
                    if (decimalCount == 0) {
                        strings.Add(hex);
                    } else {
                        try {
                            strings.Add(String.valueOf(Convert.ToDouble(hex) * scale / Math.pow(10.0, decimalCount)));
                        }
                        catch (Exception ex) {
                            return new OperateResultExOne<String[]>(ex.getMessage() + " ID:" + SoftBasic.ByteToHexString(dataID, '-') + " Value:" + hex + Environment.NewLine + ex.getStackTrace());
                        }
                    }
                }
                offset += byteCount;
            }
            return OperateResultExOne.CreateSuccessResult(strings.toStringArray());
        }
        catch (Exception ex) {
            return new OperateResultExOne<String[]>(ex.getMessage() + " ID:" + SoftBasic.ByteToHexString(dataID, '-') + Environment.NewLine + ex.getStackTrace());
        }
    }

    public static OperateResultExOne<double[]> TransDoubleFromDLt(byte[] content, short length) {
        return DLTTransform.TransDoubleFromDLt(content, length, "XXXXXX.XX");
    }

    public static OperateResultExOne<double[]> TransDoubleFromDLt(byte[] content, short length, String format) {
        try {
            format = format.toUpperCase();
            int byteCount = 0;
            for (int i = 0; i < format.length(); ++i) {
                if (format.charAt(i) == '.') continue;
                ++byteCount;
            }
            byteCount /= 2;
            int decimalCount = format.indexOf(46) >= 0 ? format.length() - format.indexOf(46) - 1 : 0;
            double[] values = new double[length];
            for (int i = 0; i < values.length; ++i) {
                byte[] buffer = Utilities.EveryByteAdd(Utilities.ReverseNew(SoftBasic.BytesArraySelectMiddle(content, i * byteCount, byteCount)), -51);
                values[i] = Convert.ToDouble(SoftBasic.ByteToHexString(buffer)) / Math.pow(10.0, decimalCount);
            }
            return OperateResultExOne.CreateSuccessResult(values);
        }
        catch (Exception ex) {
            return new OperateResultExOne<double[]>(ex.getMessage());
        }
    }

    private static int GetFormat(int byteLength, int digtal) {
        return DLTTransform.GetFormat(byteLength, digtal, 1, false);
    }

    private static int GetFormat(int byteLength, int digtal, int length) {
        return DLTTransform.GetFormat(byteLength, digtal, length, false);
    }

    private static int GetFormat(int byteLength, int digtal, int length, boolean negativeFlag) {
        if (digtal >= 0) {
            return BitConverter.ToInt32(new byte[]{(byte)byteLength, (byte)(digtal + (negativeFlag ? 128 : 0)), (byte)length, 0}, 0);
        }
        return BitConverter.ToInt32(new byte[]{(byte)byteLength, 0, (byte)length, 1}, 0);
    }

    private static int[] get03_30_02() {
        List<Integer> result = new List<Integer>();
        result.Add(DLTTransform.GetFormat(6, 0));
        result.Add(DLTTransform.GetFormat(4, 0));
        for (int i = 0; i < 24; ++i) {
            result.Add(DLTTransform.GetFormat(3, 4));
            result.Add(DLTTransform.GetFormat(5, 0));
        }
        return result.toInt32Array();
    }

    public static int[] GetDLT2007FormatWithDataArea(byte[] dataArea) {
        int dataIndex2 = dataArea[2] & 0xFF;
        int dataIndex0 = dataArea[0] & 0xFF;
        int dataIndex1 = dataArea[1] & 0xFF;
        if (dataArea[3] == 0) {
            if (dataIndex2 == 0) {
                return new int[]{DLTTransform.GetFormat(4, 2, 1, true)};
            }
            if (dataIndex2 == 1) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 2) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 3) {
                return new int[]{DLTTransform.GetFormat(4, 2, 1, true)};
            }
            if (dataIndex2 == 4) {
                return new int[]{DLTTransform.GetFormat(4, 2, 1, true)};
            }
            return new int[]{DLTTransform.GetFormat(4, 2)};
        }
        if (dataArea[3] == 1) {
            if (dataIndex2 == 3) {
                return new int[]{DLTTransform.GetFormat(3, 4, 1, true), DLTTransform.GetFormat(5, 0)};
            }
            if (dataIndex2 == 4) {
                return new int[]{DLTTransform.GetFormat(3, 4, 1, true), DLTTransform.GetFormat(5, 0)};
            }
            return new int[]{DLTTransform.GetFormat(3, 4), DLTTransform.GetFormat(5, 0)};
        }
        if (dataArea[3] == 2) {
            if (dataIndex2 == 1) {
                return new int[]{DLTTransform.GetFormat(2, 1)};
            }
            if (dataIndex2 == 2) {
                return new int[]{DLTTransform.GetFormat(3, 3, 1, true)};
            }
            if (dataIndex2 < 6) {
                return new int[]{DLTTransform.GetFormat(3, 4, 1, true)};
            }
            if (dataIndex2 == 6) {
                return new int[]{DLTTransform.GetFormat(2, 3, 1, true)};
            }
            if (dataIndex2 == 7) {
                return new int[]{DLTTransform.GetFormat(2, 1)};
            }
            if (dataIndex2 < 128) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 1) {
                return new int[]{DLTTransform.GetFormat(3, 3, 1, true)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 2) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 3) {
                return new int[]{DLTTransform.GetFormat(3, 4)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 4) {
                return new int[]{DLTTransform.GetFormat(3, 4, 1, true)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 5) {
                return new int[]{DLTTransform.GetFormat(3, 4, 1, true)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 6) {
                return new int[]{DLTTransform.GetFormat(3, 4, 1, true)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 7) {
                return new int[]{DLTTransform.GetFormat(2, 1, 1, true)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 8) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 9) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
            if (dataIndex2 == 128 && dataIndex0 == 10) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
        }
        if (dataArea[3] == 3) {
            if (dataIndex2 < 5 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 6)};
            }
            if (dataIndex2 < 5) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(4, 2, 4), DLTTransform.GetFormat(4, 2, 4), DLTTransform.GetFormat(2, 1), DLTTransform.GetFormat(3, 3), DLTTransform.GetFormat(3, 4, 2), DLTTransform.GetFormat(2, 3), DLTTransform.GetFormat(4, 2, 4), DLTTransform.GetFormat(2, 1), DLTTransform.GetFormat(3, 3), DLTTransform.GetFormat(3, 4, 2), DLTTransform.GetFormat(2, 3), DLTTransform.GetFormat(4, 2, 4), DLTTransform.GetFormat(2, 1), DLTTransform.GetFormat(3, 3), DLTTransform.GetFormat(3, 4, 2), DLTTransform.GetFormat(2, 3), DLTTransform.GetFormat(4, 2, 4)};
            }
            if (dataIndex2 == 5 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 2)};
            }
            if (dataIndex2 == 5 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(3, 3), DLTTransform.GetFormat(6, 0)};
            }
            if (dataIndex2 == 6 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 2)};
            }
            if (dataIndex2 == 6 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(6, 0)};
            }
            if (dataIndex2 == 7 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 2)};
            }
            if (dataIndex2 == 7 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(4, 2, 16)};
            }
            if (dataIndex2 == 8 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 2)};
            }
            if (dataIndex2 == 8 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(4, 2, 16)};
            }
            if (dataIndex2 == 9 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 2)};
            }
            if (dataIndex2 == 9 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(2, 2), DLTTransform.GetFormat(4, 2, 16)};
            }
            if (dataIndex2 == 10 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 2)};
            }
            if (dataIndex2 == 10 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(2, 2), DLTTransform.GetFormat(4, 2, 16)};
            }
            if ((dataIndex2 == 11 || dataIndex2 == 12 || dataIndex2 == 13) && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 6)};
            }
            if (dataIndex2 == 11 || dataIndex2 == 12 || dataIndex2 == 13) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(4, 2, 4), DLTTransform.GetFormat(4, 2, 4), DLTTransform.GetFormat(2, 1), DLTTransform.GetFormat(3, 3), DLTTransform.GetFormat(3, 4, 2), DLTTransform.GetFormat(2, 3), DLTTransform.GetFormat(4, 2, 4), DLTTransform.GetFormat(2, 1), DLTTransform.GetFormat(3, 3), DLTTransform.GetFormat(3, 4, 2), DLTTransform.GetFormat(2, 3), DLTTransform.GetFormat(4, 2, 4), DLTTransform.GetFormat(2, 1), DLTTransform.GetFormat(3, 3), DLTTransform.GetFormat(3, 4, 2), DLTTransform.GetFormat(2, 3)};
            }
            if (dataIndex2 == 14 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 6)};
            }
            if (dataIndex2 == 14) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(4, 2, 16)};
            }
            if (dataIndex2 == 15 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 6)};
            }
            if (dataIndex2 == 15) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(4, 2, 16)};
            }
            if (dataIndex2 == 16) {
                return new int[]{DLTTransform.GetFormat(3, 0), DLTTransform.GetFormat(3, 2, 2), DLTTransform.GetFormat(3, 0, 2), DLTTransform.GetFormat(2, 1), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(2, 1), DLTTransform.GetFormat(4, 0)};
            }
            if (dataIndex2 == 17 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 17 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2)};
            }
            if (dataIndex2 == 18 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 6)};
            }
            if (dataIndex2 == 18) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(3, 4), DLTTransform.GetFormat(5, 0, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(4, 0, 10)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 1 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 1) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(4, 2, 24)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 2 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 2) {
                return DLTTransform.get03_30_02();
            }
            if (dataIndex2 == 48 && dataIndex1 == 3 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 3) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(4, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 4 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 4) {
                return new int[]{DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(6, 0, 2)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 5 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 5) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(3, 0, 14)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 6 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 6) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(3, 0, 28)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 7 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 7) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 8 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 8) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(4, 0, 254)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 9 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 9) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 10 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 10) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 11 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 11) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 12 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 12) {
                return new int[]{DLTTransform.GetFormat(6, 0), DLTTransform.GetFormat(4, 0), DLTTransform.GetFormat(2, 0, 3)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 13 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 13) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(4, 2, 12)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 14 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 48 && dataIndex1 == 14) {
                return new int[]{DLTTransform.GetFormat(6, 0, 2), DLTTransform.GetFormat(4, 2, 12)};
            }
            return null;
        }
        if (dataArea[3] == 4) {
            if (dataIndex2 == 0 && dataIndex1 == 1 && dataIndex0 == 1) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 1 && dataIndex0 == 2) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 1 && dataIndex0 == 3) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 1 && dataIndex0 == 4) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 1 && dataIndex0 == 5) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 1 && dataIndex0 == 6) {
                return new int[]{DLTTransform.GetFormat(5, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 1 && dataIndex0 == 7) {
                return new int[]{DLTTransform.GetFormat(5, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 2 && dataIndex0 == 5) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 2) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 3) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 4 && dataIndex0 <= 2) {
                return new int[]{DLTTransform.GetFormat(6, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 4 && dataIndex0 == 3) {
                return new int[]{DLTTransform.GetFormat(32, -1)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 4 && dataIndex0 <= 6) {
                return new int[]{DLTTransform.GetFormat(6, -1)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 4 && dataIndex0 <= 8) {
                return new int[]{DLTTransform.GetFormat(4, -1)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 4 && dataIndex0 <= 10) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 4 && dataIndex0 <= 12) {
                return new int[]{DLTTransform.GetFormat(10, -1)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 4 && dataIndex0 == 13) {
                return new int[]{DLTTransform.GetFormat(16, -1)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 5) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 6) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 7) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 8) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 9) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 10 && dataIndex0 == 1) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 10) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 11) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 12) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 13) {
                return new int[]{DLTTransform.GetFormat(2, 3)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 14 && dataIndex0 < 3) {
                return new int[]{DLTTransform.GetFormat(3, 4)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 14) {
                return new int[]{DLTTransform.GetFormat(2, 1)};
            }
            if (dataIndex2 == 1 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 14)};
            }
            if (dataIndex2 == 2 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(3, 0, 14)};
            }
            if (dataIndex2 == 3 && dataIndex1 == 0) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if (dataIndex2 == 4 && dataIndex1 == 1) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if (dataIndex2 == 4 && dataIndex1 == 2) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if (dataIndex2 == 128) {
                return new int[]{DLTTransform.GetFormat(32, -1)};
            }
            return null;
        }
        if (dataArea[3] == 5) {
            if (dataIndex2 == 0 && dataIndex1 == 0 && dataIndex0 == 1) {
                return new int[]{DLTTransform.GetFormat(5, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 1) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 2) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 3) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 4) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 5) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 6) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 7) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 8) {
                return new int[]{DLTTransform.GetFormat(4, 2)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 9) {
                return new int[]{DLTTransform.GetFormat(3, 4), DLTTransform.GetFormat(5, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 10) {
                return new int[]{DLTTransform.GetFormat(3, 4), DLTTransform.GetFormat(5, 0)};
            }
            if (dataIndex2 == 0 && dataIndex1 == 16) {
                return new int[]{DLTTransform.GetFormat(3, 4, 8)};
            }
            return null;
        }
        if (dataArea[3] == 6) {
            if (dataIndex1 == 0 && dataIndex0 == 0) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataIndex1 == 0 && dataIndex0 == 1) {
                return new int[]{DLTTransform.GetFormat(6, 0)};
            }
            if (dataIndex1 == 0 && dataIndex0 == 2) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            return null;
        }
        return null;
    }

    public static int[] GetDLT1997FormatWithDataArea(byte[] dataArea) {
        if ((dataArea[1] & 0xF0) == 144) {
            return new int[]{DLTTransform.GetFormat(4, 2)};
        }
        if ((dataArea[1] & 0xF0) == 160) {
            return new int[]{DLTTransform.GetFormat(3, 4)};
        }
        if (dataArea[1] == -80 || dataArea[1] == -79 || dataArea[1] == -76 || dataArea[1] == -75 || dataArea[1] == -72 || dataArea[1] == -71) {
            return new int[]{DLTTransform.GetFormat(4, 0)};
        }
        if (dataArea[1] == -78) {
            if (dataArea[0] == 16 || dataArea[0] == 17) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if (dataArea[0] == 18 || dataArea[0] == 19) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            return new int[]{DLTTransform.GetFormat(3, 0)};
        }
        if (dataArea[1] == -77) {
            if ((dataArea[0] & 0xF0) == 16) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if ((dataArea[0] & 0xF0) == 32) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if ((dataArea[0] & 0xF0) == 48) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if ((dataArea[0] & 0xF0) == 64) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
        } else if (dataArea[1] == -74) {
            if ((dataArea[0] & 0xF0) == 16) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if ((dataArea[0] & 0xF0) == 32) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
            if (dataArea[0] >= 48 && dataArea[0] < 52) {
                return new int[]{DLTTransform.GetFormat(3, 4)};
            }
            if (dataArea[0] == 52) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
            if (dataArea[0] == 53) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
            if ((dataArea[0] & 0xF0) == 64) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
            if ((dataArea[0] & 0xF0) == 80) {
                return new int[]{DLTTransform.GetFormat(2, 2)};
            }
        } else if (dataArea[1] == -64) {
            if (dataArea[0] == 16) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
            if (dataArea[0] == 17) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if ((dataArea[0] & 0xF0) == 32) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataArea[0] == 48) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataArea[0] == 49) {
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataArea[0] == 50) {
                return new int[]{DLTTransform.GetFormat(6, 0)};
            }
            if (dataArea[0] == 51) {
                return new int[]{DLTTransform.GetFormat(6, 0)};
            }
            if (dataArea[0] == 52) {
                return new int[]{DLTTransform.GetFormat(6, 0)};
            }
        } else if (dataArea[1] == -63) {
            if (dataArea[0] == 17) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataArea[0] == 18) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataArea[0] == 19) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataArea[0] == 20) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataArea[0] == 21) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataArea[0] == 22) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataArea[0] == 23) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if (dataArea[0] == 24) {
                return new int[]{DLTTransform.GetFormat(1, 0)};
            }
            if (dataArea[0] == 25) {
                return new int[]{DLTTransform.GetFormat(4, 1)};
            }
            if (dataArea[0] == 26) {
                return new int[]{DLTTransform.GetFormat(4, 1)};
            }
        } else if (dataArea[1] == -62) {
            if (dataArea[0] == 17) {
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
            if (dataArea[0] == 18) {
                return new int[]{DLTTransform.GetFormat(4, 0)};
            }
        } else {
            if (dataArea[1] == -61) {
                if ((dataArea[0] & 0xF0) == 16) {
                    return new int[]{DLTTransform.GetFormat(1, 0)};
                }
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataArea[1] == -60) {
                if (dataArea[0] == 30) {
                    return new int[]{DLTTransform.GetFormat(1, 0)};
                }
                return new int[]{DLTTransform.GetFormat(3, 0)};
            }
            if (dataArea[1] == -59) {
                if (dataArea[0] == 16) {
                    return new int[]{DLTTransform.GetFormat(4, 0)};
                }
                return new int[]{DLTTransform.GetFormat(2, 0)};
            }
        }
        return new int[]{DLTTransform.GetFormat(3, 0)};
    }
}

