package dev.ugwulo.lecturenote.util;

import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import dev.ugwulo.lecturenote.R;

public class UniversalImageLoader {
    private static final String TAG = "UniversalImageLoader";
    private static final int defaultImage = R.drawable.ic_android;
    private Context mContext;

    public UniversalImageLoader(Context context) {
        this.mContext = context;
        Log.d(TAG, "UniversalImageLoader: started");
    }

    public ImageLoaderConfiguration getConfig(){
        Log.d(TAG, "getConfig: Returning image loader configuration");
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage) // resource or drawable
                .showImageForEmptyUri(defaultImage) // resource or drawable
                .showImageOnFail(defaultImage) // resource or drawable
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024)
                .build();


        return config;
    }


}

//package dev.ugwulo.lecturenote.view.profile;
//
//        import android.Manifest;
//        import android.app.Activity;
//        import android.content.Context;
//        import android.content.Intent;
//        import android.content.pm.PackageManager;
//        import android.graphics.Bitmap;
//        import android.net.Uri;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.view.WindowManager;
//        import android.widget.Toast;
//
//        import androidx.annotation.NonNull;
//        import androidx.core.app.ActivityCompat;
//        import androidx.core.content.ContextCompat;
//        import androidx.fragment.app.Fragment;
//
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.auth.FirebaseUser;
//        import com.google.firebase.database.DataSnapshot;
//        import com.google.firebase.database.DatabaseError;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.Query;
//        import com.google.firebase.database.ValueEventListener;
//        import com.nostra13.universalimageloader.core.ImageLoader;
//
//        import org.jetbrains.annotations.NotNull;
//
//        import java.util.Objects;
//
//        import dev.ugwulo.lecturenote.R;
//        import dev.ugwulo.lecturenote.databinding.FragmentProfileBinding;
//        import dev.ugwulo.lecturenote.model.student.Student;
//        import dev.ugwulo.lecturenote.view.AuthActivity;
//        import dev.ugwulo.lecturenote.view.ChangePhotoDialog;
//
//        import static android.content.ContentValues.TAG;
//
//public class ProfileFragment extends Fragment implements View.OnClickListener, ChangePhotoDialog.OnPhotoReceivedListener {
//    FragmentProfileBinding binding;
//    private static final int REQUEST_CODE = 1234;
//    public Uri mSelectedImageUri;
//    public Bitmap mSelectedImageBitmap;
//    private boolean mStoragePermissions = false;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentProfileBinding.inflate(getLayoutInflater());
//        View root = binding.getRoot();
//        verifyStoragePermissions();
//
//        binding.btnSaveProfile.setOnClickListener(this);
//        binding.profileImage.setOnClickListener(this);
//
//        init();
//        return root;
//    }
//
//    private void init() {
//        getUserAccountData();
//        saveProfile();
//
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.profile_image:
//                if(mStoragePermissions){
//                    ChangePhotoDialog dialog = new ChangePhotoDialog();
//                    dialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), getString(R.string.dialog_change_photo));
//                }else{
//                    verifyStoragePermissions();
//                }
//                break;
//            case R.id.btn_save_profile:
//                saveProfile();
//                break;
//            default:
//        }
//
//    }
//
//    public void saveProfile(){
//
//    }
//
//
//    private void getUserAccountData(){
//        Log.d(TAG, "getUserAccountData: getting the user's account information");
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//        /*
//            ---------- QUERY Method 1 ----------
//         */
//        Query query1 = reference.child(getString(R.string.dbnode_student))
//                .orderByKey()
//                .equalTo( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
//        query1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
//
//                //this loop will return a single result
//                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
//                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found user: "
//                            + Objects.requireNonNull(singleSnapshot.getValue(Student.class)).toString());
//                    Student student = singleSnapshot.getValue(Student.class);
//                    assert student != null;
//                    binding.tvUserName.setText(student.getNname());
//                    binding.tvLevel.setText(student.getLevel());
//                    binding.tvRegNumber.setText(student.getReg_number());
//                    binding.tvDepartment.setText(student.getDepartment());
//                    binding.tvEmail.setText(student.getEmail());
//
////                    ImageLoader.getInstance().displayImage(student.getImage_url(), binding.profileImage);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NotNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//        /*
//            ---------- QUERY Method 2 ----------
//         */
//        Query query2 = reference.child(getString(R.string.dbnode_student))
//                .orderByChild(getString(R.string.field_student_id))
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        query2.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
//
//                //this loop will return a single result
//                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
//                    Log.d(TAG, "onDataChange: (QUERY METHOD 2) found user: "
//                            + Objects.requireNonNull(singleSnapshot.getValue(Student.class)).toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NotNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//    }
//
//    public void verifyStoragePermissions(){
//        Log.d(TAG, "verifyPermissions: asking user for permissions.");
//        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA};
//        if (ActivityCompat.checkSelfPermission( getActivity(),
//                permissions[0] ) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission( getActivity(),
//                permissions[1] ) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission( getActivity(),
//                permissions[2] ) == PackageManager.PERMISSION_GRANTED) {
//            mStoragePermissions = true;
//        } else {
//            ActivityCompat.requestPermissions(
//                    getActivity(),
//                    permissions,
//                    REQUEST_CODE
//            );
//        }
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        Log.d(TAG, "onRequestPermissionsResult: requestCode: " + requestCode);
//        switch(requestCode){
//            case REQUEST_CODE:
//                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    Log.d(TAG, "onRequestPermissionsResult: User has allowed permission to access: " + permissions[0]);
//
//                }
//                break;
//        }
//    }
//
//    private void showProgressBar(){
//        binding.progressBar.setVisibility(View.VISIBLE);
//
//    }
//
//    private void hideProgressBar(){
//        if(binding.progressBar.getVisibility() == View.VISIBLE){
//            binding.progressBar.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private void hideSoftKeyboard(){
//        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        checkAuthenticationState();
//    }
//
//    private void checkAuthenticationState(){
//        Log.d(TAG, "checkAuthenticationState: checking authentication state.");
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if(user == null){
//            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");
//
//            Intent intent = new Intent(getActivity(), AuthActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            Objects.requireNonNull(getActivity()).finish();
//        }else{
//            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
//        }
//    }
//
//    @Override
//    public void getImagePath(Uri imagePath) {
//        if (!imagePath.toString().equals("")){
//            mSelectedImageBitmap = null;
//            mSelectedImageUri = imagePath;
//            Log.d(TAG, "getImagePath: Got the image Uri:" + mSelectedImageUri);
////            ImageLoader.getInstance().displayImage(imagePath.toString(), binding.profileImage);
//        }
//    }
//
//    @Override
//    public void getImageBitmap(Bitmap bitmap) {
//        if (bitmap != null){
//            mSelectedImageUri = null;
//            mSelectedImageBitmap = bitmap;
//            Log.d(TAG, "getImageBitmap: Got the image Bitmap: " + mSelectedImageBitmap);
////            binding.profileImage.setImageBitmap(bitmap);
//        }
//    }
//
//    public void uploadNewPhoto(Bitmap imageBitmap){
//        /*
//            upload a new profile photo to firebase storage
//         */
//        Log.d(TAG, "uploadNewPhoto: uploading new profile photo to firebase storage.");
//
//        //Only accept image sizes that are compressed to under 5MB. If thats not possible
//        //then do not allow image to be uploaded
////        BackgroundImageResize resize = new BackgroundImageResize(imageBitmap);
////        Uri uri = null;
////        resize.execute(uri);
//    }
//
//
//}
