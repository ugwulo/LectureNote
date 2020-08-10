package dev.ugwulo.lecturenote.model.courses;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Course {

    private  String course_title;
    private  String course_code;
    private  String course_lecturer;

    public Course(){}

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getCourse_lecturer() {
        return course_lecturer;
    }

    public void setCourse_lecturer(String course_lecturer) {
        this.course_lecturer = course_lecturer;
    }

    public Course(String course_title, String course_code, String course_lecturer) {
        this.course_title = course_title;
        this.course_code = course_code;
        this.course_lecturer = course_lecturer;
    }
}
