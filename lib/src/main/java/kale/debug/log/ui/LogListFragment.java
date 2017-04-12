package kale.debug.log.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import kale.debug.log.LogParser;
import kale.debug.log.R;
import kale.debug.log.constant.Level;
import kale.debug.log.model.LogBean;
import kale.debug.log.util.Action1;
import kale.debug.log.util.LogBeanUtil;

/**
 * @author Jack Tony
 * @date 2015/12/7
 */
public class LogListFragment extends Fragment {

    private static final String TAG = "LogListFragment";

    public static final String KEY_LEV = "key_lev";

    private ListView listView;

    private ProgressBar loadingPb;

    private String tag;

    private Level lev;

    public static LogListFragment getInstance(String lev) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LEV, lev);

        LogListFragment fragment = new LogListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lev = LogParser.parseLev(getArguments().getString(KEY_LEV, LogParser.VERBOSE));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.kale_log_fragment, container, false);
        loadingPb = (ProgressBar) root.findViewById(R.id.loading_pb);
        listView = (ListView) root.findViewById(R.id.log_lv);
        listView.setEmptyView(root.findViewById(R.id.empty_view));

        updateLog(null);
        final LogAdapter adapter = new LogAdapter(new ArrayList<LogBean>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(LogDetailActivity.withIntent(getActivity(), adapter.getData().get(position)));
            }
        });
        return root;
    }

    public void clearData() {
        LogAdapter adapter = (LogAdapter) listView.getAdapter();
        adapter.setData(Collections.EMPTY_LIST);
        adapter.notifyDataSetChanged();
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
        loadingPb.setVisibility(View.VISIBLE);

        LogBeanUtil.loadLogBeanList(tag, lev, new Action1<List<LogBean>>() {
            @Override
            public void onComplete(List<LogBean> list) {
                LogAdapter adapter = (LogAdapter) listView.getAdapter();
                adapter.setData(list);
                loadingPb.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
        loadingPb = null;
    }

    public String getLogTag() {
        return tag;
    }

    public Level getLogLev() {
        return lev;
    }

}
