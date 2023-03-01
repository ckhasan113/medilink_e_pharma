package com.example.epharmaapp.signin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.epharmaapp.LoadingDialog;
import com.example.epharmaapp.MainActivity;
import com.example.epharmaapp.R;
import com.example.epharmaapp.pojo.EPharmaDetails;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 848;
    private static final int PERMISSION_CODE = 8972;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private DatabaseReference rootRef;
    private DatabaseReference userRef;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private CircleImageView addImageBtn;

    private EditText farmNameEdt, tinEdt, areaEdt, cityEdt, establishYearEdt, ownerNameEdt, phoneEdt, emailEdt, passwordEdt, rePassEdt;

    private CardView registrationBnt;

    private String id, image, farmName, tinNumber, area, city, establishYear, ownerName, phone, email, password, rePass;

    private Uri ImageUrl_main = null;

    private boolean isPermissionGranted = false;

    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dialog = new LoadingDialog(RegistrationActivity.this, "Loading...");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        addImageBtn = findViewById(R.id.addImageBtn);
        farmNameEdt = findViewById(R.id.eFarm_vendor_name);
        tinEdt = findViewById(R.id.tinNumber);
        areaEdt = findViewById(R.id.eFarm_vendor_area);
        cityEdt = findViewById(R.id.eFarm_vendor_city);
        establishYearEdt = findViewById(R.id.eFarm_vendor_establish_year);
        ownerNameEdt = findViewById(R.id.eFarm_vendor_owner_name);
        phoneEdt = findViewById(R.id.eFarm_vendor_phone_number);
        emailEdt = findViewById(R.id.eFarm_vendor_email);
        passwordEdt = findViewById(R.id.eFarm_vendor_password);
        rePassEdt = findViewById(R.id.re_password);
        registrationBnt = findViewById(R.id.eFarm_vendor_register);

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                if(isPermissionGranted){
                    openGallery();
                }else {
                    Toast.makeText(RegistrationActivity.this, "Please Allow Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registrationBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ImageUrl_main == null){
                    Toast.makeText(RegistrationActivity.this, "Select an image for your Pharmacy", Toast.LENGTH_SHORT).show();
                    return;
                }

                farmName = farmNameEdt.getText().toString().trim();
                if (farmName.isEmpty()){
                    farmNameEdt.setError(getString(R.string.required_field));
                    farmNameEdt.requestFocus();
                    return;
                }

                tinNumber = tinEdt.getText().toString().trim();
                if (tinNumber.isEmpty()){
                    tinEdt.setError(getString(R.string.required_field));
                    tinEdt.requestFocus();
                    return;
                }

                area = areaEdt.getText().toString().trim();
                if (area.isEmpty()){
                    areaEdt.setError(getString(R.string.required_field));
                    areaEdt.requestFocus();
                    return;
                }

                city = cityEdt.getText().toString().trim();
                if (city.isEmpty()){
                    cityEdt.setError(getString(R.string.required_field));
                    cityEdt.requestFocus();
                    return;
                }

                establishYear = establishYearEdt.getText().toString().trim();
                if (establishYear.isEmpty()){
                    establishYearEdt.setError(getString(R.string.required_field));
                    establishYearEdt.requestFocus();
                    return;
                }

                ownerName = ownerNameEdt.getText().toString().trim();
                if (ownerName.isEmpty()){
                    ownerNameEdt.setError(getString(R.string.required_field));
                    ownerNameEdt.requestFocus();
                    return;
                }

                phone = phoneEdt.getText().toString().trim();
                if (phone.isEmpty()){
                    phoneEdt.setError(getString(R.string.required_field));
                    phoneEdt.requestFocus();
                    return;
                }

                email = emailEdt.getText().toString().trim();
                if (email.isEmpty()){
                    emailEdt.setError(getString(R.string.required_field));
                    emailEdt.requestFocus();
                    return;
                }

                password = passwordEdt.getText().toString().trim()+"pharmacy";
                if (password.isEmpty()){
                    passwordEdt.setError(getString(R.string.required_field));
                    passwordEdt.requestFocus();
                    return;
                }

                rePass = rePassEdt.getText().toString().trim()+"pharmacy";
                if (rePass.isEmpty()){
                    rePassEdt.setError(getString(R.string.required_field));
                    rePassEdt.requestFocus();
                    return;
                }

                if (!(password.equals(rePass))){
                    rePassEdt.setError(getString(R.string.required_field));
                    rePassEdt.requestFocus();
                    return;
                }

                dialog.show();

                registerUser();
            }
        });
    }

    private void registerUser() {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                firebaseAuth = FirebaseAuth.getInstance();
                                rootRef = FirebaseDatabase.getInstance().getReference();
                                user = firebaseAuth.getCurrentUser();
                                userRef = rootRef.child("Pharmacy").child(user.getUid());
                                id = user.getUid();
                                Toast.makeText(RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                                userRegistrationData();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(RegistrationActivity.this, "Failed to register...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void userRegistrationData() {
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
                EPharmaDetails details = new EPharmaDetails(id, image, farmName, tinNumber, area, city, establishYear, ownerName, phone, email, password);
                userRef.child("Details").setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            String token = FirebaseInstanceId.getInstance().getToken();
                            Map<String, Object> tokenMap = new HashMap<>();
                            tokenMap.put("device_token", token);

                            rootRef.child("Users").child(user.getUid()).child("Token").setValue(tokenMap);

                            dialog.dismiss();
                            startActivity(intent);
                            finish();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Failed to registration", Toast.LENGTH_SHORT).show();

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
            addImageBtn.setImageURI(ImageUrl_main);
        }
    }

    private void checkPermission() {
        if ((ActivityCompat
                .checkSelfPermission(RegistrationActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) &&
                (ActivityCompat
                        .checkSelfPermission(RegistrationActivity.this
                                ,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(RegistrationActivity.this,
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
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
