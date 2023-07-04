package com.example.thesisproject;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GPUItemFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    File f;
    SQLiteDatabase database;
    Cursor cursor;
    List<GPUItem> GPU_ItemList = new ArrayList<>();
    GPUItem gpu_item;
    RecyclerView recyclerView;
    GPUItemRecyclerViewAdapter GPUAdapter;
    String primary_key;
    Drawable gpuPhoto;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    String user_id;
    DocumentReference documentReference;
    SharedPreferences sharedPreferences;
    String x;

    @SuppressWarnings("SuspiciousMethodCalls")
    @SuppressLint({"SetTextI18n", "DiscouragedApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gpu_item, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            primary_key = String.valueOf(bundle.getInt("primary_key"));
        }

        String search_base = "SELECT [index], [Bellek Boyutu(GB)], [İşlemci Üreticisi], [Ekran Kartı Modeli], [Ekran Kartı Skoru]," +
                "[En Düşük Fiyat], [uri], [Medyan Fiyat], [Marka], [Ürün Adı], [Cosine Similar], [FirstNearestNeighbor], [SecondNearestNeighbor] FROM GPUS WHERE [index] = ?";

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        database = SQLiteDatabase.openOrCreateDatabase(f, null);

        cursor = database.rawQuery(search_base, new String[]{primary_key});

        int index_index = cursor.getColumnIndex("index");
        int vram_index = cursor.getColumnIndex("Bellek Boyutu(GB)");
        int gpu_producer_index = cursor.getColumnIndex("İşlemci Üreticisi");
        int gpu_model_index = cursor.getColumnIndex("Ekran Kartı Modeli");
        int lowestPrice_index = cursor.getColumnIndex("En Düşük Fiyat");
        int medianPrice_index = cursor.getColumnIndex("Medyan Fiyat");
        int gpu_score_index = cursor.getColumnIndex("Ekran Kartı Skoru");
        int gpu_brand_index = cursor.getColumnIndex("Marka");
        int uri_index = cursor.getColumnIndex("uri");
        int product_name_index = cursor.getColumnIndex("Ürün Adı");
        int cosineSimilar_index = cursor.getColumnIndex("Cosine Similar");
        int firstNearestNeighbour_index = cursor.getColumnIndex("FirstNearestNeighbor");
        int secondNearestNeighbour_index = cursor.getColumnIndex("SecondNearestNeighbor");

        cursor.moveToFirst();
        int index = cursor.getInt(index_index);
        float vram = cursor.getFloat(vram_index);
        String gpu_producer = cursor.getString(gpu_producer_index);
        String gpu_model = cursor.getString(gpu_model_index);
        float gpu_score = cursor.getFloat(gpu_score_index);
        String gpu_brand = cursor.getString(gpu_brand_index);
        float lowestPrice = cursor.getFloat(lowestPrice_index);
        float medianPrice = cursor.getFloat(medianPrice_index);
        String uri = cursor.getString(uri_index);
        String product_name = cursor.getString(product_name_index);
        String cosineSimilar_primaryKey = String.valueOf(cursor.getInt(cosineSimilar_index));
        String firstNearestNeighbour_primaryKey = String.valueOf(cursor.getInt(firstNearestNeighbour_index));
        String secondNearestNeighbour_primaryKey = String.valueOf(cursor.getInt(secondNearestNeighbour_index));

        if (vram != 0.0f && gpu_producer != null && gpu_model != null && gpu_score != 0.0f) {
            if (gpu_brand.equals("İZOLY") || gpu_brand.equals("İzoly")) {
                gpu_brand = "izoly";
            }
            x = gpu_brand.toLowerCase(Locale.ROOT);
            int processorNameID = getResources().getIdentifier(x, "drawable", requireActivity().getPackageName());
            gpuPhoto = ResourcesCompat.getDrawable(getResources(), processorNameID, requireContext().getTheme());
        }

        ImageView gpu_photo = view.findViewById(R.id.gpu_photo);
        gpu_photo.setImageDrawable(gpuPhoto);
        TextView gpu_lowest_price_value = view.findViewById(R.id.gpu_price_lowest_value);
        gpu_lowest_price_value.setText((int) lowestPrice + " TL");
        TextView gpu_average_price_value = view.findViewById(R.id.gpu_average_price_value);
        gpu_average_price_value.setText((int) medianPrice + " TL");
        TextView gpu_name = view.findViewById(R.id.gpu_name);
        gpu_name.setText(product_name);
        TextView gpu_model_name = view.findViewById(R.id.cpu_model_value);
        gpu_model_name.setText(gpu_producer + " " + gpu_model);
        TextView cpu_page_link = view.findViewById(R.id.laptop_page_link);
        cpu_page_link.setText(uri);
        TextView cpu_score_value = view.findViewById(R.id.gpu_score_value);
        cpu_score_value.setText(String.valueOf(gpu_score));
        TextView vram_value = view.findViewById(R.id.vram_value);
        vram_value.setText((int) vram + " GB");
        Button favourite_button = view.findViewById(R.id.favourite_button);

        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("gpu-likes");
                Long primary_key = (long) index;
                assert group != null;
                if(group.contains(primary_key)){
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                }
            });
        }

        favourite_button.setOnClickListener(v -> {
            if(mAuth.getCurrentUser() != null){
                user_id = mAuth.getCurrentUser().getEmail();
                documentReference = user_database.collection("users").document(user_id);
                if(favourite_button.getBackgroundTintList() == ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on))) {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.colorPrimary)));
                    documentReference.update("gpu-likes", FieldValue.arrayRemove(index));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.gpu_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("gpu-likes", FieldValue.arrayUnion(index));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.gpu_fav_added), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
            }
        });

        Button compare_button = view.findViewById(R.id.compare_button);
        compare_button.setOnClickListener(v -> {
            BottomSheetDialog comparison_dialog = new BottomSheetDialog(requireContext());
            comparison_dialog.setContentView(R.layout.item_comparison_dialog);
            Button item1_delete_button = comparison_dialog.findViewById(R.id.item1_delete_button);
            Button item2_delete_button = comparison_dialog.findViewById(R.id.item2_delete_button);
            ImageView item1_photo = comparison_dialog.findViewById(R.id.comparison_item1_photo);
            ImageView item2_photo = comparison_dialog.findViewById(R.id.comparison_item2_photo);
            TextView item1_title = comparison_dialog.findViewById(R.id.comparison_item1_title);
            TextView item2_title = comparison_dialog.findViewById(R.id.comparison_item2_title);
            Button item_compare_button = comparison_dialog.findViewById(R.id.comparison_button);

            assert item1_photo != null;
            assert item1_title != null;
            assert item2_photo != null;
            assert item2_title != null;
            assert item1_delete_button != null;
            assert item2_delete_button != null;

            item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
            item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));

            if(!sharedPreferences.getBoolean("gpu_item1",false) && !sharedPreferences.getBoolean("gpu_item2",false)) {
                sharedPreferences.edit().putBoolean("gpu_item1",true).apply();
                sharedPreferences.edit().putString("gpu_item1_id", String.valueOf(index)).apply();
                sharedPreferences.edit().putString("gpu_item1_name",product_name).apply();
                sharedPreferences.edit().putString("gpu_item1_photo",x).apply();
                item1_title.setText(product_name);
                item1_photo.setImageDrawable(gpu_photo.getDrawable());
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
            }
            else if(sharedPreferences.getBoolean("gpu_item1",false) && !sharedPreferences.getBoolean("gpu_item2",false)) {
                if(!sharedPreferences.getString("gpu_item1_name","").equals(product_name)){
                    sharedPreferences.edit().putBoolean("gpu_item2",true).apply();
                    sharedPreferences.edit().putString("gpu_item2_id", String.valueOf(index)).apply();
                    sharedPreferences.edit().putString("gpu_item2_name",product_name).apply();
                    sharedPreferences.edit().putString("gpu_item2_photo",x).apply();
                    item2_title.setText(product_name);
                    item2_photo.setImageDrawable(gpu_photo.getDrawable());
                    item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                } else {
                    Toast.makeText(requireContext(),getString(R.string.comparison_already_in),Toast.LENGTH_SHORT).show();
                }

                item1_title.setText(sharedPreferences.getString("gpu_item1_name",""));
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                int processorNameID = getResources().getIdentifier(sharedPreferences.getString("gpu_item1_photo",""),"drawable",requireActivity().getPackageName());
                gpuPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
                item1_photo.setImageDrawable(gpuPhoto);
            }
            else if(sharedPreferences.getBoolean("gpu_item1",false) && sharedPreferences.getBoolean("gpu_item2",false)){
                item1_title.setText(sharedPreferences.getString("gpu_item1_name",""));
                int item1_id = getResources().getIdentifier(sharedPreferences.getString("gpu_item1_photo",""),"drawable",requireActivity().getPackageName());
                gpuPhoto = ResourcesCompat.getDrawable(getResources(),item1_id,requireContext().getTheme());
                item1_photo.setImageDrawable(gpuPhoto);

                item2_title.setText(sharedPreferences.getString("gpu_item2_name",""));
                int item2_id = getResources().getIdentifier(sharedPreferences.getString("gpu_item2_photo",""),"drawable",requireActivity().getPackageName());
                gpuPhoto = ResourcesCompat.getDrawable(getResources(),item2_id,requireContext().getTheme());
                item2_photo.setImageDrawable(gpuPhoto);

                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                Toast.makeText(requireContext(),getString(R.string.comparison_full),Toast.LENGTH_SHORT).show();
            }

            assert item_compare_button != null;
            item_compare_button.setOnClickListener(v1 -> {
                if(sharedPreferences.getBoolean("gpu_item1",false) && sharedPreferences.getBoolean("gpu_item2",false)){
                    comparison_dialog.dismiss();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GPUComparisonFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                }
                else {
                    Toast.makeText(requireContext(),getString(R.string.comparison_not_enough),Toast.LENGTH_SHORT).show();
                }
            });

            item1_delete_button.setOnClickListener(v1 -> {
                if(sharedPreferences.getBoolean("gpu_item2",false)){
                    item1_title.setText(item1_title.getText());
                    int item2_id = getResources().getIdentifier(sharedPreferences.getString("gpu_item1_photo",""),"drawable",requireActivity().getPackageName());
                    gpuPhoto = ResourcesCompat.getDrawable(getResources(),item2_id,requireContext().getTheme());
                    item1_photo.setImageDrawable(gpuPhoto);
                    item2_title.setText("");
                    item2_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    sharedPreferences.edit().putString("gpu_item1_name",sharedPreferences.getString("gpu_item2_name","")).apply();
                    sharedPreferences.edit().putString("gpu_item1_photo", sharedPreferences.getString("gpu_item2_photo","")).apply();
                    sharedPreferences.edit().putBoolean("gpu_item2",false).apply();
                } else {
                    item1_title.setText("");
                    item1_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    sharedPreferences.edit().putBoolean("gpu_item1",false).apply();
                }
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                comparison_dialog.dismiss();
                Toast.makeText(requireContext(),getString(R.string.comparison_item_deleted),Toast.LENGTH_SHORT).show();
            });

            item2_delete_button.setOnClickListener(v2 -> {
                if(sharedPreferences.getBoolean("gpu_item2",false)){
                    item2_title.setText("");
                    item2_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                    sharedPreferences.edit().putBoolean("gpu_item2",false).apply();
                }
                comparison_dialog.dismiss();
                Toast.makeText(requireContext(),getString(R.string.comparison_item_deleted),Toast.LENGTH_SHORT).show();
            });

            comparison_dialog.show();
        });




        String similar_search_base = "SELECT [index], [Bellek Boyutu(GB)], [İşlemci Üreticisi], [Ekran Kartı Modeli], [Ekran Kartı Skoru]," +
                "[En Düşük Fiyat], [Medyan Fiyat], [Marka], [uri] FROM GPUS";


        cursor = database.rawQuery(similar_search_base + " WHERE [index] = ? OR [index] = ? OR [index] = ?", new String[]{cosineSimilar_primaryKey, firstNearestNeighbour_primaryKey, secondNearestNeighbour_primaryKey});
        cursor.moveToFirst();

        GPU_ItemList.clear();
        GPU_ItemList = getGPU_ItemList(cursor);
        recyclerView = view.findViewById(R.id.gpu_page_similar_items);
        GPUAdapter = new GPUItemRecyclerViewAdapter(GPU_ItemList);
        recyclerView.setAdapter(GPUAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        return view;
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
        int uri_index = cursor.getColumnIndex("uri");

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
            String uri = cursor.getString(uri_index);

            if(vram != 0.0f && gpu_producer != null && gpu_model != null && gpu_score != 0.0f){
                if (gpu_brand.equals("İZOLY") || gpu_brand.equals("İzoly")){
                    gpu_brand = "izoly";
                }
                @SuppressLint("DiscouragedApi")
                String x = gpu_brand.toLowerCase(Locale.ROOT);
                @SuppressLint("DiscouragedApi") int processorNameID = getResources().getIdentifier(x,"drawable",requireActivity().getPackageName());
                gpuPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
                gpu_item = new GPUItem(index,vram,gpu_model,gpu_score,gpu_brand,lowestPrice,medianPrice,gpuPhoto,uri,1);
                GPU_ItemList.add(gpu_item);
            }
        } while (cursor.moveToNext());
        return GPU_ItemList;
    }
}