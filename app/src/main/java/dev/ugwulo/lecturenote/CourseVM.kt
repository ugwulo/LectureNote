//package dev.ugwulo.lecturenote.view.home

import androidx.lifecycle.ViewModel
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.ClassSnapshotParser
import com.firebase.ui.database.FirebaseArray
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import dev.ugwulo.lecturenote.model.courses.Course

class CourseVM : ViewModel() {
    val query: Query = FirebaseDatabase.getInstance().reference.child("course").limitToLast(100)

    val courses = FirebaseArray(query, ClassSnapshotParser(Course::class.java))

    init {
        courses.addChangeEventListener(KeepAliveListener)
    }

    override fun onCleared() {
        super.onCleared()
        courses.removeChangeEventListener(KeepAliveListener)
    }

    private object KeepAliveListener : ChangeEventListener {
        override fun onDataChanged() = Unit

        override fun onChildChanged(type: ChangeEventType, snapshot: DataSnapshot, newIndex: Int, oldIndex: Int) = Unit

        override fun onError(e: DatabaseError) = Unit

    }
}