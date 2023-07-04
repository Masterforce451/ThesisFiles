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
import java.util.Arrays;
import java.util.List;

public class CPUItemFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    File f;
    SQLiteDatabase database;
    Cursor cursor;
    CPUItemRecyclerViewAdapter adapter;
    List<CPUItem> CPU_ItemList = new ArrayList<>();
    CPUItem cpu_item;
    RecyclerView recyclerView;
    String primary_key;
    String processorName;
    Drawable processorPhoto;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    String user_id;
    DocumentReference documentReference;
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

    @SuppressWarnings("SuspiciousMethodCalls")
    @SuppressLint({"SetTextI18n", "DiscouragedApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cpu_item, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            primary_key = String.valueOf(bundle.getInt("primary_key"));
        }

        String search_base = "SELECT [index], [İşlemci], [İşlemci Serisi], [İşlemci Modeli], [Jenerasyon], [Arttırılmış Frekans(GHz)], [Çekirdek], [Temel Frekans(GHz)]," +
                "[uri], [Ürün Adı], [En Düşük Fiyat], [Medyan Fiyat], [Cosine Similar], [FirstNearestNeighbor], [SecondNearestNeighbor], [İşlemci Skoru]" +
                "FROM CPUS WHERE [index] = ?";

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        database = SQLiteDatabase.openOrCreateDatabase(f,null);
        cursor = database.rawQuery(search_base,new String[]{primary_key});

        int index_index = cursor.getColumnIndex("index");
        int processor_index = cursor.getColumnIndex("İşlemci");
        int processorSeries_index = cursor.getColumnIndex("İşlemci Serisi");
        int processorModel_index = cursor.getColumnIndex("İşlemci Modeli");
        int generation_index = cursor.getColumnIndex("Jenerasyon");
        int increasedFrequency_index = cursor.getColumnIndex("Arttırılmış Frekans(GHz)");
        int core_index = cursor.getColumnIndex("Çekirdek");
        int baseFrequency_index = cursor.getColumnIndex("Temel Frekans(GHz)");
        int uri_index = cursor.getColumnIndex("uri");
        int productName_index = cursor.getColumnIndex("Ürün Adı");
        int lowestPrice_index = cursor.getColumnIndex("En Düşük Fiyat");
        int medianPrice_index = cursor.getColumnIndex("Medyan Fiyat");
        int cosineSimilar_index = cursor.getColumnIndex("Cosine Similar");
        int firstNearestNeighbour_index = cursor.getColumnIndex("FirstNearestNeighbor");
        int secondNearestNeighbour_index = cursor.getColumnIndex("SecondNearestNeighbor");
        int cpu_score_index = cursor.getColumnIndex("İşlemci Skoru");

        cursor.moveToFirst();
        int index = cursor.getInt(index_index);
        String processor = cursor.getString(processor_index);
        String processorSeries = cursor.getString(processorSeries_index);
        String processorModel = cursor.getString(processorModel_index);
        String generation = cursor.getString(generation_index);
        float increasedFrequency = cursor.getFloat(increasedFrequency_index);
        float core = cursor.getFloat(core_index);
        float baseFrequency = cursor.getFloat(baseFrequency_index);
        String uri = cursor.getString(uri_index);
        String productName = cursor.getString(productName_index);
        float lowestPrice = cursor.getFloat(lowestPrice_index);
        float medianPrice = cursor.getFloat(medianPrice_index);
        String cosineSimilar_primaryKey = String.valueOf(cursor.getInt(cosineSimilar_index));
        String firstNearestNeighbour_primaryKey = String.valueOf(cursor.getInt(firstNearestNeighbour_index));
        String secondNearestNeighbour_primaryKey = String.valueOf(cursor.getInt(secondNearestNeighbour_index));
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

        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("cpu-likes");
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
                    documentReference.update("cpu-likes", FieldValue.arrayRemove(index));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.cpu_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("cpu-likes", FieldValue.arrayUnion(index));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.cpu_fav_added), Toast.LENGTH_SHORT).show();
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

            if(!sharedPreferences.getBoolean("cpu_item1",false) && !sharedPreferences.getBoolean("cpu_item2",false)) {
                sharedPreferences.edit().putBoolean("cpu_item1",true).apply();
                sharedPreferences.edit().putString("cpu_item1_id", String.valueOf(index)).apply();
                sharedPreferences.edit().putString("cpu_item1_name",productName).apply();
                sharedPreferences.edit().putString("cpu_item1_photo",processorName).apply();
                item1_title.setText(productName);
                item1_photo.setImageDrawable(cpu_photo.getDrawable());
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
            }
            else if(sharedPreferences.getBoolean("cpu_item1",false) && !sharedPreferences.getBoolean("cpu_item2",false)) {
                if(!sharedPreferences.getString("cpu_item1_name","").equals(productName)){
                    sharedPreferences.edit().putBoolean("cpu_item2",true).apply();
                    sharedPreferences.edit().putString("cpu_item2_id", String.valueOf(index)).apply();
                    sharedPreferences.edit().putString("cpu_item2_name",productName).apply();
                    sharedPreferences.edit().putString("cpu_item2_photo",processorName).apply();
                    item2_title.setText(productName);
                    item2_photo.setImageDrawable(cpu_photo.getDrawable());
                    item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                } else {
                    Toast.makeText(requireContext(),getString(R.string.comparison_already_in),Toast.LENGTH_SHORT).show();
                }

                item1_title.setText(sharedPreferences.getString("cpu_item1_name",""));
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                int processorNameID = getResources().getIdentifier(sharedPreferences.getString("cpu_item1_photo",""),"drawable",requireActivity().getPackageName());
                processorPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
                item1_photo.setImageDrawable(processorPhoto);
            }
            else if(sharedPreferences.getBoolean("cpu_item1",false) && sharedPreferences.getBoolean("cpu_item2",false)){
                item1_title.setText(sharedPreferences.getString("cpu_item1_name",""));
                int item1_id = getResources().getIdentifier(sharedPreferences.getString("cpu_item1_photo",""),"drawable",requireActivity().getPackageName());
                processorPhoto = ResourcesCompat.getDrawable(getResources(),item1_id,requireContext().getTheme());
                item1_photo.setImageDrawable(processorPhoto);

                item2_title.setText(sharedPreferences.getString("cpu_item2_name",""));
                int item2_id = getResources().getIdentifier(sharedPreferences.getString("cpu_item2_photo",""),"drawable",requireActivity().getPackageName());
                processorPhoto = ResourcesCompat.getDrawable(getResources(),item2_id,requireContext().getTheme());
                item2_photo.setImageDrawable(processorPhoto);

                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                Toast.makeText(requireContext(),getString(R.string.comparison_full),Toast.LENGTH_SHORT).show();
            }

            assert item_compare_button != null;
            item_compare_button.setOnClickListener(v1 -> {
                if(sharedPreferences.getBoolean("cpu_item1",false) && sharedPreferences.getBoolean("cpu_item2",false)){
                    comparison_dialog.dismiss();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CPUComparisonFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                }
                else {
                    Toast.makeText(requireContext(),getString(R.string.comparison_not_enough),Toast.LENGTH_SHORT).show();
                }
            });

            item1_delete_button.setOnClickListener(v1 -> {
                if(sharedPreferences.getBoolean("cpu_item2",false)){
                    item1_title.setText(item1_title.getText());
                    int item2_id = getResources().getIdentifier(sharedPreferences.getString("cpu_item1_photo",""),"drawable",requireActivity().getPackageName());
                    processorPhoto = ResourcesCompat.getDrawable(getResources(),item2_id,requireContext().getTheme());
                    item1_photo.setImageDrawable(processorPhoto);
                    item2_title.setText("");
                    item2_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    sharedPreferences.edit().putString("cpu_item1_name",sharedPreferences.getString("cpu_item2_name","")).apply();
                    sharedPreferences.edit().putString("cpu_item1_photo", sharedPreferences.getString("cpu_item2_photo","")).apply();
                    sharedPreferences.edit().putBoolean("cpu_item2",false).apply();
                } else {
                    item1_title.setText("");
                    item1_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    sharedPreferences.edit().putBoolean("cpu_item1",false).apply();
                }
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                comparison_dialog.dismiss();
                Toast.makeText(requireContext(),getString(R.string.comparison_item_deleted),Toast.LENGTH_SHORT).show();
            });

            item2_delete_button.setOnClickListener(v2 -> {
                if(sharedPreferences.getBoolean("cpu_item2",false)){
                    item2_title.setText("");
                    item2_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                    sharedPreferences.edit().putBoolean("cpu_item2",false).apply();
                }
                comparison_dialog.dismiss();
                Toast.makeText(requireContext(),getString(R.string.comparison_item_deleted),Toast.LENGTH_SHORT).show();
            });

            comparison_dialog.show();
        });

        String similar_search_base = "SELECT [index], [İşlemci], [İşlemci Serisi], [İşlemci Modeli], [Arttırılmış Frekans(GHz)], " +
                "[Temel Frekans(GHz)], [En Düşük Fiyat], [Medyan Fiyat], [uri] , [İşlemci Skoru] FROM CPUS";


        cursor = database.rawQuery(similar_search_base + " WHERE [index] = ? OR [index] = ? OR [index] = ?",new String[]{cosineSimilar_primaryKey,firstNearestNeighbour_primaryKey,secondNearestNeighbour_primaryKey});
        cursor.moveToFirst();

        CPU_ItemList.clear();
        CPU_ItemList = getCPU_ItemList(cursor);
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