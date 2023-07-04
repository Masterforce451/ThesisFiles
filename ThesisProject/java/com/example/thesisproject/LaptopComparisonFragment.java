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

public class LaptopComparisonFragment extends Fragment {
    BottomNavigationView bottomNavigationView;
    File f;
    DocumentReference documentReference;
    SQLiteDatabase database;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    String user_id;
    List<LaptopItem> laptop_ItemList = new ArrayList<>();
    LaptopItem laptop_item;
    RecyclerView recyclerView;
    LaptopItemRecyclerViewAdapter LaptopItemAdapter;
    Drawable laptopPhoto;
    SharedPreferences sharedPreferences;
    Cursor cursor_item1, cursor_item2, cursor_similar;

    @SuppressWarnings("SuspiciousMethodCalls")
    @SuppressLint({"SetTextI18n","DiscouragedApi"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laptopcomparison, container, false);
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);

        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String item1_id = sharedPreferences.getString("laptop_item1_id","");
        String item2_id = sharedPreferences.getString("laptop_item2_id","");

        try {
            URI uri = new URI("/storage/emulated/0/Android/data/com.example.thesisproject/files/Download/database.db");
            f = new File(uri.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        database = SQLiteDatabase.openOrCreateDatabase(f,null);

        String search_base =
                "SELECT [index], [Ekran Boyutu(inç)], [İşletim Sistemi], [Ekran Çözünürlüğü], [Ekran Panel Tipi], [Ekran Çözünürlük Biçimi], [Ağırlık(kg)], " +
                        "[Ram(Bellek) Boyutu(GB)], [İşlemci Modeli], [İşlemci Arttırılmış Frekans(GHz)], [İşlemci Çekirdek Sayısı], [İşlemci Temel Frekans(GHz)], " +
                        "[SSD Boyutu], [uri], [Ürün Adı], [En Düşük Fiyat], [Medyan Fiyat], [Sabit Disk(HDD) Boyutu], [Harici Ekran Kartı Belleği(GB)], " +
                        "[Harici Ekran Kartı Modeli], [İşlemci Skoru], [Ekran Kartı Skoru], [Cosine Similar], [FirstNearestNeighbor], [SecondNearestNeighbor], [Marka] FROM NOTEBOOKS";

        cursor_item1 = database.rawQuery(search_base + " " + "WHERE [index] = ?",new String[]{item1_id});

        int index_index = cursor_item1.getColumnIndex("index");
        int screen_size_index = cursor_item1.getColumnIndex("Ekran Boyutu(inç)");
        int operating_system_index = cursor_item1.getColumnIndex("İşletim Sistemi");
        int screen_resolution_index = cursor_item1.getColumnIndex("Ekran Çözünürlüğü");
        int screen_panel_type_index = cursor_item1.getColumnIndex("Ekran Panel Tipi");
        int screen_resolution_type_index = cursor_item1.getColumnIndex("Ekran Çözünürlük Biçimi");
        int weight_index = cursor_item1.getColumnIndex("Ağırlık(kg)");
        int ram_index = cursor_item1.getColumnIndex("Ram(Bellek) Boyutu(GB)");
        int cpu_model_index = cursor_item1.getColumnIndex("İşlemci Modeli");
        int cpu_high_speed_index = cursor_item1.getColumnIndex("İşlemci Arttırılmış Frekans(GHz)");
        int core_amount_index = cursor_item1.getColumnIndex("İşlemci Çekirdek Sayısı");
        int cpu_low_speed_index = cursor_item1.getColumnIndex("İşlemci Temel Frekans(GHz)");
        int ssd_index = cursor_item1.getColumnIndex("SSD Boyutu");
        int uri_index = cursor_item1.getColumnIndex("uri");
        int laptop_name_index = cursor_item1.getColumnIndex("Ürün Adı");
        int lowest_price_index = cursor_item1.getColumnIndex("En Düşük Fiyat");
        int average_price_index = cursor_item1.getColumnIndex("Medyan Fiyat");
        int hdd_index = cursor_item1.getColumnIndex("Sabit Disk(HDD) Boyutu");
        int gpu_value_index = cursor_item1.getColumnIndex("Harici Ekran Kartı Belleği(GB)");
        int gpu_model_index = cursor_item1.getColumnIndex("Harici Ekran Kartı Modeli");
        int cpu_score_index = cursor_item1.getColumnIndex("İşlemci Skoru");
        int gpu_score_index = cursor_item1.getColumnIndex("Ekran Kartı Skoru");
        int cosine_similar_index = cursor_item1.getColumnIndex("Cosine Similar");
        int first_nearest_neighbor_index = cursor_item1.getColumnIndex("FirstNearestNeighbor");
        int second_nearest_neighbor_index = cursor_item1.getColumnIndex("SecondNearestNeighbor");
        int brand_index = cursor_item1.getColumnIndex("Marka");

        cursor_item1.moveToFirst();
        int index = cursor_item1.getInt(index_index);
        float screen_size = cursor_item1.getFloat(screen_size_index);
        String operating_system = cursor_item1.getString(operating_system_index);
        String screen_resolution = cursor_item1.getString(screen_resolution_index);
        String screen_panel_type = cursor_item1.getString(screen_panel_type_index);
        String screen_resolution_type = cursor_item1.getString(screen_resolution_type_index);
        String weight = cursor_item1.getString(weight_index);
        float ram = cursor_item1.getFloat(ram_index);
        String cpu_model = cursor_item1.getString(cpu_model_index);
        float cpu_high_speed = cursor_item1.getFloat(cpu_high_speed_index);
        float core_amount = cursor_item1.getFloat(core_amount_index);
        float cpu_low_speed = cursor_item1.getFloat(cpu_low_speed_index);
        String ssd = cursor_item1.getString(ssd_index);
        String uri = cursor_item1.getString(uri_index);
        String laptop_name = cursor_item1.getString(laptop_name_index);
        float lowest_price = cursor_item1.getFloat(lowest_price_index);
        float average_price = cursor_item1.getFloat(average_price_index);
        String hdd = cursor_item1.getString(hdd_index);
        float gpu_value = cursor_item1.getFloat(gpu_value_index);
        String gpu_model = cursor_item1.getString(gpu_model_index);
        Float cpu_score = cursor_item1.getFloat(cpu_score_index);
        Float gpu_score = cursor_item1.getFloat(gpu_score_index);
        String cosineSimilar_primaryKey = String.valueOf(cursor_item1.getInt(cosine_similar_index));
        String firstNearestNeighbour_primaryKey = String.valueOf(cursor_item1.getInt(first_nearest_neighbor_index));
        String secondNearestNeighbour_primaryKey = String.valueOf(cursor_item1.getInt(second_nearest_neighbor_index));
        String brand = cursor_item1.getString(brand_index);

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

        TextView gpu_model_view = view.findViewById(R.id.gpu_model_value);
        if (gpu_model != null) {
            gpu_model_view.setText(gpu_model + " " + gpu_value + " GHz");
        } else {
            gpu_model_view.setText(getString(R.string.unknown));
        }

        Button favourite_button = view.findViewById(R.id.favourite_button);

        int finalIndex = index;
        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("laptop-likes");
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
                    documentReference.update("laptop-likes", FieldValue.arrayRemove(finalIndex));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.laptop_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("laptop-likes", FieldValue.arrayUnion(finalIndex));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.laptop_fav_added), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
            }
        });

        cursor_item2 = database.rawQuery(search_base + " " + "WHERE [index] = ?",new String[]{item2_id});

        index_index = cursor_item2.getColumnIndex("index");
        screen_size_index = cursor_item2.getColumnIndex("Ekran Boyutu(inç)");
        operating_system_index = cursor_item2.getColumnIndex("İşletim Sistemi");
        screen_resolution_index = cursor_item2.getColumnIndex("Ekran Çözünürlüğü");
        screen_panel_type_index = cursor_item2.getColumnIndex("Ekran Panel Tipi");
        screen_resolution_type_index = cursor_item2.getColumnIndex("Ekran Çözünürlük Biçimi");
        weight_index = cursor_item2.getColumnIndex("Ağırlık(kg)");
        ram_index = cursor_item2.getColumnIndex("Ram(Bellek) Boyutu(GB)");
        cpu_model_index = cursor_item2.getColumnIndex("İşlemci Modeli");
        cpu_high_speed_index = cursor_item2.getColumnIndex("İşlemci Arttırılmış Frekans(GHz)");
        core_amount_index = cursor_item2.getColumnIndex("İşlemci Çekirdek Sayısı");
        cpu_low_speed_index = cursor_item2.getColumnIndex("İşlemci Temel Frekans(GHz)");
        ssd_index = cursor_item2.getColumnIndex("SSD Boyutu");
        uri_index = cursor_item2.getColumnIndex("uri");
        laptop_name_index = cursor_item2.getColumnIndex("Ürün Adı");
        lowest_price_index = cursor_item2.getColumnIndex("En Düşük Fiyat");
        average_price_index = cursor_item2.getColumnIndex("Medyan Fiyat");
        hdd_index = cursor_item2.getColumnIndex("Sabit Disk(HDD) Boyutu");
        gpu_value_index = cursor_item2.getColumnIndex("Harici Ekran Kartı Belleği(GB)");
        gpu_model_index = cursor_item2.getColumnIndex("Harici Ekran Kartı Modeli");
        cpu_score_index = cursor_item2.getColumnIndex("İşlemci Skoru");
        gpu_score_index = cursor_item2.getColumnIndex("Ekran Kartı Skoru");
        cosine_similar_index = cursor_item2.getColumnIndex("Cosine Similar");
        first_nearest_neighbor_index = cursor_item2.getColumnIndex("FirstNearestNeighbor");
        second_nearest_neighbor_index = cursor_item2.getColumnIndex("SecondNearestNeighbor");
        brand_index = cursor_item2.getColumnIndex("Marka");

        cursor_item2.moveToFirst();
        index = cursor_item2.getInt(index_index);
        screen_size = cursor_item2.getFloat(screen_size_index);
        operating_system = cursor_item2.getString(operating_system_index);
        screen_resolution = cursor_item2.getString(screen_resolution_index);
        screen_panel_type = cursor_item2.getString(screen_panel_type_index);
        screen_resolution_type = cursor_item2.getString(screen_resolution_type_index);
        weight = cursor_item2.getString(weight_index);
        ram = cursor_item2.getFloat(ram_index);
        cpu_model = cursor_item2.getString(cpu_model_index);
        cpu_high_speed = cursor_item2.getFloat(cpu_high_speed_index);
        core_amount = cursor_item2.getFloat(core_amount_index);
        cpu_low_speed = cursor_item2.getFloat(cpu_low_speed_index);
        ssd = cursor_item2.getString(ssd_index);
        uri = cursor_item2.getString(uri_index);
        laptop_name = cursor_item2.getString(laptop_name_index);
        lowest_price = cursor_item2.getFloat(lowest_price_index);
        average_price = cursor_item2.getFloat(average_price_index);
        hdd = cursor_item2.getString(hdd_index);
        gpu_value = cursor_item2.getFloat(gpu_value_index);
        gpu_model = cursor_item2.getString(gpu_model_index);
        cpu_score = cursor_item2.getFloat(cpu_score_index);
        gpu_score = cursor_item2.getFloat(gpu_score_index);
        String cosineSimilar_primaryKey2 = String.valueOf(cursor_item2.getInt(cosine_similar_index));
        String firstNearestNeighbour_primaryKey2 = String.valueOf(cursor_item2.getInt(first_nearest_neighbor_index));
        String secondNearestNeighbour_primaryKey2 = String.valueOf(cursor_item2.getInt(second_nearest_neighbor_index));
        brand = cursor_item2.getString(brand_index);

        x = brand.toLowerCase(Locale.ROOT);
        laptop_photoID = getResources().getIdentifier(x,"drawable",requireActivity().getPackageName());
        try {
            laptopPhoto = ResourcesCompat.getDrawable(getResources(),laptop_photoID,requireContext().getTheme());
        } catch (Resources.NotFoundException e){
            e.printStackTrace();
        }

        ImageView laptop_photo2 = view.findViewById(R.id.laptop_photo2);
        laptop_photo2.setImageDrawable(laptopPhoto);

        TextView laptop_name_view2 = view.findViewById(R.id.laptop_name2);
        laptop_name_view2.setText(laptop_name);

        TextView laptop_price_lowest_view2 = view.findViewById(R.id.laptop_price_lowest_value2);
        laptop_price_lowest_view2.setText((int) lowest_price + " TL");

        TextView laptop_average_price_view2 = view.findViewById(R.id.laptop_average_price_value2);
        laptop_average_price_view2.setText((int) average_price + " TL");

        TextView laptop_page_link_view2 = view.findViewById(R.id.laptop_page_link2);
        laptop_page_link_view2.setText(uri);

        TextView cpu_model_value_view2 = view.findViewById(R.id.cpu_model_value2);
        if (cpu_model != null) {
            cpu_model_value_view2.setText(cpu_model);
        } else {
            cpu_model_value_view.setText(getString(R.string.unknown));
        }

        TextView cpu_score_value_view2 = view.findViewById(R.id.cpu_score_value2);
        if (cpu_score != 0.0f) {
            cpu_score_value_view2.setText(String.valueOf(cpu_score));
        } else {
            cpu_score_value_view2.setText(getString(R.string.unknown));
        }

        TextView gpu_score_value2 = view.findViewById(R.id.gpu_score_value2);
        if (gpu_score != 0.0f) {
            gpu_score_value2.setText(String.valueOf(gpu_score));
        } else {
            gpu_score_value2.setText(getString(R.string.unknown));
        }

        TextView ram_value_view2 = view.findViewById(R.id.ram_value2);
        if (ram != 0.0f) {
            ram_value_view2.setText((int) ram + " GB");
        }
        else {
            ram_value_view2.setText(getString(R.string.unknown));
        }

        TextView screen_size_value_view2 = view.findViewById(R.id.screen_size_value2);
        if (screen_size != 0.0f) {
            screen_size_value_view2.setText(screen_size + "\"");
        } else {
            screen_size_value_view2.setText(getString(R.string.unknown));
        }

        TextView screen_resolution_value_view2 = view.findViewById(R.id.screen_resolution_value2);
        if (screen_resolution != null) {
            screen_resolution_value_view2.setText(screen_resolution);
        } else {
            screen_resolution_value_view2.setText(getString(R.string.unknown));
        }

        TextView screen_resolution_type_value_view2 = view.findViewById(R.id.screen_resolution_type_value2);
        if (screen_resolution_type != null) {
            screen_resolution_type_value_view2.setText(screen_resolution_type);
        } else {
            screen_resolution_type_value_view2.setText(getString(R.string.unknown));
        }

        TextView ssd_value_view2 = view.findViewById(R.id.ssd_value2);
        if (ssd != null) {
            ssd_value_view2.setText(ssd);
        } else {
            ssd_value_view2.setText(getString(R.string.unknown));
        }

        TextView hdd_value_view2 = view.findViewById(R.id.hdd_value2);
        if (hdd != null) {
            hdd_value_view2.setText(ssd);
        } else {
            hdd_value_view2.setText(getString(R.string.unknown));
        }

        TextView weight_value_view2 = view.findViewById(R.id.weight_value2);
        if (weight != null) {
            weight_value_view2.setText(weight + " kg");
        } else {
            weight_value_view2.setText(getString(R.string.unknown));
        }

        TextView operating_system_value_view2 = view.findViewById(R.id.operating_system_value2);
        if (operating_system != null) {
            operating_system_value_view2.setText(operating_system);
        } else {
            operating_system_value_view2.setText(getString(R.string.unknown));
        }

        TextView cpu_clock_speeds_value_view2 = view.findViewById(R.id.cpu_clock_speeds_value2);
        if (cpu_low_speed != 0.0f || cpu_high_speed != 0.0f) {
            if (cpu_low_speed != 0.0f && cpu_high_speed != 0.0f) {
                cpu_clock_speeds_value_view2.setText(cpu_low_speed + " GHz - " + cpu_high_speed + " GHZ");
            }
            if (cpu_low_speed != 0.0f) {
                cpu_clock_speeds_value_view2.setText(cpu_low_speed + " GHZ");
            }
            if (cpu_high_speed != 0.0f) {
                cpu_clock_speeds_value_view2.setText(cpu_high_speed + " GHz");
            }
        } else {
            cpu_clock_speeds_value_view2.setText(getString(R.string.unknown));
        }

        TextView core_amount_value_view2 = view.findViewById(R.id.core_amount_value2);
        if (core_amount != 0.0f) {
            core_amount_value_view2.setText(String.valueOf((int) core_amount));
        } else {
            core_amount_value_view2.setText(getString(R.string.unknown));
        }

        TextView screen_panel_type_value_view2 = view.findViewById(R.id.screen_panel_type_value2);
        if (screen_panel_type != null) {
            screen_panel_type_value_view2.setText(screen_panel_type);
        } else {
            screen_panel_type_value_view2.setText(getString(R.string.unknown));
        }

        TextView gpu_model_view2 = view.findViewById(R.id.gpu_model_value2);
        if (gpu_model != null) {
            gpu_model_view2.setText(gpu_model + " " + gpu_value + " GHz");
        } else {
            gpu_model_view2.setText(getString(R.string.unknown));
        }

        Button favourite_button2 = view.findViewById(R.id.favourite_button2);

        int finalIndex2 = index;
        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Integer> group = (List<Integer>) documentSnapshot.get("laptop-likes");
                Long primary_key = (long) finalIndex2;
                assert group != null;
                if(group.contains(primary_key)){
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.favourite_on)));
                }
            });
        }

        favourite_button2.setOnClickListener(v -> {
            if(mAuth.getCurrentUser() != null){
                user_id = mAuth.getCurrentUser().getEmail();
                documentReference = user_database.collection("users").document(user_id);
                if(favourite_button.getBackgroundTintList() == ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on))) {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.colorPrimary)));
                    documentReference.update("laptop-likes", FieldValue.arrayRemove(finalIndex2));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.laptop_fav_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                    documentReference.update("laptop-likes", FieldValue.arrayUnion(finalIndex2));
                    Toast.makeText(v.getContext(),v.getContext().getText(R.string.laptop_fav_added), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
            }
        });

        String similar_search_base = "SELECT [index], [Ekran Boyutu(inç)], [Ram(Bellek) Boyutu(GB)], [İşlemci Modeli], [İşlemci Markası], [İşlemci Arttırılmış Frekans(GHz)], [Ürün Adı], [En Düşük Fiyat], [Medyan Fiyat], " +
                "[Harici Ekran Kartı Belleği(GB)], [Harici Ekran Kartı Modeli], [Marka], [uri] FROM NOTEBOOKS";


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

        laptop_ItemList.clear();
        laptop_ItemList = getLaptop_ItemList(cursor_similar);
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