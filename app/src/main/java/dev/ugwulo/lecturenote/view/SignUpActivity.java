package dev.ugwulo.lecturenote.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = SignUpActivity.class.getSimpleName();
    Button btnSignUp;
    TextView signIn;
    ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnSignUp = findViewById(R.id.btn_register);
        binding.txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSignUp.setOnClickListener(this);
    }



    private void registerUser() {
        Log.d(TAG, "registerUser: User Registration Started!");
        String email, password, confirmPassword;

        FirebaseUser user;

        email = binding.email.getText().toString();
        password = binding.password.getText().toString();
        confirmPassword = binding.confirmPassword.getText().toString();


        if (TextUtils.isEmpty(email)){
            binding.email.setError("You must enter a valid email.");
            return;
        }
        if (TextUtils.isEmpty(password)){
            binding.password.setError("You must enter a password.");
            return;
        }
        if (password.length() < 6){
            binding.password.setError("password must be => 6 characters");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)){
            binding.confirmPassword.setError("Confirm password.");
            return;
        }
        if (!Objects.equals(password, confirmPassword)){
            Toast.makeText(getApplicationContext(), "Password does not match!", Toast.LENGTH_SHORT).show();
            return;
        }

//        register user
        showProgressBar();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Registration Successful :)", Toast.LENGTH_SHORT).show();

                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    redirectToLoginScreen();
                }
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Error! " + Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                hideProgressBar();
            }
        });
    }

    private void redirectToLoginScreen() {
        Log.d(TAG, "redirectToLoginScreen: REDIRECT USER TO LOGIN");
        Intent intent = new Intent(SignUpActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendVerificationEmail() {
        Log.d(TAG, "sendVerificationEmail: EMAIL VERIFICATION");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Verification Email Sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                    if (!task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Verification Email Not Sent", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void showProgressBar() {binding.progressBar.setVisibility(View.VISIBLE);}

    private void hideProgressBar(){binding.progressBar.setVisibility(View.GONE);}

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.btn_register) {
            registerUser();
        }
    }
}