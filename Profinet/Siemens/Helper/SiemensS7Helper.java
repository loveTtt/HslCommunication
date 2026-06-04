/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Siemens.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.S7AddressData;
import HslCommunication.Core.Net.IReadWriteNet;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.List;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Siemens.SiemensPLCS;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SiemensS7Helper {
    public static OperateResultExOne<byte[]> AnalysisReadBit(byte[] content) {
        try {
            int receiveCount = 1;
            if (content.length >= 21 && content[20] == 1) {
                byte[] buffer = new byte[receiveCount];
                if (22 < content.length) {
                    if (content[21] == -1 && content[22] == 3) {
                        buffer[0] = content[25];
                    } else {
                        if (content[21] == 5 && content[22] == 0) {
                            return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensReadLengthOverPlcAssign());
                        }
                        if (content[21] == 6 && content[22] == 0) {
                            return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensError0006());
                        }
                        if (content[21] == 10 && content[22] == 0) {
                            return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensError000A());
                        }
                        return new OperateResultExOne<byte[]>(content[21], StringResources.Language.UnknownError() + " Source: " + SoftBasic.ByteToHexString(content, ' '));
                    }
                }
                return OperateResultExOne.CreateSuccessResult(buffer);
            }
            return new OperateResultExOne<byte[]>(StringResources.Language.SiemensDataLengthCheckFailed());
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("AnalysisReadBit failed: " + ex.getMessage() + "\r\n Msg:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    public static ArrayList<S7AddressData[]> ArraySplitByLength(S7AddressData[] s7Addresses, int pduLength) {
        List<S7AddressData[]> array = new List<S7AddressData[]>();
        List<S7AddressData> tmpArray = new List<S7AddressData>();
        int lengthTotle = 0;
        for (int i = 0; i < s7Addresses.length; ++i) {
            if (tmpArray.size() >= 19 || lengthTotle + s7Addresses[i].getLength() >= pduLength) {
                if (tmpArray.size() > 0) {
                    array.Add(tmpArray.toArray(new S7AddressData[0]));
                    tmpArray.clear();
                }
                lengthTotle = 0;
            }
            tmpArray.Add(s7Addresses[i]);
            lengthTotle += s7Addresses[i].getLength();
        }
        if (tmpArray.size() > 0) {
            array.Add(tmpArray.toArray(new S7AddressData[0]));
        }
        return array;
    }

    public static OperateResultExOne<byte[]> AnalysisReadByte(byte[] content) {
        try {
            ArrayList<Byte> list = new ArrayList<Byte>();
            if (content.length >= 21) {
                for (int i = 21; i < content.length - 1; ++i) {
                    int count;
                    if (content[i] == -1 && content[i + 1] == 4) {
                        count = (content[i + 2] * 256 + content[i + 3]) / 8;
                        Utilities.ArrayListAddArray(list, SoftBasic.BytesArraySelectMiddle(content, i + 4, count));
                        i += count + 3;
                        continue;
                    }
                    if (content[i] == -1 && content[i + 1] == 9) {
                        int j;
                        count = content[i + 2] * 256 + content[i + 3];
                        if (count % 3 == 0) {
                            for (j = 0; j < count / 3; ++j) {
                                Utilities.ArrayListAddArray(list, SoftBasic.BytesArraySelectMiddle(content, i + 5 + 3 * j, 2));
                            }
                        } else {
                            for (j = 0; j < count / 5; ++j) {
                                Utilities.ArrayListAddArray(list, SoftBasic.BytesArraySelectMiddle(content, i + 7 + 5 * j, 2));
                            }
                        }
                        i += count + 4;
                        continue;
                    }
                    if (content[i] == 5 && content[i + 1] == 0) {
                        return new OperateResultExOne<byte[]>(content[i], StringResources.Language.SiemensReadLengthOverPlcAssign());
                    }
                    if (content[i] == 6 && content[i + 1] == 0) {
                        return new OperateResultExOne<byte[]>(content[i], StringResources.Language.SiemensError0006());
                    }
                    if (content[i] != 10 || content[i + 1] != 0) continue;
                    return new OperateResultExOne<byte[]>(content[i], StringResources.Language.SiemensError000A());
                }
                return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(list));
            }
            return new OperateResultExOne<byte[]>(StringResources.Language.SiemensDataLengthCheckFailed() + " Msg: " + SoftBasic.ByteToHexString(content, ' '));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("AnalysisReadByte failed: " + ex.getMessage() + "\r\n Msg:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    public static OperateResultExOne<String> ReadString(IReadWriteNet plc, SiemensPLCS currentPlc, String address, Charset encoding) {
        if (currentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> read = plc.Read(address, (short)2);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            if (((byte[])read.Content)[0] == 0 || ((byte[])read.Content)[0] == -1) {
                return new OperateResultExOne<String>("Value in plc is not string type");
            }
            OperateResultExOne<byte[]> readString = plc.Read(address, (short)(2 + ((byte[])read.Content)[1]));
            if (!readString.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(readString);
            }
            return OperateResultExOne.CreateSuccessResult(new String((byte[])readString.Content, 2, ((byte[])readString.Content).length - 2, encoding));
        }
        OperateResultExOne<byte[]> read = plc.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> readString = plc.Read(address, (short)(1 + ((byte[])read.Content)[0]));
        if (!readString.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(readString);
        }
        return OperateResultExOne.CreateSuccessResult(new String((byte[])readString.Content, 1, ((byte[])readString.Content).length - 1, encoding));
    }

    public static OperateResult Write(IReadWriteNet plc, SiemensPLCS currentPlc, String address, String value, Charset encoding) {
        if (value == null) {
            value = "";
        }
        byte[] buffer = value.getBytes(encoding);
        if (encoding == StandardCharsets.UTF_16) {
            buffer = SoftBasic.BytesReverseByWord(buffer);
        }
        if (currentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> readLength = plc.Read(address, (short)2);
            if (!readLength.IsSuccess) {
                return readLength;
            }
            if (((byte[])readLength.Content)[0] == -1) {
                return new OperateResultExOne("Value in plc is not string type");
            }
            if (((byte[])readLength.Content)[0] == 0) {
                ((byte[])readLength.Content)[0] = -2;
            }
            if (buffer.length > (((byte[])readLength.Content)[0] & 0xFF)) {
                return new OperateResultExOne("String length is too long than plc defined");
            }
            return plc.Write(address, SoftBasic.SpliceArray(new byte[]{((byte[])readLength.Content)[0], (byte)buffer.length}, buffer));
        }
        return plc.Write(address, SoftBasic.SpliceArray(new byte[]{(byte)buffer.length}, buffer));
    }

    public static OperateResultExOne<String> ReadWString(IReadWriteNet plc, SiemensPLCS currentPlc, String address) {
        if (currentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> read = plc.Read(address, (short)4);
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> readString = plc.Read(address, (short)(4 + (((byte[])read.Content)[2] * 256 + ((byte[])read.Content)[3]) * 2));
            if (!readString.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(readString);
            }
            return OperateResultExOne.CreateSuccessResult(new String(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin((byte[])readString.Content, 4)), StandardCharsets.UTF_16));
        }
        OperateResultExOne<byte[]> read = plc.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> readString = plc.Read(address, (short)(1 + ((byte[])read.Content)[0] * 2));
        if (!readString.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(readString);
        }
        return OperateResultExOne.CreateSuccessResult(new String((byte[])readString.Content, 1, ((byte[])readString.Content).length - 1, StandardCharsets.UTF_16));
    }

    public static OperateResult WriteWString(IReadWriteNet plc, SiemensPLCS currentPlc, String address, String value) {
        if (currentPlc != SiemensPLCS.S200Smart) {
            if (value == null) {
                value = "";
            }
            byte[] buffer = value.getBytes(StandardCharsets.UTF_16);
            buffer = SoftBasic.BytesReverseByWord(buffer);
            OperateResultExOne<byte[]> readLength = plc.Read(address, (short)4);
            if (!readLength.IsSuccess) {
                return readLength;
            }
            int defineLength = ((byte[])readLength.Content)[0] * 256 + ((byte[])readLength.Content)[1];
            if (value.length() > defineLength) {
                return new OperateResultExOne("String length is too long than plc defined");
            }
            byte[] write = new byte[buffer.length + 4];
            write[0] = ((byte[])readLength.Content)[0];
            write[1] = ((byte[])readLength.Content)[1];
            write[2] = BitConverter.GetBytes(value.length())[1];
            write[3] = BitConverter.GetBytes(value.length())[0];
            Utilities.ByteArrayCopyTo(buffer, write, 4);
            return plc.Write(address, write);
        }
        return plc.Write(address, value, StandardCharsets.UTF_16);
    }

    public static S7AddressData[] SplitS7Address(S7AddressData s7Address, int pduLength) {
        int readLength;
        List<S7AddressData> array = new List<S7AddressData>();
        int length = s7Address.getLength();
        for (int alreadyFinished = 0; alreadyFinished < length; alreadyFinished += readLength) {
            readLength = Math.min(length - alreadyFinished, pduLength);
            S7AddressData addressData = new S7AddressData(s7Address);
            if (s7Address.getDataCode() == 31 || s7Address.getDataCode() == 30) {
                addressData.setAddressStart(s7Address.getAddressStart() + alreadyFinished / 2);
            } else {
                addressData.setAddressStart(s7Address.getAddressStart() + alreadyFinished * 8);
            }
            addressData.setLength(readLength);
            array.Add(addressData);
        }
        S7AddressData[] result = new S7AddressData[array.size()];
        array.toArray(result);
        return result;
    }
}

