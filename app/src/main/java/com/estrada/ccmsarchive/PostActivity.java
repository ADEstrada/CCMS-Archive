package com.estrada.ccmsarchive;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
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
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

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
    private TextView headerTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        headerTitle = findViewById(R.id.header_title);

        if (headerTitle != null) {
            headerTitle.setText(R.string.title_post);
        }
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
        contributors_field.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() {
            @Override
            public int findTokenStart(CharSequence text, int cursor) {
                int i = cursor;
                while (i > 0 && text.charAt(i - 1) != ',') { i--; }
                while (i < cursor && text.charAt(i) == ' ') { i++; }
                return i;
            }
            @Override
            public int findTokenEnd(CharSequence text, int cursor) {
                int i = cursor;
                int len = text.length();
                while (i < len) {
                    if (text.charAt(i) == ',') return i;
                    else i++;
                }
                return len;
            }
            @Override
            public CharSequence terminateToken(CharSequence text) {
                int i = text.length();
                while (i > 0 && text.charAt(i - 1) == ' ') { i--; }

                if (i > 0 && text.charAt(i - 1) == ',') {
                    return text;
                } else {
                    return text;
                }
            }
        });

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
        tech_used_field.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() {
            @Override
            public int findTokenStart(CharSequence text, int cursor) {
                int i = cursor;
                while (i > 0 && text.charAt(i - 1) != ',') { i--; }
                while (i < cursor && text.charAt(i) == ' ') { i++; }
                return i;
            }
            @Override
            public int findTokenEnd(CharSequence text, int cursor) {
                int i = cursor;
                int len = text.length();
                while (i < len) {
                    if (text.charAt(i) == ',') return i;
                    else i++;
                }
                return len;
            }
            @Override
            public CharSequence terminateToken(CharSequence text) {
                int i = text.length();
                while (i > 0 && text.charAt(i - 1) == ' ') { i--; }

                if (i > 0 && text.charAt(i - 1) == ',') {
                    return text;
                } else {
                    return text;
                }
            }
        });
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

        if (title.isEmpty() || selectedImageUris.isEmpty() || yearStr.isEmpty()) {
            Toast.makeText(this, "Title, Year, and images are required", Toast.LENGTH_SHORT).show();
            return;
        }

        //comment ko muna itong inputyear and current year, may itatry lang ako
        // int inputYear = Integer.parseInt(yearStr);
        // int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        if (!yearStr.matches("\\d{4}-\\d{4}")) {
            year_field.setError("Year format should be: 0000-0000");
            Toast.makeText(this, "Please enter a valid academic year (ex. 2025-2026)", Toast.LENGTH_SHORT).show();
            return;
        }

        /* if (inputYear < 2015) {
            year_field.setError("Year is too old");
            return;
        } */


        btnPost.setEnabled(false);
        Toast.makeText(this, "Saving project... Please wait.", Toast.LENGTH_SHORT).show();

        List<String> uploadedUrls = new ArrayList<>();
        try {
            Map config = new HashMap();
            config.put("cloud_name", "dmmxmvhiz");
            config.put("api_key", "861187866891224");
            config.put("api_secret", "swm-eqgYt72R58Oa1APxuC_hEUY");
            MediaManager.init(this, config);
        } catch (Exception e) {}

        for (Uri uri : selectedImageUris) {
            MediaManager.get().upload(uri)
                    .option("upload_preset", "ml_default")
                    .callback(new UploadCallback() {
                        @Override public void onStart(String requestId) {}
                        @Override public void onProgress(String requestId, long bytes, long totalBytes) {}
                        @Override public void onSuccess(String requestId, Map resultData) {
                            uploadedUrls.add((String) resultData.get("secure_url"));
                            if (uploadedUrls.size() == selectedImageUris.size()) {
                                saveToFirestore(uploadedUrls);
                            }
                        }
                        @Override public void onError(String requestId, ErrorInfo error) {
                            btnPost.setEnabled(true);
                            btnPost.setText("POST");
                        }
                        @Override public void onReschedule(String requestId, ErrorInfo error) {}
                    }).dispatch();
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

    private void sendEmailToProfessor(String title, String student, String desc, String projectId, String profEmail) {

        String scriptUrl = "https://script.google.com/macros/s/AKfycbylCYSDCCW30SBE5zeGKy8Y4hgUP3T6TWLNfrstTgoMNSq4rJf0FRHwP61pjQteDRgA/exec";
        String emailJsUrl = "https://api.emailjs.com/api/v1.0/email/send";


        String approveLink = scriptUrl + "?id=" + projectId + "&action=Approved";
        String rejectLink = scriptUrl + "?id=" + projectId + "&action=Rejected";

        org.json.JSONObject jsonBody = new org.json.JSONObject();
        try {
            jsonBody.put("service_id", "service_ikm3exh");
            jsonBody.put("template_id", "template_hbiz8r7");
            jsonBody.put("user_id", "j7ae_X2G5hcKUvhM3");

            org.json.JSONObject templateParams = new org.json.JSONObject();
            templateParams.put("project_title", title);
            templateParams.put("student_name", student);
            templateParams.put("project_description", desc);
            templateParams.put("approve_link", approveLink);
            templateParams.put("reject_link", rejectLink);
            templateParams.put("to_email", profEmail);
            templateParams.put("from_name", "CCMS Archive");
            templateParams.put("date", new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(new java.util.Date()));

            jsonBody.put("template_params", templateParams);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }

        com.android.volley.toolbox.StringRequest request = new com.android.volley.toolbox.StringRequest(
                com.android.volley.Request.Method.POST, emailJsUrl,
                response -> {
                    android.util.Log.d("EmailJS", "Success! Response: " + response);
                    Toast.makeText(PostActivity.this, "Project submitted for approval!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    android.util.Log.e("EmailJS", "Failed: " + error.toString());
                    btnPost.setEnabled(true);
                }
        ) {
            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void saveToFirestore(List<String> imageUrls) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");
                        String program = documentSnapshot.getString("program");

                        String rawTech = tech_used_field.getText().toString().trim();
                        String rawContributors = contributors_field.getText().toString().trim();

                        rawTech = rawTech.replaceAll(",\\s*$", "");
                        rawContributors = rawContributors.replaceAll(",\\s*$", "");

                        Map<String, Object> project = new HashMap<>();
                        project.put("title", project_title_field.getText().toString().trim());
                        project.put("description", desc_field.getText().toString().trim());
                        project.put("uploader", fullName);
                        project.put("uploaderUid", currentUid);
                        project.put("program", program);
                        project.put("year", year_field.getText().toString().trim());
                        project.put("course", course_field.getText().toString().trim());
                        project.put("technologies", rawTech);
                        project.put("contributors", rawContributors);
                        project.put("instructor", prof_instructor_field.getText().toString().trim());
                        project.put("status", "Pending");
                        project.put("imageData", imageUrls);
                        project.put("timestamp", FieldValue.serverTimestamp());

                        // I-save muna ang project sa Pending_Projects
                        db.collection("Pending_Projects").add(project)
                                .addOnSuccessListener(doc -> {
                                    String generatedProjectId = doc.getId();
                                    String selectedProfName = prof_instructor_field.getText().toString().trim();

                                    // --- SMART CHECK: May account ba si Prof sa App? ---
                                    db.collection("users")
                                            .whereEqualTo("role", "Instructor")
                                            .get()
                                            .addOnSuccessListener(querySnapshot -> {
                                                String instructorUid = null;
                                                for (com.google.firebase.firestore.DocumentSnapshot userDoc : querySnapshot) {
                                                    String regName = userDoc.getString("firstName") + " " + userDoc.getString("lastName");
                                                    if (regName.equalsIgnoreCase(selectedProfName)) {
                                                        instructorUid = userDoc.getId();
                                                        break;
                                                    }
                                                }

                                                if (instructorUid != null) {
                                                    // MAY ACCOUNT: Update project doc para lumabas sa ApprovalFragment ni Prof
                                                    db.collection("Pending_Projects").document(generatedProjectId)
                                                            .update("instructorUid", instructorUid)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Toast.makeText(this, "Submitted! Prof will review this in-app.", Toast.LENGTH_LONG).show();
                                                                finish();
                                                            });
                                                } else {
                                                    // WALANG ACCOUNT: Gamitin ang original Email logic mo
                                                    db.collection("Instructors").document(selectedProfName).get()
                                                            .addOnSuccessListener(profDoc -> {
                                                                if (profDoc.exists()) {
                                                                    String actualProfEmail = profDoc.getString("email");
                                                                    sendEmailToProfessor(
                                                                            project.get("title").toString(),
                                                                            fullName,
                                                                            project.get("description").toString(),
                                                                            generatedProjectId,
                                                                            actualProfEmail
                                                                    );
                                                                } else {
                                                                    Toast.makeText(this, "Instructor email not found in masterlist.", Toast.LENGTH_SHORT).show();
                                                                    btnPost.setEnabled(true);
                                                                }
                                                            });
                                                }
                                            });
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    btnPost.setEnabled(true);
                    Toast.makeText(this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                });
    }
}