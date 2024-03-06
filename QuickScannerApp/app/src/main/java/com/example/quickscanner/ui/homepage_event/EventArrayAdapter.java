package com.example.quickscanner.ui.homepage_event;

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

public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;

    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
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
            view = LayoutInflater.from(context).inflate(R.layout.fragment_events_content, parent,false);
        }

        /* Gets the Event object at a given row (position) */
        Event event = events.get(position);

        // Elements from the custom (*_context.xml) view
        TextView eventName = view.findViewById(R.id.EventFragment_Name_text);
        TextView eventDescription = view.findViewById(R.id.EventFragment_Description_text);
        TextView eventLocation = view.findViewById(R.id.EventFragment_Location_text);
        TextView eventOrganizers = view.findViewById(R.id.EventFragment_Organizers_text);
        TextView eventTime = view.findViewById(R.id.EventFragment_Time_text);

        // Set values of Elements from the custom (*_context.xml) view
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText(event.getLocation());
        eventOrganizers.setText(event.getOrganizer().getUserProfile().getName());
        eventTime.setText(event.getTime());

        /* Return the populated, custom view ( which is a row in listview).
         i.e. returns a customized row in the events listview */
        return view;
    }
}
