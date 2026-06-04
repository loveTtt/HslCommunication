/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.ModBus.Helper;

import HslCommunication.BasicFramework.SoftBuffer;
import HslCommunication.Core.Address.ModbusAddress;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.ModBus.ModbusInfo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

public class ModbusDataPool {
    private byte station = 1;
    private SoftBuffer coilBuffer = new SoftBuffer(65536);
    private SoftBuffer inputBuffer = new SoftBuffer(65536);
    private SoftBuffer registerBuffer = new SoftBuffer(131072);
    private SoftBuffer inputRegisterBuffer = new SoftBuffer(131072);
    private final int DataPoolLength = 65536;

    public ModbusDataPool(int station) {
        this.registerBuffer.setIsBoolReverseByWord(true);
        this.inputRegisterBuffer.setIsBoolReverseByWord(true);
        this.station = (byte) station;
    }

    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, this.station, true, (byte)3);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((ModbusAddress)analysis.Content).getFunction() == 3) {
            return OperateResultExOne.CreateSuccessResult(this.registerBuffer.GetBytes(((ModbusAddress)analysis.Content).getAddress() * 2, length * 2));
        }
        if (((ModbusAddress)analysis.Content).getFunction() == 4) {
            return OperateResultExOne.CreateSuccessResult(this.inputRegisterBuffer.GetBytes(((ModbusAddress)analysis.Content).getAddress() * 2, length * 2));
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
    }

    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, this.station, true, (byte)3);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((ModbusAddress)analysis.Content).getFunction() == 3 || ((ModbusAddress)analysis.Content).getFunction() == 6 || ((ModbusAddress)analysis.Content).getFunction() == 16) {
            this.registerBuffer.SetBytes(value, ((ModbusAddress)analysis.Content).getAddress() * 2);
            return OperateResult.CreateSuccessResult();
        }
        if (((ModbusAddress)analysis.Content).getFunction() == 4) {
            this.inputRegisterBuffer.SetBytes(value, ((ModbusAddress)analysis.Content).getAddress() * 2);
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResultExOne(StringResources.Language.NotSupportedDataType());
    }

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        if (address.indexOf(46) < 0) {
            OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, this.station, true, (byte)1);
            if (!analysis.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(analysis);
            }
            if (((ModbusAddress)analysis.Content).getFunction() == 1 || ((ModbusAddress)analysis.Content).getFunction() == 5 || ((ModbusAddress)analysis.Content).getFunction() == 15) {
                return OperateResultExOne.CreateSuccessResult(Utilities.getBoolArray(this.coilBuffer.GetBytes(((ModbusAddress)analysis.Content).getAddress(), length)));
            }
            if (((ModbusAddress)analysis.Content).getFunction() == 2) {
                return OperateResultExOne.CreateSuccessResult(Utilities.getBoolArray(this.inputBuffer.GetBytes(((ModbusAddress)analysis.Content).getAddress(), length)));
            }
            return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
        }
        try {
            int bitIndex = Integer.parseInt(address.substring(address.indexOf(46) + 1));
            address = address.substring(0, address.indexOf(46));
            OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, this.station, true, (byte)3);
            if (!analysis.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(analysis);
            }
            bitIndex = ((ModbusAddress)analysis.Content).getAddress() * 16 + bitIndex;
            if (((ModbusAddress)analysis.Content).getFunction() == 3) {
                return OperateResultExOne.CreateSuccessResult(this.registerBuffer.GetBool(bitIndex, length));
            }
            if (((ModbusAddress)analysis.Content).getFunction() == 4) {
                return OperateResultExOne.CreateSuccessResult(this.inputRegisterBuffer.GetBool(bitIndex, length));
            }
            return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExOne<boolean[]>(ex.getMessage());
        }
    }

    public OperateResult Write(String address, boolean[] value) {
        if (address.indexOf(46) < 0) {
            OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, this.station, true, (byte)1);
            if (!analysis.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(analysis);
            }
            if (((ModbusAddress)analysis.Content).getFunction() == 1 || ((ModbusAddress)analysis.Content).getFunction() == 15 || ((ModbusAddress)analysis.Content).getFunction() == 5) {
                this.coilBuffer.SetBytes(Utilities.ToByteArray(value), ((ModbusAddress)analysis.Content).getAddress());
                return OperateResult.CreateSuccessResult();
            }
            if (((ModbusAddress)analysis.Content).getFunction() == 2) {
                this.inputBuffer.SetBytes(Utilities.ToByteArray(value), ((ModbusAddress)analysis.Content).getAddress());
                return OperateResult.CreateSuccessResult();
            }
            return new OperateResultExOne(StringResources.Language.NotSupportedDataType());
        }
        try {
            int bitIndex = Integer.parseInt(address.substring(address.indexOf(46) + 1));
            address = address.substring(0, address.indexOf(46));
            OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, this.station, true, (byte)3);
            if (!analysis.IsSuccess) {
                return analysis;
            }
            bitIndex = ((ModbusAddress)analysis.Content).getAddress() * 16 + bitIndex;
            if (((ModbusAddress)analysis.Content).getFunction() == 3) {
                this.registerBuffer.SetBool(value, bitIndex);
                return OperateResult.CreateSuccessResult();
            }
            if (((ModbusAddress)analysis.Content).getFunction() == 4) {
                this.inputRegisterBuffer.SetBool(value, bitIndex);
                return OperateResult.CreateSuccessResult();
            }
            return new OperateResult(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResult(ex.getMessage());
        }
    }

    public boolean ReadCoil(String address) {
        int add = Integer.parseInt(address);
        return this.coilBuffer.GetByte(add) != 0;
    }

    public boolean[] ReadCoil(String address, short length) {
        int add = Integer.parseInt(address);
        return Utilities.getBoolArray(this.coilBuffer.GetBytes(add, length));
    }

    public void WriteCoil(String address, boolean data) {
        int add = Integer.parseInt(address);
        this.coilBuffer.SetValue((byte)(data ? 1 : 0), add);
    }

    public void WriteCoil(String address, boolean[] data) {
        if (data == null) {
            return;
        }
        int add = Integer.parseInt(address);
        this.coilBuffer.SetBytes(Utilities.ToByteArray(data), add);
    }

    public boolean ReadDiscrete(String address) {
        int add = Integer.parseInt(address);
        return this.inputBuffer.GetByte(add) != 0;
    }

    public boolean[] ReadDiscrete(String address, short length) {
        int add = Integer.parseInt(address);
        return Utilities.getBoolArray(this.inputBuffer.GetBytes(add, length));
    }

    public void WriteDiscrete(String address, boolean data) {
        int add = Integer.parseInt(address);
        this.inputBuffer.SetValue((byte)(data ? 1 : 0), add);
    }

    public void WriteDiscrete(String address, boolean[] data) {
        if (data == null) {
            return;
        }
        int add = Integer.parseInt(address);
        this.inputBuffer.SetBytes(Utilities.ToByteArray(data), add);
    }

    public byte[] SaveToBytes() {
        byte[] buffer = new byte[393216];
        System.arraycopy(this.coilBuffer.GetBytes(), 0, buffer, 0, 65536);
        System.arraycopy(this.inputBuffer.GetBytes(), 0, buffer, 65536, 65536);
        System.arraycopy(this.registerBuffer.GetBytes(), 0, buffer, 131072, 131072);
        System.arraycopy(this.inputRegisterBuffer.GetBytes(), 0, buffer, 262144, 131072);
        return buffer;
    }

    public void LoadFromBytes(byte[] content, int index) {
        this.coilBuffer.SetBytes(content, 0 + index, 0, 65536);
        this.inputBuffer.SetBytes(content, 65536 + index, 0, 65536);
        this.registerBuffer.SetBytes(content, 131072 + index, 0, 131072);
        this.inputRegisterBuffer.SetBytes(content, 262144 + index, 0, 131072);
    }
}

