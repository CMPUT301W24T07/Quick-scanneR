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

import com.example.quickscanner.R;
import com.example.quickscanner.model.User;

import java.util.ArrayList;

public class ProfileArrayAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private ArrayList<User> mProfiles;

    public ProfileArrayAdapter(@NonNull Context context, ArrayList<User> profiles) {
        super(context, R.layout.fragment_profiles_content, profiles);
        this.mContext = context;
        this.mProfiles = profiles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.fragment_profiles_content, parent, false);

        User currentUser = mProfiles.get(position);

        CheckBox checkBox = listItem.findViewById(R.id.profile_checkbox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUser.setSelected(isChecked);
            ((BrowseProfilesActivity) mContext).updateDeleteButtonVisibility();
        });

        TextView name = listItem.findViewById(R.id.profile_name);
        name.setText(currentUser.getUserProfile().getName());

        return listItem;
    }

    public boolean isAnyUserSelected() {
        for (User user : mProfiles) {
            if (user.isSelected()) {
                return true;
            }
        }
        return false;
    }
}