/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.AllenBradley;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.Net.NetworkBase.NetworkConnectedCip;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.BitConverter;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Profinet.AllenBradley.AllenBradleyHelper;
import HslCommunication.Profinet.AllenBradley.AllenBradleySLCNet;
import HslCommunication.Utilities;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class AllenBradleyPcccNet
extends NetworkConnectedCip {
    private SoftIncrementCount incrementCount = new SoftIncrementCount(65535L, 2L, 2);

    public AllenBradleyPcccNet() {
        this.WordLength = (short)2;
        this.setByteTransform(new RegularByteTransform());
    }

    public AllenBradleyPcccNet(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected byte[] GetLargeForwardOpen(short connectionID) {
        this.TOConnectionId = new Random().nextInt();
        byte[] buffer = SoftBasic.HexStringToBytes("00 00 00 00 0a 00 02 00 00 00 00 00 b2 00 30 00 54 02 20 06 24 01 0a 05 00 00 00 00 e8 a3 14 00 27 04 09 10 0b 46 a5 c1 07 00 00 00 01 40 20 00 f4 43 01 40 20 00 f4 43 a3 03 01 00 20 02 24 01");
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short)4105), buffer, 34);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(-1046133237), buffer, 36);
        Utilities.ByteArrayCopyTo(BitConverter.GetBytes(this.TOConnectionId), buffer, 28);
        return buffer;
    }

    @Override
    protected byte[] GetLargeForwardClose() {
        return SoftBasic.HexStringToBytes("00 00 00 00 0a 00 02 00 00 00 00 00 b2 00 18 00 4e 02 20 06 24 01 0a 05 27 04 09 10 0b 46 a5 c1 03 00 01 00 20 02 24 01");
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<byte[]> command = AllenBradleyHelper.PackExecutePCCCRead((int)this.incrementCount.GetCurrentValue(), address, length);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommandService(new byte[][]{(byte[])command.Content}));
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = AllenBradleyHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        OperateResultExThree<byte[], Short, Boolean> extra = AllenBradleyPcccNet.ExtractActualData((byte[])read.Content, true);
        if (!extra.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(extra);
        }
        return OperateResultExOne.CreateSuccessResult(extra.Content1);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> command = AllenBradleyHelper.PackExecutePCCCWrite((int)this.incrementCount.GetCurrentValue(), address, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommandService(new byte[][]{(byte[])command.Content}));
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = AllenBradleyHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        OperateResultExThree<byte[], Short, Boolean> extra = AllenBradleyPcccNet.ExtractActualData((byte[])read.Content, true);
        if (!extra.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(extra);
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<Boolean> ReadBool(String address) {
        int bitIndex = 0;
        OperateResultExTwo<String, Integer> analysis = AllenBradleySLCNet.AnalysisBitIndex(address);
        if (analysis.IsSuccess) {
            address = (String)analysis.Content1;
            bitIndex = (Integer)analysis.Content2;
        }
        OperateResultExOne<byte[]> read = this.Read(address, (short)(bitIndex / 16 * 2 + 2));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.ByteToBoolArray((byte[])read.Content)[bitIndex]);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        int bitIndex = 0;
        OperateResultExTwo<String, Integer> analysis = AllenBradleySLCNet.AnalysisBitIndex(address);
        if (analysis.IsSuccess) {
            address = (String)analysis.Content1;
            bitIndex = (Integer)analysis.Content2;
        }
        OperateResultExOne<byte[]> command = AllenBradleyHelper.PackExecutePCCCWrite((int)this.incrementCount.GetCurrentValue(), address, bitIndex, value);
        if (!command.IsSuccess) {
            return command;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.PackCommandService(new byte[][]{(byte[])command.Content}));
        if (!read.IsSuccess) {
            return read;
        }
        OperateResult check = AllenBradleyHelper.CheckResponse((byte[])read.Content);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        OperateResultExThree<byte[], Short, Boolean> extra = AllenBradleyPcccNet.ExtractActualData((byte[])read.Content, true);
        if (!extra.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(extra);
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<String> ReadString(String address) {
        return this.ReadString(address, (short)0, StandardCharsets.US_ASCII);
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        if (!Utilities.IsStringNullOrEmpty(address) && address.startsWith("ST")) {
            if (length <= 0) {
                OperateResultExOne<byte[]> read = this.Read(address, (short)2);
                if (!read.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(read);
                }
                int len = this.getByteTransform().TransUInt16((byte[])read.Content, 0);
                read = this.Read(address, (short)(2 + (len % 2 != 0 ? len + 1 : len)));
                if (!read.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(read);
                }
                return OperateResultExOne.CreateSuccessResult(new String(SoftBasic.BytesReverseByWord((byte[])read.Content), 2, len, encoding));
            }
            OperateResultExOne<byte[]> read = this.Read(address, (short)(length % 2 != 0 ? length + 3 : length + 2));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            int len = this.getByteTransform().TransUInt16((byte[])read.Content, 0);
            if (len + 2 > ((byte[])read.Content).length) {
                len = ((byte[])read.Content).length - 2;
            }
            return OperateResultExOne.CreateSuccessResult(new String(SoftBasic.BytesReverseByWord((byte[])read.Content), 2, len, encoding));
        }
        return super.ReadString(address, length, encoding);
    }

    @Override
    public OperateResult Write(String address, String value, Charset charset) {
        if (!Utilities.IsStringNullOrEmpty(address) && address.startsWith("ST")) {
            byte[] temp = value.getBytes(charset);
            int len = temp.length;
            temp = SoftBasic.ArrayExpandToLengthEven(temp);
            return this.Write(address, SoftBasic.SpliceArray(new byte[]{BitConverter.GetBytes(len)[0], BitConverter.GetBytes(len)[1]}, SoftBasic.BytesReverseByWord(temp)));
        }
        return super.Write(address, value, charset);
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((byte[])read.Content)[0]);
    }

    public OperateResult Write(String address, byte value) {
        return this.Write(address, new byte[]{value});
    }

    @Override
    public String toString() {
        return "AllenBradleyPcccNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

