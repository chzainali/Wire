package com.example.wire.user.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wire.R;
import com.example.wire.admin.AdminMainActivity;
import com.example.wire.auth.LoginActivity;
import com.example.wire.databinding.FragmentProfileBinding;
import com.example.wire.model.HelperClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    StorageReference storageReference;
    String name, email, phone, imageUri = "";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (HelperClass.users.getImage() != null){
            imageUri = HelperClass.users.getImage();
        }

        if (!HelperClass.users.getImage().isEmpty()){
            Glide.with(this)
                    .asBitmap()
                    .load(Uri.parse(HelperClass.users.getImage()))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Set the Bitmap as the image resource.
                            binding.ivProfile.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Called when the Drawable is cleared, for example, when the view is recycled.
                        }
                    });
        }

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");

        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), 123);
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidated()){
                    updateProfile();
                }
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finishAffinity();
            }
        });

    }

    private void updateProfile() {
        progressDialog.show();
        if (imageUri.equals(HelperClass.users.getImage())){
            Map<String, Object> update = new HashMap<String, Object>();
            update.put("name", name);
            update.put("phone", phone);
            dbRefUsers.child(auth.getCurrentUser().getUid()).updateChildren(update).addOnCompleteListener(task -> {
                HelperClass.users.setName(name);
                HelperClass.users.setPhone(phone);
                progressDialog.dismiss();
                showMessage("Successfully Saved");
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
                update.put("name", name);
                update.put("phone", phone);
                dbRefUsers.child(auth.getCurrentUser().getUid()).updateChildren(update).addOnCompleteListener(task -> {
                    HelperClass.users.setName(name);
                    HelperClass.users.setPhone(phone);
                    HelperClass.users.setImage(downloadUri);
                    progressDialog.dismiss();
                    showMessage("Successfully Saved");
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

    @Override
    public void onResume() {
        super.onResume();
        if (HelperClass.users != null){
            if (!HelperClass.users.getName().isEmpty()){
                binding.userNameEt.setText(HelperClass.users.getName());
            }

            if (!HelperClass.users.getEmail().isEmpty()){
                binding.emailEt.setText(HelperClass.users.getEmail());
            }

            if (!HelperClass.users.getPhone().isEmpty()){
                binding.phoneEt.setText(HelperClass.users.getPhone());
            }

        }
    }

    private Boolean isValidated() {
        name = binding.userNameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Please enter userName");
            return false;
        }
        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (phone.isEmpty()) {
            showMessage("Please enter phone");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData().toString();
            binding.ivProfile.setImageURI(data.getData());
        }
    }

}