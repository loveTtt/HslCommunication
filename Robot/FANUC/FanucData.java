/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Robot.FANUC;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Transfer.IByteTransform;
import HslCommunication.Core.Transfer.RegularByteTransform;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Robot.FANUC.FanucAlarm;
import HslCommunication.Robot.FANUC.FanucHelper;
import HslCommunication.Robot.FANUC.FanucPose;
import HslCommunication.Robot.FANUC.FanucTask;
import HslCommunication.Utilities;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FanucData {
    public FanucAlarm[] AlarmList = null;
    public FanucAlarm AlarmCurrent = null;
    public FanucAlarm AlarmPassword = null;
    public FanucPose CurrentPose = null;
    public FanucPose CurrentPoseUF = null;
    public FanucPose CurrentPose2 = null;
    public FanucPose CurrentPose3 = null;
    public FanucPose CurrentPose4 = null;
    public FanucPose CurrentPose5 = null;
    public FanucTask Task = null;
    public FanucTask TaskIgnoreMacro = null;
    public FanucTask TaskIgnoreKarel = null;
    public FanucTask TaskIgnoreMacroKarel = null;
    public FanucPose[] PosRegGP1 = null;
    public FanucPose[] PosRegGP2 = null;
    public FanucPose[] PosRegGP3 = null;
    public FanucPose[] PosRegGP4 = null;
    public FanucPose[] PosRegGP5 = null;
    public int FAST_CLOCK = 0;
    public int Timer10_TIMER_VAL = 0;
    public float MOR_GRP_CURRENT_ANG = 0.0f;
    public float DUTY_TEMP = 0.0f;
    public String TIMER10_COMMENT = "";
    public String TIMER2_COMMENT = "";
    public FanucPose MNUTOOL1_1 = null;
    public String HTTPKCL_CMDS = "";
    public int[] NumReg1 = null;
    public float[] NumReg2 = null;
    public FanucPose[] DataPosRegMG = null;
    public String[] DIComment = null;
    public String[] DOComment = null;
    public String[] RIComment = null;
    public String[] ROComment = null;
    public String[] UIComment = null;
    public String[] UOComment = null;
    public String[] SIComment = null;
    public String[] SOComment = null;
    public String[] WIComment = null;
    public String[] WOComment = null;
    public String[] WSIComment = null;
    public String[] AIComment = null;
    public String[] AOComment = null;
    public String[] GIComment = null;
    public String[] GOComment = null;
    public String[] STRREGComment = null;
    public String[] STRREG_COMMENT_Comment = null;
    private boolean isIni = false;

    public void LoadByContent(byte[] content) {
        int i;
        Charset encoding;
        RegularByteTransform byteTransform = new RegularByteTransform();
        try {
            encoding = Charset.forName("GB2312");
        }
        catch (Exception ex) {
            encoding = StandardCharsets.UTF_8;
        }
        String[] cmds = FanucHelper.GetFanucCmds();
        int[] indexs = new int[cmds.length - 1];
        int[] length = new int[cmds.length - 1];
        for (i = 1; i < cmds.length; ++i) {
            Matcher matches = Pattern.compile("[0-9]+", 32).matcher(cmds[i]);
            if (matches.find()) {
                indexs[i - 1] = (Integer.parseInt(matches.group()) - 1) * 2;
            }
            if (!matches.find()) continue;
            length[i - 1] = Integer.parseInt(matches.group()) * 2;
        }
        this.AlarmList = FanucData.GetFanucAlarmArray(byteTransform, content, indexs[0], 5, encoding);
        this.AlarmCurrent = FanucAlarm.PraseFrom(byteTransform, content, indexs[1], encoding);
        this.AlarmPassword = FanucAlarm.PraseFrom(byteTransform, content, indexs[2], encoding);
        this.CurrentPose = FanucPose.ParseFrom(byteTransform, content, indexs[3]);
        this.CurrentPoseUF = FanucPose.ParseFrom(byteTransform, content, indexs[4]);
        this.CurrentPose2 = FanucPose.ParseFrom(byteTransform, content, indexs[5]);
        this.CurrentPose3 = FanucPose.ParseFrom(byteTransform, content, indexs[6]);
        this.CurrentPose4 = FanucPose.ParseFrom(byteTransform, content, indexs[7]);
        this.CurrentPose5 = FanucPose.ParseFrom(byteTransform, content, indexs[8]);
        this.Task = FanucTask.ParseFrom(byteTransform, content, indexs[9], encoding);
        this.TaskIgnoreMacro = FanucTask.ParseFrom(byteTransform, content, indexs[10], encoding);
        this.TaskIgnoreKarel = FanucTask.ParseFrom(byteTransform, content, indexs[11], encoding);
        this.TaskIgnoreMacroKarel = FanucTask.ParseFrom(byteTransform, content, indexs[12], encoding);
        this.PosRegGP1 = FanucData.GetFanucPoseArray(byteTransform, content, indexs[13], 10, encoding);
        this.PosRegGP2 = FanucData.GetFanucPoseArray(byteTransform, content, indexs[14], 4, encoding);
        this.PosRegGP3 = FanucData.GetFanucPoseArray(byteTransform, content, indexs[15], 10, encoding);
        this.PosRegGP4 = FanucData.GetFanucPoseArray(byteTransform, content, indexs[16], 10, encoding);
        this.PosRegGP5 = FanucData.GetFanucPoseArray(byteTransform, content, indexs[17], 10, encoding);
        this.FAST_CLOCK = Utilities.getInt(content, indexs[18]);
        this.Timer10_TIMER_VAL = Utilities.getInt(content, indexs[19]);
        this.MOR_GRP_CURRENT_ANG = Utilities.getFloat(content, indexs[20]);
        this.DUTY_TEMP = Utilities.getFloat(content, indexs[21]);
        this.TIMER10_COMMENT = new String(content, indexs[22], 80, encoding).trim();
        this.TIMER2_COMMENT = new String(content, indexs[23], 80, encoding).trim();
        this.MNUTOOL1_1 = FanucPose.ParseFrom(byteTransform, content, indexs[24]);
        this.HTTPKCL_CMDS = new String(content, indexs[25], 80, encoding).trim();
        this.NumReg1 = byteTransform.TransInt32(content, indexs[26], 5);
        this.NumReg2 = byteTransform.TransSingle(content, indexs[27], 5);
        this.DataPosRegMG = new FanucPose[10];
        for (i = 0; i < this.DataPosRegMG.length; ++i) {
            this.DataPosRegMG[i] = new FanucPose();
            this.DataPosRegMG[i].Xyzwpr = byteTransform.TransSingle(content, indexs[29] + i * 50, 9);
            this.DataPosRegMG[i].Config = FanucPose.TransConfigStringArray(byteTransform.TransInt16(content, indexs[29] + 36 + i * 50, 7));
            this.DataPosRegMG[i].Joint = byteTransform.TransSingle(content, indexs[30] + i * 36, 9);
        }
        this.DIComment = FanucData.GetStringArray(content, indexs[31], 80, 3, encoding);
        this.DOComment = FanucData.GetStringArray(content, indexs[32], 80, 3, encoding);
        this.RIComment = FanucData.GetStringArray(content, indexs[33], 80, 3, encoding);
        this.ROComment = FanucData.GetStringArray(content, indexs[34], 80, 3, encoding);
        this.UIComment = FanucData.GetStringArray(content, indexs[35], 80, 3, encoding);
        this.UOComment = FanucData.GetStringArray(content, indexs[36], 80, 3, encoding);
        this.SIComment = FanucData.GetStringArray(content, indexs[37], 80, 3, encoding);
        this.SOComment = FanucData.GetStringArray(content, indexs[38], 80, 3, encoding);
        this.WIComment = FanucData.GetStringArray(content, indexs[39], 80, 3, encoding);
        this.WOComment = FanucData.GetStringArray(content, indexs[40], 80, 3, encoding);
        this.WSIComment = FanucData.GetStringArray(content, indexs[41], 80, 3, encoding);
        this.AIComment = FanucData.GetStringArray(content, indexs[42], 80, 3, encoding);
        this.AOComment = FanucData.GetStringArray(content, indexs[43], 80, 3, encoding);
        this.GIComment = FanucData.GetStringArray(content, indexs[44], 80, 3, encoding);
        this.GOComment = FanucData.GetStringArray(content, indexs[45], 80, 3, encoding);
        this.STRREGComment = FanucData.GetStringArray(content, indexs[46], 80, 3, encoding);
        this.STRREG_COMMENT_Comment = FanucData.GetStringArray(content, indexs[47], 80, 3, encoding);
        this.isIni = true;
    }

    public String toString() {
        if (!this.isIni) {
            return "NULL";
        }
        StringBuilder sb = new StringBuilder();
        FanucData.AppendStringBuilder(sb, "AlarmList", Utilities.TranslateStringArray(this.AlarmList));
        FanucData.AppendStringBuilder(sb, "AlarmCurrent", this.AlarmCurrent.toString());
        FanucData.AppendStringBuilder(sb, "AlarmPassword", this.AlarmPassword.toString());
        FanucData.AppendStringBuilder(sb, "CurrentPose", this.CurrentPose.toString());
        FanucData.AppendStringBuilder(sb, "CurrentPoseUF", this.CurrentPoseUF.toString());
        FanucData.AppendStringBuilder(sb, "CurrentPose2", this.CurrentPose2.toString());
        FanucData.AppendStringBuilder(sb, "CurrentPose3", this.CurrentPose3.toString());
        FanucData.AppendStringBuilder(sb, "CurrentPose4", this.CurrentPose4.toString());
        FanucData.AppendStringBuilder(sb, "CurrentPose5", this.CurrentPose5.toString());
        FanucData.AppendStringBuilder(sb, "Task", this.Task.toString());
        FanucData.AppendStringBuilder(sb, "TaskIgnoreMacro", this.TaskIgnoreMacro.toString());
        FanucData.AppendStringBuilder(sb, "TaskIgnoreKarel", this.TaskIgnoreKarel.toString());
        FanucData.AppendStringBuilder(sb, "TaskIgnoreMacroKarel", this.TaskIgnoreMacroKarel.toString());
        FanucData.AppendStringBuilder(sb, "PosRegGP1", Utilities.TranslateStringArray(this.PosRegGP1));
        FanucData.AppendStringBuilder(sb, "PosRegGP2", Utilities.TranslateStringArray(this.PosRegGP2));
        FanucData.AppendStringBuilder(sb, "PosRegGP3", Utilities.TranslateStringArray(this.PosRegGP3));
        FanucData.AppendStringBuilder(sb, "PosRegGP4", Utilities.TranslateStringArray(this.PosRegGP4));
        FanucData.AppendStringBuilder(sb, "PosRegGP5", Utilities.TranslateStringArray(this.PosRegGP5));
        FanucData.AppendStringBuilder(sb, "FAST_CLOCK", String.valueOf(this.FAST_CLOCK));
        FanucData.AppendStringBuilder(sb, "Timer10_TIMER_VAL", String.valueOf(this.Timer10_TIMER_VAL));
        FanucData.AppendStringBuilder(sb, "MOR_GRP_CURRENT_ANG", String.valueOf(this.MOR_GRP_CURRENT_ANG));
        FanucData.AppendStringBuilder(sb, "DUTY_TEMP", String.valueOf(this.DUTY_TEMP));
        FanucData.AppendStringBuilder(sb, "TIMER10_COMMENT", this.TIMER10_COMMENT);
        FanucData.AppendStringBuilder(sb, "TIMER2_COMMENT", this.TIMER2_COMMENT);
        FanucData.AppendStringBuilder(sb, "MNUTOOL1_1", this.MNUTOOL1_1.toString());
        FanucData.AppendStringBuilder(sb, "HTTPKCL_CMDS", this.HTTPKCL_CMDS);
        FanucData.AppendStringBuilder(sb, "NumReg1", SoftBasic.ArrayFormat(this.NumReg1));
        FanucData.AppendStringBuilder(sb, "NumReg2", SoftBasic.ArrayFormat(this.NumReg2));
        FanucData.AppendStringBuilder(sb, "DataPosRegMG", Utilities.TranslateStringArray(this.DataPosRegMG));
        FanucData.AppendStringBuilder(sb, "DIComment", SoftBasic.ArrayFormat(this.DIComment));
        FanucData.AppendStringBuilder(sb, "DOComment", SoftBasic.ArrayFormat(this.DOComment));
        FanucData.AppendStringBuilder(sb, "RIComment", SoftBasic.ArrayFormat(this.RIComment));
        FanucData.AppendStringBuilder(sb, "ROComment", SoftBasic.ArrayFormat(this.ROComment));
        FanucData.AppendStringBuilder(sb, "UIComment", SoftBasic.ArrayFormat(this.UIComment));
        FanucData.AppendStringBuilder(sb, "UOComment", SoftBasic.ArrayFormat(this.UOComment));
        FanucData.AppendStringBuilder(sb, "SIComment", SoftBasic.ArrayFormat(this.SIComment));
        FanucData.AppendStringBuilder(sb, "SOComment", SoftBasic.ArrayFormat(this.SOComment));
        FanucData.AppendStringBuilder(sb, "WIComment", SoftBasic.ArrayFormat(this.WIComment));
        FanucData.AppendStringBuilder(sb, "WOComment", SoftBasic.ArrayFormat(this.WOComment));
        FanucData.AppendStringBuilder(sb, "WSIComment", SoftBasic.ArrayFormat(this.WSIComment));
        FanucData.AppendStringBuilder(sb, "AIComment", SoftBasic.ArrayFormat(this.AIComment));
        FanucData.AppendStringBuilder(sb, "AOComment", SoftBasic.ArrayFormat(this.AOComment));
        FanucData.AppendStringBuilder(sb, "GIComment", SoftBasic.ArrayFormat(this.GIComment));
        FanucData.AppendStringBuilder(sb, "GOComment", SoftBasic.ArrayFormat(this.GOComment));
        FanucData.AppendStringBuilder(sb, "STRREGComment", SoftBasic.ArrayFormat(this.STRREGComment));
        FanucData.AppendStringBuilder(sb, "STRREG_COMMENT_Comment", SoftBasic.ArrayFormat(this.STRREG_COMMENT_Comment));
        return sb.toString();
    }

    public static OperateResultExOne<FanucData> PraseFrom(byte[] content) {
        FanucData fanucData = new FanucData();
        fanucData.LoadByContent(content);
        return OperateResultExOne.CreateSuccessResult(fanucData);
    }

    private static void AppendStringBuilder(StringBuilder sb, String name, String value) {
        FanucData.AppendStringBuilder(sb, name, new String[]{value});
    }

    private static void AppendStringBuilder(StringBuilder sb, String name, String[] values) {
        sb.append(name);
        sb.append(":");
        if (values.length > 1) {
            sb.append(System.lineSeparator());
        }
        for (int i = 0; i < values.length; ++i) {
            sb.append(values[i]);
            sb.append(System.lineSeparator());
        }
        if (values.length > 1) {
            sb.append(System.lineSeparator());
        }
    }

    private static String[] GetStringArray(byte[] content, int index, int length, int arraySize, Charset encoding) {
        String[] array = new String[arraySize];
        for (int i = 0; i < arraySize; ++i) {
            array[i] = new String(content, index + length * i, length, encoding).trim();
        }
        return array;
    }

    private static FanucPose[] GetFanucPoseArray(IByteTransform byteTransform, byte[] content, int index, int arraySize, Charset encoding) {
        FanucPose[] array = new FanucPose[arraySize];
        for (int i = 0; i < arraySize; ++i) {
            array[i] = FanucPose.ParseFrom(byteTransform, content, index + i * 100);
        }
        return array;
    }

    private static FanucAlarm[] GetFanucAlarmArray(IByteTransform byteTransform, byte[] content, int index, int arraySize, Charset encoding) {
        FanucAlarm[] array = new FanucAlarm[arraySize];
        for (int i = 0; i < arraySize; ++i) {
            array[i] = FanucAlarm.PraseFrom(byteTransform, content, index + 200 * i, encoding);
        }
        return array;
    }
}

