package com.example.sensoft.ui.externalsound;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExternalSoundViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ExternalSoundViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is reminder fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}