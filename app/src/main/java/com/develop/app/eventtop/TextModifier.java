package com.develop.app.eventtop;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

public class TextModifier {

    public static void replaceTypeface(ViewGroup viewGroup, Context context) {
        // Log.d("view-11", viewGroup.getAccessibilityClassName().toString());

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);

            if (v instanceof  TextView) {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "STV_0.ttf");
                ((TextView) v).setTypeface(typeface);
            }

            if (v instanceof RadioButton) {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "STV_0.ttf");
                ((RadioButton) v).setTypeface(typeface);
            }

            if (v instanceof Button) {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "STV_0.ttf");
                ((Button) v).setTypeface(typeface);
            }
        }
    }

    public static void replaceTypeface(RecyclerView recyclerView, Context context) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View v = recyclerView.getChildAt(i);

            if (v instanceof  TextView) {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "STV_0.ttf");
                ((TextView) v).setTypeface(typeface);
            }
        }
    }

    public static void replaceTypeface(Snackbar snackbar, Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "STV_0.ttf");
        TextView tv = (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        tv.setTypeface(typeface);
        tv.setTextSize(17);
        snackbar.show();
    }
}
