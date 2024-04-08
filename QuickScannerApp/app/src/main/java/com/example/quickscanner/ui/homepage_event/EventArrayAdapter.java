package com.example.quickscanner.ui.homepage_event;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.model.*;
import com.squareup.picasso.Picasso;

//javadocs
/**
 * This Array Adapter customizes the presentation of the Events list
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    /*
        This Array Adapter customizes the presentation of the Events list
    */

    private ArrayList<Event> events;
    private Context context;
    private Map<Integer, Boolean> imageLoadedMap; // Map to track image loading status

    // TextView References
    TextView eventName;
    TextView eventDescription;
    TextView eventLocation;
    TextView eventOrganizers;
    TextView eventTime;
    ImageView eventImage;
    //ProgressBar imageLoading;


    //javadocs
    /**
     * Constructor for EventArrayAdapter
     * @param context
     * @param events
     */
    public EventArrayAdapter(Context context, ArrayList<Event> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
        this.imageLoadedMap = new HashMap<>();
    }


    //javadocs
    /**
     * Responsible for creating the View for each row in the ListView.
     * Called for each item(row) in the listview.
     * @param position
     * @param convertView
     * @param parent
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.fragment_events_content, parent,false);
        } else {
            view = convertView;
        }
        /* Gets the Event object at a given row (position) */
        Event event = events.get(position);
        //ProgressBar imageLoading = view.findViewById(R.id.image_loading);

        eventName = view.findViewById(R.id.EventFragment_Name_text);
        eventImage = view.findViewById(R.id.EventFragment_Image);
        eventTime = view.findViewById(R.id.EventFragment_Time);
        eventDescription = view.findViewById(R.id.EventFragment_LongDescription);
        eventLocation = view.findViewById(R.id.EventFragment_Location);

        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText(event.getLocation());
        eventTime.setText(event.getTimeAsString());


        // Check if the image is already loaded for this position
        if (imageLoadedMap.containsKey(position) && imageLoadedMap.get(position)) {
            Log.d("EventArrayAdapter", "Image already loaded for position: " + position);
            // Image already loaded, do nothing
        } else {
            Log.d("EventArrayAdapter", "Loading image for position: " + position);
            // Image not loaded, load it using Picasso
            FirebaseImageController fbImageController = new FirebaseImageController();
            fbImageController.downloadImage(event.getImagePath()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String url = String.valueOf(task.getResult());
                    Picasso.get().load(url).placeholder(null).into(eventImage);
                    // Mark image as loaded for this position
                    imageLoadedMap.put(position, true);
                    Log.d("EventArrayAdapter", "Image loaded successfully for position: " + position);
                } else {
                    Log.d("EventArrayAdapter", "Failed to download event image for position: " + position);
                }
            });
        }

        return view;
    }
}
