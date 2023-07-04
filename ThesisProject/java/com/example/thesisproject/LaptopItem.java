package com.example.thesisproject;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class LaptopItem implements Parcelable {
    private final int primary_key, type;
    private final String laptop_name, cpu_model, gpu_model, similar_item_link;
    private final float screen_size, ram, lowestPrice, medianPrice, gpu_model_value, cpu_turbo_speed;
    private Drawable laptopPhoto;

    public LaptopItem(int primary_key, String laptop_name, Float screen_size,
                      Float ram, String cpu_model, Float cpu_turbo_speed, Float lowestPrice,
                      Float medianPrice, String gpu_model, Float gpu_model_value, Drawable laptopPhoto,
                      String similar_item_link,int type) {

        this.primary_key = primary_key;
        this.laptop_name = laptop_name;
        this.screen_size = screen_size;
        this.ram = ram;
        this.cpu_model = cpu_model;
        this.cpu_turbo_speed = cpu_turbo_speed;
        this.lowestPrice = lowestPrice;
        this.medianPrice = medianPrice;
        this.gpu_model = gpu_model;
        this.gpu_model_value = gpu_model_value;
        this.laptopPhoto = laptopPhoto;
        this.similar_item_link = similar_item_link;
        this.type = type;
    }

    protected LaptopItem(Parcel in) {
        primary_key = in.readInt();
        laptop_name = in.readString();
        screen_size = in.readFloat();
        ram = in.readFloat();
        cpu_model = in.readString();
        cpu_turbo_speed = in.readFloat();
        lowestPrice = in.readFloat();
        medianPrice = in.readFloat();
        gpu_model = in.readString();
        gpu_model_value = in.readFloat();
        similar_item_link = in.readString();
        type = in.readInt();
    }

    public static final Creator<LaptopItem> CREATOR = new Creator<LaptopItem>() {
        @Override
        public LaptopItem createFromParcel(Parcel in) {return new LaptopItem(in);}
        @Override
        public LaptopItem[] newArray(int size) {return new LaptopItem[size];}
    };

    public int getPrimary_key() {return primary_key;}
    public String getLaptop_name() {return laptop_name;}
    public Float getScreen_size() {return screen_size;}
    public Float getRam() {return ram;}
    public String getCPU_model() {return cpu_model;}
    public Float getCPU_turbo_speed() {return cpu_turbo_speed;}
    public Float getLowestPrice() {return lowestPrice;}
    public Float getMedianPrice() {return medianPrice;}
    public String getGpu_model() {return gpu_model;}
    public Float getGpu_model_value() {return gpu_model_value;}
    public Drawable getLaptopPhoto() {return laptopPhoto;}
    public String getSimilar_item_link() {return similar_item_link;}
    public int getType() {return type;}

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(primary_key);
        dest.writeString(laptop_name);
        dest.writeFloat(screen_size);
        dest.writeFloat(ram);
        dest.writeString(cpu_model);
        dest.writeFloat(cpu_turbo_speed);
        dest.writeFloat(lowestPrice);
        dest.writeFloat(medianPrice);
        dest.writeString(gpu_model);
        dest.writeFloat(gpu_model_value);
        dest.writeString(similar_item_link);
        dest.writeInt(type);
    }
}
