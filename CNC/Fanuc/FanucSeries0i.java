/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.CNC.Fanuc;

import HslCommunication.Authorization;
import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.CNC.Fanuc.CNCFanucSeriesMessage;
import HslCommunication.CNC.Fanuc.CNCRunStatus;
import HslCommunication.CNC.Fanuc.CNCWorkMode;
import HslCommunication.CNC.Fanuc.CutterInfo;
import HslCommunication.CNC.Fanuc.FanucOperatorMessage;
import HslCommunication.CNC.Fanuc.FanucSysInfo;
import HslCommunication.CNC.Fanuc.FileDirInfo;
import HslCommunication.CNC.Fanuc.SysAlarm;
import HslCommunication.CNC.Fanuc.SysAllCoors;
import HslCommunication.CNC.Fanuc.SysStatusInfo;
import HslCommunication.CNC.Fanuc.ToolInformation;
import HslCommunication.Core.IMessage.INetMessage;
import HslCommunication.Core.Net.NetworkBase.NetworkDoubleBase;
import HslCommunication.Core.Transfer.ReverseBytesTransform;
import HslCommunication.Core.Types.Encoding;
import HslCommunication.Core.Types.List;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;
import HslCommunication.Utilities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

public class FanucSeries0i
extends NetworkDoubleBase {
    private Charset encoding;
    private FanucSysInfo fanucSysInfo;
    private short opPath = 1;

    public FanucSeries0i(String ipAddress, int port) {
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.setByteTransform(new ReverseBytesTransform());
        this.encoding = Charset.forName("GB2312");
        this.setReceiveTimeOut(30000);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new CNCFanucSeriesMessage();
    }

    public Charset getTextEncoding() {
        return this.encoding;
    }

    public void setTextEncoding(Charset value) {
        this.encoding = value;
    }

    public short getOperatePath() {
        return this.opPath;
    }

    public void setOperatePath(short value) {
        this.opPath = value;
    }

    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        OperateResultExOne<byte[]> read1 = this.ReadFromCoreServer(socket, SoftBasic.HexStringToBytes("a0 a0 a0 a0 00 01 01 01 00 02 00 02"), true, true);
        if (!read1.IsSuccess) {
            return read1;
        }
        OperateResultExOne<byte[]> read2 = this.ReadFromCoreServer(socket, SoftBasic.HexStringToBytes("a0 a0 a0 a0 00 01 21 01 00 1e 00 01 00 1c 00 01 00 01 00 18 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00"), true, true);
        if (!read2.IsSuccess) {
            return read2;
        }
        this.fanucSysInfo = new FanucSysInfo((byte[])read2.Content);
        return OperateResult.CreateSuccessResult();
    }

    @Override
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        return this.ReadFromCoreServer(socket, SoftBasic.HexStringToBytes("a0 a0 a0 a0 00 01 02 01 00 00"), true, true);
    }

    private double GetFanucDouble(byte[] content, int index) {
        return this.GetFanucDouble(content, index, 1)[0];
    }

    private double[] GetFanucDouble(byte[] content, int index, int length) {
        double[] buffer = new double[length];
        for (int i = 0; i < length; ++i) {
            int data = this.getByteTransform().TransInt32(content, index + 8 * i);
            short decs = this.getByteTransform().TransInt16(content, index + 8 * i + 6);
            buffer[i] = data == 0 ? 0.0 : new BigDecimal((double)data * Math.pow(0.1, decs)).setScale((int)decs, 4).doubleValue();
        }
        return buffer;
    }

    private byte[] CreateFromFanucDouble(double value) {
        byte[] buffer = new byte[8];
        int interge = (int)(value * 1000.0);
        System.arraycopy(this.getByteTransform().TransByte(interge), 0, buffer, 0, 4);
        buffer[5] = 10;
        buffer[7] = 3;
        return buffer;
    }

    private void ChangeTextEncoding(short code) {
        switch (code) {
            case 0: {
                this.encoding = Charset.forName("GB2312");
                break;
            }
            case 1: 
            case 4: {
                this.encoding = Charset.forName("shift_jis");
                break;
            }
            case 6: {
                this.encoding = Charset.forName("ks_c_5601-1987");
                break;
            }
            case 15: {
                this.encoding = Charset.forName("GB2312");
                break;
            }
            case 16: {
                this.encoding = Charset.forName("windows-1251");
                break;
            }
            case 17: {
                this.encoding = Charset.forName("windows-1254");
            }
        }
    }

    public OperateResultExTwo<Double, Double> ReadSpindleSpeedAndFeedRate() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(this.BuildReadSingle((short)164, 3, 0, 0, 0, 0), this.BuildReadSingle((short)138, 1, 0, 0, 0, 0), this.BuildReadSingle((short)136, 3, 0, 0, 0, 0), this.BuildReadSingle((short)136, 4, 0, 0, 0, 0), this.BuildReadSingle((short)36, 0, 0, 0, 0, 0), this.BuildReadSingle((short)37, 0, 0, 0, 0, 0), this.BuildReadSingle((short)164, 3, 0, 0, 0, 0)));
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        return OperateResultExTwo.CreateSuccessResult(this.GetFanucDouble(result.get(5), 14), this.GetFanucDouble(result.get(4), 14));
    }

    public OperateResultExOne<FanucSysInfo> ReadSysInfo() {
        return this.fanucSysInfo == null ? new OperateResultExOne<FanucSysInfo>("Must connect device first!") : OperateResultExOne.CreateSuccessResult(this.fanucSysInfo);
    }

    private OperateResult CheckSingleResultLeagle(byte[] result) {
        int status = result[6] * 256 + result[7];
        if (status != 0) {
            return new OperateResultExOne(status, StringResources.Language.UnknownError());
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<FileDirInfo[]> ReadAllDirectoryAndFile(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        byte[] buffer = new byte[256];
        byte[] pathBuffer = path.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(pathBuffer, 0, buffer, 0, pathBuffer.length);
        OperateResultExOne<Integer> readCount = this.ReadAllDirectoryAndFileCount(path);
        if (!readCount.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(readCount);
        }
        if ((Integer)readCount.Content == 0) {
            return OperateResultExOne.CreateSuccessResult(new FileDirInfo[0]);
        }
        int[] splits = SoftBasic.SplitIntegerToArray((Integer)readCount.Content, 20);
        ArrayList<FileDirInfo> list = new ArrayList<FileDirInfo>();
        int already = 0;
        for (int j = 0; j < splits.length; ++j) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildWriteSingle((short)1, (short)179, already, splits[j], 1, 1, buffer)}));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            if (((byte[])read.Content).length == 18 || this.getByteTransform().TransInt16((byte[])read.Content, 10) == 0) {
                read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildWriteSingle((short)1, (short)179, 0, 20, 1, 1, buffer)}));
                if (!read.IsSuccess) {
                    return OperateResultExOne.CreateFailedResult(read);
                }
            }
            byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
            OperateResult checkResult = this.CheckSingleResultLeagle(result);
            if (!checkResult.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(checkResult);
            }
            int count = (result.length - 14) / 128;
            for (int i = 0; i < count; ++i) {
                list.add(new FileDirInfo(this.getByteTransform(), result, 14 + 128 * i));
            }
            already += splits[j];
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ArrayListToArray(FileDirInfo.class, list));
    }

    public OperateResultExOne<Integer> ReadAllDirectoryAndFileCount(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        byte[] buffer = new byte[256];
        byte[] pathBuffer = path.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(pathBuffer, 0, buffer, 0, pathBuffer.length);
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildWriteSingle((short)1, (short)180, 0, 0, 0, 0, buffer)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        OperateResult checkResult = this.CheckSingleResultLeagle(result);
        if (!checkResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(checkResult);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransInt32(result, 14) + this.getByteTransform().TransInt32(result, 18));
    }

    public OperateResultExTwo<String, Integer> ReadSystemProgramCurrent() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)207, 0, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExTwo.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        int number = this.getByteTransform().TransInt32(result.get(0), 14);
        String name = new String(SoftBasic.BytesArraySelectMiddle(result.get(0), 18, 36), this.encoding).trim();
        return OperateResultExTwo.CreateSuccessResult(name, number);
    }

    public OperateResultExOne<Short> ReadLanguage() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)141, 3281, 3281, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        short code = this.getByteTransform().TransInt16(result.get(0), 24);
        this.ChangeTextEncoding(code);
        return OperateResultExOne.CreateSuccessResult(code);
    }

    public OperateResultExOne<Double> ReadSystemMacroValue(int number) {
        OperateResultExOne<double[]> read = this.ReadSystemMacroValue(number, 1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((double[])read.Content)[0]);
    }

    public OperateResultExOne<double[]> ReadSystemMacroValue(int number, int length) {
        int[] lenArray = SoftBasic.SplitIntegerToArray(length, 5);
        int index = number;
        ArrayList<Byte> result = new ArrayList<Byte>();
        for (int i = 0; i < lenArray.length; ++i) {
            OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)21, index, index + lenArray[i] - 1, 0, 0, 0)}));
            if (!read.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read);
            }
            Utilities.ArrayListAddArray(result, SoftBasic.BytesArrayRemoveBegin(this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0), 14));
            index += lenArray[i];
        }
        try {
            return OperateResultExOne.CreateSuccessResult(this.GetFanucDouble(Utilities.getBytes(result), 0, length));
        }
        catch (Exception ex) {
            return new OperateResultExOne<double[]>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(Utilities.getBytes(result), ' '));
        }
    }

    public OperateResult WriteSystemMacroValue(int number, double[] values) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildWriteSingle((short)22, number, number + values.length - 1, 0, 0, values)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        if (this.getByteTransform().TransUInt16(result.get(0), 6) == 0) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(this.getByteTransform().TransUInt16(result.get(0), 6), "Unknown Error");
    }

    public OperateResult WriteCutterLengthShapeOffset(int cutter, double offset) {
        return this.WriteSystemMacroValue(11000 + cutter, new double[]{offset});
    }

    public OperateResult WriteCutterLengthWearOffset(int cutter, double offset) {
        return this.WriteSystemMacroValue(10000 + cutter, new double[]{offset});
    }

    public OperateResult WriteCutterRadiusShapeOffset(int cutter, double offset) {
        return this.WriteSystemMacroValue(13000 + cutter, new double[]{offset});
    }

    public OperateResult WriteCutterRadiusWearOffset(int cutter, double offset) {
        return this.WriteSystemMacroValue(12000 + cutter, new double[]{offset});
    }

    public OperateResultExOne<double[]> ReadFanucAxisLoad() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(this.BuildReadSingle((short)164, 2, 0, 0, 0, 0), this.BuildReadSingle((short)137, 0, 0, 0, 0, 0), this.BuildReadSingle((short)86, 1, 0, 0, 0, 0), this.BuildReadSingle((short)164, 2, 0, 0, 0, 0)));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        int length = -1;
        if (result.get(0).length >= 16) {
            length = this.getByteTransform().TransUInt16(result.get(0), 14);
        }
        if (length < 0 || length * 8 + 14 > result.get(2).length) {
            length = (result.get(2).length - 14) / 8;
        }
        return OperateResultExOne.CreateSuccessResult(this.GetFanucDouble(result.get(2), 14, length));
    }

    public OperateResultExOne<Integer> ReadProgramNumber() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)28, 0, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        OperateResult check = this.CheckSingleResultLeagle(result);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransInt32(result, 14));
    }

    public OperateResultExOne<Double> ReadSpindleLoad() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(this.BuildReadSingle((short)64, 4, -1, 0, 0, 0), this.BuildReadSingle((short)64, 5, -1, 0, 0, 0), this.BuildReadSingle((short)138, 0, 0, 0, 0, 0)));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        if (result.get(0).length >= 18) {
            return OperateResultExOne.CreateSuccessResult(this.GetFanucDouble(result.get(0), 14));
        }
        return new OperateResultExOne<Double>("Read failed, data is too short: " + SoftBasic.ByteToHexString(result.get(0), ' '));
    }

    public OperateResultExOne<Integer> ReadFeedRate() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadMulti((short)2, (short)-32767, 12, 13, 0, 1, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        OperateResult check = this.CheckSingleResultLeagle(result);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(100 - (this.getByteTransform().TransUInt16(result, 14) - 155));
    }

    public OperateResultExOne<Integer> ReadSpindleRate() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadMulti((short)2, (short)-32767, 30, 31, 0, 1, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        OperateResult check = this.CheckSingleResultLeagle(result);
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(check);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransUInt16(result, 14));
    }

    public OperateResultExOne<SysAllCoors> ReadSysAllCoors() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(this.BuildReadSingle((short)164, 0, 0, 0, 0, 0), this.BuildReadSingle((short)137, -1, 0, 0, 0, 0), this.BuildReadSingle((short)136, 1, 0, 0, 0, 0), this.BuildReadSingle((short)136, 2, 0, 0, 0, 0), this.BuildReadSingle((short)163, 0, -1, 0, 0, 0), this.BuildReadSingle((short)38, 0, -1, 0, 0, 0), this.BuildReadSingle((short)38, 1, -1, 0, 0, 0), this.BuildReadSingle((short)38, 2, -1, 0, 0, 0), this.BuildReadSingle((short)38, 3, -1, 0, 0, 0), this.BuildReadSingle((short)164, 0, 0, 0, 0, 0)));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        int length = this.getByteTransform().TransUInt16(result.get(0), 14);
        SysAllCoors allCoors = new SysAllCoors();
        allCoors.Absolute = this.GetFanucDouble(result.get(5), 14, length);
        allCoors.Machine = this.GetFanucDouble(result.get(6), 14, length);
        allCoors.Relative = this.GetFanucDouble(result.get(7), 14, length);
        return OperateResultExOne.CreateSuccessResult(allCoors);
    }

    public OperateResultExOne<SysAlarm[]> ReadSystemAlarm() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)35, -1, 10, 2, 64, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        if (this.getByteTransform().TransUInt16(result.get(0), 12) > 0) {
            int length = this.getByteTransform().TransUInt16(result.get(0), 12) / 80;
            SysAlarm[] alarms = new SysAlarm[length];
            for (int i = 0; i < alarms.length; ++i) {
                alarms[i] = new SysAlarm();
                alarms[i].AlarmId = this.getByteTransform().TransInt32(result.get(0), 14 + 80 * i);
                alarms[i].Type = this.getByteTransform().TransInt16(result.get(0), 20 + 80 * i);
                alarms[i].Axis = this.getByteTransform().TransInt16(result.get(0), 24 + 80 * i);
                int msgLength = this.getByteTransform().TransUInt16(result.get(0), 28 + 80 * i);
                alarms[i].Message = new String(result.get(0), 30 + 80 * i, msgLength, this.encoding);
            }
            return OperateResultExOne.CreateSuccessResult(alarms);
        }
        return OperateResultExOne.CreateSuccessResult(new SysAlarm[0]);
    }

    public OperateResultExOne<Long> ReadTimeData(int timeType) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)288, timeType, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        int millisecond = this.getByteTransform().TransInt32(result.get(0), 18);
        long munite = this.getByteTransform().TransInt32(result.get(0), 14);
        if (millisecond < 0 || millisecond > 60000 || munite < 0L) {
            millisecond = Utilities.getInt(result.get(0), 18);
            munite = Utilities.getInt(result.get(0), 14);
        }
        long seconds = millisecond / 1000;
        return OperateResultExOne.CreateSuccessResult(munite * 60L + seconds);
    }

    public OperateResultExOne<Integer> ReadAlarmStatus() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)26, 0, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransUInt16(result.get(0), 16));
    }

    public OperateResultExOne<SysStatusInfo> ReadSysStatusInfo() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(this.BuildReadSingle((short)25, 0, 0, 0, 0, 0), this.BuildReadSingle((short)225, 0, 0, 0, 0, 0), this.BuildReadSingle((short)152, 0, 0, 0, 0, 0)));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        try {
            ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
            SysStatusInfo statusInfo = new SysStatusInfo();
            statusInfo.Dummy = result.get(1).length >= 16 ? this.getByteTransform().TransInt16(result.get(1), 14) : (short)0;
            statusInfo.TMMode = result.get(2).length >= 16 ? this.getByteTransform().TransInt16(result.get(2), 14) : (short)0;
            statusInfo.WorkMode = CNCWorkMode.values()[this.getByteTransform().TransInt16(result.get(0), 14)];
            statusInfo.RunStatus = CNCRunStatus.values()[this.getByteTransform().TransInt16(result.get(0), 16)];
            statusInfo.Motion = this.getByteTransform().TransInt16(result.get(0), 18);
            statusInfo.MSTB = this.getByteTransform().TransInt16(result.get(0), 20);
            statusInfo.Emergency = this.getByteTransform().TransInt16(result.get(0), 22);
            statusInfo.Alarm = this.getByteTransform().TransInt16(result.get(0), 24);
            statusInfo.Edit = this.getByteTransform().TransInt16(result.get(0), 26);
            return OperateResultExOne.CreateSuccessResult(statusInfo);
        }
        catch (Exception e) {
            return new OperateResultExOne<SysStatusInfo>("CreateSysStatusInfo failed: " + e.getMessage());
        }
    }

    public OperateResultExOne<int[]> ReadProgramList() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)6, 1, 19, 0, 0, 0)}));
        OperateResultExOne<byte[]> check = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)6, 6667, 19, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        if (!check.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        int length = (result.get(0).length - 14) / 72;
        int[] programs = new int[length];
        for (int i = 0; i < length; ++i) {
            programs[i] = this.getByteTransform().TransInt32(result.get(0), 14 + 72 * i);
        }
        return OperateResultExOne.CreateSuccessResult(programs);
    }

    public OperateResultExOne<CutterInfo[]> ReadCutterInfos(int cutterNumber) {
        OperateResultExOne<byte[]> read1 = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)8, 1, cutterNumber, 0, 0, 0)}));
        if (!read1.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read1);
        }
        OperateResultExOne<byte[]> read2 = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)8, 1, cutterNumber, 1, 0, 0)}));
        if (!read2.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read2);
        }
        OperateResultExOne<byte[]> read3 = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)8, 1, cutterNumber, 2, 0, 0)}));
        if (!read3.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read3);
        }
        OperateResultExOne<byte[]> read4 = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)8, 1, cutterNumber, 3, 0, 0)}));
        if (!read4.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read4);
        }
        return this.ExtraCutterInfos((byte[])read1.Content, (byte[])read2.Content, (byte[])read3.Content, (byte[])read4.Content, cutterNumber);
    }

    public OperateResultExOne<Integer> ReadCutterNumber() {
        OperateResultExOne<double[]> read = this.ReadSystemMacroValue(4120, 1);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(new Double(((double[])read.Content)[0]).intValue());
    }

    public OperateResultExOne<byte[]> ReadRData(int start, int end) {
        OperateResultExOne<byte[]> read1 = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadMulti((short)2, (short)-32767, start, end, 5, 0, 0)}));
        if (!read1.IsSuccess) {
            return read1;
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read1.Content, 10));
        int length = this.getByteTransform().TransUInt16(result.get(0), 12);
        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle(result.get(0), 14, length));
    }

    public OperateResultExOne<double[]> ReadDeviceWorkPiecesSize() {
        return this.ReadSystemMacroValue(601, 20);
    }

    public OperateResultExOne<String> ReadCurrentProgram() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)32, 1428, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        return OperateResultExOne.CreateSuccessResult(new String(result, 18, result.length - 18, StandardCharsets.US_ASCII));
    }

    public OperateResult SetCurrentProgram(short programNum) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)3, programNum, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        short err = this.getByteTransform().TransInt16(result, 6);
        if (err == 0) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(err, StringResources.Language.UnknownError());
    }

    public OperateResult StartProcessing() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)1, 0, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        short err = this.getByteTransform().TransInt16(result, 6);
        if (err == 0) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(err, StringResources.Language.UnknownError());
    }

    public OperateResult WriteProgramFile(String file) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(file, new String[0])), StandardCharsets.US_ASCII);
        return this.WriteProgramContent(content, 512);
    }

    public OperateResult WriteProgramContent(String program, int everyWriteSize) {
        return this.WriteProgramContent(program, everyWriteSize, "");
    }

    public OperateResult WriteProgramContent(String program, int everyWriteSize, String path) {
        short err;
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResult(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<Socket> socket = this.CreateSocketAndConnect(this.getIpAddress(), this.getPort(), this.getConnectTimeOut());
        if (!socket.IsSuccess) {
            return socket;
        }
        OperateResultExOne<byte[]> ini1 = this.ReadFromCoreServer((Socket)socket.Content, SoftBasic.HexStringToBytes("a0 a0 a0 a0 00 01 01 01 00 02 00 01"), true, true);
        if (!ini1.IsSuccess) {
            return ini1;
        }
        OperateResultExOne<byte[]> read1 = this.ReadFromCoreServer((Socket)socket.Content, this.BulidWriteProgramFilePre(path), true, true);
        if (!read1.IsSuccess) {
            return read1;
        }
        ArrayList<byte[]> contents = this.BulidWriteProgram(program.getBytes(StandardCharsets.US_ASCII), everyWriteSize);
        for (int i = 0; i < contents.size(); ++i) {
            OperateResultExOne<byte[]> read2 = this.ReadFromCoreServer((Socket)socket.Content, contents.get(i), false, true);
            if (read2.IsSuccess) continue;
            return read2;
        }
        OperateResultExOne<byte[]> read3 = this.ReadFromCoreServer((Socket)socket.Content, new byte[]{-96, -96, -96, -96, 0, 1, 19, 1, 0, 0}, true, true);
        if (!read3.IsSuccess) {
            return read3;
        }
        this.CloseSocket((Socket)socket.Content);
        if (((byte[])read3.Content).length >= 14 && (err = this.getByteTransform().TransInt16((byte[])read3.Content, 12)) != 0) {
            return new OperateResultExOne(err, StringResources.Language.UnknownError());
        }
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<String> ReadProgram(int program) {
        return this.ReadProgram(program, "");
    }

    public OperateResultExOne<String> ReadProgram(int program, String path) {
        return this.ReadProgram("O" + program, path);
    }

    public OperateResultExOne<String> ReadProgram(String program, String path) {
        if (!Authorization.asdniasnfaksndiqwhawfskhfaiw()) {
            return new OperateResultExOne<String>(StringResources.Language.InsufficientPrivileges());
        }
        OperateResultExOne<Socket> socket = this.CreateSocketAndConnect(this.getIpAddress(), this.getPort(), this.getConnectTimeOut());
        if (!socket.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(socket);
        }
        OperateResultExOne<byte[]> ini1 = this.ReadFromCoreServer((Socket)socket.Content, SoftBasic.HexStringToBytes("a0 a0 a0 a0 00 01 01 01 00 02 00 01"), true, true);
        if (!ini1.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(ini1);
        }
        OperateResultExOne<byte[]> read1 = this.ReadFromCoreServer((Socket)socket.Content, this.BuildReadProgramPre(program, path), true, true);
        if (!read1.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read1);
        }
        int err = ((byte[])read1.Content)[12] * 256 + ((byte[])read1.Content)[13];
        if (err != 0) {
            this.CloseSocket((Socket)socket.Content);
            return new OperateResultExOne<String>(err, StringResources.Language.UnknownError());
        }
        StringBuilder sb = new StringBuilder();
        while (true) {
            OperateResultExOne<byte[]> read2 = this.ReadFromCoreServer((Socket)socket.Content, null, true, true);
            if (!read2.IsSuccess) {
                return OperateResultExOne.CreateFailedResult(read2);
            }
            if (((byte[])read2.Content)[6] == 22) {
                sb.append(new String((byte[])read2.Content, 10, ((byte[])read2.Content).length - 10, StandardCharsets.US_ASCII));
                continue;
            }
            if (((byte[])read2.Content)[6] == 23) break;
        }
        OperateResult send = this.Send((Socket)socket.Content, new byte[]{-96, -96, -96, -96, 0, 1, 23, 2, 0, 0});
        if (!send.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(send);
        }
        this.CloseSocket((Socket)socket.Content);
        return OperateResultExOne.CreateSuccessResult(sb.toString());
    }

    public OperateResult DeleteProgram(int program) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)5, program, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        short err = this.getByteTransform().TransInt16(result, 6);
        if (err == 0) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(err, StringResources.Language.UnknownError());
    }

    public OperateResult DeleteFile(String fileName) {
        byte[] buffer = new byte[256];
        Utilities.ByteArrayCopyTo(fileName.getBytes(StandardCharsets.US_ASCII), buffer, 0);
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildWriteSingle((short)1, (short)182, 0, 0, 0, 0, buffer)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        return this.CheckSingleResultLeagle(result);
    }

    public OperateResultExOne<String> ReadCurrentForegroundDir() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)176, 1, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        int index = 0;
        for (int i = 14; i < result.get(0).length; ++i) {
            if (result.get(0)[i] != 0) continue;
            index = i;
            break;
        }
        if (index == 0) {
            index = result.get(0).length;
        }
        return OperateResultExOne.CreateSuccessResult(new String(result.get(0), 14, index - 14, StandardCharsets.US_ASCII));
    }

    public OperateResult SetDeviceProgsCurr(String programName) {
        OperateResultExOne<String> path = this.ReadCurrentForegroundDir();
        if (!path.IsSuccess) {
            return path;
        }
        byte[] buffer = new byte[256];
        byte[] b = ((String)path.Content + programName).getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(b, 0, buffer, 0, b.length);
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildWriteSingle((short)186, 0, 0, 0, 0, buffer)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        ArrayList<byte[]> result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10));
        int status = result.get(0)[10] * 256 + result.get(0)[11];
        if (status == 0) {
            return OperateResult.CreateSuccessResult();
        }
        return new OperateResult(status, StringResources.Language.UnknownError());
    }

    public OperateResultExOne<ToolInformation> ReadToolInfoByGroup(short groupId) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)139, groupId, groupId, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        OperateResult checkResult = this.CheckSingleResultLeagle(result);
        if (!checkResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(checkResult);
        }
        this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)77, 2, 1, 2, 0, 0)}));
        return OperateResultExOne.CreateSuccessResult(new ToolInformation(result, this.getByteTransform()));
    }

    public OperateResultExOne<Integer> ReadUseToolGroupId() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)72, 0, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        OperateResult checkResult = this.CheckSingleResultLeagle(result);
        if (!checkResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(checkResult);
        }
        return OperateResultExOne.CreateSuccessResult(this.getByteTransform().TransInt32(result, 18));
    }

    public OperateResult ClearToolGroup(int start, int end) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)82, start, end, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        return this.CheckSingleResultLeagle(result);
    }

    public OperateResultExOne<Date> ReadCurrentDateTime() {
        OperateResultExOne<Double> read1 = this.ReadSystemMacroValue(3011);
        if (!read1.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read1);
        }
        OperateResultExOne<Double> read2 = this.ReadSystemMacroValue(3012);
        if (!read2.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read2);
        }
        String date = String.valueOf(((Double)read1.Content).intValue());
        String time = String.format("%06d", ((Double)read2.Content).intValue());
        return OperateResultExOne.CreateSuccessResult(new Date(Integer.parseInt(date.substring(0, 4)) - 1900, Integer.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date.substring(6)), Integer.parseInt(time.substring(0, 2)), Integer.parseInt(time.substring(2, 4)), Integer.parseInt(time.substring(4))));
    }

    public OperateResultExOne<Integer> ReadCurrentProduceCount() {
        OperateResultExOne<Double> read = this.ReadSystemMacroValue(3901);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((Double)read.Content).intValue());
    }

    public OperateResultExOne<Integer> ReadExpectProduceCount() {
        OperateResultExOne<Double> read = this.ReadSystemMacroValue(3902);
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return OperateResultExOne.CreateSuccessResult(((Double)read.Content).intValue());
    }

    public OperateResultExOne<FanucOperatorMessage[]> ReadOperatorMessage() {
        int len;
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)52, 0, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin((byte[])read.Content, 10)).get(0);
        OperateResult checkResult = this.CheckSingleResultLeagle(result);
        if (!checkResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(checkResult);
        }
        ArrayList<FanucOperatorMessage> list = new ArrayList<FanucOperatorMessage>();
        int index = 12;
        do {
            len = this.getByteTransform().TransUInt16(result, index);
            list.add(FanucOperatorMessage.CreateMessage(this.getByteTransform(), SoftBasic.BytesArraySelectMiddle(result, index + 2, len), this.encoding));
            index += 2;
        } while ((index += len) < result.length);
        return OperateResultExOne.CreateSuccessResult(Utilities.ArrayListToArray(FanucOperatorMessage.class, list));
    }

    public OperateResultExOne<double[]> ReadDiagnoss(int number, int length, int axis) {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)147, number, number + length - 1, axis, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return this.ParseDiagnoss((byte[])read.Content, length);
    }

    private OperateResultExOne<double[]> ParseDiagnoss(byte[] content, int length) {
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin(content, 10)).get(0);
        OperateResult checkResult = this.CheckSingleResultLeagle(result);
        if (!checkResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(checkResult);
        }
        List<Double> array = new List<Double>();
        int index = 14;
        for (int i = 0; i < length && (index += 8) < result.length; ++i) {
            for (int j = 0; j < 32 && index + j * 8 < result.length; ++j) {
                array.Add(this.GetFanucDouble(result, index + j * 8));
            }
            index += 256;
        }
        return OperateResultExOne.CreateSuccessResult(array.toDoubleArray());
    }

    private OperateResultExOne<String[]> ParseAxisNames(byte[] content) {
        byte[] result = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin(content, 10)).get(0);
        OperateResult checkResult = this.CheckSingleResultLeagle(result);
        if (!checkResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(checkResult);
        }
        int length = this.getByteTransform().TransInt32(result, 10);
        List<String> list = new List<String>();
        for (int i = 0; i < length; i += 4) {
            if (i >= result.length) continue;
            list.Add(Encoding.ASCII.GetString(result, 14 + i, 1));
        }
        return OperateResultExOne.CreateSuccessResult(list.toStringArray());
    }

    public OperateResultExOne<String[]> ReadAxisNames() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)137, 0, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return this.ParseAxisNames((byte[])read.Content);
    }

    public OperateResultExOne<String[]> ReadSpindleNames() {
        OperateResultExOne<byte[]> read = this.ReadFromCoreServer(this.BuildReadArray(new byte[][]{this.BuildReadSingle((short)138, 0, 0, 0, 0, 0)}));
        if (!read.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(read);
        }
        return this.ParseAxisNames((byte[])read.Content);
    }

    private byte[] BuildReadSingle(short code, int a, int b, int c, int d, int e) {
        return this.BuildReadMulti((short)1, code, a, b, c, d, e);
    }

    private byte[] BuildReadMulti(short mode, short code, int a, int b, int c, int d, int e) {
        byte[] buffer = new byte[28];
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte((short)buffer.length), buffer, 0);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(mode), buffer, 2);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(this.opPath), buffer, 4);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(code), buffer, 6);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(a), buffer, 8);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(b), buffer, 12);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(c), buffer, 16);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(d), buffer, 20);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(e), buffer, 24);
        return buffer;
    }

    private byte[] BuildWriteSingle(short code, int a, int b, int c, int d, byte[] data) {
        return this.BuildWriteSingle((short)1, code, a, b, c, d, data);
    }

    private byte[] BuildWriteSingle(short mode, short code, int a, int b, int c, int d, byte[] data) {
        byte[] buffer = new byte[28 + data.length];
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte((short)buffer.length), buffer, 0);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(mode), buffer, 2);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(this.opPath), buffer, 4);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(code), buffer, 6);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(a), buffer, 8);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(b), buffer, 12);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(c), buffer, 16);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(d), buffer, 20);
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte(data.length), buffer, 24);
        if (data.length > 0) {
            Utilities.ByteArrayCopyTo(data, buffer, 28);
        }
        return buffer;
    }

    private byte[] BuildWriteSingle(short code, int a, int b, int c, int d, double[] data) {
        byte[] buffer = new byte[data.length * 8];
        for (int i = 0; i < data.length; ++i) {
            Utilities.ByteArrayCopyTo(this.CreateFromFanucDouble(data[i]), buffer, 0);
        }
        return this.BuildWriteSingle(code, a, b, c, d, buffer);
    }

    private byte[] BuildReadArray(byte[] ... commands) {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write(new byte[]{-96, -96, -96, -96, 0, 1, 33, 1, 0, 30}, 0, 10);
        ms.write(this.getByteTransform().TransByte((short)commands.length), 0, 2);
        for (int i = 0; i < commands.length; ++i) {
            ms.write(commands[i], 0, commands[i].length);
        }
        byte[] buffer = ms.toByteArray();
        Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte((short)(buffer.length - 10)), buffer, 8);
        return buffer;
    }

    private byte[] BulidWriteProgramFilePre(String path) {
        if (!Utilities.IsStringNullOrEmpty(path)) {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (!path.startsWith("N:")) {
                path = "N:" + path;
            }
        }
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write(new byte[]{-96, -96, -96, -96, 0, 1, 17, 1, 2, 4}, 0, 10);
        ms.write(new byte[]{0, 0, 0, 1}, 0, 4);
        for (int i = 0; i < 512; ++i) {
            ms.write(0);
        }
        byte[] buffer = ms.toByteArray();
        if (!Utilities.IsStringNullOrEmpty(path)) {
            Utilities.ByteArrayCopyTo(path.getBytes(StandardCharsets.US_ASCII), buffer, 14);
        }
        return buffer;
    }

    private byte[] BuildReadProgramPre(String programName, String path) {
        if (!Utilities.IsStringNullOrEmpty(path)) {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
            if (!path.startsWith("N:")) {
                path = "N:" + path;
            }
        }
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        ms.write(new byte[]{-96, -96, -96, -96, 0, 1, 21, 1, 2, 4}, 0, 10);
        ms.write(new byte[]{0, 0, 0, 1}, 0, 4);
        for (int i = 0; i < 512; ++i) {
            ms.write(0);
        }
        byte[] buffer = ms.toByteArray();
        String pro = Utilities.IsStringNullOrEmpty(path) ? programName + "-" + programName : path + programName;
        Utilities.ByteArrayCopyTo(pro.getBytes(StandardCharsets.US_ASCII), buffer, 14);
        return buffer;
    }

    private ArrayList<byte[]> BulidWriteProgram(byte[] program, int everyWriteSize) {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        int[] lengths = SoftBasic.SplitIntegerToArray(program.length, everyWriteSize);
        int index = 0;
        for (int i = 0; i < lengths.length; ++i) {
            ByteArrayOutputStream ms = new ByteArrayOutputStream();
            ms.write(new byte[]{-96, -96, -96, -96, 0, 1, 18, 4, 0, 0}, 0, 10);
            ms.write(program, index, lengths[i]);
            byte[] buffer = ms.toByteArray();
            Utilities.ByteArrayCopyTo(this.getByteTransform().TransByte((short)(buffer.length - 10)), buffer, 8);
            list.add(buffer);
            index += lengths[i];
        }
        return list;
    }

    private ArrayList<byte[]> ExtraContentArray(byte[] content) {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        int count = this.getByteTransform().TransUInt16(content, 0);
        int index = 2;
        for (int i = 0; i < count; ++i) {
            int length = this.getByteTransform().TransUInt16(content, index);
            list.add(SoftBasic.BytesArraySelectMiddle(content, index + 2, length - 2));
            index += length;
        }
        return list;
    }

    private OperateResultExOne<CutterInfo[]> ExtraCutterInfos(byte[] content1, byte[] content2, byte[] content3, byte[] content4, int cutterNumber) {
        ArrayList<byte[]> result1 = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin(content1, 10));
        ArrayList<byte[]> result2 = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin(content2, 10));
        ArrayList<byte[]> result3 = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin(content3, 10));
        ArrayList<byte[]> result4 = this.ExtraContentArray(SoftBasic.BytesArrayRemoveBegin(content4, 10));
        boolean check1 = this.getByteTransform().TransInt16(result1.get(0), 6) == 0;
        boolean check2 = this.getByteTransform().TransInt16(result2.get(0), 6) == 0;
        boolean check3 = this.getByteTransform().TransInt16(result3.get(0), 6) == 0;
        boolean check4 = this.getByteTransform().TransInt16(result4.get(0), 6) == 0;
        CutterInfo[] cutters = new CutterInfo[cutterNumber];
        for (int i = 0; i < cutters.length; ++i) {
            cutters[i] = new CutterInfo();
            cutters[i].LengthSharpOffset = check1 ? this.GetFanucDouble(result1.get(0), 14 + 8 * i) : Double.NaN;
            cutters[i].LengthWearOffset = check2 ? this.GetFanucDouble(result2.get(0), 14 + 8 * i) : Double.NaN;
            cutters[i].RadiusSharpOffset = check3 ? this.GetFanucDouble(result3.get(0), 14 + 8 * i) : Double.NaN;
            cutters[i].RadiusWearOffset = check4 ? this.GetFanucDouble(result4.get(0), 14 + 8 * i) : Double.NaN;
        }
        return OperateResultExOne.CreateSuccessResult(cutters);
    }

    @Override
    public String toString() {
        return "FanucSeries0i[" + this.getIpAddress() + ":" + this.getPort() + "]";
    }
}

