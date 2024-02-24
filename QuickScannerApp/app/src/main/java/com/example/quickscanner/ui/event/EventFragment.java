package com.example.quickscanner.ui.event;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.quickscanner.databinding.FragmentEventsBinding;
import com.example.quickscanner.model.Event;

import java.util.ArrayList;
import java.util.Objects;

public class EventFragment extends Fragment {

    private FragmentEventsBinding binding;

    // EventList Data
    @SuppressLint("StaticFieldLeak")
    ListView eventList;
    ArrayList<Event> eventDataList;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventViewModel eventViewModel =
                new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textEvents;
        eventViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;


    }

//    public void onResume() {
//        super.onResume();
//        if (getActivity() != null) {
//            // hide original action bar
//            //Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).hide();
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}