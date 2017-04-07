package kale.debug.log.util;

import android.support.annotation.Nullable;

import kale.debug.log.LogParser;
import kale.debug.log.model.LogBean;

/**
 * @author Kale
 * @date 2017/4/7
 */
public class LogBeanUtil {

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
        logBean.time = line.substring(0, tagStart - 2);
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
