package com.parse.starter;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import android.os.Environment;
import android.net.Uri;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;android.util.Log;
import
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class RetrieveImfiles_outputs AppCompatActivity {
    private byte[] bitmapdata;
    private List<Bitmap> photos = new Ar>();


  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_images);

        Toast.makeText(RetrieveImages.this, "HEELLLOOOO GOT HEREWAHOOO", Toast.LENGTH_SHORT).show();
        getPhotos();

//        Intent intent = getIntent();
//        byte[] bitmapdata = intent.getByteArrayExtra("bytes");
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.outWidth = 750;
//        options.outHeight = 1000;
//        List<Bitmap> photos = new ArrayList<>();
//        photos.add(BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length, options));
///

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
                            bitmapdata = pf.getData();
                            photos.add(BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    ImageView img1 = (ImageView) findViewById(R.id.image1);
                    ImageView img2 = (ImageView) findViewById(R.id.image2);
                    ImageView img3 = (ImageView) findViewById(R.id.image3);
                    ImageView img4 = (ImageView) findViewById(R.id.image4);
                    ImageView img5 = (ImageView) findViewById(R.id.image5);
                    ImageView img6 = (ImageView) findViewById(R.id.image6);
                    ImageView img7 = (ImageView) findViewById(R.id.image7);
                    ImageView img8 = (ImageView) findViewById(R.id.image8);


                    img1.setImageBitmap(photos.get(0));
                    img2.setImageBitmap(photos.get(1));
                    img3.setImageBitmap(photos.get(2));
                    img4.setImageBitmap(photos.get(3));
                    img5.setImageBitmap(photos.get(4));
                    img6.setImageBitmap(photos.get(5));
                    img7.setImageBitmap(photos.get(6));
                    img8.setImageBitmap(photos.get(7));

                    img1.setRotation(90);
                    img2.setRotation(90);
                    img3.setRotation(90);
                    img4.setRotation(90);
                    img5.setRotation(90);
                    img6.setRotation(90);
                    img7.setRotation(90);
                    img8.setRotation(90);






                } else {
                    Toast.makeText(RetrieveImages.this, "Error: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
