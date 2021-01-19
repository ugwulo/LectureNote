package dev.ugwulo.lecturenote.view.profile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.jetbrains.annotations.NotNull;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.FragmentProfileBinding;
import dev.ugwulo.lecturenote.model.Lecturer;
import dev.ugwulo.lecturenote.model.Student;
import dev.ugwulo.lecturenote.util.StoragePaths;
import dev.ugwulo.lecturenote.util.Settings;
import dev.ugwulo.lecturenote.view.LoginActivity;
import dev.ugwulo.lecturenote.view.PhotoDialog;

public class ProfileFragment extends Fragment implements View.OnClickListener, PhotoDialog.OnPhotoReceivedListener {

    @Override
    public void getImagePath(Uri imagePath) {
        if( !imagePath.toString().equals("")){
            mSelectedImageBitmap = null;
            mSelectedImageUri = imagePath;
            Log.d(TAG, "getImagePath: got the image uri: " + mSelectedImageUri);

            Glide.with(this)
                    .load(imagePath.toString())
                    .circleCrop()
                    .into(binding.profileImage);
        }

    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        if(bitmap != null){
            mSelectedImageUri = null;
            mSelectedImageBitmap = bitmap;
            Log.d(TAG, "getImageBitmap: got the image bitmap: " + mSelectedImageBitmap);

            Glide.with(this)
                    .load(bitmap)
                    .circleCrop()
                    .into(binding.profileImage);
        }
    }

    private static final String TAG = "ProfileFragment";
    FragmentProfileBinding binding;
    Uri imgPath = null;
    String download_url = null;

    private static final int PERMISSION_REQUEST_CODE = 321;
    private static final double MB_THRESHHOLD = 5.0;
    private static final double MB = 1000000.0;
    //vars
    private boolean mStoragePermissions;
    private Uri mSelectedImageUri;
    private Bitmap mSelectedImageBitmap;
    private byte[] mBytes;
    private double progress;

    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorageReference;
    private StoragePaths mStoragePaths;
    private DatabaseReference mDatabaseReference;
    private StorageReference mLecturerStorageReference;
    private StorageReference mStudentStorageReference;
    private DatabaseReference mLecturerNode;
    private DatabaseReference mStudentNode;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        Settings.init(getActivity().getApplicationContext());

        verifyStoragePermissions();
        setupFirebaseAuth();
        init();

        return binding.getRoot();
    }

    private void init() {
        mStoragePaths = new StoragePaths();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        //specify where the photo will be stored
        //just replace the old image with the new one
        mLecturerStorageReference = mStorageReference
                .child(mStoragePaths.LECTURER_PROFILE_IMAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                        + "/profile_image");

        mStudentStorageReference = mStorageReference
                .child(mStoragePaths.STUDENT_PROFILE_IMAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                        + "/profile_image");

        // database reference to user node
        mLecturerNode = mDatabaseReference
                .child(getString(R.string.dbnode_lecturer))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_profile_image));

        // database reference to user node
        mStudentNode = mDatabaseReference
                .child(getString(R.string.dbnode_student))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_profile_image));

        if (Settings.isLecturerLogin()){
            getLecturerAccountData();
        }else if (Settings.isStudentLogin()){
            getStudentAccountData();
        }

        binding.btnSaveProfile.setOnClickListener(this);
        binding.changePhoto.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_photo:
                if(mStoragePermissions){
                    binding.btnSaveProfile.setVisibility(View.VISIBLE);
                    PhotoDialog dialog = new PhotoDialog(getActivity());
                    dialog.setTargetFragment(this, 1);
                    dialog.show(getParentFragmentManager(), getString(R.string.dialog_change_photo));
                }else{
                    verifyStoragePermissions();
                }
                break;
            case R.id.btn_save_profile:
                saveProfile();
                break;
            default:
        }

    }
    private void uploadImage() {

        if (imgPath != null) {

            showProgressBar();

            //final StorageReference sRef = mStorageReference.child("User_Profile/" + "User_" + System.currentTimeMillis());
            final StorageReference sRef = mStorageReference
                    .child(mStoragePaths.LECTURER_PROFILE_IMAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                            + "/profile_image"); //just replace the old image with the new one

            sRef.putFile(imgPath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            hideProgressBar();
                            Toast.makeText(requireActivity(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();

                            sRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {
                                        download_url = task.getResult().toString();
                                        Log.e(TAG, "onComplete:========>>><<<<" + download_url);
                                        String push_key = mDatabaseReference.push().getKey().toString();
                                        //mDatabaseReference.getRoot().child("User_Profile").child(push_key).setValue(download_url);

                                        mDatabaseReference.child(getString(R.string.dbnode_student))
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(getString(R.string.field_profile_image))
                                                .setValue(download_url);

                                        Glide.with(ProfileFragment.this)
                                                .load(download_url)
                                                .into(binding.profileImage);
                                    }
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           hideProgressBar();
                            Toast.makeText(requireActivity(), "Image Upload Fail!" + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onFailure:====>>><<<" + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            Snackbar.make(requireActivity().getCurrentFocus().getRootView(), "Uploading " +
                                    taskSnapshot.getBytesTransferred() / 1024 + " / " + taskSnapshot.getTotalByteCount() / 1024
                                    + " KB", Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void saveProfile(){
          /**
          ------ Upload the New Photo -----
                 **/
        if(mSelectedImageUri != null){
            uploadNewPhoto(mSelectedImageUri);
        }else if(mSelectedImageBitmap  != null){
            uploadNewPhoto(mSelectedImageBitmap);
        }

        Toast.makeText(requireActivity(), "saved", Toast.LENGTH_SHORT).show();
    }

    private void getStudentAccountData(){
        Log.d(TAG, "getUserAccountData: getting the user's account information");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        /*
            ---------- QUERY Method 1 ----------
         */
        Query query1 = reference.child(getString(R.string.dbnode_student))
                .orderByKey()
                .equalTo( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                //this loop will return a single result
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found user: "
                            + Objects.requireNonNull(singleSnapshot.getValue(Student.class)).toString());
                    Student student = singleSnapshot.getValue(Student.class);
                    assert student != null;
                    binding.tvUserName.setText(student.getStudent_name());
                    binding.tvLevel.setText(student.getLevel());
                    binding.tvRegNumber.setText(student.getReg_number());
                    binding.tvDepartment.setText(student.getDepartment());
                    binding.tvEmail.setText(student.getEmail());

                    Glide.with(requireActivity())
                            .load(student.getImage_url())
                            .circleCrop()
                            .into(binding.profileImage);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });


    }
    private void getLecturerAccountData(){
        Log.d(TAG, "getUserAccountData: getting the lecturer's account information");

        Query lecturerQuery = mDatabaseReference.child(getString(R.string.dbnode_lecturer))
                .orderByKey()
                .equalTo( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        lecturerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                //this loop will return a single result
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: (LECTURER QUERY) found lecturer: "
                            + Objects.requireNonNull(singleSnapshot.getValue(Lecturer.class)).toString());
                    Lecturer lecturer = singleSnapshot.getValue(Lecturer.class);
                    assert lecturer != null;
                    binding.tvUserName.setText(lecturer.getLecturer_name());
                    binding.tvRegNumber.setText(lecturer.getStaff_id());
                    binding.tvDepartment.setText(lecturer.getDepartment());
                    binding.tvEmail.setText(lecturer.getEmail());

                    Glide.with(requireActivity())
                            .load(lecturer.getImage_url())
                            .circleCrop()
                            .into(binding.profileImage);


                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });


    }

    private void showProgressBar(){
        binding.progressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar(){
        if(binding.progressBar.getVisibility() == View.VISIBLE){
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }

    /**
     * Uploads a new profile photo to Firebase Storage using Uri from file storage
     * @param imageUri
     */
    public void uploadNewPhoto(Uri imageUri){
        Log.d(TAG, "uploadNewPhoto: uploading new profile photo to firebase storage.");

        //Only accept image sizes that are compressed to under 5MB. If thats not possible
        //then do not allow image to be uploaded
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imageUri);
    }

    /**
     * Uploads a new profile photo to Firebase Storage using Bitmap from Camera
     * @param imageBitmap
     */
    public void uploadNewPhoto(Bitmap imageBitmap){
        /*
            upload a new profile photo to firebase storage
         */
        Log.d(TAG, "uploadNewPhoto: uploading new profile photo to firebase storage.");

        //Only accept image sizes that are compressed to under 5MB. If thats not possible
        //then do not allow image to be uploaded
        BackgroundImageResize resize = new BackgroundImageResize(imageBitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    /**
     * 1) doInBackground takes an imageUri and returns the byte array after compression
     * 2) onPostExecute will print the % compression to the log once finished
     */
    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;
        public BackgroundImageResize(Bitmap bm) {
            if(bm != null){
                mBitmap = bm;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
            Toast.makeText(getActivity(), "compressing image", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri...  params ) {
            Log.d(TAG, "doInBackground: started.");

            if(mBitmap == null){

                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), params[0]);
                    Log.d(TAG, "doInBackground: bitmap size: megabytes: " + mBitmap.getByteCount()/MB + " MB");
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: IOException: ", e.getCause());
                }
            }

            byte[] bytes = null;
            for (int i = 1; i < 11; i++){
                if(i == 10){
                    Toast.makeText(getActivity(), "That image is too large.", Toast.LENGTH_SHORT).show();
                    break;
                }
                bytes = getBytesFromBitmap(mBitmap,100/i);
                Log.d(TAG, "doInBackground: megabytes: (" + (11-i) + "0%) "  + bytes.length/MB + " MB");
                if(bytes.length/MB  < MB_THRESHHOLD){
                    return bytes;
                }
            }
            return bytes;
        }


        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            hideProgressBar();
            mBytes = bytes;
            //execute the upload
            if (Settings.isLecturerLogin()){
                executeUploadTask(mLecturerStorageReference, mLecturerNode);
            }else if (Settings.isStudentLogin()){
                executeUploadTask(mStudentStorageReference, mStudentNode);
            }
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    private void executeUploadTask(StorageReference storageImagePath, DatabaseReference userDbNode){
        showProgressBar();


        if(mBytes.length/MB < MB_THRESHHOLD) {

            // Create file metadata including the content type
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .setContentLanguage("en") //see nodes below
                    .build();
            //if the image size is valid then we can submit to database
            UploadTask uploadTask = null;
            uploadTask = storageImagePath.putBytes(mBytes, metadata);
            //uploadTask = storageReference.putBytes(mBytes); //without metadata


            uploadTask.addOnSuccessListener(taskSnapshot -> {
                //Now insert the download url into the firebase database
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null){
                    Task<Uri> firebaseURL = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    firebaseURL.addOnSuccessListener(donwloadUrl -> {

                        Toast.makeText(getActivity(), "Upload Success", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onSuccess: firebase download url : " + donwloadUrl.toString());

                        userDbNode.setValue(donwloadUrl.toString());
                        hideProgressBar();
                    });
                }
            }).addOnFailureListener(exception -> {
                Log.d(TAG, "executeUploadTask: " + exception.getMessage());
                Toast.makeText(getActivity(), "could not upload photo", Toast.LENGTH_SHORT).show();
                hideProgressBar();

            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if(currentProgress > (progress + 15)){
                        progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: Upload is " + progress + "% done");
                        Toast.makeText(getActivity(), progress + "%", Toast.LENGTH_SHORT).show();
                    }

                }
            })
            ;
        }else{
            Toast.makeText(getActivity(), "Image is too Large", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Generalized method for asking permission. Can pass any array of permissions
     */
    public void verifyStoragePermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions.");
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(requireContext(),
                permissions[0] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(),
                permissions[1] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(),
                permissions[2] ) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissions = true;
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions,
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //Toast.makeText(requireActivity(), "Successfully signed in with: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(requireActivity(), "Signed out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        };

    }
}

