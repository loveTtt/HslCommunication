/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Core.Types;

import HslCommunication.Core.Types.FunctionOperateExOne;
import HslCommunication.Core.Types.OperateResult;
import HslCommunication.Core.Types.OperateResultExFour;
import HslCommunication.Core.Types.OperateResultExThree;
import HslCommunication.Core.Types.OperateResultExTwo;

public class OperateResultExOne<T>
extends OperateResult {
    public T Content = null;

    public OperateResultExOne() {
    }

    public OperateResultExOne(String msg) {
        super(msg);
    }

    public OperateResultExOne(int err, String msg) {
        super(err, msg);
    }

    public static <T> OperateResultExOne<T> CreateFailedResult(OperateResult result) {
        OperateResultExOne<T> resultExOne = new OperateResultExOne<T>();
        resultExOne.CopyErrorFromOther(result);
        return resultExOne;
    }

    public static <T> OperateResultExOne<T> CreateSuccessResult(T content) {
        OperateResultExOne<T> result = new OperateResultExOne<T>();
        result.IsSuccess = true;
        result.Content = content;
        result.Message = "success";
        return result;
    }

    public OperateResultExOne<T> Check(FunctionOperateExOne<T, Boolean> check, String message) {
        if (!this.IsSuccess) {
            return this;
        }
        if (check.Action(this.Content).booleanValue()) {
            return this;
        }
        return new OperateResultExOne<T>(message);
    }

    public OperateResultExOne<T> Check(FunctionOperateExOne<T, OperateResult> check) {
        if (!this.IsSuccess) {
            return this;
        }
        OperateResult checkResult = check.Action(this.Content);
        if (!checkResult.IsSuccess) {
            return OperateResultExOne.CreateFailedResult(checkResult);
        }
        return this;
    }

    public OperateResult Then(FunctionOperateExOne<T, OperateResult> func) {
        if (this.IsSuccess) {
            return func.Action(this.Content);
        }
        return this;
    }

    public <TResult> OperateResultExOne<TResult> ThenExOne(FunctionOperateExOne<T, OperateResultExOne<TResult>> func) {
        return this.IsSuccess ? func.Action(this.Content) : OperateResultExOne.CreateFailedResult(this);
    }

    public <TResult1, TResult2> OperateResultExTwo<TResult1, TResult2> ThenExTwo(FunctionOperateExOne<T, OperateResultExTwo<TResult1, TResult2>> func) {
        return this.IsSuccess ? func.Action(this.Content) : OperateResultExTwo.CreateFailedResult(this);
    }

    public <TResult1, TResult2, TResult3> OperateResultExThree<TResult1, TResult2, TResult3> ThenExThree(FunctionOperateExOne<T, OperateResultExThree<TResult1, TResult2, TResult3>> func) {
        return this.IsSuccess ? func.Action(this.Content) : OperateResultExThree.CreateFailedResult(this);
    }

    public <TResult1, TResult2, TResult3, TResult4> OperateResultExFour<TResult1, TResult2, TResult3, TResult4> ThenExFour(FunctionOperateExOne<T, OperateResultExFour<TResult1, TResult2, TResult3, TResult4>> func) {
        return this.IsSuccess ? func.Action(this.Content) : OperateResultExFour.CreateFailedResult(this);
    }
}

