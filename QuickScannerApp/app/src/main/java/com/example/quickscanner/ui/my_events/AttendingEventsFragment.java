package com.example.quickscanner.ui.my_events;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.databinding.FragmentMyEventsBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.ui.homepage_event.EventArrayAdapter;
import com.example.quickscanner.ui.viewevent.ViewEventActivity;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

//javadocs
/**
 * This Fragment hosts our Attending event list for users to view.
 * Can see more event details by clicking an event.
 */
public class AttendingEventsFragment extends Fragment {
    /**
     * This Fragment hosts our Attending event list for users to view.
     * Can see more event details by clicking an event.
     */


    private @NonNull FragmentMyEventsBinding binding;

    // EventList References
    ListView eventListView;
    LinearLayout eventLinearLayout;
    ArrayList<Event> eventsDataList;
    ArrayAdapter<Event> eventAdapter;
    ListenerRegistration attendListener;

    // DropDown click References
    private LinearLayout fullRowLayout;  // the entire row including drop down
    private LinearLayout dropDownLayout; // the layout you see when you click drop down
    private RelativeLayout itemClicked;
    private ImageView expandableArrow;

    // Joey Firestore References
    private FirebaseUserController fbUserController;
    private FirebaseEventController fbEventController;

    // javadocs
    /**
     * This method creates the view for the AttendingEventsFragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false);

        // Firebase references
        fbUserController = new FirebaseUserController();
        fbEventController = new FirebaseEventController();

        return binding.getRoot();
    }

    // javadocs
    /**
     * This method is called when the view is created.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Store view references
        eventListView = view.findViewById(R.id.my_event_listview);

        // Initialize the event data list and ArrayAdapter
        eventsDataList = new ArrayList<Event>();
        eventAdapter = new EventArrayAdapter(getContext(), eventsDataList);
        // Set the adapter to the ListView
        binding.myEventListview.setAdapter(eventAdapter);
        String attendingId = fbUserController.getCurrentUserUid();
        ListView myEventListView = binding.myEventListview;
        TextView noaattendTextView = binding.noAttendTextview;
        attendListener = fbEventController.setupAttendListListener(attendingId, eventsDataList,
                eventAdapter, noaattendTextView, myEventListView);



        /*      Event ListView Click       */
        binding.myEventListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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
        }
    });


    }

    // javadocs
    /**
     * This method is called when the view is destroyed.
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (attendListener != null) {
            attendListener.remove();
            attendListener = null;
        }
        binding = null;
    }


}