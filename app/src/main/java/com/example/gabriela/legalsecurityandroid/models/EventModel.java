package com.example.gabriela.legalsecurityandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class EventModel implements Parcelable {
    @SerializedName("codigoRespuesta")
    public Integer codeResponse;

    @SerializedName("eventosDisponibles")
    public Integer availableEvents;

    @SerializedName("mensaje")
    public String message;



    protected EventModel(Parcel in) {
        codeResponse = in.readInt();
        message = in.readString();
        availableEvents = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(codeResponse);
        dest.writeString(message);
        dest.writeInt(availableEvents);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventModel> CREATOR = new Creator<EventModel>() {
        @Override
        public EventModel createFromParcel(Parcel in) {
            return new EventModel(in);
        }

        @Override
        public EventModel[] newArray(int size) {
            return new EventModel[size];
        }
    };
}
