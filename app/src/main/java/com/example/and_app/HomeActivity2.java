package com.example.and_app;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class HomeActivity2 extends AppCompatActivity {

    private DrawerLayout drawer;

    private AppBarConfiguration mAppBarConfiguration;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button buttonUpload, buttonChoseImage;
    private EditText editText;
    private ImageView imageView;
    private ProgressBar progressBar;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask uploadTask;

    private Uri imageUri;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(HomeActivity2.this, ImagesActivity.class);
                startActivity(gallery);
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        buttonChoseImage = findViewById(R.id.button_choose_image);
        buttonUpload = findViewById(R.id.button_upload);
        editText = findViewById(R.id.edit_text_file_name);
        imageView = findViewById(R.id.image_view);
        progressBar = findViewById(R.id.progress_bar);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");


        buttonChoseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });


    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {

            StorageReference fileReference = storageReference.child(System.currentTimeMillis()

                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override

                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {

                                @Override

                                public void run() {

                                    progressBar.setProgress(0);

                                }

                            }, 500);

                            Toast.makeText(HomeActivity2.this, "Upload successful", Toast.LENGTH_LONG).show();

                           /* Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),

                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                            String uploadId = mDatabaseRef.push().getKey();

                            mDatabaseRef.child(uploadId).setValue(upload);*/

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!urlTask.isSuccessful());

                            Uri downloadUrl = urlTask.getResult();

                            Upload upload = new Upload(editText.getText().toString().trim(),downloadUrl.toString());

                            String uploadId = databaseReference.push().getKey();

                            databaseReference.child(uploadId).setValue(upload);

                        }

                    })

                    .addOnFailureListener(new OnFailureListener() {

                        @Override

                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(HomeActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override

                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                            progressBar.setProgress((int) progress);

                        }

                    });

        } else {

            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();

        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_home:

                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_gallery:

                drawer.closeDrawer(GravityCompat.START);
                Intent gallery = new Intent(HomeActivity2.this, ImagesActivity.class);
                startActivity(gallery);
                return true;
            case R.id.nav_share:

                drawer.closeDrawer(GravityCompat.START);
                return true;
            default:
                return false;
        }
    }


}
