package kale.debug.logcat.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Kale
 * @date 2015/12/18
 */
public class Net {

    @SerializedName("request")
    public Request request;

    @SerializedName("response")
    public Response response;

    public static class Request {

        @SerializedName("url")
        public String url;

        @SerializedName("method")
        public String method;

        @SerializedName("headers")
        public String headers;

        @SerializedName("body")
        public String body;
    }

    public static class Response {

        @SerializedName("status")
        public int status;

        @SerializedName("headers")
        public String headers;

        @SerializedName("body")
        public String body;

        @SerializedName("data_md5")
        public String md5;
    }

}
