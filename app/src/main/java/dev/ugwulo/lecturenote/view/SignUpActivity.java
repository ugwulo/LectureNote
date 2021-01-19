package dev.ugwulo.lecturenote.view;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.ActivitySignUpBinding;
import dev.ugwulo.lecturenote.model.Lecturer;
import dev.ugwulo.lecturenote.model.Student;
import dev.ugwulo.lecturenote.util.Settings;

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
        Settings.init(this);

        if (Settings.isStudentLogin()){
            binding.studentView.setVisibility(View.VISIBLE);
            binding.lecturerView.setVisibility(View.GONE);
        } else if (Settings.isLecturerLogin()){
            binding.lecturerView.setVisibility(View.VISIBLE);
            binding.studentView.setVisibility(View.GONE);
        }

        btnSignUp = findViewById(R.id.btn_sign_up);
        signIn = findViewById(R.id.txt_sign_in);

        signIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    private void registerStudent() {
        Log.d(TAG, "registerUser: User Registration Started!");
        String studentName, email, password, confirmPassword, regNumber, level, department;

        studentName = binding.studentName.getText().toString();
        regNumber = binding.editRegNum.getText().toString();
        level = binding.editLevel.getText().toString();
        department = binding.editStudentDept.getText().toString();
        email = binding.editStudentEmail.getText().toString();
        password = binding.studentPassword.getText().toString();
        confirmPassword = binding.confirmStudentPassword.getText().toString();


        if (TextUtils.isEmpty(studentName)){
            binding.editRegNum.setError("Enter your first name");
            return;
        }

        if (TextUtils.isEmpty(regNumber)){
            binding.editRegNum.setError("Enter your Reg Number");
            return;
        }

        if (TextUtils.isEmpty(level)){
            binding.editLevel.setError("Enter your Level");
            return;
        }

        if (TextUtils.isEmpty(department)){
            binding.editRegNum.setError("Enter your Department");
            return;
        }

        if (TextUtils.isEmpty(email)){
            binding.editStudentEmail.setError("You must enter a valid email.");
            return;
        }
        if (TextUtils.isEmpty(password)){
            binding.studentPassword.setError("You must enter a password.");
            return;
        }
        if (password.length() < 6){
            binding.studentPassword.setError("password must be => 6 characters");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)){
            binding.confirmStudentPassword.setError("Confirm password.");
            return;
        }
        if (!Objects.equals(password, confirmPassword)){
            Toast.makeText(getApplicationContext(), "Password does not match!", Toast.LENGTH_SHORT).show();
            return;
        }

//        register user
        showProgressBar();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Registration Successful :)", Toast.LENGTH_SHORT).show();
                    /*
                     * create student node with default data
                     */

                    Student student = new Student();
                    student.setStudent_name(studentName);
                    student.setImage_url("");
                    student.setLevel(level);
                    student.setDepartment(department);
                    student.setReg_number(regNumber);
                    student.setEmail(email);
                    student.setStudent_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                    FirebaseDatabase.getInstance().getReference()
                            .child(getString(R.string.dbnode_student))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(student)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "onComplete: REDIRECT TO LOGIN");
                                    sendVerificationEmail();
                                    FirebaseAuth.getInstance().signOut();
                                    redirectToLoginScreen();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "An Error Occurred!", Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();
                                    redirectToLoginScreen();
                                }
                            });
                }
                if (!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Error! " + Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                hideProgressBar();
            }
        });
    }
    private void registerLecturer() {
        Log.d(TAG, "registerUser: User Registration Started!");
        String lecturerName, email, password, confirmPassword, staffID, department;

        lecturerName = binding.lecturerName.getText().toString();
        staffID = binding.editStaffId.getText().toString();
        department = binding.editLecturerDept.getText().toString();
        email = binding.lecturerEmail.getText().toString();
        password = binding.lecturerPassword.getText().toString();
        confirmPassword = binding.confirmLecturerPassword.getText().toString();


        if (TextUtils.isEmpty(lecturerName)){
            binding.editRegNum.setError("Please enter your name");
            return;
        }

        if (TextUtils.isEmpty(staffID)){
            binding.editRegNum.setError("Enter your Staff ID");
            return;
        }

        if (TextUtils.isEmpty(department)){
            binding.editRegNum.setError("Enter your Department");
            return;
        }

        if (TextUtils.isEmpty(email)){
            binding.editStudentEmail.setError("You must enter a valid email.");
            return;
        }
        if (TextUtils.isEmpty(password)){
            binding.studentPassword.setError("You must enter a password.");
            return;
        }
        if (password.length() < 6){
            binding.studentPassword.setError("password must be => 6 characters");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)){
            binding.confirmStudentPassword.setError("Confirm password.");
            return;
        }
        if (!Objects.equals(password, confirmPassword)){
            Toast.makeText(getApplicationContext(), "Password does not match!", Toast.LENGTH_SHORT).show();
            return;
        }

//        register lecturer
        showProgressBar();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Registration Successful :)", Toast.LENGTH_SHORT).show();
                            /**
                             * create lecturer node
                             */
                            Lecturer lecturer = new Lecturer();
                            lecturer.setLecturer_name(lecturerName);
                            lecturer.setStaff_id(staffID);
                            lecturer.setEmail(email);
                            lecturer.setLecturer_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            lecturer.setDepartment(department);
                            lecturer.setImage_url("");

                            FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.dbnode_lecturer))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(lecturer)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onComplete: REDIRECT TO LOGIN");
                                            sendVerificationEmail();
                                            FirebaseAuth.getInstance().signOut();
                                            redirectToLoginScreen();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "An Error Occurred!", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            redirectToLoginScreen();
                                        }
                                    });
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
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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
        if (id == R.id.txt_sign_in){
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(id == R.id.btn_sign_up) {
            if (Settings.isStudentLogin()){
                registerStudent();
            } else if (Settings.isLecturerLogin()){
                registerLecturer();
            }
        }
    }
}