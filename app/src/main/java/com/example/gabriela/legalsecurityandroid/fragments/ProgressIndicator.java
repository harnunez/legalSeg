package com.example.gabriela.legalsecurityandroid.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.gabriela.legalsecurityandroid.R;


public class ProgressIndicator extends DialogFragment {
    private static final String MENSAJE = "mensaje";
    private static final String TAG_CANCEL = "tag";

    /**
     * Instancia el progress con un mensaje pasado por parámetro
     *
     * @return la instancia del ProgresIndicator
     */
    public static ProgressIndicator newInstance(String tagCancel) {
        ProgressIndicator progresIndicator = new ProgressIndicator();
        if (!TextUtils.isEmpty(tagCancel)) {
            Bundle args = new Bundle();
            if (!TextUtils.isEmpty(tagCancel)) {
                args.putString(TAG_CANCEL, tagCancel);
            }
            progresIndicator.setArguments(args);
        }
        return progresIndicator;
    }


    public static ProgressIndicator newInstance() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        Bundle args = new Bundle();
        args.putString(TAG_CANCEL, "");
        progressIndicator.setArguments(args);
        return progressIndicator;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View bodyView = inflater.inflate(R.layout.fragment_dialog_progress, container, false);
        return bodyView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);

    }


    @Override
    public void show(FragmentManager manager, String tag) {
        manager.beginTransaction().add(this, tag).commitAllowingStateLoss();
    }


}
