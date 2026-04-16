package com.estrada.ccmsarchive;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private EditText project_title_field, desc_field, year_field, contributors_field;
    private AutoCompleteTextView course_field, tech_used_field, prof_instructor_field;
    private Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        project_title_field = findViewById(R.id.project_title_field);
        course_field = findViewById(R.id.course_field);
        desc_field = findViewById(R.id.desc_field);
        tech_used_field = findViewById(R.id.tech_used_field);
        year_field = findViewById(R.id.year_field);
        contributors_field = findViewById(R.id.contributors_field);
        prof_instructor_field = findViewById(R.id.prof_instructor_field);

        ImageButton btnAddMedia = findViewById(R.id.btnAddMedia);
        RecyclerView rvImagePreview = findViewById(R.id.rvImagePreview);
        btnPost = findViewById(R.id.btnPost);

        // Adapter Setup
        imageAdapter = new ImagePreviewAdapter(selectedImageUris);
        rvImagePreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImagePreview.setAdapter(imageAdapter);

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
        if (title.isEmpty() || selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Title and images are required", Toast.LENGTH_SHORT).show();
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

    private void saveToFirestore(List<String> base64Images) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Gamitin ang "users" collection imbes na masterlist dahil UID ang gamit doon
        db.collection("users").document(currentUid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Pag-combine sa firstName at lastName
                        String fName = documentSnapshot.getString("firstName");
                        String lName = documentSnapshot.getString("lastName");
                        String fullName = fName + " " + lName;

                        String program = documentSnapshot.getString("program");

                        Map<String, Object> project = new HashMap<>();
                        project.put("title", project_title_field.getText().toString().trim());
                        project.put("description", desc_field.getText().toString().trim());

                        project.put("uploader", fullName);
                        project.put("program", program);

                        project.put("year", year_field.getText().toString().trim());
                        project.put("instructor", prof_instructor_field.getText().toString().trim());
                        project.put("tech_used", tech_used_field.getText().toString().trim());
                        project.put("imageData", base64Images);
                        project.put("timestamp", FieldValue.serverTimestamp());

                        db.collection("Projects").add(project)
                                .addOnSuccessListener(doc -> {
                                    Toast.makeText(this, "Project Posted!", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    } else {
                        btnPost.setEnabled(true);
                        Toast.makeText(this, "Error: User data not found in 'users' collection", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    btnPost.setEnabled(true);
                    Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
                });
    }
}