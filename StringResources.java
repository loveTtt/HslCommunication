/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication;

import HslCommunication.Language.DefaultLanguage;
import HslCommunication.Language.English;
import java.util.Locale;

public class StringResources {
    public static DefaultLanguage Language;

    public static void SetLanguageChinese() {
        Language = new DefaultLanguage();
    }

    public static void SeteLanguageEnglish() {
        Language = new English();
    }

    static {
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().startsWith("zh")) {
            StringResources.SetLanguageChinese();
        } else {
            StringResources.SeteLanguageEnglish();
        }
        Language = new DefaultLanguage();
    }
}

