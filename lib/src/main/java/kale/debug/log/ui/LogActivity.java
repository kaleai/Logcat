package kale.debug.log.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import kale.debug.log.LogParser;
import kale.debug.log.R;
import kale.debug.log.LogCat;
import kale.debug.log.util.TextWatcherAdapter;


public class LogActivity extends AppCompatActivity {

    private static final String[] TITLES = {
            LogParser.VERBOSE,
            LogParser.DEBUG,
            LogParser.INFO,
            LogParser.WARN,
            LogParser.ERROR};

    private TabLayout tabLayout;

    private EditText tagEt;

    private ViewPager mainVp;

    private View clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tabLayout = (TabLayout) findViewById(R.id.log_tl);
        mainVp = (ViewPager) findViewById(R.id.log_vp);
        tagEt = (EditText) findViewById(R.id.log_et);
        clearBtn = findViewById(R.id.clear_btn);
        setViews();
    }

    private void setViews() {
        final List<LogListFragment> fragments = new ArrayList<>();
        for (String title : TITLES) {
            fragments.add(LogListFragment.getInstance(title));
        }

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public CharSequence getPageTitle(int position) {
                return TITLES[position];
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        mainVp.setAdapter(adapter);
        mainVp.setOffscreenPageLimit(TITLES.length);

        tabLayout.setupWithViewPager(mainVp);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mainVp.setCurrentItem(position);
                updateLog(fragments.get(position));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // refresh log
                updateLog(fragments.get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
        });
        tagEt.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                updateLog(fragments.get(mainVp.getCurrentItem()), s.toString());
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogCat.getInstance().clear().commit();
                updateLog(fragments.get(mainVp.getCurrentItem()));
            }
        });
    }

    private void updateLog(LogListFragment fragment) {
        updateLog(fragment, tagEt.getText().toString());
    }

    private void updateLog(LogListFragment fragment, @Nullable String tag) {
        if (tag == null) {
            tag = tagEt.getText().toString();
        }
        fragment.updateLog(tag);
    }

}
