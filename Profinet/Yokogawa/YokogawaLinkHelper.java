/*
 * Decompiled with CFR 0.152.
 */
package HslCommunication.Profinet.Yokogawa;

import HslCommunication.StringResources;

public class YokogawaLinkHelper {
    public static String GetErrorMsg(byte code) {
        switch (code) {
            case 1: {
                return StringResources.Language.YokogawaLinkError01();
            }
            case 2: {
                return StringResources.Language.YokogawaLinkError02();
            }
            case 3: {
                return StringResources.Language.YokogawaLinkError03();
            }
            case 4: {
                return StringResources.Language.YokogawaLinkError04();
            }
            case 5: {
                return StringResources.Language.YokogawaLinkError05();
            }
            case 6: {
                return StringResources.Language.YokogawaLinkError06();
            }
            case 7: {
                return StringResources.Language.YokogawaLinkError07();
            }
            case 8: {
                return StringResources.Language.YokogawaLinkError08();
            }
            case 65: {
                return StringResources.Language.YokogawaLinkError41();
            }
            case 66: {
                return StringResources.Language.YokogawaLinkError42();
            }
            case 67: {
                return StringResources.Language.YokogawaLinkError43();
            }
            case 68: {
                return StringResources.Language.YokogawaLinkError44();
            }
            case 81: {
                return StringResources.Language.YokogawaLinkError51();
            }
            case 82: {
                return StringResources.Language.YokogawaLinkError52();
            }
            case -15: {
                return StringResources.Language.YokogawaLinkErrorF1();
            }
        }
        return StringResources.Language.UnknownError();
    }
}

