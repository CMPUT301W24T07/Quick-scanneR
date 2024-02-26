package com.example.quickscanner.ui.announcements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quickscanner.databinding.FragmentAnnouncementsBinding;

public class AnnouncementsFragment extends Fragment {

    private FragmentAnnouncementsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnnouncementsViewModel announcementsViewModel =
                new ViewModelProvider(this).get(AnnouncementsViewModel.class);

        binding = FragmentAnnouncementsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAnnouncements;
        announcementsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}