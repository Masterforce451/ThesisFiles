package com.example.thesisproject;
import static android.content.Context.MODE_PRIVATE;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CPUFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    File f;
    SQLiteDatabase database;
    Cursor cursor;
    List<CPUItem> CPU_ItemList = new ArrayList<>();
    CPUItem cpu_item;
    RecyclerView recyclerView;
    CPUItemRecyclerViewAdapter CPUAdapter;
    Button sort_button, filter_button, clear_sort_button, clear_filter_button;
    SearchView cpu_search_view;
    TextView lowest_value_filter, highest_value_filter;
    String lowest_value_string, highest_value_string;
    String processorName;
    Drawable processorPhoto;
    SharedPreferences sharedPreferences;

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
        View view = inflater.inflate(R.layout.fragment_cpu, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(f,null);


        String search_base = "SELECT [index], [İşlemci], [İşlemci Serisi], [İşlemci Modeli], [Arttırılmış Frekans(GHz)], " +
                "[Temel Frekans(GHz)], [En Düşük Fiyat], [Medyan Fiyat], [İşlemci Skoru] FROM CPUS";

        String global_price_base_limit = "WHERE [En Düşük Fiyat] > 500";

        String sort_highest_price = "ORDER BY [En Düşük Fiyat] DESC";
        String sort_lowest_price = "ORDER BY [En Düşük Fiyat] ASC";
        String sort_cpu_normal = "ORDER BY [Temel Frekans(GHz)] DESC";
        String sort_cpu_high = "ORDER BY [Arttırılmış Frekans(GHz)] DESC";
        String sort_cpu_highest = "ORDER BY [İşlemci Skoru] DESC";
        String sort_cpu_lowest = "ORDER BY [İşlemci Skoru] ASC";

        String filter_intel = "AND [İşlemci Modeli] LIKE 'Intel%'";
        String filter_amd = "AND [İşlemci Modeli] LIKE 'AMD%'";

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sort_highest_price_cpu",false);
        editor.putBoolean("sort_lowest_price_cpu",false);
        editor.putBoolean("sort_cpu_normal_cpu",false);
        editor.putBoolean("sort_cpu_high_cpu",false);
        editor.putBoolean("sort_cpu_highest_cpu",false);
        editor.putBoolean("sort_cpu_lowest_cpu",false);

        editor.putBoolean("filter_intel_cpu",false);
        editor.putBoolean("filter_amd_cpu",false);

        editor.apply();

        cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
        CPU_ItemList = getCPU_ItemList(cursor);
        recyclerView = view.findViewById(R.id.cpuRecyclerView);
        CPUAdapter = new CPUItemRecyclerViewAdapter(CPU_ItemList);
        recyclerView.setAdapter(CPUAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));

        sort_button = view.findViewById(R.id.sort_button);
        clear_sort_button = view.findViewById(R.id.clear_sort_button);
        filter_button = view.findViewById(R.id.filter_button);
        clear_filter_button = view.findViewById(R.id.clear_filter_button);

        sort_button.setOnClickListener(v -> {
            PopupMenu sort_menu = new PopupMenu(requireContext(),sort_button);
            sort_menu.getMenuInflater().inflate(R.menu.cpu_sort_menu, sort_menu.getMenu());
            sort_menu.setOnMenuItemClickListener(item -> {
                CharSequence title = item.getTitle();
                if(title.equals(getString(R.string.price_highest))) {
                    editor.putBoolean("sort_highest_price_cpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_intel_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_highest_price,null);
                    }
                    else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_highest_price,null);
                    }

                } else if(title.equals(getString(R.string.price_lowest))) {
                    editor.putBoolean("sort_lowest_price_cpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_intel_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " +sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " +sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_lowest_price,null);
                    }
                } else if(title.equals(getString(R.string.cpu_speed_highest))) {
                    editor.putBoolean("sort_cpu_normal_cpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_intel_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + sort_cpu_normal,null);
                    } else if (sharedPreferences.getBoolean("filter_amd_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + sort_cpu_normal,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_cpu_normal,null);
                    }

                } else if(title.equals(getString(R.string.cpu_turbo_speed_highest))) {
                    editor.putBoolean("sort_cpu_high_cpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_intel_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " + sort_cpu_high,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_cpu_high,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_cpu_high,null);
                    }

                } else if(title.equals(getString(R.string.cpu_score_sort_highest_text))) {
                    editor.putBoolean("sort_cpu_highest_cpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_intel_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " + sort_cpu_highest,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_cpu_highest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_cpu_highest,null);
                    }
                } else if(title.equals(getString(R.string.cpu_score_sort_lowest_text))) {
                    editor.putBoolean("sort_cpu_lowest_cpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_intel_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " + sort_cpu_lowest,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_cpu_lowest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_cpu_lowest,null);
                    }
                }
                CPU_ItemList.clear();
                CPU_ItemList = getCPU_ItemList(cursor);
                CPUAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));
                return true;
            });
            sort_menu.show();
        });

        filter_button.setOnClickListener(v -> {
            PopupMenu filter_menu = new PopupMenu(requireContext(),filter_button);
            filter_menu.getMenuInflater().inflate(R.menu.cpu_filter_menu, filter_menu.getMenu());
            filter_menu.setOnMenuItemClickListener(item -> {
                CharSequence title = item.getTitle();
                if(title.equals(getString(R.string.intel_cpus))){
                    sharedPreferences.edit().putBoolean("filter_intel_cpu",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " +sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_cpu_normal_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + sort_cpu_normal,null);
                    } else if(sharedPreferences.getBoolean("sort_cpu_high_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " + sort_cpu_high,null);
                    } else if(sharedPreferences.getBoolean("sort_cpu_highest_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " + sort_cpu_highest,null);
                    } else if(sharedPreferences.getBoolean("sort_cpu_lowest_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel + " " + sort_cpu_lowest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel,null);
                    }


                } else if(title.equals(getString(R.string.amd_cpus))){
                    sharedPreferences.edit().putBoolean("filter_amd_cpu",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " +sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_cpu_normal_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + sort_cpu_normal,null);
                    } else if(sharedPreferences.getBoolean("sort_cpu_high_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_cpu_high,null);
                    } else if(sharedPreferences.getBoolean("sort_cpu_highest_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_cpu_highest,null);
                    } else if(sharedPreferences.getBoolean("sort_cpu_lowest_cpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_cpu_lowest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd,null);
                    }


                } else if(title.equals(getString(R.string.price_range_selection))){
                    lowest_value_filter = view.findViewById(R.id.lowest_value_filter);
                    highest_value_filter = view.findViewById(R.id.highest_value_filter);

                    final EditText lowest_value_edit = new EditText(requireContext());
                    final EditText highest_value_edit = new EditText(requireContext());
                    AlertDialog.Builder range_selection_window =new AlertDialog.Builder( requireContext() );

                    lowest_value_edit.setHint(getString(R.string.price_lowest));
                    lowest_value_edit.setGravity(Gravity.CENTER);
                    lowest_value_edit.setInputType(InputType.TYPE_CLASS_NUMBER);
                    highest_value_edit.setHint(getString(R.string.price_highest));
                    highest_value_edit.setGravity(Gravity.CENTER);
                    highest_value_edit.setInputType(InputType.TYPE_CLASS_NUMBER);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins( 20,20,20,20);
                    LinearLayout PriceSelectionLayout=new LinearLayout(requireContext());
                    PriceSelectionLayout.setOrientation( LinearLayout.VERTICAL );

                    PriceSelectionLayout.addView( lowest_value_edit ,layoutParams);
                    PriceSelectionLayout.addView( highest_value_edit,layoutParams );
                    range_selection_window.setView(PriceSelectionLayout);

                    range_selection_window.setPositiveButton(getString(R.string.show), (dialog, which) -> {
                        lowest_value_string = lowest_value_edit.getText().toString().trim();
                        highest_value_string = highest_value_edit.getText().toString().trim();

                        if(Objects.equals(lowest_value_string, "")) {lowest_value_string = String.valueOf(500);}

                        cursor = database.rawQuery(search_base + " WHERE [En Düşük Fiyat] BETWEEN ? AND ? ORDER BY [En Düşük Fiyat] ASC " ,new String[] {lowest_value_string,highest_value_string});

                        CPU_ItemList.clear();
                        CPU_ItemList = getCPU_ItemList(cursor);
                        CPUAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(0);
                        dialog.dismiss();
                        clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));
                    });
                    range_selection_window.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
                    AlertDialog alert = range_selection_window.create();
                    alert.setCanceledOnTouchOutside(true);
                    alert.show();
                }
                CPU_ItemList.clear();
                CPU_ItemList = getCPU_ItemList(cursor);
                CPUAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));
                return true;
            });
            filter_menu.show();
        });

        clear_sort_button.setOnClickListener(v -> {
            editor.putBoolean("sort_highest_price_cpu",false);
            editor.putBoolean("sort_lowest_price_cpu",false);
            editor.putBoolean("sort_cpu_normal_cpu",false);
            editor.putBoolean("sort_cpu_high_cpu",false);
            editor.putBoolean("sort_cpu_highest_cpu",false);
            editor.putBoolean("sort_cpu_lowest_cpu",false);
            editor.apply();
            if (sharedPreferences.getBoolean("filter_amd_cpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd,null);
            } else if (sharedPreferences.getBoolean("filter_intel_cpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel,null);
            } else {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
            }
            CPU_ItemList.clear();
            CPU_ItemList = getCPU_ItemList(cursor);
            CPUAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
        });

        clear_filter_button.setOnClickListener(v -> {
            editor.putBoolean("filter_intel_cpu",false);
            editor.putBoolean("filter_amd_cpu",false);
            editor.apply();
            if(sharedPreferences.getBoolean("sort_highest_price_cpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_highest_price,null);
            } else if(sharedPreferences.getBoolean("sort_lowest_price_cpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_lowest_price,null);
            } else if(sharedPreferences.getBoolean("sort_cpu_normal_cpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_cpu_normal,null);
            } else if(sharedPreferences.getBoolean("sort_cpu_high_cpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_cpu_high,null);
            } else if(sharedPreferences.getBoolean("sort_cpu_highest_cpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_cpu_highest,null);
            } else if(sharedPreferences.getBoolean("sort_cpu_lowest_cpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_cpu_lowest,null);
            } else {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
            }
            CPU_ItemList.clear();
            CPU_ItemList = getCPU_ItemList(cursor);
            CPUAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
        });

        cpu_search_view = view.findViewById(R.id.cpu_search_view);
        cpu_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                if (query != null) {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + "AND [Ürün Adı] LIKE ?", new String[]{"%"+query+"%"});
                } else {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
                }

                CPU_ItemList.clear();
                CPU_ItemList = getCPU_ItemList(cursor);
                CPUAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                cpu_search_view.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                if (newText != null) {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + "AND [Ürün Adı] LIKE ?", new String[]{"%"+newText+"%"});
                } else {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
                }

                CPU_ItemList.clear();
                CPU_ItemList = getCPU_ItemList(cursor);
                CPUAdapter.notifyDataSetChanged();
                return true;
            }
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

        if (cursor.moveToFirst()) {
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
            } while (cursor.moveToNext());
        }
        return CPU_ItemList;
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }
}