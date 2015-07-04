/*
 * This file is part of Popcorn Time.
 *
 * Popcorn Time is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Popcorn Time is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Popcorn Time. If not, see <http://www.gnu.org/licenses/>.
 */

package pct.droid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.WindowManager;

import com.squareup.okhttp.Call;

import org.videolan.libvlc.Media;

import java.util.ArrayList;
import java.util.List;

import pct.droid.R;
import pct.droid.base.providers.media.MediaProvider;
import pct.droid.base.torrent.StreamInfo;
import pct.droid.containers.MediaContainer;
import pct.droid.fragments.StreamLoadingFragment;

public class StreamLoadingActivity extends PopcornBaseActivity implements StreamLoadingFragment.FragmentListener {

    public final static String EXTRA_INFO = "mInfo";
    public final static String EXTRA_MEDIA_CODEC = "mMediaCodec";

    private StreamInfo mInfo;
    private StreamLoadingFragment mFragment;

    public static Intent startActivity(Context context, MediaContainer info) {
        Intent i = new Intent(context, StreamLoadingActivity.class);
        i.putExtra(EXTRA_MEDIA_CODEC, info);
		context.startActivity(i);
        return i;
    }

    public static Intent startActivity(Context context, StreamInfo info) {
        Intent i = new Intent(context, StreamLoadingActivity.class);
        i.putExtra(EXTRA_INFO, info);
		context.startActivity(i);
        return i;
    }
/*
    public static Intent startActivity(Context context, StreamInfo info, Pair<View, String>... elements) {
        Intent i = new Intent(context, StreamLoadingActivity.class);
        i.putExtra(EXTRA_INFO, info);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(context, elements);
        ActivityCompat.startActivity(context, i, options.toBundle());
        return i;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setBackgroundDrawableResource(R.color.bg);

        super.onCreate(savedInstanceState, R.layout.activity_streamloading);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!getIntent().hasExtra(EXTRA_INFO) && !getIntent().hasExtra(EXTRA_MEDIA_CODEC)) finish();

        MediaContainer mediaContainer	= (MediaContainer) getIntent().getSerializableExtra(EXTRA_MEDIA_CODEC);
		mInfo = getIntent().getParcelableExtra(EXTRA_INFO);

        pct.droid.base.providers.media.models.Media media =
                new pct.droid.base.providers.media.models.Media(null,null);
		media.isMovie	= true;
		if(mediaContainer != null){
			media.fullImage = mediaContainer.getImageUrl();
			media.image = mediaContainer.getImageUrl();
			media.headerImage = mediaContainer.getImageUrl();
			media.title = mediaContainer.getTorrentName();
			media.year = "2011";
			mInfo = new StreamInfo(media,mediaContainer.getTorrentUrl()
					, "no-subs"
					, ""
			);
		}


		mFragment = (StreamLoadingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    protected void onTorrentServiceConnected() {
        super.onTorrentServiceConnected();
        if (null != mFragment) {
            mFragment.onTorrentServiceConnected();
        }
    }

    @Override
    public void onTorrentServiceDisconnected() {
        super.onTorrentServiceDisconnected();
        if (null != mFragment) {
            mFragment.onTorrentServiceDisconnected();
        }
    }

    @Override
    public StreamInfo getStreamInformation() {
        return mInfo;
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null) {
            mFragment.cancelStream();
        }
        super.onBackPressed();
    }
}