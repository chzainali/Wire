package com.example.wire.user.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.transition.ChangeImageTransform;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wire.R;
import com.example.wire.admin.ChatActivity;
import com.example.wire.databinding.FragmentHomeBinding;
import com.example.wire.databinding.FragmentMachineDetailsBinding;
import com.example.wire.model.MachineModel;
import com.example.wire.model.NotificationModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MachineDetailsFragment extends Fragment {
    private FragmentMachineDetailsBinding binding;
    MachineModel machineModel;
    String deliveryOption = "", address = "";
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefMachines, notificationRef;

    public MachineDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            machineModel = (MachineModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMachineDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefMachines = FirebaseDatabase.getInstance().getReference("Machines");
        notificationRef = FirebaseDatabase.getInstance().getReference("Notifications");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });

        if (machineModel != null) {
            if (Objects.equals(machineModel.getStatus(), "Available")) {
                binding.btnBook.setVisibility(View.VISIBLE);
                binding.tvStatus.setTextColor(requireActivity().getColor(R.color.green));
            } else {
                binding.btnBook.setVisibility(View.GONE);
                binding.tvStatus.setTextColor(requireActivity().getColor(R.color.red));
            }
            binding.tvStatus.setText(machineModel.getStatus());
            binding.nameEt.setText(machineModel.getMachineName());
            binding.brandNameEt.setText(machineModel.getBrandName());
            binding.capacityEt.setText(machineModel.getWeightCapacity());
            binding.colorEt.setText(machineModel.getColor());
            binding.priceEt.setText(machineModel.getPrice());
            binding.detailsEt.setText(machineModel.getDetails());
            Glide.with(binding.ivMachine).load(machineModel.getImage()).into(binding.ivMachine);
        }

        binding.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(machineModel.getStatus(), "Available")) {
                    showBookingDialog();
                }
            }
        });

        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("userId", "nAFegdLtsHeLvLLP5QQHMECXrue2");
                startActivity(intent);
            }
        });

    }
    public void showBookingDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_booking);
        deliveryOption = "";
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
        TextInputLayout layoutAddress = (TextInputLayout) dialog.findViewById(R.id.layoutAddress);
        TextInputEditText addressEt = (TextInputEditText) dialog.findViewById(R.id.addressEt);
        RadioButton rbCollection = (RadioButton) dialog.findViewById(R.id.rbCollection);
        RadioButton rbDelivery = (RadioButton) dialog.findViewById(R.id.rbDelivery);

        rbCollection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deliveryOption = "Collection";
                    layoutAddress.setVisibility(View.GONE);
                }
            }
        });

        rbDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deliveryOption = "Delivery";
                    layoutAddress.setVisibility(View.VISIBLE);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = addressEt.getText().toString();
                if (deliveryOption.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select collection or delivery", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (deliveryOption.equals("Delivery")) {
                    if (address.isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter delivery address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat;
                dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss", Locale.getDefault());
                String formattedDate = dateFormat.format(currentDate);
                progressDialog.show();
                Map<String, Object> update = new HashMap<>();
                update.put("status", "Booked");
                update.put("userId", auth.getCurrentUser().getUid());
                update.put("bookingDateTime", formattedDate);
                update.put("bookingAddress", address);
                dbRefMachines.child(machineModel.getMachineId()).updateChildren(update).addOnCompleteListener(task -> {
                    String id = notificationRef.push().getKey();
                    NotificationModel notificationModel = new NotificationModel(id, auth.getCurrentUser().getUid(), machineModel.getMachineId(),
                            machineModel.getMachineName() + " by " + machineModel.getBrandName() +
                                    " in " + machineModel.getColor() + " color has been booked for £" + machineModel.getPrice() + " via " + deliveryOption, formattedDate);
                    notificationRef.child(id).setValue(notificationModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            dialog.dismiss();
                            showMessage("Booked Successfully");
                            Navigation.findNavController(binding.getRoot()).navigateUp();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            showMessage(e.getMessage());
                        }
                    });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getMessage());
                });
            }
        });

        dialog.show();
    }

    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}