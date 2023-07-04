package com.example.thesisproject;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class HomeFragment extends Fragment {
    View view;
    BottomNavigationView bottomNavigationView;
    File f;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
            if (!f.exists()) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference pathReference = storageRef.child("recommender.db");
                pathReference.getDownloadUrl().addOnSuccessListener(uri2 -> {
                    String url = uri2.toString();
                    downloadFile(requireContext().getApplicationContext(), "database", ".db", DIRECTORY_DOWNLOADS, url);
                });
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        sharedPreferences.edit().putBoolean("cpu_item1",false).apply();
        sharedPreferences.edit().putBoolean("cpu_item2",false).apply();

        sharedPreferences.edit().putBoolean("gpu_item1",false).apply();
        sharedPreferences.edit().putBoolean("gpu_item2",false).apply();

        sharedPreferences.edit().putBoolean("laptop_item1",false).apply();
        sharedPreferences.edit().putBoolean("laptop_item2",false).apply();

        return view;
    }

    public void downloadFile(@NonNull Context context, String filaName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,filaName + fileExtension);
        downloadManager.enqueue(request);
    }
}