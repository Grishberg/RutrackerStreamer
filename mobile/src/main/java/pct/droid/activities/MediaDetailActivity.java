package pct.droid.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import butterknife.Optional;
import pct.droid.R;
import pct.droid.base.beaming.server.BeamServerService;
import pct.droid.base.preferences.Prefs;
import pct.droid.base.providers.media.models.Media;
import pct.droid.base.providers.media.models.Movie;
import pct.droid.base.providers.media.models.Show;
import pct.droid.base.torrent.StreamInfo;
import pct.droid.base.utils.AnimUtils;
import pct.droid.base.utils.NetworkUtils;
import pct.droid.base.utils.PixelUtils;
import pct.droid.base.utils.PrefUtils;
import pct.droid.base.utils.VersionUtils;
import pct.droid.containers.MediaContainer;
import pct.droid.dialogfragments.MessageDialogFragment;
import pct.droid.fragments.BaseDetailFragment;
import pct.droid.fragments.MovieDetailFragment;
import pct.droid.fragments.ShowDetailFragment;
import pct.droid.utils.ActionBarBackground;
import pct.droid.widget.ObservableParallaxScrollView;
import timber.log.Timber;

public class MediaDetailActivity extends PopcornBaseActivity implements BaseDetailFragment.FragmentListener {

    private static Media sMedia;
    private Integer mHeaderHeight = 0, mToolbarHeight = 0, mTopHeight;
    private Boolean mTransparentBar = true, mIsTablet = false;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    TextView mToolbarTitle;
    @InjectView(R.id.scrollview)
    ObservableParallaxScrollView mScrollView;
    @Optional
    @InjectView(R.id.parallax)
    RelativeLayout mParallaxLayout;
    @Optional
    @InjectView(R.id.parallax_color)
    View mParallaxColor;
    @InjectView(R.id.content)
    FrameLayout mContent;
    @InjectView(R.id.logo)
    ImageView mLogo;
    @InjectView(R.id.bg_image)
    ImageView mBgImage;

    public static void startActivity(Context context, Media media) {
        Intent intent = new Intent(context, MediaDetailActivity.class);
        if (media != null)
            sMedia = media;
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        super.onCreate(savedInstanceState, R.layout.activity_mediadetail);
        setSupportActionBar(mToolbar);
        setShowCasting(true);

        // Set transparent toolbar
        // Hacky empty string to make sure title textview is added to the toolbar
        getSupportActionBar().setTitle("   ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarBackground.fadeOut(this);

        // Get Title TextView from the Toolbar
        if (mToolbar.getChildAt(0) instanceof TextView) {
            mToolbarTitle = (TextView) mToolbar.getChildAt(0);
        } else {
            mToolbarTitle = (TextView) mToolbar.getChildAt(1);
        }
        mToolbarTitle.setVisibility(View.INVISIBLE);

        // mParallaxLayout doesn't exist? Then this is a tablet or big screen device
        mIsTablet = mParallaxLayout == null;

        getSupportActionBar().setTitle(sMedia.title);

        mScrollView.setListener(mOnScrollListener);
        mScrollView.setOverScrollEnabled(false);
        // Calculate toolbar scrolling variables
        if (!mIsTablet) {
            int parallaxHeight = mParallaxLayout.getLayoutParams().height = PixelUtils.getScreenHeight(this);
            mTopHeight = (parallaxHeight / 3) * 2;
            ((LinearLayout.LayoutParams) mContent.getLayoutParams()).topMargin = -(parallaxHeight / 3);
            mContent.setMinimumHeight(mTopHeight / 3);

            mParallaxColor.setBackgroundColor(sMedia.color);
            mParallaxColor.getBackground().setAlpha(0);
            mToolbar.setBackgroundColor(sMedia.color);
            mToolbar.getBackground().setAlpha(0);
        } else {
            mTopHeight = (PixelUtils.getScreenHeight(this) / 2);
            ((LinearLayout.LayoutParams) mContent.getLayoutParams()).topMargin = mTopHeight;
            mContent.setMinimumHeight(mTopHeight);
        }

        Fragment fragment = null;
        if (sMedia.isMovie) {
            fragment = MovieDetailFragment.newInstance((Movie) sMedia);
        } else if (sMedia instanceof Show) {
            fragment = ShowDetailFragment.newInstance((Show) sMedia);
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        }

        String imageUrl = sMedia.image;
        if (mIsTablet || !PixelUtils.screenIsPortrait(this)) {
            imageUrl = sMedia.headerImage;
        }
        Picasso.with(this).load(imageUrl).into(mBgImage, new Callback() {
            @Override
            public void onSuccess() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AnimUtils.fadeIn(mBgImage);
                        mLogo.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        supportInvalidateOptionsMenu();

        if (null != mService) {
            mService.stopStreaming();
        }
        BeamServerService.getServer().stop();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void playStream(StreamInfo streamInfo) {
        if (PrefUtils.get(this, Prefs.WIFI_ONLY, true) &&
                !NetworkUtils.isWifiConnected(this) &&
                NetworkUtils.isNetworkConnected(this)) {
            MessageDialogFragment.show(getFragmentManager(), R.string.wifi_only, R.string.wifi_only_message);
        } else {
            if(mService != null) {
                mService.startForeground();
            }
			MediaContainer mediaContainer	= new MediaContainer("http://zerkalo-rutor.org/download/442296"
					, "Дом 2015", "http://s017.radikal.ru/i425/1507/31/da69283fba34.png", "390");

			StreamLoadingActivity.startActivity(this, mediaContainer);
//			StreamLoadingActivity.startActivity(this, streamInfo);
/*
            if (VersionUtils.isLollipop()) {
                mScrollView.smoothScrollTo(0, 0);
                StreamLoadingActivity.startActivity(this, streamInfo, Pair.create((View) mBgImage, mBgImage.getTransitionName()));
            } else {
                StreamLoadingActivity.startActivity(this, streamInfo);
            }
*/
        }
    }

    public void setSubScrollListener(ObservableParallaxScrollView.Listener subScrollListener) {
        mSubOnScrollListener = subScrollListener;
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    /* The scroll listener makes the toolbar scroll off the screen when the user scroll all the way down. And it appears again on scrolling up. */
    private ObservableParallaxScrollView.Listener mSubOnScrollListener = null;
    private ObservableParallaxScrollView.Listener mOnScrollListener = new ObservableParallaxScrollView.Listener() {
        @Override
        public void onScroll(int scrollY, ObservableParallaxScrollView.Direction direction) {
            if (mToolbarHeight == 0) {
                mToolbarHeight = mToolbar.getHeight();
                mHeaderHeight = mTopHeight - mToolbarHeight;
                Timber.d("mHeaderHeight: %d", mHeaderHeight);
            }

            if (!mIsTablet) {
                if (scrollY > 0) {
                    if (scrollY < mHeaderHeight) {
                        float diff = (float) scrollY / (float) mHeaderHeight;
                        int alpha = (int) Math.ceil(255 * diff);
                        mParallaxColor.getBackground().setAlpha(alpha);
                        mToolbar.getBackground().setAlpha(0);
                        AnimUtils.fadeOut(mToolbarTitle);
                    } else {
                        mToolbar.getBackground().setAlpha(255);
                        mParallaxColor.getBackground().setAlpha(255);
                        AnimUtils.fadeIn(mToolbarTitle);
                    }
                }
            } else {
                /* Fade out when over header */
                if (mTopHeight - scrollY < 0) {
                    if (mTransparentBar) {
                        mTransparentBar = false;
                        ActionBarBackground.changeColor(MediaDetailActivity.this, sMedia.color, true);
                    }
                } else {
                    if (!mTransparentBar) {
                        mTransparentBar = true;
                        ActionBarBackground.fadeOut(MediaDetailActivity.this);
                    }
                }
            }

            if (mSubOnScrollListener != null) {
                mSubOnScrollListener.onScroll(scrollY, direction);
            }
        }
    };
}