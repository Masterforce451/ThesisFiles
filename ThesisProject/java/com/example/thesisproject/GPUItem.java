package com.example.thesisproject;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class GPUItem implements Parcelable {
    private final int primary_key, type;
    private final String gpu_model, gpu_brand, similar_item_link;
    private final float vram, gpu_score;
    private final float lowestPrice, medianPrice;
    private Drawable gpuPhoto;

    public GPUItem(int primary_key, Float vram, String gpu_model, Float gpu_score, String gpu_brand,
                   Float lowestPrice, Float medianPrice, Drawable gpuPhoto, String similar_item_link, int type) {

        this.primary_key = primary_key;
        this.vram = vram;
        this.gpu_model = gpu_model;
        this.gpu_score = gpu_score;
        this.gpu_brand = gpu_brand;
        this.lowestPrice = lowestPrice;
        this.medianPrice = medianPrice;
        this.gpuPhoto = gpuPhoto;
        this.similar_item_link = similar_item_link;
        this.type = type;
    }

    public static final Creator<GPUItem> CREATOR = new Creator<GPUItem>() {
        @Override
        public GPUItem createFromParcel(Parcel in) {return new GPUItem(in);}
        @Override
        public GPUItem[] newArray(int size) {return new GPUItem[size];}
    };

    public GPUItem(Parcel in) {
        primary_key = in.readInt();
        vram = in.readFloat();
        gpu_model = in.readString();
        gpu_score = in.readFloat();
        gpu_brand = in.readString();
        lowestPrice = in.readFloat();
        medianPrice = in.readFloat();
        similar_item_link = in.readString();
        type = in.readInt();
    }

    public int getPrimary_key() {return primary_key;}
    public Float getVram() {return vram;}
    public String getGPU_model() {return gpu_model;}
    public Float getGpu_score() {return gpu_score;}
    public String getGpu_brand() {return gpu_brand;}
    public Float getLowestPrice() {return lowestPrice;}
    public Float getMedianPrice() {return medianPrice;}
    public Drawable getGpuPhoto() {return gpuPhoto;}
    public int getType() {return type;}
    public String getSimilar_item_link() {return similar_item_link;}

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(primary_key);
        dest.writeFloat(vram);
        dest.writeString(gpu_model);
        dest.writeFloat(gpu_score);
        dest.writeString(gpu_brand);
        dest.writeFloat(lowestPrice);
        dest.writeFloat(medianPrice);
        dest.writeString(similar_item_link);
        dest.writeInt(type);}
}
