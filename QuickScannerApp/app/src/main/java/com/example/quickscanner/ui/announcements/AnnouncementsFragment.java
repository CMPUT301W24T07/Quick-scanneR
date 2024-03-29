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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;


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
    /**
     * This Fragment hosts our Announcement list for users to view.
     * Announcements are expandable by clicking on the dropdown menu to read
     * more.
     */

    private FragmentAnnouncementsBinding binding;

    // DropDown click References
    private LinearLayout fullRowLayout;  // the entire row including drop down
    private LinearLayout dropDownLayout; // the layout you see when you click drop down
    private RelativeLayout itemClicked;
    private ImageView expandableArrow;

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

        // Store view References
        announcementListView = view.findViewById(R.id.announcement_listview);


        // Initialize the Announcement data list and ArrayAdapter
        AnnouncementsDataList = new ArrayList<Announcement>();
        announcementsAdapter = new AnnouncementsArrayAdapter(getContext(), AnnouncementsDataList);
        // Set the adapter to the ListView
        announcementListView.setAdapter(announcementsAdapter);


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

                // references again
                dropDownLayout = view.findViewById(R.id.announcementsFragment_Extension);
                expandableArrow = view.findViewById(R.id.announcementsFragment_DropDown);
                itemClicked = view.findViewById(R.id.announcementsContent_itemClicked);
                fullRowLayout = view.findViewById(R.id.announcementsContent_Row);

                // display fragment
                if (dropDownLayout.getVisibility() == View.GONE){
                    dropDownLayout.setVisibility(View.VISIBLE);
                    expandableArrow.setImageResource(R.drawable.ic_up_arrow);
                } else {
                    dropDownLayout.setVisibility(View.GONE);
                    expandableArrow.setImageResource(R.drawable.ic_down_arrow);
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}