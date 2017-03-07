package com.example.kiran.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText lemail;
    private EditText lpassword;
    private Button llogin;
    private FirebaseAuth mauth;
    private DatabaseReference mdatabasechild;
    private ProgressDialog mprogress;
    private Button lnew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);


        mauth = FirebaseAuth.getInstance();
        mdatabasechild = FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabasechild.keepSynced(true);

        lemail = (EditText)findViewById(R.id.lemail);
        lpassword = (EditText)findViewById(R.id.lpassword);
        llogin = (Button)findViewById(R.id.llogin);
        mprogress = new ProgressDialog(this);
        lnew = (Button)findViewById(R.id.lnew);


        llogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);
                checklogin();
            }
        });
        lnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);
                goregister();
            }
        });


    }

    private void goregister() {
        Intent goregister = new Intent(Login.this, Register.class);
        goregister.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goregister);
    }

    private void checklogin() {
        String email = lemail.getText().toString().trim();
        String password = lpassword.getText().toString().trim();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mprogress.setMessage("Checking Login..");
            mprogress.show();
            mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mprogress.dismiss();
                        checkuserexist();
                    }else {
                        mprogress.dismiss();
                        Toast.makeText(Login.this,"Error Login",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //checking if the user already exist using uid
    private void checkuserexist() {
        final String uid = mauth.getCurrentUser().getUid();
        mdatabasechild.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)){
                    Intent login = new Intent(Login.this,MainActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);

                }else{
                    Intent setup = new Intent(Login.this,setupactivity.class);
                    setup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setup);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}





































































