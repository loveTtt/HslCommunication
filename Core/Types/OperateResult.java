/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.Core.Types.FunctionOperate;
import HslCommunication.Core.Types.OperateResultExFour;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;
import HslCommunication.StringResources;

public class OperateResult {
    public boolean IsSuccess = false;
    public String Message = StringResources.Language.UnknownError();
    public int ErrorCode = 10000;

    public OperateResult() {
    }

    public OperateResult(String msg) {
        this.Message = msg;
    }

    public OperateResult(int err, String msg) {
        this.ErrorCode = err;
        this.Message = msg;
    }

    public String ToMessageShowString() {
        return StringResources.Language.ErrorCode() + ":" + this.ErrorCode + "\r\n" + StringResources.Language.TextDescription() + ":" + this.Message;
    }

    public void CopyErrorFromOther(OperateResult result) {
        if (result != null) {
            this.ErrorCode = result.ErrorCode;
            this.Message = result.Message;
        }
    }

    public static OperateResult CreateSuccessResult() {
        OperateResult result = new OperateResult();
        result.IsSuccess = true;
        result.Message = StringResources.Language.SuccessText();
        return result;
    }

    public <T> OperateResultExOne<T> Convert(T content) {
        return this.IsSuccess ? OperateResultExOne.CreateSuccessResult(content) : OperateResultExOne.CreateFailedResult(this);
    }

    public <T1, T2> OperateResultExTwo<T1, T2> Convert(T1 content1, T2 content2) {
        return this.IsSuccess ? OperateResultExTwo.CreateSuccessResult(content1, content2) : OperateResultExTwo.CreateFailedResult(this);
    }

    public <T1, T2, T3> OperateResultExThree<T1, T2, T3> Convert(T1 content1, T2 content2, T3 content3) {
        return this.IsSuccess ? OperateResultExThree.CreateSuccessResult(content1, content2, content3) : OperateResultExThree.CreateFailedResult(this);
    }

    public <T1, T2, T3, T4> OperateResultExFour<T1, T2, T3, T4> Convert(T1 content1, T2 content2, T3 content3, T4 content4) {
        return this.IsSuccess ? OperateResultExFour.CreateSuccessResult(content1, content2, content3, content4) : OperateResultExFour.CreateFailedResult(this);
    }

    public OperateResult Then(FunctionOperate<OperateResult> func) {
        return this.IsSuccess ? func.Action() : this;
    }

    public <T> OperateResultExOne<T> ThenExOne(FunctionOperate<OperateResultExOne<T>> func) {
        return this.IsSuccess ? func.Action() : OperateResultExOne.CreateFailedResult(this);
    }

    public <T1, T2> OperateResultExTwo<T1, T2> ThenExTwo(FunctionOperate<OperateResultExTwo<T1, T2>> func) {
        return this.IsSuccess ? func.Action() : OperateResultExTwo.CreateFailedResult(this);
    }

    public <T1, T2, T3> OperateResultExThree<T1, T2, T3> ThenExThree(FunctionOperate<OperateResultExThree<T1, T2, T3>> func) {
        return this.IsSuccess ? func.Action() : OperateResultExThree.CreateFailedResult(this);
    }

    public <T1, T2, T3, T4> OperateResultExFour<T1, T2, T3, T4> ThenExFour(FunctionOperate<OperateResultExFour<T1, T2, T3, T4>> func) {
        return this.IsSuccess ? func.Action() : OperateResultExFour.CreateFailedResult(this);
    }
}

