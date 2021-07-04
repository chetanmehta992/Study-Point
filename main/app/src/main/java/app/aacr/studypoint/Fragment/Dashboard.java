package app.aacr.studypoint.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Objects;

import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class Dashboard extends Fragment {
    private TabLayout tabLayout;
    private View view;
    private TextView textView;
    private ImageView imageView;
    private TextView name;
    private CircleImageView profile;
    public static Dashboard dashboard;
    private String chat_ref,user_phone;
    private Runnable runnable;

    public Dashboard(String chat_ref, String user_phone) {
        this.chat_ref = chat_ref;
        this.user_phone = user_phone;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { getActivity().finish();}
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_dashboard, container, false);

        dashboard = this;
        tabLayout = root.findViewById(R.id.tabLayout);
        view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.tab_background, null);
        textView = view.findViewById(R.id.tv1);
        imageView = view.findViewById(R.id.iv1);
        profile = root.findViewById(R.id.profileImage);
        name = root.findViewById(R.id.user_name);


        if(user_phone == null && chat_ref==null)
        {
            setCustomView(0, 1, 2, 3);
            setTextAndImageWithAnimation("HOME", R.drawable.ic_home);
            handleFragment(new HomeFragment());
        }
        else
        {
            AppBarLayout appbar = root.findViewById(R.id.app_bar);
            setCustomView(3, 1, 2, 0);
            setTextAndImageWithAnimation("CHAT", R.drawable.chat1);
            Dashboard.dashboard.handleFragment(new chat_message(chat_ref,user_phone));
            ViewGroup.LayoutParams lp = appbar.getLayoutParams();
            lp.height = 0;
            appbar.setLayoutParams(lp);
            user_phone = null;chat_ref = null;
        }

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("User_mobile",MODE_PRIVATE);
        String mobile = pref.getString("mobile","");
        name.setText(toTitleCase(getActivity().getApplicationContext().getSharedPreferences("User_name", Context.MODE_PRIVATE).getString("name","")));

        if(!getActivity().getApplicationContext().getSharedPreferences("User_profile", Context.MODE_PRIVATE).getString("profile","").isEmpty())
            Glide.with(this).load(getActivity().getApplicationContext().getSharedPreferences("User_profile", Context.MODE_PRIVATE).getString("profile","")).into(profile);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                AppBarLayout appbar = root.findViewById(R.id.app_bar);
                switch (tab.getPosition()) {
                    case 1:
                        setCustomView(1, 0, 2, 3);
                        setTextAndImageWithAnimation("BRANCHES", R.drawable.tree);
                        appbar.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT));
                        //change to the fragment which you want to display
                        handleFragment(new Branch());
                        break;

                    case 2:
                        setCustomView(2, 1, 0, 3);
                        setTextAndImageWithAnimation("FORUMS", R.drawable.discussion);
                        appbar.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT));
                        //change to the fragment which you want to display
                        handleFragment(new forum());
                        break;

                    case 3:
                        setCustomView(3, 1, 2, 0);
                        setTextAndImageWithAnimation("CHAT", R.drawable.chat1);
                        //change to the fragment which you want to display
                        handleFragment(new chatfragment());
                        ViewGroup.LayoutParams lp = appbar.getLayoutParams();
                        lp.height = 0;
                        appbar.setLayoutParams(lp);
                        break;
                    case 0:
                        //3 methods will be used in each case.
                        //method 1 : custom view(selected tab, non selected tabs) -> done
                        //method 2 : set text and image in tab with animation -> done
                        //method 3 : set fragment

                    default:
                        setCustomView(0, 1, 2, 3);
                        setTextAndImageWithAnimation("HOME", R.drawable.ic_home);
                        appbar.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT));
                        //change to the fragment which you want to display
                        handleFragment(new HomeFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        root.findViewById(R.id.upload_document).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.info.get("verified").toString().equals("true"))
                {
                    setCustomView(0, 1, 2, 3);
                    setTextAndImageWithAnimation("HOME", R.drawable.ic_home);
                    //change to the fragment which you want to display
                    handleFragment(new Upload_document());
                }
                else
                {
                    Snackbar.make(root, "User Id Not Verified...", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        new Handler().postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
             if(MainActivity.info != null)
             {
                 if(MainActivity.info.get("verified").toString().equals("true")) {
                     root.findViewById(R.id.verified).setVisibility(View.VISIBLE);
                 }
             }
             else
             {
                 new Handler().postDelayed(runnable,100);
             }
            }
        },100);
        return root;
    }
    private static String toTitleCase(String str) {

        if(str == null || str.isEmpty())
            return "";

        if(str.length() == 1)
            return str.toUpperCase();

        String[] parts = str.split(" ");

        StringBuilder sb = new StringBuilder( str.length() );

        for(String part : parts){

            if(part.length() > 1 )
                sb.append( part.substring(0, 1).toUpperCase() )
                        .append( part.substring(1).toLowerCase() );
            else
                sb.append(part.toUpperCase());

            sb.append(" ");
        }
        return sb.toString().trim();
    }
    private void setCustomView(int selectedtab, int non1, int non2, int non3) {
        Objects.requireNonNull(tabLayout.getTabAt(selectedtab)).setCustomView(view);
        Objects.requireNonNull(tabLayout.getTabAt(non1)).setCustomView(null);
        Objects.requireNonNull(tabLayout.getTabAt(non2)).setCustomView(null);
        Objects.requireNonNull(tabLayout.getTabAt(non3)).setCustomView(null);
    }

    private void setTextAndImageWithAnimation(String text, int images) {
        Animation animation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.slide_in_left);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        textView.setText(text);
        imageView.setImageResource(images);
        textView.startAnimation(animation);
        imageView.startAnimation(animation);
    }

    public void handleFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }
}