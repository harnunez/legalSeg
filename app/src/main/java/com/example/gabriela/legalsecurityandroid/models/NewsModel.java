package com.example.gabriela.legalsecurityandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class NewsModel implements  Parcelable {

    @SerializedName("codigoRespuesta")
    public Integer codeResponse;

    @SerializedName("mensaje")
    public String message;

    @SerializedName("nivelAlerta")
    public Integer alertLevel;


    protected NewsModel(Parcel in) {
        codeResponse = in.readInt();
        message = in.readString();
        alertLevel = in.readInt();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(codeResponse);
        dest.writeString(message);
        dest.writeInt(alertLevel);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewsModel> CREATOR = new Creator<NewsModel>() {
        @Override
        public NewsModel createFromParcel(Parcel in) {
            return new NewsModel(in);
        }

        @Override
        public NewsModel[] newArray(int size) {
            return new NewsModel[size];
        }
    };
}
