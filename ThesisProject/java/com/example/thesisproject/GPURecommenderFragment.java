package com.example.thesisproject;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GPURecommenderFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    SeekBar vram_seekbar, gpu_score_seekbar, average_price_seekbar;
    Button recommend_button;
    File f;
    SQLiteDatabase database;
    RecyclerView recyclerView;
    GPUItemRecyclerViewAdapter GPUAdapter;
    Cursor cursor;
    List<GPUItem> GPU_ItemList = new ArrayList<>();
    GPUItem gpu_item;
    Drawable gpuPhoto;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gpurecommender, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(4).setChecked(true);

        vram_seekbar = view.findViewById(R.id.vram_seekbar);
        gpu_score_seekbar = view.findViewById(R.id.gpu_score_seekbar);
        average_price_seekbar = view.findViewById(R.id.average_price_seekbar);
        recommend_button = view.findViewById(R.id.recommend_button);
        recyclerView = view.findViewById(R.id.gpuRecyclerView);

        GPUAdapter = new GPUItemRecyclerViewAdapter(GPU_ItemList);
        recyclerView.setAdapter(GPUAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(f,null);

        recommend_button.setOnClickListener(v -> {
            int vram_value = vram_seekbar.getProgress();
            int gpu_score_value = gpu_score_seekbar.getProgress();
            int average_price_value = -1 * average_price_seekbar.getProgress();

            String select = "SELECT GPUS.[index], GPUS.[Bellek Boyutu(GB)], GPUS.[Ekran Kartı Modeli], GPUS.[Ekran Kartı Skoru]," +
                    "GPUS.[En Düşük Fiyat], GPUS.[Medyan Fiyat], GPUS.[En Düşük Fiyat], GPUS.[Marka],";
            String scaled = vram_value + "*ScaledGPUS.[Bellek Boyutu(GB)] +" + gpu_score_value + "*ScaledGPUS.[Ekran Kartı Skoru] +" + average_price_value + "*ScaledGPUS.[Medyan Fiyat]" + " AS SCORE";

            cursor = database.rawQuery(select + scaled + " " +
                    "FROM GPUS, ScaledGPUS" + " " +
                    "WHERE GPUS.[index] = ScaledGPUS.[index] AND GPUS.[En Düşük Fiyat] > 1000 ORDER BY SCORE DESC", null);

            GPU_ItemList.clear();
            GPU_ItemList = getGPU_ItemList(cursor);
            GPUAdapter.notifyDataSetChanged();

        });
        return view;
    }

    public List<GPUItem> getGPU_ItemList(Cursor cursor){
        int index_index = cursor.getColumnIndex("index");
        int vram_index = cursor.getColumnIndex("Bellek Boyutu(GB)");
        int gpu_model_index = cursor.getColumnIndex("Ekran Kartı Modeli");
        int lowestPrice_index = cursor.getColumnIndex("En Düşük Fiyat");
        int medianPrice_index = cursor.getColumnIndex("Medyan Fiyat");
        int gpu_score_index = cursor.getColumnIndex("Ekran Kartı Skoru");
        int gpu_brand_index = cursor.getColumnIndex("Marka");

        int a = 0;
        if (cursor.moveToFirst()) {
            do {
                a++;
                int index = cursor.getInt(index_index);
                float vram = cursor.getFloat(vram_index);
                String gpu_model = cursor.getString(gpu_model_index);
                float gpu_score = cursor.getFloat(gpu_score_index);
                String gpu_brand = cursor.getString(gpu_brand_index);
                Float lowestPrice = cursor.getFloat(lowestPrice_index);
                Float medianPrice = cursor.getFloat(medianPrice_index);

                if (gpu_brand.equals("İZOLY") || gpu_brand.equals("İzoly")){
                    gpu_brand = "izoly";
                }

                if (gpu_score != 0.0f) {
                    @SuppressLint("DiscouragedApi")
                    String x = gpu_brand.toLowerCase(Locale.ROOT);
                    @SuppressLint("DiscouragedApi") int processorNameID = getResources().getIdentifier(x,"drawable",requireActivity().getPackageName());
                    gpuPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
                    gpu_item = new GPUItem(index,vram,gpu_model,gpu_score,gpu_brand,lowestPrice,medianPrice,gpuPhoto,null,0);
                    GPU_ItemList.add(gpu_item);
                }

            } while (a < 10 && cursor.moveToNext());
        }
        return GPU_ItemList;
    }
}