package com.example.thesisproject;
import static android.content.Context.MODE_PRIVATE;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LaptopFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    File f;
    SQLiteDatabase database;
    Cursor cursor;
    List<LaptopItem> laptop_ItemList = new ArrayList<>();
    LaptopItem laptop_item;
    RecyclerView recyclerView;
    LaptopItemRecyclerViewAdapter LaptopAdapter;
    Button sort_button, filter_button, clear_sort_button, clear_filter_button;
    SearchView laptop_search_view;
    TextView lowest_value_filter, highest_value_filter;
    String lowest_value_string, highest_value_string;
    Drawable laptopPhoto;
    SharedPreferences sharedPreferences;

    @SuppressWarnings({"All"})
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laptop, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(f,null);

        String search_base = "SELECT [index], [Ekran Boyutu(inç)], [Ram(Bellek) Boyutu(GB)], [İşlemci Modeli], [İşlemci Markası], [İşlemci Arttırılmış Frekans(GHz)], [Ürün Adı], [En Düşük Fiyat], [Medyan Fiyat], " +
                             "[Harici Ekran Kartı Belleği(GB)], [Harici Ekran Kartı Modeli], [Marka] FROM NOTEBOOKS";

        String global_price_base_limit = "WHERE [En Düşük Fiyat] > 500 AND [Harici Ekran Kartı Modeli] IS NOT 'NVIDIA A40'";

        String sort_highest_price = "ORDER BY [En Düşük Fiyat] DESC";
        String sort_lowest_price = "ORDER BY [En Düşük Fiyat] ASC";

        String filter_intel_cpu = "AND [İşlemci Markası] = 'Intel' OR [İşlemci Markası] = 'ıntel' OR [İşlemci Markası] = 'İntel'";
        String filter_amd_cpu = "AND [İşlemci Markası] = 'AMD'";
        String filter_apple_cpu = "AND [İşlemci Markası] = 'Apple'";
        String filter_gtx = "AND [Harici Ekran Kartı Modeli] LIKE 'GeForce GTX%'";
        String filter_rtx20 = "AND [Harici Ekran Kartı Modeli] LIKE 'GeForce RTX 20%'";
        String filter_rtx30 = "AND [Harici Ekran Kartı Modeli] LIKE 'GeForce RTX 30%'";

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sort_highest_price",false);
        editor.putBoolean("sort_lowest_price",false);

        editor.putBoolean("filter_intel_cpu",false);
        editor.putBoolean("filter_amd_cpu",false);
        editor.putBoolean("filter_apple_cpu",false);
        editor.putBoolean("filter_gtx",false);
        editor.putBoolean("filter_rtx20",false);
        editor.putBoolean("filter_rtx30",false);

        editor.apply();


        cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
        laptop_ItemList = getLaptop_ItemList(cursor);
        recyclerView = view.findViewById(R.id.laptopRecyclerView);
        LaptopAdapter = new LaptopItemRecyclerViewAdapter(laptop_ItemList);
        recyclerView.setAdapter(LaptopAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));

        sort_button = view.findViewById(R.id.sort_button);
        clear_sort_button = view.findViewById(R.id.clear_sort_button);
        filter_button = view.findViewById(R.id.filter_button);
        clear_filter_button = view.findViewById(R.id.clear_filter_button);

        sort_button.setOnClickListener(v -> {
            PopupMenu sort_menu = new PopupMenu(requireContext(),sort_button);
            sort_menu.getMenuInflater().inflate(R.menu.laptop_sort_menu, sort_menu.getMenu());
            sort_menu.setOnMenuItemClickListener(item -> {
                if(item.getTitle().equals(getString(R.string.price_highest))) {
                    editor.putBoolean("sort_highest_price",true).apply();
                    if(sharedPreferences.getBoolean("filter_intel_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd_cpu + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_apple_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_apple_cpu + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_gtx",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_gtx + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_rtx20",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx20 + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_rtx30",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx30 + " " + sort_highest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + sort_highest_price,null);
                    }

                    laptop_ItemList.clear();
                    laptop_ItemList = getLaptop_ItemList(cursor);
                    LaptopAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));

                } else if(item.getTitle().equals(getString(R.string.price_lowest))) {
                    editor.putBoolean("sort_lowest_price",true).apply();
                    if(sharedPreferences.getBoolean("filter_intel_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu + " " + sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd_cpu + " " + sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_apple_cpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_apple_cpu + " " + sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_gtx",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_gtx + " " + sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_rtx20",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx20 + " " + sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_rtx30",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx30 + " " + sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_lowest_price,null);
                    }
                    laptop_ItemList.clear();
                    laptop_ItemList = getLaptop_ItemList(cursor);
                    LaptopAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));
                }
                return true;
            });
            sort_menu.show();
        });

        filter_button.setOnClickListener(v -> {
            PopupMenu filter_menu = new PopupMenu(requireContext(),filter_button);
            filter_menu.getMenuInflater().inflate(R.menu.laptop_filter_menu, filter_menu.getMenu());
            filter_menu.setOnMenuItemClickListener(item -> {
                CharSequence title = item.getTitle();
                if(title.equals(getString(R.string.intel_cpus))){
                    editor.putBoolean("filter_intel_cpu",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu + " " + sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu,null);
                    }

                    laptop_ItemList.clear();
                    laptop_ItemList = getLaptop_ItemList(cursor);
                    LaptopAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));

                } else if(title.equals(getString(R.string.amd_cpus))){
                    editor.putBoolean("filter_amd_cpu",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd_cpu + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd_cpu + " " + sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd_cpu,null);
                    }

                    laptop_ItemList.clear();
                    laptop_ItemList = getLaptop_ItemList(cursor);
                    LaptopAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));

                } else if(title.equals(getString(R.string.apple_cpus))){
                    editor.putBoolean("filter_apple_cpu",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_apple_cpu + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_apple_cpu + " " + sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_apple_cpu,null);
                    }

                    laptop_ItemList.clear();
                    laptop_ItemList = getLaptop_ItemList(cursor);
                    LaptopAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));

                } else if(title.equals(getString(R.string.nvidia_gtx))){
                    editor.putBoolean("filter_gtx",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_gtx + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_gtx + " " + sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_gtx,null);
                    }

                    laptop_ItemList.clear();
                    laptop_ItemList = getLaptop_ItemList(cursor);
                    LaptopAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));

                } else if(title.equals(getString(R.string.nvidia_rtx2000))){
                    editor.putBoolean("filter_rtx20",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx20 + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx20 + " " + sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx20,null);
                    }

                    laptop_ItemList.clear();
                    laptop_ItemList = getLaptop_ItemList(cursor);
                    LaptopAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));

                } else if(title.equals(getString(R.string.nvidia_rtx3000))){
                    editor.putBoolean("filter_rtx30",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx30 + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx30 + " " + sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx30,null);
                    }

                    laptop_ItemList.clear();
                    laptop_ItemList = getLaptop_ItemList(cursor);
                    LaptopAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                    clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));

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
                    LinearLayout PriceSelectionLayout = new LinearLayout(requireContext());
                    PriceSelectionLayout.setOrientation( LinearLayout.VERTICAL );

                    PriceSelectionLayout.addView( lowest_value_edit ,layoutParams);
                    PriceSelectionLayout.addView( highest_value_edit,layoutParams );
                    range_selection_window.setView(PriceSelectionLayout);

                    range_selection_window.setPositiveButton(getString(R.string.show), (dialog, which) -> {
                        lowest_value_string = lowest_value_edit.getText().toString().trim();
                        highest_value_string = highest_value_edit.getText().toString().trim();

                        if(Objects.equals(lowest_value_string, "")) {lowest_value_string = String.valueOf(500);}

                        cursor = database.rawQuery(search_base + " WHERE [En Düşük Fiyat] BETWEEN ? AND ? ORDER BY [En Düşük Fiyat] ASC " ,new String[] {lowest_value_string,highest_value_string});

                        laptop_ItemList.clear();
                        laptop_ItemList = getLaptop_ItemList(cursor);
                        LaptopAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(0);
                        dialog.dismiss();
                        clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));
                    });
                    range_selection_window.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
                    AlertDialog alert = range_selection_window.create();
                    alert.setCanceledOnTouchOutside(true);
                    alert.show();
                }

                return true;
            });
            filter_menu.show();
        });

        clear_sort_button.setOnClickListener(v -> {
            editor.putBoolean("sort_highest_price",false);
            editor.putBoolean("sort_lowest_price",false);
            editor.apply();
            if(sharedPreferences.getBoolean("filter_intel_cpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu + " " + sort_lowest_price,null);
            } else if(sharedPreferences.getBoolean("filter_amd_cpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd_cpu + " " + sort_lowest_price,null);
            } else if(sharedPreferences.getBoolean("filter_apple_cpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_apple_cpu + " " + sort_lowest_price,null);
            } else if(sharedPreferences.getBoolean("filter_gtx",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_gtx + " " + sort_lowest_price,null);
            } else if(sharedPreferences.getBoolean("filter_rtx20",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx20 + " " + sort_lowest_price,null);
            } else if(sharedPreferences.getBoolean("filter_rtx30",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_rtx30 + " " + sort_lowest_price,null);
            } else {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_lowest_price,null);
            }
            laptop_ItemList.clear();
            laptop_ItemList = getLaptop_ItemList(cursor);
            LaptopAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
        });

        clear_filter_button.setOnClickListener(v -> {
            editor.putBoolean("filter_intel_cpu",false);
            editor.putBoolean("filter_amd_cpu",false);
            editor.putBoolean("filter_apple_cpu",false);
            editor.putBoolean("filter_gtx",false);
            editor.putBoolean("filter_rtx20",false);
            editor.putBoolean("filter_rtx30",false);
            editor.apply();
            if(sharedPreferences.getBoolean("sort_highest_price",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu + " " + sort_highest_price,null);
            } else if(sharedPreferences.getBoolean("sort_lowest_price",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu + " " + sort_lowest_price,null);
            } else {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_intel_cpu,null);
            }
            laptop_ItemList.clear();
            laptop_ItemList = getLaptop_ItemList(cursor);
            LaptopAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
        });

        laptop_search_view = view.findViewById(R.id.laptop_search_view);
        laptop_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                if (query != null) {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit + " AND [Ürün Adı] LIKE ?", new String[]{"%"+query+"%"});
                } else {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
                }

                if( laptop_ItemList != null && !laptop_ItemList.isEmpty() ){
                    laptop_ItemList.clear();
                }
                laptop_ItemList = getLaptop_ItemList(cursor);
                LaptopAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                laptop_search_view.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                if (newText != null) {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit + " AND [Ürün Adı] LIKE ?", new String[]{"%"+newText+"%"});
                } else {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
                }

                if( laptop_ItemList != null && !laptop_ItemList.isEmpty() ){
                    laptop_ItemList.clear();
                }
                laptop_ItemList = getLaptop_ItemList(cursor);
                LaptopAdapter.notifyDataSetChanged();
                return true;
            }
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

        if (cursor.moveToFirst()){
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
        }
        return laptop_ItemList;
    }
}