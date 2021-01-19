package dev.ugwulo.lecturenote.viewmodel;

import android.content.Context;
import android.content.res.Resources;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Collections;
import java.util.List;

import dev.ugwulo.lecturenote.model.Course;

public class HomeViewModel extends ViewModel {

    private Context mContext;
//    public HomeViewModel(Context context){
//        this.mContext = context;
//    }
    Resources mResources = Resources.getSystem();
    private MutableLiveData<List<FirebaseRecyclerOptions<Course>>> options;

    public LiveData<List<FirebaseRecyclerOptions<Course>>> getOptions() {
        if (options == null) {
            options = new MutableLiveData<>();
            fetchValues();
        }
        return options;
    }



    private void fetchValues() {
        Query query1 = FirebaseDatabase.getInstance().getReference().child("course").limitToLast(100);

        options.setValue(Collections.singletonList(new FirebaseRecyclerOptions.Builder<Course>()
                .setQuery(query1, Course.class)
                .setLifecycleOwner((LifecycleOwner) mContext)
                .build()));
//        new FirebaseRecyclerOptions.Builder<Course>()
//                .setQuery(query1, Course.class);
//        //                .setQuery(query1, new SnapshotParser<Course>() {
////                    @NonNull
////                    @Override
////                    public Course parseSnapshot(@NonNull DataSnapshot snapshot) {
////                        return new Course(snapshot.child("course_title").getValue().toString(),
////                                snapshot.child("course_code").getValue().toString(),
////                                snapshot.child("course_lecturer").getValue().toString());
////                    }
////                })
    }
}