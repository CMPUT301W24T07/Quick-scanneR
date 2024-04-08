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
import com.example.quickscanner.controller.FirebaseAttendanceController;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.databinding.AttendanceCheckInContentBinding;
import com.example.quickscanner.databinding.AttendanceSignUpContentBinding;
import com.example.quickscanner.model.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//javadocs
/**
 * This Array Adapter customizes the presentation of the CheckIn list
 */
public class CheckInAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private ArrayList<User> mAttendees;
    private FirebaseImageController fbImageController;
    private FirebaseAttendanceController fbAttendanceController;

    //javadocs
    /**
     * Constructor for CheckInAdapter
     * @param context
     * @param attendees
     */
    public CheckInAdapter(@NonNull Context context, ArrayList<User> attendees) {
        super(context, R.layout.attendance_sign_up_content, attendees);
        this.mContext = context;
        this.mAttendees = attendees;
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
        AttendanceCheckInContentBinding binding;
        View view;
        if(convertView == null) {
            binding = AttendanceCheckInContentBinding.inflate(LayoutInflater.from(mContext), parent, false);
            view = binding.getRoot();
            view.setTag(binding);
        } else {
            binding = (AttendanceCheckInContentBinding) convertView.getTag();
            view = convertView;
        }
        fbAttendanceController = new FirebaseAttendanceController();
        ProgressBar loadingSpinner = binding.loadCheckIn;
        loadingSpinner.setVisibility(View.VISIBLE);

        User curAttendees = mAttendees.get(position);
        String currentName = curAttendees.getUserProfile().getName();
        String profilePicturePath = curAttendees.getUserProfile().getImageUrl();
        fbAttendanceController.getTimesCheckedIn(curAttendees.getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String timesCheckedIn = task.getResult();
                TextView timesCheckedInTextView = binding.timesCheckedIn;
                timesCheckedInTextView.setText(String.valueOf(timesCheckedIn));
            }
        });
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