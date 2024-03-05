package com.example.quickscanner.ui.homepage_event;

import com.example.quickscanner.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickscanner.databinding.FragmentEventsBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private FragmentEventsBinding binding;

    // EventList Data
    ListView eventList;
    ArrayList<Event> eventDataList;
    ArrayAdapter<Event> eventAdapter;
    // button
    FloatingActionButton fobButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // inflate fragment to MainActivity
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        // idk why we return view
        View root = binding.getRoot();
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

        // fob button (add event)
        fobButton = view.findViewById(R.id.fob_createEvent);
        fobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start new create event activity
                Intent intent = new Intent(requireContext(), AddEventActivity.class);
                intent.putExtra("eventAdapter", eventDataList);
                startActivity(intent);
            }

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}