package dev.ugwulo.lecturenote.view.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.ClassSnapshotParser;
import com.firebase.ui.database.FirebaseArray;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import dev.ugwulo.lecturenote.model.Course;

public class CourseViewModel extends ViewModel {

    Query query = FirebaseDatabase.getInstance().getReference().child("course").limitToLast(100);
    FirebaseArray<Course> courses = new FirebaseArray<>(query, new ClassSnapshotParser<>(Course.class));

    public CourseViewModel(){
        courses.addChangeEventListener(new KeepAliveListener());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        courses.removeChangeEventListener(new KeepAliveListener());
    }

     private static class KeepAliveListener implements ChangeEventListener{

         @Override
         public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {

         }

         @Override
         public void onDataChanged() {

         }

         @Override
         public void onError(@NonNull DatabaseError databaseError) {

         }
     }
}
