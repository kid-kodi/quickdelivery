package com.alaqos.kodi.opium.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alaqos.kodi.opium.R;
import com.alaqos.kodi.opium.helper.CircleTransform;
import com.alaqos.kodi.opium.model.Order;
import com.alaqos.kodi.opium.network.ApiClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Order> OrderArrayList;
    private static String today;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reference, montant, timestamp;
        public ImageView picture, status_pending, status_done;

        public ViewHolder(View view) {
            super(view);
            reference = (TextView) view.findViewById(R.id.reference);
            montant = (TextView) view.findViewById(R.id.montant);
            timestamp = (TextView) view.findViewById(R.id.timestamp);

            picture = (ImageView) view.findViewById(R.id.picture);
            status_pending = (ImageView) view.findViewById(R.id.status_pending);
            status_done = (ImageView) view.findViewById(R.id.status_done);
        }
    }


    public OrdersAdapter(Context mContext, ArrayList<Order> OrderArrayList) {
        this.mContext = mContext;
        this.OrderArrayList = OrderArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order Order = OrderArrayList.get(position);

        Log.v("####",ApiClient.BASE_URL+Order.getPicture());

        Glide.with(mContext).load(ApiClient.BASE_URL+"/"+Order.getPicture())
                .thumbnail(0.5f)
                .crossFade()
                .transform(new CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.picture);

        holder.reference.setText(Order.getReference());
        holder.montant.setText(Order.getMontant()+" F.CFA");
        if (Order.isStatus()) {
            holder.status_done.setVisibility(View.VISIBLE);
            holder.status_pending.setVisibility(View.GONE);
        } else {
            holder.status_pending.setVisibility(View.VISIBLE);
            holder.status_done.setVisibility(View.GONE);
        }

        holder.timestamp.setText(getTimeStamp(Order.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return OrderArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private OrdersAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final OrdersAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}