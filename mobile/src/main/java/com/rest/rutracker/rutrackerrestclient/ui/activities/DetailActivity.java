package com.rest.rutracker.rutrackerrestclient.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rest.rutracker.rutrackerrestclient.data.api.ApiService;
import com.rest.rutracker.rutrackerrestclient.data.api.ApiServiceHelper;
import com.rest.rutracker.rutrackerrestclient.data.api.request.ViewTopicRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DescriptionDataResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.TorrentFileDataResponse;
import com.rest.rutracker.rutrackerrestclient.data.containers.InfoContainer;
import com.rest.rutracker.rutrackerrestclient.data.model.RutrackerFeedParcer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pct.droid.R;
import pct.droid.activities.StreamLoadingActivity;
import pct.droid.containers.MediaContainer;

/**
 * Detail view activity
 */
public class DetailActivity extends AppCompatActivity implements Button.OnClickListener {

	public static final String TORRENT_VIEW_TOPIC_LINK = "VIEW_TOPIC_LINK";
	private static final int CODE_GET_IMAGE = 1;

	private static final String BASE_TORRENT_LINK = "http://dl.rutracker.org/forum/dl.php?t=";

	private static final String DEFAULT_TORRENT_NAME = "The best torrent of the world";
	private static final String DEFAULT_KEY_TORRENT_VIEW_TOPIC = "4869690";

	private FloatingActionButton buttonPlay;
	private ImageView imageFromTorrent;

	private String keyTorrentViewTopic;
	private String nameTorrent;
	private MediaContainer mediaContainer;
	private InfoContainer infoContainer;
	private String imageUrl;

	public final static String EXTRA_INFO = "mInfo";
	private RecyclerView contentRecyclerView;
	private TextView nameTextView;
	private TextView descTextView;

	public static Intent startActivity(Context context, InfoContainer info) {
		Intent i = new Intent(context, DetailActivity.class);
		i.putExtra(EXTRA_INFO, info);
		context.startActivity(i);
		return i;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		Intent intent = getIntent();
		infoContainer = null;
		infoContainer = (InfoContainer) intent.getSerializableExtra(EXTRA_INFO);
		if (infoContainer!=null) {
			keyTorrentViewTopic=infoContainer.getTorrentKey();
			nameTorrent=infoContainer.getTorrentName();
		}

		buttonPlay = (FloatingActionButton) findViewById(R.id.buttonLoadTorrentFile);
		nameTextView = (TextView) findViewById(R.id.TorrentFileName);
		descTextView = (TextView) findViewById(R.id.TorrentFileDesc);
		imageFromTorrent = (ImageView) findViewById(R.id.backdrop);

		getImageAndDesc();
		buttonPlay.setOnClickListener(this);
		nameTextView.setText(nameTorrent);
		descTextView.setText("WAIT PLEASE CONTENT IS LOADING...");
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		CollapsingToolbarLayout collapsingToolbar =
				(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle(nameTorrent);
	}



	private void getImageAndDesc() {
		getImageUrlRequest(new MainActivity.IResponseListener() {
			@Override
			public void onResponse(Object object, int code) {
				if (code == CODE_GET_IMAGE) {
					///
					imageUrl = ((DescriptionDataResponse) object).getUrlImage();
					String html = ((DescriptionDataResponse) object).getHtml();
					String htmlWithOutdownloadsSection = html.split("Download")[0];
					descTextView.setText(Html.fromHtml(htmlWithOutdownloadsSection));
					getImageFromUrlWithPicasso(imageUrl);
					Log.d(imageUrl, imageUrl);
				}
			}
		}, null);
	}

	void getImageFromUrlWithPicasso(String imageUrl){
		Picasso.with(this)
				.load(imageUrl)
				.into(imageFromTorrent);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.buttonLoadTorrentFile:
				onPlayClick();
				break;
		}
	}

	private void onPlayClick(){
		ApiServiceHelper.getTorrent(new ViewTopicRequest(keyTorrentViewTopic), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (!resultData.containsKey(ApiService.ERROR_KEY)) {
					TorrentFileDataResponse torrentBody
							= (TorrentFileDataResponse) resultData.getSerializable(ApiService.RESPONSE_OBJECT_KEY);
					mediaContainer = new MediaContainer(BASE_TORRENT_LINK + keyTorrentViewTopic,
							nameTorrent, imageUrl, torrentBody.getTorrentFile());
					startLoadingActivity(mediaContainer);
				}
			}
		});
	}

	private void startLoadingActivity(MediaContainer mediaContainer){
		StreamLoadingActivity.startActivity(this, mediaContainer);
	}

	public void getImageUrlRequest(final MainActivity.IResponseListener responseListener
			, final MainActivity.IErrorListener errorListener) {

		ApiServiceHelper.getImageUrl(new ViewTopicRequest(keyTorrentViewTopic), new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData.containsKey(ApiService.ERROR_KEY)) {
					if (errorListener != null) {
						errorListener.onError();
					}
				} else {
					if (responseListener != null) {
						responseListener.onResponse(resultData.getSerializable(ApiService.RESPONSE_OBJECT_KEY), CODE_GET_IMAGE);
					}
				}
			}
		});
	}
}