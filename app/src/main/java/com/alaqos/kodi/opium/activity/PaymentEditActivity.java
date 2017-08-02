package com.alaqos.kodi.opium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alaqos.kodi.opium.R;
import com.alaqos.kodi.opium.model.Order;
import com.alaqos.kodi.opium.model.Payment;
import com.alaqos.kodi.opium.network.ApiClient;
import com.alaqos.kodi.opium.network.OrderApiInterface;
import com.alaqos.kodi.opium.network.PaymentApiInterface;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentEditActivity extends AppCompatActivity {

    private EditText inputMontant;
    private EditText inputLibelle;
    private Spinner spinnerPaiementModeId;
    private EditText inputReference;
    private EditText inputLocation;
    private String _extras;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_edit);

        Bundle extras = getIntent().getExtras();
        if (extras != null ){
            _extras = extras.getString("order");
            order = new Gson().fromJson(_extras, Order.class);
        }

        inputMontant = (EditText) findViewById(R.id.input_montant);
        inputLibelle = (EditText) findViewById(R.id.input_libelle);

        spinnerPaiementModeId = (Spinner) findViewById(R.id.spinner_paiementModeId);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerPaiementModeId.setAdapter(adapter);

        inputLocation = (EditText) findViewById(R.id.input_location);
        inputReference = (EditText) findViewById(R.id.input_reference);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {

            if (!validateMontant()) {
                return false;
            }

            Payment payment = new Payment();
            payment.setOrderId(order.get_id());
            payment.setMontant(Integer.parseInt(inputMontant.getText().toString()));
            payment.setLieu(inputLocation.getText().toString());
            payment.setLibelle(inputLibelle.getText().toString());
            payment.setPaiementModeId(spinnerPaiementModeId.getSelectedItem().toString());
            payment.setReference(inputReference.getText().toString());
            payment.setCreatedAt(Today());
            payment.setStatus(false);
            createOrder(payment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateMontant() {
        int montant = Integer.parseInt(inputMontant.getText().toString());
        if (inputMontant.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,"Le montant est requis",Toast.LENGTH_LONG).show();
            return false;
        }
        if (montant>order.getReste()) {
            Toast.makeText(this,"Vérifier le montant du paiement",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void createOrder(final Payment payment){
        PaymentApiInterface apiService =
                ApiClient.getClient().create(PaymentApiInterface.class);

        Call<Payment> call = apiService.create(
                payment.getOrderId(), payment.getMontant(), payment.getPaiementModeId(),
                payment.getLibelle(),payment.getLieu(), payment.getReference(), payment.getCreatedAt(), payment.isStatus());
        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                // clear the inbox
                Toast.makeText(PaymentEditActivity.this, "Data saved", Toast.LENGTH_LONG).show();
                //order.setPaye(order.getPaye() + payment.getMontant());
                //order.setReste(order.getReste() - payment.getMontant());
                //updateOrder(order);
                PaymentEditActivity.this.finish();
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                Toast.makeText(PaymentEditActivity.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateOrder(Order order){
        OrderApiInterface apiService =
                ApiClient.getClient().create(OrderApiInterface.class);

        Call<Order> call = apiService.update(
                order.get_id(),
                order.getFrom(),
                order.getReference(),
                order.getMontant(),
                order.getPaye(),
                order.getReste(),
                Today(),
                order.getPicture(), false);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                // clear the inbox
                Toast.makeText(PaymentEditActivity.this, "Data saved", Toast.LENGTH_LONG).show();
                PaymentEditActivity.this.finish();
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(PaymentEditActivity.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public String Today(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
