/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.MelsecQnA3EBinaryMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.Melsec.Helper.IReadWriteMc;
import HslCommunication.Profinet.Melsec.Helper.McBinaryHelper;
import HslCommunication.Profinet.Melsec.Helper.McHelper;
import HslCommunication.Profinet.Melsec.Helper.McType;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.Profinet.Melsec.MelsecMcDataType;
import HslCommunication.StringResources;

public class MelsecMcRNet
extends NetworkDeviceBase
implements IReadWriteMc {
    private byte NetworkNumber = 0;
    private byte NetworkStationNumber = 0;

    public MelsecMcRNet() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
    }

    public MelsecMcRNet(String ipAddress, int port) {
        this.WordLength = 1;
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.setByteTransform(new RegularByteTransform());
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new MelsecQnA3EBinaryMessage();
    }

    @Override
    public McType getMcType() {
        return McType.McRBinary;
    }

    @Override
    public byte getNetworkNumber() {
        return this.NetworkNumber;
    }

    @Override
    public void setNetworkNumber(byte networkNumber) {
        this.NetworkNumber = networkNumber;
    }

    @Override
    public byte getNetworkStationNumber() {
        return this.NetworkStationNumber;
    }

    @Override
    public void setNetworkStationNumber(byte networkStationNumber) {
        this.NetworkStationNumber = networkStationNumber;
    }

    @Override
    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length, boolean isBit) {
        return McAddressData.ParseMelsecRFrom(address, length);
    }

    @Override
    public byte[] PackCommandWithHeader(byte[] command) {
        return McBinaryHelper.PackMcCommand(command, this.NetworkNumber, this.NetworkStationNumber);
    }

    @Override
    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        OperateResult check = McBinaryHelper.CheckResponseContentHelper(response);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 11));
    }

    @Override
    public byte[] ExtractActualData(byte[] response, boolean isBit) {
        return McBinaryHelper.ExtractActualDataHelper(response, isBit);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return McHelper.Read(this, address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return McHelper.Write((IReadWriteMc)this, address, value);
    }

    public OperateResultExOne<byte[]> ReadRandom(String[] address) {
        return McHelper.ReadRandom(this, address);
    }

    public OperateResultExOne<byte[]> ReadRandom(String[] address, short[] length) {
        return McHelper.ReadRandom(this, address, length);
    }

    public OperateResultExOne<short[]> ReadRandomInt16(String[] address) {
        return McHelper.ReadRandomInt16(this, address);
    }

    public OperateResultExOne<int[]> ReadRandomUInt16(String[] address) {
        return McHelper.ReadRandomUInt16(this, address);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return McHelper.ReadBool(this, address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        return McHelper.Write((IReadWriteMc)this, address, values);
    }

    @Override
    public String toString() {
        return "MelsecMcRNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }

    @Override
    public OperateResult RemoteRun() {
        return McHelper.RemoteRun(this);
    }

    @Override
    public OperateResult RemoteStop() {
        return McHelper.RemoteStop(this);
    }

    @Override
    public OperateResult RemoteReset() {
        return McHelper.RemoteReset(this);
    }

    @Override
    public OperateResultExOne<String> ReadPlcType() {
        return McHelper.ReadPlcType(this);
    }

    @Override
    public OperateResult ErrorStateReset() {
        return McHelper.ErrorStateReset(this);
    }

    public static OperateResultExTwo<MelsecMcDataType, Integer> AnalysisAddress(String address) {
        try {
            if (address.startsWith("LSTS")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LSTS, Convert.ToInt32(address.substring(4), MelsecMcDataType.R_LSTS.getFromBase()));
            }
            if (address.startsWith("LSTC")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LSTC, Convert.ToInt32(address.substring(4), MelsecMcDataType.R_LSTC.getFromBase()));
            }
            if (address.startsWith("LSTN")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LSTN, Convert.ToInt32(address.substring(4), MelsecMcDataType.R_LSTN.getFromBase()));
            }
            if (address.startsWith("STS")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_STS, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_STS.getFromBase()));
            }
            if (address.startsWith("STC")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_STC, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_STC.getFromBase()));
            }
            if (address.startsWith("STN")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_STN, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_STN.getFromBase()));
            }
            if (address.startsWith("LTS")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LTS, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_LTS.getFromBase()));
            }
            if (address.startsWith("LTC")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LTC, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_LTC.getFromBase()));
            }
            if (address.startsWith("LTN")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LTN, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_LTN.getFromBase()));
            }
            if (address.startsWith("LCS")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LCS, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_LCS.getFromBase()));
            }
            if (address.startsWith("LCC")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LCC, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_LCC.getFromBase()));
            }
            if (address.startsWith("LCN")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_LCN, Convert.ToInt32(address.substring(3), MelsecMcDataType.R_LCN.getFromBase()));
            }
            if (address.startsWith("TS")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_TS, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_TS.getFromBase()));
            }
            if (address.startsWith("TC")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_TC, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_TC.getFromBase()));
            }
            if (address.startsWith("TN")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_TN, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_TN.getFromBase()));
            }
            if (address.startsWith("CS")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_CS, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_CS.getFromBase()));
            }
            if (address.startsWith("CC")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_CC, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_CC.getFromBase()));
            }
            if (address.startsWith("CN")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_CN, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_CN.getFromBase()));
            }
            if (address.startsWith("SM")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_SM, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_SM.getFromBase()));
            }
            if (address.startsWith("SB")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_SB, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_SB.getFromBase()));
            }
            if (address.startsWith("DX")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_DX, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_DX.getFromBase()));
            }
            if (address.startsWith("DY")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_DY, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_DY.getFromBase()));
            }
            if (address.startsWith("SD")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_SD, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_SD.getFromBase()));
            }
            if (address.startsWith("SW")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_SW, Convert.ToInt32(address.substring(2), MelsecMcDataType.R_SW.getFromBase()));
            }
            if (address.startsWith("X")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_X, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_X.getFromBase()));
            }
            if (address.startsWith("Y")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_Y, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_Y.getFromBase()));
            }
            if (address.startsWith("M")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_M, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_M.getFromBase()));
            }
            if (address.startsWith("L")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_L, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_L.getFromBase()));
            }
            if (address.startsWith("F")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_F, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_F.getFromBase()));
            }
            if (address.startsWith("V")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_V, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_V.getFromBase()));
            }
            if (address.startsWith("S")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_S, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_S.getFromBase()));
            }
            if (address.startsWith("B")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_B, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_B.getFromBase()));
            }
            if (address.startsWith("D")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_D, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_D.getFromBase()));
            }
            if (address.startsWith("W")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_W, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_W.getFromBase()));
            }
            if (address.startsWith("R")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_R, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_R.getFromBase()));
            }
            if (address.startsWith("Z")) {
                return OperateResultExTwo.CreateSuccessResult(MelsecMcDataType.R_Z, Convert.ToInt32(address.substring(1), MelsecMcDataType.R_Z.getFromBase()));
            }
            return new OperateResultExTwo<MelsecMcDataType, Integer>(StringResources.Language.NotSupportedDataType());
        }
        catch (Exception ex) {
            return new OperateResultExTwo<MelsecMcDataType, Integer>(ex.getMessage());
        }
    }

    public static byte[] BuildReadMcCoreCommand(McAddressData address, boolean isBit) {
        byte[] command = new byte[]{1, 4, isBit ? (byte)3 : 2, 0, BitConverter.GetBytes(address.getAddressStart())[0], BitConverter.GetBytes(address.getAddressStart())[1], BitConverter.GetBytes(address.getAddressStart())[2], BitConverter.GetBytes(address.getAddressStart())[3], BitConverter.GetBytes(address.getMcDataType().getDataCode())[0], BitConverter.GetBytes(address.getMcDataType().getDataCode())[1], (byte)(address.getLength() % 256), (byte)(address.getLength() / 256)};
        return command;
    }

    public static byte[] BuildWriteWordCoreCommand(McAddressData address, byte[] value) {
        if (value == null) {
            value = new byte[]{};
        }
        byte[] command = new byte[12 + value.length];
        command[0] = 1;
        command[1] = 20;
        command[2] = 2;
        command[3] = 0;
        command[4] = BitConverter.GetBytes(address.getAddressStart())[0];
        command[5] = BitConverter.GetBytes(address.getAddressStart())[1];
        command[6] = BitConverter.GetBytes(address.getAddressStart())[2];
        command[7] = BitConverter.GetBytes(address.getAddressStart())[3];
        command[8] = BitConverter.GetBytes(address.getMcDataType().getDataCode())[0];
        command[9] = BitConverter.GetBytes(address.getMcDataType().getDataCode())[1];
        command[10] = (byte)(value.length / 2 % 256);
        command[11] = (byte)(value.length / 2 / 256);
        System.arraycopy(value, 0, command, 12, value.length);
        return command;
    }

    public static byte[] BuildWriteBitCoreCommand(McAddressData address, boolean[] value) {
        if (value == null) {
            value = new boolean[]{};
        }
        byte[] buffer = MelsecHelper.TransBoolArrayToByteData(value);
        byte[] command = new byte[12 + buffer.length];
        command[0] = 1;
        command[1] = 20;
        command[2] = 3;
        command[3] = 0;
        command[4] = BitConverter.GetBytes(address.getAddressStart())[0];
        command[5] = BitConverter.GetBytes(address.getAddressStart())[1];
        command[6] = BitConverter.GetBytes(address.getAddressStart())[2];
        command[7] = BitConverter.GetBytes(address.getAddressStart())[3];
        command[8] = BitConverter.GetBytes(address.getMcDataType().getDataCode())[0];
        command[9] = BitConverter.GetBytes(address.getMcDataType().getDataCode())[1];
        command[10] = (byte)(value.length % 256);
        command[11] = (byte)(value.length / 256);
        System.arraycopy(buffer, 0, command, 12, buffer.length);
        return command;
    }
}

