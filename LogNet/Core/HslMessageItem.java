/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.LogNet.Core;

import HslCommunication.LogNet.Core.HslMessageDegree;
import HslCommunication.LogNet.Core.LogNetManagment;
import HslCommunication.Utilities;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class HslMessageItem {
    private AtomicInteger IdNumber = new AtomicInteger();
    private long id = 0L;
    private HslMessageDegree degree = HslMessageDegree.DEBUG;
    private int threadId = 0;
    private String text = "";
    private Date time = new Date();
    private String keyWord = "";

    public HslMessageItem() {
        this.id = this.IdNumber.getAndIncrement();
    }

    public long getId() {
        return this.id;
    }

    public HslMessageDegree getDegree() {
        return this.degree;
    }

    public void setDegree(HslMessageDegree degree) {
        this.degree = degree;
    }

    public int getThreadId() {
        return this.threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTime() {
        return this.time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getKeyWord() {
        return this.keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String toString() {
        if (this.keyWord == null || this.keyWord.length() == 0) {
            return "[" + LogNetManagment.GetDegreeDescription(this.degree) + "] " + Utilities.getStringDateShort(this.time, "yyyy-MM-dd HH:mm:ss.SSS") + " Thread[" + String.format("%03d", this.threadId) + "] " + this.text;
        }
        return "[" + LogNetManagment.GetDegreeDescription(this.degree) + "] " + Utilities.getStringDateShort(this.time, "yyyy-MM-dd HH:mm:ss.SSS") + " Thread[" + String.format("%03d", this.threadId) + "] " + this.keyWord + " : " + this.text;
    }

    public String ToStringWithoutKeyword() {
        return "[" + LogNetManagment.GetDegreeDescription(this.degree) + "] " + Utilities.getStringDateShort(this.time, "yyyy-MM-dd HH:mm:ss.SSS") + " Thread[" + String.format("%03d", this.threadId) + "] " + this.text;
    }
}

