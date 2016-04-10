package tw.gov.ey.nici.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.Toast;
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

        mImageButton = (ImageButton) mView.findViewById(R.id.youTubeImageButton);
        mImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                    YOUTUBE_API_KEY, YOUTUBE_VIDEO_ID, 0, true, true));
            }
        });

        return mView;
    }
}
