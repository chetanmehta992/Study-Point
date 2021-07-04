package app.aacr.studypoint.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import app.aacr.studypoint.R;


public class Aboutus extends Fragment {

    private RelativeLayout one,two,three,four;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aboutus, container, false);
        one=root.findViewById(R.id.one);
        two=root.findViewById(R.id.two);
        three=root.findViewById(R.id.three);
        four=root.findViewById(R.id.four);
        one.setTranslationX(600);
        one.setAlpha(0);
        two.setTranslationX(-600);
        two.setAlpha(0);
        three.setTranslationX(600);
        three.setAlpha(0);
        four.setTranslationX(-600);
        four.setAlpha(0);
        one.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                two.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        three.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                four.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(0).start();
                            }
                        }).start();
                    }
                }).start();
            }
        }).start();
        return root;
    }
}