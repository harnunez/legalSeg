package com.example.gabriela.legalsecurityandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class LoginUserModel implements Parcelable {

    @SerializedName("codigoRespuesta")
    public Integer codeResponse;

    @SerializedName("mensaje")
    public String message;

    @SerializedName("idCliente")
    public String clientId;

    @SerializedName("cuentas")
    public String[] bills;


    protected LoginUserModel(Parcel in) {
        codeResponse = in.readInt();
        message = in.readString();
        clientId = in.readString();
        bills = in.createStringArray();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(codeResponse);
        dest.writeString(message);
        dest.writeString(clientId);
        dest.writeStringArray(bills);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LoginUserModel> CREATOR = new Creator<LoginUserModel>() {
        @Override
        public LoginUserModel createFromParcel(Parcel in) {
            return new LoginUserModel(in);
        }

        @Override
        public LoginUserModel[] newArray(int size) {
            return new LoginUserModel[size];
        }
    };
}
