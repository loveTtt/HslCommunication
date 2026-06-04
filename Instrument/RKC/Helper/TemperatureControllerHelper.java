/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Instrument.RKC.Helper;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Net.IReadWriteDevice;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.Environment;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.List;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Utilities;

public class TemperatureControllerHelper {
    public static OperateResultExOne<byte[]> BuildReadCommand(byte station, String address) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = ((Integer)extra.Content1).byteValue();
            address = (String)extra.Content2;
        }
        if (station >= 100) {
            return new OperateResultExOne<byte[]>("Station must less than 100");
        }
        try {
            byte[] buffer = new byte[4 + address.length()];
            buffer[0] = 4;
            Utilities.ByteArrayCopyTo(Encoding.ASCII.GetBytes(String.format("%02d", station)), buffer, 1);
            Utilities.ByteArrayCopyTo(Encoding.ASCII.GetBytes(address), buffer, 3);
            buffer[buffer.length - 1] = 5;
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(byte station, String address, double value) {
        OperateResultExTwo<Integer, String> extra = HslHelper.ExtractParameter(address, "s", station);
        if (extra.IsSuccess) {
            station = ((Integer)extra.Content1).byteValue();
            address = (String)extra.Content2;
        }
        if (station >= 100) {
            return new OperateResultExOne<byte[]>("Station must less than 100");
        }
        if (String.valueOf(value).length() > 6) {
            return new OperateResultExOne<byte[]>("The data consists of up to 6 characters");
        }
        try {
            List<Byte> list = new List<Byte>();
            list.Add((byte)4);
            list.Add((Byte)((Object)Encoding.ASCII.GetBytesList(String.format("%02d", station))));
            list.Add((byte)2);
            list.Add((Byte)((Object)Encoding.ASCII.GetBytesList(address)));
            list.Add((Byte)((Object)Encoding.ASCII.GetBytesList(String.valueOf(value))));
            list.Add((byte)3);
            int bcc = ((Byte)list.get(4)).byteValue();
            for (int i = 5; i < list.size(); ++i) {
                bcc ^= ((Byte)list.get(i)).byteValue();
            }
            list.Add((byte)bcc);
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(list));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    public static OperateResultExOne<Double> ReadDouble(IReadWriteDevice device, byte station, String address) {
        OperateResultExOne<byte[]> build = TemperatureControllerHelper.BuildReadCommand(station, address);
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        if (((byte[])read.Content)[0] != 2) {
            return new OperateResultExOne<Double>("STX check failed: " + SoftBasic.ByteToHexString((byte[])read.Content, ' '));
        }
        try {
            return OperateResultExOne.CreateSuccessResult(Double.parseDouble(Encoding.ASCII.GetString((byte[])read.Content, 3, ((byte[])read.Content).length - 5)));
        }
        catch (Exception ex) {
            return new OperateResultExOne<Double>(ex.getMessage() + Environment.NewLine + "Source: " + SoftBasic.ByteToHexString((byte[])read.Content, ' '));
        }
    }

    public static OperateResult Write(IReadWriteDevice device, byte station, String address, double value) {
        OperateResultExOne<byte[]> build = TemperatureControllerHelper.BuildWriteCommand(station, address, value);
        if (!build.IsSuccess) {
            return build;
        }
        OperateResultExOne<byte[]> read = device.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return read;
        }
        if (((byte[])read.Content)[0] != 6) {
            return new OperateResultExOne("ACK check failed: " + SoftBasic.ByteToHexString((byte[])read.Content, ' '));
        }
        return OperateResult.CreateSuccessResult();
    }
}

