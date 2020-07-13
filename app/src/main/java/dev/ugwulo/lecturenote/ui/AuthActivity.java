
package dev.ugwulo.lecturenote.ui;
/**
 * Login User with email and password
 * @Author Ndubuisi Ugwulo
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import dev.ugwulo.lecturenote.MainActivity;
import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = AuthActivity.class.getSimpleName();
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    ActivityAuthBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.txtSignUp.setOnClickListener(this);
        binding.btnSignIn.setOnClickListener(this);
//        binding.textForgotPassword.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
////        updateUI(currentUser);
//    }

    private void signUp() {
        Intent intent = new Intent(AuthActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.txtSignUp){
            signUp();
        }else if (i == R.id.textForgotPassword){
//            forgotPassword();
        }else if (i == R.id.btn_sign_in){
            signInUser(binding.email.getText().toString(), binding.password.getText().toString());
        }
    }

    private void signInUser(String email, String password) {
        if (!validateLoginForm()){
            return;
        }
        showProgressBar();
        
//        Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Log.w(TAG, "signInWithEmail: failed" + task.getException());
                    Toast.makeText(getApplicationContext(),  "Authentication Failed!", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }else {
                    Log.d(TAG, "signInWithEmail: successful");
                    FirebaseUser user = mAuth.getCurrentUser();

                    updateUI(user);
                }
                hideProgressBar();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showProgressBar() {binding.progressBar.setVisibility(View.VISIBLE);}
    
    private void hideProgressBar(){binding.progressBar.setVisibility(View.GONE);}

    private boolean validateLoginForm() {
         boolean valid = true;

         String email = binding.email.getText().toString();
         String password = binding.password.getText().toString();

         if (TextUtils.isEmpty(email)){
             binding.email.setError("Required");
             valid = false;
         }else {
             binding.email.setError(null);
         }

         if (TextUtils.isEmpty(password)){
             binding.password.setError("Required");
             valid = false;
         }else {
             binding.password.setError(null);
         }

         return valid;
    }
}