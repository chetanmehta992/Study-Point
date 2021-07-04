package app.aacr.studypoint.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.cuberto.liquid_swipe.LiquidPager;
import app.aacr.studypoint.Fragment.OnBoardingFragment1;
import app.aacr.studypoint.Fragment.OnBoardingFragment2;
import app.aacr.studypoint.Fragment.OnBoardingFragment3;
import app.aacr.studypoint.Fragment.OnBoardingFragment4;
import app.aacr.studypoint.R;

public class IntroductoryActivity extends AppCompatActivity {

    ImageView logo,appName,bottom;
    LottieAnimationView lottieAnimationView;
    private LiquidPager viewPager;
    private ScreenSliderPagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        appName = findViewById(R.id.app_name);
        logo = findViewById(R.id.logo);
        bottom = findViewById(R.id.bottom);
        lottieAnimationView = findViewById(R.id.lottie);
        viewPager = findViewById(R.id.pager);

        appName.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        bottom.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(2000).setDuration(1000).setStartDelay(4000)
                .withEndAction(new Runnable() {
            @Override
            public void run() {
                if(getApplicationContext().getSharedPreferences("startactivity",MODE_PRIVATE).getBoolean("isIntroOpnend",false)) {
                    if(getApplicationContext().getSharedPreferences("User_mobile", Context.MODE_PRIVATE).getString("mobile","").equals("")){
                        Intent intent = new Intent(IntroductoryActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    else if(getApplicationContext().getSharedPreferences("User_name", Context.MODE_PRIVATE).getString("name","").equals("")) {
                        startActivity(new Intent(IntroductoryActivity.this,Completeyourprofile.class));
                    }
                    else
                    {
                        startActivity(new Intent(IntroductoryActivity.this,MainActivity.class));
                    }
                    finish();
                }else {
                    ActivityCompat.requestPermissions(IntroductoryActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 101);
                }
            }
        });

    }
    private static class ScreenSliderPagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSliderPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new OnBoardingFragment1();
                case 1:
                    return new OnBoardingFragment2();
                case 2:
                    return new OnBoardingFragment3();
                case 3:
                    return new OnBoardingFragment4();
            }
            return null;
        }
        @Override
        public int getCount() {
            return 4;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

      if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pagerAdapter = new ScreenSliderPagerAdapter(getSupportFragmentManager(), 1);
                viewPager.setAdapter(pagerAdapter);
            }
            else {
                finish();
            }
        }
    }
}