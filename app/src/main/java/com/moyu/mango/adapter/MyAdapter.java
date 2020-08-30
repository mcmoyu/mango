package com.moyu.mango.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.moyu.mango.R;
import com.moyu.mango.object.Goods;

import java.util.List;


public class MyAdapter extends ArrayAdapter<Goods> {

    private int resourceId;
    Context context;

    public MyAdapter(@NonNull Context context, int textViewResourceId, List<Goods> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Goods goods = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        ImageView mainPic = view.findViewById(R.id.main_pic);
        TextView title = view.findViewById(R.id.title);
        TextView coupon = view.findViewById(R.id.coupon);
        TextView volume = view.findViewById(R.id.volume);
        ProgressBar pb = view.findViewById(R.id.pb); // max | process=max-remain
        TextView remain = view.findViewById(R.id.remain);
        TextView price = view.findViewById(R.id.price);
        TextView yuanjia = view.findViewById(R.id.yuanjia); // price + coupon
        TextView yongjin = view.findViewById(R.id.yongjin);

//        mainPic.setImageBitmap(goods.getMainPic());
        title.setText(goods.getTitle());
        coupon.setText(goods.getCoupon() + "");
        volume.setText(goods.getVolume());
        pb.setMax(goods.getTotalNum());
        pb.setProgress(goods.getRemainNum());
        remain.setText(goods.getRemainNum()>99999?"99999+":goods.getRemainNum()+"");
        price.setText(goods.getPrice());
        String yj = (Double.parseDouble(goods.getPrice()) + goods.getCoupon()) + "";
        yuanjia.setText((yj.length()-yj.indexOf(".")) == 0?yj+"00":(yj.length()-yj.indexOf(".")) == 1?yj+"0":yj);
        yuanjia.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        String s = Double.parseDouble(goods.getPrice()) * goods.getRate() / 250 + "";
        yongjin.setText((s.length()-s.indexOf(".")) > 2?s.substring(0,s.indexOf(".") + 3):s);

        return view;
    }
}
