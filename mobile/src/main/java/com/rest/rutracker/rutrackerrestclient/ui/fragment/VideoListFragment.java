package com.rest.rutracker.rutrackerrestclient.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rest.rutracker.rutrackerrestclient.data.api.request.DataAuthRequest;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataResponse;
import com.rest.rutracker.rutrackerrestclient.data.containers.InfoContainer;
import com.rest.rutracker.rutrackerrestclient.data.api.response.DataResponse;
import com.rest.rutracker.rutrackerrestclient.data.model.RutrackerFeedParcer;
import com.rest.rutracker.rutrackerrestclient.ui.activities.MainActivity;
import com.rest.rutracker.rutrackerrestclient.ui.activities.MainActivity.*;
import com.rest.rutracker.rutrackerrestclient.data.api.ApiService;
import com.rest.rutracker.rutrackerrestclient.data.api.ApiServiceHelper;

import com.rest.rutracker.rutrackerrestclient.ui.activities.DetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pct.droid.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideoListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoListFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoListFragment newInstance(String param1, String param2) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public VideoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        getCategoriesRequest(new IResponseListener() {
            @Override
            public void onResponse(Object id, int code) {
                    if(code == MainActivity.CODE_GET_TORRENT_FEED){
                        //List<RutrackerFeedParcer.Entry> entries = (List<RutrackerFeedParcer.Entry>) id;
                    }
            }
        }, new IErrorListener() {
            @Override
            public void onError() {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_video_list, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    private void setupRecyclerView(final RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        getCategoriesRequest(new IResponseListener() {
            @Override
            public void onResponse(Object id, int code) {
                if (code == MainActivity.CODE_GET_TORRENT_FEED) {
                    List<RutrackerFeedParcer.Entry> entries =((DataResponse) id).getXMLEntry();
                    recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
                            entries));
                }
            }
        }, new IErrorListener() {
            @Override
            public void onError() {

            }
        });
    }

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public void getCategoriesRequest(final IResponseListener responseListener
            , final IErrorListener errorListener) {

        ApiServiceHelper.getTorrentFeed(new DataAuthRequest("rebbe2015","101010"),new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData.containsKey(ApiService.ERROR_KEY)) {
                    if (errorListener != null) {
                        errorListener.onError();
                    }
                } else {
                    if (responseListener != null) {
                        responseListener.onResponse(resultData.getSerializable(ApiService.RESPONSE_OBJECT_KEY),MainActivity.CODE_GET_TORRENT_FEED);
                    }
                }
            }
        });

    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<RutrackerFeedParcer.Entry> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundLink;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTitleTextView;
            public final TextView mDescTextView;
            public final TextView mSizeTextView;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTitleTextView  = (TextView) view.findViewById(R.id.titleTextView);
                mDescTextView = (TextView) view.findViewById(R.id.descTextView);
                mSizeTextView = (TextView) view.findViewById(R.id.sizeTextView);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mBoundLink;
            }
        }

        public RutrackerFeedParcer.Entry getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<RutrackerFeedParcer.Entry> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            RutrackerFeedParcer.Entry entry = mValues.get(position);
            holder.mBoundLink = entry.link;
            String titleRaw = entry.title;
            int i = titleRaw.indexOf("/");
            String Title= titleRaw.substring(0, i);
            final String[] split = titleRaw.split("(\\[|\\])+");

            if(split.length == 4){
                holder.mTitleTextView.setText(split[0]);
                holder.mDescTextView.setText(split[1]);
                holder.mSizeTextView.setText(split[3]);
            }else {
                holder.mTitleTextView.setText(split[0]);
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InfoContainer infoContainer = new InfoContainer(split[0], holder.mBoundLink.split("t=")[1] );
                    DetailActivity.startActivity(v.getContext(),infoContainer);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }


}
