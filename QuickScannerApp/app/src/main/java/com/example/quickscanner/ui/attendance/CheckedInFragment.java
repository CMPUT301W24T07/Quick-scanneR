package com.example.quickscanner.ui.attendance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickscanner.controller.FirebaseAttendanceController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.User;
import com.example.quickscanner.databinding.FragmentAttendanceBinding;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class CheckedInFragment extends Fragment {

    private ListView listView;
    private CheckInAdapter adapter;
    private FirebaseAttendanceController fbAttendanceController;
    FirebaseUserController fbUserController;
    private ArrayList<User> checkInDataList;
    private TextView emptyCheckIn;
    private FragmentAttendanceBinding binding;
    private ListenerRegistration checkInListenerReg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = binding.userListview;
        emptyCheckIn = binding.emptyCheckIn;
        emptyCheckIn.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        checkInDataList = new ArrayList<>();
        adapter = new CheckInAdapter(getContext(), checkInDataList);
        listView.setAdapter(adapter);


        fbAttendanceController = new FirebaseAttendanceController();
        fbUserController = new FirebaseUserController();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String eventId = bundle.getString("eventID", "");
            checkInListenerReg = fbAttendanceController.setupCheckInListListener(eventId,checkInDataList,adapter,emptyCheckIn,listView);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (checkInListenerReg != null) {
            checkInListenerReg.remove();
            checkInListenerReg = null;
        }
    }
}