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

public class GPUItemRecyclerViewAdapter extends RecyclerView.Adapter<GPUItemRecyclerViewAdapter.GPUItemAdapter> {
    private static List<GPUItem> GPUItemList;
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    static String user_id;
    static DocumentReference documentReference;

    public GPUItemRecyclerViewAdapter(List<GPUItem> GPUItemList) {
        GPUItemRecyclerViewAdapter.GPUItemList = GPUItemList;
    }
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public GPUItemAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if(viewType == 0){
            return new GPUItemAdapter(LayoutInflater.from(context).inflate(R.layout.gpu_item, null));
        } else if(viewType == 1){
            return new GPUItemAdapter(LayoutInflater.from(context).inflate(R.layout.gpu_similar_item, null));
        }
        return new GPUItemAdapter(LayoutInflater.from(context).inflate(R.layout.gpu_item, null));
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GPUItemAdapter holder, int position) {
        GPUItem GPUItem = GPUItemList.get(position);
        Float vram = GPUItem.getVram();
        String gpu_model = GPUItem.getGPU_model();
        Float gpu_score = GPUItem.getGpu_score();
        String gpu_brand = GPUItem.getGpu_brand();
        int price_lowest_value = GPUItem.getLowestPrice().intValue();
        int price_median_value = GPUItem.getMedianPrice().intValue();
        Drawable gpu_photo = GPUItem.getGpuPhoto();
        String similar_item_url = GPUItem.getSimilar_item_link();
        int primary_key = GPUItem.getPrimary_key();

        holder.gpu_model.setText(gpu_brand+" "+gpu_model);
        holder.vram.setText(vram.intValue()+" GB");
        holder.gpu_score.setText(gpu_score.toString());
        holder.price_lowest_value.setText(price_lowest_value +" TL");
        holder.price_median_value.setText(price_median_value +" TL");
        holder.gpu_photo.setImageDrawable(gpu_photo);
        if(similar_item_url != null){
            holder.similar_item_url.setText(similar_item_url);
        }

        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Long> group = (List<Long>) documentSnapshot.get("gpu-likes");
                List<Integer> intList = new ArrayList<>();
                for(int i = 0; i< Objects.requireNonNull(group).size(); i++){
                    int number = Math.toIntExact(group.get(i));
                    intList.add(number);
                }
                if(intList.contains(primary_key)){
                    holder.favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.gpu_model.getContext(), R.color.favourite_on)));
                } else {
                    holder.favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.gpu_model.getContext(), R.color.colorPrimary)));
                }
            });
        }

    }
    @Override
    public int getItemCount() {
        return GPUItemList.size();
    }

    public static class GPUItemAdapter extends RecyclerView.ViewHolder{
        TextView price_lowest_value, price_median_value, gpu_model, vram, gpu_score, similar_item_url;
        ImageView gpu_photo;
        TextView id, primary_key;
        Button favourite_button;
        public GPUItemAdapter(@NonNull View itemView) {
            super(itemView);

            gpu_model = itemView.findViewById(R.id.gpu_model);
            vram = itemView.findViewById(R.id.vram_value);
            gpu_score = itemView.findViewById(R.id.gpu_score_value);
            price_lowest_value = itemView.findViewById(R.id.price_lowest_value);
            price_median_value = itemView.findViewById(R.id.price_average_value);
            gpu_photo = itemView.findViewById(R.id.gpuPhoto);
            similar_item_url = itemView.findViewById(R.id.similar_item_url);
            id = itemView.findViewById(R.id.type);
            favourite_button = itemView.findViewById(R.id.favourite_button);
            primary_key = itemView.findViewById(R.id.primary_key);

            itemView.setOnClickListener(v -> {
                int position = getLayoutPosition();
                int primary_key = GPUItemList.get(position).getPrimary_key();

                Bundle bundle = new Bundle();
                bundle.putInt("primary_key",primary_key);
                GPUItemFragment gpuItemFragment = new GPUItemFragment();
                gpuItemFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, gpuItemFragment).addToBackStack(null).setReorderingAllowed(true).commit();
        });

            favourite_button.setOnClickListener(v -> {
                if(mAuth.getCurrentUser() != null) {
                    documentReference = user_database.collection("users").document(user_id);
                    int position = getLayoutPosition();
                    if(favourite_button.getBackgroundTintList() == ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on))) {
                        favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.colorPrimary)));
                        int primary_key = GPUItemList.get(position).getPrimary_key();
                        documentReference.update("gpu-likes", FieldValue.arrayRemove(primary_key));
                        Toast.makeText(v.getContext(),v.getContext().getText(R.string.gpu_fav_deleted), Toast.LENGTH_SHORT).show();
                    } else {
                        favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                        int primary_key = GPUItemList.get(position).getPrimary_key();
                        documentReference.update("gpu-likes", FieldValue.arrayUnion(primary_key));
                        Toast.makeText(v.getContext(),v.getContext().getText(R.string.gpu_fav_added), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return GPUItemList.get(position).getType();
    }
}