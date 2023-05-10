package com.nionios.uniwatune.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.nionios.uniwatune.R;
import com.nionios.uniwatune.databinding.FragmentPlayerBinding;

public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PlayerViewModel playerViewModel =
                new ViewModelProvider(this).get(PlayerViewModel.class);

        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAlbums;
        playerViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*
    //TODO Does not work lol
    @Override
    public void onBackPressed(View view) {
        NavController navController =
                Navigation.findNavController(view);
        navController.navigate(R.id.action_nav_player_to_nav_transform);
    }

     */
}