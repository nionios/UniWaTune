package com.nionios.uniwatune.ui.player;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.data.controllers.MediaPlayerController;
import com.nionios.uniwatune.databinding.FragmentPlayerBinding;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        PlayerViewModel playerViewModel =
                new ViewModelProvider(this).get(PlayerViewModel.class);

        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView playerTitleTextView = binding.playerTitleTextView;
        playerViewModel.getMutableAudioFileTitle().observe(
                getViewLifecycleOwner(),
                playerTitleTextView::setText
        );
        final TextView playerAlbumTextView = binding.playerAlbumTextView;
        playerViewModel.getMutableAudioFileAlbum().observe(
                getViewLifecycleOwner(),
                playerAlbumTextView::setText
        );
        final TextView playerArtistTextView = binding.playerArtistTextView;
        playerViewModel.getMutableAudioFileArtist().observe(
                getViewLifecycleOwner(),
                playerArtistTextView::setText
        );
        final ImageView playerAlbumCoverImageView = binding.playerAlbumCoverImageView;
        playerViewModel.getMutableAudioFileAlbumArt().observe(
                getViewLifecycleOwner(),
                playerAlbumCoverImageView::setImageBitmap
        );

        // Make text scroll when it overflows
        //FIXME: only works on unlock phone
        playerTitleTextView.setSelected(true);
        playerAlbumTextView.setSelected(true);
        playerArtistTextView.setSelected(true);
        // Create a controller object and see if song is playing currently
        // According to the state of the song (playing/not) set the appropriate drawable.
        // This is on initial viewing of the player
        MediaPlayerController localMediaPlayerController = new MediaPlayerController();
        Drawable appropriateDrawableInitial;
        if (localMediaPlayerController.isAudioPlaying()) {
            appropriateDrawableInitial = ContextCompat.getDrawable(requireContext(),
                    R.drawable.baseline_pause_circle_24);
        } else {
            appropriateDrawableInitial = ContextCompat.getDrawable(requireContext(),
                    R.drawable.baseline_play_circle_24);
        }
        binding.playerPlayButton.setImageDrawable(appropriateDrawableInitial);

        // Initializing pulse and quickpulse animation for some buttons
        Animation pulseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
        Animation quickPulseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.quickpulse);
        // Signal the service to start/stop the song on click.
        // According to the state of the song (playing/not) set the appropriate drawable again.
        // This is on click of playerPlayButton on player
        binding.playerPlayButton.setOnClickListener(view -> {
            localMediaPlayerController.toggleCurrentlyPlayingAudioFilePlayState(getContext());
            // According to the state of the song (playing/not) set the appropriate drawable.
            Drawable appropriateDrawableOnClick;
            if (localMediaPlayerController.isAudioPlaying()) {
                appropriateDrawableOnClick = ContextCompat.getDrawable(requireContext(),
                        R.drawable.baseline_play_circle_24);
            } else {
                appropriateDrawableOnClick = ContextCompat.getDrawable(requireContext(),
                        R.drawable.baseline_pause_circle_24);
            }
            binding.playerPlayButton.startAnimation(quickPulseAnimation);
            binding.playerPlayButton.setImageDrawable(appropriateDrawableOnClick);
        });

        binding.playerNextButton.setOnClickListener(view -> {
            localMediaPlayerController.playNextAudioFile(getContext());
            Drawable pauseCircleButton = ContextCompat.getDrawable(requireContext(),
                    R.drawable.baseline_pause_circle_24);
            binding.playerPlayButton.setImageDrawable(pauseCircleButton);
            // Signal to user button is pressed
            binding.playerNextButton.startAnimation(pulseAnimation);

            Animation slideOutLeftAnimation = AnimationUtils.loadAnimation(
                    getContext(),
                    R.anim.slide_out_left);
            playerAlbumCoverImageView.startAnimation(slideOutLeftAnimation);
            view.postDelayed(() -> {
                playerViewModel.updateUI();
                Animation slideInRight = AnimationUtils.loadAnimation(
                        getContext(),
                        R.anim.slide_in_right);
                playerAlbumCoverImageView.startAnimation(slideInRight);
            }, 400);
        });

        binding.playerPreviousButton.setOnClickListener(view -> {
            localMediaPlayerController.playPreviousAudioFile(getContext());
            Drawable pauseCircleButton = ContextCompat.getDrawable(requireContext(),
                    R.drawable.baseline_pause_circle_24);
            // Signal to user button is pressed
            binding.playerPreviousButton.startAnimation(pulseAnimation);

            Animation slideOutRightAnimation = AnimationUtils.loadAnimation(
                    getContext(),
                    android.R.anim.slide_out_right);
            playerAlbumCoverImageView.startAnimation(slideOutRightAnimation);
            binding.playerPlayButton.setImageDrawable(pauseCircleButton);
            view.postDelayed(() -> {
                playerViewModel.updateUI();
                Animation slideInLeft = AnimationUtils.loadAnimation(
                        getContext(),
                        android.R.anim.slide_in_left);
                playerAlbumCoverImageView.startAnimation(slideInLeft);
            }, 400);

        });

        // TODO: revisit this, does not work at all.
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(@NonNull MotionEvent e1,
                                           @NonNull MotionEvent e2,
                                           float velocityX,
                                           float velocityY) {
                        System.out.println("onFling has been called!");
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                System.out.println("Right to Left");
                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                System.out.println("Left to Right");
                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                }
        );

        playerAlbumCoverImageView.setOnTouchListener((view, event) -> gesture.onTouchEvent(event));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*
    //TODO Does not work
    @Override
    public void onBackPressed(View view) {
        NavController navController =
                Navigation.findNavController(view);
        navController.navigate(R.id.action_nav_player_to_nav_Player);
    }
     */
}