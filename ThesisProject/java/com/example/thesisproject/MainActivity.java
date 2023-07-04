package com.example.thesisproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    final Handler handler = new Handler();
    int NightMode, language;
    SharedPreferences sharedPreferences;

    @SuppressWarnings("All")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        NightMode = sharedPreferences.getInt("night_mode",0);
        switch(NightMode){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        language = sharedPreferences.getInt("language",0);
        switch (language) {
            case 0:
                Locale locale_new = new Locale("en");
                sharedPreferences.edit().putInt("language",0).apply();
                Locale.setDefault(locale_new);
                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                config.setLocale(locale_new);
                resources.updateConfiguration(config, resources.getDisplayMetrics());
                break;
            case 1:
                Locale locale_new2 = new Locale("tr");
                sharedPreferences.edit().putInt("language",1).apply();
                Locale.setDefault(locale_new2);
                Resources resources2 = getResources();
                Configuration config2 = resources2.getConfiguration();
                config2.setLocale(locale_new2);
                resources2.updateConfiguration(config2, resources2.getDisplayMetrics());
                break;
        }

        handler.postDelayed(this::endIntro,1000);
    }
    void endIntro() {
        Intent intent = new Intent(MainActivity.this, LoginScreenActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }
}