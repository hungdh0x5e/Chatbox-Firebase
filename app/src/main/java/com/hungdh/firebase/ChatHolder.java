package com.hungdh.firebase;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by hungdh on 14/03/2016.
 */
public class ChatHolder extends RecyclerView.ViewHolder {
    View mView;

    public ChatHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setIsSender(Boolean isSender) {
        FrameLayout left_arrow = (FrameLayout) mView.findViewById(R.id.left_arrow);
        FrameLayout right_arrow = (FrameLayout) mView.findViewById(R.id.right_arrow);
        RelativeLayout messageContainer = (RelativeLayout) mView.findViewById(R.id.message_container);
        LinearLayout message = (LinearLayout) mView.findViewById(R.id.message);
        TextView fieldName = (TextView) mView.findViewById(R.id.name_text);

        if (isSender) {
            fieldName.setVisibility(View.GONE);
            left_arrow.setVisibility(View.GONE);
            right_arrow.setVisibility(View.VISIBLE);
            messageContainer.setGravity(Gravity.RIGHT);
        } else {
            fieldName.setVisibility(View.VISIBLE);
            left_arrow.setVisibility(View.VISIBLE);
            right_arrow.setVisibility(View.GONE);
            messageContainer.setGravity(Gravity.LEFT);
        }
    }

    public void setName(String name) {
        TextView field = (TextView) mView.findViewById(R.id.name_text);
        field.setText(name);
    }

    public void setText(String text) {
        TextView field = (TextView) mView.findViewById(R.id.message_text);
        field.setText(text);
    }
}
