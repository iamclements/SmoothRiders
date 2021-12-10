package com.clementsnelson.smoothriders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.smoothriders.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserAcceptedRides extends AppCompatActivity implements View.OnClickListener {

    // Initialize reference to User
    private FirebaseUser user;
    // Initialize references to objects in Activity file
    private Button logoutBtn, refreshButton;
    private ProgressBar progressBar;
    private TextView emailLabel;
    // Initialize ArrayAdapter for Ride objects
    private ArrayAdapter<Ride> adapter;
    // Get instance to Firebase Firestore
    private FirebaseFirestore mDbProfile = FirebaseFirestore.getInstance();
    // Name of collection in FireStore database
    private static final String RIDE = "Rides";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_accepted_rides);

        // Get current user instance
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get reference to logout button and create onClick listener
        logoutBtn = (Button) findViewById(R.id.logoutButton);
        logoutBtn.setOnClickListener(this);

        // Get reference to refresh button and create onClick listener
        refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);

        // Get reference to the ProgressBar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Get reference to emailLabel for Greeting
        emailLabel = (TextView) findViewById(R.id.emailLabel);
        // Set text of emailLabel to greeting
        emailLabel.setText("Hello " + user.getEmail());

        // Get reference to the ListView rideList
        ListView rideList = findViewById(R.id.rideList);
        // Create new ArrayAdapter of Rides
        adapter = new ArrayAdapter<Ride>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<Ride>()
        );
        // Set the ArrayAdapter to to the rideList ListAdapter
        rideList.setAdapter((ListAdapter) adapter);
    } // end of onCreate method

    // Method for onClick listeners in onCreate
    @Override
    public void onClick(View view) {
        // Change behavior of onClick method based on the View passed
        switch (view.getId()){
            // if user clicks the logout button
            case R.id.logoutButton:
                // Logout using FirebaseAuth built in method
                FirebaseAuth.getInstance().signOut();
                // Return the user to the login screen after they sign out
                startActivity(new Intent(UserAcceptedRides.this, MainActivity.class));
                break;

            // if user clicks the refresh rides button
            case R.id.refreshButton:
                // Call the getUserRides method
                getUserRides();
                break;
        }
    } // end of onClick method

    // Method called when refresh button is clicked
    private void getUserRides() {
        // onClick of the refresh button, set the progress bar to visible
        progressBar.setVisibility(View.VISIBLE);

        // Get the Rides collection from the Firestore
        mDbProfile.collection(RIDE)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Initialize new ArrayList for Ride objects
                        ArrayList<Ride> rides = new ArrayList<>();

                        // Iterate over each Ride object in the ArrayList
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                            // Set r equal to the the a document in the Rides collection
                            Ride r = document.toObject(Ride.class);
                            if (r.getDriverEmail().equals(user.getEmail())) {
                                // If we update the driverEmail field in document this will only
                                // pull rides from Rides documents where the driverEmail field is
                                // equal to the current users email.
                                rides.add(r);
                            }
                        }
                        // Clear adapter in case of multiple clicks
                        adapter.clear();

                        // Add all the rides added to the ArrayList to the adapter
                        adapter.addAll(rides);

                        // Once all rides have been added to the ListView hide the ProgressBar
                        progressBar.setVisibility(View.GONE);
                    } // end of onSuccess method
                });
    } // end of getUserRides method

    // Method to alter the default behavior of the back button in Android
    @Override
    public void onBackPressed() {
        // If user clicks the back button take them to them to the Profile Activity view
        startActivity(new Intent(UserAcceptedRides.this, ProfileActivity.class));
    } // end of onBackPressed method

} // end of UserRides class
