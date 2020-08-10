package dev.ugwulo.lecturenote.view.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
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
import dev.ugwulo.lecturenote.model.lecturer.Lecturer;
import dev.ugwulo.lecturenote.model.student.Student;
import dev.ugwulo.lecturenote.util.FilePaths;
import dev.ugwulo.lecturenote.util.Settings;
import dev.ugwulo.lecturenote.view.AuthActivity;

import static android.content.ContentValues.TAG;
import static android.Manifest.permission.CAMERA;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    FragmentProfileBinding binding;
    Bitmap myBitmap;
    Uri picUri;
    private static final double MB_THRESHHOLD = 5.0;
    private static final double MB = 1000000.0;
    private byte[] mBytes;
    private double progress;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        Settings.init(getActivity().getApplicationContext());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        if (Settings.isLecturerLogin()){
            getLecturerAccountData();
        }else if (Settings.isStudentLogin()){
            getStudentAccountData();
        }

        binding.btnSaveProfile.setOnClickListener(this);
        binding.changePhoto.setOnClickListener(this);

        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        return root;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_photo:
                startActivityForResult(getPickImageChooserIntent(), 200);
                break;
            case R.id.btn_save_profile:
                saveProfile();
                break;
            default:
        }

    }

    /**
     * Create a chooser intent to select the source to get image from.<br />
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br />
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList();
        PackageManager packageManager = getActivity().getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("dev.ugwulo.lecturenote.view.ProfileActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {

            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);
                    myBitmap = rotateImageIfRequired(myBitmap, picUri);
                    myBitmap = getResizedBitmap(myBitmap, 500);

//                    CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.img_profile);
//                    croppedImageView.setImageBitmap(myBitmap);
//                    imageView.setImageBitmap(myBitmap);
//                    binding.profileImage.setImageBitmap(myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {


                bitmap = (Bitmap) data.getExtras().get("data");

                myBitmap = bitmap;
//                CircleImageView croppedImageView = (CircleImageView) getActivity().findViewById(R.id.profile_image);
//                if (binding.profileImage != null) {
////                    croppedImageView.setImageBitmap(myBitmap);
////                    binding.profileImage.setImageBitmap(myBitmap);
//
//                }

//                imageView.setImageBitmap(myBitmap);

            }

        }

    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br />
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (picUri != null){
            picUri = savedInstanceState.getParcelable("pic_uri");
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {

                    } else {

                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());

                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    public void saveProfile(){
         /*
                ------ Upload the New Photo -----
                 */
        if(picUri != null){
            uploadNewPhoto(picUri);
        }else if(myBitmap  != null){
            uploadNewPhoto(myBitmap);
        }
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

                    ImageLoader.getInstance().displayImage(student.getImage_url(), binding.profileImage);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });


    }
    private void getLecturerAccountData(){
        Log.d(TAG, "getUserAccountData: getting the lecturer's account information");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query lecturerQuery = reference.child(getString(R.string.dbnode_lecturer))
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

                    ImageLoader.getInstance().displayImage(lecturer.getImage_url(), binding.profileImage);
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

    private void hideSoftKeyboard(){
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }

    /**
     * Uploads a new profile photo to Firebase Storage using a @param ***imageUri***
     * @param imageUri
     */
    public void uploadNewPhoto(Uri imageUri){
        /*
            upload a new profile photo to firebase storage
         */
        Log.d(TAG, "uploadNewPhoto: uploading new profile photo to firebase storage.");

        //Only accept image sizes that are compressed to under 5MB. If thats not possible
        //then do not allow image to be uploaded
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imageUri);
    }

    /**
     * Uploads a new profile photo to Firebase Storage using a @param ***imageBitmap***
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
        protected byte[] doInBackground(Uri... params ) {
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
            executeUploadTask();
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    private void executeUploadTask(){
        showProgressBar();
        FilePaths filePaths = new FilePaths();
//specify where the photo will be stored
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                        + "/image_url"); //just replace the old image with the new one

        if(mBytes.length/MB < MB_THRESHHOLD) {

            // Create file metadata including the content type
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .setContentLanguage("en") //see nodes below
                    .build();
            //if the image size is valid then we can submit to database
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(mBytes, metadata);
            //uploadTask = storageReference.putBytes(mBytes); //without metadata


            uploadTask.addOnSuccessListener(taskSnapshot -> {
                //Now insert the download url into the firebase database
                if (taskSnapshot.getMetadata() != null){
                    if (taskSnapshot.getMetadata().getReference() != null){
                        Task<Uri> firebaseURL = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseURL.addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            Toast.makeText(getActivity(), "Upload Success", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: firebase download url : " + imageUrl);
                            FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.dbnode_student))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(getString(R.string.field_profile_image))
                                    .setValue(imageUrl);
                            hideProgressBar();
                        });
                    }
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
}

