package com.example.quickscanner.ui.my_events;

import static android.content.ContentValues.TAG;

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
import com.example.quickscanner.controller.FirebaseAttendanceController;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.databinding.FragmentEventsBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.example.quickscanner.ui.homepage_event.EventArrayAdapter;
import com.example.quickscanner.ui.viewevent.ViewEventActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttendingEventsFragment extends Fragment {
    /**
     * This Fragment hosts our Attending event list for users to view.
     * Can see more event details by clicking an event.
     */

    private FragmentEventsBinding binding;

    // EventList References
    ListView eventListView;
    LinearLayout eventLinearLayout;
    ArrayList<Event> eventsDataList;
    ArrayAdapter<Event> eventAdapter;

    // DropDown click References
    private LinearLayout fullRowLayout;  // the entire row including drop down
    private LinearLayout dropDownLayout; // the layout you see when you click drop down
    private RelativeLayout itemClicked;
    private ImageView expandableArrow;

    // User
    User myUser;


    // Firestore References
    private FirebaseStorage idb;

    // Joey Firestore References
    private FirebaseUserController fbUserController;
    private FirebaseEventController fbEventController;
    private FirebaseAttendanceController fbAttendanceController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        // Firebase references
        fbUserController = new FirebaseUserController();
        fbEventController = new FirebaseEventController();
        fbAttendanceController = new FirebaseAttendanceController();


        // Joey Firebase References
        fbUserController = new FirebaseUserController();

        return inflater.inflate(R.layout.fragment_my_events, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Store view references
        eventListView = view.findViewById(R.id.my_event_listview);

        // Initialize the event data list and ArrayAdapter
        eventsDataList = new ArrayList<Event>();
        eventAdapter = new EventArrayAdapter(getContext(), eventsDataList);
        // Set the adapter to the ListView
        eventListView.setAdapter(eventAdapter);


        // Fetch user data from Firebase.
        fbUserController.getUserTask(fbUserController.getCurrentUserUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Process the result when the user data retrieval is successful.
                DocumentSnapshot document = task.getResult();
                if (!document.exists())
                    Log.w("error", "user document doesn't exist");
                else {
                    // Extract user information from the document.
                    myUser = document.toObject(User.class);

                }
            }
        });

        // obtain filtered events, pertaining to the user.
        fbAttendanceController.getUserSignedUpEvents(fbUserController.getCurrentUserUid())
                .addOnCompleteListener(new OnCompleteListener<List<Event>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Event>> task) {
                        if (task.isSuccessful()) {
                            eventsDataList.clear();
                            eventsDataList.addAll(task.getResult());
                            eventAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting signed up events: ", task.getException());
                        }
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