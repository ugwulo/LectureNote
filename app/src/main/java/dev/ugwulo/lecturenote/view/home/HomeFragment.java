package dev.ugwulo.lecturenote.view.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.databinding.CourseItemBinding;
import dev.ugwulo.lecturenote.databinding.FragmentHomeBinding;
import dev.ugwulo.lecturenote.model.courses.Course;
import dev.ugwulo.lecturenote.view.AddCourseDialogFragment;

public class HomeFragment extends Fragment implements AddCourseDialogFragment.AddCourseListener {

    Course mCourse = new Course();
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private FragmentHomeBinding mHomeBinding;
    private CourseItemBinding mCourseItemBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mHomeBinding = FragmentHomeBinding.inflate(getLayoutInflater());
        View root = mHomeBinding.getRoot();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = mHomeBinding.recyclerView;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mHomeBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        init();
        return root;
    }

    private void showDialog() {
        DialogFragment dialogFragment = new AddCourseDialogFragment();
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getParentFragmentManager(), "AddCourseDialogFragment");
    }

    private void init() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_course));
////        initialize course node
//        Course course = new Course("Web Development", "COM 312", "Mr. Onyike");
//        mDatabaseReference.setValue(course);
        /**
         * get data to populate recyclerview
         */
        fetchValues();
    }

    private void fetchValues() {
        Query query1 = mDatabaseReference;

        FirebaseRecyclerOptions<Course> options = new FirebaseRecyclerOptions.Builder<Course>()
                .setQuery(query1, new SnapshotParser<Course>() {
                    @NonNull
                    @Override
                    public Course parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new Course(snapshot.child("course_title").getValue().toString(),
                                snapshot.child("course_code").getValue().toString(),
                                snapshot.child("course_lecturer").getValue().toString());
                    }
                })
                .build();

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(options){
            @NonNull
            @Override
            public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                mCourseItemBinding = CourseItemBinding.inflate(LayoutInflater.from(getActivity()), parent, false);
                View view = mCourseItemBinding.getRoot();
                return new CourseViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull CourseViewHolder holder, int position, Course course) {
                holder.bind(course);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        mRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        mFirebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFirebaseRecyclerAdapter != null){
            mFirebaseRecyclerAdapter.stopListening();
        }
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


    public class CourseViewHolder extends RecyclerView.ViewHolder {
        public CourseViewHolder(@NonNull View view) {
            super(view);
        }

        public void bind(Course course){
            setCourseCode(course.getCourse_code());
            setCourseTitle(course.getCourse_title());
            setCourseLecturer(course.getCourse_lecturer());
        }
        private void setCourseTitle(String title){
            mCourseItemBinding.tvCourseTitle.setText(title);
        }
        private void setCourseCode(String courseCode){
            mCourseItemBinding.tvCourseCode.setText(courseCode);
        }

        private void setCourseLecturer(String courseLecturer){
            mCourseItemBinding.tvLecturerName.setText(courseLecturer);
        }
    }
}