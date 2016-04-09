package tw.gov.ey.nici.utils;

import tw.gov.ey.nici.models.NiciContent;

public class NiciContentUtils {
    // TODO change this implementation to a better one
    //       if I have time
    private static final String SETTING_SMALL_STR = "small";
    private static final String SETTING_MEDIUM_STR = "medium";
    private static final String SETTING_LARGE_STR = "large";
    private static final NiciContent.Setting DEFAULT_SETTING = NiciContent.Setting.MEDIUM;

    public static NiciContent.Setting getSetting(String setting) {
        if (setting == null) {
            return DEFAULT_SETTING;
        }
        switch (setting.toLowerCase()) {
            case SETTING_SMALL_STR:
                return NiciContent.Setting.SMALL;
            case SETTING_MEDIUM_STR:
                return NiciContent.Setting.MEDIUM;
            case SETTING_LARGE_STR:
                return NiciContent.Setting.LARGE;
            default:
        }
        return DEFAULT_SETTING;
    }
}
