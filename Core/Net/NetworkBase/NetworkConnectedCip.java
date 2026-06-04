/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Net.NetworkBase;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.BasicFramework.SoftIncrementCount;
import HslCommunication.Core.IMessage.AllenBradleyMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Types.Environment;
import HslCommunication.Core.Types.HslHelper;
import HslCommunication.Core.Types.MemoryStream;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Profinet.AllenBradley.AllenBradleyDF1Serial;
import HslCommunication.Profinet.AllenBradley.AllenBradleyHelper;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.Socket;
import java.util.ArrayList;

public class NetworkConnectedCip
extends NetworkDeviceBase {
    public int SessionHandle = 0;
    public int OTConnectionId = 0;
    public int TOConnectionId = 0;
    private SoftIncrementCount incrementCount = new SoftIncrementCount(65535L, 3L, 2);
    private SoftIncrementCount connectID = new SoftIncrementCount(50000L, 2L);
    private long openForwardId = 256L;
    private long context = 0L;

    @Override
    protected INetMessage GetNewNetMessage() {
        return new AllenBradleyMessage();
    }

    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        return AllenBradleyHelper.PackRequestHeader(112, this.SessionHandle, AllenBradleyHelper.PackCommandSpecificData(this.GetOTConnectionIdService(), command));
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResultExOne<byte[]> read1 = this.ReadFromCoreServer(socket, AllenBradleyHelper.RegisterSessionHandle(Utilities.getBytes(this.context++)), true, false);
        if (!read1.IsSuccess) {
            return read1;
        }
        OperateResult check = AllenBradleyHelper.CheckResponse((byte[])read1.Content);
        if (!check.IsSuccess) {
            return check;
        }
        int sessionHandle = Utilities.getInt((byte[])read1.Content, 4);
        for (int i = 0; i < 10; ++i) {
            long id = this.openForwardId;
            ++this.openForwardId;
            short tick = i < 7 ? (short)i : (short)(HslHelper.HslRandom.nextInt(190) + 7);
            OperateResultExOne<byte[]> read2 = this.ReadFromCoreServer(socket, AllenBradleyHelper.PackRequestHeader(111, sessionHandle, this.GetLargeForwardOpen(tick), this.getByteTransform().TransByte(id)), true, false);
            if (!read2.IsSuccess) {
                return read2;
            }
            try {
                if (((byte[])read2.Content).length >= 46 && ((byte[])read2.Content)[42] != 0) {
                    int err = this.getByteTransform().TransUInt16((byte[])read2.Content, 44);
                    if (err == 256 && i < 9) continue;
                    if (err == 256) {
                        return new OperateResult("Connection in use or duplicate Forward Open");
                    }
                    if (err == 275) {
                        return new OperateResult("Extended Status: Out of connections (0x0113)");
                    }
                    return new OperateResult("Forward Open failed, Code: " + this.getByteTransform().TransUInt16((byte[])read2.Content, 44));
                }
                this.OTConnectionId = (int)this.getByteTransform().TransUInt32((byte[])read2.Content, 44);
                break;
            }
            catch (Exception ex) {
                return new OperateResult(ex.getMessage() + Environment.NewLine + "Source: " + SoftBasic.ByteToHexString((byte[])read2.Content, ' '));
            }
        }
        this.incrementCount.ResetCurrentValue();
        this.SessionHandle = sessionHandle;
        return OperateResult.CreateSuccessResult();
    }

    @Override
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        if (socket == null) {
            return OperateResult.CreateSuccessResult();
        }
        byte[] forwardClose = this.GetLargeForwardClose();
        if (forwardClose != null) {
            OperateResultExOne<byte[]> close = this.ReadFromCoreServer(socket, AllenBradleyHelper.PackRequestHeader(111, this.SessionHandle, forwardClose), true, false);
            if (!close.IsSuccess) {
                return close;
            }
        }
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(socket, AllenBradleyHelper.UnRegisterSessionHandle(this.SessionHandle), true, false);
        if (!read.IsSuccess) {
            return read;
        }
        return OperateResult.CreateSuccessResult();
    }

    protected byte[] PackCommandService(byte[] ... cip) {
        MemoryStream ms = new MemoryStream();
        ms.WriteByte(177);
        ms.WriteByte(0);
        ms.WriteByte(0);
        ms.WriteByte(0);
        long messageId = this.incrementCount.GetCurrentValue();
        ms.WriteByte(Utilities.getBytes(messageId)[0]);
        ms.WriteByte(Utilities.getBytes(messageId)[1]);
        if (cip.length == 1) {
            ms.Write(cip[0], 0, cip[0].length);
        } else {
            int i;
            ms.Write(new byte[]{10, 2, 32, 2, 36, 1}, 0, 6);
            ms.WriteByte(Utilities.getBytes(cip.length)[0]);
            ms.WriteByte(Utilities.getBytes(cip.length)[1]);
            int offset = 2 + cip.length * 2;
            for (i = 0; i < cip.length; ++i) {
                ms.WriteByte(Utilities.getBytes(offset)[0]);
                ms.WriteByte(Utilities.getBytes(offset)[1]);
                offset += cip[i].length;
            }
            for (i = 0; i < cip.length; ++i) {
                ms.Write(cip[i], 0, cip[i].length);
            }
        }
        byte[] data = ms.ToArray();
        Utilities.ByteArrayCopyTo(Utilities.getBytes((short)(data.length - 4)), data, 2);
        return data;
    }

    protected byte[] GetLargeForwardOpen(short connectionID) {
        return SoftBasic.HexStringToBytes("00 00 00 00 00 00 02 00 00 00 00 00 b2 00 34 00 5b 02 20 06 24 01 0e 9c 02 00 00 80 01 00 fe 80 02 00 1b 05 30 a7 2b 03 02 00 00 00 80 84 1e 00cc 07 00 42 80 84 1e 00 cc 07 00 42 a3 03 20 02 24 01 2c 01");
    }

    protected byte[] GetLargeForwardClose() {
        return null;
    }

    private byte[] GetOTConnectionIdService() {
        byte[] buffer = new byte[8];
        buffer[0] = -95;
        buffer[1] = 0;
        buffer[2] = 4;
        buffer[3] = 0;
        Utilities.ByteArrayCopyTo(Utilities.getBytes(this.OTConnectionId), buffer, 4);
        return buffer;
    }

    public static OperateResultExThree<byte[], Short, Boolean> ExtractActualData(byte[] response, boolean isRead) {
        ArrayList<Byte> data = new ArrayList<Byte>();
        int offset = 42;
        boolean hasMoreData = false;
        Short dataType = 0;
        int count = Utilities.getUShort(response, offset);
        if (Utilities.getInt(response, 46) == 138) {
            offset = 50;
            int dataCount = Utilities.getShort(response, offset);
            for (int i = 0; i < dataCount; ++i) {
                int offsetStart = Utilities.getShort(response, offset + 2 + i * 2) + offset;
                int offsetEnd = i == dataCount - 1 ? response.length : Utilities.getShort(response, offset + 4 + i * 2) + offset;
                short err = Utilities.getShort(response, offsetStart + 2);
                switch (err) {
                    case 4: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley04());
                    }
                    case 5: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley05());
                    }
                    case 6: {
                        if (response[offset + 2] != -46 && response[offset + 2] != -52) break;
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley06());
                    }
                    case 10: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley0A());
                    }
                    case 19: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley13());
                    }
                    case 28: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1C());
                    }
                    case 30: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1E());
                    }
                    case 38: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley26());
                    }
                    case 0: {
                        break;
                    }
                    default: {
                        return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.UnknownError());
                    }
                }
                if (!isRead) continue;
                for (int j = offsetStart + 6; j < offsetEnd; ++j) {
                    data.add(response[j]);
                }
            }
        } else {
            byte err = response[offset + 6];
            switch (err) {
                case 4: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley04());
                }
                case 5: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley05());
                }
                case 6: {
                    hasMoreData = true;
                    break;
                }
                case 10: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley0A());
                }
                case 19: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley13());
                }
                case 28: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1C());
                }
                case 30: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley1E());
                }
                case 38: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.AllenBradley26());
                }
                case 0: {
                    break;
                }
                default: {
                    return new OperateResultExThree<byte[], Short, Boolean>(err, StringResources.Language.UnknownError());
                }
            }
            if (response[offset + 4] == -51 || response[offset + 4] == -45) {
                return OperateResultExThree.CreateSuccessResult(Utilities.getBytes(data), dataType, hasMoreData);
            }
            if (response[offset + 4] == -52 || response[offset + 4] == -46) {
                for (int i = offset + 10; i < offset + 2 + count; ++i) {
                    data.add(response[i]);
                }
                dataType = Utilities.getShort(response, offset + 8);
            } else if (response[offset + 4] == -43) {
                for (int i = offset + 8; i < offset + 2 + count; ++i) {
                    data.add(response[i]);
                }
            } else if (response[offset + 4] == -53) {
                if (response[58] != 0) {
                    return new OperateResultExThree<byte[], Short, Boolean>(response[58], AllenBradleyDF1Serial.GetExtStatusDescription(response[58]) + "\r\nSource: " + SoftBasic.ByteToHexString(SoftBasic.BytesArrayRemoveBegin(response, 57), ' '));
                }
                if (!isRead) {
                    return OperateResultExThree.CreateSuccessResult(Utilities.getBytes(data), dataType, hasMoreData);
                }
                return OperateResultExThree.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 61), dataType, hasMoreData);
            }
        }
        return OperateResultExThree.CreateSuccessResult(Utilities.getBytes(data), dataType, hasMoreData);
    }
}

