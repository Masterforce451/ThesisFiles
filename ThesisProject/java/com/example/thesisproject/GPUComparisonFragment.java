package com.example.thesisproject;
import static android.content.Context.MODE_PRIVATE;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class GPUComparisonFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    File f;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    String user_id;
    Cursor cursor_item1, cursor_item2, cursor_similar;
    List<GPUItem> GPU_ItemList = new ArrayList<>();
    GPUItem gpu_item;
    RecyclerView recyclerView;
    GPUItemRecyclerViewAdapter GPUAdapter;
    Drawable gpuPhoto;
    DocumentReference documentReference;
    SQLiteDatabase database;

    @SuppressWarnings("SuspiciousMethodCalls")
    @SuppressLint({"SetTextI18n","DiscouragedApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gpucomparison, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String item1_id = sharedPreferences.getString("gpu_item1_id","");
        String item2_id = sharedPreferences.getString("gpu_item2_id","");

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(f,null);

        String search_base = "SELECT [index], [Bellek Boyutu(GB)], [İşlemci Üreticisi], [Ekran Kartı Modeli], [Ekran Kartı Skoru]," +
                "[En Düşük Fiyat], [uri], [Medyan Fiyat], [Marka], [Ürün Adı], [Cosine Similar], [FirstNearestNeighbor], [SecondNearestNeighbor] FROM GPUS";

        cursor_item1 = database.rawQuery(search_base + " " + "WHERE [index] = ?",new String[]{item1_id});

        int index_index = cursor_item1.getColumnIndex("index");
        int vram_index = cursor_item1.getColumnIndex("Bellek Boyutu(GB)");
        int gpu_producer_index = cursor_item1.getColumnIndex("İşlemci Üreticisi");
        int gpu_model_index = cursor_item1.getColumnIndex("Ekran Kartı Modeli");
        int lowestPrice_index = cursor_item1.getColumnIndex("En Düşük Fiyat");
        int medianPrice_index = cursor_item1.getColumnIndex("Medyan Fiyat");
        int gpu_score_index = cursor_item1.getColumnIndex("Ekran Kartı Skoru");
        int gpu_brand_index = cursor_item1.getColumnIndex("Marka");
        int uri_index = cursor_item1.getColumnIndex("uri");
        int product_name_index = cursor_item1.getColumnIndex("Ürün Adı");
        int cosineSimilar_index = cursor_item1.getColumnIndex("Cosine Similar");
        int firstNearestNeighbour_index = cursor_item1.getColumnIndex("FirstNearestNeighbor");
        int secondNearestNeighbour_index = cursor_item1.getColumnIndex("SecondNearestNeighbor");

        cursor_item1.moveToFirst();
        int index = cursor_item1.getInt(index_index);
        float vram = cursor_item1.getFloat(vram_index);
        String gpu_producer = cursor_item1.getString(gpu_producer_index);
        String gpu_model = cursor_item1.getString(gpu_model_index);
        float gpu_score = cursor_item1.getFloat(gpu_score_index);
        String gpu_brand = cursor_item1.getString(gpu_brand_index);
        float lowestPrice = cursor_item1.getFloat(lowestPrice_index);
        float medianPrice = cursor_item1.getFloat(medianPrice_index);
        String uri = cursor_item1.getString(uri_index);
        String product_name = cursor_item1.getString(product_name_index);
        String cosineSimilar_primaryKey = String.valueOf(cursor_item1.getInt(cosineSimilar_index));
        String firstNearestNeighbour_primaryKey = String.valueOf(cursor_item1.getInt(firstNearestNeighbour_index));
        String secondNearestNeighbour_primaryKey = String.valueOf(cursor_item1.getInt(secondNearestNeighbour_index));

        if (vram != 0.0f && gpu_producer != null && gpu_model != null && gpu_score != 0.0f) {
            if (gpu_brand.equals("İZOLY") || gpu_brand.equals("İzoly")) {
                gpu_brand = "izoly";
            }

            String x = gpu_brand.toLowerCase(Locale.ROOT);
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
        TextView gpu_model_name = view.findViewById(R.id.gpu_model_value);
        gpu_model_name.setText(gpu_producer + " " + gpu_model);
        TextView gpu_page_link = view.findViewById(R.id.gpu_page_link);
        gpu_page_link.setText(uri);
        TextView gpu_score_value = view.findViewById(R.id.gpu_score_value);
        gpu_score_value.setText(String.valueOf(gpu_score));
        TextView vram_value = view.findViewById(R.id.vram_value);
        vram_value.setText((int) vram + " GB");
        Button favourite_button = view.findViewById(R.id.favourite_button);

        int finalIndex = index;
        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("gpu-likes");
                Long primary_key = (long) finalIndex;
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
                    documentReference.update("gpu-likes", FieldValue.arrayRemove(finalIndex));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.gpu_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("gpu-likes", FieldValue.arrayUnion(finalIndex));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.gpu_fav_added), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
            }
        });

        cursor_item2 = database.rawQuery(search_base + " " + "WHERE [index] = ?",new String[]{item2_id});

        index_index = cursor_item2.getColumnIndex("index");
        vram_index = cursor_item2.getColumnIndex("Bellek Boyutu(GB)");
        gpu_producer_index = cursor_item2.getColumnIndex("İşlemci Üreticisi");
        gpu_model_index = cursor_item2.getColumnIndex("Ekran Kartı Modeli");
        lowestPrice_index = cursor_item2.getColumnIndex("En Düşük Fiyat");
        medianPrice_index = cursor_item2.getColumnIndex("Medyan Fiyat");
        gpu_score_index = cursor_item2.getColumnIndex("Ekran Kartı Skoru");
        gpu_brand_index = cursor_item2.getColumnIndex("Marka");
        uri_index = cursor_item2.getColumnIndex("uri");
        product_name_index = cursor_item2.getColumnIndex("Ürün Adı");
        cosineSimilar_index = cursor_item2.getColumnIndex("Cosine Similar");
        firstNearestNeighbour_index = cursor_item2.getColumnIndex("FirstNearestNeighbor");
        secondNearestNeighbour_index = cursor_item2.getColumnIndex("SecondNearestNeighbor");

        cursor_item2.moveToFirst();
        index = cursor_item2.getInt(index_index);
        vram = cursor_item2.getFloat(vram_index);
        gpu_producer = cursor_item2.getString(gpu_producer_index);
        gpu_model = cursor_item2.getString(gpu_model_index);
        gpu_score = cursor_item2.getFloat(gpu_score_index);
        gpu_brand = cursor_item2.getString(gpu_brand_index);
        lowestPrice = cursor_item2.getFloat(lowestPrice_index);
        medianPrice = cursor_item2.getFloat(medianPrice_index);
        uri = cursor_item2.getString(uri_index);
        product_name = cursor_item2.getString(product_name_index);
        String cosineSimilar_primaryKey2 = String.valueOf(cursor_item2.getInt(cosineSimilar_index));
        String firstNearestNeighbour_primaryKey2 = String.valueOf(cursor_item2.getInt(firstNearestNeighbour_index));
        String secondNearestNeighbour_primaryKey2 = String.valueOf(cursor_item2.getInt(secondNearestNeighbour_index));

        if (vram != 0.0f && gpu_producer != null && gpu_model != null && gpu_score != 0.0f) {
            if (gpu_brand.equals("İZOLY") || gpu_brand.equals("İzoly")) {
                gpu_brand = "izoly";
            }
            @SuppressLint("DiscouragedApi")
            String x = gpu_brand.toLowerCase(Locale.ROOT);
            @SuppressLint("DiscouragedApi") int processorNameID = getResources().getIdentifier(x, "drawable", requireActivity().getPackageName());
            gpuPhoto = ResourcesCompat.getDrawable(getResources(), processorNameID, requireContext().getTheme());
        }

        ImageView gpu_photo2 = view.findViewById(R.id.gpu_photo2);
        gpu_photo2.setImageDrawable(gpuPhoto);
        TextView gpu_lowest_price_value2 = view.findViewById(R.id.gpu_price_lowest_value2);
        gpu_lowest_price_value2.setText((int) lowestPrice + " TL");
        TextView gpu_average_price_value2 = view.findViewById(R.id.gpu_average_price_value2);
        gpu_average_price_value2.setText((int) medianPrice + " TL");
        TextView gpu_name2 = view.findViewById(R.id.gpu_name2);
        gpu_name2.setText(product_name);
        TextView gpu_model_name2 = view.findViewById(R.id.gpu_model_value2);
        gpu_model_name2.setText(gpu_producer + " " + gpu_model);
        TextView gpu_page_link2 = view.findViewById(R.id.gpu_page_link2);
        gpu_page_link2.setText(uri);
        TextView gpu_score_value2 = view.findViewById(R.id.gpu_score_value2);
        gpu_score_value2.setText(String.valueOf(gpu_score));
        TextView vram_value2 = view.findViewById(R.id.vram_value2);
        vram_value2.setText((int) vram + " GB");
        Button favourite_button2 = view.findViewById(R.id.favourite_button2);

        int finalIndex2 = index;
        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("gpu-likes");
                Long primary_key = (long) finalIndex2;
                assert group != null;
                if(group.contains(primary_key)){
                    favourite_button2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                }
            });
        }

        favourite_button2.setOnClickListener(v -> {
            if(mAuth.getCurrentUser() != null){
                user_id = mAuth.getCurrentUser().getEmail();
                documentReference = user_database.collection("users").document(user_id);
                if(favourite_button2.getBackgroundTintList() == ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on))) {
                    favourite_button2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.colorPrimary)));
                    documentReference.update("gpu-likes", FieldValue.arrayRemove(finalIndex2));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.gpu_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("gpu-likes", FieldValue.arrayUnion(finalIndex2));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.gpu_fav_added), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
            }
        });

        String similar_search_base = "SELECT [index], [Bellek Boyutu(GB)], [İşlemci Üreticisi], [Ekran Kartı Modeli], [Ekran Kartı Skoru]," +
                "[En Düşük Fiyat], [Medyan Fiyat], [Marka], [uri] FROM GPUS";

        List<String> string_list = new ArrayList<>();
        string_list.add(cosineSimilar_primaryKey);
        string_list.add(firstNearestNeighbour_primaryKey);
        string_list.add(secondNearestNeighbour_primaryKey);
        if (!string_list.contains(cosineSimilar_primaryKey2)){
            string_list.add(cosineSimilar_primaryKey2);
        }
        if (!string_list.contains(firstNearestNeighbour_primaryKey2)){
            string_list.add(firstNearestNeighbour_primaryKey2);
        }
        if (!string_list.contains(secondNearestNeighbour_primaryKey2)){
            string_list.add(secondNearestNeighbour_primaryKey2);
        }

        StringBuilder arg_size = new StringBuilder("(");
        String[] args = new String[string_list.size()];
        for(int i=0;i<string_list.size();i++){
            if(i == string_list.size()-1) {
                arg_size.append("?");
            } else {
                arg_size.append("?,");
            }
            args[i] = string_list.get(i);
        }
        arg_size.append(")");

        cursor_similar = database.rawQuery(similar_search_base + " WHERE [index] IN "+ arg_size, args);

        GPU_ItemList.clear();
        GPU_ItemList = getGPU_ItemList(cursor_similar);
        recyclerView = view.findViewById(R.id.gpu_page_similar_items);
        GPUAdapter = new GPUItemRecyclerViewAdapter(GPU_ItemList);
        recyclerView.setAdapter(GPUAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));

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