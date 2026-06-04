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
import HslCommunication.Profinet.Omron.OmronFinsNetHelper;
import HslCommunication.Profinet.Omron.OmronPlcType;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class OmronHostLinkOverTcp
extends NetworkDeviceBase {
    public byte ICF = 0;
    public byte DA2 = 0;
    public byte SA2 = 0;
    public byte SID = 0;
    public byte ResponseWaitTime = (byte)48;
    public byte UnitNumber = 0;
    public int ReadSplits = 260;
    private OmronPlcType plcType = OmronPlcType.CSCJ;

    public OmronHostLinkOverTcp() {
        this.setByteTransform(new ReverseWordTransform());
        this.WordLength = 1;
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
        this.LogMsgFormatBinary = false;
    }

    public OmronHostLinkOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(13);
    }

    @Override
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return OmronHostLinkOverTcp.ResponseValidAnalysis(send, response);
    }

    public OmronPlcType getPlcType() {
        return this.plcType;
    }

    public void setPlcType(OmronPlcType plcType) {
        this.plcType = plcType;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = ((Integer)analysis.Content1).byteValue();
            address = (String)analysis.Content2;
        }
        OperateResultExOne<byte[][]> command = OmronFinsNetHelper.BuildReadCommand(this.plcType, address, length, false, this.ReadSplits);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> contentArray = new ArrayList<Byte>();
        for (int i = 0; i < ((byte[][])command.Content).length; ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommand(station, ((byte[][])command.Content)[i]));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            Utilities.ArrayListAddArray(contentArray, (byte[])read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    public OperateResultExOne<byte[]> Read(String[] address) {
        byte station = this.UnitNumber;
        if (address != null && address.length > 0) {
            OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address[0], "s", this.UnitNumber);
            if (analysis.IsSuccess) {
                station = ((Integer)analysis.Content1).byteValue();
                address[0] = (String)analysis.Content2;
            }
        }
        OperateResultExOne<ArrayList<byte[]>> command = OmronFinsNetHelper.BuildReadCommand(address, this.plcType);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> contentArray = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommand(station, (byte[])((ArrayList)command.Content).get(i)));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            Utilities.ArrayListAddArray(contentArray, (byte[])read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = ((Integer)analysis.Content1).byteValue();
            address = (String)analysis.Content2;
        }
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand(this.plcType, address, value, false);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommand(station, (byte[])command.Content));
        if (!read.IsSuccess) {
            return read;
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = ((Integer)analysis.Content1).byteValue();
            address = (String)analysis.Content2;
        }
        if (address.startsWith("DR") || address.startsWith("dr") || address.startsWith("IR") || address.startsWith("ir")) {
            if (!address.contains(".")) {
                address = address + ".0";
            }
            String stationAddress = station != this.UnitNumber ? "s=" + station + ";" : "";
            return HslHelper.ReadBool(this, stationAddress + address, length, 16, true);
        }
        OperateResultExOne<byte[][]> command = OmronFinsNetHelper.BuildReadCommand(this.plcType, address, length, true, this.ReadSplits);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Boolean> contentArray = new ArrayList<Boolean>();
        for (int i = 0; i < ((byte[][])command.Content).length; ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommand(station, ((byte[][])command.Content)[i]));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            if (((byte[])read.Content).length == 0) {
                return new OperateResultExOne<boolean[]>("Data is empty.");
            }
            for (int j = 0; j < ((byte[])read.Content).length; ++j) {
                contentArray.add(((byte[])read.Content)[j] != 0);
            }
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBools(contentArray));
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = HslHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = ((Integer)analysis.Content1).byteValue();
            address = (String)analysis.Content2;
        }
        if (address.startsWith("DR") || address.startsWith("dr") || address.startsWith("IR") || address.startsWith("ir")) {
            return new OperateResult("DR and IR address not support bit write");
        }
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand(this.plcType, address, OmronHostLinkOverTcp.TransBoolsArray(values), true);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommand(station, (byte[])command.Content));
        if (!read.IsSuccess) {
            return read;
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public String toString() {
        return "OmronHostLinkOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }

    private byte[] PackCommand(byte station, byte[] cmd) {
        cmd = SoftBasic.BytesToAsciiBytes(cmd);
        byte[] buffer = new byte[18 + cmd.length];
        buffer[0] = 64;
        buffer[1] = SoftBasic.BuildAsciiBytesFrom(station)[0];
        buffer[2] = SoftBasic.BuildAsciiBytesFrom(station)[1];
        buffer[3] = 70;
        buffer[4] = 65;
        buffer[5] = this.ResponseWaitTime;
        buffer[6] = SoftBasic.BuildAsciiBytesFrom(this.ICF)[0];
        buffer[7] = SoftBasic.BuildAsciiBytesFrom(this.ICF)[1];
        buffer[8] = SoftBasic.BuildAsciiBytesFrom(this.DA2)[0];
        buffer[9] = SoftBasic.BuildAsciiBytesFrom(this.DA2)[1];
        buffer[10] = SoftBasic.BuildAsciiBytesFrom(this.SA2)[0];
        buffer[11] = SoftBasic.BuildAsciiBytesFrom(this.SA2)[1];
        buffer[12] = SoftBasic.BuildAsciiBytesFrom(this.SID)[0];
        buffer[13] = SoftBasic.BuildAsciiBytesFrom(this.SID)[1];
        buffer[buffer.length - 2] = 42;
        buffer[buffer.length - 1] = 13;
        System.arraycopy(cmd, 0, buffer, 14, cmd.length);
        int tmp = buffer[0];
        for (int i = 1; i < buffer.length - 4; ++i) {
            tmp ^= buffer[i];
        }
        buffer[buffer.length - 4] = SoftBasic.BuildAsciiBytesFrom((byte)tmp)[0];
        buffer[buffer.length - 3] = SoftBasic.BuildAsciiBytesFrom((byte)tmp)[1];
        return buffer;
    }

    public static OperateResultExOne<byte[]> ResponseValidAnalysis(byte[] send, byte[] response) {
        if (response.length >= 27) {
            String commandReceive = new String(response, 15, 4, StandardCharsets.US_ASCII);
            String commandSend = new String(send, 14, 4, StandardCharsets.US_ASCII);
            if (!commandReceive.equals(commandSend)) {
                return new OperateResultExOne<byte[]>("Send Command [" + commandSend + "] not the same as receive command [" + commandReceive + "]");
            }
            int err = 0;
            try {
                err = Integer.parseInt(new String(response, 19, 4, StandardCharsets.US_ASCII), 16);
            }
            catch (Exception ex) {
                return new OperateResultExOne<byte[]>("Get error code failed: " + ex.getMessage() + "\r\nSource Data: " + SoftBasic.GetAsciiStringRender(response));
            }
            byte[] content = new byte[]{};
            if (response.length > 27) {
                content = SoftBasic.HexStringToBytes(new String(response, 23, response.length - 27, StandardCharsets.US_ASCII));
            }
            if (err > 0) {
                OperateResultExOne result = new OperateResultExOne();
                result.ErrorCode = err;
                result.Content = content;
                result.Message = OmronHostLinkOverTcp.GetErrorText(err);
            } else {
                if (new String(response, 15, 4, StandardCharsets.US_ASCII).equals("0104")) {
                    byte[] buffer = content.length > 0 ? new byte[content.length * 2 / 3] : new byte[]{};
                    for (int i = 0; i < content.length / 3; ++i) {
                        buffer[i * 2 + 0] = content[i * 3 + 1];
                        buffer[i * 2 + 1] = content[i * 3 + 2];
                    }
                    content = buffer;
                }
                return OperateResultExOne.CreateSuccessResult(content);
            }
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError() + " Source Data: " + SoftBasic.ByteToHexString(response, ' '));
    }

    public static String GetErrorText(int error) {
        switch (error) {
            case 1: {
                return "Service was canceled.";
            }
            case 257: {
                return "Local node is not participating in the network.";
            }
            case 258: {
                return "Token does not arrive.";
            }
            case 259: {
                return "Send was not possible during the specified number of retries.";
            }
            case 260: {
                return "Cannot send because maximum number of event frames exceeded.";
            }
            case 261: {
                return "Node address setting error occurred.";
            }
            case 262: {
                return "The same node address has been set twice in the same network.";
            }
            case 513: {
                return "The destination node is not in the network.";
            }
            case 514: {
                return "There is no Unit with the specified unit address.";
            }
            case 515: {
                return "The third node does not exist.";
            }
            case 516: {
                return "The destination node is busy.";
            }
            case 517: {
                return "The message was destroyed by noise";
            }
            case 769: {
                return "An error occurred in the communications controller.";
            }
            case 770: {
                return "A CPU error occurred in the destination CPU Unit.";
            }
            case 771: {
                return "A response was not returned because an error occurred in the Board.";
            }
            case 772: {
                return "The unit number was set incorrectly";
            }
            case 1025: {
                return "The Unit/Board does not support the specified command code.";
            }
            case 1026: {
                return "The command cannot be executed because the model or version is incorrect";
            }
            case 1281: {
                return "The destination network or node address is not set in the routing tables.";
            }
            case 1282: {
                return "Relaying is not possible because there are no routing tables";
            }
            case 1283: {
                return "There is an error in the routing tables.";
            }
            case 1284: {
                return "An attempt was made to send to a network that was over 3 networks away";
            }
            case 4097: {
                return "The command is longer than the maximum permissible length.";
            }
            case 4098: {
                return "The command is shorter than the minimum permissible length.";
            }
            case 4099: {
                return "The designated number of elements differs from the number of write data items.";
            }
            case 4100: {
                return "An incorrect format was used.";
            }
            case 4101: {
                return "Either the relay table in the local node or the local network table in the relay node is incorrect.";
            }
            case 4353: {
                return "The specified word does not exist in the memory area or there is no EM Area.";
            }
            case 4354: {
                return "The access size specification is incorrect or an odd word address is specified.";
            }
            case 4355: {
                return "The start address in command process is beyond the accessible area";
            }
            case 4356: {
                return "The end address in command process is beyond the accessible area.";
            }
            case 4358: {
                return "FFFF hex was not specified.";
            }
            case 4361: {
                return "A large\u2013small relationship in the elements in the command data is incorrect.";
            }
            case 4363: {
                return "The response format is longer than the maximum permissible length.";
            }
            case 4364: {
                return "There is an error in one of the parameter settings.";
            }
            case 8194: {
                return "The program area is protected.";
            }
            case 8195: {
                return "A table has not been registered.";
            }
            case 8196: {
                return "The search data does not exist.";
            }
            case 8197: {
                return "A non-existing program number has been specified.";
            }
            case 8198: {
                return "The file does not exist at the specified file device.";
            }
            case 8199: {
                return "A data being compared is not the same.";
            }
            case 8449: {
                return "The specified area is read-only.";
            }
            case 8450: {
                return "The program area is protected.";
            }
            case 8451: {
                return "The file cannot be created because the limit has been exceeded.";
            }
            case 8453: {
                return "A non-existing program number has been specified.";
            }
            case 8454: {
                return "The file does not exist at the specified file device.";
            }
            case 8455: {
                return "A file with the same name already exists in the specified file device.";
            }
            case 8456: {
                return "The change cannot be made because doing so would create a problem.";
            }
            case 8705: 
            case 8706: 
            case 8712: {
                return "The mode is incorrect.";
            }
            case 8707: {
                return "The PLC is in PROGRAM mode.";
            }
            case 8708: {
                return "The PLC is in DEBUG mode.";
            }
            case 8709: {
                return "The PLC is in MONITOR mode.";
            }
            case 8710: {
                return "The PLC is in RUN mode.";
            }
            case 8711: {
                return "The specified node is not the polling node.";
            }
            case 8961: {
                return "The specified memory does not exist as a file device.";
            }
            case 8962: {
                return "There is no file memory.";
            }
            case 8963: {
                return "There is no clock.";
            }
            case 9217: {
                return "The data link tables have not been registered or they contain an error.";
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static byte[] TransBoolsArray(boolean[] values) {
        byte[] buffer = new byte[values.length];
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = values[i] ? (byte)1 : 0;
        }
        return buffer;
    }
}

