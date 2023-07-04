package com.example.thesisproject;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class CPUItem implements Parcelable {
    private final int primary_key, type;
    private final String processorModel, similar_item_link;
    private final float baseFrequency, increasedFrequency, lowestPrice, medianPrice, cpu_score;
    private Drawable processorPhoto;

    public CPUItem(int primary_key, String processorModel,
                    Float increasedFrequency, Float baseFrequency, Float lowestPrice,
                    Float medianPrice, Drawable processorPhoto, String similar_item_link, Float cpu_score, int type) {

        this.primary_key = primary_key;
        this.processorModel = processorModel;
        this.increasedFrequency = increasedFrequency;
        this.baseFrequency = baseFrequency;
        this.lowestPrice = lowestPrice;
        this.medianPrice = medianPrice;
        this.processorPhoto = processorPhoto;
        this.similar_item_link = similar_item_link;
        this.type = type;
        this.cpu_score = cpu_score;
    }


    public static final Creator<CPUItem> CREATOR = new Creator<CPUItem>() {
        @Override
        public CPUItem createFromParcel(Parcel in) {return new CPUItem(in);}
        @Override
        public CPUItem[] newArray(int size) {return new CPUItem[size];}
    };

    public CPUItem(Parcel in) {
        primary_key = in.readInt();
        processorModel = in.readString();
        increasedFrequency = in.readFloat();
        baseFrequency = in.readFloat();
        lowestPrice = in.readFloat();
        medianPrice = in.readFloat();
        similar_item_link = in.readString();
        type = in.readInt();
        cpu_score = in.readFloat();
    }

    public int getPrimary_key() {return primary_key;}
    public String getCPUModel() {return processorModel;}
    public Float getLowestPrice() {return lowestPrice;}
    public Float getMedianPrice() {return medianPrice;}
    public Float getBaseFrequency() {return baseFrequency;}
    public Float getIncreasedFrequency() {return increasedFrequency;}
    public Drawable getProcessorPhoto() {return processorPhoto;}
    public String getSimilar_item_link() {return similar_item_link;}
    public int getType() {return type;}
    public Float getCpu_score() {return cpu_score;}

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(primary_key);
        dest.writeString(processorModel);
        dest.writeFloat(increasedFrequency);
        dest.writeFloat(baseFrequency);
        dest.writeFloat(lowestPrice);
        dest.writeFloat(medianPrice);
        dest.writeString(similar_item_link);
        dest.writeInt(type);
        dest.writeFloat(cpu_score);}
}
