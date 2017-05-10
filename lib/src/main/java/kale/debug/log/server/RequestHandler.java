/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package kale.debug.log.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kale.debug.log.LogParser;
import kale.debug.log.constant.Level;
import kale.debug.log.model.LogBean;
import kale.debug.log.util.Action1;
import kale.debug.log.util.LogBeanUtil;
import kale.debug.log.util.NetUtil;

/**
 * Created by amitshekhar on 06/02/17.
 */

public class RequestHandler {

    private final Gson mGson;

    private final AssetManager mAssets;

    public RequestHandler(Context context) {
        mAssets = context.getResources().getAssets();
        mGson = new GsonBuilder().serializeNulls().create();
    }

    public void handle(Socket socket) throws IOException {
        BufferedReader reader = null;
        PrintStream output = null;
        try {
            String route = null;

            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while (!TextUtils.isEmpty(line = reader.readLine())) {
                if (line.startsWith("GET /")) {
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                }
            }

            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());

            final byte[] bytes;

            if (route == null) {
                bytes = null;
            } else if (route.startsWith("getLogList")) {
                final String response = getLogListResponse();
                bytes = response != null ? response.getBytes() : null;
            } else if (route.startsWith("getListByLev")) {
                final String response = getLogListByLev(route);
                bytes = response != null ? response.getBytes() : null;
            } else {
                bytes = NetUtil.loadContent(route, mAssets);
            }

            if (null == bytes) {
                writeServerError(output);
                return;
            }

            // Send out the content.
            output.println("HTTP/1.0 200 OK");
            output.println("Content-Type: " + NetUtil.detectMimeType(route));
            output.println("Content-Length: " + bytes.length);
            output.println();
            output.write(bytes);
            output.flush();
        } finally {
            try {
                if (null != output) {
                    output.close();
                }
                if (null != reader) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.flush();
    }

    private String getLogListResponse() {
        final RespFutureTask<String> futureTask = new RespFutureTask<>();
        LogBeanUtil.loadLogBeanList(null, Level.VERBOSE, new Action1<List<LogBean>>() {
            @Override
            public void onComplete(List<LogBean> list) {
                futureTask.setResult(mGson.toJson(list));
            }
        });
        return futureTask.get(500, TimeUnit.MILLISECONDS);
    }

    private String getLogListByLev(String route) {
        String lev = null;
        if (route.contains("?lev=")) {
            lev = route.substring(route.indexOf("=") + 1, route.length());
        }
        final RespFutureTask<String> futureTask = new RespFutureTask<>();
        LogBeanUtil.loadLogBeanList(null, LogParser.parseLev(lev), new Action1<List<LogBean>>() {
            @Override
            public void onComplete(List<LogBean> list) {
                futureTask.setResult(mGson.toJson(list));
            }
        });
        return futureTask.get(500, TimeUnit.MILLISECONDS);
    }

    private static class RespFutureTask<V> extends FutureTask<V> {

        RespFutureTask() {
            super(new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return null;
                }
            });
        }

        void setResult(V v) {
            set(v);
        }

        @Override
        public V get(long timeout, @NonNull TimeUnit unit) {
            try {
                return super.get(timeout, unit);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            } catch (TimeoutException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
