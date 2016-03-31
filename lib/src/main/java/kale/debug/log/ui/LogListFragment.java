package kale.debug.log.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kale.debug.log.LogCat;
import kale.debug.log.LogDivider;
import kale.debug.log.LogLoader;
import kale.debug.log.LogParser;
import kale.debug.log.R;
import kale.debug.log.model.LogBean;
import kale.debug.log.util.Level;
import kale.debug.log.util.Options;

/**
 * @author Jack Tony
 * @date 2015/12/7
 */
public class LogListFragment extends Fragment {

    private static final String TAG = "LogListFragment";

    public static final String KEY_LEV = "key_lev";

    private ListView listView;

    private ProgressBar loadingPb;

    private TextView emptyTv;

    private final List<LogBean> data = new ArrayList<>();

    private String tag;

    private Level lev;

    private View shareBtn;

    public static LogListFragment getInstance(String lev) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LEV, lev);
        LogListFragment fragment = new LogListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.log_fragment, container, false);
        shareBtn = root.findViewById(R.id.share_btn);
        lev = LogParser.parseLev(getArguments().getString(KEY_LEV, LogParser.VERBOSE));
        loadingPb = (ProgressBar) root.findViewById(R.id.loading_pb);
        emptyTv = (TextView) root.findViewById(R.id.empty_tv);
        listView = (ListView) root.findViewById(R.id.log_lv);

        setViews();
        updateLog(null);
        return root;
    }

    public void setViews() {
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Share Log File")
                        .setMessage("Share log by other client?")
                        .setNegativeButton("Close", null)
                        .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shareLogFile();
                            }
                        }).create().show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(LogDetailActivity.withIntent(getActivity(), data.get(position)));
            }
        });
        listView.setAdapter(new Adapter(data));
    }

    /**
     * 注意：此方法会被频繁调用，需要注意效率问题
     *
     * @param tag log的tag
     */
    public void updateLog(@Nullable String tag) {
        if (getActivity() == null) {
            Log.e(TAG, "updateLog: activity is null!");
            return;
        }
        this.tag = tag;
        startLoading();
        data.clear();
        Process process = LogCat.getInstance()
                .options(Options.DUMP)
                .withTime()
                .recentLines(1000)
                .filter(tag, lev)
                .commit();

        LogLoader.load(process, handler);
    }

    private LogLoader.LoadHandler handler = new LogLoader.LoadHandler() {
        @Override
        public void handLine(String line) {
            LogBean logBean = getLogBean(line);
            if (logBean != null) {
                data.add(logBean);
            }
        }

        @Override
        public void onComplete() {
            Adapter adapter = (Adapter) listView.getAdapter();
            adapter.notifyDataSetChanged();
            stopLoading(!adapter.isEmpty());
        }
    };

    @Nullable
    private LogBean getLogBean(String line) {
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

    private void shareLogFile() {
        Process process = LogCat.getInstance()
                .options(Options.DUMP)
                .withTime()
                .recentLines(1000)
                .filter(tag, lev)
                .commit();

        final StringBuilder sb = new StringBuilder();
        LogLoader.load(process, new LogLoader.LoadHandler() {
            @Override
            public void handLine(String line) {
                sb.append(line).append("\n");
            }

            @Override
            public void onComplete() {
                File file = LogDivider.saveFile(sb.toString());
                if (file == null) {
                    return;
                }

                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
        loadingPb = null;
        emptyTv = null;
    }

    private void startLoading() {
        loadingPb.setVisibility(View.VISIBLE);
        emptyTv.setVisibility(View.INVISIBLE);
    }

    private void stopLoading(boolean hasData) {
        loadingPb.setVisibility(View.INVISIBLE);
        if (!hasData) {
            emptyTv.setVisibility(View.VISIBLE);
        }
    }
}
