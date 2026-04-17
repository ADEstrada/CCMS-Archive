package com.estrada.ccmsarchive;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private List<Uri> selectedImageUris = new ArrayList<>();
    private ImagePreviewAdapter imageAdapter;
    private static final int MAX_IMAGES = 5;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText project_title_field, desc_field;
    private AutoCompleteTextView course_field, prof_instructor_field, year_field;
    private MultiAutoCompleteTextView tech_used_field, contributors_field;
    private Button btnPost;

    private List<String> yearSuggestion = new ArrayList<>();
    private ArrayAdapter<String> yearAdapter;
    private List<String> courseSuggestions = new ArrayList<>();
    private ArrayAdapter<String> courseAdapter;
    private List<String> techSuggestions = new ArrayList<>();
    private ArrayAdapter<String> techAdapter;
    private List<String> userSuggestions = new ArrayList<>();
    private ArrayAdapter<String> userAdapter;
    private List<String> instructorSuggestions = new ArrayList<>();
    private ArrayAdapter<String> instructorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        project_title_field = findViewById(R.id.project_title_field);
        course_field = findViewById(R.id.course_field);
        desc_field = findViewById(R.id.desc_field);
        year_field = findViewById(R.id.year_field);
        contributors_field = findViewById(R.id.contributors_field);
        prof_instructor_field = findViewById(R.id.prof_instructor_field);
        course_field = findViewById(R.id.course_field);
        tech_used_field = findViewById(R.id.tech_used_field);

        // YEAR
        yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, yearSuggestion);
        year_field.setAdapter(yearAdapter);
        year_field.setThreshold(1);
        fetchYearData();

        // CONTRIBUTORS
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, userSuggestions);
        contributors_field.setAdapter(userAdapter);
        contributors_field.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        contributors_field.setThreshold(1);
        fetchRegisteredUsers();

        // COURSE
        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, courseSuggestions);
        course_field.setAdapter(courseAdapter);
        course_field.setThreshold(1);
        fetchCourseData();

        // TECH USED
        techAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, techSuggestions);
        tech_used_field.setAdapter(techAdapter);
        tech_used_field.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        tech_used_field.setThreshold(1);
        fetchTechData();

        // INSTRUCTOR
        instructorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, instructorSuggestions);
        prof_instructor_field.setAdapter(instructorAdapter);
        prof_instructor_field.setThreshold(1);
        fetchInstructorData();

        ImageButton btnAddMedia = findViewById(R.id.btnAddMedia);
        RecyclerView rvImagePreview = findViewById(R.id.rvImagePreview);
        btnPost = findViewById(R.id.btnPost);

        // IMAGE
        imageAdapter = new ImagePreviewAdapter(selectedImageUris);
        rvImagePreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImagePreview.setAdapter(imageAdapter);

        // PROF
        prof_instructor_field.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
        });

        btnAddMedia.setOnClickListener(v -> {
            if (selectedImageUris.size() >= MAX_IMAGES) {
                Toast.makeText(this, "Limit reached", Toast.LENGTH_SHORT).show();
            } else {
                pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btnPost.setOnClickListener(v -> validateAndProcess());
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGES), uris -> {
                if (!uris.isEmpty()) {
                    selectedImageUris.addAll(uris);
                    imageAdapter.notifyDataSetChanged();
                }
            });

    private void validateAndProcess() {
        String title = project_title_field.getText().toString().trim();
        String yearStr = year_field.getText().toString().trim();

        // 1. Basic empty check
        if (title.isEmpty() || selectedImageUris.isEmpty() || yearStr.isEmpty()) {
            Toast.makeText(this, "Title, Year, and images are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int inputYear = Integer.parseInt(yearStr);
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        if (inputYear > currentYear) {
            year_field.setError("Year cannot be in the future");
            Toast.makeText(this, "Please enter a valid year (up to " + currentYear + ")", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inputYear < 2015) {
            year_field.setError("Year is too old");
            return;
        }

        long totalSize = 0;
        for (Uri uri : selectedImageUris) {
            try {
                java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    totalSize += inputStream.available();
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (totalSize > 700000) {
            Toast.makeText(this, "Files are too large! Please remove some photos or use smaller ones.", Toast.LENGTH_LONG).show();
            return;
        }

        btnPost.setEnabled(false);
        Toast.makeText(this, "Saving project... Please wait.", Toast.LENGTH_SHORT).show();

        List<String> base64Images = new ArrayList<>();
        for (Uri uri : selectedImageUris) {
            String encoded = encodeImage(uri);
            if (encoded != null) base64Images.add(encoded);
        }

        saveToFirestore(base64Images);
    }

    private String encodeImage(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (IOException e) {
            return null;
        }
    }

    private void fetchRegisteredUsers() {
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                userSuggestions.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    String fName = doc.getString("firstName");
                    String lName = doc.getString("lastName");

                    if (fName != null && lName != null) {
                        userSuggestions.add(fName + " " + lName);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchCourseData() {
        db.collection("Courses").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                courseSuggestions.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    String id = doc.getId();
                    String name = doc.getString("courseName");

                    if (name != null) {
                        courseSuggestions.add(id + " - " + name);
                    }
                }
                courseAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load courses", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchYearData() {
        db.collection("Year").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                yearSuggestion.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    yearSuggestion.add(doc.getId());
                }
                yearAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchTechData() {
        db.collection("technologies").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                techSuggestions.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    techSuggestions.add(doc.getId());
                }
                techAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchInstructorData() {
        db.collection("Instructors").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                instructorSuggestions.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    String name = doc.getId();

                    instructorSuggestions.add(name);
                }
                instructorAdapter.notifyDataSetChanged();
            }
        });
    }

    private void saveToFirestore(List<String> base64Images) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
                        String program = documentSnapshot.getString("program");

                        Map<String, Object> project = new HashMap<>();
                        project.put("title", project_title_field.getText().toString().trim());
                        project.put("description", desc_field.getText().toString().trim());
                        project.put("uploader", fullName);
                        project.put("uploaderUid", currentUid);
                        project.put("program", program);
                        project.put("year", year_field.getText().toString().trim());
                        project.put("course", course_field.getText().toString().trim());
                        project.put("technologies", tech_used_field.getText().toString().trim());
                        project.put("instructor", prof_instructor_field.getText().toString().trim());
                        project.put("status", "Pending");
                        project.put("contributors", contributors_field.getText().toString().trim());
                        project.put("imageData", base64Images);
                        project.put("timestamp", FieldValue.serverTimestamp());

                        db.collection("Projects").add(project)
                                .addOnSuccessListener(doc -> {
                                    Toast.makeText(this, "Project Posted!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    btnPost.setEnabled(true);
                                    btnPost.setText("POST");

                                    if (e.getMessage().contains("too large")) {
                                        Toast.makeText(this, "Failed: Document is too large. Try removing a photo.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(this, "Post failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    btnPost.setEnabled(true);
                    btnPost.setText("POST");
                    Toast.makeText(this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                });
    }
}