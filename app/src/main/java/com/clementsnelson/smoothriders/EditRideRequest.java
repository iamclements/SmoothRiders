package com.clementsnelson.smoothriders;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.smoothriders.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditRideRequest extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextDescription, editTextTime, editTextPickupLocation, editTextDestinationLocation, editTextRideTip;
    private Button editRideRequestBtn, logoutButtonBtn;
    private ProgressBar progressBar;
    private FirebaseFirestore mDbb = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride_request);

        // Receive intent from RideInformation Activity
        Intent i2 = getIntent();

        // Create Ride object selectedRide from intent sent from ProfileActivity
        Ride selectedRide = (Ride) i2.getSerializableExtra("Ride");

        // EditText obj references
        editTextDescription = findViewById(R.id.rideDescription);
        editTextTime = findViewById(R.id.rideTime);
        editTextPickupLocation = findViewById(R.id.pickupLocation);
        editTextDestinationLocation = findViewById(R.id.destinationLocation);
        editTextRideTip = findViewById(R.id.rideTip);

        // Fill EditText objects with information from intent
        editTextDescription.setText(selectedRide.getRideDescription());
        editTextTime.setText(selectedRide.getRideTime());
        editTextPickupLocation.setText(selectedRide.getPickupLocation());
        editTextDestinationLocation.setText(selectedRide.getDestinationLocation());
        editTextRideTip.setText(selectedRide.getRideTip());

        // Button obj reference
        editRideRequestBtn = findViewById(R.id.editRequest);
        // Set onClick listener for button
        editRideRequestBtn.setOnClickListener(this);

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
            case R.id.editRequest:
                updateRideRequest();
                break;
        }
    }// end of onClick method

    private void updateRideRequest() {
        // EditText obj references
        editTextDescription = findViewById(R.id.rideDescription);
        editTextTime = findViewById(R.id.rideTime);
        editTextPickupLocation = findViewById(R.id.pickupLocation);
        editTextDestinationLocation = findViewById(R.id.destinationLocation);
        editTextRideTip = findViewById(R.id.rideTip);

        // Receive intent from RideInformation Activity
        Intent i2 = getIntent();

        // Create Ride object selectedRide from intent sent from ProfileActivity
        Ride selectedRide = (Ride) i2.getSerializableExtra("Ride");

        // Get Document reference in Firestore from collection using rideId
        DocumentReference rideRef = FirebaseFirestore.getInstance()
                .collection("Rides")
                .document(selectedRide.getRideId());

        // Update Document reference with new values.
        rideRef.update("rideDescription", editTextDescription.getText().toString());
        rideRef.update("rideTime", editTextTime.getText().toString());
        rideRef.update("pickupLocation", editTextPickupLocation.getText().toString());
        rideRef.update("destinationLocation", editTextDestinationLocation.getText().toString());
        rideRef.update("rideTip", editTextRideTip.getText().toString());

        startActivity(new Intent(EditRideRequest.this, ProfileActivity.class));
        Toast.makeText(EditRideRequest.this, "Ride has been edited successfully!", Toast.LENGTH_LONG).show();

    } // end of updateRideRequest method

    // Method to alter the default behavior of the back button in Android
    @Override
    public void onBackPressed() {
        // If user clicks the back button take them to them to the Profile Activity view
        startActivity(new Intent(EditRideRequest.this, ProfileActivity.class));
    } // end of onBackPressed method

} // end of EditRideRequest class