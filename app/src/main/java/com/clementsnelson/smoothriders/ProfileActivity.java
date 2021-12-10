package com.clementsnelson.smoothriders;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.smoothriders.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private TextView greetingTextView;
    private ArrayAdapter<Ride> adapter;
    private Button logoutButton, createRideButton, ridesPosted, ridesAccepted;
    private ImageButton refreshButton;
    private static final String RIDE = "Rides";
    private ProgressBar progressBar;
    private EditText searchByTipField;

    private FirebaseFirestore mDbProfile = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ListView rideRequestList = findViewById(R.id.requestList);
        adapter = new ArrayAdapter<Ride>(
                this,
                android.R.layout.simple_list_item_1,
                new ArrayList<Ride>()
        );
        //adapter = new RideAdapter(this, new ArrayList<Ride>());
        rideRequestList.setAdapter(adapter);

        rideRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Ride selectedRide = (Ride) adapterView.getItemAtPosition(position);
                Intent i = new Intent(ProfileActivity.this, RideInformation.class);
                i.putExtra("Ride", selectedRide);

                startActivity(i);
            }
        });

        createRideButton = (Button) findViewById(R.id.createARideButton);
        createRideButton.setOnClickListener(this);

        ridesAccepted = (Button) findViewById(R.id.ridesAccepted);
        ridesAccepted.setOnClickListener(this);

        ridesPosted = (Button) findViewById(R.id.ridesPosted);
        ridesPosted.setOnClickListener(this);

        refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);

        // Get logoutButton obj reference
        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);

        // Get progress bar reference
        progressBar = findViewById(R.id.progressBar);

        // Get current user obj by searching db by User ID.
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        // Get final obj references to empty text views
        greetingTextView = (TextView) findViewById(R.id.greeting);
        greetingTextView.setText("Hello " + user.getEmail());

        // Get EditText obj reference
        searchByTipField = (EditText) findViewById(R.id.searchByTipField);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.createARideButton: // If user clicks the create Rides button
                startActivity(new Intent(ProfileActivity.this, CreateRideRequest.class));
                break;

            case R.id.ridesAccepted: // If user clicks the Rides Accepted button
                startActivity(new Intent(this, UserAcceptedRides.class));
                break;

            case R.id.ridesPosted: // If user clicks the Rides Posted button
                startActivity(new Intent(this, UserRides.class));
                break;

            case R.id.refreshButton:
                progressBar.setVisibility(View.VISIBLE);
                mDbProfile.collection(RIDE)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                adapter.clear();
                                ArrayList<Ride> rides = new ArrayList<>();
                                for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                                    Ride r = document.toObject(Ride.class);
                                    if (!r.getIsAccepted() && searchByTipField.getText().toString().trim().length() == 0) {
                                        // By default rides are created with the isAccepted field
                                        // set to false. When a user accepts the ride we need to
                                        // update that field to true so they don't show up in the
                                        // all rides list.
                                        rides.add(r);
                                        Log.d(RIDE, document.getId() + " =>" + document.getData());
                                    }

                                    // If search field is not blank
                                    else if (searchByTipField.getText().toString().trim().length() > 0 && !r.getIsAccepted()) {
                                        String searchFieldTip = searchByTipField.getText().toString();
                                        double searchTipAmount = Double.parseDouble(searchFieldTip);
                                        double rideTipAmount = Double.parseDouble(r.getRideTip());

                                        if(searchTipAmount <= rideTipAmount) {
                                            rides.add(r);
                                            Log.d(RIDE, document.getId() + " =>" + document.getData());
//                                            Log.d(RIDE, "Search tip amount: " + searchTipAmount);
//                                            Log.d(RIDE, "Ride tip amount: " + rideTipAmount);

                                        }

                                    }

                                }
                                adapter.clear();
                                adapter.addAll(rides);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                break;

            case R.id.logoutButton: // If user clicks the logout Button
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                break;

        }
    }

    // Method to alter the default behavior of the back button in Android
    @Override
    public void onBackPressed() {
        // If user clicks the back button take them to them to the Profile Activity view
        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
    } // end of onBackPressed method
}