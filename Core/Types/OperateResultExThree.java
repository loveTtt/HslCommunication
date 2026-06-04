/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.Core.Types.FunctionOperateExThree;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExFour;
import HslCommunication.Core.Types.OperateResultExOne;
import HslCommunication.Core.Types.OperateResultExTwo;

public class OperateResultExThree<T1, T2, T3>
extends OperateResult {
    public T1 Content1 = null;
    public T2 Content2 = null;
    public T3 Content3 = null;

    public OperateResultExThree() {
    }

    public OperateResultExThree(String msg) {
        super(msg);
    }

    public OperateResultExThree(int err, String msg) {
        super(err, msg);
    }

    public static <T1, T2, T3> OperateResultExThree<T1, T2, T3> CreateSuccessResult(T1 content1, T2 content2, T3 content3) {
        OperateResultExThree<T1, T2, T3> result = new OperateResultExThree<T1, T2, T3>();
        result.IsSuccess = true;
        result.Content1 = content1;
        result.Content2 = content2;
        result.Content3 = content3;
        result.Message = "success";
        return result;
    }

    public static <T1, T2, T3> OperateResultExThree<T1, T2, T3> CreateFailedResult(OperateResult result) {
        OperateResultExThree<T1, T2, T3> resultExThree = new OperateResultExThree<T1, T2, T3>();
        resultExThree.CopyErrorFromOther(result);
        return resultExThree;
    }

    public OperateResultExThree<T1, T2, T3> Check(FunctionOperateExThree<T1, T2, T3, Boolean> check, String message) {
        if (!this.IsSuccess) {
            return this;
        }
        if (check.Action(this.Content1, this.Content2, this.Content3).booleanValue()) {
            return this;
        }
        return new OperateResultExThree<T1, T2, T3>(message);
    }

    public OperateResultExThree<T1, T2, T3> Check(FunctionOperateExThree<T1, T2, T3, OperateResult> check) {
        if (!this.IsSuccess) {
            return this;
        }
        OperateResult checkResult = check.Action(this.Content1, this.Content2, this.Content3);
        if (!checkResult.IsSuccess) {
            return OperateResultExThree.CreateFailedResult(checkResult);
        }
        return this;
    }

    public OperateResult Then(FunctionOperateExThree<T1, T2, T3, OperateResult> func) {
        if (this.IsSuccess) {
            return func.Action(this.Content1, this.Content2, this.Content3);
        }
        return this;
    }

    public <TResult> OperateResultExOne<TResult> ThenExOne(FunctionOperateExThree<T1, T2, T3, OperateResultExOne<TResult>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2, this.Content3) : OperateResultExOne.CreateFailedResult(this);
    }

    public <TResult1, TResult2> OperateResultExTwo<TResult1, TResult2> ThenExTwo(FunctionOperateExThree<T1, T2, T3, OperateResultExTwo<TResult1, TResult2>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2, this.Content3) : OperateResultExTwo.CreateFailedResult(this);
    }

    public <TResult1, TResult2, TResult3> OperateResultExThree<TResult1, TResult2, TResult3> ThenExThree(FunctionOperateExThree<T1, T2, T3, OperateResultExThree<TResult1, TResult2, TResult3>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2, this.Content3) : OperateResultExThree.CreateFailedResult(this);
    }

    public <TResult1, TResult2, TResult3, TResult4> OperateResultExFour<TResult1, TResult2, TResult3, TResult4> ThenExFour(FunctionOperateExThree<T1, T2, T3, OperateResultExFour<TResult1, TResult2, TResult3, TResult4>> func) {
        return this.IsSuccess ? func.Action(this.Content1, this.Content2, this.Content3) : OperateResultExFour.CreateFailedResult(this);
    }
}

