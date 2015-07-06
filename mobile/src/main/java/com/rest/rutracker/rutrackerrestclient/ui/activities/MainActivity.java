package com.rest.rutracker.rutrackerrestclient.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.rest.rutracker.rutrackerrestclient.data.api.ApiService;
import com.rest.rutracker.rutrackerrestclient.data.api.ApiServiceHelper;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DataAuthRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.request.ViewTopicRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataLoginResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DescriptionDataResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.TorrentFileDataResponse;

import pct.droid.R;
import pct.droid.base.providers.media.MediaProvider;
import com.rest.rutracker.rutrackerrestclient.ui.fragment.VideoListFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements VideoListFragment.OnFragmentInteractionListener {
    /**
     * USed in {@link IResponseListener} for detect that we received LIST OF XML ENTRY
     */
    public static final int CODE_GET_TORRENT_FEED   = 0;
    private static final int REQUEST_CODE_LOGIN     = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup custom action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        startActivityForResult(new Intent(this,LoginActivity.class), REQUEST_CODE_LOGIN);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(VideoListFragment.newInstance(VideoListFragment.TYPE_OUR_MOVIE), "OUR MOVIE");
        adapter.addFragment(VideoListFragment.newInstance(VideoListFragment.TYPE_FOREIGN_LANG), "FOREIGN MOVIE");
        adapter.addFragment(VideoListFragment.newInstance(VideoListFragment.TYPE_RUSSIAN_SERIES), "OUR SERIES");
        viewPager.setAdapter(adapter);
    }



    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    // response from LoginActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == LoginActivity.RESULT_CODE_LOGINED) {
                // we are logined
                onLogined();
            } else {

            }
        }
    }

    private void onLogined(){
        //VideoListFragment videoListFragment = VideoListFragment.newInstance("name","name2");
        //getSupportFragmentManager().beginTransaction()
        //        .add(R.id.FragmentContainer, videoListFragment)
        //        .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public interface IErrorListener {
        void onError();
    }

    public interface IResponseListener {
        void onResponse(Object id, int code);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent homeIntent = new Intent(this, MainActivity.class);
		homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		setResult(RESULT_CANCELED, homeIntent);
		finish();
	}
}
