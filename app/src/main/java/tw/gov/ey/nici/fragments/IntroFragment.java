package tw.gov.ey.nici.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import android.view.View.OnClickListener;

import tw.gov.ey.nici.R;

public class IntroFragment extends Fragment {
    public static final String YOUTUBE_API_KEY = "AIzaSyA2e_42-4z4KElVeurPFjsESmjzz0PFVfc";
    private String YOUTUBE_VIDEO_ID = "Gx1emgAKkh0";

    private View mView;
    private ImageButton mImageButton;

    public static IntroFragment newInstance() {
        return new IntroFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.intro_fragment, container, false);
        
        if (getChildFragmentManager().findFragmentById(R.id.youtube_fragment) == null) {
            YouTubePlayerSupportFragment fragment = YouTubePlayerSupportFragment.newInstance();
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.youtube_fragment, fragment)
                    .commit();

            fragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
                    youTubePlayer.setOnFullscreenListener(fullscreenListener);
                    youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                    youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
                    if (!restored) {
                        // only load the video, does not play
                        youTubePlayer.cueVideo(YOUTUBE_VIDEO_ID);
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
        }

        mImageButton = (ImageButton) mView.findViewById(R.id.youTubeImageButton);
        // FIXME temporarily hide the image button, remove if needed
        mImageButton.setVisibility(View.GONE);
//        mImageButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),
//                    YOUTUBE_API_KEY, YOUTUBE_VIDEO_ID, 0, true, true));
//            }
//        });

        return mView;
    }

    private YouTubePlayer.OnFullscreenListener fullscreenListener = new YouTubePlayer.OnFullscreenListener() {
        @Override
        public void onFullscreen(boolean isFullscreen) {
            if (!isFullscreen) {
                // prevent youtube player from stuck in landscape after leaving fullscreen
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
        }
    };
}
