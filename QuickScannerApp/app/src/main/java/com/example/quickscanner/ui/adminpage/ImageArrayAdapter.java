package com.example.quickscanner.ui.adminpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.quickscanner.R;
import com.example.quickscanner.model.Image;

import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter<Image> {

    private Context mContext;
    private ArrayList<Image> mImages;

    public ImageArrayAdapter(Context context, ArrayList<Image> images) {
        super(context, R.layout.fragment_images_content, images);
        this.mContext = context;
        this.mImages = images;
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

        TextView imageUrl = listItem.findViewById(R.id.image_url);
        imageUrl.setText(currentImage.getImageUrl());

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