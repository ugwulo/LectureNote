package dev.ugwulo.lecturenote.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import dev.ugwulo.lecturenote.databinding.DialogAddCourseBinding;

public class AddCourseDialogFragment extends DialogFragment {
    DialogAddCourseBinding mBinding;
    public final String TAG = AddCourseDialogFragment.class.getSimpleName();

    public interface AddCourseListener{
         void onDialogPositiveClick(String title, String code, String lecturer);
         void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    AddCourseListener mAddCourseListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        mBinding = DialogAddCourseBinding.inflate(LayoutInflater.from(getActivity()));

        builder.setView(mBinding.getRoot())
                .setTitle("Add Course")
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity

                        String title, code, lecturer;
                        title = mBinding.editCourseTitle.getText().toString();
                        code = mBinding.editCourseCode.getText().toString();
                        lecturer = mBinding.editCourseLecturer.getText().toString();
                       mAddCourseListener.onDialogPositiveClick(title, code, lecturer);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mAddCourseListener.onDialogNegativeClick(AddCourseDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: Verify Callback Interface");
        super.onAttach(context);
        // Verify that the host fragment implements the callback interface
        Fragment fragment = getTargetFragment();
        try {
            // Instantiate the AddCourseListener so we can send events to the host
            mAddCourseListener = (AddCourseListener) fragment;
        } catch (ClassCastException e) {
            // if the fragment doesn't implement the interface, throw exception
            throw new ClassCastException(getChildFragmentManager().toString()
                    + " must implement AddCourseListener");
        }
    }
}
