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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GPUFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    File f;
    SQLiteDatabase database;
    Cursor cursor;
    List<GPUItem> GPU_ItemList = new ArrayList<>();
    GPUItem gpu_item;
    RecyclerView recyclerView;
    GPUItemRecyclerViewAdapter GPUAdapter;
    Button sort_button, filter_button, clear_sort_button, clear_filter_button;
    SearchView gpu_search_view;
    TextView lowest_value_filter, highest_value_filter;
    String lowest_value_string, highest_value_string;
    SharedPreferences sharedPreferences;
    Drawable gpuPhoto;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gpu, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(f,null);

        String search_base = "SELECT [index], [Bellek Boyutu(GB)], [Ekran Kartı Modeli], [Ekran Kartı Skoru]," +
                "[En Düşük Fiyat], [Medyan Fiyat], [Marka] FROM GPUS";

        String global_price_base_limit = "WHERE [En Düşük Fiyat] > 500";

        String sort_highest_price = "ORDER BY [En Düşük Fiyat] DESC";
        String sort_lowest_price = "ORDER BY [En Düşük Fiyat] ASC";
        String sort_vram_highest = "ORDER BY [Bellek Boyutu(GB)] DESC";
        String sort_vram_lowest = "ORDER BY [Bellek Boyutu(GB)] ASC";
        String sort_gpu_score_highest = "ORDER BY [Ekran Kartı Skoru] DESC";
        String sort_gpu_score_lowest = "ORDER BY [Ekran Kartı Skoru] ASC";

        String filter_nvidia = "AND [İşlemci Üreticisi] = 'NVIDIA'";
        String filter_amd = "AND [İşlemci Üreticisi] = 'AMD'";

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("sort_highest_price_gpu",false);
        editor.putBoolean("sort_lowest_price_gpu",false);
        editor.putBoolean("sort_highest_vram_gpu",false);
        editor.putBoolean("sort_lowest_vram_gpu",false);
        editor.putBoolean("sort_highest_gpu_score_gpu",false);
        editor.putBoolean("sort_lowest_gpu_score_gpu",false);

        editor.putBoolean("filter_nvidia_gpu",false);
        editor.putBoolean("filter_amd_gpu",false);

        editor.apply();


        cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);

        GPU_ItemList = getGPU_ItemList(cursor);
        recyclerView = view.findViewById(R.id.gpuRecyclerView);
        GPUAdapter = new GPUItemRecyclerViewAdapter(GPU_ItemList);
        recyclerView.setAdapter(GPUAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));

        sort_button = view.findViewById(R.id.sort_button);
        clear_sort_button = view.findViewById(R.id.clear_sort_button);
        filter_button = view.findViewById(R.id.filter_button);
        clear_filter_button = view.findViewById(R.id.clear_filter_button);

        sort_button.setOnClickListener(v -> {
            PopupMenu sort_menu = new PopupMenu(requireContext(),sort_button);
            sort_menu.getMenuInflater().inflate(R.menu.gpu_sort_menu, sort_menu.getMenu());
            sort_menu.setOnMenuItemClickListener(item -> {
                CharSequence title = item.getTitle();
                if(title.equals(getString(R.string.price_highest))) {
                    editor.putBoolean("sort_highest_price_gpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_nvidia_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_gpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_highest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_highest_price,null);
                    }

                } else if(title.equals(getString(R.string.price_lowest))) {
                    editor.putBoolean("sort_lowest_price_gpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_nvidia_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_gpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_lowest_price,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_lowest_price,null);
                    }

                } else if(title.equals(getString(R.string.vram_highest))) {
                    editor.putBoolean("sort_highest_vram_gpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_nvidia_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_vram_highest,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_gpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_vram_highest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_vram_highest,null);
                    }

                } else if(title.equals(getString(R.string.vram_lowest))) {
                    editor.putBoolean("sort_lowest_vram_gpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_nvidia_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_vram_lowest,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_gpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_vram_lowest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_vram_lowest,null);
                    }

                } else if(title.equals(getString(R.string.gpu_score_highest))) {
                    editor.putBoolean("sort_highest_gpu_score_gpu",true).apply();
                    if(sharedPreferences.getBoolean("filter_nvidia_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_gpu_score_highest,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_gpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_gpu_score_highest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_gpu_score_highest,null);
                    }

                } else if(title.equals(getString(R.string.gpu_score_lowest))) {
                    editor.putBoolean("sort_lowest_vram_gpu",false).apply();
                    if(sharedPreferences.getBoolean("filter_nvidia_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_gpu_score_lowest,null);
                    } else if(sharedPreferences.getBoolean("filter_amd_gpu",false)) {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_gpu_score_lowest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_gpu_score_lowest,null);
                    }
                }
                GPU_ItemList.clear();
                GPU_ItemList = getGPU_ItemList(cursor);
                GPUAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));
                return true;
            });
            sort_menu.show();
        });

        filter_button.setOnClickListener(v -> {
            PopupMenu filter_menu = new PopupMenu(requireContext(),filter_button);
            filter_menu.getMenuInflater().inflate(R.menu.gpu_filter_menu, filter_menu.getMenu());
            filter_menu.setOnMenuItemClickListener(item -> {
                CharSequence title = item.getTitle();
                if(title.equals(getString(R.string.nvidia))){
                    editor.putBoolean("filter_nvidia_gpu",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_highest_vram_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_vram_highest,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_vram_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_vram_lowest,null);
                    } else if(sharedPreferences.getBoolean("sort_highest_gpu_score_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_gpu_score_highest,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_gpu_score_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia + " " + sort_gpu_score_lowest,null);
                    } else {
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia,null);
                    }


                } else if(title.equals(getString(R.string.amd_gpus))){
                    editor.putBoolean("filter_amd_gpu",true).apply();
                    if(sharedPreferences.getBoolean("sort_highest_price_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_highest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_price_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_lowest_price,null);
                    } else if(sharedPreferences.getBoolean("sort_highest_vram_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_vram_highest,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_vram_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_vram_lowest,null);
                    } else if(sharedPreferences.getBoolean("sort_highest_gpu_score_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_gpu_score_highest,null);
                    } else if(sharedPreferences.getBoolean("sort_lowest_gpu_score_gpu",false)){
                        cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd + " " + sort_gpu_score_lowest,null);
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

                        GPU_ItemList.clear();
                        GPU_ItemList = getGPU_ItemList(cursor);
                        GPUAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(0);
                        dialog.dismiss();
                        clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));
                    });
                    range_selection_window.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
                    AlertDialog alert = range_selection_window.create();
                    alert.setCanceledOnTouchOutside(true);
                    alert.show();
                }
                GPU_ItemList.clear();
                GPU_ItemList = getGPU_ItemList(cursor);
                GPUAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_applied)));
                return true;
            });
            filter_menu.show();
        });

        clear_sort_button.setOnClickListener(v -> {
            editor.putBoolean("sort_highest_price_gpu",false);
            editor.putBoolean("sort_lowest_price_gpu",false);
            editor.putBoolean("sort_highest_vram_gpu",false);
            editor.putBoolean("sort_lowest_vram_gpu",false);
            editor.putBoolean("sort_highest_gpu_score_gpu",false);
            editor.putBoolean("sort_lowest_gpu_score_gpu",false);
            editor.apply();
            if(sharedPreferences.getBoolean("filter_nvidia_gpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_nvidia,null);
            } else if(sharedPreferences.getBoolean("filter_amd_gpu",false)) {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + filter_amd,null);
            } else {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
            }
            GPU_ItemList.clear();
            GPU_ItemList = getGPU_ItemList(cursor);
            GPUAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
        });

        clear_filter_button.setOnClickListener(v -> {
            editor.putBoolean("filter_nvidia_gpu",false).apply();
            editor.putBoolean("filter_amd_gpu",false).apply();
            if(sharedPreferences.getBoolean("sort_highest_price_gpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_highest_price,null);
            } else if(sharedPreferences.getBoolean("sort_lowest_price_gpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_lowest_price,null);
            } else if(sharedPreferences.getBoolean("sort_highest_vram_gpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_vram_highest,null);
            } else if(sharedPreferences.getBoolean("sort_lowest_vram_gpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_vram_lowest,null);
            } else if(sharedPreferences.getBoolean("sort_highest_gpu_score_gpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_gpu_score_highest,null);
            } else if(sharedPreferences.getBoolean("sort_lowest_gpu_score_gpu",false)){
                cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + sort_gpu_score_lowest,null);
            } else {
                cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
            }
            GPU_ItemList.clear();
            GPU_ItemList = getGPU_ItemList(cursor);
            GPUAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
        });

        gpu_search_view = view.findViewById(R.id.gpu_search_view);
        gpu_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                clear_sort_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                clear_filter_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                if (query != null) {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit + " " + "AND [Ürün Adı] LIKE ?", new String[]{"%"+query+"%"});
                } else {
                    cursor = database.rawQuery(search_base + " " + global_price_base_limit,null);
                }

                if( GPU_ItemList != null && !GPU_ItemList.isEmpty() ){
                    GPU_ItemList.clear();
                }
                GPU_ItemList = getGPU_ItemList(cursor);
                GPUAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(0);
                gpu_search_view.clearFocus();
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

                if( GPU_ItemList != null && !GPU_ItemList.isEmpty() ){
                    GPU_ItemList.clear();
                }
                GPU_ItemList = getGPU_ItemList(cursor);
                GPUAdapter.notifyDataSetChanged();
                return true;
            }

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

        if (cursor.moveToFirst()) {
            do {
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
                    String x = gpu_brand.toLowerCase(Locale.ROOT);
                    @SuppressLint("DiscouragedApi") int processorNameID = getResources().getIdentifier(x,"drawable",requireActivity().getPackageName());
                    gpuPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
                    gpu_item = new GPUItem(index,vram,gpu_model,gpu_score,gpu_brand,lowestPrice,medianPrice,gpuPhoto,null,0);
                    GPU_ItemList.add(gpu_item);
                }

            } while (cursor.moveToNext());
        }
        return GPU_ItemList;
    }
}
