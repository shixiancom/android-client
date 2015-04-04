package com.shixian.android.client.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by tangtang on 15/3/26.
 */
@SuppressLint("ValidFragment")
public class DialgoFragment extends DialogFragment {

    private   String message;
    private String title;
    private  String negativeButton;
    private  String positiveButton;
    private  DialogInterface.OnClickListener negativeButtonListener;
    private  DialogInterface.OnClickListener positiveButtonListener;




    public DialgoFragment(String message,String title,String positiveButton,String negativeButton,DialogInterface.OnClickListener positiveButtonListener,DialogInterface.OnClickListener negativeButtonListener) {

        super();
        this.message = message;
        this.title=title;
        this.negativeButton=negativeButton;
        this.positiveButton=positiveButton;
        this.negativeButtonListener=negativeButtonListener;
        this.positiveButtonListener=positiveButtonListener;

    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

       return  builder.setTitle(title).setMessage(message).setPositiveButton(positiveButton,positiveButtonListener).setNegativeButton(negativeButton,negativeButtonListener).create();
    }
}
