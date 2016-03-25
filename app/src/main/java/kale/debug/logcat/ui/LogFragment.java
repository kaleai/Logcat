package kale.debug.logcat.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import kale.debug.logcat.P;
import kale.debug.logcat.R;
import kale.debug.logcat.db.LogcatDB;

/**
 * @author Jack Tony
 * @date 2015/12/7
 */
public class LogFragment extends Fragment {

    private final String[] COLUMN = new String[]{
            LogcatDB.LEVEL,
            LogcatDB.MESSAGE,
            LogcatDB.LOG_TAG,
            LogcatDB.CATEGORY,
            LogcatDB.LOG_TIME};

    private ListView mLogLv;

    private ProgressDialog mDialog;

    private LogcatDB mHelper;

    private SimpleCursorAdapter mAdapter;

    private Cursor cursor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHelper = new LogcatDB(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.log_fragment, null);
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Loading...");
        setViews(root);
        return root;
    }

    public void setViews(View root) {
        mLogLv = (ListView) root.findViewById(R.id.log_lv);
        cursor = mHelper.queryByColumn(COLUMN);
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.log_item, cursor, COLUMN,
                new int[]{R.id.lev_tv, R.id.msg_tv, R.id.tag_tv, R.id.category_tv, R.id.time_tv}, 0);
        mLogLv.setAdapter(mAdapter);
        mLogLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(LogDetailActivity.withIntent(getActivity(), String.valueOf(id)));
            }
        });
    }

    public void updateLog(int pos, String tag) {
        if (getActivity() == null) {
            Log.e("LogFragment", "updateLog: activity = null");
        }
        mDialog.show();
        String category = "all";
        switch (pos) {
            case 0:
                category = "all";
                break;
            case 1:
                category = P.Category.NET;
                break;
            case 2:
                category = P.Category.JS;
                break;
            case 3:
                category = P.Category.PIC;
                break;
            case 4:
                category = P.Category.OTHER;
                break;
        }
        if (TextUtils.isEmpty(tag)) {
            tag = "";
        }
        
        String whereSql;
        if (category.equals("all")) {
            whereSql = "1=1"; 
        } else {
            whereSql = LogcatDB.CATEGORY + "=\'" + category + "\'";
        }
        
        whereSql += " and " + LogcatDB.MESSAGE + " like \'%" + tag + "%\'";
        
        cursor = mHelper.queryByColumn(COLUMN, whereSql);

        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.log_item, cursor, COLUMN,
                new int[]{R.id.lev_tv, R.id.msg_tv, R.id.tag_tv, R.id.category_tv, R.id.time_tv}, 0);
        
        mLogLv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mDialog.dismiss();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLogLv = null;
        mDialog = null;
    }
}
