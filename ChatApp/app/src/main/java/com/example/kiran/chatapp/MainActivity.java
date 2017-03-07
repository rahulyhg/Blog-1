package com.example.kiran.chatapp;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    private RecyclerView rec;
    private DatabaseReference mDatabase;

    private FirebaseAuth mauth;
    private FirebaseAuth.AuthStateListener mlistener;

    private DatabaseReference mdatabasechild;

    private Boolean mprocesslike = false;

    private DatabaseReference mdatabaselike;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mauth = FirebaseAuth.getInstance();

        mlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){
                    Intent login = new Intent(MainActivity.this,Login.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);

                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase.keepSynced(true);

        //saves data offline
        mdatabasechild = FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabasechild.keepSynced(true);

        mdatabaselike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mdatabaselike.keepSynced(true);

        rec = (RecyclerView) findViewById(R.id.blog_list);
        rec.setHasFixedSize(true);
        rec.setLayoutManager(new LinearLayoutManager(this));

        checkuserexist();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mlistener);

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(

                Blog.class,
                R.layout.blog_card,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                //get the refrence to a selected user
                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImageUrl(getApplicationContext(),model.getImageUrl());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setlikebtn(post_key);

                //user click at single post
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


                        Intent bsingle = new Intent(MainActivity.this,singlepost.class);
                        bsingle.putExtra("blog_id",post_key);
                        startActivity(bsingle);
                    }
                });
                viewHolder.mlike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mprocesslike = true;
                            mdatabaselike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(mprocesslike) {
                                        if (dataSnapshot.child(post_key).hasChild(mauth.getCurrentUser().getUid())) {

                                            mdatabaselike.child(post_key).child(mauth.getCurrentUser().getUid()).removeValue();
                                            mprocesslike = false;

                                        } else {
                                            mdatabaselike.child(post_key).child(mauth.getCurrentUser().getUid()).setValue("RandomValue");
                                            mprocesslike = false;
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    }
                });
            }
        };
        rec.setAdapter(firebaseRecyclerAdapter);
    }


    //checking if the user already exist using uid if not goto setup activity
    private void checkuserexist() {
        if(mauth.getCurrentUser()!=null) {
            final String uid = mauth.getCurrentUser().getUid();
            mdatabasechild.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(uid)) {
                        Intent login = new Intent(MainActivity.this, setupactivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public  static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageButton mlike;
        DatabaseReference msetlike;
        FirebaseAuth msetauth;


        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=  itemView;
            mlike = (ImageButton) mView.findViewById(R.id.mlike);
            msetlike = FirebaseDatabase.getInstance().getReference().child("Likes");
            msetauth = FirebaseAuth.getInstance();

            msetlike.keepSynced(true);

        }

        public  void setlikebtn(final String post_key){
            msetlike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(msetauth.getCurrentUser()!=null){
                    if (dataSnapshot.child(post_key).hasChild(msetauth.getCurrentUser().getUid())){
                        mlike.setImageResource(R.mipmap.red);
                    }else{
                        mlike.setImageResource(R.mipmap.ic_thumb_up_black_24dp);

                    }
                    //mlike.setImageResource(R.mipmap.ic_thumb_up_black_24dp);
                }}

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        public void setTitle(String title){
            TextView  post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setDesc(String desc) {
            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
        public void setImageUrl(Context c, String imageUrl) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(c).load(imageUrl).into(post_image);
        }
        public void setUsername(String username) {
            TextView post_username = (TextView) mView.findViewById(R.id.username);
            post_username.setText(username);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_add){
            startActivity(new Intent(MainActivity.this,PostActivity.class));
        }
        if(item.getItemId()==R.id.action_logout){
            mauth.signOut();
            this.finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.action_about){
            Intent intent = new Intent(MainActivity.this,About.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}












































































































































































































