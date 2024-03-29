package com.example.quickscanner.ui.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quickscanner.R;
import com.example.quickscanner.model.User;

import java.util.ArrayList;

public class SignUpAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private ArrayList<User> mAttendees;

    public SignUpAdapter(@NonNull Context context, ArrayList<User> attendees) {
        super(context, R.layout.attendance_sign_up_content, attendees);
        this.mContext = context;
        this.mAttendees = attendees;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.attendance_sign_up_content, parent, false);
        } else {
            view = convertView;
        }

        User curAttendees = mAttendees.get(position);
        String currentName = curAttendees.getUserProfile().getName();

        TextView name = view.findViewById(R.id.attendee_name);
        if (currentName == null || currentName.trim().isEmpty()) {
            name.setText("Anonymous User");
        } else {
            name.setText(currentName);
        }

        return view;
    }
}