/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.LogNet.Core;

import HslCommunication.Core.Types.Environment;
import HslCommunication.LogNet.Core.HslMessageDegree;
import HslCommunication.StringResources;
import HslCommunication.Utilities;

public class LogNetManagment {
    public static String GetDegreeDescription(HslMessageDegree degree) {
        switch (degree) {
            case DEBUG: {
                return StringResources.Language.LogNetDebug();
            }
            case INFO: {
                return StringResources.Language.LogNetInfo();
            }
            case WARN: {
                return StringResources.Language.LogNetWarn();
            }
            case ERROR: {
                return StringResources.Language.LogNetError();
            }
            case FATAL: {
                return StringResources.Language.LogNetFatal();
            }
            case None: {
                return StringResources.Language.LogNetAbandon();
            }
        }
        return StringResources.Language.LogNetAbandon();
    }

    public static String GetSaveStringFromException(String text, Exception ex) {
        StringBuilder builder = new StringBuilder(text);
        if (ex != null) {
            if (!Utilities.IsStringNullOrEmpty(text)) {
                builder.append(" : ");
            }
            try {
                builder.append(StringResources.Language.ExceptionMessage());
                builder.append(ex.getMessage());
                builder.append(Environment.NewLine);
            }
            catch (Exception exception) {
                // empty catch block
            }
            builder.append(Environment.NewLine);
            builder.append("\u0002/=================================================[    Exception    ]================================================/");
        }
        try {
            return builder.toString();
        }
        catch (Exception ex1) {
            return Utilities.IsStringNullOrEmpty(text) ? ex1.getMessage() : text + ":" + ex.getMessage();
        }
    }
}

