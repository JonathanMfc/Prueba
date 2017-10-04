package com.mfc.prueba.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mfc.prueba.R;

/**
 * Created by USUARIO on 03/10/2017.
 */

public class ProgressLottie extends Dialog {

    public ProgressLottie(@NonNull Context context) {
        super(context);
        View viewProgress = LayoutInflater.from(context).inflate(R.layout.view_progress,null,false);

        setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(viewProgress);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.color_drawable, null));
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);




    }

}
