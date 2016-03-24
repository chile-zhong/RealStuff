package com.example.ivor_hu.meizhi.widget;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.ViewerActivity;
import com.ortiz.touch.TouchImageView;

/**
 * Created by Ivor on 2016/2/15.
 */
public class ViewerFragment extends Fragment implements RequestListener<String, GlideDrawable> {
    public static final String TAG = "ViewerFragment";
    public static final String INITIAL_SHOWN = "initial_shown";
    public static final String URL = "url";
    private TouchImageView touchImageView;
    private String mUrl;
    private boolean mInitialShown;
    private boolean mIsHidden = false;

    private static final int FLAG_IMMERSIVE = View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrl = getArguments().getString(URL);
        mInitialShown = getArguments().getBoolean(INITIAL_SHOWN, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewer_pager_item, container, false);
        touchImageView = (TouchImageView) view.findViewById(R.id.picture);
        touchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewerActivity) getActivity()).toggleSysbar();
                toggleToolbar();
//                toggleHideyBar(touchImageView);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewCompat.setTransitionName(touchImageView, mUrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(this)
                .load(mUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade(0)
                .listener(this)
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }

    public static Fragment newInstance(String url, boolean initialShown) {
        Bundle args = new Bundle();
        args.putSerializable(URL, url);
        args.putBoolean(INITIAL_SHOWN, initialShown);

        ViewerFragment fragment = new ViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        Log.e(TAG, "onException: ", e);
        maybeStartPostponedEnterTransition();
        return true;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        touchImageView.setImageDrawable(resource);
        maybeStartPostponedEnterTransition();
        return true;
    }

    private void maybeStartPostponedEnterTransition() {
        if (mInitialShown) {
            getActivity().supportStartPostponedEnterTransition();
        }
    }

    public void toggleToolbar() {
        int flag = touchImageView.getSystemUiVisibility();
        if (mIsHidden) {
//            touchImageView.setSystemUiVisibility(flag & ~FLAG_IMMERSIVE);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        } else {
//            touchImageView.setSystemUiVisibility(flag | FLAG_IMMERSIVE);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        mIsHidden = !mIsHidden;
    }

    public void toggleHideyBar(View view) {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = view.getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN & View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        }

        view.setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    public View getSharedElement() {
        return touchImageView;
    }
}
