package com.example.usersloginfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ProgressBar progressBar;
    private TextView txtAvailablePosts;

    private DatabaseReference databaseReference;
    private List<PostClass> posts;
    private View view;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = (RecyclerView)findViewById(R.id.recyclewViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        txtAvailablePosts = (TextView)findViewById(R.id.txtAvailablePosts);
        progressBar = (ProgressBar)findViewById(R.id.progressBarUserActivity);
        view = (View)findViewById(R.id.viewUserActivity);

        posts = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    PostClass post = postSnapshot.getValue(PostClass.class);
                    posts.add(post);
                }

                imageAdapter = new ImageAdapter(UserActivity.this, posts);
                recyclerView.setAdapter(imageAdapter) ;
                if(posts.size() == 0){
                    txtAvailablePosts.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(view, databaseError.getMessage(), Snackbar.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });
    }
}
