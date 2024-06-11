package com.example.wire.admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wire.R;
import com.example.wire.databinding.ActivityAddMachineBinding;
import com.example.wire.databinding.ActivityMachinesBinding;
import com.example.wire.model.MachineModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddMachineActivity extends AppCompatActivity {
    ActivityAddMachineBinding binding;
    String machineName, brandName, weightCapacity, color, price, details, imageUri = "";
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefMachines;
    StorageReference storageReference;
    MachineModel machineModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMachineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null){
            machineModel = (MachineModel) getIntent().getSerializableExtra("data");
            if (machineModel != null){
                binding.nameEt.setText(machineModel.getMachineName());
                binding.brandNameEt.setText(machineModel.getBrandName());
                binding.capacityEt.setText(machineModel.getWeightCapacity());
                binding.colorEt.setText(machineModel.getColor());
                binding.priceEt.setText(machineModel.getPrice());
                binding.detailsEt.setText(machineModel.getDetails());
                imageUri = machineModel.getImage();
                Glide.with(binding.ivMachine).load(machineModel.getImage()).into(binding.ivMachine);
                binding.tvHeading.setText("Update Washing Machine");
                binding.btnSave.setText("Update");
                binding.btnDelete.setVisibility(View.VISIBLE);
            }else{
                binding.btnDelete.setVisibility(View.GONE);
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        dbRefMachines = FirebaseDatabase.getInstance().getReference("Machines");
        storageReference = FirebaseStorage.getInstance().getReference("MachinesPictures");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        binding.ivMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select Machine Image"), 123);
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidated()) {
                    if (machineModel != null){
                        updateMachine();
                    }else{
                        addMachine();
                    }
                }
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRefMachines.child(machineModel.getMachineId()).removeValue();
                showMessage("Successfully Deleted");
                finish();
            }
        });

    }

    private void updateMachine() {
        if (imageUri.equals(machineModel.getImage())){
            Map<String, Object> update = new HashMap<String, Object>();
            update.put("machineName", machineName);
            update.put("brandName", brandName);
            update.put("weightCapacity", weightCapacity);
            update.put("color", color);
            update.put("price", price);
            update.put("details", details);
            dbRefMachines.child(machineModel.getMachineId()).updateChildren(update).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                showMessage("Successfully Saved");
                finish();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getMessage());
            });
        }
        else{
            Uri uriImage = Uri.parse(imageUri);
            StorageReference imageRef = storageReference.child(uriImage.getLastPathSegment());
            imageRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUri = uri.toString();
                Map<String, Object> update = new HashMap<>();
                update.put("image", downloadUri);
                update.put("machineName", machineName);
                update.put("brandName", brandName);
                update.put("weightCapacity", weightCapacity);
                update.put("color", color);
                update.put("price", price);
                update.put("details", details);
                dbRefMachines.child(machineModel.getMachineId()).updateChildren(update).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    showMessage("Successfully Saved");
                    finish();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getMessage());
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            })).addOnFailureListener(e -> {
                progressDialog.dismiss();
                showMessage(e.getLocalizedMessage());
            });
        }
    }

    private void addMachine() {
        // Display a progress dialog to indicate the addition process
        progressDialog.show();

        // Parse the image URI to a Uri object
        Uri uriImage = Uri.parse(imageUri);

        // Create a reference to store the image in Firebase Storage
        StorageReference imageRef = storageReference.child(uriImage.getLastPathSegment());

        imageRef.putFile(uriImage).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

            String downloadUri1 = uri.toString();

            String machineId = dbRefMachines.push().getKey();

            MachineModel model = new MachineModel(machineId, machineName, brandName, weightCapacity, color, price, details, downloadUri1, "Available", "", "", "");

            dbRefMachines.child(machineId).setValue(model).addOnSuccessListener(unused -> {

                showMessage("Added Successfully");

                progressDialog.dismiss();

                // Finish the current activity
                finish();
            }).addOnFailureListener(e -> {
                // If setting data in database fails

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Show an error message indicating the failure reason
                showMessage(e.getLocalizedMessage());
            });

        }).addOnFailureListener(e -> {
            // If getting download URL fails

            // Dismiss the progress dialog
            progressDialog.dismiss();

            // Show an error message indicating the failure reason
            showMessage(e.getLocalizedMessage());
        })).addOnFailureListener(e -> {
            // If uploading image file fails

            // Dismiss the progress dialog
            progressDialog.dismiss();

            // Show an error message indicating the failure reason
            showMessage(e.getLocalizedMessage());
        });
    }

    private Boolean isValidated() {
        machineName = binding.nameEt.getText().toString().trim();
        brandName = binding.brandNameEt.getText().toString().trim();
        weightCapacity = binding.capacityEt.getText().toString().trim();
        color = binding.colorEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        details = binding.detailsEt.getText().toString().trim();

        if (imageUri.isEmpty()) {
            showMessage("Please choose machine picture");
            return false;
        }

        if (machineName.isEmpty()) {
            showMessage("Please enter machine name");
            return false;
        }

        if (brandName.isEmpty()) {
            showMessage("Please enter brand name");
            return false;
        }

        if (weightCapacity.isEmpty()) {
            showMessage("Please enter weight capacity");
            return false;
        }

        if (color.isEmpty()) {
            showMessage("Please enter machine color");
            return false;
        }

        if (price.isEmpty()) {
            showMessage("Please enter machine price");
            return false;
        }

        if (details.isEmpty()) {
            showMessage("Please enter details");
            return false;
        }


        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData().toString();
            binding.ivMachine.setImageURI(data.getData());
        }
    }

}