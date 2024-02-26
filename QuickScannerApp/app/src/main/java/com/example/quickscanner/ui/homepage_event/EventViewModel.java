package com.example.quickscanner.ui.homepage_event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;




public class EventViewModel extends ViewModel {


    // TextView Data
    private final MutableLiveData<String> mText;

    public EventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Event fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}