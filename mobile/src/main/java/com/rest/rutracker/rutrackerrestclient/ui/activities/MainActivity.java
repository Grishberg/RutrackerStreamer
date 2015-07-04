package com.rest.rutracker.rutrackerrestclient.ui.activities;

import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
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
import com.rest.rutracker.rutrackerrestclient.data.api.request.ViewTopicRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DescriptionDataResponse;
import com.rest.rutracker.rutrackerrestclient.ui.fragment.VideoListFragment;

import pct.droid.R;


public class MainActivity extends AppCompatActivity implements VideoListFragment.OnFragmentInteractionListener {
    /**
     * USed in {@link IResponseListener} for detect that we received LIST OF XML ENTRY
     */
    public static final int CODE_GET_TORRENT_FEED=0;

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

        VideoListFragment videoListFragment=VideoListFragment.newInstance("name","name2");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentContainer, videoListFragment)
                .commit();

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
}
