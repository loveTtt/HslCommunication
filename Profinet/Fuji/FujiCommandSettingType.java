/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Fuji;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.FujiCommandSettingTypeAddress;
import HslCommunication.Core.IMessage.FujiCommandSettingTypeMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.ByteTransformHelper;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Transfer.ReverseBytesTransform;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

public class FujiCommandSettingType
extends NetworkDeviceBase {
    private boolean dataSwap = false;

    public FujiCommandSettingType() {
        this.setByteTransform(new ReverseBytesTransform());
        this.WordLength = (short)2;
    }

    public FujiCommandSettingType(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new FujiCommandSettingTypeMessage();
    }

    @Override
    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return FujiCommandSettingType.UnpackResponseContentHelper(send, response);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<byte[]> bulid = FujiCommandSettingType.BuildReadCommand(address, length);
        if (!bulid.IsSuccess) {
            return bulid;
        }
        return this.ReadFromCoreServer((byte[])bulid.Content);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> bulid = FujiCommandSettingType.BuildWriteCommand(address, value);
        if (!bulid.IsSuccess) {
            return bulid;
        }
        return this.ReadFromCoreServer((byte[])bulid.Content);
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        return ByteTransformHelper.GetByteResultFromBytes(this.Read(address, (short)1), this.getByteTransform());
    }

    public OperateResult Write(String address, byte value) {
        return this.Write(address, new byte[]{value});
    }

    public boolean getDataSwap() {
        return this.dataSwap;
    }

    public void setDataSwap(boolean value) {
        this.dataSwap = value;
        if (value) {
            this.setByteTransform(new RegularByteTransform());
        } else {
            this.setByteTransform(new ReverseBytesTransform());
        }
    }

    @Override
    public String toString() {
        return "FujiCommandSettingType[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }

    public static OperateResultExOne<byte[]> BuildReadCommand(String address, short length) {
        OperateResultExOne<FujiCommandSettingTypeAddress> analysisAddress = FujiCommandSettingTypeAddress.ParseFrom(address, length);
        if (!analysisAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysisAddress);
        }
        byte[] buffer = new byte[]{0, 0, 0, ((FujiCommandSettingTypeAddress)analysisAddress.Content).DataCode, 4, BitConverter.GetBytes(((FujiCommandSettingTypeAddress)analysisAddress.Content).getAddressStart())[0], BitConverter.GetBytes(((FujiCommandSettingTypeAddress)analysisAddress.Content).getAddressStart())[1], BitConverter.GetBytes(((FujiCommandSettingTypeAddress)analysisAddress.Content).getLength())[0], BitConverter.GetBytes(((FujiCommandSettingTypeAddress)analysisAddress.Content).getLength())[1]};
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand(String address, byte[] value) {
        OperateResultExOne<FujiCommandSettingTypeAddress> analysisAddress = FujiCommandSettingTypeAddress.ParseFrom(address, 0);
        if (!analysisAddress.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysisAddress);
        }
        byte[] buffer = new byte[9 + value.length];
        buffer[0] = 1;
        buffer[1] = 0;
        buffer[2] = 0;
        buffer[3] = ((FujiCommandSettingTypeAddress)analysisAddress.Content).DataCode;
        buffer[4] = (byte)(4 + value.length);
        buffer[5] = BitConverter.GetBytes(((FujiCommandSettingTypeAddress)analysisAddress.Content).getAddressStart())[0];
        buffer[6] = BitConverter.GetBytes(((FujiCommandSettingTypeAddress)analysisAddress.Content).getAddressStart())[1];
        buffer[7] = BitConverter.GetBytes(((FujiCommandSettingTypeAddress)analysisAddress.Content).getLength())[0];
        buffer[8] = BitConverter.GetBytes(((FujiCommandSettingTypeAddress)analysisAddress.Content).getLength())[0];
        Utilities.ByteArrayCopyTo(value, buffer, 9);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static String GetErrorText(int error) {
        switch (error) {
            case 18: {
                return "Write of data to the program area";
            }
            case 32: {
                return "Non-existing CMND code";
            }
            case 33: {
                return "Input data is not in the order of data corresponding to CMND";
            }
            case 34: {
                return "Operation only from the loader is effective. Operation from any other node is disabled";
            }
            case 36: {
                return "A non-existing module has been specified";
            }
            case 50: {
                return "An address out of the memory size has been specified";
            }
        }
        return StringResources.Language.UnknownError();
    }

    public static OperateResultExOne<byte[]> UnpackResponseContentHelper(byte[] send, byte[] response) {
        try {
            if (response[1] != 0) {
                return new OperateResultExOne<byte[]>(FujiCommandSettingType.GetErrorText(response[1]));
            }
            if (response[0] == 1) {
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
            }
            if (response.length < 10) {
                return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + "10, Source: " + SoftBasic.ByteToHexString(response, ' '));
            }
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 10));
        }
        catch (Exception ex) {
            return new OperateResultExOne<byte[]>("UnpackResponseContentHelper failed: " + ex.getMessage() + " Source: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }
}

