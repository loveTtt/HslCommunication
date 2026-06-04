/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Robot.FANUC;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.IMessage.FanucRobotMessage;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.IReadWriteNet;
import HslCommunication.Core.Net.NetworkBase.NetworkDeviceBase;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.Robot.FANUC.FanucData;
import HslCommunication.Robot.FANUC.FanucHelper;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FanucInterfaceNet
extends NetworkDeviceBase
implements IReadWriteNet {
    public int ClientId = 1024;
    private byte[] connect_req = new byte[56];
    private byte[] session_req = new byte[]{8, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -64, 0, 0, 0, 0, 16, 14, 0, 0, 1, 1, 79, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public FanucInterfaceNet() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
    }

    public FanucInterfaceNet(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new FanucRobotMessage();
    }

    private OperateResult ReadCommandFromRobot(Socket socket, String[] cmds) {
        for (int i = 0; i < cmds.length; ++i) {
            byte[] buffer = cmds[i].getBytes(StandardCharsets.US_ASCII);
            OperateResultExOne<byte[]> write = this.ReadFromCoreServer(socket, FanucHelper.BuildWriteData((byte)56, 1, buffer, buffer.length), true, true);
            if (write.IsSuccess) continue;
            return write;
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        System.arraycopy(Utilities.getBytes(this.ClientId), 0, this.connect_req, 1, 4);
        OperateResultExOne<byte[]> receive = this.ReadFromCoreServer(socket, this.connect_req, true, true);
        if (!receive.IsSuccess) {
            return receive;
        }
        receive = this.ReadFromCoreServer(socket, this.session_req, true, true);
        if (!receive.IsSuccess) {
            return receive;
        }
        return this.ReadCommandFromRobot(socket, FanucHelper.GetFanucCmds());
    }

    public OperateResultExOne<byte[]> Read(String address) {
        return this.Read((byte)8, 1, (short)6130);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExTwo<Byte, Integer> analysis = FanucHelper.AnalysisFanucAddress(address, false);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if ((Byte)analysis.Content1 == 8 || (Byte)analysis.Content1 == 10 || (Byte)analysis.Content1 == 12) {
            return this.Read((Byte)analysis.Content1, (Integer)analysis.Content2, length);
        }
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExTwo<Byte, Integer> analysis = FanucHelper.AnalysisFanucAddress(address, false);
        if (!analysis.IsSuccess) {
            return analysis;
        }
        if ((Byte)analysis.Content1 == 8 || (Byte)analysis.Content1 == 10 || (Byte)analysis.Content1 == 12) {
            return this.Write((Byte)analysis.Content1, (Integer)analysis.Content2, value);
        }
        return new OperateResult(StringResources.Language.NotSupportedDataType());
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExTwo<Byte, Integer> analysis = FanucHelper.AnalysisFanucAddress(address, true);
        if (!analysis.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(analysis);
        }
        if ((Byte)analysis.Content1 == 70 || (Byte)analysis.Content1 == 72 || (Byte)analysis.Content1 == 76) {
            return this.ReadBool((Byte)analysis.Content1, (Integer)analysis.Content2, length);
        }
        return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        OperateResultExTwo<Byte, Integer> analysis = FanucHelper.AnalysisFanucAddress(address, true);
        if (!analysis.IsSuccess) {
            return analysis;
        }
        if ((Byte)analysis.Content1 == 70 || (Byte)analysis.Content1 == 72 || (Byte)analysis.Content1 == 76) {
            return this.WriteBool((Byte)analysis.Content1, (Integer)analysis.Content2, value);
        }
        return new OperateResult(StringResources.Language.NotSupportedDataType());
    }

    public OperateResultExOne<byte[]> Read(byte select, int address, short length) {
        byte[] send = FanucHelper.BulidReadData(select, address, length);
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(send);
        if (!read.IsSuccess) {
            return read;
        }
        if (((byte[])read.Content)[31] == -108) {
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 56));
        }
        if (((byte[])read.Content)[31] == -44) {
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle((byte[])read.Content, 44, length * 2));
        }
        return new OperateResultExOne<byte[]>(((byte[])read.Content)[31], "Error");
    }

    public OperateResult Write(byte select, int address, byte[] value) {
        byte[] send = FanucHelper.BuildWriteData(select, address, value, value.length / 2);
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(send);
        if (!read.IsSuccess) {
            return read;
        }
        if (((byte[])read.Content)[31] == -44) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResultExOne(((byte[])read.Content)[31], "Error");
    }

    public OperateResultExOne<boolean[]> ReadBool(byte select, int address, short length) {
        int byteStartIndex = address - 1 - (address - 1) % 8 + 1;
        int byteEndIndex = (address + length - 1) % 8 == 0 ? address + length - 1 : (address + length - 1) / 8 * 8 + 8;
        int byteLength = (byteEndIndex - byteStartIndex + 1) / 8;
        byte[] send = FanucHelper.BulidReadData(select, address, (short)(byteLength * 8));
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(send);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        if (((byte[])read.Content)[31] == -108) {
            boolean[] array = SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 56));
            boolean[] buffer = new boolean[length];
            System.arraycopy(array, address - byteStartIndex, buffer, 0, length);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        if (((byte[])read.Content)[31] == -44) {
            boolean[] array = SoftBasic.ByteToBoolArray(SoftBasic.BytesArraySelectMiddle((byte[])read.Content, 44, byteLength));
            boolean[] buffer = new boolean[length];
            System.arraycopy(array, address - byteStartIndex, buffer, 0, length);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
        return new OperateResultExOne<boolean[]>(((byte[])read.Content)[31], "Error");
    }

    public OperateResult WriteBool(byte select, int address, boolean[] value) {
        int byteStartIndex = address - 1 - (address - 1) % 8 + 1;
        int byteEndIndex = (address + value.length - 1) % 8 == 0 ? address + value.length - 1 : (address + value.length - 1) / 8 * 8 + 8;
        int byteLength = (byteEndIndex - byteStartIndex + 1) / 8;
        boolean[] buffer = new boolean[byteLength * 8];
        System.arraycopy(value, 0, buffer, address - byteStartIndex, value.length);
        byte[] send = FanucHelper.BuildWriteData(select, address, this.getByteTransform().TransByte(buffer), value.length);
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(send);
        if (!read.IsSuccess) {
            return read;
        }
        if (((byte[])read.Content)[31] == -44) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult();
    }

    public OperateResultExOne<FanucData> ReadFanucData() {
        OperateResultExOne<byte[]> read = this.Read("");
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return FanucData.PraseFrom((byte[])read.Content);
    }

    public OperateResultExOne<boolean[]> ReadSDO(int address, short length) {
        if (address < 11001) {
            return this.ReadBool((byte)70, address, length);
        }
        return this.ReadPMCR2(address - 11000, length);
    }

    public OperateResult WriteSDO(int address, boolean[] value) {
        if (address < 11001) {
            return this.WriteBool((byte)70, address, value);
        }
        return this.WritePMCR2(address - 11000, value);
    }

    public OperateResultExOne<boolean[]> ReadSDI(int address, short length) {
        return this.ReadBool((byte)72, address, length);
    }

    public OperateResult WriteSDI(int address, boolean[] value) {
        return this.WriteBool((byte)72, address, value);
    }

    public OperateResultExOne<boolean[]> ReadRDI(int address, short length) {
        return this.ReadBool((byte)72, address + 5000, length);
    }

    public OperateResult WriteRDI(int address, boolean[] value) {
        return this.WriteBool((byte)72, address + 5000, value);
    }

    public OperateResultExOne<boolean[]> ReadUI(int address, short length) {
        return this.ReadBool((byte)72, address + 6000, length);
    }

    public OperateResultExOne<boolean[]> ReadUO(int address, short length) {
        return this.ReadBool((byte)70, address + 6000, length);
    }

    public OperateResult WriteUO(int address, boolean[] value) {
        return this.WriteBool((byte)70, address + 6000, value);
    }

    public OperateResultExOne<boolean[]> ReadSI(int address, short length) {
        return this.ReadBool((byte)72, address + 7000, length);
    }

    public OperateResultExOne<boolean[]> ReadSO(int address, short length) {
        return this.ReadBool((byte)70, address + 7000, length);
    }

    public OperateResult WriteSO(int address, boolean[] value) {
        return this.WriteBool((byte)70, address + 7000, value);
    }

    public OperateResultExOne<short[]> ReadGI(int address, short length) {
        OperateResultExOne<byte[]> read = this.Read((byte)12, address, length);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransInt16((byte[])read.Content, 0, length));
    }

    public OperateResult WriteGI(int address, short[] value) {
        return this.Write((byte)12, address, this.getByteTransform().TransByte(value));
    }

    public OperateResultExOne<short[]> ReadGO(int address, short length) {
        if (address >= 10001) {
            address -= 6000;
        }
        OperateResultExOne<byte[]> read = this.Read((byte)10, address, length);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransInt16((byte[])read.Content, 0, length));
    }

    public OperateResult WriteGO(int address, short[] value) {
        if (address >= 10001) {
            address -= 6000;
        }
        return this.Write((byte)10, address, this.getByteTransform().TransByte(value));
    }

    public OperateResultExOne<boolean[]> ReadPMCR2(int address, short length) {
        return this.ReadBool((byte)76, address, length);
    }

    public OperateResult WritePMCR2(int address, boolean[] value) {
        return this.WriteBool((byte)76, address, value);
    }

    public OperateResultExOne<boolean[]> ReadRDO(int address, short length) {
        return this.ReadBool((byte)70, address + 5000, length);
    }

    public OperateResult WriteRDO(int address, boolean[] value) {
        return this.WriteBool((byte)70, address + 5000, value);
    }

    public OperateResult WriteRXyzwpr(int Address, float[] Xyzwpr, short[] Config, short UserFrame, short UserTool) {
        int num = Xyzwpr.length * 4 + Config.length * 2 + 2;
        byte[] robotBuffer = new byte[num];
        byte[] buffer_xyzwpr = this.getByteTransform().TransByte(Xyzwpr);
        System.arraycopy(buffer_xyzwpr, 0, robotBuffer, 0, buffer_xyzwpr.length);
        byte[] buffer_config = this.getByteTransform().TransByte(Config);
        System.arraycopy(buffer_config, 0, robotBuffer, 36, buffer_config.length);
        OperateResult write = this.Write((byte)8, Address, robotBuffer);
        if (!write.IsSuccess) {
            return write;
        }
        if (0 <= UserFrame && UserFrame <= 15) {
            if (0 <= UserTool && UserTool <= 15) {
                write = this.Write((byte)8, Address + 45, this.getByteTransform().TransByte(new short[]{UserFrame, UserTool}));
                if (!write.IsSuccess) {
                    return write;
                }
            } else {
                write = this.Write((byte)8, Address + 45, this.getByteTransform().TransByte(new short[]{UserFrame}));
                if (!write.IsSuccess) {
                    return write;
                }
            }
        } else if (0 <= UserTool && UserTool <= 15) {
            write = this.Write((byte)8, Address + 46, this.getByteTransform().TransByte(new short[]{UserTool}));
            if (!write.IsSuccess) {
                return write;
            }
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResult WriteRJoint(int address, float[] joint, short UserFrame, short UserTool) {
        OperateResult write = this.Write((byte)8, address + 26, this.getByteTransform().TransByte(joint));
        if (!write.IsSuccess) {
            return write;
        }
        if (0 <= UserFrame && UserFrame <= 15) {
            if (0 <= UserTool && UserTool <= 15) {
                write = this.Write((byte)8, address + 44, this.getByteTransform().TransByte(new short[]{0, UserFrame, UserTool}));
                if (!write.IsSuccess) {
                    return write;
                }
            } else {
                write = this.Write((byte)8, address + 44, this.getByteTransform().TransByte(new short[]{0, UserFrame}));
                if (!write.IsSuccess) {
                    return write;
                }
            }
        } else {
            write = this.Write((byte)8, address + 44, this.getByteTransform().TransByte(new short[]{0}));
            if (!write.IsSuccess) {
                return write;
            }
            if (0 <= UserTool && UserTool <= 15) {
                write = this.Write((byte)8, address + 44, this.getByteTransform().TransByte(new short[]{0, UserTool}));
                if (!write.IsSuccess) {
                    return write;
                }
            }
        }
        return OperateResult.CreateSuccessResult();
    }

    @Override
    public String toString() {
        return "FanucInterfaceNet[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

