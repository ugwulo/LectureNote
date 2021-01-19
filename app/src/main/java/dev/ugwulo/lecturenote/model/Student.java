package dev.ugwulo.lecturenote.model;

public class Student {

    private String student_id;
    private String student_name;
    private String reg_number;
    private String email;
    private String department;
    private String level;
    private String image_url;

    public Student() {
    }

    public Student(String student_id, String student_name, String reg_number, String email, String department, String level, String image_url) {
        this.student_id = student_id;
        this.student_name = student_name;
        this.reg_number = reg_number;
        this.email = email;
        this.department = department;
        this.level = level;
        this.image_url = image_url;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getReg_number() {
        return reg_number;
    }

    public void setReg_number(String reg_number) {
        this.reg_number = reg_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "Student{" +
                "student_name='" + student_name + '\'' +
                ", reg_number='" + reg_number + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", level='" + level + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
