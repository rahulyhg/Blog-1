package com.example.kiran.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class PostActivity extends AppCompatActivity {

    private ImageButton imageselect;
    private static final int GALLERY_REQUEST = 1;
    private EditText title;
    private EditText desc;
     private Button submit;
    private Uri imageUri = null;
    private StorageReference mref;
    private ProgressDialog mprogress;
    private DatabaseReference mdatabase;
    private FirebaseAuth mauth;
    private FirebaseUser muser;
    private  DatabaseReference mdatabaseuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mauth = FirebaseAuth.getInstance();
        muser = mauth.getCurrentUser();

        mref = FirebaseStorage.getInstance().getReference();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        imageselect = (ImageButton)findViewById(R.id.imageselect);
        title = (EditText)findViewById(R.id.title);
        desc = (EditText)findViewById(R.id.descp);
        submit = (Button)findViewById(R.id.submit);
        mprogress = new ProgressDialog(this);

        mdatabaseuser = FirebaseDatabase.getInstance().getReference().child("Users").child(muser.getUid());


        imageselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_REQUEST);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post();
            }
        });
    }

    private void post() {
        mprogress.setMessage("Uploading");
        mprogress.show();
        final String title_val = title.getText().toString().trim();
        final String desc_val = desc.getText().toString().trim();
        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && imageUri != null){

            StorageReference filepath = mref.child("BlogPhotos").child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloaduri = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newpost = mdatabase.push();


                    //used to see if any error occur on posting name
                    mdatabaseuser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newpost.child("title").setValue(title_val);
                            newpost.child("desc").setValue(desc_val);
                            newpost.child("ImageUrl").setValue(downloaduri.toString());
                            newpost.child("uid").setValue(muser.getUid());
                            newpost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(PostActivity.this,MainActivity.class));
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mprogress.dismiss();


                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){
            imageUri = data.getData();

            Picasso.with(PostActivity.this).load(imageUri).resize(385,200).into(imageselect);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}








































































