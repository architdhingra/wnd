package com.wnd.myapp.lenovate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class profile extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 7;
    private static final int SELECT_FILE = 6;
    EditText getfrname, getsecname, dob,phone, addr, pin;
    TextView getemail;
    CheckBox male, female;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    DatePicker datePicker;
    int imgchangeflag=0;
    SharedPreferences sharedPref;
    static  final int CAM_REQUEST = 3;
    ImageView imgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        imgView = (ImageView) findViewById(R.id.backdrop);
        
        /*Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drawer, getApplicationContext().getTheme());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getSupportActionBar().setHomeAsUpIndicator(drawable);
        }*/
        
        getfrname = (EditText)findViewById(R.id.frstname);
        getemail = (TextView)findViewById(R.id.emailid);
        addr = (EditText)findViewById(R.id.addr);
        pin = (EditText)findViewById(R.id.pin);
        phone = (EditText)findViewById(R.id.num);
        male = (CheckBox)findViewById(R.id.malecheck);
        female = (CheckBox)findViewById(R.id.femalecheck);
        //datePicker = (DatePicker)findViewById(R.id.datePicker);

        sharedPref = profile.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        getfrname.setText(sharedPref.getString("userName", null));
        getemail.setText(sharedPref.getString("userEmail", null));
        addr.setText(sharedPref.getString("userAddr",null));
        pin.setText(sharedPref.getString("userPin",null));
        phone.setText(sharedPref.getString("userNumber",null));

        if(sharedPref.getString("userSex",null)!=null){
            if(sharedPref.getString("userSex",null).equals("m")){
                male.setChecked(true);
            }else if(sharedPref.getString("userSex",null).equals("f")){
                female.setChecked(true);
            }
        }
        

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(sharedPref.getString("userName", null));

        loadBackdrop();


        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (male.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userSex", "m");
                    editor.commit();
                } else if (!male.isChecked() && !female.isChecked()) {
                    female.setChecked(true);
                }
                if (female.isChecked() && male.isChecked()) {
                    female.setChecked(false);
                }
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (female.isChecked()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userSex", "f");
                    editor.commit();
                } else if (!male.isChecked() && !female.isChecked()) {
                    male.setChecked(true);
                }
                if (male.isChecked() && female.isChecked()) {
                    male.setChecked(false);
                }
            }
        });

        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            Log.d("profile","back button pressed");
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {

        if(imgchangeflag==1){
            startActivity(new Intent(profile.this, Dashboard.class));
        }else{
            super.onBackPressed();
        }
    }

    public void add(View view){

        String frstname = getfrname.getText().toString();
        String email = getemail.getText().toString();
        String addrx = addr.getText().toString();
        String pinx = pin.getText().toString();
        String phoneno = phone.getText().toString();

        if (pinx.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter Pin Code First!", Toast.LENGTH_SHORT).show();
        }
        else if (phoneno.length()!=10){
            Toast.makeText(getApplicationContext(), "Enter 10 digit Phone Number!", Toast.LENGTH_SHORT).show();
        }
        else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userName", frstname);
            editor.putString("userEmail", email);
            editor.putString("userAddr", addrx);
            editor.putString("userPin", pinx);
            editor.putString("userNumber", phoneno);

            editor.commit();

            Toast.makeText(getApplicationContext(), "Profile Saved Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBackdrop() {
        Bitmap bitmap=null;
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/Lenovate");
        File file = new File(directory, "profile.jpg"); //or any other format supported
        Log.d("file path: ", file.toString());

        if(file.exists() && file.canRead()) {
            Log.d("Profile","Profile image exists");

            try {
                bitmap = BitmapFactory.decodeFile(file.toString());
                imgView.setImageBitmap(bitmap);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
        }
    }

    public void selectImage(View view) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    loadcamera();
                } else if (items[item].equals("Choose from Library")) {
                    loadImage();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void loadcamera(){

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Lenovate/profile.jpg");
        Log.d("file path",myDir.toString());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(myDir));

        startActivityForResult(intent,CAM_REQUEST);
    }

    public void loadImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                imgchangeflag =1;

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                SaveImage(BitmapFactory.decodeFile(imgDecodableString));

                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            }
            else if (requestCode == CAM_REQUEST && resultCode == RESULT_OK
                    && null != data) {
                imgchangeflag =1;
                String root = Environment.getExternalStorageDirectory().toString();
                File file = new File(root + "/Lenovate/profile.jpg");
                Bitmap bm = BitmapFactory.decodeFile(file.toString());
                Bitmap resizedbm = getResizedBitmap(bm, 1024, 1024);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    Log.d("Compressing", "image from camera");
                    resizedbm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                loadBackdrop();

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Lenovate");
        myDir.mkdirs();

        String fname = "profile.jpg";
        String nomedia = ".nomedia";
        File file = new File (myDir, fname);
        File file2 = new File (myDir, nomedia);

        if (file.exists ()) file.delete ();
        try {
            if(!file2.exists()){
                FileOutputStream out2 = new FileOutputStream(file2);
                out2.flush();
                out2.close();
            }

            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


}
