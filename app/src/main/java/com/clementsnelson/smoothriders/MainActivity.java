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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;

    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase auth obj
        mAuth = FirebaseAuth.getInstance();

        // Register TextView obj reference
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        // Forgot Password TextView obj reference
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        // Sign in button reference
        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        // EditText fields obj references
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        // Progress bar obj reference
        progressBar = findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register: // If user clicks the register TextView
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.signIn: // If user clicks the sign in button
                userLogin();
                break;

            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    private void userLogin() {
        // String objects from EditText fields
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Login form validation
        if (email.isEmpty()) { // basic email validation to check if empty
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // checks if valid email address
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) { // basic password validation to check if empty
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) { // Requires passwords be over 6 characters. (Firebase standard)
            editTextPassword.setError("Min password should be at least 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        // If form is valid try to login
        progressBar.setVisibility(View.VISIBLE);

        // Log user in using Firebase function
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // if login is successful
                if (task.isSuccessful()) {
                    // redirect to user profile (temporary)
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    progressBar.setVisibility(View.GONE);
                }
                else { // user inputs incorrect login information
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }//end of userLogin();
}
