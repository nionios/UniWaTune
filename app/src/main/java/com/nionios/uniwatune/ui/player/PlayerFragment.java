package com.nionios.uniwatune.ui.player;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
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
            binding.playerPlayButton.setImageDrawable(appropriateDrawableOnClick);
        });

        binding.playerNextButton.setOnClickListener(view -> {
            localMediaPlayerController.playNextAudioFile(getContext());
            Drawable pauseCircleButton = ContextCompat.getDrawable(requireContext(),
                    R.drawable.baseline_pause_circle_24);
            binding.playerPlayButton.setImageDrawable(pauseCircleButton);
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
            }, 600);
        });

        binding.playerPreviousButton.setOnClickListener(view -> {
            localMediaPlayerController.playPreviousAudioFile(getContext());
            Drawable pauseCircleButton = ContextCompat.getDrawable(requireContext(),
                    R.drawable.baseline_pause_circle_24);
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
            }, 600);

        });
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