package com.nionios.uniwatune.ui.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlayerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PlayerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("No albums here yet.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}