package app.aacr.studypoint.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import app.aacr.studypoint.ImageResizer;
import app.aacr.studypoint.Adapter.LoginAdapter;
import app.aacr.studypoint.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class Completeyourprofile extends AppCompatActivity {
    private ViewPager viewPager;
    private LoginAdapter adapter;
    private CircleImageView profileImage;
    public static Uri fileUri = null;
    public static String mobile ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completeyourprofile);

        mobile = getApplicationContext().getSharedPreferences("User_mobile", Context.MODE_PRIVATE).getString("mobile","");
        viewPager = findViewById(R.id.view_pager);
        profileImage = findViewById(R.id.profile1);

        NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.tab_layout);
        navigationTabStrip.setTitles("College Student", "Faculty", "School Student");

        adapter = new LoginAdapter(getSupportFragmentManager(), this, 3);
        viewPager.setAdapter(adapter);
        navigationTabStrip.setViewPager(viewPager,1);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery,"Select Profile Picture"),1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            Uri image = data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),image);
                Bitmap reduce = ImageResizer.reduceBitmapSize(bitmap,15000);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), reduce, null, null);
                fileUri = Uri.parse(path);
                profileImage.setImageURI(data.getData());
            }
            catch (IOException e)
            {
                Snackbar.make(findViewById(R.id.parent), "Reselect the Image", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}