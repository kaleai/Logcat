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
        switch (options) {
            case SILENT:
                return SILENT;
            case FILE:
                return FILE;
            case BYTES:
                return BYTES;
            case COUNT:
                return COUNT;
            case FORMAT:
                return FORMAT;
            case CLEAR:
                return CLEAR;
            case DUMP:
                //return DUMP;
            default:
                return DUMP;
        }
    }

    @CheckResult
    public static String parse(Level level) {
        switch (level) {
            case VERBOSE:
                return VERBOSE;
            case DEBUG:
                return DEBUG;
            case INFO:
                return INFO;
            case WARN:
                return WARN;
            case ERROR:
                return ERROR;
            case FATAL:
                return FATAL;
            case ASSERT:
                //return ASSERT;
            default:
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
