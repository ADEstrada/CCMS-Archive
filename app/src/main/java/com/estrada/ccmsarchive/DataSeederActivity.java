package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSeederActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_seeder);

        db = FirebaseFirestore.getInstance();
        statusText = findViewById(R.id.statusText);

        Button btnUploadStudents = findViewById(R.id.btnUploadStudents);
        Button btnUploadInstructors = findViewById(R.id.btnUploadInstructors);
        Button btnUploadCourses = findViewById(R.id.btnUploadCourses);

        btnUploadStudents.setOnClickListener(v -> seedStudentData());
        btnUploadInstructors.setOnClickListener(v -> seedInstructorData());
        btnUploadCourses.setOnClickListener(v -> seedCourseData());
    }

    private void seedStudentData() {
        List<Student> students = JsonHelper.getStudentList(this);

        if (students != null && !students.isEmpty()) {
            for (Student s : students) {
                Map<String, Object> data = new HashMap<>();

                data.put("firstName", s.getFirstName());
                data.put("lastName", s.getLastName());

                data.put("name", s.getFullName());
                data.put("program", s.getProgram());
                data.put("yearLevel", s.getYear());

                db.collection("student_masterlist")
                        .document(s.getStudentId())
                        .set(data)
                        .addOnSuccessListener(aVoid -> Log.d("SEEDER", "Uploaded: " + s.getStudentId()))
                        .addOnFailureListener(e -> Log.e("SEEDER", "Error: " + e.getMessage()));
            }
            statusText.setText("Status: Student Masterlist Seeded!");
            Toast.makeText(this, "Students uploaded!", Toast.LENGTH_SHORT).show();
        } else {
            statusText.setText("Status: No student data found in JSON.");
        }
    }

    private void seedInstructorData() {
        List<Instructor> instructors = JsonHelper.getInstructorMasterList(this);

        if (instructors != null && !instructors.isEmpty()) {
            int total = instructors.size();
            final int[] count = {0}; 

            for (Instructor i : instructors) {
                Map<String, Object> data = new HashMap<>();
                data.put("email", i.getEmail());
                data.put("firstName", i.getFirstName());
                data.put("lastName", i.getLastName());
                data.put("academicRank", i.getRank());

                String fullName = i.getFirstName() + " " + i.getLastName();

                db.collection("Instructors") 
                        .document(fullName)
                        .set(data)
                        .addOnSuccessListener(aVoid -> {
                            count[0]++;
                            if (count[0] == total) {
                                Toast.makeText(this, "All " + total + " instructors uploaded successfully!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(e -> Log.e("FIREBASE", "Failed: " + fullName));
            }
        } else {
            Toast.makeText(this, "No data found in JSON", Toast.LENGTH_SHORT).show();
        }
    }

    // FOR COURSES
    private void seedCourseData() {
        List<Course> courses = JsonHelper.getCourseList(this);

        if (courses != null && !courses.isEmpty()) {
            for (Course c : courses) {
                Map<String, Object> data = new HashMap<>();
                data.put("courseName", c.getCourseName());

                db.collection("Courses")
                        .document(c.getCourseCode())
                        .set(data)
                        .addOnSuccessListener(aVoid -> Log.d("SEEDER", "Course Added: " + c.getCourseCode()));
            }
            statusText.setText("Status: Courses Seeded!");
            Toast.makeText(this, "Courses masterlist updated!", Toast.LENGTH_SHORT).show();
        }
    }

}
