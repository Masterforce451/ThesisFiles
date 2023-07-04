package com.example.thesisproject;
import static android.content.Context.MODE_PRIVATE;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
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

public class LaptopItemFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    File f;
    SQLiteDatabase database;
    Cursor cursor;
    List<LaptopItem> laptop_ItemList = new ArrayList<>();
    LaptopItem laptop_item;
    RecyclerView recyclerView;
    LaptopItemRecyclerViewAdapter LaptopItemAdapter;
    String primary_key;
    Drawable laptopPhoto;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    String user_id;
    DocumentReference documentReference;
    SharedPreferences sharedPreferences;

    @SuppressWarnings("SuspiciousMethodCalls")
    @SuppressLint({"SetTextI18n","DiscouragedApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laptop_item, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            primary_key = String.valueOf(bundle.getInt("primary_key"));
        }

        String search_base =
                "SELECT [index], [Ekran Boyutu(inç)], [İşletim Sistemi], [Ekran Çözünürlüğü], [Ekran Panel Tipi], [Ekran Çözünürlük Biçimi], [Ağırlık(kg)], " +
                "[Ram(Bellek) Boyutu(GB)], [İşlemci Modeli], [İşlemci Arttırılmış Frekans(GHz)], [İşlemci Çekirdek Sayısı], [İşlemci Temel Frekans(GHz)], " +
                "[SSD Boyutu], [uri], [Ürün Adı], [En Düşük Fiyat], [Medyan Fiyat], [Sabit Disk(HDD) Boyutu], [Harici Ekran Kartı Belleği(GB)], " +
                "[Harici Ekran Kartı Modeli], [İşlemci Skoru], [Ekran Kartı Skoru], [Cosine Similar], [FirstNearestNeighbor], [SecondNearestNeighbor], [Marka] FROM NOTEBOOKS";


        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        database = SQLiteDatabase.openOrCreateDatabase(f, null);
        cursor = database.rawQuery(search_base + " WHERE [index] = ?", new String[]{primary_key});

        int index_index = cursor.getColumnIndex("index");
        int screen_size_index = cursor.getColumnIndex("Ekran Boyutu(inç)");
        int operating_system_index = cursor.getColumnIndex("İşletim Sistemi");
        int screen_resolution_index = cursor.getColumnIndex("Ekran Çözünürlüğü");
        int screen_panel_type_index = cursor.getColumnIndex("Ekran Panel Tipi");
        int screen_resolution_type_index = cursor.getColumnIndex("Ekran Çözünürlük Biçimi");
        int weight_index = cursor.getColumnIndex("Ağırlık(kg)");
        int ram_index = cursor.getColumnIndex("Ram(Bellek) Boyutu(GB)");
        int cpu_model_index = cursor.getColumnIndex("İşlemci Modeli");
        int cpu_high_speed_index = cursor.getColumnIndex("İşlemci Arttırılmış Frekans(GHz)");
        int core_amount_index = cursor.getColumnIndex("İşlemci Çekirdek Sayısı");
        int cpu_low_speed_index = cursor.getColumnIndex("İşlemci Temel Frekans(GHz)");
        int ssd_index = cursor.getColumnIndex("SSD Boyutu");
        int uri_index = cursor.getColumnIndex("uri");
        int laptop_name_index = cursor.getColumnIndex("Ürün Adı");
        int lowest_price_index = cursor.getColumnIndex("En Düşük Fiyat");
        int average_price_index = cursor.getColumnIndex("Medyan Fiyat");
        int hdd_index = cursor.getColumnIndex("Sabit Disk(HDD) Boyutu");
        int gpu_value_index = cursor.getColumnIndex("Harici Ekran Kartı Belleği(GB)");
        int gpu_model_index = cursor.getColumnIndex("Harici Ekran Kartı Modeli");
        int cpu_score_index = cursor.getColumnIndex("İşlemci Skoru");
        int gpu_score_index = cursor.getColumnIndex("Ekran Kartı Skoru");
        int cosine_similar_index = cursor.getColumnIndex("Cosine Similar");
        int first_nearest_neighbor_index = cursor.getColumnIndex("FirstNearestNeighbor");
        int second_nearest_neighbor_index = cursor.getColumnIndex("SecondNearestNeighbor");
        int brand_index = cursor.getColumnIndex("Marka");

        cursor.moveToFirst();
        int index = cursor.getInt(index_index);
        float screen_size = cursor.getFloat(screen_size_index);
        String operating_system = cursor.getString(operating_system_index);
        String screen_resolution = cursor.getString(screen_resolution_index);
        String screen_panel_type = cursor.getString(screen_panel_type_index);
        String screen_resolution_type = cursor.getString(screen_resolution_type_index);
        String weight = cursor.getString(weight_index);
        float ram = cursor.getFloat(ram_index);
        String cpu_model = cursor.getString(cpu_model_index);
        float cpu_high_speed = cursor.getFloat(cpu_high_speed_index);
        float core_amount = cursor.getFloat(core_amount_index);
        float cpu_low_speed = cursor.getFloat(cpu_low_speed_index);
        String ssd = cursor.getString(ssd_index);
        String uri = cursor.getString(uri_index);
        String laptop_name = cursor.getString(laptop_name_index);
        float lowest_price = cursor.getFloat(lowest_price_index);
        float average_price = cursor.getFloat(average_price_index);
        String hdd = cursor.getString(hdd_index);
        float gpu_value = cursor.getFloat(gpu_value_index);
        String gpu_model = cursor.getString(gpu_model_index);
        Float cpu_score = cursor.getFloat(cpu_score_index);
        Float gpu_score = cursor.getFloat(gpu_score_index);
        String cosine_similar = String.valueOf(cursor.getInt(cosine_similar_index));
        String first_nearest_neighbor = String.valueOf(cursor.getInt(first_nearest_neighbor_index));
        String second_nearest_neighbor = String.valueOf(cursor.getInt(second_nearest_neighbor_index));
        String brand = cursor.getString(brand_index);

        String x = brand.toLowerCase(Locale.ROOT);
        @SuppressLint("DiscouragedApi") int laptop_photoID = getResources().getIdentifier(x,"drawable",requireActivity().getPackageName());
        try {
            laptopPhoto = ResourcesCompat.getDrawable(getResources(),laptop_photoID,requireContext().getTheme());
        } catch (Resources.NotFoundException e){
            e.printStackTrace();
        }

        ImageView laptop_photo = view.findViewById(R.id.laptop_photo);
        laptop_photo.setImageDrawable(laptopPhoto);

        TextView laptop_name_view = view.findViewById(R.id.laptop_name);
        laptop_name_view.setText(laptop_name);

        TextView laptop_price_lowest_view = view.findViewById(R.id.laptop_price_lowest_value);
        laptop_price_lowest_view.setText((int) lowest_price + " TL");

        TextView laptop_average_price_view = view.findViewById(R.id.laptop_average_price_value);
        laptop_average_price_view.setText((int) average_price + " TL");

        TextView laptop_page_link_view = view.findViewById(R.id.laptop_page_link);
        laptop_page_link_view.setText(uri);

        TextView cpu_model_value_view = view.findViewById(R.id.cpu_model_value);
        if (cpu_model != null) {
            cpu_model_value_view.setText(cpu_model);
        } else {
            cpu_model_value_view.setText(getString(R.string.unknown));
        }

        TextView cpu_score_value_view = view.findViewById(R.id.cpu_score_value);
        if (cpu_score != 0.0f) {
            cpu_score_value_view.setText(String.valueOf(cpu_score));
        } else {
            cpu_score_value_view.setText(getString(R.string.unknown));
        }

        TextView gpu_score_value = view.findViewById(R.id.gpu_score_value);
        if (gpu_score != 0.0f) {
            gpu_score_value.setText(String.valueOf(gpu_score));
        } else {
            gpu_score_value.setText(getString(R.string.unknown));
        }

        TextView ram_value_view = view.findViewById(R.id.ram_value);
        if (ram != 0.0f) {
            ram_value_view.setText((int) ram + " GB");
        }
        else {
            ram_value_view.setText(getString(R.string.unknown));
        }

        TextView screen_size_value_view = view.findViewById(R.id.screen_size_value);
        if (screen_size != 0.0f) {
            screen_size_value_view.setText(screen_size + "\"");
        } else {
            screen_size_value_view.setText(getString(R.string.unknown));
        }

        TextView screen_resolution_value_view = view.findViewById(R.id.screen_resolution_value);
        if (screen_resolution != null) {
            screen_resolution_value_view.setText(screen_resolution);
        } else {
            screen_resolution_value_view.setText(getString(R.string.unknown));
        }

        TextView screen_resolution_type_value_view = view.findViewById(R.id.screen_resolution_type_value);
        if (screen_resolution_type != null) {
            screen_resolution_type_value_view.setText(screen_resolution_type);
        } else {
            screen_resolution_type_value_view.setText(getString(R.string.unknown));
        }

        TextView ssd_value_view = view.findViewById(R.id.ssd_value);
        if (ssd != null) {
            ssd_value_view.setText(ssd);
        } else {
            ssd_value_view.setText(getString(R.string.unknown));
        }

        TextView hdd_value_view = view.findViewById(R.id.hdd_value);
        if (hdd != null) {
            hdd_value_view.setText(ssd);
        } else {
            hdd_value_view.setText(getString(R.string.unknown));
        }

        TextView weight_value_view = view.findViewById(R.id.weight_value);
        if (weight != null) {
            weight_value_view.setText(weight + " kg");
        } else {
            weight_value_view.setText(getString(R.string.unknown));
        }

        TextView operating_system_value_view = view.findViewById(R.id.operating_system_value);
        if (operating_system != null) {
            operating_system_value_view.setText(operating_system);
        } else {
            operating_system_value_view.setText(getString(R.string.unknown));
        }

        TextView cpu_clock_speeds_value_view = view.findViewById(R.id.cpu_clock_speeds_value);
        if (cpu_low_speed != 0.0f || cpu_high_speed != 0.0f) {
            if (cpu_low_speed != 0.0f && cpu_high_speed != 0.0f) {
                cpu_clock_speeds_value_view.setText(cpu_low_speed + " GHz - " + cpu_high_speed + " GHZ");
            }
            if (cpu_low_speed != 0.0f) {
              cpu_clock_speeds_value_view.setText(cpu_low_speed + " GHZ");
            }
            if (cpu_high_speed != 0.0f) {
              cpu_clock_speeds_value_view.setText(cpu_high_speed + " GHz");
          }
        } else {
            cpu_clock_speeds_value_view.setText(getString(R.string.unknown));
        }

        TextView core_amount_value_view = view.findViewById(R.id.core_amount_value);
        if (core_amount != 0.0f) {
            core_amount_value_view.setText(String.valueOf((int) core_amount));
        } else {
            core_amount_value_view.setText(getString(R.string.unknown));
        }

        TextView screen_panel_type_value_view = view.findViewById(R.id.screen_panel_type_value);
        if (screen_panel_type != null) {
            screen_panel_type_value_view.setText(screen_panel_type);
        } else {
            screen_panel_type_value_view.setText(getString(R.string.unknown));
        }

        TextView gpu_model_view = view.findViewById(R.id.gpu_model);
        if (gpu_model != null) {
            gpu_model_view.setText(gpu_model + " " + gpu_value + " GHz");
        } else {
            gpu_model_view.setText(getString(R.string.unknown));
        }

        Button favourite_button = view.findViewById(R.id.favourite_button);

        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("laptop-likes");
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
                    documentReference.update("laptop-likes", FieldValue.arrayRemove(index));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.laptop_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("laptop-likes", FieldValue.arrayUnion(index));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.laptop_fav_added), Toast.LENGTH_SHORT).show();
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

            if(!sharedPreferences.getBoolean("laptop_item1",false) && !sharedPreferences.getBoolean("laptop_item2",false)) {
                sharedPreferences.edit().putBoolean("laptop_item1",true).apply();
                sharedPreferences.edit().putString("laptop_item1_id", String.valueOf(index)).apply();
                sharedPreferences.edit().putString("laptop_item1_name",laptop_name).apply();
                sharedPreferences.edit().putString("laptop_item1_photo",x).apply();
                item1_title.setText(laptop_name);
                item1_photo.setImageDrawable(laptop_photo.getDrawable());
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
            }
            else if(sharedPreferences.getBoolean("laptop_item1",false) && !sharedPreferences.getBoolean("laptop_item2",false)) {
                if(!sharedPreferences.getString("laptop_item1_name","").equals(laptop_name)){
                    sharedPreferences.edit().putBoolean("laptop_item2",true).apply();
                    sharedPreferences.edit().putString("laptop_item2_id", String.valueOf(index)).apply();
                    sharedPreferences.edit().putString("laptop_item2_name",laptop_name).apply();
                    sharedPreferences.edit().putString("laptop_item2_photo",x).apply();
                    item2_title.setText(laptop_name);
                    item2_photo.setImageDrawable(laptop_photo.getDrawable());
                    item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                } else {
                    Toast.makeText(requireContext(),getString(R.string.comparison_already_in),Toast.LENGTH_SHORT).show();
                }

                item1_title.setText(sharedPreferences.getString("laptop_item1_name",""));
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                int processorNameID = getResources().getIdentifier(sharedPreferences.getString("laptop_item1_photo",""),"drawable",requireActivity().getPackageName());
                laptopPhoto = ResourcesCompat.getDrawable(getResources(),processorNameID,requireContext().getTheme());
                item1_photo.setImageDrawable(laptopPhoto);
            }
            else if(sharedPreferences.getBoolean("laptop_item1",false) && sharedPreferences.getBoolean("laptop_item2",false)){
                item1_title.setText(sharedPreferences.getString("laptop_item1_name",""));
                int item1_id = getResources().getIdentifier(sharedPreferences.getString("laptop_item1_photo",""),"drawable",requireActivity().getPackageName());
                laptopPhoto = ResourcesCompat.getDrawable(getResources(),item1_id,requireContext().getTheme());
                item1_photo.setImageDrawable(laptopPhoto);

                item2_title.setText(sharedPreferences.getString("laptop_item2_name",""));
                int item2_id = getResources().getIdentifier(sharedPreferences.getString("laptop_item2_photo",""),"drawable",requireActivity().getPackageName());
                laptopPhoto = ResourcesCompat.getDrawable(getResources(),item2_id,requireContext().getTheme());
                item2_photo.setImageDrawable(laptopPhoto);

                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                Toast.makeText(requireContext(),getString(R.string.comparison_full),Toast.LENGTH_SHORT).show();
            }

            assert item_compare_button != null;
            item_compare_button.setOnClickListener(v1 -> {
                if(sharedPreferences.getBoolean("laptop_item1",false) && sharedPreferences.getBoolean("laptop_item2",false)){
                    comparison_dialog.dismiss();
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LaptopComparisonFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                }
                else {
                    Toast.makeText(requireContext(),getString(R.string.comparison_not_enough),Toast.LENGTH_SHORT).show();
                }
            });

            item1_delete_button.setOnClickListener(v1 -> {
                if(sharedPreferences.getBoolean("laptop_item2",false)){
                    item1_title.setText(item1_title.getText());
                    int item2_id = getResources().getIdentifier(sharedPreferences.getString("laptop_item1_photo",""),"drawable",requireActivity().getPackageName());
                    laptopPhoto = ResourcesCompat.getDrawable(getResources(),item2_id,requireContext().getTheme());
                    item1_photo.setImageDrawable(laptopPhoto);
                    item2_title.setText("");
                    item2_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    sharedPreferences.edit().putString("laptop_item1_name",sharedPreferences.getString("laptop_item2_name","")).apply();
                    sharedPreferences.edit().putString("laptop_item1_photo", sharedPreferences.getString("glaptop_item2_photo","")).apply();
                    sharedPreferences.edit().putBoolean("laptop_item2",false).apply();
                } else {
                    item1_title.setText("");
                    item1_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    sharedPreferences.edit().putBoolean("laptop_item1",false).apply();
                }
                item1_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                comparison_dialog.dismiss();
                Toast.makeText(requireContext(),getString(R.string.comparison_item_deleted),Toast.LENGTH_SHORT).show();
            });

            item2_delete_button.setOnClickListener(v2 -> {
                if(sharedPreferences.getBoolean("laptop_item2",false)){
                    item2_title.setText("");
                    item2_photo.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
                    item2_delete_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                    sharedPreferences.edit().putBoolean("laptop_item2",false).apply();
                }
                comparison_dialog.dismiss();
                Toast.makeText(requireContext(),getString(R.string.comparison_item_deleted),Toast.LENGTH_SHORT).show();
            });

            comparison_dialog.show();
        });

        String similar_search_base = "SELECT [index], [Ekran Boyutu(inç)], [Ram(Bellek) Boyutu(GB)], [İşlemci Modeli], [İşlemci Markası], [İşlemci Arttırılmış Frekans(GHz)], [Ürün Adı], [En Düşük Fiyat], [Medyan Fiyat], " +
                "[Harici Ekran Kartı Belleği(GB)], [Harici Ekran Kartı Modeli], [Marka], [uri] FROM NOTEBOOKS";

        cursor = database.rawQuery(similar_search_base + " WHERE [index] = ? OR [index] = ? OR [index] = ?", new String[]{cosine_similar, first_nearest_neighbor, second_nearest_neighbor});

        laptop_ItemList.clear();
        laptop_ItemList = getLaptop_ItemList(cursor);
        recyclerView = view.findViewById(R.id.laptop_page_similar_items);
        LaptopItemAdapter = new LaptopItemRecyclerViewAdapter(laptop_ItemList);
        recyclerView.setAdapter(LaptopItemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

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
        int uri_index = cursor.getColumnIndex("uri");

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
            String uri = cursor.getString(uri_index);

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
            laptop_item = new LaptopItem(index,laptop_name,screen_size,ram,cpu_model,cpu_turbo_speed,lowestPrice,medianPrice,gpu_model,gpu_model_value,laptopPhoto,uri,1);
            laptop_ItemList.add(laptop_item);

        } while (cursor.moveToNext());
        return laptop_ItemList;
    }
}