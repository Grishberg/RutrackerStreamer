package com.rest.rutracker.rutrackerrestclient.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rest.rutracker.rutrackerrestclient.data.api.ApiService;
import com.rest.rutracker.rutrackerrestclient.data.api.ApiServiceHelper;
import com.rest.rutracker.rutrackerrestclient.data.api.request.ViewTopicRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataResponse;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DescriptionDataResponse;
import com.rest.rutracker.rutrackerrestclient.data.containers.InfoContainer;
import com.rest.rutracker.rutrackerrestclient.data.model.Cheeses;
import com.rest.rutracker.rutrackerrestclient.data.model.RutrackerFeedParcer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pct.droid.R;
import pct.droid.activities.StreamLoadingActivity;
import pct.droid.containers.MediaContainer;

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
        contentRecyclerView = (RecyclerView) findViewById(R.id.scrollableview);
        buttonPlay.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        toolbar.setTitle(nameTorrent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(nameTorrent);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getImageUrl();

        imageFromTorrent = (ImageView) findViewById(R.id.backdrop);
    }


    private void setupRecyclerView(final RecyclerView recyclerView, String html) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        ArrayList<String> objects = new ArrayList<>();
        objects.add(html);
        recyclerView.setAdapter(new SimpleContentAdapter(this,
                objects));
    }



    private void getImageUrl() {
        getImageUrlRequest(new MainActivity.IResponseListener() {
            @Override
            public void onResponse(Object object, int code) {
                if (code == CODE_GET_IMAGE) {
                    ///
                    imageUrl = ((DescriptionDataResponse) object).getUrlImage();
                    String html = ((DescriptionDataResponse) object).getHtml();
                    setupRecyclerView(contentRecyclerView, html);
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
                if (mediaContainer==null) {
                    mediaContainer=new MediaContainer(BASE_TORRENT_LINK + keyTorrentViewTopic,
                            nameTorrent, imageUrl);
                }
                StreamLoadingActivity.startActivity(this ,mediaContainer);
                break;
        }
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

    public class SimpleContentAdapter
            extends RecyclerView.Adapter<SimpleContentAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundLink;

            public final View mView;
            public final TextView mTitleTextView;
            public TextView mDescTextView;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitleTextView  = (TextView) view.findViewById(R.id.titleTextView);
                mDescTextView = (TextView) view.findViewById(R.id.descTextView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mBoundLink;
            }
        }

        public String getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleContentAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_content_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String Content = mValues.get(position);
            holder.mTitleTextView.setText(nameTorrent);
            holder.mDescTextView.setText(Html.fromHtml(Content));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }


}
