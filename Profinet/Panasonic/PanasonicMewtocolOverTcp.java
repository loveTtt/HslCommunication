/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Panasonic;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.SpecifiedCharacterMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.DataFormat;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Panasonic.PanasonicHelper;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.util.ArrayList;

public class PanasonicMewtocolOverTcp
extends NetworkDeviceBase {
    private int Station = -18;

    public PanasonicMewtocolOverTcp(int station) {
        this.setByteTransform(new RegularByteTransform());
        this.Station = station;
        this.getByteTransform().setDataFormat(DataFormat.DCBA);
        this.LogMsgFormatBinary = false;
    }

    public PanasonicMewtocolOverTcp(String ipAddress, int port, byte station) {
        this(station);
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(13);
    }

    public int getStation() {
        return this.Station;
    }

    public void setStation(int station) {
        this.Station = station;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        OperateResultExOne<ArrayList<byte[]>> command = PanasonicHelper.BuildReadCommand(station, address, length, false);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return read;
            }
            OperateResultExOne<byte[]> extraData = PanasonicHelper.ExtraActualData((byte[])read.Content);
            if (!extraData.IsSuccess) {
                return extraData;
            }
            Utilities.ArrayListAddArray(list, (byte[])extraData.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(list));
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        OperateResultExOne<byte[]> command = PanasonicHelper.BuildWriteCommand(station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return PanasonicHelper.ExtraActualData((byte[])read.Content);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        if (PanasonicHelper.CheckBoolOnWordAddress(address)) {
            return HslHelper.ReadBool(this, address, length, 16, false);
        }
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extractParameter = HslHelper.ExtractParameter(address, "s", this.getStation());
        if (extractParameter.IsSuccess) {
            station = (Integer)extractParameter.Content1;
            address = (String)extractParameter.Content2;
        }
        OperateResultExTwo<String, Integer> analysis = PanasonicHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<ArrayList<byte[]>> command = PanasonicHelper.BuildReadCommand(station, address, length, true);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        ArrayList<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < ((ArrayList)command.Content).size(); ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])((ArrayList)command.Content).get(i));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            OperateResultExOne<byte[]> extra = PanasonicHelper.ExtraActualData((byte[])read.Content);
            if (!extra.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(extra);
            }
            Utilities.ArrayListAddArray(list, (byte[])extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(Utilities.ToByteArray(list)), (Integer)analysis.Content2 % 16, length));
    }

    @Override
    public OperateResultExOne<Boolean> ReadBool(String address) {
        if (PanasonicHelper.CheckBoolOnWordAddress(address)) {
            return ByteTransformHelper.GetBoolResultFromArray(HslHelper.ReadBool(this, address, (short)1, 16, false));
        }
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        OperateResultExOne<byte[]> command = PanasonicHelper.BuildReadOneCoil(station, address);
        if (!command.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(command);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return PanasonicHelper.ExtraActualBool((byte[])read.Content);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        if (PanasonicHelper.CheckBoolOnWordAddress(address)) {
            return HslHelper.WriteBool(this, address, values, 16, false);
        }
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        OperateResultExTwo<String, Integer> analysis = PanasonicHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if ((Integer)analysis.Content2 % 16 != 0) {
            return new OperateResult(StringResources.Language.PanasonicAddressBitStartMulti16());
        }
        if (values.length % 16 != 0) {
            return new OperateResult(StringResources.Language.PanasonicBoolLengthMulti16());
        }
        byte[] buffer = SoftBasic.BoolArrayToByte(values);
        OperateResultExOne<byte[]> command = PanasonicHelper.BuildWriteCommand(station, address, buffer);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return PanasonicHelper.ExtraActualData((byte[])read.Content);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        if (PanasonicHelper.CheckBoolOnWordAddress(address)) {
            return HslHelper.WriteBool(this, address, new boolean[]{value}, 16, false);
        }
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = (Integer)extra.Content1;
            address = (String)extra.Content2;
        }
        OperateResultExOne<byte[]> command = PanasonicHelper.BuildWriteOneCoil(station, address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])command.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return PanasonicHelper.ExtraActualData((byte[])read.Content);
    }

    @Override
    public String toString() {
        return "PanasonicMewtocolOverTcp[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

