package com.parse.starter;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.io.File;
import android.os.Environment;
import android.net.Uri;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.widget.Toast;
import android.app.ActionBar;

import java.text.SimpleDateFormat;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.media.ExifInterface;
import android.widget.EditText;
import android.widget.LinearLayout;
import java.util.List;
import java.util.ArrayList;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.parse.ParseUser;

public class TakePhotoActivity extends AppCompatActivity {
    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;
    private static final int REQ_CODE_TAKE_PICTURE = 30210;
    private String pictureImagePath = "";
    private Bitmap bitmap;
    private ParseFile photoFile;
    private List<byte[]> photos = new ArrayList<>();
    private byte[] bitmapdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        if(ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(this, HomePageActivity.class);
            startActivity(intent);
        }
    }


    public void takePhoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Check that request code matches ours:
        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE) {
            //Get our saved file into a bitmap object:
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            System.out.println("The absolute path is " + file.getAbsolutePath());
            bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
            if (bitmap == null) {
                System.out.println("Error major error BAH");
            }

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);


            ImageView myImage = (ImageView) findViewById(R.id.photo);
            myImage.setImageBitmap(bitmap);

            if (orientation.equals("6")) {
                myImage.setRotation(90);
            }
            if (orientation.equals("8")) {
                myImage.setRotation(270);
            }

            //myImage.setRotation(90);
            myImage.setAdjustViewBounds(true);

            //myImage.setScaleType(ImageView.ScaleType.FIT_XY);

            Button upload = (Button) findViewById(R.id.ClickToAdvance);



            LinearLayout advance = (LinearLayout) findViewById(R.id.advance);
            advance.setVisibility(LinearLayout.VISIBLE);
            Button clickToAdvance = (Button)findViewById(R.id.ClickToAdvance);
            clickToAdvance.setClickable(true);

            Button clickToRetrieve = (Button)findViewById(R.id.RetrieveImages);
            clickToRetrieve.setClickable(true);


        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
    public void uploadImage(View view) throws ParseException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        photoFile = new ParseFile("image.jpg", byteArray);
        photoFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(TakePhotoActivity.this,
                            "Error saving",
                            Toast.LENGTH_SHORT).show();
                } else {
                    ParseObject img = new ParseObject("NoteDB");
                    img.put("File", photoFile);
                    EditText tag = (EditText) findViewById(R.id.Tag);
                    img.put("Tag", tag.getText().toString());
                    String username = ParseUser.getCurrentUser().getUsername();
                    img.put("Username", username);
                    ParseACL pacl = img.getACL();
                    pacl.setPublicReadAccess(true);
                    img.setACL(pacl);
                    img.saveInBackground();
                    Toast.makeText(TakePhotoActivity.this,
                            "SUCCESS!",
                            Toast.LENGTH_SHORT).show();

                }
            }

        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout:
                ParseUser.logOut();
                Intent intent = new Intent(TakePhotoActivity.this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getPhotos() {
        ParseQuery query = new ParseQuery<ParseObject>("NoteDB");
        query.orderByDescending("createdAt");
        query.setLimit(8);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {
                    for (ParseObject parseObject : objects) {
                        Log.d("Update: ", "Object: " + parseObject.getObjectId().toString());
                        ParseFile pf = (ParseFile) parseObject.get("File");
                        try {
                            //bitmapdata = pf.getData();
                            photos.add(pf.getData());
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                    //shiftActivity();


                } else {
                    Toast.makeText(TakePhotoActivity.this, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//    public void getQuery(String column, String exact) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("NoteDB");
//        query.whereEqualTo(column, exact);
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            public void done(ParseObject object, ParseException e) {
//                if (object == null) {
//                    Log.d("score", "Query failed.");
//                } else {
//
//                    ParseFile pf = (ParseFile) object.get("File");
//                    byte[] bytedata;
//                    try {
//                        try {
//                            bytedata = pf.getData();
//                            Bitmap bitmapdata = BitmapFactory.decodeByteArray(bytedata, 0, bytedata.length);
//                            ImageView resultingImg = (ImageView) findViewById(R.id.imgtmp);
//                            resultingImg.setImageBitmap(bitmapdata);
//                        } catch (ParseException e1) {
//                            e1.printStackTrace();
//                        }
//                    } catch (ParseException e1) {
//                        e1.printStackTrace();
//                    }
//                    Log.d("score", "Retrieved Query.");
//                }
//            }
//        });
//    }

    public void retrieveImages(View view) {
        Toast.makeText(this, "GOT TO RETRIEVE IMAGES", Toast.LENGTH_SHORT).show();
        // getPhotos();


        Intent intent = new Intent(TakePhotoActivity.this, RetrieveImages.class);
        startActivity(intent);

    }
    /*
    public void shiftActivity() {

        Intent intent = new Intent(TakePhotoActivity.this, RetrieveImages.class);
        Log.d("Update: " , "Checkpoint1");
        String send = "data";
        Log.d("Update: " , "Checkpoint2");
        for (int i = 0; i < 8; i++) {
            Log.d("Update: ", "Checkpoint3");
            //intent.putExtra("bob", "bob");
            intent.putExtra(send + i, photos.get(i));
            Log.d("Update: ", "Checkpoint4");
        }
        startActivity(intent);
    }
    */
}