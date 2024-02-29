package com.example.quickscanner.ui.homepage_event;

import com.example.quickscanner.MainActivity;
import com.example.quickscanner.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quickscanner.databinding.FragmentEventsBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.example.quickscanner.ui.profile.ProfileActivity;
import com.ncorti.slidetoact.SlideToActView;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private FragmentEventsBinding binding;

    // EventList Data
    ListView eventList;
    ArrayList<Event> eventDataList;
    ArrayAdapter<Event> eventAdapter;
    // button
    SlideToActView swipeButton;

    // Function to handle redirecting to other activities
    private final ActivityResultLauncher<Intent> startProfileActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            // Handle the result if needed
                        }
                    });

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the event data list and ArrayAdapter
        eventDataList = new ArrayList<>();
        eventAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, eventDataList);

        // Set the adapter to the ListView
        eventList = view.findViewById(R.id.event_listview);
        eventList.setAdapter(eventAdapter);

        // swipe button
        swipeButton = view.findViewById(R.id.slider_createEvent);
        swipeButton.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                // Handle Edit Profile click
                Intent intent = new Intent(requireContext(), AddEventActivity.class);
                intent.putExtra("eventAdapter", eventDataList);
                startActivityForResult(intent, 1);
                swipeButton.setCompleted(false, true);
            }

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}