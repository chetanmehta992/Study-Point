package app.aacr.studypoint.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import app.aacr.studypoint.R;

public class OnBoardingFragment1 extends Fragment {
    @Override
    public View onCreateView( LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_on_boarding1, container, false);
        return root;

    }
}
