package com.example.quickscanner.ui.attendance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> selected = new MutableLiveData<String>();

    public void select(String ID) {
        selected.setValue(ID);
    }

    public LiveData<String> getSelected() {
        return selected;
    }
}