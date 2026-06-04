/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Yokogawa;

import HslCommunication.BasicFramework.SoftBasic;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.StringResources;
import java.nio.charset.StandardCharsets;

public class YokogawaSystemInfo {
    public String SystemID = "";
    public String Revision = "";
    public String CpuType = "";
    public int ProgramAreaSize = 0;

    public String toString() {
        return "YokogawaSystemInfo[" + this.SystemID + "]";
    }

    public static OperateResultExOne<YokogawaSystemInfo> Parse(byte[] content) {
        try {
            YokogawaSystemInfo systemInfo = new YokogawaSystemInfo();
            systemInfo.SystemID = new String(content, 0, 16, StandardCharsets.US_ASCII).trim();
            systemInfo.Revision = new String(content, 16, 8, StandardCharsets.US_ASCII).trim();
            systemInfo.CpuType = content[25] == 1 || content[25] == 17 ? "Sequence" : (content[25] == 2 || content[25] == 18 ? "BASIC" : StringResources.Language.UnknownError());
            systemInfo.ProgramAreaSize = content[26] * 256 + content[27];
            return OperateResultExOne.CreateSuccessResult(systemInfo);
        }
        catch (Exception ex) {
            return new OperateResultExOne<YokogawaSystemInfo>("Parse YokogawaSystemInfo failed: " + ex.getMessage() + System.lineSeparator() + "Source: " + SoftBasic.ByteToHexString(content));
        }
    }
}

