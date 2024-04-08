package com.example.quickscanner.ui.adminpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import com.example.quickscanner.R;
import com.example.quickscanner.model.*;

public class AdminEventArrayAdapter extends ArrayAdapter<Event> {
    /*
        This Array Adapter customizes the presentation of the Events list
    */

    private ArrayList<Event> events;
    private Context context;

    // TextView References
    TextView eventName;
    TextView eventDescription;
    TextView eventLocation;
    TextView eventOrganizers;
    TextView eventTime;



    public AdminEventArrayAdapter(Context context, ArrayList<Event> events){
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
            view = LayoutInflater.from(context).inflate(R.layout.fragment_admin_events_content, parent,false);
        }

        /* Gets the Event object at a given row (position) */
        Event event = events.get(position);

        // Elements from the custom (*_context.xml) view
        eventName = view.findViewById(R.id.EventFragment_Name_text);
        eventDescription = view.findViewById(R.id.EventFragment_LongDescription);
        eventLocation = view.findViewById(R.id.EventFragment_Location);
        // eventOrganizers = view.findViewById(R.id.EventFragment_Organizers_text);
        eventTime = view.findViewById(R.id.EventFragment_Time);

        // Set values of Elements from the custom (*_context.xml) view
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText(event.getLocation());
        // eventOrganizers.setText(event.getOrganizer().getUserProfile().getName());
        eventTime.setText(event.getTimeAsString());

        /* Return the populated, custom view ( which is a row in listview).
         i.e. returns a customized row in the events listview */

        CheckBox checkBox = view.findViewById(R.id.admin_event_checkbox);
        checkBox.setFocusable(false);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            events.get(position).setSelected(isChecked);
            ((BrowseEventsActivity) context).updateDeleteButtonVisibility();
        });
        checkBox.setChecked(events.get(position).isSelected());
        return view;
    }

    public boolean isAnyEventSelected() {
        for (Event event : events) {
            if (event.isSelected()) {
                return true;
            }
        }
        return false;
    }

}
