package kale.debug.logcat;

import android.support.annotation.StringDef;
import android.util.Log;

/**
 * @author Kale
 * @date 2015/12/17
 */
public class P {

    public static final String PREFIX = "$-$-$";
    
    private static String json;
    
    @StringDef({Level.VERBOSE, Level.DEBUG, Level.INFO, Level.WARNING, Level.ERROR})
    public @interface Level {
        String VERBOSE = "verbose";
        String DEBUG = "debug";
        String INFO = "info";
        String WARNING = "warning";
        String ERROR = "error";
    }

    @StringDef({Category.NET, Category.PIC, Category.JS, Category.OTHER})
    public @interface Category {
        String NET = "net";
        String PIC = "pic";
        String JS = "js";
        String OTHER = "other";
    }

    public static void print(@Level String level, String tag, String message,  String clsName, @Category String category, String extra) {
        String function = "";
        int line = 1;
        json = PREFIX + 
                "{"
                + "  \"category\": \"" + category + "\","
                + "  \"class_name\": \"" + clsName + "\","
                + "  \"function\": \"" + function + "\","
                + "  \"level\": \"" + level + "\","
                + "  \"line\": " + line + ","
                + "  \"log_time\": " + System.currentTimeMillis() + ","
                + "  \"message\": \"" + message + "\","
                + "  \"extra\":" + extra
                + "}";
        
        switch (level) {
            case Level.VERBOSE:
                Log.v(tag, json);
                break;
            case Level.DEBUG:
                Log.d(tag, json);
                break;
            case Level.INFO:
                Log.i(tag, json);
                break;
            case Level.WARNING:
                Log.w(tag, json);
                break;
            case Level.ERROR:
                Log.e(tag, json);
                break;
        }
        
    }


}
