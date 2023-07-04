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

public class CPUItemRecyclerViewAdapter extends RecyclerView.Adapter<CPUItemRecyclerViewAdapter.CPUItemAdapter> {
    private static List<CPUItem> CPUItemList;
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("StaticFieldLeak")
    static FirebaseFirestore user_database = FirebaseFirestore.getInstance();
    static String user_id;
    static DocumentReference documentReference;

    public CPUItemRecyclerViewAdapter(List<CPUItem> CPUItemList) {
        CPUItemRecyclerViewAdapter.CPUItemList = CPUItemList;
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public CPUItemAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if(viewType == 0){
            return new CPUItemAdapter(LayoutInflater.from(context).inflate(R.layout.cpu_item, null));
        } else if(viewType == 1){
            return new CPUItemAdapter(LayoutInflater.from(context).inflate(R.layout.cpu_similar_item, null));
        }
        return new CPUItemAdapter(LayoutInflater.from(context).inflate(R.layout.cpu_item, null));
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CPUItemAdapter holder, int position) {
        CPUItem CPUItem = CPUItemList.get(position);
        String cpu_model = CPUItem.getCPUModel();
        int price_lowest_value = CPUItem.getLowestPrice().intValue();
        int price_median_value = CPUItem.getMedianPrice().intValue();
        Float cpu_base_frequency = CPUItem.getBaseFrequency();
        Float cpu_increased_frequency = CPUItem.getIncreasedFrequency();
        Drawable cpu_photo = CPUItem.getProcessorPhoto();
        String similar_item_url = CPUItem.getSimilar_item_link();
        Float cpu_score = CPUItem.getCpu_score();
        int primary_key = CPUItem.getPrimary_key();

        holder.cpu_model.setText(cpu_model);
        holder.price_lowest_value.setText(price_lowest_value +" TL");
        holder.price_median_value.setText(price_median_value +" TL");
        holder.cpu_base_frequency.setText(cpu_base_frequency.toString() +" GHz");
        holder.cpu_increased_frequency.setText(cpu_increased_frequency.toString() + " GHz");
        holder.cpu_photo.setImageDrawable(cpu_photo);
        if(similar_item_url != null){
            holder.similar_item_url.setText(similar_item_url);
        }
        holder.cpu_score.setText(String.valueOf(cpu_score.intValue()));
        holder.primary_key.setText(String.valueOf(primary_key));

        if(mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getEmail();
            user_database.collection("users").document(user_id).get().addOnCompleteListener(task -> {
                DocumentSnapshot documentSnapshot = task.getResult();
                @SuppressWarnings("unchecked")
                List<Long> group = (List<Long>) documentSnapshot.get("cpu-likes");
                List<Integer> intList = new ArrayList<>();
                for(int i = 0; i< Objects.requireNonNull(group).size(); i++){
                    int number = Math.toIntExact(group.get(i));
                    intList.add(number);
                }
                if(intList.contains(primary_key)){
                    holder.favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.cpu_model.getContext(), R.color.favourite_on)));
                } else {
                    holder.favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.cpu_model.getContext(), R.color.colorPrimary)));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return CPUItemList.size();
    }

    public static class CPUItemAdapter extends RecyclerView.ViewHolder{
        TextView price_lowest_value, price_median_value, cpu_base_frequency, cpu_increased_frequency, cpu_model, similar_item_url, cpu_score;
        ImageView cpu_photo;
        TextView id, primary_key;
        Button favourite_button;
        public CPUItemAdapter(@NonNull View itemView) {
            super(itemView);
            cpu_model = itemView.findViewById(R.id.cpu_model);
            price_lowest_value = itemView.findViewById(R.id.price_lowest_value);
            price_median_value = itemView.findViewById(R.id.price_average_value);
            cpu_base_frequency = itemView.findViewById(R.id.cpu_speed_value);
            cpu_increased_frequency = itemView.findViewById(R.id.cpu_turbo_speed_value);
            cpu_photo = itemView.findViewById(R.id.cpu_photo);
            similar_item_url = itemView.findViewById(R.id.similar_item_url);
            id = itemView.findViewById(R.id.type);
            cpu_score = itemView.findViewById(R.id.cpu_score_value);
            favourite_button = itemView.findViewById(R.id.favourite_button);
            primary_key = itemView.findViewById(R.id.primary_key);

            itemView.setOnClickListener(v -> {
                int position = getLayoutPosition();
                int primary_key = CPUItemList.get(position).getPrimary_key();

                Bundle bundle = new Bundle();
                bundle.putInt("primary_key",primary_key);
                CPUItemFragment cpuItemFragment = new CPUItemFragment();
                cpuItemFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, cpuItemFragment).addToBackStack(null).setReorderingAllowed(true).commit();
            });

            favourite_button.setOnClickListener(v -> {
                if(mAuth.getCurrentUser() != null){
                    documentReference = user_database.collection("users").document(user_id);
                    int position = getLayoutPosition();
                    if(favourite_button.getBackgroundTintList() == ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on))) {
                        favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.colorPrimary)));
                        int primary_key = CPUItemList.get(position).getPrimary_key();
                        documentReference.update("cpu-likes", FieldValue.arrayRemove(primary_key));
                        Toast.makeText(v.getContext(),v.getContext().getText(R.string.cpu_fav_deleted), Toast.LENGTH_SHORT).show();
                    } else {
                        favourite_button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(v.getContext(), R.color.favourite_on)));
                        int primary_key = CPUItemList.get(position).getPrimary_key();
                        documentReference.update("cpu-likes", FieldValue.arrayUnion(primary_key));
                        Toast.makeText(v.getContext(),v.getContext().getText(R.string.cpu_fav_added), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(),v.getContext().getString(R.string.login_false), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return CPUItemList.get(position).getType();
    }
}