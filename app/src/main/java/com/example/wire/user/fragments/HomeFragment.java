package com.example.wire.user.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wire.R;
import com.example.wire.adapter.MachinesAdapter;
import com.example.wire.admin.AddMachineActivity;
import com.example.wire.admin.AdminMainActivity;
import com.example.wire.admin.MachinesActivity;
import com.example.wire.admin.NotificationsActivity;
import com.example.wire.databinding.FragmentHomeBinding;
import com.example.wire.model.MachineModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefMachines;
    List<MachineModel> list = new ArrayList<>();
    MachinesAdapter adapter;

    public HomeFragment() {
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
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

        binding.ivNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), NotificationsActivity.class);
                intent.putExtra("from", "user");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        dbRefMachines.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            MachineModel model = ds.getValue(MachineModel.class);
                            list.add(model);
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();

                    if (list.isEmpty()) {
                        binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                        binding.rvGoals.setVisibility(View.GONE);
                    } else {
                        binding.tvNoGoalFound.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressDialog.dismiss();
                    binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                    binding.rvGoals.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter() {
        adapter = new MachinesAdapter(requireContext(), list, pos -> {
            MachineModel model = list.get(pos);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", model);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_navigation_home_to_machineDetailsFragment, bundle);
        });
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGoals.setAdapter(adapter);
    }

}