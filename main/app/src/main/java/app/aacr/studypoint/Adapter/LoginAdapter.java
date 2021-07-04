package app.aacr.studypoint.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import app.aacr.studypoint.Fragment.clg_registertation;
import app.aacr.studypoint.Fragment.faculty;
import app.aacr.studypoint.Fragment.school_registration;

public class LoginAdapter extends FragmentPagerAdapter {

    private final Context context;
    int totalTabs;


    public LoginAdapter(FragmentManager fm, Context context, int totalTabs){
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return  new clg_registertation();
            case 1:
            return  new faculty();
            case 2:
                return  new school_registration();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
