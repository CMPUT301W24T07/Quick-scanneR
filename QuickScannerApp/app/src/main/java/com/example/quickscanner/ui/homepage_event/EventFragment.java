package com.example.quickscanner.ui.homepage_event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.EventListener;

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


    // Button References
    FloatingActionButton fobButton;

    // DropDown click References
    private LinearLayout fullRowLayout;  // the entire row including drop down
    private LinearLayout dropDownLayout; // the layout you see when you click drop down
    private RelativeLayout itemClicked;
    private ImageView expandableArrow;

    // Firestore References
    private FirebaseEventController fbEventController;



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

        // Store view references
        eventListView = view.findViewById(R.id.event_listview);
        eventLinearLayout = view.findViewById(R.id.EventFragmentContent_Layout);

        // Initialize the event data list and ArrayAdapter
        eventsDataList = new ArrayList<Event>();
        eventAdapter = new EventArrayAdapter(getContext(), eventsDataList);
        // Set the adapter to the ListView
        eventListView.setAdapter(eventAdapter);


// Create Firestore Listener for real-time updates to the Events List.
        fbEventController.getEventsCollectionReference()
                .addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("eventfragment listener", "Listen failed.", e);
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            eventsDataList.clear();  // removes current data
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Event event = doc.toObject(Event.class);
                                eventsDataList.add(event);
                            }
                            eventAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("eventfragment listener", "Current data: null");
                        }
                    }
                });



        /*     Fob Button (add event) Click       */
        fobButton = view.findViewById(R.id.fob_createEvent);
        fobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start new create event activity
                Intent intent = new Intent(requireContext(), AddEventActivity.class);
                startActivity(intent);
            }

        });

        /*      Event ListView Click       */
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}