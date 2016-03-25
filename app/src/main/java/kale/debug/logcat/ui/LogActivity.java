package kale.debug.logcat.ui;

import com.google.gson.Gson;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import kale.debug.logcat.P;
import kale.debug.logcat.R;
import kale.debug.logcat.db.LogcatDB;
import kale.debug.logcat.model.Net;

public class LogActivity extends AppCompatActivity {

    private static final String TAG = "LogActivity";

    private TabLayout mTabLayout;

    private AutoCompleteTextView mEditText;
    
    private ViewPager mViewPager;

    private ProgressDialog mDialog;

    private LogcatDB mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mTabLayout = (TabLayout) findViewById(R.id.log_tl);
        mViewPager = (ViewPager) findViewById(R.id.log_vp);
        mEditText = (AutoCompleteTextView) findViewById(R.id.log_et);
        mEditText.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{P.Category.JS, P.Category.NET, P.Category.PIC, P.Category.OTHER}));
        
        mDialog = new ProgressDialog(LogActivity.this);
        mDialog.setMessage("Loading...");

        Log.d(TAG, "onCreate: 测试");
        Log.i(TAG, "你好啊");

        Net net = new Net();
        net.request = new Net.Request();
        net.request.headers = "header";
        
        P.print(P.Level.ERROR, TAG, "kale message====", TAG, P.Category.NET, new Gson().toJson(net));

        mDialog.show();
        mHelper = new LogcatDB(LogActivity.this);
        mHelper.creatNewTable();
        mHelper.saveToDB(LogActivity.this);
        mDialog.dismiss();
        
        setViews();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //最后关闭helper中的SqliteDataBase
        mHelper.deleteOldTable();
        mHelper.closeDB();
    }

    private void setViews() {
        final List<LogFragment> fragments = new ArrayList<>();
        fragments.add(new LogFragment());
        fragments.add(new LogFragment());
        fragments.add(new LogFragment());
        fragments.add(new LogFragment());
        fragments.add(new LogFragment());

        final String[] titles = new String[]{"all", P.Category.NET, P.Category.JS, P.Category.PIC, P.Category.OTHER};

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(5);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
                updateLog(fragments);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // do noting
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                updateLog(fragments);
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do noting
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do noting
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateLog(fragments, s.toString());
            }
        });


        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                updateLog(fragments, null);
            }
        });
    }

    private void updateLog(List<LogFragment> fragments) {
        String tag = mEditText.getText().toString();
        updateLog(fragments, tag);
    }

    private void updateLog(List<LogFragment> fragments, String tag) {
        int currentPos = mViewPager.getCurrentItem();
        fragments.get(currentPos).updateLog(currentPos, tag);
    }

}
