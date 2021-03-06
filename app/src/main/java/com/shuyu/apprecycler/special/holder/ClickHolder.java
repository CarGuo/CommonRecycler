package com.shuyu.apprecycler.special.holder;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.shuyu.common.RecyclerBaseHolder;
import com.shuyu.common.model.RecyclerBaseModel;
import com.shuyu.apprecycler.special.model.ClickModel;
import com.shuyu.apprecycler.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shuyu on 2016/11/23.
 */

public class ClickHolder extends RecyclerBaseHolder {

    public final static int ID = R.layout.click_item;

    @BindView(R.id.item_button)
    Button itemButton;

    public ClickHolder(Context context, View v) {
        super(context, v);
    }

    @Override
    public void createView(View v) {
        ButterKnife.bind(this, v);
    }

    @Override
    public void onBind(RecyclerBaseModel model, int position) {
        ClickModel clickModel = (ClickModel) model;
        itemButton.setText(clickModel.getBtnText());
    }
}
