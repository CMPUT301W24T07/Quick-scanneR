package com.example.quickscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickscanner.model.Announcement;

import java.util.ArrayList;

public class AnnouncementArrayAdapter extends ArrayAdapter<Announcement>
{    /*
    This Array Adapter customizes the presentation of the Announcements list
*/
    private ArrayList<Announcement> announcements;
    private Context context;

    public AnnouncementArrayAdapter(Context context, ArrayList<Announcement> announcements){
        super(context,0, announcements);
        this.announcements = announcements;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
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
        TextView eventName = view.findViewById(R.id.announcementsContent_Title);
        TextView announcementLongDescription = view.findViewById(R.id.announcementsFragment_LongDescription);
        TextView announcementShortDescription = view.findViewById(R.id.announcementsContent_ShortDescription);


        // Set values of Elements from the custom (*_context.xml) view
        eventName.setText(announcement.getEventName());
        String description = announcement.getMessage();
        announcementLongDescription.setText(description);
        // Short description formatting.
        if (description.length() <= 20)
            announcementShortDescription.setText(description);
        else
            announcementShortDescription.setText(description.substring(0, 20) + "...");


        /* Return the populated, custom view ( which is a row in listview).
         i.e. returns a customized row in the listview */
        return view;
    }



}
