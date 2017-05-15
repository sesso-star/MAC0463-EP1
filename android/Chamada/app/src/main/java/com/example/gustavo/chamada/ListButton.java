package com.example.gustavo.chamada;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;

/**
 * Contains definitions of seminar buttons used on ListView of seminars and Students
 */

abstract class ListButton extends android.support.v7.widget.AppCompatButton {

        public ListButton(Context context) {
            super(context, null, R.style.Widget_ListButton);
            setPadding(0, 20, 0, 20);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            setGravity(Gravity.CENTER);
            setBackgroundColor(ContextCompat.getColor(context, R.color.seminarButtonColor));
            setTextColor(ContextCompat.getColor(context, R.color.White));
        }

}
