package com.example.kiran.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class singlepost extends AppCompatActivity {

    private  String mpost_key = null;
    private DatabaseReference mDatabase;
    private ImageView singleimage;
   // private TextView singleuser;
    private  TextView singletitle;
    private  TextView singledesc;
    private Button singlebtn;
    private FirebaseAuth singleauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepost);

        final Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);


        singleimage = (ImageView) findViewById(R.id.singleimage);
       // singleuser = (TextView) findViewById(R.id.singleuser);
        singletitle = (TextView) findViewById(R.id.singletitle);
        singledesc = (TextView) findViewById(R.id.singledesc);
        singlebtn = (Button) findViewById(R.id.singlebtn);

        singleauth = FirebaseAuth.getInstance();

        mpost_key = getIntent().getExtras().getString("blog_id");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.child(mpost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title =(String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("ImageUrl").getValue();
                //String post_user = (String) dataSnapshot.child("username").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                singletitle.setText(post_title);
                //singleuser.setText(post_user);
                singledesc.setText(post_desc);

                Picasso.with(singlepost.this).load(post_image).resize(385,380).into(singleimage);
                //Picasso.with(singlepost.this).load(post_image).into(singleimage);

                if(singleauth.getCurrentUser().getUid().equals(post_uid) ){
                    singlebtn.setVisibility(View.VISIBLE);
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        singlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);

                mDatabase.child(mpost_key).removeValue();

                Intent back = new Intent(singlepost.this,MainActivity.class);
               back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(back);
            }
        });
    }
}










































































