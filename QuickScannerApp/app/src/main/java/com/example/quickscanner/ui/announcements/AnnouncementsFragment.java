package com.example.quickscanner.ui.announcements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.quickscanner.databinding.FragmentAnnouncementsBinding;
import com.example.quickscanner.R;

import android.os.Bundle;
import android.util.Log;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.quickscanner.model.Announcement;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;

public class AnnouncementsFragment extends Fragment {
    private FragmentAnnouncementsBinding binding;

    // AnnouncementList References
    ListView announcementListView;
    ArrayList<Announcement> AnnouncementsDataList;
    ArrayAdapter<Announcement> announcementsAdapter;

    // Firestore References
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference announcementsRef;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // inflate fragment to MainActivity
        binding = FragmentAnnouncementsBinding.inflate(inflater, container, false);
        // return view for MainActivity
        View root = binding.getRoot();

        // Firebase references
        db = FirebaseFirestore.getInstance(); // non-image db references
        eventsRef = db.collection("Events");
        announcementsRef = db.collection("Announcements");

        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Store view references
        announcementListView = view.findViewById(R.id.announcement_listview);

        // Initialize the Announcement data list and ArrayAdapter
        AnnouncementsDataList = new ArrayList<Announcement>();
        announcementsAdapter = new AnnouncementsArrayAdapter(getContext(), AnnouncementsDataList);
        // Set the adapter to the ListView
        announcementListView.setAdapter(announcementsAdapter);

        //addTestData(); // some test data TODO: delete before submitting


        // create listener for updates to the Announcements list.
        announcementsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    AnnouncementsDataList.clear();  // removes current data
                    for (QueryDocumentSnapshot doc : querySnapshots) { // set of documents
                        Announcement qryAnnouncement = doc.toObject(Announcement.class);
                        AnnouncementsDataList.add((qryAnnouncement)); // adds new data from db
                    }
                }
                announcementsAdapter.notifyDataSetChanged();
            }
        });



        /*      Announcement ListView Click       */
        announcementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // get announcement
                Announcement clickedAnnouncement = (Announcement) adapterView.getItemAtPosition(position);
                // display fragment


            }
        });
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void addTestData() {
        // Lets add some test data.
        Announcement announcementAryan = new Announcement("Announcement message testing", "Dylan's Event");
        db.collection("Announcements").add(announcementAryan);

        Announcement announcement2 = new Announcement("Event Extended!", "Dylan's Event");
        db.collection("Announcements").add(announcement2);

        Announcement announcement3 = new Announcement(getString(R.string.LoremIpsum), "Dylan's Event");
        db.collection("Announcements").add(announcement3);

        Announcement announcement4 = new Announcement("Announcement message" + getString(R.string.LoremIpsum), "Joey's Event");
        db.collection("Announcements").add(announcement4);

    }

}