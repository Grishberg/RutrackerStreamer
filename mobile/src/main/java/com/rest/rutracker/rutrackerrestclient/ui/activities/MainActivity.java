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
import com.rest.rutracker.rutrackerrestclient.data.api.request.DataAuthRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.request.ViewTopicRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataLoginResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DescriptionDataResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.TorrentFileDataResponse;

import pct.droid.R;
import pct.droid.base.providers.media.MediaProvider;
import com.rest.rutracker.rutrackerrestclient.ui.fragment.VideoListFragment;


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


        ApiServiceHelper.getAuth(new DataAuthRequest("rebbe2015","101010"), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (!resultData.containsKey(ApiService.ERROR_KEY)) {
					DataLoginResponse response
							= (DataLoginResponse) resultData.getSerializable(ApiService.RESPONSE_OBJECT_KEY);
					if(response.isAuth()){

						VideoListFragment videoListFragment = VideoListFragment.newInstance("name","name2");
						getSupportFragmentManager().beginTransaction()
								.add(R.id.FragmentContainer, videoListFragment)
								.commit();

					}
				}
			}
		});

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
