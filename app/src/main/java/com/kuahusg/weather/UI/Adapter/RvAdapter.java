package com.kuahusg.weather.UI.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kuahusg.weather.R;
import com.kuahusg.weather.model.bean.Forecast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.kuahusg.weather.App.getContext;

/**
 * Created by kuahusg on 16-10-12.
 */


public class RvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Forecast> forecastList;
    private Context context;

    private ImageView pic;
    private TextView info;
    private TextView temp;
    private TextView date;
    private ImageView background_img;

    private final int TYPE_NORMAL = 0;
    private final int TYPE_FOOTER = 1;

    private int oldPosition = -1;


    public RvAdapter(List<Forecast> forecastList, Context context) {
        this.forecastList = forecastList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_NORMAL) {
            View cardView = LayoutInflater.from(this.context).inflate(R.layout.include_weather_card, parent, false);
            pic = (ImageView) cardView.findViewById(R.id.weaher_pic);
            info = (TextView) cardView.findViewById(R.id.weather_info);
            temp = (TextView) cardView.findViewById(R.id.weather_temp);
            date = (TextView) cardView.findViewById(R.id.weather_date);
            background_img = (ImageView) cardView.findViewById(R.id.card_background);
            return new NormalViewHolder(cardView, pic, info, date, temp, background_img);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(this.context).inflate(R.layout.item_footer, parent, false);
            return new FooterViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            Forecast forecast = this.forecastList.get(position);
            Date today = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd", Locale.SIMPLIFIED_CHINESE);
            String date_string = simpleDateFormat.format(new Date(today.getTime()
                    - (position - 1) * 24 * 60 * 60 * 1000));

            pic = normalViewHolder.getPic();
            info = normalViewHolder.getInfo();
            temp = normalViewHolder.getTemp();
            date = normalViewHolder.getDate();
            background_img = normalViewHolder.getBackground_img();

            temp.setText(forecast.getLow() + "~" + forecast.getHigh());
            info.setText(forecast.getText());
            date.setText(forecast.getDate().substring(0, 6));
            initImg(pic, info.getText().toString());
            Glide.with(context).load("http://s.tu.ihuan.me/bgc/" + date_string + ".png")
                    .placeholder(getRandomImgPlaceHolder()).into(background_img);
//            Log.d(this.getClass().getSimpleName(), "currentPosition:" + holder.getAdapterPosition() + "oldPosition:" + holder.getOldPosition());
//            if (holder.getAdapterPosition() > holder.getOldPosition())
            if (holder.getAdapterPosition() > oldPosition)
                showAnim(holder.itemView, R.anim.anmi_forecast_list);
            oldPosition = holder.getAdapterPosition();
        } else if (holder instanceof FooterViewHolder) {
            //do nothing, right?
        }
    }

    @Override
    public int getItemCount() {
        return forecastList == null ? 0 : forecastList.size() + 1;//add a footerView
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == forecastList.size())
            return TYPE_FOOTER;
        else
            return TYPE_NORMAL;


    }

    public void setData(List<Forecast> forecastList) {
        this.forecastList = forecastList;
        notifyDataSetChanged();
    }

    public boolean hasData() {
        return !(forecastList == null || forecastList.isEmpty());
    }

    private void initImg(ImageView img, String info) {
        if (info.contains("Thunderstorms")) {
            img.setImageResource(R.drawable.thunderstorm);


        } else if (info.contains("Cloudy")) {
            img.setImageResource(R.drawable.cloud_sun);
        } else if (info.contains("Sunny")) {
            img.setImageResource(R.drawable.sunny);

        } else if (info.contains("Showers") || info.contains("Rain")) {
            img.setImageResource(R.drawable.rain3);
        } else if (info.contains("Breezy")) {
            img.setImageResource(R.drawable.wind);
        } else if (info.contains("snow")) {
            img.setImageResource(R.drawable.snow2);
        } else {
            img.setImageResource(R.drawable.sun);
        }

    }

    private int getRandomImgPlaceHolder() {
        int random = new Random().nextInt(2);
        if (random == 0) {
            return R.drawable.bg0;
        } else
            return R.drawable.back;
    }

    private void showAnim(View view, int animId) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), animId);
        view.startAnimation(animation);
    }

    class NormalViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView info;
        TextView date;
        TextView temp;
        ImageView background_img;

        public NormalViewHolder(View itemView, ImageView pic, TextView info, TextView date, TextView temp, ImageView background_img) {
            super(itemView);
            this.pic = pic;
            this.info = info;
            this.date = date;
            this.temp = temp;
            this.background_img = background_img;
        }

        public TextView getTemp() {
            return temp;
        }

        public ImageView getPic() {
            return pic;
        }

        public TextView getInfo() {
            return info;
        }

        public TextView getDate() {
            return date;
        }

        public ImageView getBackground_img() {
            return background_img;
        }

    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
