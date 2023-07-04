package com.example.thesisproject;
import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    Button password_reset_button,delete_account_button,dark_mode_button, language_button;
    SharedPreferences sharedPreferences;

    @SuppressWarnings("All")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        password_reset_button = view.findViewById(R.id.password_reset_button);
        delete_account_button = view.findViewById(R.id.delete_account_button);
        dark_mode_button = view.findViewById(R.id.dark_mode_button);
        language_button = view.findViewById(R.id.language_button);

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        FirebaseUser user = auth.getCurrentUser();
        password_reset_button.setOnClickListener(v -> {
            if(user != null) {
                new AlertDialog.Builder(requireActivity()).setTitle(getString(R.string.password_reset)).setMessage(getString(R.string.password_reset_email))
                        .setPositiveButton(getString(R.string.yes), (dialog, which) ->
                                auth.sendPasswordResetEmail(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail())))
                        .setNegativeButton(getString(R.string.no), null).setIcon(android.R.drawable.ic_dialog_alert).show();
            } else {
                new AlertDialog.Builder(requireActivity()).setTitle(getString(R.string.password_reset)).setMessage(getString(R.string.header_nologin)).show();}
        });

        delete_account_button.setOnClickListener(v -> {
            if(user != null) {
                new AlertDialog.Builder(requireActivity()).setTitle(getString(R.string.delete_account)).setMessage(getString(R.string.delete_account_final))
                        .setPositiveButton(getString(R.string.yes), (dialog, which) ->
                                user.delete())
                        .setNegativeButton(getString(R.string.no), null).setIcon(android.R.drawable.ic_dialog_alert).show();
            } else {
                new AlertDialog.Builder(requireActivity()).setTitle(getString(R.string.delete_account)).setMessage(getString(R.string.header_nologin)).show();}
        });


        int nightModeFlags = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                dark_mode_button.setText(getString(R.string.enter_light_mode));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                dark_mode_button.setText(getString(R.string.enter_dark_mode));
                break;
        }

        dark_mode_button.setOnClickListener(v -> {
            int light_or_dark = sharedPreferences.getInt("night_mode",0);
            switch (light_or_dark) {
                case 0:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPreferences.edit().putInt("night_mode", 1).apply();
                    dark_mode_button.setText(getString(R.string.enter_light_mode));
                    break;
                case 1:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPreferences.edit().putInt("night_mode", 0).apply();
                    dark_mode_button.setText(getString(R.string.enter_dark_mode));
                    break;
            }
        });

        int language = sharedPreferences.getInt("language",0);
        switch (language){
            case 0:
                language_button.setText("Change Language");
                break;
            case 1:
                language_button.setText("Dil Değiştir");
                break;
        }

        language_button.setOnClickListener(v -> {
            switch (language){
                case 0:
                    sharedPreferences.edit().putInt("language",1).apply();
                    Locale locale_new = new Locale("tr");
                    Locale.setDefault(locale_new);
                    Resources resources = requireActivity().getResources();
                    Configuration config = resources.getConfiguration();
                    config.setLocale(locale_new);
                    resources.updateConfiguration(config, resources.getDisplayMetrics());
                    requireActivity().recreate();
                    break;
                case 1:
                    sharedPreferences.edit().putInt("language",0).apply();
                    Locale locale_new2 = new Locale("en");
                    Locale.setDefault(locale_new2);
                    Resources resources2 = requireActivity().getResources();
                    Configuration config2 = resources2.getConfiguration();
                    config2.setLocale(locale_new2);
                    resources2.updateConfiguration(config2, resources2.getDisplayMetrics());
                    requireActivity().recreate();
                    break;
            }
        });

        return view;
    }
}