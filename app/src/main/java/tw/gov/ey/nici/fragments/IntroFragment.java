package tw.gov.ey.nici.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import tw.gov.ey.nici.R;

public class IntroFragment extends YouTubePlayerSupportFragment {
    public static final String YOUTUBE_API_KEY = "AIzaSyA2e_42-4z4KElVeurPFjsESmjzz0PFVfc";
    public static final String YOUTUBE_Url = "YouTube_Url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public IntroFragment() {}

    public static IntroFragment newInstance(String youTubeUrl) {

        IntroFragment introFragment = new IntroFragment();

        Bundle bundle = new Bundle();
        bundle.putString(YOUTUBE_Url, youTubeUrl);

        introFragment.setArguments(bundle);
        introFragment.init();

        return introFragment;
    }

    private void init() {

        initialize(YOUTUBE_API_KEY, new OnInitializedListener() {

            @Override
            public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {
                Toast.makeText(getActivity(), "Failured to Initialize!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                player.setPlayerStateChangeListener(playerStateChangeListener);
                player.setPlaybackEventListener(playbackEventListener);

                if (!wasRestored) {
                    player.cueVideo(getArguments().getString(YOUTUBE_Url));
                }
            }

            private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {

                @Override
                public void onBuffering(boolean arg0) {
                }

                @Override
                public void onPaused() {
                }

                @Override
                public void onPlaying() {
                }

                @Override
                public void onSeekTo(int arg0) {
                }

                @Override
                public void onStopped() {
                }

            };

            private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {

                @Override
                public void onAdStarted() {
                }

                @Override
                public void onError(ErrorReason arg0) {
                }

                @Override
                public void onLoaded(String arg0) {
                }

                @Override
                public void onLoading() {
                }

                @Override
                public void onVideoEnded() {
                }

                @Override
                public void onVideoStarted() {
                }
            };
        });
    }
}
