package com.example.thesisproject;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FavouritesFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    String user_id;
    List<Integer> cpu_likes;
    List<Integer> gpu_likes;
    List<Integer> laptop_likes;
    File f;
    SQLiteDatabase database;
    Cursor cpu_cursor;
    Cursor gpu_cursor;
    Cursor laptop_cursor;
    String[] cpu_list;
    String[] gpu_list;
    String[] laptop_list;

    String processorName;
    Drawable processorPhoto;
    List<CPUItem> CPU_ItemList = new ArrayList<>();
    CPUItem cpu_item;
    String[] intel_i3 = new String[] {"Core i3","İntel Core i3","Intel Core i3"};
    String[] intel_i5 = new String[] {"Core i5","İntel Core i5","Intel Core i5"};
    String[] intel_i7 = new String[] {"Core i7","İntel Core i7","Intel Core i7"};
    String[] intel_i9 = new String[] {"Core i9","İntel Core i9","Intel Core i9"};
    String[] intel_xeon = new String[] {"Xeon","Xeon E","Xeon E5","Intel Xeon E5","Xeon Gold","Intel Xeon Gold","Xeon Silver","Intel Xeon Silver"};
    String[] intel = new String[] {"Celeron","Celeron G","Core 2 Duo","Core 2 Quad","Pentium","Intel Pentium",};
    String[] amd_ryzen3 = new String[] {"Ryzen 3","AMD Ryzen 3","Ryzen 3 Pro"};
    String[] amd_ryzen5 = new String[] {"Ryzen 5","AMD Ryzen 5","Ryzen 5 Pro"};
    String[] amd_ryzen7 = new String[] {"Ryzen 7","AMD Ryzen 7","Ryzen 7 Pro"};
    String[] amd_ryzen9 = new String[] {"Ryzen 9","AMD Ryzen 9","Ryzen 9 Pro"};
    String[] amd_ryzen = new String[] {"AMD A8","Athlon","AMD Ryzen","Athlon II X4"};

    Drawable gpuPhoto;
    List<GPUItem> GPU_ItemList = new ArrayList<>();
    GPUItem gpu_item;

    List<LaptopItem> laptop_ItemList = new ArrayList<>();
    LaptopItem laptop_item;
    Drawable laptopPhoto;

    RecyclerView cpu_recyclerView, gpu_recyclerView, laptop_recyclerView;
    CPUItemRecyclerViewAdapter CPUAdapter;
    GPUItemRecyclerViewAdapter GPUAdapter;
    LaptopItemRecyclerViewAdapter LaptopAdapter;

    @SuppressWarnings({"unchecked"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(f,null);

        String cpu_search_base = "SELECT [index], [İşlemci], [İşlemci Serisi], [İşlemci Modeli], [Arttırılmış Frekans(GHz)], " +
                "[Temel Frekans(GHz)], [En Düşük Fiyat], [Medyan Fiyat], [İşlemci Skoru] FROM CPUS";

        String gpu_search_base = "SELECT [index], [Bellek Boyutu(GB)], [İşlemci Üreticisi], [Ekran Kartı Modeli], [Ekran Kartı Skoru]," +
                "[En Düşük Fiyat], [Medyan Fiyat], [Marka] FROM GPUS";

        String laptop_search_base = "SELECT [index], [Ekran Boyutu(inç)], [Ram(Bellek) Boyutu(GB)], [İşlemci Modeli], [İşlemci Markası], [İşlemci Arttırılmış Frekans(GHz)], [Ürün Adı], [En Düşük Fiyat], [Medyan Fiyat], " +
                "[Harici Ekran Kartı Belleği(GB)], [Harici Ekran Kartı Modeli], [Marka] FROM NOTEBOOKS";


        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                cpu_likes = (List<Integer>) documentSnapshot.get("cpu-likes");
                gpu_likes = (List<Integer>) documentSnapshot.get("gpu-likes");
                laptop_likes = (List<Integer>) documentSnapshot.get("laptop-likes");

                String global_price_base_limit = "WHERE [En Düşük Fiyat] > 500 AND [index] = ";

                cpu_list = new String[cpu_likes.size()];
                for(int i=0;i<cpu_likes.size();i++){
                    cpu_list[i] = String.valueOf(cpu_likes.get(i));
                }

                String cpu_query = "";
                if (cpu_list != null){
                    for(int i=0;i<cpu_list.length;i++){
                        if(i == 0){
                            cpu_query = cpu_query.concat(cpu_list[i]);
                        } else {
                            cpu_query = cpu_query.concat(" OR [index] = " + cpu_list[i]);
                        }
                    }
                }

                global_price_base_limit += cpu_query;

                CPU_ItemList.clear();
                try{
                    cpu_cursor = database.rawQuery(cpu_search_base + " " + global_price_base_limit,null);
                } catch (SQLiteException ignored){}

                if (cpu_cursor != null && cpu_list != null) {
                    CPU_ItemList = getCPU_ItemList(cpu_cursor);
                    cpu_recyclerView = view.findViewById(R.id.cpuRecyclerView);
                    CPUAdapter = new CPUItemRecyclerViewAdapter(CPU_ItemList);
                    cpu_recyclerView.setAdapter(CPUAdapter);
                    cpu_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    cpu_recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
                    cpu_list = new String[cpu_likes.size()];
                    global_price_base_limit = "WHERE [En Düşük Fiyat] > 500 AND [index] = ";
                }


                gpu_list = new String[gpu_likes.size()];
                for(int i=0;i<gpu_likes.size();i++){
                    gpu_list[i] = String.valueOf(gpu_likes.get(i));
                }

                String gpu_query = "";
                if (gpu_list != null){
                    for(int i=0;i<gpu_list.length;i++){
                        if(i == 0){
                            gpu_query = gpu_query.concat(gpu_list[i]);
                        } else {
                            gpu_query = gpu_query.concat(" OR [index] = " + gpu_list[i]);
                        }
                    }
                }

                global_price_base_limit += gpu_query;

                GPU_ItemList.clear();
                try {
                    gpu_cursor = database.rawQuery(gpu_search_base + " " + global_price_base_limit,null);
                } catch (SQLiteException ignored){}

                if(gpu_cursor != null && gpu_list != null) {
                    GPU_ItemList = getGPU_ItemList(gpu_cursor);
                    gpu_recyclerView = view.findViewById(R.id.gpuRecyclerView);
                    GPUAdapter = new GPUItemRecyclerViewAdapter(GPU_ItemList);
                    gpu_recyclerView.setAdapter(GPUAdapter);
                    gpu_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    gpu_recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
                    global_price_base_limit = "WHERE [En Düşük Fiyat] > 500 AND [index] = ";
                }

                laptop_list = new String[laptop_likes.size()];
                for(int i=0;i<laptop_likes.size();i++){
                    laptop_list[i] = String.valueOf(laptop_likes.get(i));
                }

                String laptop_query = "";
                if (laptop_list != null) {
                    for(int i=0;i<laptop_list.length;i++){
                        if(i == 0){
                            laptop_query = laptop_query.concat(laptop_list[i]);
                        } else {
                            laptop_query = laptop_query.concat(" OR [index] = " + laptop_list[i]);
                        }
                    }
                }

                global_price_base_limit += laptop_query;

                laptop_ItemList.clear();
                try {
                    laptop_cursor = database.rawQuery(laptop_search_base + " " + global_price_base_limit,null);
                } catch (SQLiteException ignored){}

                if(laptop_cursor != null && laptop_list != null) {
                    laptop_ItemList = getLaptop_ItemList(laptop_cursor);
                    laptop_recyclerView = view.findViewById(R.id.laptopRecyclerView);
                    LaptopAdapter = new LaptopItemRecyclerViewAdapter(laptop_ItemList);
                    laptop_recyclerView.setAdapter(LaptopAdapter);
                    laptop_recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    laptop_recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));
                }
            });
        }
        return view;
    }

    public List<CPUItem> getCPU_ItemList(Cursor cursor){
        int index_index = cursor.getColumnIndex("index");
        int processor_index = cursor.getColumnIndex("İşlemci");
        int processorSeries_index = cursor.getColumnIndex("İşlemci Serisi");
        int processorModel_index = cursor.getColumnIndex("İşlemci Modeli");
        int increasedFrequency_index = cursor.getColumnIndex("Arttırılmış Frekans(GHz)");
        int baseFrequency_index = cursor.getColumnIndex("Temel Frekans(GHz)");
        int lowestPrice_index = cursor.getColumnIndex("En Düşük Fiyat");
        int medianPrice_index = cursor.getColumnIndex("Medyan Fiyat");
        int cpu_score_index = cursor.getColumnIndex("İşlemci Skoru");

        cursor.moveToFirst();
        do {
            int index = cursor.getInt(index_index);
            String processor = cursor.getString(processor_index);
            String processorSeries = cursor.getString(processorSeries_index);
            String processorModel = cursor.getString(processorModel_index);
            float increasedFrequency = cursor.getFloat(increasedFrequency_index);
            float baseFrequency = cursor.getFloat(baseFrequency_index);
            Float lowestPrice = cursor.getFloat(lowestPrice_index);
            Float medianPrice = cursor.getFloat(medianPrice_index);
            Float cpu_score = cursor.getFloat(cpu_score_index);

            if(processor != null && processorSeries != null && processorModel != null && increasedFrequency != 0.0f && baseFrequency != 0.0f){
                if(stringContainsItemFromList(processor,intel_i3) || stringContainsItemFromList(processorSeries,intel_i3) || stringContainsItemFromList(processorModel,intel_i3)){
                    processorName = "intel_i3";
                } else if(stringContainsItemFromList(processor,intel_i5) || stringContainsItemFromList(processorSeries,intel_i5) || stringContainsItemFromList(processorModel,intel_i5)) {
                    processorName = "intel_i5";
                } else if(stringContainsItemFromList(processor,intel_i7) || stringContainsItemFromList(processorSeries,intel_i7) || stringContainsItemFromList(processorModel,intel_i7)) {
                    processorName = "intel_i7";
                } else if(stringContainsItemFromList(processor,intel_i9) || stringContainsItemFromList(processorSeries,intel_i9) || stringContainsItemFromList(processorModel,intel_i9)) {
                    processorName = "intel_i9";
                } else if(stringContainsItemFromList(processor,intel_xeon) || stringContainsItemFromList(processorSeries,intel_xeon) || stringContainsItemFromList(processorModel,intel_xeon)) {
                    processorName = "intel_xeon";
                } else if(stringContainsItemFromList(processor,intel) || stringContainsItemFromList(processorSeries,intel) || stringContainsItemFromList(processorModel,intel)) {
                    processorName = "intel";
                }  else if(stringContainsItemFromList(processor,amd_ryzen3) || stringContainsItemFromList(processorSeries,amd_ryzen3) || stringContainsItemFromList(processorModel,amd_ryzen3)) {
                    processorName = "amd_ryzen3";
                } else if(stringContainsItemFromList(processor,amd_ryzen5) || stringContainsItemFromList(processorSeries,amd_ryzen5) || stringContainsItemFromList(processorModel,amd_ryzen5)) {
                    processorName = "amd_ryzen5";
                } else if(stringContainsItemFromList(processor,amd_ryzen7) || stringContainsItemFromList(processorSeries,amd_ryzen7) || stringContainsItemFromList(processorModel,amd_ryzen7)) {
                    processorName = "amd_ryzen7";
                } else if(stringContainsItemFromList(processor,amd_ryzen9) || stringContainsItemFromList(processorSeries,amd_ryzen9) || stringContainsItemFromList(processorModel,amd_ryzen9)) {
                    processorName = "amd_ryzen9";
                } else if(stringContainsItemFromList(processor,amd_ryzen) || stringContainsItemFromList(processorSeries,amd_ryzen) || stringContainsItemFromList(processorModel,amd_ryzen)) {
                    processorName = "amd_ryzen";
                }

                @SuppressLint("DiscouragedApi")
                int processorNameID = getResources().getIdentifier(processorName,"drawable",requireActivity().getPackageName());
                processorPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
                cpu_item = new CPUItem(index,processorModel,increasedFrequency,baseFrequency,lowestPrice,medianPrice,processorPhoto,null,cpu_score,0);
                CPU_ItemList.add(cpu_item);
            }
        } while (cursor.moveToNext());
        return CPU_ItemList;
    }

    public List<GPUItem> getGPU_ItemList(Cursor cursor){
        int index_index = cursor.getColumnIndex("index");
        int vram_index = cursor.getColumnIndex("Bellek Boyutu(GB)");
        int gpu_producer_index = cursor.getColumnIndex("İşlemci Üreticisi");
        int gpu_model_index = cursor.getColumnIndex("Ekran Kartı Modeli");
        int lowestPrice_index = cursor.getColumnIndex("En Düşük Fiyat");
        int medianPrice_index = cursor.getColumnIndex("Medyan Fiyat");
        int gpu_score_index = cursor.getColumnIndex("Ekran Kartı Skoru");
        int gpu_brand_index = cursor.getColumnIndex("Marka");

        cursor.moveToFirst();
        do {
            int index = cursor.getInt(index_index);
            float vram = cursor.getFloat(vram_index);
            String gpu_producer = cursor.getString(gpu_producer_index);
            String gpu_model = cursor.getString(gpu_model_index);
            float gpu_score = cursor.getFloat(gpu_score_index);
            String gpu_brand = cursor.getString(gpu_brand_index);
            Float lowestPrice = cursor.getFloat(lowestPrice_index);
            Float medianPrice = cursor.getFloat(medianPrice_index);

            if(vram != 0.0f && gpu_producer != null && gpu_model != null && gpu_score != 0.0f){
                if (gpu_brand.equals("İZOLY") || gpu_brand.equals("İzoly")){
                    gpu_brand = "izoly";
                }
                @SuppressLint("DiscouragedApi")
                String x = gpu_brand.toLowerCase(Locale.ROOT);
                @SuppressLint("DiscouragedApi") int processorNameID = getResources().getIdentifier(x,"drawable",requireActivity().getPackageName());
                gpuPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
                gpu_item = new GPUItem(index,vram,gpu_model,gpu_score,gpu_brand,lowestPrice,medianPrice,gpuPhoto,null,0);
                GPU_ItemList.add(gpu_item);
            }
        } while (cursor.moveToNext());
        return GPU_ItemList;
    }

    public List<LaptopItem> getLaptop_ItemList(Cursor cursor){
        int index_index = cursor.getColumnIndex("index");
        int screen_size_index = cursor.getColumnIndex("Ekran Boyutu(inç)");
        int ram_index = cursor.getColumnIndex("Ram(Bellek) Boyutu(GB)");
        int cpu_model_index = cursor.getColumnIndex("İşlemci Modeli");
        int cpu_turbo_speed_index = cursor.getColumnIndex("İşlemci Arttırılmış Frekans(GHz)");
        int laptop_name_index = cursor.getColumnIndex("Ürün Adı");
        int gpu_model_index = cursor.getColumnIndex("Harici Ekran Kartı Modeli");
        int gpu_model_value_index = cursor.getColumnIndex("Harici Ekran Kartı Belleği(GB)");
        int lowestPrice_index = cursor.getColumnIndex("En Düşük Fiyat");
        int medianPrice_index = cursor.getColumnIndex("Medyan Fiyat");
        int brand_index = cursor.getColumnIndex("Marka");

        cursor.moveToFirst();
        do {
            int index = cursor.getInt(index_index);
            Float screen_size = cursor.getFloat(screen_size_index);
            Float ram = cursor.getFloat(ram_index);
            String cpu_model = cursor.getString(cpu_model_index);
            Float cpu_turbo_speed = cursor.getFloat(cpu_turbo_speed_index);
            String laptop_name = cursor.getString(laptop_name_index);
            String gpu_model = cursor.getString(gpu_model_index);
            Float gpu_model_value = cursor.getFloat(gpu_model_value_index);
            Float lowestPrice = cursor.getFloat(lowestPrice_index);
            Float medianPrice = cursor.getFloat(medianPrice_index);
            String brand = cursor.getString(brand_index);

            String x = brand.toLowerCase(Locale.ROOT);
            switch (x) {
                case "20yq000stx042":
                    x = "lenovo";
                    break;
                case "i-life":
                    x = "i_life";
                    break;
                case "inç":
                    x = "inc";
                    break;
            }
            @SuppressLint("DiscouragedApi") int processorNameID = getResources().getIdentifier(x,"drawable",requireActivity().getPackageName());
            try {
                laptopPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
            } catch (Resources.NotFoundException e){
                e.printStackTrace();
            }
            laptop_item = new LaptopItem(index,laptop_name,screen_size,ram,cpu_model,cpu_turbo_speed,lowestPrice,medianPrice,gpu_model,gpu_model_value,laptopPhoto,null,0);
            laptop_ItemList.add(laptop_item);

        } while (cursor.moveToNext());
        return laptop_ItemList;
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }
}