package com.fpt.baobang.switchphonenumber;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DialogUtils {
    private static AlertDialog alertDialog;
    private static ProgressDialog progress;
    private static Dialog dialog;

    public static void showMessage(Context context, String tittle, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title
        alertDialogBuilder.setTitle(tittle);
        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.dismiss();
                    }
                });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static ProgressDialog showLoading(Context context, String title, String message, int max) {
        progress = new ProgressDialog(context);
        progress.setMessage(message);
        progress.setTitle(title);
        progress.setMax(max);
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        return progress;
    }

    public static void showLoading(Context context, String message) {
        dialog = new Dialog(context, R.style.full_screen_dialog);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialo_loading);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        TextView txt = dialog.findViewById(R.id.txtMessage);
        txt.setText(message);
        dialog.show();
    }

    public static void hideLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
