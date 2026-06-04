/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Enthernet.SimplifyNet;

import HslCommunication.Core.IMessage.HslMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.HslProtocol;
import HslCommunication.Core.Net.NetHandle;
import HslCommunication.Core.Net.NetworkBase.NetworkDoubleBase;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Utilities;
import java.util.UUID;

public class NetSimplifyClient
extends NetworkDoubleBase {
    public NetSimplifyClient(String ipAddress, int port) {
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    public NetSimplifyClient(String ipAddress, int port, UUID token) {
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.Token = token;
    }

    public NetSimplifyClient() {
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new HslMessage();
    }

    public OperateResultExOne<String> ReadFromServer(NetHandle customer, String send) {
        OperateResultExOne<byte[]> read = this.ReadFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), this.Token, send));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.byte2CSharpString((byte[])read.Content));
    }

    public OperateResultExOne<String[]> ReadFromServer(NetHandle customer, String[] send) {
        OperateResultExOne<byte[]> read = this.ReadFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), this.Token, send));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(HslProtocol.UnPackStringArrayFromByte((byte[])read.Content));
    }

    public OperateResultExOne<byte[]> ReadFromServer(NetHandle customer, byte[] send) {
        return this.ReadFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), this.Token, send));
    }

    public OperateResultExTwo<NetHandle, String> ReadCustomerFromServer(NetHandle customer, String send) {
        OperateResultExTwo<NetHandle, byte[]> read = this.ReadCustomerFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), this.Token, send));
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        return OperateResultExTwo.CreateSuccessResult(read.Content1, Utilities.byte2CSharpString((byte[])read.Content2));
    }

    public OperateResultExTwo<NetHandle, String[]> ReadCustomerFromServer(NetHandle customer, String[] send) {
        OperateResultExTwo<NetHandle, byte[]> read = this.ReadCustomerFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), this.Token, send));
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        return OperateResultExTwo.CreateSuccessResult(read.Content1, HslProtocol.UnPackStringArrayFromByte((byte[])read.Content2));
    }

    public OperateResultExTwo<NetHandle, byte[]> ReadCustomerFromServer(NetHandle customer, byte[] send) {
        return this.ReadCustomerFromServerBase(HslProtocol.CommandBytes(customer.get_CodeValue(), this.Token, send));
    }

    private OperateResultExOne<byte[]> ReadFromServerBase(byte[] send) {
        OperateResultExTwo<NetHandle, byte[]> read = this.ReadCustomerFromServerBase(send);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(read.Content2);
    }

    private OperateResultExTwo<NetHandle, byte[]> ReadCustomerFromServerBase(byte[] send) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(send);
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        return HslProtocol.ExtractHslData((byte[])read.Content);
    }

    @Override
    public String toString() {
        return "NetSimplifyClient[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

