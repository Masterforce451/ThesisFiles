package com.example.thesisproject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LaptopItemRecyclerViewAdapter extends RecyclerView.Adapter<LaptopItemRecyclerViewAdapter.LaptopItemAdapter> {
    private static List<LaptopItem> LaptopItemList;
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    static String user_id;
    static DocumentReference documentReference;

    public LaptopItemRecyclerViewAdapter(List<LaptopItem> LaptopItemList) {
        LaptopItemRecyclerViewAdapter.LaptopItemList = LaptopItemList;
    }
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public LaptopItemAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if(viewType == 0){
            return new LaptopItemAdapter(LayoutInflater.from(context).inflate(R.layout.laptop_item, null));
        } else if(viewType == 1){
            return new LaptopItemAdapter(LayoutInflater.from(context).inflate(R.layout.laptop_similar_item, null));
        }
        return new LaptopItemAdapter(LayoutInflater.from(context).inflate(R.layout.laptop_item, null));
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LaptopItemAdapter holder, int position) {
        com.example.thesisproject.LaptopItem laptopItem = LaptopItemList.get(position);
        String laptop_name = laptopItem.getLaptop_name();
        Float screen_size = laptopItem.getScreen_size();
        Float ram = laptopItem.getRam();
        String cpu_model = laptopItem.getCPU_model();
        Float cpu_turbo_speed = laptopItem.getCPU_turbo_speed();
        int price_lowest_value = laptopItem.getLowestPrice().intValue();
        int price_median_value = laptopItem.getMedianPrice().intValue();
        String gpu_model = laptopItem.getGpu_model();
        Float gpu_model_value = laptopItem.getGpu_model_value();
        Drawable laptop_photo = laptopItem.getLaptopPhoto();
        String similar_item_url = laptopItem.getSimilar_item_link();
        int primary_key = laptopItem.getPrimary_key();


        holder.laptop_name.setText(laptop_name);
        if (screen_size != 0.0f){
            holder.screen_size.setText(screen_size + "\"");
        } else {
            holder.screen_size.setText(R.string.unknown);
        }
        if (ram != 0.0f){
            holder.ram.setText(ram.intValue()+ " GB");
        } else {
            holder.ram.setText(R.string.unknown);
        }
        if (cpu_model != null){
            if (cpu_turbo_speed != 0.0f) {
                holder.cpu_model.setText(cpu_model + " " + cpu_turbo_speed + " Ghz");
            } else {
                holder.cpu_model.setText(cpu_model);
            }
        } else {
            holder.cpu_model.setText(R.string.unknown);
        }
        if (gpu_model != null) {
            if (gpu_model_value != 0.0f) {
                holder.gpu_model_value.setText(gpu_model + " " + gpu_model_value + " GB");
            } else {
                holder.gpu_model_value.setText(gpu_model);
            }
        } else {
            holder.gpu_model_value.setText(R.string.unknown);
        }

        holder.price_lowest_value.setText(price_lowest_value +" TL");
        holder.price_median_value.setText(price_median_value +" TL");
        holder.laptop_photo.setImageDrawable(laptop_photo);
        if(similar_item_url != null){
            holder.similar_item_url.setText(similar_item_url);
        }

        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Long> group = (List<Long>) documentSnapshot.get("laptop-likes");
                List<Integer> intList = new ArrayList<>();
                for(int i = 0; i< Objects.requireNonNull(group).size(); i++){
                    int number = Math.toIntExact(group.get(i));
                    intList.add(number);
                }
                if(intList.contains(primary_key)){
                    holder.favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.laptop_name.getContext(), R.color.favourite_on)));
                } else {
                    holder.favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.laptop_name.getContext(), R.color.colorPrimary)));
                }
            });
        }

    }
    @Override
    public int getItemCount() {
        return LaptopItemList.size();
    }

    public static class LaptopItemAdapter extends RecyclerView.ViewHolder{
        TextView laptop_name, screen_size, ram, cpu_model, price_lowest_value, price_median_value, gpu_model_value, similar_item_url;
        ImageView laptop_photo;
        TextView id, primary_key;
        Button favourite_button;
        public LaptopItemAdapter(@NonNull View itemView) {
            super(itemView);

            laptop_name = itemView.findViewById(R.id.laptop_model);
            screen_size = itemView.findViewById(R.id.screen_size_value);
            ram = itemView.findViewById(R.id.ram_value);
            cpu_model = itemView.findViewById(R.id.processor_model_value);
            gpu_model_value = itemView.findViewById(R.id.cpu_model_value);
            price_lowest_value = itemView.findViewById(R.id.price_lowest_value);
            price_median_value = itemView.findViewById(R.id.price_average_value);
            laptop_photo = itemView.findViewById(R.id.laptopPhoto);
            similar_item_url = itemView.findViewById(R.id.similar_item_url);
            id = itemView.findViewById(R.id.type);
            favourite_button = itemView.findViewById(R.id.favourite_button);
            primary_key = itemView.findViewById(R.id.primary_key);

            itemView.setOnClickListener(v -> {
                int position = getLayoutPosition();
                int primary_key = LaptopItemList.get(position).getPrimary_key();

                Bundle bundle = new Bundle();
                bundle.putInt("primary_key",primary_key);
                LaptopItemFragment LaptopItemFragment = new LaptopItemFragment();
                LaptopItemFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, LaptopItemFragment).addToBackStack(null).setReorderingAllowed(true).commit();
            });

            favourite_button.setOnClickListener(v -> {
                if(mAuth.getCurrentUser() != null) {
                    documentReference = user_database.collection("users").document(user_id);
                    int position = getLayoutPosition();
                    if(favourite_button.getBackgroundTintList() == ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on))) {
                        favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.colorPrimary)));
                        int primary_key = LaptopItemList.get(position).getPrimary_key();
                        documentReference.update("laptop-likes", FieldValue.arrayRemove(primary_key));
                        Toast.makeText(v.getContext(),v.getContext().getText(R.string.laptop_fav_deleted), Toast.LENGTH_SHORT).show();
                    } else {
                        favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                        int primary_key = LaptopItemList.get(position).getPrimary_key();
                        documentReference.update("laptop-likes", FieldValue.arrayUnion(primary_key));
                        Toast.makeText(v.getContext(),v.getContext().getText(R.string.laptop_fav_added), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
    @Override
    public int getItemViewType(int position) {
        return LaptopItemList.get(position).getType();
    }
}