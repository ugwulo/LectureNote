package dev.ugwulo.lecturenote.view.home;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.ActivityMainBinding;
import dev.ugwulo.lecturenote.databinding.CourseItemBinding;
import dev.ugwulo.lecturenote.databinding.FragmentHomeBinding;
import dev.ugwulo.lecturenote.model.Course;
import dev.ugwulo.lecturenote.view.AddCourseDialogFragment;

public class HomeFragment extends Fragment implements AddCourseDialogFragment.AddCourseListener {

    private CourseViewModel mCourseViewModel;

    Course mCourse = new Course();

    private RecyclerView mRecyclerView;
    FragmentHomeBinding mFragmentHomeBinding;
    private CourseItemBinding mCourseItemBinding;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.course_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(getLayoutInflater());
        mCourseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        return mFragmentHomeBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(mFragmentHomeBinding.toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = mFragmentHomeBinding.recyclerView;
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        fetchValues();
        mFragmentHomeBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        DialogFragment dialogFragment = new AddCourseDialogFragment();
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getParentFragmentManager(), "AddCourseDialogFragment");
    }

    private void fetchValues() {


//        Query query1 = FirebaseDatabase.getInstance().getReference().child("course").limitToLast(100);

        FirebaseRecyclerOptions<Course> options = new FirebaseRecyclerOptions.Builder<Course>()
                .setSnapshotArray(mCourseViewModel.courses)
                .setLifecycleOwner(getActivity())
                .build();

        FirebaseRecyclerAdapter<Course, CourseHolder> adapter = new FirebaseRecyclerAdapter<Course, CourseHolder>(options){
            @NonNull
            @Override
            public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                mCourseItemBinding = CourseItemBinding.inflate(LayoutInflater.from(getActivity()), parent, false);
                return new CourseHolder(mCourseItemBinding);
            }

            @Override
            public void onBindViewHolder(@NonNull CourseHolder holder, int position, @NonNull Course course) {
                holder.bind(course);
                holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         Bundle bundle = new Bundle();
                         bundle.putString(getString(R.string.course_code_bundle), getItem(position).getCourse_code());
                         Navigation.findNavController(view).navigate(R.id.action_nav_home_to_courseDetailsFragment, bundle);
                     }
                 });
            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDialogPositiveClick(String title, String code, String lecturer) {
        if (!TextUtils.isEmpty(code)){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_course)).push();
            mCourse.setCourse_title(title);
            mCourse.setCourse_code(code);
            mCourse.setCourse_lecturer(lecturer);
            databaseReference.setValue(mCourse)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), code + " added successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Failed to add course", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else Toast.makeText(getActivity(), "No Course Added" + code, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
    }

    public static class CourseHolder extends RecyclerView.ViewHolder {
        CourseItemBinding binding;

        public CourseHolder(@NonNull CourseItemBinding courseItemBinding) {
            super(courseItemBinding.getRoot());

            binding = courseItemBinding;
        }

        public void bind(Course course){
            setCourseCode(course.getCourse_code());
            setCourseTitle(course.getCourse_title());
            setCourseLecturer(course.getCourse_lecturer());
        }
        private void setCourseTitle(String title){
            binding.tvCourseTitle.setText(title);
        }
        private void setCourseCode(String courseCode){
            binding.tvCourseCode.setText(courseCode);
        }

        private void setCourseLecturer(String courseLecturer){
            binding.tvLecturerName.setText(courseLecturer);
        }

    }

}