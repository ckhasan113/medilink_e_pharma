package com.example.epharmaapp.product;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epharmaapp.LoadingDialog;
import com.example.epharmaapp.MainActivity;
import com.example.epharmaapp.R;
import com.example.epharmaapp.pojo.EPharmaDetails;
import com.example.epharmaapp.pojo.PharmacyProductDetails;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewProductActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private StorageReference storageReference;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private FirebaseAuth auth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference productRef;

    private CircleImageView addImageBtn;
    private EditText productEdt, companyEdt, priceEdt;
    private TextView expiredTV;
    private Button addProductBtn, updateBtn, removeBtn;

    private LoadingDialog dialog;

    private String id, image, name, company, price, expiredDate = "";

    private Uri ImageUrl_main = null;

    private boolean isPermissionGranted = false;
    private boolean isUpdate = false;

    private PharmacyProductDetails product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        product = (PharmacyProductDetails) getIntent().getSerializableExtra("ProductDetails");

        dialog = new LoadingDialog(AddNewProductActivity.this, "Loading...");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rootRef = FirebaseDatabase.getInstance().getReference();
        productRef = rootRef.child("Pharmacy").child(user.getUid()).child("Product");

        addImageBtn = findViewById(R.id.addProductImageBtn);
        productEdt = findViewById(R.id.productName);
        companyEdt = findViewById(R.id.productCompany);
        priceEdt = findViewById(R.id.productPrice);
        expiredTV = findViewById(R.id.expired_date_TV);
        addProductBtn = findViewById(R.id.add_new_product_info);
        updateBtn = findViewById(R.id.update_product_info);
        removeBtn = findViewById(R.id.remove_product_info);

        if (product != null){
            id = product.getId();
            image = product.getImage();
            name = product.getName();
            company = product.getCompany();
            price = product.getPrice();
            expiredDate = product.getExpiredDate();

            Picasso.get().load(Uri.parse(image)).into(addImageBtn);
            productEdt.setText(name);
            companyEdt.setText(company);
            priceEdt.setText(price);
            expiredTV.setText(expiredDate);

            updateBtn.setVisibility(View.VISIBLE);
            removeBtn.setVisibility(View.VISIBLE);
            addProductBtn.setVisibility(View.GONE);
            isUpdate = true;
        }

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                if(isPermissionGranted){
                    openGallery();
                }else {
                    Toast.makeText(AddNewProductActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageUrl_main == null){
                    Toast.makeText(AddNewProductActivity.this, "Select product image", Toast.LENGTH_SHORT).show();
                    return;
                }

                name = productEdt.getText().toString().trim();
                if (name.isEmpty()){
                    productEdt.setError(getString(R.string.required_field));
                    productEdt.requestFocus();
                    return;
                }

                company = companyEdt.getText().toString().trim();
                if (company.isEmpty()){
                    companyEdt.setError(getString(R.string.required_field));
                    companyEdt.requestFocus();
                    return;
                }

                price = priceEdt.getText().toString().trim();
                if (price.isEmpty()){
                    priceEdt.setError(getString(R.string.required_field));
                    priceEdt.requestFocus();
                    return;
                }

                if (expiredDate.isEmpty()){
                    Toast.makeText(AddNewProductActivity.this, "Select product expired date", Toast.LENGTH_SHORT).show();
                    return;
                }

                id = productRef.push().getKey();
                dialog.show();
                addProductInfo();
            }
        });

        expiredTV.setOnClickListener(this);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                name = productEdt.getText().toString().trim();
                company = companyEdt.getText().toString().trim();
                price = priceEdt.getText().toString().trim();
                addProductInfo();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                productRef.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        Toast.makeText(AddNewProductActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNewProductActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private void addProductInfo() {

        try {
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
                    image = downloadUri.toString();
                    PharmacyProductDetails pd = new PharmacyProductDetails(id, image, name, company, price, expiredDate);

                    productRef.child(id).child("Details").setValue(pd).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(AddNewProductActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddNewProductActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                dialog.dismiss();
                                Toast.makeText(AddNewProductActivity.this, "Failed....", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            });
        }catch (NullPointerException e){
            PharmacyProductDetails pd = new PharmacyProductDetails(id, image, name, company, price, expiredDate);

            productRef.child(id).child("Details").setValue(pd).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dialog.dismiss();
                        Toast.makeText(AddNewProductActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNewProductActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(AddNewProductActivity.this, "Failed....", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

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
            addImageBtn.setImageURI(ImageUrl_main);
        }
    }

    private void checkPermission() {
        if ((ActivityCompat
                .checkSelfPermission(AddNewProductActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(AddNewProductActivity.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(AddNewProductActivity.this,
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
    public void onBackPressed() {
        Intent intent = new Intent(AddNewProductActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        final Calendar calendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                expiredDate = simpleDateFormat.format(calendar.getTime());
                expiredTV.setText(expiredDate);
            }

        };

        new DatePickerDialog(AddNewProductActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
