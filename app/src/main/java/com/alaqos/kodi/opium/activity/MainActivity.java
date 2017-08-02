package com.alaqos.kodi.opium.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alaqos.kodi.opium.R;
import com.alaqos.kodi.opium.adapter.OrdersAdapter;
import com.alaqos.kodi.opium.helper.DividerItemDecoration;
import com.alaqos.kodi.opium.helper.SessionManager;
import com.alaqos.kodi.opium.model.Order;
import com.alaqos.kodi.opium.network.ApiClient;
import com.alaqos.kodi.opium.network.ApiInterface;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener {
    private ArrayList<Order> orders = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrdersAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(MainActivity.this, OrderEditActivity.class);
            startActivity(i);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new OrdersAdapter(this, orders);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new OrdersAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView,
                new OrdersAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                Order order = orders.get(position);
                mAdapter.notifyDataSetChanged();

                Intent i = new Intent(MainActivity.this, OrderDetailActivity.class);
                i.putExtra("order", new Gson().toJson(order));
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
                    }
                }
        );
    }

    /**
     * Fetches mail messages by making HTTP request
     * url: http://api.androidhive.info/json/inbox.json
     */
    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Map options = new HashMap();
        options.put("from", userId);
        Call<List<Order>> call = apiService.getInbox(options);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                // clear the inbox
                orders.clear();

                // add all the messages

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                for (Order order : response.body()) {
                    // generate a random color
                    orders.add(order);
                }

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Search...", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Search...", Toast.LENGTH_SHORT).show();
            confirmMessage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getInbox();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        getInbox();
    }

    public void confirmMessage() {
        DialogFragment newFragment = new AlertDialogFragment();
        newFragment.show(getSupportFragmentManager(), "missiles");
    }


    public static class AlertDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_msg)
                    .setPositiveButton(R.string.yes_text, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            SessionManager sessionManager = new SessionManager(getActivity());
                            sessionManager.logoutUser();
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(R.string.no_text, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}