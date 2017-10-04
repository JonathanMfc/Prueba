package com.mfc.prueba.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mfc.prueba.R;
import com.mfc.prueba.model.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by USUARIO on 03/10/2017.
 */

public class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.WeekWeatherViewHolder> {

    Context context;
    ArrayList<Data> datas;

    public WeekWeatherAdapter(Context context) {
        this.context = context;
        this.datas = new ArrayList<>();

    }

    @Override
    public WeekWeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.view_item_day,parent,false);
        return new WeekWeatherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WeekWeatherViewHolder holder, int position) {
        Data data = datas.get(position);
        holder.setData(data.getIcon(),data.getTime(),data.getTemperatureHigh());

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addAll(ArrayList<Data> datas) {
        if (datas != null) {
            this.datas.addAll(datas);
            notifyDataSetChanged();

        }

    }

    public class WeekWeatherViewHolder extends RecyclerView.ViewHolder {

        TextView day,temp;
        ImageView icon;


        public WeekWeatherViewHolder(View itemView) {
            super(itemView);

            day = (TextView)itemView.findViewById(R.id.day);
            temp = (TextView)itemView.findViewById(R.id.temp_day);
            icon = (ImageView)itemView.findViewById(R.id.icon);
        }

        public void setData(String icon, int time, double temperatureHigh) {

            SimpleDateFormat sdf = new SimpleDateFormat("EEE");
            day.setText(sdf.format(new Date(time * 1000L)));
            temp.setText(String.valueOf(temperatureHigh));
        }
    }
}
