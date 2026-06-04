/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.Net.ReadWriteNetHelper;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.Helper.McAsciiHelper;
import HslCommunication.Profinet.Melsec.Helper.McHelper;
import HslCommunication.Profinet.Melsec.Helper.McType;
import HslCommunication.Profinet.Melsec.MelsecA3CNetOverTcp;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MelsecA3CNetHelper {
    public static byte[] PackCommand(MelsecA3CNetOverTcp plc, byte[] mcCommand) {
        return MelsecA3CNetHelper.PackCommand(plc, mcCommand, (byte)0);
    }

    public static byte[] PackCommand(MelsecA3CNetOverTcp plc, byte[] mcCommand, byte station) {
        MemoryStream ms = new MemoryStream();
        if (plc.Format != 3) {
            ms.WriteByte(5);
        } else {
            ms.WriteByte(2);
        }
        if (plc.Format == 2) {
            ms.WriteByte(48);
            ms.WriteByte(48);
        }
        ms.WriteByte(70);
        ms.WriteByte(57);
        ms.WriteByte(SoftBasic.BuildAsciiBytesFrom(station)[0]);
        ms.WriteByte(SoftBasic.BuildAsciiBytesFrom(station)[1]);
        ms.WriteByte(48);
        ms.WriteByte(48);
        ms.WriteByte(70);
        ms.WriteByte(70);
        ms.WriteByte(48);
        ms.WriteByte(48);
        ms.Write(mcCommand, 0, mcCommand.length);
        if (plc.Format == 3) {
            ms.WriteByte(3);
        }
        if (plc.SumCheck) {
            byte[] cmd = ms.ToArray();
            int sum = 0;
            for (int i = 1; i < cmd.length; ++i) {
                sum += cmd[i];
            }
            ms.WriteByte(SoftBasic.BuildAsciiBytesFrom((byte)sum)[0]);
            ms.WriteByte(SoftBasic.BuildAsciiBytesFrom((byte)sum)[1]);
        }
        if (plc.Format == 4) {
            ms.WriteByte(13);
            ms.WriteByte(10);
        }
        byte[] buffer = ms.ToArray();
        return buffer;
    }

    private static int GetErrorCodeOrDataStartIndex(MelsecA3CNetOverTcp plc) {
        int start = 11;
        switch (plc.Format) {
            case 1: {
                start = 11;
                break;
            }
            case 2: {
                start = 13;
                break;
            }
            case 3: {
                start = 15;
                break;
            }
            case 4: {
                start = 11;
            }
        }
        return start;
    }

    public static OperateResultExOne<byte[]> ExtraReadActualResponse(MelsecA3CNetOverTcp plc, byte[] response) {
        try {
            int start = MelsecA3CNetHelper.GetErrorCodeOrDataStartIndex(plc);
            if (plc.Format == 1 || plc.Format == 2 || plc.Format == 4) {
                if (response[0] == 21) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne<byte[]>(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (response[0] != 2) {
                    return new OperateResultExOne<byte[]>(response[0], "Read Faild:" + SoftBasic.GetAsciiStringRender(response));
                }
            } else if (plc.Format == 3) {
                String ending = new String(response, 11, 4, StandardCharsets.US_ASCII);
                if (ending.equals("QNAK")) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne<byte[]>(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (!ending.equals("QACK")) {
                    return new OperateResultExOne<byte[]>(response[0], "Read Faild:" + SoftBasic.GetAsciiStringRender(response));
                }
            }
            int end = -1;
            for (int i = start; i < response.length; ++i) {
                if (response[i] != 3) continue;
                end = i;
                break;
            }
            if (end == -1) {
                end = response.length;
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle(response, start, end - start));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("ExtraReadActualResponse Wrong:" + ex.getMessage() + "\r\nSource: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    private static OperateResult CheckWriteResponse(MelsecA3CNetOverTcp plc, byte[] response) {
        int start = MelsecA3CNetHelper.GetErrorCodeOrDataStartIndex(plc);
        try {
            if (plc.Format == 1 || plc.Format == 2) {
                if (response[0] == 21) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (response[0] != 6) {
                    return new OperateResultExOne(response[0], "Write Faild:" + SoftBasic.GetAsciiStringRender(response));
                }
            } else if (plc.Format == 3) {
                if (response[0] != 2) {
                    return new OperateResultExOne(response[0], "Write Faild:" + SoftBasic.GetAsciiStringRender(response));
                }
                String ending = new String(response, 11, 4, StandardCharsets.US_ASCII);
                if (ending.equals("QNAK")) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (!ending.equals("QACK")) {
                    return new OperateResultExOne(response[0], "Write Faild:" + SoftBasic.GetAsciiStringRender(response));
                }
            } else if (plc.Format == 4) {
                if (response[0] == 21) {
                    int errorCode = Convert.ToInt32(new String(response, start, 4, StandardCharsets.US_ASCII), 16);
                    return new OperateResultExOne(errorCode, MelsecHelper.GetErrorDescription(errorCode));
                }
                if (response[0] != 6) {
                    return new OperateResultExOne(response[0], "Write Faild:" + SoftBasic.GetAsciiStringRender(response));
                }
            }
            return OperateResult.CreateSuccessResult();
        }
        catch (Exception ex) {
            return new OperateResultExOne("CheckWriteResponse failed: " + ex.getMessage() + "\r\nContent: " + SoftBasic.GetAsciiStringRender(response));
        }
    }

    public static OperateResultExOne<byte[]> Read(MelsecA3CNetOverTcp plc, String address, short length) {
        short readLength;
        byte stat = plc.Station;
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
            address = (String)extraPara.Content2;
        }
        OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address, length, false);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        ArrayList<Byte> bytesContent = new ArrayList<Byte>();
        for (int alreadyFinished = 0; alreadyFinished < length; alreadyFinished += readLength) {
            readLength = (short)Math.min(length - alreadyFinished, McHelper.GetReadWordLength(McType.MCAscii));
            ((McAddressData)addressResult.Content).setLength(readLength);
            byte[] command = McAsciiHelper.BuildAsciiReadMcCoreCommand((McAddressData)addressResult.Content, false);
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(MelsecA3CNetHelper.PackCommand(plc, command, stat));
            if (!read.IsSuccess) {
                return read;
            }
            OperateResultExOne<byte[]> check = MelsecA3CNetHelper.ExtraReadActualResponse(plc, (byte[])read.Content);
            if (!check.IsSuccess) {
                return check;
            }
            Utilities.ArrayListAddArray(bytesContent, MelsecHelper.TransAsciiByteArrayToByteArray((byte[])check.Content));
            if (((McAddressData)addressResult.Content).getMcDataType().getDataType() == 0) {
                ((McAddressData)addressResult.Content).setAddressStart(((McAddressData)addressResult.Content).getAddressStart() + readLength);
                continue;
            }
            ((McAddressData)addressResult.Content).setAddressStart(((McAddressData)addressResult.Content).getAddressStart() + readLength * 16);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(bytesContent));
    }

    public static OperateResult Write(MelsecA3CNetOverTcp plc, String address, byte[] value) {
        byte stat = plc.Station;
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
            address = (String)extraPara.Content2;
        }
        OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address, 0, false);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        byte[] command = McAsciiHelper.BuildAsciiWriteWordCoreCommand((McAddressData)addressResult.Content, value);
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(MelsecA3CNetHelper.PackCommand(plc, command, stat));
        if (!read.IsSuccess) {
            return read;
        }
        return MelsecA3CNetHelper.CheckWriteResponse(plc, (byte[])read.Content);
    }

    public static OperateResultExOne<boolean[]> ReadBool(MelsecA3CNetOverTcp plc, String address, short length) {
        short readLength;
        byte stat = plc.Station;
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
            address = (String)extraPara.Content2;
        }
        OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address, length, true);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        ArrayList<Boolean> boolContent = new ArrayList<Boolean>();
        for (int alreadyFinished = 0; alreadyFinished < length; alreadyFinished += readLength) {
            readLength = (short)Math.min(length - alreadyFinished, McHelper.GetReadBoolLength(McType.MCAscii));
            ((McAddressData)addressResult.Content).setLength(readLength);
            byte[] command = McAsciiHelper.BuildAsciiReadMcCoreCommand((McAddressData)addressResult.Content, true);
            OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(MelsecA3CNetHelper.PackCommand(plc, command, stat));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> check = MelsecA3CNetHelper.ExtraReadActualResponse(plc, (byte[])read.Content);
            if (!check.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(check);
            }
            Utilities.ArrayListAddArray(boolContent, Utilities.getBoolArray((byte[])check.Content, (byte)48));
            ((McAddressData)addressResult.Content).setAddressStart(((McAddressData)addressResult.Content).getAddressStart() + readLength);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(boolContent));
    }

    public static OperateResult Write(MelsecA3CNetOverTcp plc, String address, boolean[] value) {
        if (plc.EnableWriteBitToWordRegister && address.contains(".")) {
            return ReadWriteNetHelper.WriteBoolWithWord(plc, address, value, 16);
        }
        byte stat = plc.Station;
        OperateResultExTwo<Integer, String> extraPara = HslHelper.ExtractParameter(address, "s");
        if (extraPara.IsSuccess) {
            stat = ((Integer)extraPara.Content1).byteValue();
            address = (String)extraPara.Content2;
        }
        OperateResultExOne<McAddressData> addressResult = McAddressData.ParseMelsecFrom(address, 0, true);
        if (!addressResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(addressResult);
        }
        byte[] command = McAsciiHelper.BuildAsciiWriteBitCoreCommand((McAddressData)addressResult.Content, value);
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(MelsecA3CNetHelper.PackCommand(plc, command, stat));
        if (!read.IsSuccess) {
            return read;
        }
        return MelsecA3CNetHelper.CheckWriteResponse(plc, (byte[])read.Content);
    }

    public static OperateResult RemoteRun(MelsecA3CNetOverTcp plc) {
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(MelsecA3CNetHelper.PackCommand(plc, "1001000000010000".getBytes(StandardCharsets.US_ASCII), plc.Station));
        if (!read.IsSuccess) {
            return read;
        }
        return MelsecA3CNetHelper.CheckWriteResponse(plc, (byte[])read.Content);
    }

    public static OperateResult RemoteStop(MelsecA3CNetOverTcp plc) {
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(MelsecA3CNetHelper.PackCommand(plc, "100200000001".getBytes(StandardCharsets.US_ASCII), plc.Station));
        if (!read.IsSuccess) {
            return read;
        }
        return MelsecA3CNetHelper.CheckWriteResponse(plc, (byte[])read.Content);
    }

    public static OperateResultExOne<String> ReadPlcType(MelsecA3CNetOverTcp plc) {
        OperateResultExOne<byte[]> read = plc.ReadFromCoreServer(MelsecA3CNetHelper.PackCommand(plc, "01010000".getBytes(StandardCharsets.US_ASCII), plc.Station));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> check = MelsecA3CNetHelper.ExtraReadActualResponse(plc, (byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(new String((byte[])check.Content, 0, 16, StandardCharsets.US_ASCII).trim());
    }
}

