package com.example.quickscanner.ui.adminpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.Image;
import com.example.quickscanner.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class ImageArrayAdapter extends ArrayAdapter<Image> {

    private Context mContext;
    private ArrayList<Image> mImages;

    private FirebaseUserController fbUserController;
    private FirebaseEventController fbEventController;

    public ImageArrayAdapter(Context context, ArrayList<Image> images) {
        super(context, R.layout.fragment_images_content, images);
        this.mContext = context;
        this.mImages = images;
        fbUserController = new FirebaseUserController();
        fbEventController = new FirebaseEventController();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.fragment_images_content, parent, false);

        Image currentImage = mImages.get(position);

        CheckBox checkBox = listItem.findViewById(R.id.admin_image_checkbox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentImage.setSelected(isChecked);
            ((BrowseImagesActivity) mContext).updateDeleteButtonVisibility();
        });
        checkBox.setFocusable(false);  // Prevents the checkbox from being selected when the list item is clicked


        // Fetch and display user or event name
        TextView name = listItem.findViewById(R.id.name);

        if(currentImage.getSource()!= null && currentImage.getSource().equals("user")){
            fbUserController.getUserByImageURL(currentImage.getImageUrl())
                    .addOnCompleteListener(new OnCompleteListener<User>() {
                        @Override
                        public void onComplete(@NonNull Task<User> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult();
                                if (user != null && user.getUserProfile() != null) {
                                    name.setText(user.getUserProfile().getName());
                                } else {
                                    name.setText("Not found");
                                }
                            } else {
                                name.setText("Not found");
                            }
                        }
                    });
        } else if(currentImage.getSource()!= null && currentImage.getSource().equals("event")){
            fbEventController.getEventByImageURL(currentImage.getImageUrl())
                    .addOnCompleteListener(new OnCompleteListener<Event>() {
                        @Override
                        public void onComplete(@NonNull Task<Event> task) {
                            if (task.isSuccessful()) {
                                Event event = task.getResult();
                                if (event != null) {
                                    name.setText(event.getName());
                                } else {
                                    name.setText("Not found");
                                }
                            } else {
                                name.setText("Not found");
                            }
                        }
                    });
        }
        else {

            fbUserController.getUserByImageURL(currentImage.getImageUrl())
                    .addOnCompleteListener(new OnCompleteListener<User>() {
                        @Override
                        public void onComplete(@NonNull Task<User> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult();
                                if (user != null && user.getUserProfile() != null) {
                                    name.setText(user.getUserProfile().getName());
                                } else {
                                    fbEventController.getEventByImageURL(currentImage.getImageUrl())
                                            .addOnCompleteListener(new OnCompleteListener<Event>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Event> task) {
                                                    if (task.isSuccessful()) {
                                                        Event event = task.getResult();
                                                        if (event != null) {
                                                            name.setText(event.getName());
                                                        } else {
                                                            name.setText("Not found");
                                                        }
                                                    } else {
                                                        name.setText("Not found");
                                                    }
                                                }
                                            });
                                }
                            } else {
                                name.setText("Not found");
                            }
                        }
                    });
        }

        return listItem;
    }

    public boolean isAnyImageSelected() {
        for (Image image : mImages) {
            if (image.isSelected()) {
                return true;
            }
        }
        return false;
    }
}