package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalUser;

public class SignUpActivity extends AppCompatActivity {

    EditText email_create;
    EditText password_create;
    EditText userName_create;
    Button signUpBtn;

    //Firebase Authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase Connection
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = firebaseFirestore.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        //Firebase Auth requires Google Account on the device to run successfully


        email_create = findViewById(R.id.emailCreation);
        password_create = findViewById(R.id.passwordCreation);
        userName_create = findViewById(R.id.userName);
        signUpBtn = findViewById(R.id.signUp);

        //Authentication
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null){
                    //user already login

                }else{
                    //no user login
                }
            }
        };

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(email_create.getText().toString())
                && !TextUtils.isEmpty(password_create.getText().toString())){
                    String email = email_create.getText().toString().trim();
                    String password = password_create.getText().toString().trim();
                    String username = userName_create.getText().toString().trim();

                    createUserAccount(email,password,username);
                }else{
                    Toast.makeText(SignUpActivity.this, "Empty Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUserAccount(String email, String password, final String username) {
        if(!TextUtils.isEmpty(email_create.getText().toString())
                && !TextUtils.isEmpty(password_create.getText().toString())){
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // take a user to the next activity - AddJournalActivity
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserId = currentUser.getUid();

                                // creating a user map
                                Map<String,String> userObject = new HashMap<>();
                                userObject.put("userId",currentUserId);
                                userObject.put("username",username);

                                // adding users to FireStore
                                collectionReference.add(userObject)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(Objects.requireNonNull(task.getResult().exists())){
                                                                    String name = task.getResult().getString("username");

                                                                    //if a user registers successfully
                                                                    //then move to AddJournalActivity

                                                                    //Getting use of Global Journal USER
                                                                    JournalUser journalUser = JournalUser.getInstance();
                                                                    journalUser.setUserId(currentUserId);
                                                                    journalUser.setUsername(name);


                                                                    Intent intent = new Intent(SignUpActivity.this,
                                                                            AddJournalActivity.class);
                                                                    intent.putExtra("username",name);
                                                                    intent.putExtra("userId",currentUserId);
                                                                    startActivity(intent);
                                                                }else{

                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}