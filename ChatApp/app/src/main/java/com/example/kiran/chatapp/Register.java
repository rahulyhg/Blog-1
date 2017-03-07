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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText ruser;
    private EditText remail;
    private EditText rpassword;
    private Button rbutton;
    private FirebaseAuth mauth;
    private ProgressDialog mprogress;
    private DatabaseReference mdatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        final Animation animAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);


        mauth = FirebaseAuth.getInstance();
        ruser = (EditText)findViewById(R.id.rname);
        remail = (EditText)findViewById(R.id.remail);
        rpassword = (EditText)findViewById(R.id.rpassword);
        rbutton = (Button)findViewById(R.id.rbutton);
        mprogress = new ProgressDialog(this);



        rbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);
                startregister();
            }
        });

    }

    private void startregister() {
        final String name = ruser.getText().toString().trim();
        String email = remail.getText().toString().trim();
        String password = rpassword.getText().toString().trim();


        //signin using email and passowrd
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mprogress.setMessage("Signing Up...");
            mprogress.show();
            mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        String uid = mauth.getCurrentUser().getUid();
                        DatabaseReference cuser = mdatabase.child(uid);
                        cuser.child("name").setValue(name);
                        cuser.child("image").setValue("default");
                        mprogress.dismiss();

                        Intent login = new Intent(Register.this,setupactivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                    }
                }
            });

        }
    }
}
