package com.example.epharmaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epharmaapp.adapter.ClientRequestAdapter;
import com.example.epharmaapp.adapter.ProductListAdapter;
import com.example.epharmaapp.pojo.ConfirmOrder;
import com.example.epharmaapp.pojo.EPharmaDetails;
import com.example.epharmaapp.pojo.PharmacyProductDetails;
import com.example.epharmaapp.product.AddNewProductActivity;
import com.example.epharmaapp.signin.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ProductListAdapter.ProductListAdapterListener, ClientRequestAdapter.ClientRequestAdapterListener {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private boolean isPermissionGranted = false;

    private Uri ImageUrl_main;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference productRef;
    private DatabaseReference requestRef;

    private Toolbar toolbar;

    private LoadingDialog dialog;

    private ImageView vendorImageIV;

    private TextView vendorNmeTV, vendorAddressTV, vendorEstablishTV, uploadPhotoTV, savePhotoTV;

    private RadioButton productRB, requestRB;

    private Button addNewProductBtn;

    private RecyclerView baseRecycler;

    private StorageReference storageReference;

    private String userID, photoLink, name, registrationNumber, owner, area , city, establishYear, phone, email, password;

    private ProductListAdapter productListAdapter;
    private ClientRequestAdapter requestAdapter;

    private List<PharmacyProductDetails> productList = new ArrayList<PharmacyProductDetails>();
    private List<ConfirmOrder> requestList = new ArrayList<ConfirmOrder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new LoadingDialog(MainActivity.this,"Loading...");
        dialog.show();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Pharmacy").child(user.getUid());
        productRef = userRef.child("Product");
        requestRef = userRef.child("Order");

        toolbar = (Toolbar) findViewById(R.id.e_pharmacy_vendor_toolbar_base);
        setSupportActionBar(toolbar);

        vendorImageIV = findViewById(R.id.vendor_image);
        vendorNmeTV = findViewById(R.id.vendor_name);
        vendorAddressTV = findViewById(R.id.vendor_address);
        vendorEstablishTV = findViewById(R.id.vendor_establish);
        uploadPhotoTV = findViewById(R.id.upload_photo);
        savePhotoTV = findViewById(R.id.save_photo);

        productRB = findViewById(R.id.productRB);
        requestRB = findViewById(R.id.requestRB);
        addNewProductBtn = findViewById(R.id.add_new_productBtn);

        baseRecycler = findViewById(R.id.product_and_request_base_recycler);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EPharmaDetails vd = dataSnapshot.child("Details").getValue(EPharmaDetails.class);

                userID = vd.getId();
                photoLink = vd.getImage();
                name = vd.getFarmName();
                registrationNumber = vd.getTinNumber();
                area = vd.getArea();
                city = vd.getCity();
                establishYear = vd.getEstablishYear();
                owner = vd.getOwner();
                phone = vd.getPhone();
                email = vd.getEmail();
                password = vd.getPassword();

                Picasso.get().load(Uri.parse(photoLink)).into(vendorImageIV);

                //vendorImageIV.setImageURI(Uri.parse(photoLink));
                vendorNmeTV.setText(name);
                vendorAddressTV.setText(area+", "+city);
                vendorEstablishTV.setText("Establish year: "+establishYear);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    PharmacyProductDetails pd = d.child("Details").getValue(PharmacyProductDetails.class);
                    productList.add(pd);
                }
                Collections.reverse(productList);
                productListAdapter = new ProductListAdapter(MainActivity.this, productList);
                LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                llm.setOrientation(RecyclerView.VERTICAL);
                baseRecycler.setLayoutManager(llm);
                baseRecycler.setAdapter(productListAdapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uploadPhotoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                if(isPermissionGranted){
                    openGallery();
                }else {
                    Toast.makeText(MainActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        savePhotoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                userRegistration();
            }
        });

        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewProductActivity.class);
                startActivity(intent);
                finish();
            }
        });

        productRB.setOnClickListener(this);
        requestRB.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify1", "notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseMessaging.getInstance().subscribeToTopic("pharmacy");
    }

    private void userRegistration() {
        storageReference = FirebaseStorage.getInstance().getReference();
        final Uri imageUri = ImageUrl_main;
        final StorageReference imageRef = storageReference.child("PharmacyVendorImage").child(imageUri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();
                photoLink = downloadUri.toString();
                EPharmaDetails details = new EPharmaDetails(userID, photoLink, name, registrationNumber, area, city, establishYear, owner, phone, email, password);
                userRef.child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE){
            ImageUrl_main = data.getData();
            vendorImageIV.setImageURI(ImageUrl_main);
            savePhotoTV.setVisibility(View.VISIBLE);
            uploadPhotoTV.setVisibility(View.GONE);
        }
    }

    private void checkPermission() {
        if ((ActivityCompat
                .checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(MainActivity.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },PERMISSION_CODE);

        }else {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==PERMISSION_CODE){

            if((grantResults[0] ==PackageManager.PERMISSION_GRANTED
                    && grantResults[1] ==PackageManager.PERMISSION_GRANTED
            )){
                isPermissionGranted = true;
            }else {
                checkPermission();
            }
        }
    }

    @Override
    public void onClick(View view) {
        dialog.show();
        if (view.getId() == R.id.productRB){
            addNewProductBtn.setVisibility(View.VISIBLE);
            productRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    productList.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        PharmacyProductDetails pd = d.child("Details").getValue(PharmacyProductDetails.class);
                        productList.add(pd);
                    }
                    Collections.reverse(productList);
                    productListAdapter = new ProductListAdapter(MainActivity.this, productList);
                    LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    baseRecycler.setLayoutManager(llm);
                    baseRecycler.setAdapter(productListAdapter);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if (view.getId() == R.id.requestRB){
            addNewProductBtn.setVisibility(View.GONE);

            //remove later
            requestRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    requestList.clear();

                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        ConfirmOrder cd = d.child("Details").getValue(ConfirmOrder.class);
                        requestList.add(cd);
                    }
                    Collections.reverse(requestList);
                    requestAdapter = new ClientRequestAdapter(MainActivity.this, requestList);
                    LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    baseRecycler.setLayoutManager(llm);
                    baseRecycler.setAdapter(requestAdapter);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            /*requestRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    requestList.clear();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        ClientRequest pd = d.child("Details").getValue(ClientRequest.class);
                        requestList.add(pd);
                    }
                    Collections.reverse(requestList);
                    requestAdapter = new ClientRequestAdapter(MainActivity.this, requestList);
                    LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                    llm.setOrientation(RecyclerView.VERTICAL);
                    baseRecycler.setLayoutManager(llm);
                    baseRecycler.setAdapter(requestAdapter);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_item,menu);
        return true;
    }

    public void log_out(MenuItem item){
        dialog.show();
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("device_token", "");
        rootRef.child("Users").child(user.getUid()).child("Token").updateChildren(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                dialog.dismiss();
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void getProductDetails(PharmacyProductDetails product) {
        Intent intent = new Intent(MainActivity.this, AddNewProductActivity.class);
        intent.putExtra("ProductDetails", product);
        startActivity(intent);
        finish();
    }

    @Override
    public void getDetails(ConfirmOrder confirmOrder) {
        Intent intent = new Intent(MainActivity.this, RequestDetailsActivity.class);
        intent.putExtra("OrderDetails", confirmOrder);
        startActivity(intent);
        finish();
    }
}
