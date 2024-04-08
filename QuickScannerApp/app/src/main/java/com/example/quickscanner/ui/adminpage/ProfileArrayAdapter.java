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

//javadocs
/**
 * This Array Adapter customizes the presentation of the Profiles list
 */
public class ProfileArrayAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private ArrayList<User> mProfiles;

    //javadocs
    /**
     * Constructor for ProfileArrayAdapter
     * @param context
     * @param profiles
     */
    public ProfileArrayAdapter(@NonNull Context context, ArrayList<User> profiles) {
        super(context, R.layout.fragment_profiles_content, profiles);
        this.mContext = context;
        this.mProfiles = profiles;
    }

    //detailed javadocs
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
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.fragment_profiles_content, parent, false);

        User currentUser = mProfiles.get(position);

        CheckBox checkBox = listItem.findViewById(R.id.profile_checkbox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUser.setSelected(isChecked);
            ((BrowseProfilesActivity) mContext).updateDeleteButtonVisibility();
        });
        checkBox.setFocusable(false);  // Prevents the checkbox from being selected when the list item is clicked
        TextView name = listItem.findViewById(R.id.profile_name);

        String username = currentUser.getUserProfile().getName();
        if(username == null || username.isEmpty()) {
            username = "Anonymous user : " + currentUser.getUid();
        }
        name.setText(username);

        return listItem;
    }

    //javadocs
    /**
     * Checks if any user is selected
     * @return boolean
     */
    public boolean isAnyUserSelected() {
        for (User user : mProfiles) {
            if (user.isSelected()) {
                return true;
            }
        }
        return false;
    }
}