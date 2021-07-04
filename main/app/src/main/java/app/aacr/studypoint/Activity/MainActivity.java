package app.aacr.studypoint.Activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Stream;

import app.aacr.studypoint.Fragment.Aboutus;
import app.aacr.studypoint.Fragment.Dashboard;
import app.aacr.studypoint.Adapter.DrawerAdapter;
import app.aacr.studypoint.DrawerItem;
import app.aacr.studypoint.Fragment.Profile;
import app.aacr.studypoint.Fragment.Search_Fragment;
import app.aacr.studypoint.Fragment.chat_message;
import app.aacr.studypoint.Fragment.chatfragment;
import app.aacr.studypoint.R;
import app.aacr.studypoint.Adapter.SimpleItem;
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int pos_CLOSE=0;
    private static final int pos_DASHBOARD=1;
    private static final int pos_MY_PROFILE=2;
    private static final int pos_ABOUT_US=3;
    private static final int pos_LOGOUT=4;
    public static  MainActivity mainActivity;
    private FirebaseDatabase ref;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav slidingRootNav ;
    static public String mobile,chat_ref,user_phone;
    public static NotificationManagerCompat notificationManager;
    public static HashMap info;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mobile = getApplicationContext().getSharedPreferences("User_mobile", Context.MODE_PRIVATE).getString("mobile", "");
        ref = FirebaseDatabase.getInstance();
        mainActivity = this;
        chat_ref = getIntent().getStringExtra("chat_ref");
        user_phone = getIntent().getStringExtra("user_phone");
        if (chat_ref == null && user_phone == null)
        {
            notificationreciver();
        }
        onlinestatus();

        ref.getReference().child("Users").child(mobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                info = (HashMap) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(pos_CLOSE),
                createItemFor(pos_DASHBOARD).setChecked(true),
                createItemFor(pos_MY_PROFILE),
                createItemFor(pos_ABOUT_US),
                createItemFor(pos_LOGOUT)
        ));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(pos_DASHBOARD);

    }

    private void onlinestatus() {
        ref.getReference(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if(connected)
                {
                    DatabaseReference con = ref.getReference().child("Users").child(mobile).child("online");
                    con.setValue("true");
                    con.onDisconnect().setValue("false");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void notificationreciver() {
        notificationManager = NotificationManagerCompat.from(this);
        DatabaseReference ref1 = ref.getReference().child("Users");
        ref1.child(mobile).child("Contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot){
                for(DataSnapshot child : snapshot.getChildren()) {
                    HashMap info = (HashMap) child.getValue();
                    if(info.get("unread")!=null && Integer.parseInt(info.get("unread").toString())>0 && !child.getKey().equals(chat_message.mobile) ) {
                        createNotificationChannels(child.getKey());
                        ref1.child(child.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                HashMap info1 = (HashMap) snapshot.getValue();
                                String name = ""+info1.get("name");
                                info1.get("profile");
                                ref.getReference().child("Chats").child(info.get("chat_ref").toString()).limitToLast(Integer.parseInt(info.get("unread").toString())).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        String message = "";
                                        int i = (int) snapshot.getChildrenCount();
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            message += ((HashMap) snapshot1.getValue()).get("message");
                                            i--;
                                            if(i!=0)
                                            {
                                                message += "\n";
                                            }
                                        }
                                        Intent activityIntent = new Intent(MainActivity.this, MainActivity.class);
                                        activityIntent.putExtra("chat_ref", info.get("chat_ref").toString());
                                        activityIntent.putExtra("user_phone", child.getKey());
                                        PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, (int)Long.parseLong(child.getKey()), activityIntent, 0);
                                        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Me");
                                        messagingStyle.setConversationTitle("Chat");
                                        NotificationCompat.MessagingStyle.Message notificationMessage = new NotificationCompat.MessagingStyle.Message(message, 1, titleCaseConversion(name));
                                        messagingStyle.addMessage(notificationMessage);
                                        Notification notification = new NotificationCompat.Builder(MainActivity.this, child.getKey())
                                                .setSmallIcon(R.drawable.app_logo)
                                                .setStyle(messagingStyle)
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                .setColor(Color.BLUE)
                                                .setAutoCancel(true)
                                                .setOnlyAlertOnce(true)
                                                .setContentIntent(contentIntent)
                                                .build();
                                        notificationManager.notify((int)Long.parseLong(child.getKey()), notification);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
                    }
                    else
                        {
                            notificationManager.cancel((int)Long.parseLong(child.getKey()));
                        }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        }
        );
    }

    private DrawerItem createItemFor(int position){
        return new SimpleItem(screenIcons[position],screenTitles[position])

                .withIconTint(color(R.color.pink))
                .withTextTint(color(R.color.black))
                .withSelectedIconTint(color(R.color.pink))
                .withSelectedTextTint(color(R.color.pink));
    }
    @ColorInt
    private int color(@ColorRes int res){
        return ContextCompat.getColor(this,res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityscreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta=getResources().obtainTypedArray(R.array.id_activityscreenIcon);
        Drawable[] icons =new Drawable[ta.length()];
        for(int i = 0; i <ta.length(); i++){
            int id = ta.getResourceId(i,0);
            if(id!=0){
                icons[i]= ContextCompat.getDrawable(this,id);
            }
        }
        ta.recycle();
        return icons;
    }


    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();

        if (position == pos_DASHBOARD){
            Dashboard dashBoardFragment =new Dashboard(chat_ref,user_phone);
            transaction.replace(R.id.container,dashBoardFragment);
            chat_ref = null;
            user_phone = null;
        }
        else if (position == pos_MY_PROFILE){
            transaction.replace(R.id.container,new Profile());
        } else if (position == pos_ABOUT_US){
            Aboutus aboutus_fragment =new Aboutus() ;
            transaction.replace(R.id.container,aboutus_fragment);
        } else if (position == pos_LOGOUT){

            MaterialDialog mDialog = new MaterialDialog.Builder(this)
                    .setTitle("Logout?")
                    .setMessage("Are you sure want to Logout?")
                    .setCancelable(false)
                    .setPositiveButton("Logout", R.drawable.ic_outline_login_24, new BottomSheetMaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            getApplicationContext().getSharedPreferences("User_profile",Context.MODE_PRIVATE).edit().putString("profile","").apply();
                            getApplicationContext().getSharedPreferences("User_name", Context.MODE_PRIVATE).edit().putString("name","").apply();
                            getApplicationContext().getSharedPreferences("User_mobile", Context.MODE_PRIVATE).edit().putString("mobile","").apply();
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            finish();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", R.drawable.close, new BottomSheetMaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.dismiss();
                        }
                    })
                    .build();
            mDialog.show();
        }
        slidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void createNotificationChannels(String key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    key,
                    "StudyPoint",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    private static String titleCaseConversion(String inputString)
    {
        if (inputString.isEmpty()) {
            return "";
        }

        if (inputString.length() == 1) {
            return inputString.toUpperCase();
        }

        StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());

        for (String s : inputString.split(" ")) {
            if (s.length() > 1)
                resultPlaceHolder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase());
            else
                resultPlaceHolder.append(s.toUpperCase());
            resultPlaceHolder.append(" ");
        }
        return resultPlaceHolder.toString().trim();
    }
}
