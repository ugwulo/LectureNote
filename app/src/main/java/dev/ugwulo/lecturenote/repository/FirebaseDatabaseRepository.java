package dev.ugwulo.lecturenote.repository;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import dev.ugwulo.lecturenote.R;
import dev.ugwulo.lecturenote.model.courses.Course;
import dev.ugwulo.lecturenote.view.home.HomeFragment;
import dev.ugwulo.lecturenote.viewmodel.HomeViewModel;


public   class FirebaseDatabaseRepository {
    Resources mResources = Resources.getSystem();
   public Query getCourseReference(){
       Query query = FirebaseDatabase.getInstance().getReference().child(mResources.getString(R.string.dbnode_course));
       return query;
   }
}

