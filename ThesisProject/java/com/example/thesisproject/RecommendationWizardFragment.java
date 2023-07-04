package com.example.thesisproject;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RecommendationWizardFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    Button cpu_button, gpu_button, laptop_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendationwizard, container, false);

        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(4).setChecked(true);

        cpu_button = view.findViewById(R.id.cpu_button);
        gpu_button = view.findViewById(R.id.gpu_button);
        laptop_button = view.findViewById(R.id.laptop_button);

        cpu_button.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CPURecommenderFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
        });

        gpu_button.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GPURecommenderFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
        });

        laptop_button.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LaptopRecommenderFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
        });

        return view;
    }
}