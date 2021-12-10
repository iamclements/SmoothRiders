package com.clementsnelson.smoothriders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smoothriders.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;



public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    // Initialize references to objects in Activity file
    private TextView banner, registerUser;
    private EditText editTextFullName, editTextAge,editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    // Initialize instance to FirebaseFirestore database
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    // Initialize instance to FirebaseAuth object
    private FirebaseAuth mAuth;
    // Name of collection in Firestore Database
    private static final String TAG = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // Get FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Get reference to banner TextView and create onClick listener
        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        // Get reference to register user Button and create onClick listener
        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        // Get reference to editText fields used in sign up form
        editTextFullName = findViewById(R.id.fullName);
        editTextAge = findViewById(R.id.age);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        // Get reference to the ProgressBar
        progressBar = findViewById(R.id.progressBar);

    } // end of onCreate method

    // Method for onClick listeners in onCreate
    @Override
    public void onClick(View v) {
        // Change behavior of onClick method based on the View passed to listener
        switch (v.getId()) {
            // if user clicks the banner TextView
            case R.id.banner:
                // If user clicks the banner return them to the login page
                startActivity(new Intent(this, MainActivity.class));
                break;

            // If user clicks the register user button
            case R.id.registerUser:
                // Call the register user method
                registerUser();
                break;
        }
    } // end of onClick method

    // Method called when register user button is clicked
    private void registerUser() {
        // Get string values from EditText fields on register user click
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        // Sign up validation form validation

        // If the name EditText input is empty
        if (fullName.isEmpty()) {
            // Display an error and request focus on that EditText object
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }

        // If the age EditText input is empty
        if (age.isEmpty()) {
            // Display an error and request focus on that EditText object
            editTextAge.setError("Age is required!");
            editTextAge.requestFocus();
            return;
        }

        // If the email EditText input is empty
        if (email.isEmpty()) {
            // Display an error and request focus on that EditText object
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        // Checks for valid email requiring @ and .com or some other domain
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Display an error and request focus on that EditText object
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        // If the password EditText input is empty
        if (password.isEmpty()) {
            // Display an error and request focus on that EditText object
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        // Requires passwords be over 6 characters. (Firebase standard)
        if(password.length() < 6) {
            // Display an error and request focus on that EditText object
            editTextPassword.setError("Min password should be at least 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        // If form is valid set the progress bar to visible
        progressBar.setVisibility(View.VISIBLE);

        // Call default createUserWithEmailAndPassword method on FirebaseAuth object
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    // On complete of createUserWithEmailAndPassword method
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If createUserWithEmailAndPassword method is successful
                        if (task.isSuccessful()) {
                            // Create new User object
                            User user = new User(fullName, age, email);

                            // Get reference to Users collection in FirestoreDatabase
                            FirebaseDatabase.getInstance().getReference(TAG)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                                // On Complete of setValue method
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // If user data is sent to the Firebase database
                                    if (task.isSuccessful()) {

                                        // Add new User object to the Users collection
                                        mDb.collection(TAG).add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                                            // On Complete of adding user to Users collection
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                // If user is added to Users collection in Firestore display a Toast
                                                Toast.makeText(RegisterUser.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        // If user is registered take them to login page
                                        startActivity(new Intent(RegisterUser.this, MainActivity.class));

                                        // Once the user is registered and the intent to go to
                                        // the login screen is executed, hide the ProgressBar
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    // If user is not sent to the Firestore database
                                    else {
                                        // Display a Toast with an error message
                                        Toast.makeText(RegisterUser.this, "Failed to register! Try Again!", Toast.LENGTH_LONG).show();

                                        // Hide the ProgressBar
                                        progressBar.setVisibility(View.GONE);
                                    }

                                } // end of onComplete setValue method
                            });
                        }

                        // If createUserWithEmailAndPassword method fails
                        else {
                            // Display a Toast with an error message
                            Toast.makeText(RegisterUser.this, "Failed to register! Try Again!", Toast.LENGTH_LONG).show();

                            // Hide the ProgressBar
                            progressBar.setVisibility(View.GONE);
                        }

                    } // end of onComplete method for createUserWithEmailAndPassword method
                });
    } // end of registerUser method

} // end of RegisterUser class