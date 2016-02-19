package bb.bb.artgen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class FilterActivity extends AppCompatActivity {

    private FloatingActionButton back;
    private ImageView imageView;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        myContext = this;
        initialize();

/**        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); **/
    }

    OnClickListener backButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // close filter activity
            finish();
        }
    };

    public void initialize() {
        imageView = (ImageView) findViewById(R.id.image_container);

        // receive data from intent
        Intent i = getIntent();
        String picturePath = i.getStringExtra("picturePath");

        // read file and place in imageView
        File imgFile = new File(picturePath);

        if (imgFile.exists()) {
            imageView.setImageURI(Uri.parse(imgFile.toString()));
        } else {
            Toast toast = Toast.makeText(myContext, "Error loading image!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }

        // set back listener
        back = (FloatingActionButton) findViewById(R.id.button_filter_back);
        back.setOnClickListener(backButtonListener);
    }


}
