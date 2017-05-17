package com.example.gustavo.chamada;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.app.ProgressDialog;
import android.widget.TextView;

/**
 * Common methods for all views related to screen
 */


class ScreenUtils {

    private static int PROGRESS_BAR_ID = 42;
    static ProgressDialog loadingDialog = null;

    /* This code is based on the one provided at:
    * http://stackoverflow.com/questions/6238881/how-to-disable-all-click-events-of-a-layout
    * */
    static void setLoadingView(View view, boolean enabled) {
        Context context = view.getContext();
        view.setEnabled(enabled);

        if (enabled)
            view.setAlpha((float) 1);
        else
            view.setAlpha((float) .5);

        if (!enabled && loadingDialog == null) {
            loadingDialog = new ProgressDialog(context, R.style.Chamada_DialogBox);
            loadingDialog.setCancelable(true);
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setMessage(context.getString(R.string.loadingMessage));
            loadingDialog.show();
        }
        else if (enabled && loadingDialog != null)
        {
            loadingDialog.dismiss();
            loadingDialog = null;
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0 ; idx < group.getChildCount(); idx++) {
                setLoadingView(group.getChildAt(idx), enabled);
                view.setEnabled(enabled);
            }
        }
    }

    static void showMessaDialog(Context context, String message,
                                DialogInterface.OnClickListener listener) {
        CustomDialogBuilder builder = new CustomDialogBuilder(context);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.ok_message, listener);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    static void showInputDialog(Context context, String title, EditText inputEditText,
                                DialogInterface.OnClickListener okListener,
                                DialogInterface.OnClickListener cancelListener) {
        CustomDialogBuilder builder = new ScreenUtils.CustomDialogBuilder(context);
        builder.setTitle(title);
        builder.addView(inputEditText);
        builder.setPositiveButton(R.string.ok_message, okListener);
        builder.setNegativeButton(R.string.cancel_message, cancelListener);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    /* Defines a custom AlertDialog */
    private static class CustomDialogBuilder extends AlertDialog.Builder {

        private TextView titleTextView = null;
        private LinearLayout layout = null;
        private LayoutInflater li;

        CustomDialogBuilder(Context context) {
            super(context, R.style.Chamada_DialogBox);
            this.li = LayoutInflater.from(context);
        }

        private void setView() {
            if (layout != null)
                return;
            View dialogView = li.inflate(R.layout.input_message_dialog, null);
            this.titleTextView = (TextView) dialogView.findViewById(R.id.title);
            this.titleTextView.setVisibility(View.GONE);
            this.layout = (LinearLayout) dialogView.findViewById(R.id.messageDialogLayout);
            setView(dialogView);
        }

        @Override
        public AlertDialog.Builder setTitle(CharSequence title) {
            setView();
            titleTextView.setText(title);
            titleTextView.setVisibility(View.VISIBLE);
            return this;
        }

        void addView(View view) {
            setView();
            layout.addView(view, 1);
        }
    }
}
