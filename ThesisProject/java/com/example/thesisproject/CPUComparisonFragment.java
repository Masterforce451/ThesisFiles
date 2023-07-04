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
import java.util.Arrays;
import java.util.List;

public class CPUComparisonFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    File f;
    SQLiteDatabase database;
    DocumentReference documentReference;
    String processorName;
    Drawable processorPhoto;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    String user_id;
    Cursor cursor_item1, cursor_item2, cursor_similar;
    CPUItemRecyclerViewAdapter adapter;
    List<CPUItem> CPU_ItemList = new ArrayList<>();
    RecyclerView recyclerView;
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

    @SuppressWarnings("SuspiciousMethodCalls")
    @SuppressLint({"SetTextI18n","DiscouragedApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cpucomparison, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String item1_id = sharedPreferences.getString("cpu_item1_id","");
        String item2_id = sharedPreferences.getString("cpu_item2_id","");

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(f,null);

        String search_base = "SELECT [index], [İşlemci], [İşlemci Serisi], [İşlemci Modeli], [Jenerasyon], [Arttırılmış Frekans(GHz)], [Çekirdek], [Temel Frekans(GHz)]," +
                "[uri], [Ürün Adı], [En Düşük Fiyat], [Medyan Fiyat], [Cosine Similar], [FirstNearestNeighbor], [SecondNearestNeighbor], [İşlemci Skoru]" +
                " FROM CPUS";

        cursor_item1 = database.rawQuery(search_base + " " +"WHERE [index] = ?",new String[]{item1_id});

        int index_index = cursor_item1.getColumnIndex("index");
        int processor_index = cursor_item1.getColumnIndex("İşlemci");
        int processorSeries_index = cursor_item1.getColumnIndex("İşlemci Serisi");
        int processorModel_index = cursor_item1.getColumnIndex("İşlemci Modeli");
        int generation_index = cursor_item1.getColumnIndex("Jenerasyon");
        int increasedFrequency_index = cursor_item1.getColumnIndex("Arttırılmış Frekans(GHz)");
        int core_index = cursor_item1.getColumnIndex("Çekirdek");
        int baseFrequency_index = cursor_item1.getColumnIndex("Temel Frekans(GHz)");
        int uri_index = cursor_item1.getColumnIndex("uri");
        int productName_index = cursor_item1.getColumnIndex("Ürün Adı");
        int lowestPrice_index = cursor_item1.getColumnIndex("En Düşük Fiyat");
        int medianPrice_index = cursor_item1.getColumnIndex("Medyan Fiyat");
        int cosineSimilar_index = cursor_item1.getColumnIndex("Cosine Similar");
        int firstNearestNeighbour_index = cursor_item1.getColumnIndex("FirstNearestNeighbor");
        int secondNearestNeighbour_index = cursor_item1.getColumnIndex("SecondNearestNeighbor");
        int cpu_score_index = cursor_item1.getColumnIndex("İşlemci Skoru");

        cursor_item1.moveToFirst();
        int index = cursor_item1.getInt(index_index);
        String processor = cursor_item1.getString(processor_index);
        String processorSeries = cursor_item1.getString(processorSeries_index);
        String processorModel = cursor_item1.getString(processorModel_index);
        String generation = cursor_item1.getString(generation_index);
        float increasedFrequency = cursor_item1.getFloat(increasedFrequency_index);
        float core = cursor_item1.getFloat(core_index);
        float baseFrequency = cursor_item1.getFloat(baseFrequency_index);
        String uri = cursor_item1.getString(uri_index);
        String productName = cursor_item1.getString(productName_index);
        float lowestPrice = cursor_item1.getFloat(lowestPrice_index);
        float medianPrice = cursor_item1.getFloat(medianPrice_index);
        String cosineSimilar_primaryKey = String.valueOf(cursor_item1.getInt(cosineSimilar_index));
        String firstNearestNeighbour_primaryKey = String.valueOf(cursor_item1.getInt(firstNearestNeighbour_index));
        String secondNearestNeighbour_primaryKey = String.valueOf(cursor_item1.getInt(secondNearestNeighbour_index));
        Float cpu_score = cursor_item1.getFloat(cpu_score_index);

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
        }

        ImageView cpu_photo = view.findViewById(R.id.cpu_photo);
        cpu_photo.setImageDrawable(processorPhoto);
        TextView cpu_lowest_price_value = view.findViewById(R.id.cpu_price_lowest_value);
        cpu_lowest_price_value.setText((int) lowestPrice + " TL");
        TextView cpu_average_price_value = view.findViewById(R.id.cpu_average_price_value);
        cpu_average_price_value.setText((int) medianPrice + " TL");
        TextView cpu_name = view.findViewById(R.id.cpu_name);
        cpu_name.setText(productName);
        TextView cpu_page_link = view.findViewById(R.id.cpu_page_link);
        cpu_page_link.setText(uri);
        TextView cpu_model_value = view.findViewById(R.id.cpu_model_value);
        cpu_model_value.setText(processorModel);
        TextView cpu_generation_value = view.findViewById(R.id.cpu_generation_value);
        cpu_generation_value.setText(generation);
        TextView cpu_speed_value = view.findViewById(R.id.cpu_speed_value);
        cpu_speed_value.setText(baseFrequency + " GHz");
        TextView cpu_turbo_speed_value = view.findViewById(R.id.cpu_turbo_speed_value);
        cpu_turbo_speed_value.setText(increasedFrequency + " GHz");
        TextView cpu_core_value = view.findViewById(R.id.cpu_core_value);
        cpu_core_value.setText(String.valueOf((int) core));
        TextView cpu_score_value = view.findViewById(R.id.cpu_score_value);
        cpu_score_value.setText(String.valueOf(cpu_score));
        Button favourite_button = view.findViewById(R.id.favourite_button);


        int finalIndex = index;
        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("cpu-likes");
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
                    documentReference.update("cpu-likes", FieldValue.arrayRemove(finalIndex));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.cpu_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("cpu-likes", FieldValue.arrayUnion(finalIndex));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.cpu_fav_added), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
            }
        });

        cursor_item2 = database.rawQuery(search_base + " " + "WHERE [index] = ?",new String[]{item2_id});

        index_index = cursor_item2.getColumnIndex("index");
        processor_index = cursor_item2.getColumnIndex("İşlemci");
        processorSeries_index = cursor_item2.getColumnIndex("İşlemci Serisi");
        processorModel_index = cursor_item2.getColumnIndex("İşlemci Modeli");
        generation_index = cursor_item2.getColumnIndex("Jenerasyon");
        increasedFrequency_index = cursor_item2.getColumnIndex("Arttırılmış Frekans(GHz)");
        core_index = cursor_item2.getColumnIndex("Çekirdek");
        baseFrequency_index = cursor_item2.getColumnIndex("Temel Frekans(GHz)");
        uri_index = cursor_item2.getColumnIndex("uri");
        productName_index = cursor_item2.getColumnIndex("Ürün Adı");
        lowestPrice_index = cursor_item2.getColumnIndex("En Düşük Fiyat");
        medianPrice_index = cursor_item2.getColumnIndex("Medyan Fiyat");
        cosineSimilar_index = cursor_item2.getColumnIndex("Cosine Similar");
        firstNearestNeighbour_index = cursor_item2.getColumnIndex("FirstNearestNeighbor");
        secondNearestNeighbour_index = cursor_item2.getColumnIndex("SecondNearestNeighbor");
        cpu_score_index = cursor_item2.getColumnIndex("İşlemci Skoru");

        cursor_item2.moveToFirst();
        index = cursor_item2.getInt(index_index);
        processor = cursor_item2.getString(processor_index);
        processorSeries = cursor_item2.getString(processorSeries_index);
        processorModel = cursor_item2.getString(processorModel_index);
        generation = cursor_item2.getString(generation_index);
        increasedFrequency = cursor_item2.getFloat(increasedFrequency_index);
        core = cursor_item2.getFloat(core_index);
        baseFrequency = cursor_item2.getFloat(baseFrequency_index);
        uri = cursor_item2.getString(uri_index);
        productName = cursor_item2.getString(productName_index);
        lowestPrice = cursor_item2.getFloat(lowestPrice_index);
        medianPrice = cursor_item2.getFloat(medianPrice_index);
        String cosineSimilar_primaryKey2 = String.valueOf(cursor_item2.getInt(cosineSimilar_index));
        String firstNearestNeighbour_primaryKey2 = String.valueOf(cursor_item2.getInt(firstNearestNeighbour_index));
        String secondNearestNeighbour_primaryKey2 = String.valueOf(cursor_item2.getInt(secondNearestNeighbour_index));
        cpu_score = cursor_item2.getFloat(cpu_score_index);

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
        }


        ImageView cpu_photo2 = view.findViewById(R.id.cpu_photo2);
        cpu_photo2.setImageDrawable(processorPhoto);

        TextView cpu_lowest_price_value2 = view.findViewById(R.id.cpu_price_lowest_value2);
        cpu_lowest_price_value2.setText((int) lowestPrice + " TL");

        TextView cpu_average_price_value2 = view.findViewById(R.id.cpu_average_price_value2);
        cpu_average_price_value2.setText((int) medianPrice + " TL");

        TextView cpu_name2 = view.findViewById(R.id.cpu_name2);
        cpu_name2.setText(productName);

        TextView cpu_page_link2 = view.findViewById(R.id.cpu_page_link2);
        cpu_page_link2.setText(uri);

        TextView cpu_model_value2 = view.findViewById(R.id.cpu_model_value2);
        cpu_model_value2.setText(processorModel);

        TextView cpu_generation_value2 = view.findViewById(R.id.cpu_generation_value2);
        cpu_generation_value2.setText(generation);

        TextView cpu_speed_value2 = view.findViewById(R.id.cpu_speed_value2);
        cpu_speed_value2.setText(baseFrequency + " GHz");

        TextView cpu_turbo_speed_value2 = view.findViewById(R.id.cpu_turbo_speed_value2);
        cpu_turbo_speed_value2.setText(increasedFrequency + " GHz");

        TextView cpu_core_value2 = view.findViewById(R.id.cpu_core_value2);
        cpu_core_value2.setText(String.valueOf((int) core));

        TextView cpu_score_value2 = view.findViewById(R.id.cpu_score_value2);
        cpu_score_value2.setText(String.valueOf(cpu_score));

        Button favourite_button2 = view.findViewById(R.id.favourite_button2);

        int finalIndex2 = index;
        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("cpu-likes");
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
                    documentReference.update("cpu-likes", FieldValue.arrayRemove(finalIndex2));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.cpu_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("cpu-likes", FieldValue.arrayUnion(finalIndex2));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.cpu_fav_added), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
            }
        });

        String similar_search_base = "SELECT [index], [İşlemci], [İşlemci Serisi], [İşlemci Modeli], [Arttırılmış Frekans(GHz)], " +
                "[Temel Frekans(GHz)], [En Düşük Fiyat], [Medyan Fiyat], [uri] , [İşlemci Skoru] FROM CPUS";


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

        CPU_ItemList.clear();
        CPU_ItemList = getCPU_ItemList(cursor_similar);
        recyclerView = view.findViewById(R.id.cpu_page_similar_items);
        adapter = new CPUItemRecyclerViewAdapter(CPU_ItemList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL));

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
        int uri_index = cursor.getColumnIndex("uri");
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
            String uri = cursor.getString(uri_index);
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
                cpu_item = new CPUItem(index,processorModel,increasedFrequency,baseFrequency,lowestPrice,medianPrice,processorPhoto,uri,cpu_score,1);
                CPU_ItemList.add(cpu_item);
            }
        } while (cursor.moveToNext());
        return CPU_ItemList;
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }
}