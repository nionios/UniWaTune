package com.nionios.uniwatune;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.nionios.uniwatune.data.controllers.MediaPlayerController;
import com.nionios.uniwatune.data.helpers.fileFinder;
import com.nionios.uniwatune.data.singletons.AudioQueueStorage;
import com.nionios.uniwatune.data.singletons.AudioScanned;
import com.nionios.uniwatune.data.types.AudioFile;
import com.nionios.uniwatune.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the main theme again from the splash screen theme
        setTheme(R.style.Theme_UniWaTune);
        super.onCreate(savedInstanceState);

        // Check for READ permission on storage
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

        // Get instance of our singleton where the list of all audio files found is stored
        AudioScanned audioScannedMainActInstance = AudioScanned.getInstance();
        fileFinder localFileFinder = new fileFinder();

        // Get the list of our scanned AudioFiles
        ArrayList<AudioFile> localAudioFileList;
        try {
            localAudioFileList = localFileFinder.getAllAudioFromDevice(this.getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Store it in out singleton with the scanned audio files
        audioScannedMainActInstance.setAudioFileList(localAudioFileList);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        LinkedList<AudioFile> currentAudioFileQueue = AudioQueueStorage.getInstance().getAudioQueue();
        if (!currentAudioFileQueue.isEmpty()) {
            refreshMiniplayerInfo(currentAudioFileQueue);
        }
    }

    public void refreshMiniplayerInfo (LinkedList<AudioFile> currentAudioFileQueue) {
        AudioFile currentAudioFile = currentAudioFileQueue.peek();
        TextView miniplayerTitleTextView = (TextView) findViewById(R.id.miniplayer_title_text_view);
        miniplayerTitleTextView.setText(currentAudioFile.getName());
        TextView miniplayerArtistTextView = (TextView) findViewById(R.id.miniplayer_artist_text_view);
        miniplayerArtistTextView.setText(currentAudioFile.getArtist());
        ImageView miniplayerAlbumImageView = (ImageView) findViewById(R.id.miniplayer_album_image);
        miniplayerAlbumImageView.setImageBitmap(currentAudioFile.getAlbumArt());
        // Set the text views as selected to make the marquee work properly
        miniplayerArtistTextView.setSelected(true);
        miniplayerTitleTextView.setSelected(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        NavigationView navView = findViewById(R.id.nav_view);
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            getMenuInflater().inflate(R.menu.overflow, menu);
        }

        // Populate the miniplayer if a song is playing with info from current song
        LinkedList<AudioFile> currentAudioFileQueue = AudioQueueStorage.getInstance().getAudioQueue();
        if (!currentAudioFileQueue.isEmpty()) {
            refreshMiniplayerInfo(currentAudioFileQueue);
            // Create a controller object and see if song is playing currently
            // According to the state of the song (playing/not) set the appropriate drawable.
            // This is on initial viewing of the player
            MediaPlayerController localMediaPlayerController = new MediaPlayerController();
            Drawable appropriateDrawableInitial;
            if (localMediaPlayerController.isAudioPlaying()) {
                appropriateDrawableInitial = ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.baseline_pause_circle_24);
            } else {
                appropriateDrawableInitial = ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.baseline_play_circle_24);
            }
            ImageButton miniplayerImageButton = findViewById(R.id.miniplayer_play_button);
            miniplayerImageButton.setImageDrawable(appropriateDrawableInitial);
            // Initializing quickpulse animation for some buttons
            Animation quickPulseAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.quickpulse);
            // Signal the service to start/stop the song on click.
            // According to the state of the song (playing/not) set the appropriate drawable again.
            // This is on click of playerPlayButton on player
            miniplayerImageButton.setOnClickListener(view -> {
                localMediaPlayerController.toggleCurrentlyPlayingAudioFilePlayState(getApplicationContext());
                // According to the state of the song (playing/not) set the appropriate drawable.
                Drawable appropriateDrawableOnClick;
                if (localMediaPlayerController.isAudioPlaying()) {
                    appropriateDrawableOnClick = ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.baseline_play_circle_24);
                } else {
                    appropriateDrawableOnClick = ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.baseline_pause_circle_24);
                }
                miniplayerImageButton.startAnimation(quickPulseAnimation);
                miniplayerImageButton.setImageDrawable(appropriateDrawableOnClick);
            });

            ImageButton miniplayerNextButton = findViewById(R.id.miniplayer_next_button);
            ImageButton miniplayerPreviousButton = findViewById(R.id.miniplayer_previous_button);

            // Click listeners on both the previous and the next audio file buttons.
            miniplayerNextButton.setOnClickListener(view -> {
                localMediaPlayerController.playNextAudioFile(getApplicationContext());
                playNextAudioFileAnimations();
            });

            miniplayerPreviousButton.setOnClickListener(view -> {
                localMediaPlayerController.playPreviousAudioFile(getApplicationContext());
                playPreviousAudioFileAnimations();
            });
        }

        return result;
    }

    public void playNextAudioFileAnimations() {
        Animation slideOutLeftAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.slide_out_left);
        Animation slideInRightAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.slide_in_right);
        changeAudioFileUISequence(
                findViewById(R.id.miniplayer_next_button),
                slideOutLeftAnimation,
                slideInRightAnimation);
    }

    public void playPreviousAudioFileAnimations() {
        Animation slideOutRightAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                android.R.anim.slide_out_right);
        Animation slideInLeftAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                android.R.anim.slide_in_left);
        changeAudioFileUISequence(
                findViewById(R.id.miniplayer_previous_button),
                slideOutRightAnimation,
                slideInLeftAnimation);
    }

    private void changeAudioFileUISequence(
            ImageButton triggeredButton,
            Animation previousAudioFileAnimation,
            Animation nextAudioFileAnimation) {
        // Initialize our pulse animation
        Animation pulseAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pulse);
        // As audio starts playing, make the play button reflect that (switch to click to pause)
        Drawable pauseCircleButton = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.baseline_pause_circle_24);
        ImageButton miniplayerPlayButton = (ImageButton) findViewById(R.id.miniplayer_play_button);
        miniplayerPlayButton.setImageDrawable(pauseCircleButton);
        // Signal to user button is pressed with our pulse animation
        triggeredButton.startAnimation(pulseAnimation);
        // Animate the miniplayer changing the audio file.
        findViewById(R.id.miniplayer_album_image).startAnimation(previousAudioFileAnimation);
        findViewById(R.id.miniplayer_title_text_view).startAnimation(previousAudioFileAnimation);
        findViewById(R.id.miniplayer_artist_text_view).startAnimation(previousAudioFileAnimation);
        findViewById(R.id.content_main).postDelayed(() -> {
            findViewById(R.id.miniplayer_album_image).startAnimation(nextAudioFileAnimation);
            LinkedList<AudioFile> currentAudioFileQueue = AudioQueueStorage.getInstance().getAudioQueue();
            if (!currentAudioFileQueue.isEmpty()) {
                refreshMiniplayerInfo(currentAudioFileQueue);
            }
        }, 400);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}