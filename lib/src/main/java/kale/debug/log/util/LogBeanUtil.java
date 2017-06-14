package kale.debug.log.util;

import java.util.ArrayList;
import java.util.List;

import android.support.annotation.Nullable;

import kale.debug.log.LogCatCmd;
import kale.debug.log.LogLoader;
import kale.debug.log.LogParser;
import kale.debug.log.constant.Level;
import kale.debug.log.constant.Options;
import kale.debug.log.model.LogBean;

/**
 * @author Kale
 * @date 2017/4/7
 */
public class LogBeanUtil {

    private static LogBean oldLogBean = null;

    public static void loadLogBeanList(final String tag, Level lev, final Action1<List<LogBean>> action) {
        final ArrayList<LogBean> list = new ArrayList<>();
        Process process = LogCatCmd.getInstance()
                .options(Options.DUMP)
                .withTime()
                .recentLines(1000)
                .filter(tag, lev)
                .commit();

        LogLoader.load(process, new LogLoader.LoadHandler() {
            @Override
            public void handLine(String line) {
                LogBean logBean = LogBeanUtil.createBeanFromLine(line);
                if (logBean != null) {
                    String msg = logBean.msg;
                    if (msg.contains("FATAL EXCEPTION")
                            || msg.startsWith(" \t... ")
                            || msg.startsWith(" Process: ")) {
                        return;
                    }

                    if (oldLogBean != null && msg.startsWith(" \tat ")) {
//                        oldLogBean.msg += "\n" + msg.replace(" \t", "");
                        oldLogBean.msg += "\n\t\t" + msg;
                    } else {
                        oldLogBean = logBean;
                        list.add(logBean);
                    }
                }
            }

            @Override
            public void onComplete() {
                oldLogBean = null;
                action.onComplete(list);
            }
        });
    }

    /**
     * line：I/System.out( 8151): 04-07 11:19:21.437 D/message
     */
    @Nullable
    public static LogBean createBeanFromLine(String line) {
        LogBean logBean = new LogBean();
        int tagStart = line.indexOf("/");
        int msgStart = line.indexOf("):");

        if (msgStart == -1 || tagStart == -1) {
            return null;
        }

        logBean.tag = line.substring(tagStart + 1, msgStart + 1);
        logBean.msg = line.substring(msgStart + 2);
        String lev = line.substring(tagStart - 1, tagStart);

        logBean.lev = LogParser.parseLev(lev);
        logBean.time = line.substring(6, tagStart - 2);
        return logBean;
    }

    public static void main(String[] args) {
        LogBean bean = createBeanFromLine("04-07 11:19:21.437 I/System.out( 8151): message");
        assert bean != null;
        System.out.println("time: " + bean.time);
        System.out.println("lev: " + bean.lev);
        System.out.println("tag: " + bean.tag);
        System.out.println("msg: " + bean.msg);
    }
}
