package com.clementsnelson.smoothriders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smoothriders.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RideInformation extends AppCompatActivity implements View.OnClickListener {

    // Initialize reference to User
    private FirebaseUser user;
    // Initialize references to objects in Activity file
    private TextView tip, time, rideDescription, driverName, pickupAddress, destination;
    private Button acceptButton, logoutButton, editButton, deleteButton;
    // Get database reference from Firebase
    private DatabaseReference reference;
    // Initialize userID String for use in Greeting
    private String userID;
    // Create final key value with package name
    static final String key = "com.clemntsnelson.smoothriders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_information);

        // Receive intent from ProfileActivity
        Intent i = getIntent();
        // Create Ride object selectedRide from intent sent from ProfileActivity
        Ride selectedRide = (Ride) i.getSerializableExtra("Ride");

        // Get string values from selectedRide to input into label TextViews
        String requesterEmail = selectedRide.getRequesterEmail();
        String pickupLocation = selectedRide.getPickupLocation();
        String destinationLocation = selectedRide.getDestinationLocation();
        String description = selectedRide.getRideDescription();
        String rideTime = selectedRide.getRideTime();
        String rideTip = selectedRide.getRideTip();
        String rideId = selectedRide.getRideId();

        // Get Boolean value from selectedRide
        Boolean isAccepted = selectedRide.getIsAccepted();

        // Get FireBase user instance
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Get unique ID of the current user
        userID = user.getUid();

        // Get reference to the Rides collection in Firebase
        reference = FirebaseDatabase.getInstance().getReference("Rides");

        // Get input TextView references
        driverName = (TextView) findViewById(R.id.DriverName);
        rideDescription = (TextView) findViewById(R.id.rideDescription);
        tip = (TextView) findViewById(R.id.tip);
        time = (TextView) findViewById(R.id.time);
        pickupAddress = (TextView) findViewById(R.id.pickupAddress);
        destination = (TextView) findViewById(R.id.rideDestination);

        // Fill the input TextView references with Strings from selectedRide
        driverName.setText(requesterEmail);
        pickupAddress.setText(pickupLocation);
        destination.setText(destinationLocation);
        rideDescription.setText(description);
        time.setText(rideTime);
        tip.setText(rideTip);

        // Get reference to logout button and create onClick listener
        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);

        // Get reference to the accept ride button and create onClick listener
        acceptButton = (Button) findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(this);

        // Get reference to the edit ride button and create onClick listener
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);

        // Get reference to the delete ride button and create onClick listener
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
    } // end of onCreate method

    // Method for onClick listeners in onCreate
    @Override
    public void onClick(View v){
        // Change behavior of onClick method based on the View passed to listener
        switch (v.getId()) {
            // if user clicks the logout button
            case R.id.logoutButton:
                // Logout using FirebaseAuth built in method
                FirebaseAuth.getInstance().signOut();
                // Return the user to the login screen after they sign out
                startActivity(new Intent(RideInformation.this, MainActivity.class));
                break;

            // if user clicks the accept ride button
            case R.id.acceptButton:
                // Call the acceptRide method
                acceptRide();
                break;

            // if user clicks the edit ride button
            case R.id.editButton:
                // Call the editRide method
                editRide();
                break;

            // if user clicks the edit ride button
            case R.id.deleteButton:
                // Call the deleteRide method
                deleteRide();
                break;
        }
    } // end of onClick method

    private void deleteRide() {
        // Receive intent from ProfileActivity
        Intent i = getIntent();

        // Create Ride object selectedRide from intent sent from ProfileActivity
        Ride selectedRide = (Ride) i.getSerializableExtra("Ride");

        // Get Document reference in Firestore from collection using rideId
        DocumentReference rideRef = FirebaseFirestore.getInstance()
                .collection("Rides")
                .document(selectedRide.getRideId());

        if (!selectedRide.getRequesterEmail().equals(user.getEmail())) {
            Toast.makeText(RideInformation.this, "A user can't delete a ride from another user ",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(RideInformation.this, ProfileActivity.class));
        }
        else {
            // Delete the Document
            rideRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(RideInformation.this, "Ride has been deleted", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RideInformation.this, ProfileActivity.class));
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RideInformation.this, "Could not delete", Toast.LENGTH_LONG).show();
                }
            });
        }


    } // end of deleteRide method

    // Method called when Edit Rides button is clicked
    private void editRide() {
        // Receive intent from ProfileActivity
        Intent i = getIntent();

        // Create Ride object selectedRide from intent sent from ProfileActivity
        Ride selectedRide = (Ride) i.getSerializableExtra("Ride");

        // Get Document reference in Firestore from collection using rideId
        DocumentReference rideRef = FirebaseFirestore.getInstance()
                .collection("Rides")
                .document(selectedRide.getRideId());

        if (!selectedRide.getRequesterEmail().equals(user.getEmail())) {
            Toast.makeText(RideInformation.this, "A user can't edit a ride another user posted",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(RideInformation.this, ProfileActivity.class));
        }
        else {
            Intent i2 = new Intent(RideInformation.this, EditRideRequest.class);
            i2.putExtra("Ride", selectedRide);
            startActivity(i2);
        }

    } // end of editRide method

    // Method called when accept button is clicked
    private void acceptRide() {
        // Receive intent from ProfileActivity
        Intent i = getIntent();

        // Create Ride object selectedRide from intent sent from ProfileActivity
        Ride selectedRide = (Ride) i.getSerializableExtra("Ride");

        // Get Document reference in Firestore from collection using rideId
        DocumentReference rideRef = FirebaseFirestore.getInstance()
                                        .collection("Rides")
                                        .document(selectedRide.getRideId());

        // Get instance of Current User
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Update the Document references
        rideRef.update("isAccepted", true);
        rideRef.update("driverEmail", user.getEmail());

        // If a user tries to accept a ride they posted
        if (selectedRide.getRequesterEmail().equals(user.getEmail())) {
            // Update Documents fields to their default value
            rideRef.update("isAccepted", false);
            rideRef.update("driverEmail", "");

            // Create Toast for user showing error
            Toast.makeText(RideInformation.this, "A requester can't accept a ride they posted",
                    Toast.LENGTH_LONG).show();

            // Redirect the user back to ProfileActivity
            startActivity(new Intent(RideInformation.this, ProfileActivity.class));
        }
        // If requesterEmail and driverEmail are not the same
        else {
            // Create Toast for user that the Ride has be accepted
            Toast.makeText(RideInformation.this, "Ride has been accepted!", Toast.LENGTH_LONG).show();
            // Return the user to the view with all of the rides they have accepted
            startActivity(new Intent(RideInformation.this, UserAcceptedRides.class));
        }

    } // end of acceptRide method

} // end of RideInformation class
