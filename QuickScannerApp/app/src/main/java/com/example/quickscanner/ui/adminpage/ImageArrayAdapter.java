package com.example.quickscanner.ui.adminpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.quickscanner.R;
import com.example.quickscanner.model.Image;
import com.squareup.picasso.Picasso;

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

        ImageView imageView = listItem.findViewById(R.id.image_view);
        Picasso.get().load(currentImage.getImageUrl()).into(imageView);

        return listItem;
    }
}