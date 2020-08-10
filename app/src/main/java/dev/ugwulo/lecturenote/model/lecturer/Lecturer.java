package dev.ugwulo.lecturenote.model.lecturer;

public class Lecturer {
    private String lecturer_id;
    private String lecturer_name;
    private String email;
    private String staff_id;
    private String department;
    private String image_url;

    public Lecturer() {}

    public Lecturer(String lecturer_id, String lecturer_name, String email, String staff_id, String department, String image_url) {
        this.lecturer_id = lecturer_id;
        this.lecturer_name = lecturer_name;
        this.email = email;
        this.staff_id = staff_id;
        this.department = department;
        this.image_url = image_url;
    }

    public String getLecturer_id() {
        return lecturer_id;
    }

    public void setLecturer_id(String lecturer_id) {
        this.lecturer_id = lecturer_id;
    }

    public String getLecturer_name() {
        return lecturer_name;
    }

    public void setLecturer_name(String lecturer_name) {
        this.lecturer_name = lecturer_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "Lecturer{" +
                "lecturer_id='" + lecturer_id + '\'' +
                ", lecturer_name='" + lecturer_name + '\'' +
                ", email='" + email + '\'' +
                ", staff_id='" + staff_id + '\'' +
                ", department='" + department + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
