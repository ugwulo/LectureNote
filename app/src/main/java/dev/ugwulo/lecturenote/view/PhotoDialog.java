package dev.ugwulo.lecturenote.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import org.jetbrains.annotations.NotNull;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.DialogPhotoBinding;

public class PhotoDialog extends DialogFragment {
    private static final String TAG = "PhotoDialog";
    Context mContext;
    DialogPhotoBinding mBinding;
    public static final int  CAMERA_REQUEST_CODE = 5467;//random number
    public static final int PICKFILE_REQUEST_CODE = 8352;//random number

    public PhotoDialog(Context context) {
        mContext = context;
    }

    public interface OnPhotoReceivedListener{
        public void getImagePath(Uri imagePath);
        public void getImageBitmap(Bitmap bitmap);
    }

    OnPhotoReceivedListener mOnPhotoReceived;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DialogPhotoBinding.inflate(getLayoutInflater(), container, false);

        mBinding.dialogChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: accessing phones memory.");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            }
        });

        //Initialize the textview for choosing an image from memory
        mBinding.dialogOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starting camera");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });


        return mBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        Results when selecting new image from phone memory
         */
        if(requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image: " + selectedImageUri);

            //send the bitmap and fragment to the interface
            mOnPhotoReceived.getImagePath(selectedImageUri);
            getDialog().dismiss();

        }

        else if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "onActivityResult: done taking a photo.");

            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            mOnPhotoReceived.getImageBitmap(bitmap);
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mOnPhotoReceived = (OnPhotoReceivedListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException", e.getCause() );
        }
    }
}
