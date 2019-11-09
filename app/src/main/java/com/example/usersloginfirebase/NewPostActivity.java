package com.example.usersloginfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class NewPostActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    Button btnSavePost;
    EditText txtTitle, txtDescription;
    ProgressBar progressBar;
    ImageView imageView;
    Uri uriImage;
    String selectedImageUrl;
    FirebaseAuth mAuth;
    private View thisView;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        btnSavePost = (Button)findViewById(R.id.btnSavePost);
        txtTitle = (EditText)findViewById(R.id.txtPostTitle);
        txtDescription = (EditText)findViewById(R.id.txtPostDescription);
        progressBar = (ProgressBar)findViewById(R.id.progressBarNewPost);
        imageView = (ImageView)findViewById(R.id.imageView);
        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference("Posts");
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        thisView = (View)findViewById(R.id.viewNewPost);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnSavePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //savePostInfo();
                if(storageTask != null && storageTask.isInProgress()){
                    Snackbar.make(thisView, "Subida en progreso", Snackbar.LENGTH_SHORT).show();
                }else{
                    savePost();
                }
            }
        });
    }

    private String getImageExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void savePost() {
        final String postTitle = txtTitle.getText().toString().trim();
        final String postDescription = txtDescription.getText().toString().trim();
        if(postTitle.isEmpty()){
            txtTitle.setError("Titulo requerido");
            txtTitle.requestFocus();
            return;
        }

        if(uriImage != null){
            FirebaseUser user = mAuth.getCurrentUser();
            if(user != null){
                progressBar.setVisibility(View.VISIBLE);
                StorageReference imageReference = storageReference
                        .child(postTitle + System.currentTimeMillis()+"."+getImageExtension(uriImage));
                storageTask = imageReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(thisView, "Se guardado la publicación", Snackbar.LENGTH_SHORT).show();
                        PostClass post = new PostClass(postTitle, postDescription,
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                        String postId = databaseReference.push().getKey();
                        databaseReference.child(postId).setValue(post);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(thisView, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            Snackbar.make(thisView, "Selecciona una imagen", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), CHOOSE_IMAGE);
    }

    @Override
    //show in the image view the selected image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
/*
    private void uploadImage() {
        final StorageReference selectedImage =
                FirebaseStorage.getInstance().getReference("postImages/"+System.currentTimeMillis()+".jpg");
        if(uriImage != null){

            progressBar.setVisibility(View.VISIBLE);
            selectedImage.putFile(uriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    selectedImageUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    View view = findViewById(R.id.viewLogin);
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void savePostInfo(){
        String postTitle = txtTitle.getText().toString();
        if(postTitle.isEmpty()){
            txtTitle.setError("Titulo requerido");
            txtTitle.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null && selectedImageUrl != null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(postTitle).setPhotoUri(Uri.parse(selectedImageUrl)).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        View view = findViewById(R.id.viewNewPost);
                        Snackbar.make(view, "Publicación creada", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
  */

}
