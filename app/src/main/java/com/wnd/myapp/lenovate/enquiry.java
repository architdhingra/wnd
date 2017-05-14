package com.wnd.myapp.lenovate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class enquiry extends AppCompatActivity {

    int success;
    TextView prod_name;
    LinearLayout chooseimg;
    static  final int CAM_REQUEST = 3;
    private static int RESULT_LOAD_IMG = 1;
    ImageView prod_image;
    EditText name, phone, email, note;
    String imgDecodableString;
    SharedPreferences sharedPref;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_enquiry);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        progressDialog = new ProgressDialog(enquiry.this);
        progressDialog.setMessage("Loading");
        sharedPref = enquiry.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        chooseimg = (LinearLayout) findViewById(R.id.chooseimg);
        prod_name = (TextView) findViewById(R.id.enq_prodname);
        prod_image = (ImageView) findViewById(R.id.enq_prodimg);

        name = (EditText) findViewById(R.id.enq_name);
        phone = (EditText) findViewById(R.id.enq_phone);
        email = (EditText) findViewById(R.id.enq_email);
        note = (EditText) findViewById(R.id.enq_note);

        name.setText(sharedPref.getString("userName", null));
        email.setText(sharedPref.getString("userEmail", null));

        if(sharedPref.getString("userNumber",null) != null){
            if (sharedPref.getString("userNumber", null).length() > 9) {
                phone.setText(sharedPref.getString("userNumber", null));
            } else {
                phone.setText(null);
            }
        }

        if (!getIntent().getStringExtra("from").equals("enquire_all")) {
            prod_image.setVisibility(View.VISIBLE);
            prod_name.setText(getIntent().getStringExtra("item_name"));                 // getting image url from intent
            Picasso.with(getApplication())
                    .load(getIntent().getStringExtra("url"))
                    .placeholder(R.drawable.placeholder)
                    .into(prod_image);
        } else {
            chooseimg.setVisibility(View.VISIBLE);
            prod_name.setText("Assistance");                 // getting image url from intent

        }

        Log.d("url", "image url: " + getIntent().getStringExtra("url"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submit(View v) {

        String namex, emailx, phonex, notex;
        namex = name.getText().toString();
        emailx = email.getText().toString();
        phonex = phone.getText().toString();
        notex = note.getText().toString();

        if(namex=="" || emailx=="" || phonex ==""){
            Toast.makeText(enquiry.this, "Please enter the missing fields!!",Toast.LENGTH_SHORT).show();
        }else {
            if (getIntent().getStringExtra("from").equals("enquire_all")) {
                if (prod_image.getDrawable() == null) {
                    Toast.makeText(enquiry.this, "Please Provide an image", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap bitmap = ((BitmapDrawable) prod_image.getDrawable()).getBitmap();
                    uploadImagewithenquiry(bitmap, namex, emailx, phonex, notex);
                }

            } else {
                SubmitQuery(namex, emailx, phonex, notex);
            }
        }

    }

    JSONObject jsonObject;

    public void SubmitQuery(final String username, final String usermail, final String userphone, final String query) {
        progressDialog.show();
        final RequestQueue rq = VolleySingelton.getInstance(getApplicationContext()).getRequestQ();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/enquiry2.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    jsonObject = new JSONObject(response);
                    success = jsonObject.getInt("success");
                    Log.d("response", response.toString());
                    if (success == 1) {
                        progressDialog.hide();
                        showDialogTofinish();
                        Log.d("Reponse", "Query submitted Successfully");

                    } else {
                        progressDialog.hide();
                        Toast.makeText(getApplication(), "Error submitting", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    progressDialog.hide();
                    e.printStackTrace();
                    Toast.makeText(getApplication(), "Something went wrong, Please Try Again!", Toast.LENGTH_SHORT).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getApplication(), "Oops! An Error Occured", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Post params to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", username);
                params.put("prodname", prod_name.getText().toString());
                params.put("email", usermail);
                params.put("phone", userphone);
                params.put("note", query);
                params.put("url", getIntent().getStringExtra("url"));
                return params;
            }

        };
        sr.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

    public void showDialogTofinish() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Query has been submitted successfully, We will get back to your shortly..")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /*-------------------------------- enquire all -----------------------------------------------------------------*/

    public void selectImage(View view) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(enquiry.this);
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
        File myDir = new File(root + "/Lenovate/enquiry.jpg");
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

    private void uploadImagewithenquiry(final Bitmap bitmap,final String username, final String usermail, final String userphone, final String query) {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://www.woodndecor.in/enquiry_upload2.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Log.d("response","response: " + s.toString());
                        showDialogTofinish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        Log.d("response","Error: " + volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name = "";

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("image", image);
                params.put("name", username);
                params.put("email", usermail);
                params.put("phone", userphone);
                params.put("note", query);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

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

                chooseimg.setVisibility(View.GONE);
                prod_image.setVisibility(View.VISIBLE);
                // Set the Image in ImageView after decoding the String
                prod_image.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            }
            else if (requestCode == CAM_REQUEST && resultCode == RESULT_OK) {
                String root = Environment.getExternalStorageDirectory().toString();
                File file = new File(root + "/Lenovate/enquiry.jpg");
                Bitmap bm = BitmapFactory.decodeFile(file.toString());
                Bitmap resizedbm = getResizedBitmap(bm, 1024, 768);
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
                if(data==null) {
                    Log.d("enquiry", "error");
                }
                Log.d("enquiry","result code: "+ requestCode+ " :: Resultccode: "+ resultCode +" :: data: ");
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
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

    private void loadBackdrop() {
        Bitmap bitmap=null;
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/Lenovate");
        File file = new File(directory, "enquiry.jpg"); //or any other format supported
        Log.d("file path: ", file.toString());

        try {

            chooseimg.setVisibility(View.GONE);
            prod_image.setVisibility(View.VISIBLE);
            bitmap = BitmapFactory.decodeFile(file.toString());
            prod_image.setImageBitmap(bitmap);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Lenovate");
        myDir.mkdirs();

        String fname = "enquiry.jpg";
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
}
