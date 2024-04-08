package com.example.quickscanner.ui.homepage_event;

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

//javadocs
/**
 * This Fragment hosts our event list for users to view.
 * Anybody can Organize an Event through this fragment, and
 * see more event details by clicking an event.
 */
public class EventFragment extends Fragment {
    /**
     * This Fragment hosts our event list for users to view.
     * Anybody can Organize an Event through this fragment, and
     * see more event details by clicking an event.
     */
    private FragmentEventsBinding binding;

    // EventList References
    ListView eventListView;

    ArrayList<Event> eventsDataList;
    ArrayAdapter<Event> eventAdapter;
    private Timer timer;
    private TimerTask timerTask;



    // Button References
    FloatingActionButton fobButton;

    // Firestore References
    private FirebaseEventController fbEventController;
    private ListenerRegistration eventListListenerReg;



    //javadocs
    /**
     * This Fragment hosts our event list for users to view.
     * Anybody can Organize an Event through this fragment, and
     * see more event details by clicking an event.
     */
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

    //javadocs
    /**
     * This Fragment hosts our event list for users to view.
     * Anybody can Organize an Event through this fragment, and
     * see more event details by clicking an event.
     */
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

    //javadocs
    /**
     * This method sets up a timer to check the time of the events in the list.
     * If the time of the event is before the current time, the event is removed from the list.
     * The adapter is then notified of the changes.
     * The timer is then scheduled to run every minute.
     * @param eventsDataList The list of events to check the time of
     * @param eventAdapter The adapter for the list of events
     */
    private void setupTimeListener(final ArrayList<Event> eventsDataList, final ArrayAdapter<Event> eventAdapter) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> {
                    boolean isListUpdated = false;
                    Timestamp now = Timestamp.now();
                    while (!eventsDataList.isEmpty() && eventsDataList.get(0).getTime().compareTo(now) <= 0) {
                        eventsDataList.remove(0);
                        isListUpdated = true;
                    }
                    if (isListUpdated) {
                        eventAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        // Schedules the task to run starting at the next minute and then every minute
        long period = 60000; // 60 seconds
        long delay = period - (System.currentTimeMillis() % period); // Time until the start of the next minute
        //fix it to run every minute
        timer.scheduleAtFixedRate(timerTask, delay, period);
    }

    //javadocs
    /**
     * This method is called when the fragment is destroyed.
     * It removes the event list listener registration.
     */

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
    @Override
    public void onPause() {
        super.onPause();
        // Assuming you have a method to pause listening to Firestore updates
        if (eventListListenerReg != null) {
            eventListListenerReg.remove();
            eventListListenerReg = null;
        }
        // Cancel the timer to stop checking for past events
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Re-initialize Firestore listener and timer
        if (eventListListenerReg == null) {
            eventListListenerReg = fbEventController.setupEventListListener(eventsDataList, eventAdapter);
        }
        setupTimeListener(eventsDataList, eventAdapter);
    }



}
