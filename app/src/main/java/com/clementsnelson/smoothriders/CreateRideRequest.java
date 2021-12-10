package com.clementsnelson.smoothriders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smoothriders.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CreateRideRequest extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextDescription, editTextTime, editTextPickupLocation, editTextDestinationLocation, editTextRideTip;
    private Button createRideRequestBtn, logoutButtonBtn;
    private ProgressBar progressBar;
    private Calendar calendar;
    private int currentHour,currentMinute;
    private String amPm;
    private TimePickerDialog timePickerDialog;

    private FirebaseFirestore mDbb = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ride_request);

        mAuth = FirebaseAuth.getInstance();

        // EditText obj references
        editTextDescription = findViewById(R.id.rideDescription);
        editTextTime = findViewById(R.id.rideTime);
        editTextPickupLocation = findViewById(R.id.pickupLocation);
        editTextDestinationLocation = findViewById(R.id.destinationLocation);
        editTextRideTip = findViewById(R.id.rideTip);

        // Button obj reference
        createRideRequestBtn = findViewById(R.id.createRequest);
        // Set onClick listener for button
        createRideRequestBtn.setOnClickListener(this);

        // Logout button obj reference
        logoutButtonBtn = findViewById(R.id.logoutButton);
        // Set onClick listener for button
        logoutButtonBtn.setOnClickListener(this);

        // Create onClick listener for editTextTime
        editTextTime.setOnClickListener(this);

        // Progress bar obj reference
        progressBar = findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createRequest:
                createRideRequest();
                break;
            case R.id.logoutButton: // If user clicks the logout button
                logoutUser();
                break;
            case R.id.rideTime:
                getRideTime();
                break;
        }
    }

    private void getRideTime() {
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(CreateRideRequest.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                if (hourOfDay >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }
                editTextTime.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
            }
        }, currentHour, currentMinute, false) ;

        timePickerDialog.show();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(CreateRideRequest.this, MainActivity.class));
    }

    private void createRideRequest() {
        // Get string values from edit text fields on create request button click
        String rideDescription = editTextDescription.getText().toString().trim();
        String rideTime = editTextTime.getText().toString().trim();
        String pickupLocation = editTextPickupLocation.getText().toString().trim();
        String destinationLocation = editTextDestinationLocation.getText().toString().trim();
        String rideTip = editTextRideTip.getText().toString().trim();
        String requesterEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().trim();
        String driverEmail = "";
        String rideId = "";
        Boolean isAccepted = false;

        // Ride request validation
        if (rideDescription.isEmpty()) {
            editTextDescription.setError("A description is required!");
            editTextDescription.requestFocus();
            return;
        }

        if (rideTime.isEmpty()) {
            editTextTime.setError("A time is required!");
            editTextTime.requestFocus();
            return;
        }

        if (pickupLocation.isEmpty()) {
            editTextPickupLocation.setError("A pickup location is required!");
            editTextPickupLocation.requestFocus();
            return;
        }

        if (destinationLocation.isEmpty()) {
            editTextDestinationLocation.setError("A destination location is required!");
            editTextDescription.requestFocus();
            return;
        }

        // If form is valid set the progress bar to visible
        progressBar.setVisibility(View.VISIBLE);

        Ride ride = new Ride(rideDescription, rideTime, pickupLocation, destinationLocation,
                rideTip, requesterEmail, driverEmail, rideId, isAccepted);

        FirebaseDatabase.getInstance().getReference("request")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(ride).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // If ride data is sent to the Firebase database
                if (task.isSuccessful()) {
                    mDbb.collection("Rides").add(ride).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            // Get reference to Ride document in Firestore
                            DocumentReference rideRef = task.getResult();

                            // Get Document ID
                            String rideId = rideRef.getId();

                            // Update rideId field in Ride document
                            rideRef.update("rideId", rideId);

                            // Send toast to user if Ride is added successfully
                            Toast.makeText(CreateRideRequest.this, "Ride has been created successfully!", Toast.LENGTH_LONG).show();
                        }
                    });
                    // If ride is created redirect to profile activity
                    startActivity(new Intent(CreateRideRequest.this, ProfileActivity.class));
                    progressBar.setVisibility(View.GONE);
                }
                // If ride is not added
                else {
                    Toast.makeText(CreateRideRequest.this, "Ride failed to create. Try Again!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    // Method to alter the default behavior of the back button in Android
    @Override
    public void onBackPressed() {
        // If user clicks the back button take them to them to the Profile Activity view
        startActivity(new Intent(CreateRideRequest.this, ProfileActivity.class));
    } // end of onBackPressed method
}