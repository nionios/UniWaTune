package com.nionios.uniwatune.ui.transform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.data.controllers.MediaPlayerController;
import com.nionios.uniwatune.data.singletons.AudioScanned;
import com.nionios.uniwatune.data.types.AudioFile;
import com.nionios.uniwatune.databinding.FragmentTransformBinding;
import com.nionios.uniwatune.databinding.ItemTransformBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
public class TransformFragment extends Fragment {

    private FragmentTransformBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Get the view model from the source and inflate it
        TransformViewModel transformViewModel =
                new ViewModelProvider(this).get(TransformViewModel.class);

        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerviewTransform;

        AudioScanned localAudioScannedInstance = AudioScanned.getInstance();
        ArrayList<AudioFile> localInstanceAudioFileList = localAudioScannedInstance.getAudioFileList();

        ListAdapter<AudioFile, TransformViewHolder> adapter = new TransformAdapter(localInstanceAudioFileList);

        recyclerView.setAdapter(adapter);

        transformViewModel.getTexts().observe(
                getViewLifecycleOwner(),
                adapter::submitList
        );
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class TransformAdapter extends ListAdapter<AudioFile, TransformViewHolder> {

        private ArrayList<AudioFile> audioFileArrayList;

        private final int OPUS_FILE_TYPE_ICON = 1;
        private final int MP3_FILE_TYPE_ICON = 2;
        private final int OGG_FILE_TYPE_ICON = 3;

        private final List<Integer> drawables = Arrays.asList(
                R.drawable.baseline_audio_file_24,
                R.drawable.opus_file_type,
                R.drawable.mp3_file_type,
                R.drawable.ogg_file_type);

        protected TransformAdapter(ArrayList<AudioFile> audioFileArrayList) {
            super(new DiffUtil.ItemCallback<AudioFile>() {
                // If and only if items have the same path, then they are the same.
                @Override
                public boolean areItemsTheSame(@NonNull AudioFile oldItem, @NonNull AudioFile newItem) {
                    return oldItem.getPath().equals(newItem.getPath());
                }

                @Override
                public boolean areContentsTheSame(@NonNull AudioFile oldItem, @NonNull AudioFile newItem) {
                    return oldItem.getPath().equals(newItem.getPath());
                }
            });
            this.audioFileArrayList = audioFileArrayList;
        }

        @NonNull
        @Override
        public TransformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTransformBinding binding =
                    ItemTransformBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new TransformViewHolder(binding);
        }

        /* Called as we are going down the list recycling already created object
          (memory optimisation, we are loading them as they are in the viewport) */
        @Override
        public void onBindViewHolder(@NonNull TransformViewHolder holder, int position) {
            // Get info of files from the audioFileArrayList and put them into the holder
            AudioFile AudioFileToDisplay = audioFileArrayList.get(position);

            holder.titleTextView.setText(AudioFileToDisplay.getName());
            holder.artistNameTextView.setText(AudioFileToDisplay.getArtist());
            holder.albumNameTextView.setText(AudioFileToDisplay.getAlbum());

            /* Display a different Icon with each file type! */
            String AudioToDisplayPath = AudioFileToDisplay.getPath();
            // Check if song has cover art. If yes, then set it in the appropriate ImageView.
            // TODO: make this work
            if (AudioFileToDisplay.getAlbumArt() != null) {
                holder.imageView.setImageBitmap(AudioFileToDisplay.getAlbumArt());
            } else if (AudioToDisplayPath.contains("mp3")) {
                holder.imageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                holder.imageView.getResources(),
                                drawables.get(MP3_FILE_TYPE_ICON),
                                null
                        )
                );
            } else if (AudioToDisplayPath.contains("opus")) {
                holder.imageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                holder.imageView.getResources(),
                                drawables.get(OPUS_FILE_TYPE_ICON),
                                null
                        )
                );
            } else if (AudioToDisplayPath.contains("ogg")) {
                holder.imageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                holder.imageView.getResources(),
                                drawables.get(OGG_FILE_TYPE_ICON),
                                null
                        )
                );
            }
            /* Set the fileID on holder so we can hold some kind of reference (its ID)
             * to it and play it when clicking the item on UI. We need to go look for it
             * through our AudioScanned singleton obj later for this to happen. */
            holder.fileID = AudioFileToDisplay.getID();
            // Make text scroll when it overflows
            //FIXME: does not work!
            holder.titleTextView.setSelected(true);
            holder.artistNameTextView.setSelected(true);
            holder.albumNameTextView.setSelected(true);
        }
    }

    /* TransformViewHolder implements listener to make every song clickable.
    * Clicking on an item navigates to player*/
    private static class TransformViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private final ImageView imageView;
        private final TextView artistNameTextView;
        private final TextView albumNameTextView;
        private final TextView titleTextView;
        private int fileID;

        public TransformViewHolder(ItemTransformBinding binding) {
            super(binding.getRoot());
            // Attach the onClick listener
            binding.getRoot().setOnClickListener(this);
            // Set all field bindings
            imageView = binding.imageViewItemTransform;
            titleTextView = binding.textViewTitleTranform;
            artistNameTextView = binding.textViewArtistNameTransform;
            albumNameTextView = binding.textViewAlbumNameTransform;
        }

        @Override
        public void onClick(View view) {
            /* Get the reference to our song through the singleton AudioScanned
             * and its ID, make sound play!*/
            AudioScanned localAudioScannedInstance = AudioScanned.getInstance();
            /* Get the file path of the clicked file and create and send controller to do the heavy
             *  lifting of communication with the MediaPlayerService */
            String clickedFilePath = localAudioScannedInstance.getAudioFile(this.fileID).getPath();
            MediaPlayerController localMediaPlayerController = new MediaPlayerController();
            localMediaPlayerController.playSelectedAudioFile(view, clickedFilePath);
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_nav_transform_to_nav_player);
        }
    }
}