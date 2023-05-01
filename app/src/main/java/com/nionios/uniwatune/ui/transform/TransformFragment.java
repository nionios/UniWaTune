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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.databinding.FragmentTransformBinding;
import com.nionios.uniwatune.databinding.ItemTransformBinding;

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
        TransformViewModel transformViewModel =
                new ViewModelProvider(this).get(TransformViewModel.class);

        binding = FragmentTransformBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerviewTransform;
        ListAdapter<String, TransformViewHolder> adapter = new TransformAdapter();
        recyclerView.setAdapter(adapter);
        transformViewModel.getTexts().observe(getViewLifecycleOwner(), adapter::submitList);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private static class TransformAdapter extends ListAdapter<String, TransformViewHolder> {

        // TODO: dark mode is still black, change color
        private final List<Integer> drawables = Arrays.asList(
                R.drawable.baseline_audio_file_24);

        protected TransformAdapter() {
            super(new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }
            });
        }

        @NonNull
        @Override
        public TransformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTransformBinding binding = ItemTransformBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new TransformViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TransformViewHolder holder, int position) {
            holder.titleTextView.setText(getItem(position));
            holder.artistNameTextView.setText(getItem(position));
            holder.albumNameTextView.setText(getItem(position));
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

    private static class TransformViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView artistNameTextView;
        private final TextView albumNameTextView;
        private final TextView titleTextView;

        public TransformViewHolder(ItemTransformBinding binding) {
            super(binding.getRoot());
            imageView = binding.imageViewItemTransform;
            // TODO: more info on text like album, creator etc.
            titleTextView = binding.textViewTitleTranform;
            artistNameTextView = binding.textViewArtistNameTransform;
            albumNameTextView = binding.textViewAlbumNameTransform;
        }
    }
}