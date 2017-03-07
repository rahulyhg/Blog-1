package com.example.kiran.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fasterxml.jackson.databind.Module;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class setupactivity extends AppCompatActivity {

    private ImageButton setupimg;
    private EditText setupname;
    private Button setupbtn;
    private Uri imageUri;
    private DatabaseReference mdatabase;
    private FirebaseAuth mauth;
    private StorageReference mstorage;
    private ProgressDialog mprogress;

    private static final int GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setupactivity);

        final Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);


        mprogress = new ProgressDialog(this);

        setupimg = (ImageButton)findViewById(R.id.setupimg);
        setupname = (EditText)findViewById(R.id.setupname);
        setupbtn = (Button)findViewById(R.id.setupbtn);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mauth = FirebaseAuth.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference().child("profile_images");

        setupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);
                setupaccount();
            }
        });

        setupimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,GALLERY);
            }
        });
    }

    private void setupaccount() {
        final String name = setupname.getText().toString().trim();
        final String uid = mauth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(name) && imageUri!= null) {

            mprogress.setMessage("Uploading..");
            mprogress.show();
            StorageReference filepath = mstorage.child(imageUri.getLastPathSegment());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloaduri = taskSnapshot.getDownloadUrl().toString();
                    mdatabase.child(uid).child("name").setValue(name);
                    mdatabase.child(uid).child("image").setValue(downloaduri);
                    mprogress.dismiss();
                    Intent login = new Intent(setupactivity.this, MainActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);

                }
            });



        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if(requestCode == GALLERY && requestCode == RESULT_OK){

             imageUri = data.getData();

            //image Crooping from gallery
           CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }*/


        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            imageUri = data.getData();
            Picasso.with(getApplicationContext()).load(imageUri).into(setupimg);

        }


        /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                setupimg.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/
    }
}









































































