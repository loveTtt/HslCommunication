/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.LogNet.Core;

import HslCommunication.Core.Thread.SimpleHybirdLock;
import HslCommunication.Core.Types.Environment;
import HslCommunication.Core.Types.List;
import HslCommunication.LogNet.Core.HslMessageDegree;
import HslCommunication.LogNet.Core.HslMessageItem;
import HslCommunication.LogNet.Core.ILogNet;
import HslCommunication.LogNet.Core.LogNetManagment;
import HslCommunication.Utilities;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class LogNetBase
implements ILogNet {
    protected SimpleHybirdLock m_fileSaveLock;
    private HslMessageDegree m_messageDegree = HslMessageDegree.DEBUG;
    private Queue<HslMessageItem> m_WaitForSave;
    private SimpleHybirdLock m_simpleHybirdLock;
    private int m_SaveStatus = 0;
    private List<String> filtrateKeyword;
    private final Object filtrateLock;
    private String lastLogSaveFileName = "";
    public boolean LogThreadID = true;
    public boolean LogStxAsciiCode = true;
    public int HourDeviation = 0;

    public LogNetBase() {
        this.m_fileSaveLock = new SimpleHybirdLock();
        this.m_simpleHybirdLock = new SimpleHybirdLock();
        this.m_WaitForSave = new LinkedList<HslMessageItem>();
        this.filtrateKeyword = new List();
        this.filtrateLock = new Object();
    }

    public void BeforeSaveToFile(HslMessageItem message) {
    }

    @Override
    public void WriteDebug(String text) {
        this.WriteDebug("", text);
    }

    @Override
    public void WriteDebug(String keyWord, String text) {
        this.RecordMessage(HslMessageDegree.DEBUG, keyWord, text);
    }

    @Override
    public void WriteDescription(String description) {
    }

    @Override
    public void WriteInfo(String text) {
        this.WriteInfo("", text);
    }

    @Override
    public void WriteInfo(String keyWord, String text) {
        this.RecordMessage(HslMessageDegree.INFO, keyWord, text);
    }

    @Override
    public void WriteWarn(String text) {
        this.WriteWarn("", text);
    }

    @Override
    public void WriteWarn(String keyWord, String text) {
        this.RecordMessage(HslMessageDegree.WARN, keyWord, text);
    }

    @Override
    public void WriteError(String text) {
        this.WriteError("", text);
    }

    @Override
    public void WriteError(String keyWord, String text) {
        this.RecordMessage(HslMessageDegree.ERROR, keyWord, text);
    }

    @Override
    public void WriteFatal(String text) {
        this.WriteFatal("", text);
    }

    @Override
    public void WriteFatal(String keyWord, String text) {
        this.RecordMessage(HslMessageDegree.FATAL, keyWord, text);
    }

    @Override
    public void WriteException(String keyWord, Exception ex) {
        this.WriteException(keyWord, "", ex);
    }

    @Override
    public void WriteException(String keyWord, String text, Exception ex) {
        this.RecordMessage(HslMessageDegree.FATAL, keyWord, LogNetManagment.GetSaveStringFromException(text, ex));
    }

    @Override
    public int LogSaveMode() {
        return 0;
    }

    @Override
    public void RecordMessage(HslMessageDegree degree, String keyWord, String text) {
        this.WriteToFile(degree, keyWord, text);
    }

    public void WriteDescrition(String description) {
        if (Utilities.IsStringNullOrEmpty(description)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("\u0002");
        stringBuilder.append(Environment.NewLine);
        stringBuilder.append("\u0002/");
        int count = 118 - this.CalculateStringOccupyLength(description);
        if (count >= 8) {
            int count_1 = (count - 8) / 2;
            this.AppendCharToStringBuilder(stringBuilder, '*', count_1);
            stringBuilder.append("   ");
            stringBuilder.append(description);
            stringBuilder.append("   ");
            if (count % 2 == 0) {
                this.AppendCharToStringBuilder(stringBuilder, '*', count_1);
            } else {
                this.AppendCharToStringBuilder(stringBuilder, '*', count_1 + 1);
            }
        } else if (count >= 2) {
            int count_1 = (count - 2) / 2;
            this.AppendCharToStringBuilder(stringBuilder, '*', count_1);
            stringBuilder.append(description);
            if (count % 2 == 0) {
                this.AppendCharToStringBuilder(stringBuilder, '*', count_1);
            } else {
                this.AppendCharToStringBuilder(stringBuilder, '*', count_1 + 1);
            }
        } else {
            stringBuilder.append(description);
        }
        stringBuilder.append("/");
        stringBuilder.append(Environment.NewLine);
        this.RecordMessage(HslMessageDegree.None, "", stringBuilder.toString());
    }

    public void WriteAnyString(String text) {
        this.RecordMessage(HslMessageDegree.None, "", text);
    }

    @Override
    public void WriteNewLine() {
        this.RecordMessage(HslMessageDegree.None, "", "\u0002" + Environment.NewLine);
    }

    @Override
    public void SetMessageDegree(HslMessageDegree degree) {
        this.m_messageDegree = degree;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void FiltrateKeyword(String keyword) {
        Object object = this.filtrateLock;
        synchronized (object) {
            if (!this.filtrateKeyword.contains(keyword)) {
                this.filtrateKeyword.Add(keyword);
            }
        }
    }

    @Override
    public String[] GetExistLogFileNames() {
        return new String[0];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void RemoveFiltrate(String keyword) {
        Object object = this.filtrateLock;
        synchronized (object) {
            if (this.filtrateKeyword.contains(keyword)) {
                this.filtrateKeyword.remove(keyword);
            }
        }
    }

    private void WriteToFile(HslMessageDegree degree, String keyword, String text) {
        if (degree.ordinal() <= this.m_messageDegree.ordinal()) {
            HslMessageItem item = this.GetHslMessageItem(degree, keyword, text);
            this.BeforeSaveToFile(item);
        }
    }

    private void AddItemToCache(HslMessageItem item, boolean start) {
    }

    private HslMessageItem GetAndRemoveLogItem() {
        HslMessageItem result = null;
        this.m_simpleHybirdLock.Enter();
        try {
            result = this.m_WaitForSave.size() > 0 ? this.m_WaitForSave.poll() : null;
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.m_simpleHybirdLock.Leave();
        return result;
    }

    private String HslMessageFormate(HslMessageItem hslMessage, boolean writeFile) {
        StringBuilder stringBuilder = new StringBuilder();
        if (hslMessage.getDegree() != HslMessageDegree.None) {
            if (writeFile && this.LogStxAsciiCode) {
                stringBuilder.append("\u0002");
            }
            stringBuilder.append("[");
            stringBuilder.append(LogNetManagment.GetDegreeDescription(hslMessage.getDegree()));
            stringBuilder.append("] ");
            stringBuilder.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(hslMessage.getTime()));
            stringBuilder.append(" ");
            if (this.LogThreadID) {
                stringBuilder.append("Thread:[");
                stringBuilder.append(String.format("%03d", hslMessage.getThreadId()));
                stringBuilder.append("] ");
            }
            if (!Utilities.IsStringNullOrEmpty(hslMessage.getKeyWord())) {
                stringBuilder.append(hslMessage.getKeyWord());
                stringBuilder.append(" : ");
            }
        }
        stringBuilder.append(hslMessage.getText());
        return stringBuilder.toString();
    }

    public String toString() {
        return "LogNetBase[" + this.LogSaveMode() + "]";
    }

    protected String GetFileSaveName() {
        return "";
    }

    protected void OnWriteCompleted(boolean createNewLogFile) {
    }

    private HslMessageItem GetHslMessageItem(HslMessageDegree degree, String keyWord, String text) {
        HslMessageItem messageItem = new HslMessageItem();
        messageItem.setDegree(degree);
        messageItem.setKeyWord(keyWord);
        messageItem.setText(text);
        messageItem.setThreadId((int)Thread.currentThread().getId());
        if (this.HourDeviation != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(messageItem.getTime());
            calendar.add(10, this.HourDeviation);
            messageItem.setTime(calendar.getTime());
        }
        return messageItem;
    }

    private int CalculateStringOccupyLength(String str) {
        if (Utilities.IsStringNullOrEmpty(str)) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) >= '\u4e00' && str.charAt(i) <= '\u9fbb') {
                result += 2;
                continue;
            }
            ++result;
        }
        return result;
    }

    private void AppendCharToStringBuilder(StringBuilder sb, char c, int count) {
        for (int i = 0; i < count; ++i) {
            sb.append(c);
        }
    }
}

