package com.example.thesisproject;
import android.annotation.SuppressLint;
import android.content.res.Resources;
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

public class LaptopRecommenderFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    Drawable laptopPhoto;
    File f;
    SQLiteDatabase database;
    Cursor cursor;
    List<LaptopItem> laptop_ItemList = new ArrayList<>();
    LaptopItem laptop_item;
    RecyclerView recyclerView;
    LaptopItemRecyclerViewAdapter LaptopAdapter;
    SeekBar cpu_speed_seekbar, cpu_highest_speed_seekbar, average_price_seekbar, cpu_score_seekbar, ram_seekbar, screen_size_seekbar, gpu_score_seekbar, weight_seekbar;
    Button recommend_button;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laptoprecommender, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(4).setChecked(true);

        cpu_speed_seekbar = view.findViewById(R.id.cpu_speed_seekbar);
        cpu_highest_speed_seekbar = view.findViewById(R.id.cpu_highest_speed_seekbar);
        average_price_seekbar = view.findViewById(R.id.average_price_seekbar);
        cpu_score_seekbar = view.findViewById(R.id.cpu_score_seekbar);
        ram_seekbar = view.findViewById(R.id.ram_seekbar);
        screen_size_seekbar = view.findViewById(R.id.screen_size_seekbar);
        gpu_score_seekbar = view.findViewById(R.id.gpu_score_seekbar);
        weight_seekbar = view.findViewById(R.id.weight_seekbar);
        recommend_button = view.findViewById(R.id.recommend_button);
        recyclerView = view.findViewById(R.id.laptopRecyclerView);

        LaptopAdapter = new LaptopItemRecyclerViewAdapter(laptop_ItemList);
        recyclerView.setAdapter(LaptopAdapter);
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
            int cpu_speed_value =  cpu_speed_seekbar.getProgress();
            int cpu_highest_speed_value = cpu_highest_speed_seekbar.getProgress();
            int average_price_value = -1 * average_price_seekbar.getProgress();
            int cpu_score_value = cpu_score_seekbar.getProgress();
            int ram_value = ram_seekbar.getProgress();
            int screen_size_value = screen_size_seekbar.getProgress();
            int gpu_score_value = gpu_score_seekbar.getProgress();
            int weight_value = weight_seekbar.getProgress();

            String select = "SELECT NOTEBOOKS.[index], NOTEBOOKS.[Ekran Boyutu(inç)], NOTEBOOKS.[Ram(Bellek) Boyutu(GB)], NOTEBOOKS.[İşlemci Modeli], NOTEBOOKS.[İşlemci Markası]," +
                    "NOTEBOOKS.[İşlemci Arttırılmış Frekans(GHz)], NOTEBOOKS.[Ürün Adı], NOTEBOOKS.[En Düşük Fiyat], NOTEBOOKS.[Medyan Fiyat]," +
                    "NOTEBOOKS.[Harici Ekran Kartı Belleği(GB)], NOTEBOOKS.[Harici Ekran Kartı Modeli], NOTEBOOKS.[Marka],";

            String scaled = cpu_speed_value + "*ScaledNOTEBOOKS.[İşlemci Temel Frekans(GHz)] +" + cpu_highest_speed_value + "*ScaledNOTEBOOKS.[İşlemci Arttırılmış Frekans(GHz)] +" +
                            average_price_value + "*ScaledNOTEBOOKS.[Medyan Fiyat] +" + cpu_score_value + "*ScaledNOTEBOOKS.[İşlemci Skoru] +" + ram_value + "*ScaledNOTEBOOKS.[Ram(Bellek) Boyutu(GB)] +" +
                            screen_size_value + "*ScaledNOTEBOOKS.[Ekran Boyutu(inç)] +" + gpu_score_value + "*ScaledNOTEBOOKS.[Ekran Kartı Skoru] +" + weight_value + "*ScaledNOTEBOOKS.[Ağırlık(kg)]" +
                            " AS SCORE";

            cursor = database.rawQuery(select + scaled + " " +
                    "FROM NOTEBOOKS, ScaledNOTEBOOKS" + " " +
                    "WHERE NOTEBOOKS.[index] = ScaledNOTEBOOKS.[index] AND NOTEBOOKS.[En Düşük Fiyat] > 500 AND NOTEBOOKS.[Harici Ekran Kartı Modeli] IS NOT 'NVIDIA A40' ORDER BY SCORE DESC", null);

            laptop_ItemList.clear();
            laptop_ItemList = getLaptop_ItemList(cursor);
            LaptopAdapter.notifyDataSetChanged();
        });


        return view;
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

        int a = 0;
        if (cursor.moveToFirst()){
            do {
                a++;
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

            } while (a < 10 && cursor.moveToNext());
        }
        return laptop_ItemList;
    }
}