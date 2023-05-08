package com.nionios.uniwatune.ui.transform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.nionios.uniwatune.data.singletons.AudioScanned;
import com.nionios.uniwatune.data.types.AudioFile;
import com.nionios.uniwatune.databinding.FragmentTransformBinding;
import com.nionios.uniwatune.databinding.ItemTransformBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
        // TODO: Make this receive all info (album name and artist name currently)
        // TODO: Learn what in the world this thing does and why is it here
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
        // TODO: dark mode is still black, change color
        private final List<Integer> drawables = Arrays.asList(
                R.drawable.baseline_audio_file_24);

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
            /* Singleton Way (maybe) */
            // Iterate through audio file list and get their names
             /*
            AudioScanned localAudioScannedInstance = AudioScanned.getInstance();
            List<AudioFile> localInstanceAudioFileList = localAudioScannedInstance.getAudioFileList();
            ArrayList<String> textsToDisplay = new ArrayList<String>();

            holder.titleTextView.setText(localInstanceAudioFileList.get(position).getName());
            holder.artistNameTextView.setText(localInstanceAudioFileList.get(position).getArtist());
            holder.albumNameTextView.setText(localInstanceAudioFileList.get(position).getAlbum());
            */
             /* Google Way (maybe) */
            AudioFile AudioFileToDisplay = audioFileArrayList.get(position);

            holder.titleTextView.setText(AudioFileToDisplay.getName());
            holder.artistNameTextView.setText(AudioFileToDisplay.getArtist());
            holder.albumNameTextView.setText(AudioFileToDisplay.getAlbum());

            // TODO: tried the following, maybe it does not work

            holder.imageView.setImageDrawable(
                    // TODO: based on the file extension, make this icon different
                    //       See drawables above too!
                    ResourcesCompat.getDrawable(
                            holder.imageView.getResources(),
                            drawables.get(0),
                            null
                    )
            );
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
            NavController navController =
                    Navigation.findNavController(view);
            navController.navigate(R.id.action_nav_transform_to_nav_player);
        }
    }
}