package com.example.quickscanner.ui.addevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddEventViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is an Add Event activity");
    }

    public LiveData<String> getText() {
        return mText;
    }
}