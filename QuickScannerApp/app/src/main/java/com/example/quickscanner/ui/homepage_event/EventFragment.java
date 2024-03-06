package com.example.quickscanner.ui.homepage_event;

import com.example.quickscanner.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private FragmentEventsBinding binding;

    // EventList References
    ListView eventListView;
    ArrayList<Event> eventsDataList;
    ArrayAdapter<Event> eventAdapter;

    // Button References
    FloatingActionButton fobButton;

    // Firestore References
    private FirebaseFirestore db;
    private FirebaseStorage idb;
    private StorageReference storeRef;
    private CollectionReference profileRef;
    private CollectionReference eventsRef;
    private CollectionReference imagesRef;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // inflate fragment to MainActivity
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        // return view for MainActivity
        View root = binding.getRoot();

        // Firebase references
        db = FirebaseFirestore.getInstance(); // non-image db references
        profileRef = db.collection("Profiles");
        eventsRef = db.collection("Events");
        imagesRef = db.collection("Images");
        idb = FirebaseStorage.getInstance(); // image db references


        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Store view references
        eventListView = view.findViewById(R.id.event_listview);

        // Initialize the event data list and ArrayAdapter
        eventsDataList = new ArrayList<Event>();
        eventAdapter = new EventArrayAdapter(getContext(), eventsDataList);
        // Set the adapter to the ListView
        eventListView.setAdapter(eventAdapter);

        testData(); // some test data TODO: delete before submitting


        // create listener for updates to the events list.
        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    eventsDataList.clear();  // removes current data
                    for (QueryDocumentSnapshot doc : querySnapshots) { // set of documents
                        Event qryEvent = doc.toObject(Event.class);
                        eventsDataList.add((qryEvent)); // adds new data from db
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }
        });



        // fob button (add event)
        fobButton = view.findViewById(R.id.fob_createEvent);
        fobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start new create event activity
                Intent intent = new Intent(requireContext(), AddEventActivity.class);
                intent.putExtra("eventAdapter", eventsDataList);
                startActivity(intent);
            }

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void testData() {
        // Lets add some test data.
        User dylanUser = new User("Dylan", "dndu@ualberta.ca", "dndu@github.com", "imageURL");
        Event eventDylan = new Event("Dylan's Event", "EventDescription",
                "EventImagePath", dylanUser);
        db.collection("Events").document(eventDylan.getName()).set(eventDylan);

        User aryanUser = new User("Aryan", "Aryan@ualberta.ca", "aryan@github.com", "imageURL");
        Event eventAryan = new Event("Aryan's Event", "EventDescription",
                "EventImagePath", aryanUser);
        db.collection("Events").document(eventAryan.getName()).set(eventAryan);

        User sidUser = new User("Sid", "Sid@ualberta.ca", "Sid@github.com", "imageURL");
        Event eventSid = new Event("Sid's Event", "EventDescription",
                "EventImagePath", sidUser);
        db.collection("Events").document(eventSid.getName()).set(eventSid);
    }

}