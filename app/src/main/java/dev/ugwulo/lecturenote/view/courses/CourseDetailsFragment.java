package dev.ugwulo.lecturenote.view.courses;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.FragmentCourseDetailsBinding;
import dev.ugwulo.lecturenote.util.Settings;
import dev.ugwulo.lecturenote.view.MainActivity;

/**
 * Fragment {@link CourseDetailsFragment} to display course contents for a particular course
 * Use the {@link CourseDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseDetailsFragment extends Fragment implements View.OnClickListener{

    FragmentCourseDetailsBinding mCourseDetailsBinding;
    private String mCourseCode;

    public CourseDetailsFragment() {}

    public static CourseDetailsFragment newInstance() {
        return new CourseDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseCode = getArguments().getString("course code");
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCourseDetailsBinding = FragmentCourseDetailsBinding.inflate(getLayoutInflater());

        showDefaultView();
        return mCourseDetailsBinding.getRoot();
    }

    // TODO display a default view when no notes are added
    private void showDefaultView() {
        if (Settings.isStudentLogin() && Settings.isFirstTimeLaunch()) {
            mCourseDetailsBinding.studentDefaultView.setVisibility(View.VISIBLE);
            mCourseDetailsBinding.sendFile.setVisibility(View.GONE);
        } else mCourseDetailsBinding.mainView.setVisibility(View.VISIBLE);

        if (Settings.isLecturerLogin() && Settings.isFirstTimeLaunch()){
            mCourseDetailsBinding.lecturerDefaultView.setVisibility(View.VISIBLE);
            mCourseDetailsBinding.addNotes.setOnClickListener(this);
        } else mCourseDetailsBinding.mainView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        /** set the Actionbar Title to the course code passed from the bundle **/
        ((MainActivity) getActivity()).setActionBarTitle(mCourseCode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_notes:
                uploadLectureNotes();
                break;
            case R.id.send_message:
//                postMessage();
        }
    }

    // TODO handle notes upload from a lecturer
    private void uploadLectureNotes() {

    }
}