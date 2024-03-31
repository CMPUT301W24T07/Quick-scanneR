package com.example.quickscanner.ui.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.databinding.AttendanceSignUpContentBinding;
import com.example.quickscanner.model.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SignUpAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private ArrayList<User> mAttendees;
    private FirebaseImageController fbImageController;

    public SignUpAdapter(@NonNull Context context, ArrayList<User> attendees) {
        super(context, R.layout.attendance_sign_up_content, attendees);
        this.mContext = context;
        this.mAttendees = attendees;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AttendanceSignUpContentBinding binding;
        View view;
        if(convertView == null) {
            binding = AttendanceSignUpContentBinding.inflate(LayoutInflater.from(mContext), parent, false);
            view = binding.getRoot();
            view.setTag(binding);
        } else {
            binding = (AttendanceSignUpContentBinding) convertView.getTag();
            view = convertView;
        }

        User curAttendees = mAttendees.get(position);
        String currentName = curAttendees.getUserProfile().getName();
        String profilePicturePath = curAttendees.getUserProfile().getImageUrl();
        ProgressBar loadingSpinner = binding.loadSignUp;
        loadingSpinner.setVisibility(View.VISIBLE);


        TextView name = binding.attendeeName;
        ImageView profilePicture = binding.profilePictureSignUp;
        if (currentName == null || currentName.trim().isEmpty()) {
            name.setText("Anonymous User");
        } else {
            name.setText(currentName);
        }
        FirebaseImageController fbImageController = new FirebaseImageController();
        fbImageController.downloadImage(profilePicturePath).addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .into(profilePicture, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadingSpinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            loadingSpinner.setVisibility(View.GONE);
                        }
                    });
        });


        return view;
    }
}