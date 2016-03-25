package kale.debug.log;

import android.support.annotation.CheckResult;

import kale.debug.log.util.Level;
import kale.debug.log.util.Options;

/**
 * @author Kale
 * @date 2016/3/25
 */
public class LogParser {

    private static final String SILENT = "-s"; // Set default filter to SILENT. Like '*:s'

    private static final String FILE = "-f"; //   <filename>   Log to FILE. Default to stdout

    private static final String BYTES = "-r"; //  Rotate log every bytes(k). (16 if unspecified). Requires -f 

    private static final String COUNT = "-n"; //  Sets max number of rotated logs to <count>, default 4

    private static final String FORMAT = "-v"; // Sets the log print format, where  is one of: brief process tag thread raw time 

    private static final String CLEAR = "-c"; //  clear (flush) the entire log and e // thread time 

    private static final String DUMP = "-d"; //   dump the log and then exit (don't block) // 不会引起线程阻塞

    ///////////////////////////////////////////////////////////////////////////
    // lev
    ///////////////////////////////////////////////////////////////////////////
    
    public static final String VERBOSE = "V"; //   Verbose (明细)  

    public static final String DEBUG = "D"; //     Debug (调试)

    public static final String INFO = "I"; //      Info (信息)

    public static final String WARN = "W"; //      Warn (警告)

    public static final String ERROR = "E"; //     Error (错误)

    private static final String FATAL = "F"; //     Fatal (严重错误)

    private static final String ASSERT = "S"; //    Silent(Super all output) (最高的优先级, 前所未有的错误); 

    @CheckResult
    public static String parse(Options options) {
        if (options == Options.SILENT) {
            return SILENT;
        } else if (options == Options.FILE) {
            return FILE;
        } else if (options == Options.BYTES) {
            return BYTES;
        } else if (options == Options.COUNT) {
            return COUNT;
        } else if (options == Options.FORMAT) {
            return FORMAT;
        } else if (options == Options.CLEAR) {
            return CLEAR;
        } else if (options == Options.DUMP) {
            return DUMP;
        } else {
            return DUMP;
        }
    }

    @CheckResult
    public static String parse(Level level) {
        if (level == Level.VERBOSE) {
            return VERBOSE;
        } else if (level == Level.DEBUG) {
            return DEBUG;
        } else if (level == Level.INFO) {
            return INFO;
        } else if (level == Level.WARN) {
            return WARN;
        } else if (level == Level.ERROR) {
            return ERROR;
        } else if (level == Level.FATAL) {
            return FATAL;
        } else if (level == Level.ASSERT) {
            return ASSERT;
        } else {
            return ASSERT;
        }
    }

    @CheckResult
    public static Level parseLev(String level) {
        switch (level) {
            case VERBOSE:
                return Level.VERBOSE;
            case DEBUG:
                return Level.DEBUG;
            case INFO:
                return Level.INFO;
            case WARN:
                return Level.WARN;
            case ERROR:
                return Level.ERROR;
            case FATAL:
                return Level.FATAL;
            case ASSERT:
                return Level.ASSERT;
            default:
                return Level.ASSERT;
        }
    }

}
