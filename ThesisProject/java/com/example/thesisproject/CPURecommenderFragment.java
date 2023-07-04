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
import java.util.Arrays;
import java.util.List;

public class CPURecommenderFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    SeekBar cpu_speed_seekbar, cpu_highest_speed_seekbar, average_price_seekbar, cpu_score_seekbar;
    Button recommend_button;
    File f;
    SQLiteDatabase database;
    RecyclerView recyclerView;
    CPUItemRecyclerViewAdapter CPUAdapter;
    Cursor cursor;
    List<CPUItem> CPU_ItemList = new ArrayList<>();
    CPUItem cpu_item;
    String processorName;
    Drawable processorPhoto;

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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cpurecommender, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(4).setChecked(true);

        cpu_speed_seekbar = view.findViewById(R.id.cpu_speed_seekbar);
        cpu_highest_speed_seekbar = view.findViewById(R.id.cpu_highest_speed_seekbar);
        average_price_seekbar = view.findViewById(R.id.average_price_seekbar);
        cpu_score_seekbar = view.findViewById(R.id.cpu_score_seekbar);
        recommend_button = view.findViewById(R.id.recommend_button);
        recyclerView = view.findViewById(R.id.cpuRecyclerView);

        CPUAdapter = new CPUItemRecyclerViewAdapter(CPU_ItemList);
        recyclerView.setAdapter(CPUAdapter);
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

            String select = "SELECT CPUS.[index], CPUS.[İşlemci], CPUS.[İşlemci Serisi], CPUS.[İşlemci Modeli], CPUS.[Arttırılmış Frekans(GHz)], CPUS.[Temel Frekans(GHz)], CPUS.[En Düşük Fiyat], CPUS.[Medyan Fiyat], CPUS.[İşlemci Skoru],";
            String scaled = cpu_speed_value + "*ScaledCPUS.[Temel Frekans(GHz)] +" + cpu_highest_speed_value + "*ScaledCPUS.[Arttırılmış Frekans(GHz)] +" + average_price_value + "*ScaledCPUS.[Medyan Fiyat] +" + cpu_score_value + "*ScaledCPUS.[İşlemci Skoru]" + " AS SCORE";

            cursor = database.rawQuery(select + scaled + " " +
                    "FROM CPUS, ScaledCPUS" + " " +
                    "WHERE CPUS.[index] = ScaledCPUS.[index] AND CPUS.[En Düşük Fiyat] > 500 ORDER BY SCORE DESC", null);

            CPU_ItemList.clear();
            CPU_ItemList = getCPU_ItemList(cursor);
            CPUAdapter.notifyDataSetChanged();
        });

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

        int a = 0;
        if (cursor.moveToFirst()) {
            do {
                a++;
                int index = cursor.getInt(index_index);
                String processor = cursor.getString(processor_index);
                String processorSeries = cursor.getString(processorSeries_index);
                String processorModel = cursor.getString(processorModel_index);
                float increasedFrequency = cursor.getFloat(increasedFrequency_index);
                float baseFrequency = cursor.getFloat(baseFrequency_index);
                Float lowestPrice = cursor.getFloat(lowestPrice_index);
                Float medianPrice = cursor.getFloat(medianPrice_index);
                Float cpu_score = cursor.getFloat(cpu_score_index);

                if(processor != null && processorSeries != null && processorModel != null && (increasedFrequency != 0.0f || baseFrequency != 0.0f)){
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
            } while (a<10 && cursor.moveToNext());
        }
        return CPU_ItemList;
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }
}