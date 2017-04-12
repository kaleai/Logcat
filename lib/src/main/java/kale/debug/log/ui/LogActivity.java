package kale.debug.log.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import kale.debug.log.LogCatCmd;
import kale.debug.log.LogFileDivider;
import kale.debug.log.LogLoader;
import kale.debug.log.LogParser;
import kale.debug.log.R;
import kale.debug.log.constant.Level;
import kale.debug.log.constant.Options;

public class LogActivity extends AppCompatActivity {

    private static final String[] TITLES = {
            LogParser.VERBOSE,
            LogParser.DEBUG,
            LogParser.INFO,
            LogParser.WARN,
            LogParser.ERROR
    };

    private ViewPager mViewPager;

    private List<LogListFragment> mFragments;

    public static void startLogAct(Activity activity) {
        activity.startActivity(new Intent(activity, LogActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kale_log_activity);

        mViewPager = (ViewPager) findViewById(R.id.log_vp);
        PagerTitleStrip mainPts = (PagerTitleStrip) findViewById(R.id.main_pts);
        mainPts.setTextColor(getResources().getColor(android.R.color.white));
        setViews();
    }

    private void setViews() {
        mFragments = new ArrayList<>();
        for (String title : TITLES) {
            mFragments.add(LogListFragment.getInstance(title));
        }

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public CharSequence getPageTitle(int position) {
                return LogParser.parseLev(TITLES[position]).toString();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(TITLES.length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kale_log_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_share) {
            new AlertDialog.Builder(this)
                    .setTitle("Share Log File")
                    .setMessage("Share log by other client?")
                    .setNegativeButton("Close", null)
                    .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shareLogFile(mFragments.get(mViewPager.getCurrentItem()));
                        }
                    }).create().show();
            return true;
        } else {
            if (i == R.id.action_clear) {
                LogCatCmd.getInstance().clear().commit();
                for (LogListFragment fragment : mFragments) {
                    fragment.clearData();
                }
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void shareLogFile(LogListFragment fragment) {
        String tag = fragment.getLogTag();
        Level lev = fragment.getLogLev();
        Process process = LogCatCmd.getInstance()
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
                File file = LogFileDivider.saveFile(sb.toString());
                if (file == null) {
                    return;
                }
                LogFileDivider.shareFile(LogActivity.this, file);
            }
        });
    }

}
