package com.estrada.ccmsarchive;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonHelper {

    // Helper para basahin ang file mula sa assets folder
    private static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }


    public static List<Student> getStudentList(Context context) {
        String json = getJsonFromAssets(context, "student_masterlist.json");
        if (json == null) return new ArrayList<>();

        // I-parse bilang Map dahil ang keys ay yung IDs (e.g., "24-0384")
        Type type = new TypeToken<Map<String, Student>>(){}.getType();
        Map<String, Student> studentMap = new Gson().fromJson(json, type);

        List<Student> studentList = new ArrayList<>();
        if (studentMap != null) {
            for (Map.Entry<String, Student> entry : studentMap.entrySet()) {
                Student s = entry.getValue();
                s.setStudentId(entry.getKey()); // I-set yung ID mula sa key
                studentList.add(s);
            }
        }
        return studentList;
    }



    public static List<Instructor> getInstructorMasterList(Context context) {
        // Siguraduhing "instructors_data.json" ang filename sa assets
        String json = getJsonFromAssets(context, "instructors_data.json");
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        // Map ang ginagamit dahil ang Keys ay names (e.g., "Ann Dominique Estrada")
        Type type = new TypeToken<Map<String, Instructor>>(){}.getType();
        Map<String, Instructor> instructorMap = gson.fromJson(json, type);

        List<Instructor> instructorList = new ArrayList<>();
        if (instructorMap != null) {
            for (Map.Entry<String, Instructor> entry : instructorMap.entrySet()) {
                Instructor i = entry.getValue();

                // Ang Key ay Full Name. Hatiin natin ito para sa firstName at lastName
                String fullName = entry.getKey();
                String[] nameParts = fullName.split(" ");

                if (nameParts.length >= 2) {
                    i.setFirstName(nameParts[0]); // Unang salita
                    i.setLastName(nameParts[nameParts.length - 1]); // Huling salita
                } else {
                    i.setFirstName(fullName);
                    i.setLastName("");
                }

                instructorList.add(i);
            }
        }
        return instructorList;
    }

    public static List<Course> getCourseList(Context context) {
        String json = getJsonFromAssets(context, "courses_data.json");
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<Map<String, Course>>(){}.getType();
        Map<String, Course> courseMap = new Gson().fromJson(json, type);

        List<Course> courseList = new ArrayList<>();
        if (courseMap != null) {
            for (Map.Entry<String, Course> entry : courseMap.entrySet()) {
                Course c = entry.getValue();
                c.setCourseCode(entry.getKey()); // Key ang magiging Course Code
                courseList.add(c);
            }
        }
        return courseList;
    }
}
