package com.example.quickscanner.ui.homepage_event;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.databinding.FragmentEventsBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.example.quickscanner.ui.viewevent.ViewEventActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class EventFragment extends Fragment {
    /**
     * This Fragment hosts our event list for users to view.
     * Anybody can Organize an Event through this fragment, and
     * see more event details by clicking an event.
     */
    private FragmentEventsBinding binding;

    // EventList References
    ListView eventListView;

    LinearLayout eventLinearLayout;
    ArrayList<Event> eventsDataList;
    ArrayAdapter<Event> eventAdapter;
    private Timer timer;
    private TimerTask timerTask;



    // Button References
    FloatingActionButton fobButton;

    // DropDown click References
    private LinearLayout fullRowLayout;  // the entire row including drop down
    private LinearLayout dropDownLayout; // the layout you see when you click drop down
    private RelativeLayout itemClicked;
    private ImageView expandableArrow;

    // Firestore References
    private FirebaseEventController fbEventController;
    private ListenerRegistration eventListListenerReg;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // inflate fragment to MainActivity
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        // return view for MainActivity
        View root = binding.getRoot();

        // Firebase references
        fbEventController = new FirebaseEventController();


        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentEventsBinding binding = FragmentEventsBinding.bind(view);

        // Store view references
        eventListView = binding.eventListview;

        // Initialize the event data list and ArrayAdapter
        eventsDataList = new ArrayList<>();
        eventAdapter = new EventArrayAdapter(getContext(), eventsDataList);
        // Set the adapter to the ListView
        eventListView.setAdapter(eventAdapter);


        // Create Firestore Listener for real-time updates to the Events List.
        eventListListenerReg = fbEventController.setupEventListListener(eventsDataList, eventAdapter);
        setupTimeListener(eventsDataList, eventAdapter);



        /*     Fob Button (add event) Click       */
        fobButton = view.findViewById(R.id.fob_createEvent);
        fobButton.setOnClickListener(view1 -> {
            // start new create event activity
            Intent intent = new Intent(requireContext(), AddEventActivity.class);
            startActivity(intent);
        });

        /*      Event ListView Click       */
        eventListView.setOnItemClickListener((adapterView, view12, position, id) -> {
            // get the clicked event
            Event clickedEvent = (Event) adapterView.getItemAtPosition(position);
            // move to new activity and pass the clicked event's unique ID.
            Intent intent = new Intent(getContext(), ViewEventActivity.class);
            Bundle bundle = new Bundle(1);
            // Pass the Event Identifier to the New Activity
            bundle.putString("eventID", clickedEvent.getEventID());
            intent.putExtras(bundle);
            // Start new Activity
            requireContext().startActivity(intent);
        });
    }

    private void setupTimeListener(final ArrayList<Event> eventsDataList, final ArrayAdapter<Event> eventAdapter)
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                // Check the top event in the list
                while (!eventsDataList.isEmpty() && eventsDataList.get(0).getTime().compareTo(Timestamp.now()) <= 0)
                {
                    // If the event's time is before the current time, remove it from the list
                    if (!eventsDataList.isEmpty()) {
                        eventsDataList.remove(0);
                    }
                }

                // Notify the adapter of the changes
                // Notify the adapter of the changes on the main thread
                eventListView.post(eventAdapter::notifyDataSetChanged);

                // Start the time listener
                timer = new Timer();
                // Get the number of milliseconds until the next minute
                long delay = 60000 - (System.currentTimeMillis() % 60000);
                // Add the current second to the delay
                delay += System.currentTimeMillis() % 1000;
                // Schedule the task to run at the same second of the next minute, and then every minute after that
                timer.schedule(timerTask, delay, 60000);
            }
        };
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the time listener when the view is destroyed
        if (timer != null)
        {
            timer.cancel();
            timerTask.cancel();
        }
        if (eventListListenerReg != null)
        {
            eventListListenerReg.remove();
            eventListListenerReg = null;
        }

        binding = null;
    }


}
