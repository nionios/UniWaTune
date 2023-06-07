package com.nionios.uniwatune.ui.player;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.data.controllers.MediaPlayerController;
import com.nionios.uniwatune.data.singletons.MediaPlayerStorage;
import com.nionios.uniwatune.databinding.FragmentPlayerBinding;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;

    private String timestampMaker(int timeInMilliseconds) {
        int minutes = (timeInMilliseconds / 1000) / 60;
        int seconds = (timeInMilliseconds / 1000) % 60;
        String timestamp;
        // If seconds are not 2 digits prepend a 0.
        if (seconds > 10) {
            timestamp = minutes + ":" + seconds;
        } else {
            timestamp = minutes + ":0" + seconds;
        }
        return timestamp;
    }

    /* Handy function that handles the UI elements that signal the transition from an AudioFile to
     * another.*/
    private void changeAudioFileUISequence(
            ImageButton triggeredButton,
            View view,
            Animation previousAudioFileAnimation,
            Animation nextAudioFileAnimation) {
        // Initialize our pulse animation
        Animation pulseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
        // As audio starts playing, make the play button reflect that (switch to click to pause)
        Drawable pauseCircleButton = ContextCompat.getDrawable(requireContext(),
                R.drawable.baseline_pause_circle_24);
        // Get our playerViewModel provider to signal change of UI infos
        PlayerViewModel playerViewModel =
                new ViewModelProvider(this).get(PlayerViewModel.class);
        binding.playerPlayButton.setImageDrawable(pauseCircleButton);
        // Signal to user button is pressed with our pulse animation
        triggeredButton.startAnimation(pulseAnimation);
        binding.playerAlbumCoverImageView.startAnimation(previousAudioFileAnimation);
        initializeSeekBar();
        view.postDelayed(() -> {
            playerViewModel.updateUI();
            binding.playerAlbumCoverImageView.startAnimation(nextAudioFileAnimation);
        }, 400);
    }

    private void initializeSeekBar() {
        SeekBar seekbar = binding.seekBar;
        seekbar.setProgress(0, true);
        binding.timestamp.setText("0:00");
    }

    // This function refreshes the info the seekbar has in order for the user to act upon it
    private void refreshSeekBar() {
        SeekBar seekbar = binding.seekBar;
        MediaPlayerStorage localMediaPlayerStorage = MediaPlayerStorage.getInstance();
        // Set the max on the seek bar from the duration of the current song, then on textView
        MediaPlayer currentMediaPlayer = localMediaPlayerStorage.getMediaPlayer();
        int fetchedDuration = currentMediaPlayer.getDuration();
        System.out.println("Fetched duration is: " + fetchedDuration);
        // Convert the fetchedDuration (it's in milliseconds) into minutes and seconds.
        binding.duration.setText(timestampMaker(fetchedDuration));
        // The seekbar's max is in milliseconds, set that normally
        seekbar.setMax(fetchedDuration);
    }

    @SuppressLint("SetTextI18n")
    private void updateSeekBar() {
        SeekBar seekbar = binding.seekBar;
        refreshSeekBar();
        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    // When the progress value has changed
                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {
                        // Re-fetch media player and refresh info every time on progress
                        refreshSeekBar();
                        MediaPlayerStorage localMediaPlayerStorage = MediaPlayerStorage.getInstance();
                        MediaPlayer currentMediaPlayer = localMediaPlayerStorage.getMediaPlayer();
                        // Change the seek bar and seek to desired timestamp
                        int currentProgress = seekBar.getProgress();
                        binding.timestamp.setText(timestampMaker(currentProgress));
                        currentMediaPlayer.seekTo(currentProgress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
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
        // Initializing quickpulse animation for some buttons
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

        // Click listeners on both the previous and the next audio file buttons.
        binding.playerNextButton.setOnClickListener(view -> {
            localMediaPlayerController.playNextAudioFile(getContext());
            Animation slideOutLeftAnimation = AnimationUtils.loadAnimation(
                    getContext(),
                    R.anim.slide_out_left);
            Animation slideInRightAnimation = AnimationUtils.loadAnimation(
                    getContext(),
                    R.anim.slide_in_right);
            changeAudioFileUISequence(
                    binding.playerNextButton,
                    view,
                    slideOutLeftAnimation,
                    slideInRightAnimation);
        });

        binding.playerPreviousButton.setOnClickListener(view -> {
            localMediaPlayerController.playPreviousAudioFile(getContext());
            Animation slideOutRightAnimation = AnimationUtils.loadAnimation(
                    getContext(),
                    android.R.anim.slide_out_right);
            Animation slideInLeftAnimation = AnimationUtils.loadAnimation(
                    getContext(),
                    android.R.anim.slide_in_left);
            changeAudioFileUISequence(
                    binding.playerNextButton,
                    view,
                    slideOutRightAnimation,
                    slideInLeftAnimation);
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

        updateSeekBar();
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