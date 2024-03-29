package com.example.quickscanner.ui.attendance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseAttendanceController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.adminpage.ProfileArrayAdapter;

import java.util.ArrayList;

public class SignedUpFragment extends Fragment
{

    private ListView listView;
    private SignUpAdapter adapter;
    private FirebaseAttendanceController fbAttendanceController;
    private ArrayList<User> signUpDataList;
    private TextView emptySignUp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        listView = view.findViewById(R.id.user_listview);
        emptySignUp = view.findViewById(R.id.empty_sign_up);
        listView.setEmptyView(emptySignUp);
        signUpDataList = new ArrayList<>();
        adapter = new SignUpAdapter(getContext(), signUpDataList);
        listView.setAdapter(adapter);

        fbAttendanceController = new FirebaseAttendanceController();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String eventId = bundle.getString("eventId", "");
            fbAttendanceController.getEventSignUps(eventId).addOnSuccessListener(users -> {
                signUpDataList.clear();
                signUpDataList.addAll(users);
                adapter.notifyDataSetChanged();
            }).addOnFailureListener(e -> {
                Log.e("Firestore failed to load users:", e.toString());
            });
        }
        //TODO make it update real time.
        return view;
    }
}