package com.example.quickscanner.ui.homepage_event;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
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
import android.os.Handler;


import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.model.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EventArrayAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;
    private FirebaseImageController imageController;

    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
        this.imageController = new FirebaseImageController();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_events_content, parent, false);
            holder = new ViewHolder();
            holder.eventName = view.findViewById(R.id.EventFragment_Name_text);
            holder.eventImage = view.findViewById(R.id.EventFragment_Image);
            holder.eventTime = view.findViewById(R.id.EventFragment_Time);
            holder.eventDescription = view.findViewById(R.id.EventFragment_LongDescription);
            holder.eventLocation = view.findViewById(R.id.EventFragment_Location);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            // Reset image to default while waiting for the new image to load
            holder.eventImage.setImageResource(R.drawable.ic_home_black_24dp);
        }

        // Gets the Event object at a given row (position)
        Event event = events.get(position);

        // Set text data
        holder.eventName.setText(event.getName());
        holder.eventDescription.setText(event.getDescription());
        holder.eventLocation.setText(event.getLocation());
        holder.eventTime.setText(event.getTimeAsString());

        // Load event image
        loadImageForEvent(event, holder.eventImage);

        return view;
    }

    public void loadImageForEvent(Event event, ImageView imageView) {
        FirebaseImageController imageController = new FirebaseImageController();
        Log.d("EventArrayAdapter", "Loading image for event: " + event.getName() + " from path: " + event.getImagePath());
        loadImage(imageController, event.getImagePath(), imageView, 3); // Retry up to 3 times
    }

    private void loadImage(FirebaseImageController imageController, String imagePath, ImageView imageView, int retryCount) {
        imageController.downloadImage(imagePath)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            String url = downloadUri.toString();
                            Picasso.get().load(url).into(imageView);
                        } else {
                            Log.d("EventArrayAdapter", "Downloaded URI is null, setting default image");
                            imageView.setImageResource(R.drawable.ic_home_black_24dp);
                        }
                    } else {
                        Log.e("EventArrayAdapter", "Failed to download event image: " + task.getException().getMessage());
                        if (retryCount > 0) {
                            // Retry the operation after a delay
                            new Handler(Looper.getMainLooper()).postDelayed(() -> loadImage(imageController, imagePath, imageView, retryCount - 1), 1000); // Retry after 1 second
                        } else {
                            // Retry limit exceeded, set default image
                            imageView.setImageResource(R.drawable.ic_home_black_24dp);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EventArrayAdapter", "Failed to download event image: " + e.getMessage());
                    if (retryCount > 0) {
                        // Retry the operation after a delay
                        new Handler(Looper.getMainLooper()).postDelayed(() -> loadImage(imageController, imagePath, imageView, retryCount - 1), 1000); // Retry after 1 second
                    } else {
                        // Retry limit exceeded, set default image
                        imageView.setImageResource(R.drawable.ic_home_black_24dp);
                    }
                });
    }


    private static class ViewHolder {
        TextView eventName;
        TextView eventDescription;
        TextView eventLocation;
        TextView eventTime;
        ImageView eventImage;
    }
}

