package com.example.quickscanner.ui.my_events;


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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.databinding.FragmentEventsBinding;
import com.example.quickscanner.databinding.FragmentMyEventsBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.example.quickscanner.ui.homepage_event.EventArrayAdapter;
import com.example.quickscanner.ui.viewevent.ViewEventActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class OrganizedEventsFragment extends Fragment {
    /**
     * This Fragment hosts our Organized event list for users to view.
     * Can see more event details by clicking an event.
     */

    private @NonNull FragmentMyEventsBinding binding;

    // EventList References
    ListView eventListView;
    ArrayList<Event> eventsDataList;
    ArrayAdapter<Event> eventAdapter;



    // DropDown click References
    private LinearLayout fullRowLayout;  // the entire row including drop down
    private LinearLayout dropDownLayout; // the layout you see when you click drop down
    private RelativeLayout itemClicked;
    private ImageView expandableArrow;



    // Joey Firestore References
    private FirebaseUserController fbUserController;
    private FirebaseEventController fbEventController;
    private ListenerRegistration orgListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyEventsBinding.inflate(inflater, container, false);


        // Joey Firebase References
        fbUserController = new FirebaseUserController();
        fbEventController = new FirebaseEventController();

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize the event data list and ArrayAdapter
        eventsDataList = new ArrayList<Event>();
        eventAdapter = new EventArrayAdapter(getContext(), eventsDataList);
        // Set the adapter to the ListView
        binding.myEventListview.setVisibility(View.GONE);
        binding.myEventListview.setAdapter(eventAdapter);
        ListView myEventListView = binding.myEventListview;
        TextView noOrgTextView = binding.noOrgTextview;


        String orgId = fbUserController.getCurrentUserUid();
        // Create FireStore Listener for Updates to the Events List.
        //log of uid
        orgListener = fbEventController.setUpOrganizedEventsListener(orgId, eventsDataList, eventAdapter,noOrgTextView, myEventListView);

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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (orgListener != null) {
            orgListener.remove();
            orgListener = null;
        }
        binding = null;
    }


}