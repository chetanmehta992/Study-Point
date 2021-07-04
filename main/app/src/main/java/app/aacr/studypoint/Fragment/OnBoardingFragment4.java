package app.aacr.studypoint.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.aacr.studypoint.Activity.LoginActivity;
import app.aacr.studypoint.R;

public class OnBoardingFragment4 extends Fragment {

    FloatingActionButton fab;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding4, container, false);
        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getApplicationContext().getSharedPreferences("startactivity", Context.MODE_PRIVATE).edit().putBoolean("isIntroOpnend",true).apply();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        return root;
    }
}