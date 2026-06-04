/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.LogNet.Core;

import HslCommunication.LogNet.Core.HslMessageDegree;

public interface ILogNet {
    public int LogSaveMode();

    public void RecordMessage(HslMessageDegree var1, String var2, String var3);

    public void WriteDebug(String var1);

    public void WriteDebug(String var1, String var2);

    public void WriteDescription(String var1);

    public void WriteError(String var1);

    public void WriteError(String var1, String var2);

    public void WriteException(String var1, Exception var2);

    public void WriteException(String var1, String var2, Exception var3);

    public void WriteFatal(String var1);

    public void WriteFatal(String var1, String var2);

    public void WriteInfo(String var1);

    public void WriteInfo(String var1, String var2);

    public void WriteNewLine();

    public void WriteWarn(String var1);

    public void WriteWarn(String var1, String var2);

    public void SetMessageDegree(HslMessageDegree var1);

    public void FiltrateKeyword(String var1);

    public String[] GetExistLogFileNames();
}

