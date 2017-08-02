package com.alaqos.kodi.opium.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alaqos.kodi.opium.R;
import com.alaqos.kodi.opium.adapter.OrdersAdapter;
import com.alaqos.kodi.opium.adapter.PaymentsAdapter;
import com.alaqos.kodi.opium.helper.DividerItemDecoration;
import com.alaqos.kodi.opium.helper.SessionManager;
import com.alaqos.kodi.opium.model.Order;
import com.alaqos.kodi.opium.model.Payment;
import com.alaqos.kodi.opium.network.ApiClient;
import com.alaqos.kodi.opium.network.ApiInterface;
import com.alaqos.kodi.opium.network.PaymentApiInterface;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private ArrayList<Payment> payments = new ArrayList<>();
    private RecyclerView recyclerView;
    private PaymentsAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;

    private String userId;
    private String orderId;
    private Order order;
    String _extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();

        Bundle extras = getIntent().getExtras();
        if (extras != null ){
            _extras = extras.getString("order");
            order = new Gson().fromJson(_extras, Order.class);

            TextView montantTxt = (TextView) findViewById(R.id.montant_txt);
            montantTxt.setText(order.getMontant()+" F.CFA");
            TextView payeTxt = (TextView) findViewById(R.id.paye);
            payeTxt.setText(order.getPaye()+" F.CFA");
            TextView resteTxt = (TextView) findViewById(R.id.reste);
            resteTxt.setText(order.getReste()+" F.CFA");
        }



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(OrderDetailActivity.this);
        emptyView = (TextView) findViewById(R.id.empty_view);

        mAdapter = new PaymentsAdapter(this, payments);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
                    }
                }
        );

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (order.isStatus()){
            menu.findItem(R.id.action_new).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {
            Intent i = new Intent(OrderDetailActivity.this, PaymentEditActivity.class);
            i.putExtra("order", new Gson().toJson(order));
            startActivity(i);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetches mail messages by making HTTP request
     * url: http://api.androidhive.info/json/inbox.json
     */
    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);
        PaymentApiInterface apiService =
                ApiClient.getClient().create(PaymentApiInterface.class);

        Map options = new HashMap();
        options.put("orderId", order.get_id());
        Call<List<Payment>> call = apiService.getList(options);
        call.enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                // clear the inbox
                payments.clear();

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                for (Payment payment : response.body()) {
                    // generate a random color
                    payments.add(payment);
                }

                if (payments.size()==0) {
                    Toast.makeText(OrderDetailActivity.this,"##",Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(OrderDetailActivity.this,"#",Toast.LENGTH_LONG).show();
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        getInbox();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        getInbox();
    }
}
