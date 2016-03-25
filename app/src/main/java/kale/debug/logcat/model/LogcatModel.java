package kale.debug.logcat.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Jack Tony
 * @date 2015/12/8
 */
public class LogcatModel {

    @SerializedName("log_time")
    public long time;
    @SerializedName("level")
    public String lev;
    @SerializedName("tag")
    public String tag;
    @SerializedName("message")
    public String msg;
    @SerializedName("class_name")
    public String clsName;
    @SerializedName("category")
    public String category;
    @SerializedName("line")
    public int line;
    @SerializedName("function")
    public String function;
    @SerializedName("extra")
    public Object extra;

    public void reSetData() {
        time = 0;
        lev = null;
        tag = null;
        msg = null;
        clsName = null;
        category = null;
        line = 0;
        function = null;
        extra = null;
    }

}
