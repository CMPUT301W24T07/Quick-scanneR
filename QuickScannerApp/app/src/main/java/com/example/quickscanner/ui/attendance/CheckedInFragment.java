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

import com.example.quickscanner.controller.FirebaseAttendanceController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.model.User;
import com.example.quickscanner.databinding.FragmentAttendanceBinding;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

//javadocs
/**
 * This Fragment hosts our CheckedIn list for users to view.
 * Can see more event details by clicking an event.
 */
public class CheckedInFragment extends Fragment {

    private ListView listView;
    private CheckInAdapter adapter;
    private FirebaseAttendanceController fbAttendanceController;
    FirebaseUserController fbUserController;
    private ArrayList<User> checkInDataList;
    private TextView emptyCheckIn;
    private FragmentAttendanceBinding binding;
    private ListenerRegistration checkInListenerReg;

    //javadocs
    /**
     * This Fragment hosts our CheckedIn list for users to view.
     * Can see more event details by clicking an event.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAttendanceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    //javadocs
    /**
     * This Fragment hosts our CheckedIn list for users to view.
     * Can see more event details by clicking an event.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        fbAttendanceController = new FirebaseAttendanceController();
        fbUserController = new FirebaseUserController();
        if (bundle != null) {
            String eventId = bundle.getString("eventID", "");
            listView = binding.userListview;
            emptyCheckIn = binding.emptyCheckIn;
            emptyCheckIn.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            checkInDataList = new ArrayList<>();
            adapter = new CheckInAdapter(getContext(), checkInDataList,eventId);
            listView.setAdapter(adapter);
            if (checkInListenerReg == null) {
                checkInListenerReg = fbAttendanceController.setupCheckInListListener(eventId,checkInDataList,adapter,emptyCheckIn,listView);
            }

        }






    }

    //javadocs
    /**
     * This Fragment hosts our CheckedIn list for users to view.
     * Can see more event details by clicking an event.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("event list testing", "check in frag onDestroyView: Removing event list listener");
        if (checkInListenerReg != null) {
            checkInListenerReg.remove();
            checkInListenerReg = null;
        }
    }
}