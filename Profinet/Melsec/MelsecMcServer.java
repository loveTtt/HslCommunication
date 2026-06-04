/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Melsec;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftBuffer;
import HslCommunication.Core.Address.McAddressData;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.IMessage.MelsecQnA3EAsciiMessage;
import HslCommunication.Core.IMessage.MelsecQnA3EBinaryMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDataServerBase;
import HslCommunication.Core.Net.StateOne.AppSession;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.Array;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.Convert;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.Melsec.Helper.McBinaryHelper;
import HslCommunication.Profinet.Melsec.MelsecHelper;
import HslCommunication.Profinet.Melsec.MelsecMcDataType;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.nio.charset.StandardCharsets;

public class MelsecMcServer
extends NetworkDataServerBase {
    private SoftBuffer xBuffer = new SoftBuffer(65536);
    private SoftBuffer yBuffer = new SoftBuffer(65536);
    private SoftBuffer mBuffer = new SoftBuffer(65536);
    private SoftBuffer lBuffer = new SoftBuffer(65536);
    private SoftBuffer dBuffer = new SoftBuffer(262144);
    private SoftBuffer wBuffer = new SoftBuffer(131072);
    private SoftBuffer bBuffer = new SoftBuffer(65536);
    private SoftBuffer sBuffer = new SoftBuffer(65536);
    private SoftBuffer fBuffer = new SoftBuffer(65536);
    private SoftBuffer rBuffer = new SoftBuffer(131072);
    private SoftBuffer zrBuffer = new SoftBuffer(262144);
    private final int DataPoolLength = 65536;
    private boolean isBinary = true;

    public MelsecMcServer(boolean isBinary) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.isBinary = isBinary;
        this.LogMsgFormatBinary = isBinary;
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<McAddressData> analysis = McAddressData.ParseMelsecFrom(address, length, false);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.M.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(this.mBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart(), length * 16), 1);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.X.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(this.xBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart(), length * 16), 1);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.Y.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(this.yBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart(), length * 16), 1);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.L.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(this.lBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart(), length * 16), 1);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.B.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(this.bBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart(), length * 16), 1);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.D.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.dBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart() * 2, length * 2));
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.W.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.wBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart() * 2, length * 2));
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.R.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.rBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart() * 2, length * 2));
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.ZR.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.zrBuffer.GetBytes(((McAddressData)analysis.Content).getAddressStart() * 2, length * 2));
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<McAddressData> analysis = McAddressData.ParseMelsecFrom(address, 0, false);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.M.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            this.mBuffer.SetBytes(buffer, ((McAddressData)analysis.Content).getAddressStart());
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.X.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            this.xBuffer.SetBytes(buffer, ((McAddressData)analysis.Content).getAddressStart());
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.Y.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            this.yBuffer.SetBytes(buffer, ((McAddressData)analysis.Content).getAddressStart());
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.L.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            this.lBuffer.SetBytes(buffer, ((McAddressData)analysis.Content).getAddressStart());
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.B.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            this.bBuffer.SetBytes(buffer, ((McAddressData)analysis.Content).getAddressStart());
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.D.getDataCode()) {
            this.dBuffer.SetBytes(value, ((McAddressData)analysis.Content).getAddressStart() * 2);
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.W.getDataCode()) {
            this.wBuffer.SetBytes(value, ((McAddressData)analysis.Content).getAddressStart() * 2);
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.R.getDataCode()) {
            this.rBuffer.SetBytes(value, ((McAddressData)analysis.Content).getAddressStart() * 2);
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataCode() == MelsecMcDataType.ZR.getDataCode()) {
            this.zrBuffer.SetBytes(value, ((McAddressData)analysis.Content).getAddressStart() * 2);
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(StringResources.Language.NotSupportedDataType());
    }

    private OperateResultExOne<SoftBuffer> GetBoolSoftBufferByDataCode(short dataCode) {
        if (dataCode == MelsecMcDataType.M.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.mBuffer);
        }
        if (dataCode == MelsecMcDataType.X.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.xBuffer);
        }
        if (dataCode == MelsecMcDataType.Y.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.yBuffer);
        }
        if (dataCode == MelsecMcDataType.L.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.lBuffer);
        }
        if (dataCode == MelsecMcDataType.B.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.bBuffer);
        }
        if (dataCode == MelsecMcDataType.S.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.sBuffer);
        }
        if (dataCode == MelsecMcDataType.F.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.fBuffer);
        }
        return new OperateResultExOne<SoftBuffer>(StringResources.Language.NotSupportedDataType());
    }

    private OperateResultExOne<SoftBuffer> GetWordSoftBufferByDataCode(short dataCode) {
        if (dataCode == MelsecMcDataType.D.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.dBuffer);
        }
        if (dataCode == MelsecMcDataType.W.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.wBuffer);
        }
        if (dataCode == MelsecMcDataType.R.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.rBuffer);
        }
        if (dataCode == MelsecMcDataType.ZR.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(this.zrBuffer);
        }
        return new OperateResultExOne<SoftBuffer>(StringResources.Language.NotSupportedDataType());
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        if (address.indexOf(".") > 0) {
            return HslHelper.ReadBool(this, address, length);
        }
        OperateResultExOne<McAddressData> analysis = McAddressData.ParseMelsecFrom(address, 0, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataType() == 0) {
            return new OperateResultExOne<boolean[]>(StringResources.Language.MelsecCurrentTypeNotSupportedWordOperate());
        }
        OperateResultExOne<SoftBuffer> bufferResult = this.GetBoolSoftBufferByDataCode(((McAddressData)analysis.Content).getMcDataType().getDataCode());
        if (!bufferResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(bufferResult);
        }
        return OperateResultExOne.CreateSuccessResult(Array.GetBoolArrayFromBytes(((SoftBuffer)bufferResult.Content).GetBytes(((McAddressData)analysis.Content).getAddressStart(), length), 1));
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        int bitIndex = -1;
        if (address.indexOf(".") > 0) {
            bitIndex = HslHelper.CalculateBitStartIndex(address.substring(address.indexOf(46) + 1));
            address = address.substring(0, address.indexOf(46));
        }
        OperateResultExOne<McAddressData> analysis = McAddressData.ParseMelsecFrom(address, 0, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (bitIndex >= 0) {
            if (((McAddressData)analysis.Content).getMcDataType().getDataType() == 1) {
                return new OperateResultExOne(StringResources.Language.MelsecCurrentTypeNotSupportedBitOperate());
            }
            OperateResultExOne<SoftBuffer> bufferResult = this.GetWordSoftBufferByDataCode(((McAddressData)analysis.Content).getMcDataType().getDataCode());
            if (!bufferResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(bufferResult);
            }
            ((SoftBuffer)bufferResult.Content).SetBool(value, ((McAddressData)analysis.Content).getAddressStart() * 16 + bitIndex);
            return OperateResult.CreateSuccessResult();
        }
        if (((McAddressData)analysis.Content).getMcDataType().getDataType() == 0) {
            return new OperateResultExOne(StringResources.Language.MelsecCurrentTypeNotSupportedWordOperate());
        }
        OperateResultExOne<SoftBuffer> bufferResult = this.GetBoolSoftBufferByDataCode(((McAddressData)analysis.Content).getMcDataType().getDataCode());
        if (!bufferResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(bufferResult);
        }
        ((SoftBuffer)bufferResult.Content).SetBytes(Array.GetByteFromBoolArray(value), ((McAddressData)analysis.Content).getAddressStart());
        return OperateResult.CreateSuccessResult();
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        if (this.isBinary) {
            return new MelsecQnA3EBinaryMessage();
        }
        return new MelsecQnA3EAsciiMessage();
    }

    @Override
    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive) {
        if (this.isBinary) {
            return OperateResultExOne.CreateSuccessResult(this.ReadFromMcCore(SoftBasic.BytesArrayRemoveBegin(receive, 11)));
        }
        return OperateResultExOne.CreateSuccessResult(this.ReadFromMcAsciiCore(SoftBasic.BytesArrayRemoveBegin(receive, 22)));
    }

    protected byte[] ReadFromMcCore(byte[] mcCore) {
        if (mcCore[0] == 1 && mcCore[1] == 1) {
            return this.PackCommand((short)0, Encoding.ASCII.GetBytes("L02CPU          \u0005A"));
        }
        if (mcCore[0] == 1 && mcCore[1] == 4) {
            return this.ReadByCommand(mcCore);
        }
        if (mcCore[0] == 1 && mcCore[1] == 16) {
            return this.PackCommand((short)0, null);
        }
        if (mcCore[0] == 2 && mcCore[1] == 16) {
            return this.PackCommand((short)0, null);
        }
        if (mcCore[0] == 3 && mcCore[1] == 4) {
            return this.ReadRandomByCommand(mcCore);
        }
        if (mcCore[0] == 6 && mcCore[1] == 4) {
            return this.ReadBlockByCommand(mcCore);
        }
        if (mcCore[0] == 1 && mcCore[1] == 20) {
            if (!this.EnableWrite) {
                return this.PackCommand((short)-16286, null);
            }
            try {
                return this.PackCommand((short)0, this.WriteByMessage(mcCore));
            }
            catch (Exception ex) {
                return this.PackCommand((short)-16286, null);
            }
        }
        if (mcCore[0] == 6 && mcCore[1] == 16) {
            return this.PackCommand((short)0, null);
        }
        if (mcCore[0] == 23 && mcCore[1] == 22) {
            return this.PackCommand((short)0, null);
        }
        return null;
    }

    protected byte[] ReadFromMcAsciiCore(byte[] mcCore) {
        if (mcCore[0] == 48 && mcCore[1] == 52 && mcCore[2] == 48 && mcCore[3] == 49) {
            return this.ReadAsciiByCommand(mcCore);
        }
        if (mcCore[0] == 49 && mcCore[1] == 52 && mcCore[2] == 48 && mcCore[3] == 49) {
            if (!this.EnableWrite) {
                return this.PackCommand((short)-16286, null);
            }
            try {
                return this.PackCommand((short)0, this.WriteAsciiByMessage(mcCore));
            }
            catch (Exception ex) {
                return this.PackCommand((short)-16286, null);
            }
        }
        return null;
    }

    protected byte[] PackCommand(short status, byte[] data) {
        if (data == null) {
            data = new byte[]{};
        }
        if (this.isBinary) {
            byte[] back = new byte[11 + data.length];
            Utilities.ByteArrayCopyTo(SoftBasic.HexStringToBytes("D0 00 00 FF FF 03 00 00 00 00 00"), back, 0);
            if (data.length > 0) {
                Utilities.ByteArrayCopyTo(data, back, 11);
            }
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short)(data.length + 2)), back, 7);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(status), back, 9);
            return back;
        }
        byte[] back = new byte[22 + data.length];
        Utilities.ByteArrayCopyTo(Encoding.ASCII.GetBytes("D00000FF03FF0000000000"), back, 0);
        if (data.length > 0) {
            Utilities.ByteArrayCopyTo(data, back, 22);
        }
        Utilities.ByteArrayCopyTo(MelsecHelper.BuildBytesFromData(data.length + 4), back, 14);
        Utilities.ByteArrayCopyTo(MelsecHelper.BuildBytesFromData(status), back, 18);
        return back;
    }

    private byte[] ReadByCommand(byte[] command) {
        int length = this.getByteTransform().TransUInt16(command, 8);
        int startIndex = (command[6] & 0xFF) * 65536 + (command[5] & 0xFF) * 256 + (command[4] & 0xFF);
        if (command[2] == 1) {
            if (length > 7168) {
                return this.PackCommand((short)-16303, null);
            }
            short dataCode = (short)(command[7] & 0xFF);
            if (dataCode == MelsecMcDataType.M.getDataCode()) {
                return this.PackCommand((short)0, MelsecHelper.TransBoolArrayToByteData(this.mBuffer.GetBytes(startIndex, length)));
            }
            if (dataCode == MelsecMcDataType.X.getDataCode()) {
                return this.PackCommand((short)0, MelsecHelper.TransBoolArrayToByteData(this.xBuffer.GetBytes(startIndex, length)));
            }
            if (dataCode == MelsecMcDataType.Y.getDataCode()) {
                return this.PackCommand((short)0, MelsecHelper.TransBoolArrayToByteData(this.yBuffer.GetBytes(startIndex, length)));
            }
            if (dataCode == MelsecMcDataType.B.getDataCode()) {
                return this.PackCommand((short)0, MelsecHelper.TransBoolArrayToByteData(this.bBuffer.GetBytes(startIndex, length)));
            }
            if (dataCode == MelsecMcDataType.L.getDataCode()) {
                return this.PackCommand((short)0, MelsecHelper.TransBoolArrayToByteData(this.lBuffer.GetBytes(startIndex, length)));
            }
            if (dataCode == MelsecMcDataType.S.getDataCode()) {
                return this.PackCommand((short)0, MelsecHelper.TransBoolArrayToByteData(this.sBuffer.GetBytes(startIndex, length)));
            }
            if (dataCode == MelsecMcDataType.F.getDataCode()) {
                return this.PackCommand((short)0, MelsecHelper.TransBoolArrayToByteData(this.fBuffer.GetBytes(startIndex, length)));
            }
            return this.PackCommand((short)-16294, null);
        }
        short dataCode = (short)(command[7] & 0xFF);
        if (length > 960) {
            return this.PackCommand((short)-16303, null);
        }
        if (dataCode == MelsecMcDataType.M.getDataCode()) {
            return this.PackCommand((short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.mBuffer.GetBytes(startIndex, length * 16), 1)));
        }
        if (dataCode == MelsecMcDataType.X.getDataCode()) {
            return this.PackCommand((short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.xBuffer.GetBytes(startIndex, length * 16), 1)));
        }
        if (dataCode == MelsecMcDataType.Y.getDataCode()) {
            return this.PackCommand((short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.yBuffer.GetBytes(startIndex, length * 16), 1)));
        }
        if (dataCode == MelsecMcDataType.B.getDataCode()) {
            return this.PackCommand((short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.bBuffer.GetBytes(startIndex, length * 16), 1)));
        }
        if (dataCode == MelsecMcDataType.L.getDataCode()) {
            return this.PackCommand((short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.lBuffer.GetBytes(startIndex, length * 16), 1)));
        }
        if (dataCode == MelsecMcDataType.S.getDataCode()) {
            return this.PackCommand((short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.sBuffer.GetBytes(startIndex, length * 16), 1)));
        }
        if (dataCode == MelsecMcDataType.F.getDataCode()) {
            return this.PackCommand((short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.fBuffer.GetBytes(startIndex, length * 16), 1)));
        }
        if (dataCode == MelsecMcDataType.D.getDataCode()) {
            return this.PackCommand((short)0, this.dBuffer.GetBytes(startIndex * 2, length * 2));
        }
        if (dataCode == MelsecMcDataType.W.getDataCode()) {
            return this.PackCommand((short)0, this.wBuffer.GetBytes(startIndex * 2, length * 2));
        }
        if (dataCode == MelsecMcDataType.R.getDataCode()) {
            return this.PackCommand((short)0, this.rBuffer.GetBytes(startIndex * 2, length * 2));
        }
        if (dataCode == MelsecMcDataType.ZR.getDataCode()) {
            return this.PackCommand((short)0, this.zrBuffer.GetBytes(startIndex * 2, length * 2));
        }
        return this.PackCommand((short)-16294, null);
    }

    private byte[] ReadRandomByCommand(byte[] command) {
        int count = command[4] & 0xFF;
        byte[] buffer = new byte[count * 2];
        for (int i = 0; i < count; ++i) {
            short dataCode = (short)(command[9 + 4 * i] & 0xFF);
            int startIndex = (command[8 + 4 * i] & 0xFF) * 65536 + (command[7 + 4 * i] & 0xFF) * 256 + (command[6 + 4 * i] & 0xFF);
            if (dataCode == MelsecMcDataType.M.getDataCode()) {
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.mBuffer.GetBytes(startIndex, 16), 1)), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.X.getDataCode()) {
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.xBuffer.GetBytes(startIndex, 16), 1)), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.Y.getDataCode()) {
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.yBuffer.GetBytes(startIndex, 16), 1)), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.B.getDataCode()) {
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.bBuffer.GetBytes(startIndex, 16), 1)), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.L.getDataCode()) {
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.lBuffer.GetBytes(startIndex, 16), 1)), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.S.getDataCode()) {
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.sBuffer.GetBytes(startIndex, 16), 1)), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.F.getDataCode()) {
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.fBuffer.GetBytes(startIndex, 16), 1)), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.D.getDataCode()) {
                Utilities.ByteArrayCopyTo(this.dBuffer.GetBytes(startIndex * 2, 2), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.W.getDataCode()) {
                Utilities.ByteArrayCopyTo(this.wBuffer.GetBytes(startIndex * 2, 2), buffer, i * 2);
                continue;
            }
            if (dataCode == MelsecMcDataType.R.getDataCode()) {
                Utilities.ByteArrayCopyTo(this.rBuffer.GetBytes(startIndex * 2, 2), buffer, i * 2);
                continue;
            }
            if (dataCode != MelsecMcDataType.ZR.getDataCode()) continue;
            Utilities.ByteArrayCopyTo(this.zrBuffer.GetBytes(startIndex * 2, 2), buffer, i * 2);
        }
        return this.PackCommand((short)0, buffer);
    }

    private byte[] ReadBlockByCommand(byte[] command) {
        int count = command[4] & 0xFF;
        MemoryStream ms = new MemoryStream();
        for (int i = 0; i < count; ++i) {
            short dataCode = (short)(command[9 + 6 * i] & 0xFF);
            int startIndex = (command[8 + 6 * i] & 0xFF) * 65536 + (command[7 + 6 * i] & 0xFF) * 256 + (command[6 + 6 * i] & 0xFF);
            int length = this.getByteTransform().TransUInt16(command, 10 + 6 * i);
            if (dataCode == MelsecMcDataType.M.getDataCode()) {
                ms.Write(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.mBuffer.GetBytes(startIndex, length * 16), 1)));
                continue;
            }
            if (dataCode == MelsecMcDataType.X.getDataCode()) {
                ms.Write(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.xBuffer.GetBytes(startIndex, length * 16), 1)));
                continue;
            }
            if (dataCode == MelsecMcDataType.Y.getDataCode()) {
                ms.Write(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.yBuffer.GetBytes(startIndex, length * 16), 1)));
                continue;
            }
            if (dataCode == MelsecMcDataType.B.getDataCode()) {
                ms.Write(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.bBuffer.GetBytes(startIndex, length * 16), 1)));
                continue;
            }
            if (dataCode == MelsecMcDataType.L.getDataCode()) {
                ms.Write(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.lBuffer.GetBytes(startIndex, length * 16), 1)));
                continue;
            }
            if (dataCode == MelsecMcDataType.S.getDataCode()) {
                ms.Write(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.sBuffer.GetBytes(startIndex, length * 16), 1)));
                continue;
            }
            if (dataCode == MelsecMcDataType.F.getDataCode()) {
                ms.Write(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(this.fBuffer.GetBytes(startIndex, length * 16), 1)));
                continue;
            }
            if (dataCode == MelsecMcDataType.D.getDataCode()) {
                ms.Write(this.dBuffer.GetBytes(startIndex * 2, length * 2));
                continue;
            }
            if (dataCode == MelsecMcDataType.W.getDataCode()) {
                ms.Write(this.wBuffer.GetBytes(startIndex * 2, length * 2));
                continue;
            }
            if (dataCode == MelsecMcDataType.R.getDataCode()) {
                ms.Write(this.rBuffer.GetBytes(startIndex * 2, length * 2));
                continue;
            }
            if (dataCode != MelsecMcDataType.ZR.getDataCode()) continue;
            ms.Write(this.zrBuffer.GetBytes(startIndex * 2, length * 2));
        }
        return this.PackCommand((short)0, ms.ToArray());
    }

    private byte[] ReadAsciiPackCommand(SoftBuffer softBuffer, int startIndex, int length, boolean isBool) {
        if (isBool) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(softBuffer.GetBytes(startIndex, length * 16), 1);
            return this.PackCommand((short)0, MelsecHelper.TransByteArrayToAsciiByteArray(SoftBasic.BoolArrayToByte(buffer)));
        }
        return this.PackCommand((short)0, MelsecHelper.TransByteArrayToAsciiByteArray(softBuffer.GetBytes(startIndex * 2, length * 2)));
    }

    private byte[] GetBoolAsciiFromSoftBuffer(SoftBuffer softBuffer, int startIndex, int length) {
        byte[] buffer = softBuffer.GetBytes(startIndex, length);
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = (byte) (buffer[i] == 1 ? 49 : 48);
        }
        return buffer;
    }

    private byte[] ReadAsciiByCommand(byte[] command) {
        int length = Convert.ToInt32(Encoding.ASCII.GetString(command, 16, 4), 16);
        String typeCode = Encoding.ASCII.GetString(command, 8, 2);
        int startIndex = 0;
        startIndex = typeCode.equals(MelsecMcDataType.X.getAsciiCode()) || typeCode.equals(MelsecMcDataType.Y.getAsciiCode()) || typeCode.equals(MelsecMcDataType.W.getAsciiCode()) || typeCode.equals(MelsecMcDataType.B.getAsciiCode()) || typeCode.equals(MelsecMcDataType.L.getAsciiCode()) ? Convert.ToInt32(Encoding.ASCII.GetString(command, 10, 6), 16) : Convert.ToInt32(Encoding.ASCII.GetString(command, 10, 6));
        if (command[7] == 49) {
            if (length > 3584) {
                return this.PackCommand((short)-16303, null);
            }
            if (typeCode.equals(MelsecMcDataType.M.getAsciiCode())) {
                return this.PackCommand((short)0, this.GetBoolAsciiFromSoftBuffer(this.mBuffer, startIndex, length));
            }
            if (typeCode.equals(MelsecMcDataType.X.getAsciiCode())) {
                return this.PackCommand((short)0, this.GetBoolAsciiFromSoftBuffer(this.xBuffer, startIndex, length));
            }
            if (typeCode.equals(MelsecMcDataType.Y.getAsciiCode())) {
                return this.PackCommand((short)0, this.GetBoolAsciiFromSoftBuffer(this.yBuffer, startIndex, length));
            }
            if (typeCode.equals(MelsecMcDataType.B.getAsciiCode())) {
                return this.PackCommand((short)0, this.GetBoolAsciiFromSoftBuffer(this.bBuffer, startIndex, length));
            }
            if (typeCode.equals(MelsecMcDataType.L.getAsciiCode())) {
                return this.PackCommand((short)0, this.GetBoolAsciiFromSoftBuffer(this.lBuffer, startIndex, length));
            }
            if (typeCode.equals(MelsecMcDataType.S.getAsciiCode())) {
                return this.PackCommand((short)0, this.GetBoolAsciiFromSoftBuffer(this.sBuffer, startIndex, length));
            }
            if (typeCode.equals(MelsecMcDataType.F.getAsciiCode())) {
                return this.PackCommand((short)0, this.GetBoolAsciiFromSoftBuffer(this.fBuffer, startIndex, length));
            }
            return this.PackCommand((short)-16294, null);
        }
        if (length > 960) {
            return this.PackCommand((short)-16303, null);
        }
        if (typeCode.equals(MelsecMcDataType.M.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.mBuffer, startIndex, length, true);
        }
        if (typeCode.equals(MelsecMcDataType.X.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.xBuffer, startIndex, length, true);
        }
        if (typeCode.equals(MelsecMcDataType.Y.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.yBuffer, startIndex, length, true);
        }
        if (typeCode.equals(MelsecMcDataType.B.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.bBuffer, startIndex, length, true);
        }
        if (typeCode.equals(MelsecMcDataType.L.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.lBuffer, startIndex, length, true);
        }
        if (typeCode.equals(MelsecMcDataType.D.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.dBuffer, startIndex, length, false);
        }
        if (typeCode.equals(MelsecMcDataType.W.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.wBuffer, startIndex, length, false);
        }
        if (typeCode.equals(MelsecMcDataType.R.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.rBuffer, startIndex, length, false);
        }
        if (typeCode.equals(MelsecMcDataType.ZR.getAsciiCode())) {
            return this.ReadAsciiPackCommand(this.zrBuffer, startIndex, length, false);
        }
        return this.PackCommand((short)-16294, null);
    }

    private byte[] GetByteFromBinaryData(byte[] command) {
        boolean[] data = SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin(command, 10));
        return Array.GetByteFromBoolArray(data);
    }

    private byte[] WriteByMessage(byte[] command) throws Exception {
        if (!this.EnableWrite) {
            return null;
        }
        int length = this.getByteTransform().TransUInt16(command, 8);
        int startIndex = (command[6] & 0xFF) * 65536 + (command[5] & 0xFF) * 256 + (command[4] & 0xFF);
        if (command[2] == 1) {
            byte[] buffer = McBinaryHelper.ExtractActualDataHelper(SoftBasic.BytesArrayRemoveBegin(command, 10), true);
            short dataCode = (short)(command[7] & 0xFF);
            if (dataCode == MelsecMcDataType.M.getDataCode()) {
                this.mBuffer.SetBytes(SoftBasic.BytesArraySelectBegin(buffer, length), startIndex);
            } else if (dataCode == MelsecMcDataType.X.getDataCode()) {
                this.xBuffer.SetBytes(SoftBasic.BytesArraySelectBegin(buffer, length), startIndex);
            } else if (dataCode == MelsecMcDataType.Y.getDataCode()) {
                this.yBuffer.SetBytes(SoftBasic.BytesArraySelectBegin(buffer, length), startIndex);
            } else if (dataCode == MelsecMcDataType.B.getDataCode()) {
                this.bBuffer.SetBytes(SoftBasic.BytesArraySelectBegin(buffer, length), startIndex);
            } else if (dataCode == MelsecMcDataType.L.getDataCode()) {
                this.lBuffer.SetBytes(SoftBasic.BytesArraySelectBegin(buffer, length), startIndex);
            } else if (dataCode == MelsecMcDataType.S.getDataCode()) {
                this.sBuffer.SetBytes(SoftBasic.BytesArraySelectBegin(buffer, length), startIndex);
            } else if (dataCode == MelsecMcDataType.F.getDataCode()) {
                this.fBuffer.SetBytes(SoftBasic.BytesArraySelectBegin(buffer, length), startIndex);
            } else {
                throw new Exception(StringResources.Language.NotSupportedDataType());
            }
            return new byte[0];
        }
        short dataCode = (short)(command[7] & 0xFF);
        if (dataCode == MelsecMcDataType.M.getDataCode()) {
            byte[] buffer = this.GetByteFromBinaryData(command);
            this.mBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.X.getDataCode()) {
            byte[] buffer = this.GetByteFromBinaryData(command);
            this.xBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.Y.getDataCode()) {
            byte[] buffer = this.GetByteFromBinaryData(command);
            this.yBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.B.getDataCode()) {
            byte[] buffer = this.GetByteFromBinaryData(command);
            this.bBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.L.getDataCode()) {
            byte[] buffer = this.GetByteFromBinaryData(command);
            this.lBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.S.getDataCode()) {
            byte[] buffer = this.GetByteFromBinaryData(command);
            this.sBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.F.getDataCode()) {
            byte[] buffer = this.GetByteFromBinaryData(command);
            this.fBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.D.getDataCode()) {
            this.dBuffer.SetBytes(SoftBasic.BytesArrayRemoveBegin(command, 10), startIndex * 2);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.W.getDataCode()) {
            this.wBuffer.SetBytes(SoftBasic.BytesArrayRemoveBegin(command, 10), startIndex * 2);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.R.getDataCode()) {
            this.rBuffer.SetBytes(SoftBasic.BytesArrayRemoveBegin(command, 10), startIndex * 2);
            return new byte[0];
        }
        if (dataCode == MelsecMcDataType.ZR.getDataCode()) {
            this.zrBuffer.SetBytes(SoftBasic.BytesArrayRemoveBegin(command, 10), startIndex * 2);
            return new byte[0];
        }
        throw new Exception(StringResources.Language.NotSupportedDataType());
    }

    private byte[] GetByteFromAsciiData(byte[] command) {
        boolean[] data = SoftBasic.ByteToBoolArray(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)));
        return Array.GetByteFromBoolArray(data);
    }

    private byte[] WriteAsciiByMessage(byte[] command) throws Exception {
        int length = Convert.ToInt32(new String(command, 16, 4, StandardCharsets.US_ASCII), 16);
        String typeCode = Encoding.ASCII.GetString(command, 8, 2);
        int startIndex = 0;
        startIndex = typeCode.equals(MelsecMcDataType.X.getAsciiCode()) || typeCode.equals(MelsecMcDataType.Y.getAsciiCode()) || typeCode.equals(MelsecMcDataType.W.getAsciiCode()) || typeCode.equals(MelsecMcDataType.B.getAsciiCode()) || typeCode.equals(MelsecMcDataType.L.getAsciiCode()) || typeCode.equals(MelsecMcDataType.S.getAsciiCode()) || typeCode.equals(MelsecMcDataType.F.getAsciiCode()) ? Convert.ToInt32(Encoding.ASCII.GetString(command, 10, 6), 16) : Convert.ToInt32(Encoding.ASCII.GetString(command, 10, 6));
        if (command[7] == 49) {
            byte[] buffer = SoftBasic.BytesArrayRemoveBegin(command, 20);
            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = (byte)(buffer[i] & 0xCF);
            }
            if (buffer.length > length) {
                buffer = SoftBasic.BytesArraySelectBegin(buffer, length);
            }
            if (typeCode.equals(MelsecMcDataType.M.getAsciiCode())) {
                this.mBuffer.SetBytes(buffer, startIndex);
            } else if (typeCode.equals(MelsecMcDataType.X.getAsciiCode())) {
                this.xBuffer.SetBytes(buffer, startIndex);
            } else if (typeCode.equals(MelsecMcDataType.Y.getAsciiCode())) {
                this.yBuffer.SetBytes(buffer, startIndex);
            } else if (typeCode.equals(MelsecMcDataType.B.getAsciiCode())) {
                this.bBuffer.SetBytes(buffer, startIndex);
            } else if (typeCode.equals(MelsecMcDataType.L.getAsciiCode())) {
                this.lBuffer.SetBytes(buffer, startIndex);
            } else if (typeCode.equals(MelsecMcDataType.S.getAsciiCode())) {
                this.sBuffer.SetBytes(buffer, startIndex);
            } else if (typeCode.equals(MelsecMcDataType.F.getAsciiCode())) {
                this.fBuffer.SetBytes(buffer, startIndex);
            } else {
                throw new Exception(StringResources.Language.NotSupportedDataType());
            }
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.M.getAsciiCode())) {
            byte[] buffer = this.GetByteFromAsciiData(command);
            this.mBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.X.getAsciiCode())) {
            byte[] buffer = this.GetByteFromAsciiData(command);
            this.xBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.Y.getAsciiCode())) {
            byte[] buffer = this.GetByteFromAsciiData(command);
            this.yBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.B.getAsciiCode())) {
            byte[] buffer = this.GetByteFromAsciiData(command);
            this.bBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.L.getAsciiCode())) {
            byte[] buffer = this.GetByteFromAsciiData(command);
            this.lBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.S.getAsciiCode())) {
            byte[] buffer = this.GetByteFromAsciiData(command);
            this.sBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.F.getAsciiCode())) {
            byte[] buffer = this.GetByteFromAsciiData(command);
            this.fBuffer.SetBytes(buffer, startIndex);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.D.getAsciiCode())) {
            this.dBuffer.SetBytes(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)), startIndex * 2);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.W.getAsciiCode())) {
            this.wBuffer.SetBytes(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)), startIndex * 2);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.R.getAsciiCode())) {
            this.rBuffer.SetBytes(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)), startIndex * 2);
            return new byte[0];
        }
        if (typeCode.equals(MelsecMcDataType.ZR.getAsciiCode())) {
            this.zrBuffer.SetBytes(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)), startIndex * 2);
            return new byte[0];
        }
        throw new Exception(StringResources.Language.NotSupportedDataType());
    }

    @Override
    protected void LoadFromBytes(byte[] content) {
        this.mBuffer.SetBytes(content, 0, 0, 65536);
        this.xBuffer.SetBytes(content, 65536, 0, 65536);
        this.yBuffer.SetBytes(content, 131072, 0, 65536);
        this.dBuffer.SetBytes(content, 196608, 0, 131072);
        this.wBuffer.SetBytes(content, 327680, 0, 131072);
        if (content.length >= 12) {
            this.bBuffer.SetBytes(content, 458752, 0, 65536);
            this.rBuffer.SetBytes(content, 524288, 0, 131072);
            this.zrBuffer.SetBytes(content, 655360, 0, 131072);
        }
        if (content.length >= 15) {
            this.dBuffer.SetBytes(content, 786432, 131072, 131072);
            this.lBuffer.SetBytes(content, 917504, 0, 65536);
        }
    }

    @Override
    protected byte[] SaveToBytes() {
        byte[] buffer = new byte[983040];
        Array.Copy(this.mBuffer.GetBytes(), 0, buffer, 0, 65536);
        Array.Copy(this.xBuffer.GetBytes(), 0, buffer, 65536, 65536);
        Array.Copy(this.yBuffer.GetBytes(), 0, buffer, 131072, 65536);
        Array.Copy(this.dBuffer.GetBytes(), 0, buffer, 196608, 131072);
        Array.Copy(this.wBuffer.GetBytes(), 0, buffer, 327680, 131072);
        Array.Copy(this.bBuffer.GetBytes(), 0, buffer, 458752, 65536);
        Array.Copy(this.rBuffer.GetBytes(), 0, buffer, 524288, 131072);
        Array.Copy(this.zrBuffer.GetBytes(), 0, buffer, 655360, 131072);
        Array.Copy(this.dBuffer.GetBytes(), 131072, buffer, 786432, 131072);
        Array.Copy(this.lBuffer.GetBytes(), 0, buffer, 917504, 65536);
        return buffer;
    }

    public boolean getIsBinary() {
        return this.isBinary;
    }

    public void setIsBinary(boolean value) {
        this.LogMsgFormatBinary = value;
        this.isBinary = value;
    }

    @Override
    public String toString() {
        return "MelsecMcServer[" + this.getPort() + "]";
    }
}

