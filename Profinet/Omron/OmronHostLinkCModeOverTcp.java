/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Omron;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.SpecifiedCharacterMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.ReverseWordTransform;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import java.nio.charset.StandardCharsets;

public class OmronHostLinkCModeOverTcp
extends NetworkDeviceBase {
    public byte UnitNumber = 0;

    public OmronHostLinkCModeOverTcp() {
        this.setByteTransform(new ReverseWordTransform());
        this.WordLength = 1;
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
        this.getByteTransform().setIsStringReverse(true);
    }

    public OmronHostLinkCModeOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(13);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = ((Integer)analysis.Content1).byteValue();
            address = (String)analysis.Content2;
        }
        OperateResultExOne<byte[]> command = OmronHostLinkCModeOverTcp.BuildReadCommand(address, length, false);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(OmronHostLinkCModeOverTcp.PackCommand((byte[])command.Content, station));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> valid = OmronHostLinkCModeOverTcp.ResponseValidAnalysis((byte[])read.Content, true);
        if (!valid.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(valid);
        }
        return OperateResultExOne.CreateSuccessResult(valid.Content);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = ((Integer)analysis.Content1).byteValue();
            address = (String)analysis.Content2;
        }
        OperateResultExOne<byte[]> command = OmronHostLinkCModeOverTcp.BuildWriteWordCommand(address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(OmronHostLinkCModeOverTcp.PackCommand((byte[])command.Content, station));
        if (!read.IsSuccess) {
            return read;
        }
        OperateResultExOne<byte[]> valid = OmronHostLinkCModeOverTcp.ResponseValidAnalysis((byte[])read.Content, false);
        if (!valid.IsSuccess) {
            return valid;
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return HslHelper.ReadBool(this, address, length, 16, true);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return HslHelper.WriteBool(this, address, value, 16, true);
    }

    public OperateResultExOne<String> ReadPlcModel() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(OmronHostLinkCModeOverTcp.PackCommand("MM".getBytes(StandardCharsets.US_ASCII), this.UnitNumber));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        int err = Integer.parseInt(new String((byte[])read.Content, 5, 2, StandardCharsets.US_ASCII), 16);
        if (err > 0) {
            return new OperateResultExOne<String>(err, "Unknown Error");
        }
        String model = new String((byte[])read.Content, 7, 2, StandardCharsets.US_ASCII);
        return OmronHostLinkCModeOverTcp.GetModelText(model);
    }

    public static OperateResultExTwo<String, String> AnalysisAddress(String address, boolean isBit, boolean isRead) {
        OperateResultExTwo<String, String> result = new OperateResultExTwo<String, String>();
        try {
            String[] splits;
            switch (address.charAt(0)) {
                case 'D': 
                case 'd': {
                    result.Content1 = isRead ? "RD" : "WD";
                    break;
                }
                case 'C': 
                case 'c': {
                    result.Content1 = isRead ? "RR" : "WR";
                    break;
                }
                case 'H': 
                case 'h': {
                    result.Content1 = isRead ? "RH" : "WH";
                    break;
                }
                case 'A': 
                case 'a': {
                    result.Content1 = isRead ? "RJ" : "WJ";
                    break;
                }
                case 'E': 
                case 'e': {
                    splits = address.split("\\.");
                    int block = Integer.parseInt(splits[0].substring(1), 16);
                    result.Content1 = (isRead ? "RE" : "WE") + new String(SoftBasic.BuildAsciiBytesFrom((byte)block), StandardCharsets.US_ASCII);
                    break;
                }
                default: {
                    throw new Exception(StringResources.Language.NotSupportedDataType());
                }
            }
            if (address.charAt(0) == 'E' || address.charAt(0) == 'e') {
                splits = address.split("\\.");
                if (!isBit) {
                    int addr = Integer.parseInt(splits[1]);
                    result.Content2 = String.format("%04d", addr);
                }
            } else if (!isBit) {
                int addr = Integer.parseInt(address.substring(1));
                result.Content2 = String.format("%04d", addr);
            }
        }
        catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }
        result.IsSuccess = true;
        return result;
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(String address, short length, boolean isBit) {
        OperateResultExTwo<String, String> analysis = OmronHostLinkCModeOverTcp.AnalysisAddress(address, isBit, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        StringBuilder sb = new StringBuilder();
        sb.append((String)analysis.Content1);
        sb.append((String)analysis.Content2);
        sb.append(String.format("%04d", length));
        return OperateResultExOne.CreateSuccessResult(sb.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> BuildWriteWordCommand(String address, byte[] value) {
        OperateResultExTwo<String, String> analysis = OmronHostLinkCModeOverTcp.AnalysisAddress(address, false, false);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        StringBuilder sb = new StringBuilder();
        sb.append((String)analysis.Content1);
        sb.append((String)analysis.Content2);
        sb.append(SoftBasic.ByteToHexString(value));
        return OperateResultExOne.CreateSuccessResult(sb.toString().getBytes(StandardCharsets.US_ASCII));
    }

    public static OperateResultExOne<byte[]> ResponseValidAnalysis(byte[] response, boolean isRead) {
        if (response.length >= 11) {
            try {
                int err = Integer.parseInt(new String(response, 5, 2, StandardCharsets.US_ASCII), 16);
                byte[] Content = null;
                if (response.length > 11) {
                    Content = SoftBasic.HexStringToBytes(new String(response, 7, response.length - 11));
                }
                if (err > 0) {
                    OperateResultExOne<byte[]> result = new OperateResultExOne<byte[]>();
                    result.ErrorCode = err;
                    result.Content = Content;
                    return result;
                }
                return OperateResultExOne.CreateSuccessResult(Content);
            }
            catch (Exception ex) {
                return new OperateResultExOne<byte[]>("ResponseValidAnalysis failed: " + ex.getMessage() + " Source: " + SoftBasic.ByteToHexString(response, ' '));
            }
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    public static byte[] PackCommand(byte[] cmd, byte unitNumber) {
        byte[] buffer = new byte[7 + cmd.length];
        buffer[0] = 64;
        buffer[1] = SoftBasic.BuildAsciiBytesFrom(unitNumber)[0];
        buffer[2] = SoftBasic.BuildAsciiBytesFrom(unitNumber)[1];
        buffer[buffer.length - 2] = 42;
        buffer[buffer.length - 1] = 13;
        System.arraycopy(cmd, 0, buffer, 3, cmd.length);
        int tmp = buffer[0];
        for (int i = 1; i < buffer.length - 4; ++i) {
            tmp ^= buffer[i];
        }
        buffer[buffer.length - 4] = SoftBasic.BuildAsciiBytesFrom((byte)tmp)[0];
        buffer[buffer.length - 3] = SoftBasic.BuildAsciiBytesFrom((byte)tmp)[1];
        String output = new String(buffer, StandardCharsets.US_ASCII);
        return buffer;
    }

    public static OperateResultExOne<String> GetModelText(String model) {
        switch (model) {
            case "30": {
                return OperateResultExOne.CreateSuccessResult("CS/CJ");
            }
            case "01": {
                return OperateResultExOne.CreateSuccessResult("C250");
            }
            case "02": {
                return OperateResultExOne.CreateSuccessResult("C500");
            }
            case "03": {
                return OperateResultExOne.CreateSuccessResult("C120/C50");
            }
            case "09": {
                return OperateResultExOne.CreateSuccessResult("C250F");
            }
            case "0A": {
                return OperateResultExOne.CreateSuccessResult("C500F");
            }
            case "0B": {
                return OperateResultExOne.CreateSuccessResult("C120F");
            }
            case "0E": {
                return OperateResultExOne.CreateSuccessResult("C2000");
            }
            case "10": {
                return OperateResultExOne.CreateSuccessResult("C1000H");
            }
            case "11": {
                return OperateResultExOne.CreateSuccessResult("C2000H/CQM1/CPM1");
            }
            case "12": {
                return OperateResultExOne.CreateSuccessResult("C20H/C28H/C40H, C200H, C200HS, C200HX/HG/HE (-ZE)");
            }
            case "20": {
                return OperateResultExOne.CreateSuccessResult("CV500");
            }
            case "21": {
                return OperateResultExOne.CreateSuccessResult("CV1000");
            }
            case "22": {
                return OperateResultExOne.CreateSuccessResult("CV2000");
            }
            case "40": {
                return OperateResultExOne.CreateSuccessResult("CVM1-CPU01-E");
            }
            case "41": {
                return OperateResultExOne.CreateSuccessResult("CVM1-CPU11-E");
            }
            case "42": {
                return OperateResultExOne.CreateSuccessResult("CVM1-CPU21-E");
            }
        }
        return new OperateResultExOne<String>("Unknown model, model code:" + model);
    }
}

