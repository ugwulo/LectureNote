package dev.ugwulo.lecturenote.repository;

import android.content.res.Resources;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import dev.ugwulo.lecturenote.R;


public   class FirebaseDatabaseRepository {
    Resources mResources = Resources.getSystem();
   public Query getCourseReference(){
       Query query = FirebaseDatabase.getInstance().getReference().child(mResources.getString(R.string.dbnode_course));
       return query;
   }
}

