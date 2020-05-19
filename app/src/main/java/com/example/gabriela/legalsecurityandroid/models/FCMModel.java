package com.example.gabriela.legalsecurityandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class FCMModel implements Parcelable {

    @SerializedName("codigoRespuesta")
    public Integer codeResponse;


    public FCMModel(Parcel in){
        codeResponse = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(codeResponse);
    }

    public static final Creator<FCMModel> CREATOR = new Creator<FCMModel>() {
        @Override
        public FCMModel createFromParcel(Parcel in) {
            return new FCMModel(in);
        }

        @Override
        public FCMModel[] newArray(int size) {
            return new FCMModel[size];
        }
    };
}
