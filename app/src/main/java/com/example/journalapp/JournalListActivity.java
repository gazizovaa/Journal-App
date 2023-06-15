package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journalapp.model.Journal;
import com.example.journalapp.ui.JournalRecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import util.JournalUser;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<Journal> journalList;

    private RecyclerView recyclerView;
    private TextView noPostTV;

    private CollectionReference collectionReference = firebaseFirestore.collection("Journal");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.postRecyclerView);
        noPostTV = findViewById(R.id.unlistedPosts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Arraylist of posts
        journalList = new ArrayList<>();
    }

    //Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_journalapp,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                if(firebaseUser != null && firebaseAuth != null){
                    startActivity(new Intent(
                            JournalListActivity.this,
                            AddJournalActivity.class
                    ));
                }
                break;
            case R.id.action_signOut:
                if(firebaseUser != null && firebaseAuth != null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(
                            JournalListActivity.this,
                            MainActivity.class
                    ));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //getting all journal posts

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("userId", JournalUser.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot journals : queryDocumentSnapshots){
                                Journal journal = journals.toObject(Journal.class);
                                journalList.add(journal);
                            }

                            //journal adapter for RecyclerView
                            JournalRecyclerViewAdapter journalRecyclerViewAdapter = new JournalRecyclerViewAdapter(
                                    JournalListActivity.this, journalList);
                            recyclerView.setAdapter(journalRecyclerViewAdapter);
                            journalRecyclerViewAdapter.notifyDataSetChanged();
                        }else{
                            noPostTV.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JournalListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}