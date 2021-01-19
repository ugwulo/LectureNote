package dev.ugwulo.lecturenote.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.ActivityOnboardingBinding;
import dev.ugwulo.lecturenote.util.Settings;

public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = Activity.class.getSimpleName();
    ActivityOnboardingBinding mBinding;
    private static final String REG_NUMBER = "18EH/0059/CS";
    private static final String STAFF_ID = "ID-2020-CS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        Settings.init(getApplicationContext());

        if (Settings.isFirstTimeLaunch()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        mBinding.btnVerify.setOnClickListener(this);

        initRegisterRadioGroupAndFieldVisibility();
        intVerifyButtonVisibility();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_verify){
            verifyUser();
        }
    }

    private void verifyUser() {
        Log.d(TAG, "verifyUser: VERIFYING USER");
        String regNumber = mBinding.editRegNum.getEditText().getText().toString();
        String staffID = mBinding.editStaffId.getEditText().getText().toString();
        if (TextUtils.isEmpty(regNumber) && TextUtils.isEmpty(staffID)){
            Toast.makeText(this, "Enter Appropriate details", Toast.LENGTH_SHORT).show();
        }

        if (!TextUtils.isEmpty(staffID) && TextUtils.isEmpty(regNumber)){
            if (staffID.equals(STAFF_ID)){
                Settings.setLecturerLogin(true);
                Settings.setStudentLogin(false);
                Settings.setIsFirstTimeLaunch(true);
                gotoSignUp();
            } else Toast.makeText(this, "Invalid Staff ID", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(staffID) && !TextUtils.isEmpty(regNumber)){
            if (regNumber.equals(REG_NUMBER)){
                Settings.setStudentLogin(true);
                Settings.setLecturerLogin(false);
                Settings.setIsFirstTimeLaunch(true);
                gotoSignUp();
            } else Toast.makeText(this, "Invalid Reg Number", Toast.LENGTH_SHORT).show();
        }

    }

    private void gotoSignUp() {
        Intent lecturerIntent = new Intent(OnboardingActivity.this, SignUpActivity.class);
        startActivity(lecturerIntent);
        finish();
    }

    private void initRegisterRadioGroupAndFieldVisibility() {
        ((RadioGroup) mBinding.registerChoiceRadioGroup).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.studentRadio) {
                    mBinding.editRegNum.setVisibility(View.VISIBLE);
                    mBinding.editStaffId.setVisibility(View.GONE);
                    mBinding.btnVerify.setEnabled(!TextUtils.isEmpty(mBinding.editRegNum.getEditText().getText().toString()));
                } else {
                    mBinding.editRegNum.setVisibility(View.GONE);
                    mBinding.editStaffId.setVisibility(View.VISIBLE);
                    mBinding.btnVerify.setEnabled(!TextUtils.isEmpty(mBinding.editStaffId.getEditText().getText().toString()));
                }
            }
        });
    }

    private void intVerifyButtonVisibility() {
        TextWatcher verifyButtonEnabledSetter = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) mBinding.btnVerify.setEnabled(false);
                else mBinding.btnVerify.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mBinding.editRegNum.getEditText().addTextChangedListener(verifyButtonEnabledSetter);
        mBinding.editStaffId.getEditText().addTextChangedListener(verifyButtonEnabledSetter);
    }
}