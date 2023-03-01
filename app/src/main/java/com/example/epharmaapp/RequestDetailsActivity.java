package com.example.epharmaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epharmaapp.adapter.OrderListAdapter;
import com.example.epharmaapp.pojo.ConfirmOrder;
import com.example.epharmaapp.pojo.OrderChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestDetailsActivity extends AppCompatActivity {

    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference confirmVendorRef;
    private DatabaseReference confirmClientRef;

    private ConfirmOrder cd;

    private TextView nameTV, statusTV, addressTV, phoneTV, emailTV, priceTV, totalTV;

    private Button confirmBtn;

    private RecyclerView productRecycler;

    private LinearLayout callLO, emailLO;

    private List<OrderChart> chartList = new ArrayList<OrderChart>();

    private OrderListAdapter adapter;

    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        cd = (ConfirmOrder) getIntent().getSerializableExtra("OrderDetails");
        chartList = cd.getChartList();

        dialog = new LoadingDialog(RequestDetailsActivity.this, "Loading...");

        user = FirebaseAuth.getInstance().getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        confirmVendorRef = rootRef.child("Pharmacy").child(cd.getePharmaDetails().getId()).child("Order").child(cd.getOrderId()).child("Details");
        confirmClientRef = rootRef.child("Patient").child(cd.getPatientDetails().getPatient_id()).child("Pharmacy").child("Order").child(cd.getOrderId()).child("Details");

        Collections.reverse(chartList);

        nameTV = findViewById(R.id.request_details_user_name_tv);
        statusTV = findViewById(R.id.req_details_status_tv);
        addressTV = findViewById(R.id.req_details_user_address_tv);
        phoneTV = findViewById(R.id.req_details_user_phone_tv);
        emailTV = findViewById(R.id.req_details_user_email_tv);
        priceTV = findViewById(R.id.showPrice);
        totalTV = findViewById(R.id.showPriceF);

        confirmBtn = findViewById(R.id.confirm_order_btn);

        productRecycler = findViewById(R.id.request_details_product_recycler);

        callLO = findViewById(R.id.req_details_user_callLO);
        emailLO = findViewById(R.id.req_details_user_mailLO);

        if (cd.getStatus().equals("Pending")){
            confirmBtn.setVisibility(View.VISIBLE);
        }

        nameTV.setText("Mr. "+cd.getPatientDetails().getFirstName()+" "+cd.getPatientDetails().getLastName());
        statusTV.setText("Status: order is "+cd.getStatus());
        addressTV.setText("Address: "+cd.getPatientDetails().getArea()+", "+cd.getPatientDetails().getCity());
        phoneTV.setText(cd.getPatientDetails().getPhone());
        emailTV.setText(cd.getPatientDetails().getEmail());
        priceTV.setText(" "+cd.getTotalPrice()+"Tk.");
        totalTV.setText(" "+cd.getTotalPlusVat()+"Tk.");

        adapter = new OrderListAdapter(RequestDetailsActivity.this, chartList);
        LinearLayoutManager llm = new LinearLayoutManager(RequestDetailsActivity.this);
        llm.setOrientation(RecyclerView.VERTICAL);
        productRecycler.setLayoutManager(llm);
        productRecycler.setAdapter(adapter);

        callLO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "tel:" + cd.getPatientDetails().getPhone().trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        return;
                    }
                }
                startActivity(intent);
            }
        });

        emailLO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:"+cd.getPatientDetails().getEmail());
                intent.setData(data);
                startActivity(intent);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                cd.setStatus("Delivering");
                confirmClientRef.setValue(cd).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        confirmVendorRef.setValue(cd).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Map<String, Object> tokenMap = new HashMap<>();
                                tokenMap.put("sms", "You order is on the way");
                                tokenMap.put("from", String.valueOf(user.getUid()));
                                tokenMap.put("title", "Order delivering");
                                tokenMap.put("to", cd.getPatientDetails().getPatient_id());
                                rootRef.child("Notifications").child(cd.getPatientDetails().getPatient_id()).child(rootRef.push().getKey()).setValue(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(RequestDetailsActivity.this, MainActivity.class);
                                        Toast.makeText(RequestDetailsActivity.this, "Order delivering start...", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RequestDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
