/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.GE;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.Address.GeSRTPAddress;
import HslCommunication.Core.IMessage.GeSRTPMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Profinet.GE.GeHelper;
import HslCommunication.StringResources;
import java.net.Socket;
import java.util.Date;

public class GeSRTPNet
extends NetworkDeviceBase {
    private SoftIncrementCount incrementCount = new SoftIncrementCount(65535L);

    public GeSRTPNet() {
        this.setByteTransform(new RegularByteTransform());
        this.WordLength = (short)2;
    }

    public GeSRTPNet(String ipAddress) {
        this.setByteTransform(new RegularByteTransform());
        this.WordLength = (short)2;
        this.setIpAddress(ipAddress);
        this.setPort(18245);
    }

    public GeSRTPNet(String ipAddress, int port) {
        this.setByteTransform(new RegularByteTransform());
        this.WordLength = (short)2;
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new GeSRTPMessage();
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(socket, new byte[56], true, true);
        if (!read.IsSuccess) {
            return read;
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<byte[]> build = GeHelper.BuildReadCommand(this.incrementCount.GetCurrentValue(), address, length, false);
        if (!build.IsSuccess) {
            return build;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return GeHelper.ExtraResponseContent((byte[])read.Content);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<byte[]> build = GeHelper.BuildWriteCommand(this.incrementCount.GetCurrentValue(), address, value);
        if (!build.IsSuccess) {
            return build;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return GeHelper.ExtraResponseContent((byte[])read.Content);
    }

    public OperateResultExOne<Byte> ReadByte(String address) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, (short)1, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((GeSRTPAddress)analysis.Content).getDataCode() == 10 || ((GeSRTPAddress)analysis.Content).getDataCode() == 12 || ((GeSRTPAddress)analysis.Content).getDataCode() == 8) {
            return new OperateResultExOne<Byte>(StringResources.Language.GeSRTPNotSupportByteReadWrite());
        }
        OperateResultExOne<byte[]> read = this.Read(address, (short)1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((byte[])read.Content)[0]);
    }

    public OperateResult Write(String address, byte value) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, (short)1, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if (((GeSRTPAddress)analysis.Content).getDataCode() == 10 || ((GeSRTPAddress)analysis.Content).getDataCode() == 12 || ((GeSRTPAddress)analysis.Content).getDataCode() == 8) {
            return new OperateResultExOne(StringResources.Language.GeSRTPNotSupportByteReadWrite());
        }
        return this.Write(address, new byte[]{value});
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, length, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        OperateResultExOne<byte[]> build = GeHelper.BuildReadCommand(this.incrementCount.GetCurrentValue(), (GeSRTPAddress)analysis.Content);
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> extra = GeHelper.ExtraResponseContent((byte[])read.Content);
        if (!extra.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(extra);
        }
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray((byte[])extra.Content), ((GeSRTPAddress)analysis.Content).getAddressStart() % 8, length));
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExOne<byte[]> build = GeHelper.BuildWriteCommand(this.incrementCount.GetCurrentValue(), address, value);
        if (!build.IsSuccess) {
            return build;
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return read;
        }
        return GeHelper.ExtraResponseContent((byte[])read.Content);
    }

    public OperateResultExOne<Date> ReadPLCTime() {
        OperateResultExOne<byte[]> build = GeHelper.BuildReadCoreCommand(this.incrementCount.GetCurrentValue(), (byte)37, new byte[]{0, 0, 0, 2, 0});
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> extra = GeHelper.ExtraResponseContent((byte[])read.Content);
        if (!extra.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(extra);
        }
        return GeHelper.ExtraDateTime((byte[])extra.Content);
    }

    public OperateResultExOne<String> ReadProgramName() {
        OperateResultExOne<byte[]> build = GeHelper.BuildReadCoreCommand(this.incrementCount.GetCurrentValue(), (byte)1, new byte[]{0, 0, 0, 2, 0});
        if (!build.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(build);
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer((byte[])build.Content);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        OperateResultExOne<byte[]> extra = GeHelper.ExtraResponseContent((byte[])read.Content);
        if (!extra.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(extra);
        }
        return GeHelper.ExtraProgramName((byte[])extra.Content);
    }

    @Override
    public String toString() {
        return "GeSRTPNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

