package com.example.quickscanner.ui.announcements;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import com.example.quickscanner.R;
import com.example.quickscanner.model.*;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class AnnouncementsArrayAdapter extends ArrayAdapter<Announcement>{
    private ArrayList<Announcement> announcements;
    private Context context;

    public AnnouncementsArrayAdapter(Context context, ArrayList<Announcement> announcements){
        super(context,0, announcements);
        this.announcements = announcements;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /*
            Responsible for creating the View for each row in the ListView.
            Called for each item(row) in the listview.
        * */
        View view = convertView;

        if(view == null){
            /*  If recycled view is null, inflates with custom (*_context.xml) layout. */
            view = LayoutInflater.from(context).inflate(R.layout.fragment_announcements_content, parent,false);
        }

        /* Gets the object at a given row (position) */
        Announcement announcement = announcements.get(position);

        // Elements from the custom (*_context.xml) view
        TextView eventName = view.findViewById(R.id.AnnouncementFragment_Event_text);
        TextView announcementDescription = view.findViewById(R.id.AnnouncementFragment_Description_text);


        // Set values of Elements from the custom (*_context.xml) view
        eventName.setText(announcement.getName());
        announcementDescription.setText(announcement.getDescription());


        /* Return the populated, custom view ( which is a row in listview).
         i.e. returns a customized row in the listview */
        return view;
    }




}
