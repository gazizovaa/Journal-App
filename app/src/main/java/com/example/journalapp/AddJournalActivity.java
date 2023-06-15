package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journalapp.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.Date;
import util.JournalUser;

public class AddJournalActivity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;
    private ImageView addImageBtn;
    private TextView addUsernameTV;
    private ImageView addCameraBtn;
    private EditText addTitleET;
    private EditText addDescET;
    private ProgressBar progressBar;
    private Button saveBtn;

    //UserId and Username
    private String currentUserId;
    private String currentUserName;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    //Connection to FireStore
    private FirebaseFirestore  firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = firebaseFirestore.collection("Journal");
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        addUsernameTV = findViewById(R.id.usernameTextview);
        addCameraBtn = findViewById(R.id.postCameraButton);
        addTitleET = findViewById(R.id.titleEditText);
        addDescET = findViewById(R.id.descriptionEditText);
        progressBar = findViewById(R.id.postProgressBar);
        addImageBtn = findViewById(R.id.postImageView);
        saveBtn = findViewById(R.id.saveJournalButton);

        progressBar.setVisibility(View.INVISIBLE);

        if(JournalUser.getInstance() != null){
            currentUserId = JournalUser.getInstance().getUserId();
            currentUserName = JournalUser.getInstance().getUsername();

            addUsernameTV.setText(currentUserName);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser != null){

                }else{

                }
            }
        };

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJournal();
            }
        });

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting image from gallery
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("img/*");
                startActivityForResult(intent,GALLERY_CODE);
            }
        });
    }

    //saving journals inside the storage and linked it with FireStorage
    private void saveJournal() {
        final String titleOfPost = addTitleET.getText().toString().trim();
        final String comments = addDescET.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(titleOfPost) && !TextUtils.isEmpty(comments) && imageUri != null){

            //saving the path of images in the storage
            final StorageReference filePath = storageReference
                    .child("journal_images")
                    .child("my_image" + Timestamp.now().getSeconds());

            //uploading the images
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    //creating objects of the Journal
                                    //creating Journal model class
                                    Journal journal = new Journal();
                                    journal.setTitleOfPost(titleOfPost);
                                    journal.setComments(comments);
                                    journal.setImageUrl(imageUrl);
                                    journal.setTimestamp(new Timestamp(new Date()));
                                    journal.setUserId(currentUserId);
                                    journal.setUserName(currentUserName);

                                    //invoking Collection Reference
                                    collectionReference.add(journal)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    startActivity(new Intent(AddJournalActivity.this,
                                                            JournalListActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Failed!: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if(data != null){
                //getting the actual image path
                imageUri = data.getData();
                //displaying the image
                addImageBtn.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}