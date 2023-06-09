package com.nionios.uniwatune.ui.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.navigation.NavController;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.data.controllers.MediaPlayerController;
import com.nionios.uniwatune.data.helpers.TimestampMaker;
import com.nionios.uniwatune.data.singletons.AudioQueueStorage;
import com.nionios.uniwatune.data.singletons.MediaPlayerStorage;
import com.nionios.uniwatune.databinding.FragmentPlayerBinding;

public class PlayerFragment extends Fragment {
    private FragmentPlayerBinding binding;
    private Handler seekBarHandler;
    private PlayerFragment playerFragment = this;
    private Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            PlayerViewModel playerViewModel =
                    new ViewModelProvider(playerFragment).get(PlayerViewModel.class);
            playerViewModel.updateSeekBar();
            updateProgressOnSeekBar();
            seekBarHandler.postDelayed(this, 1000);
        }
    };


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
        Drawable pauseCircleButton = ContextCompat.getDrawable(getContext(),
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
            binding.playerAlbumCoverImageView.startAnimation(nextAudioFileAnimation);
        }, 400);
    }

    private void stopSeekBarUpdateRunnable () {
        seekBarHandler.removeCallbacks(seekBarRunnable);
    }

    private void createSeekBarUpdateRunnable () {
        seekBarHandler = new Handler();
        //Update seekbar every 1 second through the playerViewModel.
        requireActivity().runOnUiThread(seekBarRunnable);
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
        TimestampMaker timestampMaker = new TimestampMaker();
        binding.duration.setText(timestampMaker.convertMillisecondsToTimestamp(fetchedDuration));
        // The seekbar's max is in milliseconds, set that normally
        seekbar.setMax(fetchedDuration);
    }

    private void updateProgressOnSeekBar () {
        // Set the currrent progress of the song on the seekbar
        SeekBar seekbar = binding.seekBar;
        MediaPlayerStorage localMediaPlayerStorage = MediaPlayerStorage.getInstance();
        MediaPlayer currentMediaPlayer = localMediaPlayerStorage.getMediaPlayer();
        int fetchedCurrentPosition = currentMediaPlayer.getCurrentPosition();
        seekbar.setProgress(fetchedCurrentPosition);
    }

    @SuppressLint("SetTextI18n")
    private void updateSeekBar() {
        SeekBar seekbar = binding.seekBar;
        initializeSeekBar();
        refreshSeekBar();
        seekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {
                        // Only respond when user is moving the bar
                        if (fromUser) {
                            // Re-fetch media player and refresh info every time on progress
                            refreshSeekBar();
                            MediaPlayerStorage localMediaPlayerStorage = MediaPlayerStorage.getInstance();
                            MediaPlayer currentMediaPlayer = localMediaPlayerStorage.getMediaPlayer();
                            // Change the seek bar and seek to desired timestamp
                            int currentProgress = seekBar.getProgress();
                            TimestampMaker timestampMaker = new TimestampMaker();
                            binding.timestamp.setText(
                                    timestampMaker.convertMillisecondsToTimestamp(currentProgress));
                            currentMediaPlayer.seekTo(currentProgress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });
    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // When this fullscreen player is visible, hide mini player
        requireActivity().findViewById(R.id.bottom_nav_view).setVisibility(View.GONE);
        PlayerViewModel playerViewModel =
                new ViewModelProvider(this).get(PlayerViewModel.class);

        binding = FragmentPlayerBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        Context context = requireContext();

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
        final TextView timestamp = binding.timestamp;
        playerViewModel.getMutableTimestamp().observe(
                getViewLifecycleOwner(),
                timestamp::setText
        );

        // Make text scroll when it overflows
        playerTitleTextView.setSelected(true);
        playerAlbumTextView.setSelected(true);
        playerArtistTextView.setSelected(true);
        // Create a controller object and see if song is playing currently
        // According to the state of the song (playing/not) set the appropriate drawable.
        // This is on initial viewing of the player
        MediaPlayerController localMediaPlayerController = new MediaPlayerController();
        Drawable appropriateDrawableInitialToggleButton;
        if (localMediaPlayerController.isAudioPlaying()) {
            appropriateDrawableInitialToggleButton = ContextCompat.getDrawable(context,
                    R.drawable.baseline_pause_circle_24);
        } else {
            appropriateDrawableInitialToggleButton = ContextCompat.getDrawable(context,
                    R.drawable.baseline_play_circle_24);
        }
        binding.playerPlayButton.setImageDrawable(appropriateDrawableInitialToggleButton);
        
        AudioQueueStorage localAudioQueueStorage = AudioQueueStorage.getInstance();
        // See if queue is shuffled, to set the shuffle drawable
        Drawable appropriateDrawableInitialShuffleButton;
        if (localAudioQueueStorage.isQueueShuffled()) {

            appropriateDrawableInitialShuffleButton = ContextCompat.getDrawable(context,
                    R.drawable.baseline_shuffle_on_24);
        } else {
            appropriateDrawableInitialShuffleButton = ContextCompat.getDrawable(context,
                    R.drawable.baseline_shuffle_24);
        }
        binding.playerShuffleButton.setImageDrawable(appropriateDrawableInitialShuffleButton);


        // See if queue is looped too, to set the loop drawable
        Drawable appropriateDrawableInitialLoopButton;
        if (localAudioQueueStorage.isQueueLooped()) {
            appropriateDrawableInitialLoopButton = ContextCompat.getDrawable(context,
                    R.drawable.baseline_repeat_on_24);
        } else {
            appropriateDrawableInitialLoopButton = ContextCompat.getDrawable(context,
                    R.drawable.baseline_repeat_24);
        }
        binding.playerLoopButton.setImageDrawable(appropriateDrawableInitialLoopButton);


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
                appropriateDrawableOnClick = ContextCompat.getDrawable(context,
                        R.drawable.baseline_play_circle_24);
            } else {
                appropriateDrawableOnClick = ContextCompat.getDrawable(context,
                        R.drawable.baseline_pause_circle_24);
                stopSeekBarUpdateRunnable();
            }
            binding.playerPlayButton.startAnimation(quickPulseAnimation);
            binding.playerPlayButton.setImageDrawable(appropriateDrawableOnClick);
        });

        // Click listeners on both the previous and the next audio file buttons.
        binding.playerNextButton.setOnClickListener(view -> {
            localMediaPlayerController.playNextAudioFile(getContext());
            playNextAudioFileAnimations();
         });

        binding.playerPreviousButton.setOnClickListener(view -> {
            localMediaPlayerController.playPreviousAudioFile(getContext());
            playPreviousAudioFileAnimations();
        });

        binding.playerShuffleButton.setOnClickListener(view -> {
            // According to the state of the queue (shuffled/not) set the appropriate drawable.
            Drawable appropriateDrawableOnClick;
            if (localAudioQueueStorage.isQueueShuffled()) {
                localAudioQueueStorage.unshuffle();
                appropriateDrawableOnClick = ContextCompat.getDrawable(context,
                        R.drawable.baseline_shuffle_24);
            } else {
                localAudioQueueStorage.shuffle();
                appropriateDrawableOnClick = ContextCompat.getDrawable(context,
                        R.drawable.baseline_shuffle_on_24);
            }
            binding.playerShuffleButton.startAnimation(quickPulseAnimation);
            binding.playerShuffleButton.setImageDrawable(appropriateDrawableOnClick);
        });


        binding.playerLoopButton.setOnClickListener(view -> {
            // According to the state of the queue (shuffled/not) set the appropriate drawable.
            Drawable appropriateDrawableOnClick;
            if (localAudioQueueStorage.isQueueLooped()) {
                localAudioQueueStorage.unloop();
                appropriateDrawableOnClick = ContextCompat.getDrawable(context,
                        R.drawable.baseline_repeat_24);
            } else {
                localAudioQueueStorage.loop();
                appropriateDrawableOnClick = ContextCompat.getDrawable(context,
                        R.drawable.baseline_repeat_on_24);
            }
            binding.playerLoopButton.startAnimation(quickPulseAnimation);
            binding.playerLoopButton.setImageDrawable(appropriateDrawableOnClick);
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
        createSeekBarUpdateRunnable();

        return root;
    }

    public void playNextAudioFileAnimations () {
        View view = getView();
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
    }

    public void playPreviousAudioFileAnimations () {
        View view = getView();
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
    }


    /* Stop the seek bar runnable when view is destroyed to avoid getting illegal state exception
       off accessing detached fragment method. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopSeekBarUpdateRunnable();
        // When this fullscreen player is invisible, show mini player
        View miniplayer = requireActivity().findViewById(R.id.bottom_nav_view);
        miniplayer.setVisibility(View.VISIBLE);
        binding = null;
    }

    //TODO Does not work, needs to happen in a new activity
    /*
    @Override
    public void onBackPressed(View view) {
        NavController navController =
                Navigation.findNavController(view);
        navController.navigate(R.id.action_nav_player_to_nav_Player);
    }

     */
}