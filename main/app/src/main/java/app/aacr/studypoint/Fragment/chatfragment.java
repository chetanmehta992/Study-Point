package app.aacr.studypoint.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.R;

import static android.content.Context.MODE_PRIVATE;

public class chatfragment extends Fragment {
    ListView display_people;
    ArrayList<DataSnapshot> users;
//    static ArrayList name,img,status,online,mobile,chat_ref;
    private DatabaseReference ref;
    static ArrayList<HashMap> all_users;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new GoogleEmojiProvider());
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() { getActivity().finish();}
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View root = inflater.inflate(R.layout.fragment_chatfragment, container, false);
        users = new ArrayList<>();
        all_users = new ArrayList<>();
        display_people = root.findViewById(R.id.people);

        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        if (MainActivity.mainActivity != null) {

            ref.child(MainActivity.mobile).child("Contacts").orderByChild("last_msg_time").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        users.add(data);
                    }
                    Collections.reverse(users);
                    ListAdapter customAdapter = new ListAdapter(MainActivity.mainActivity, android.R.layout.simple_list_item_1, all_users);
                    display_people.setAdapter(customAdapter);
                    for (DataSnapshot data : users) {
                        ref.child(data.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap info = (HashMap) snapshot.getValue();
                                info.putAll((Map) data.getValue());
                                info.put("mobile", data.getKey());
                                for (HashMap all : all_users) {
                                    if (all.containsValue((data.getKey()))) {
                                        all_users.remove(all);
                                        break;
                                    }
                                }
                                all_users.add(info);
                                sort_data(all_users);
                                customAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();

                }
            });
        }
        EditText search_bar = root.findViewById(R.id.search_bar);

        root.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.dashboard.handleFragment(new Search_Fragment());
            }
        });

        return root;
    }
    void sort_data(ArrayList modelList)
    {
        Collections.sort(modelList, new Comparator<HashMap>() {

            @Override
            public int compare(HashMap o1, HashMap o2) {
                 return o2.get("last_msg_time").toString().compareTo(o1.get("last_msg_time").toString());
            }
        });
    }
}
class ListAdapter extends ArrayAdapter {

    private final Context mContext;

    public ListAdapter(Context context, int resource, ArrayList<HashMap> all_users) {
        super(context, resource,all_users);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_user, parent, false);
        ImageView user_img;
        TextView user_name,user_status,unread;
        CardView user_online;

        unread = convertView.findViewById(R.id.unread);
        if(chatfragment.all_users.get(position).get("unread").toString().equals("0"))
            unread.setText("");
        else
            unread.setText(chatfragment.all_users.get(position).get("unread").toString());
        user_name = convertView.findViewById(R.id.name);
        user_name.setText(toTitleCase(chatfragment.all_users.get(position).get("name").toString()));
//        name.add(info.get("name"));
//                                img.add(info.get("profile"));
//                                status.add(info.get("status"));
//                                online.add(info.get("online"));
//                                mobile.add(data.getKey());
//                                chat_ref.add(((HashMap) ((HashMap) info.get("Contacts")).get(MainActivity.mobile)).get("chat_ref"));
//
        user_status = convertView.findViewById(R.id.status);
        user_status.setText(chatfragment.all_users.get(position).get("status").toString());

        user_img = convertView.findViewById(R.id.profile);
        if(chatfragment.all_users.get(position).containsKey("profile"))
            Glide.with(mContext).load(chatfragment.all_users.get(position).get("profile")).into(user_img);
        else
            user_img.setBackgroundResource(R.drawable.profile);
        user_online = convertView.findViewById(R.id.online);
        if(chatfragment.all_users.get(position).get("online").toString().equals("true"))
        {
            user_online.setBackgroundColor(Color.GREEN);
        }else
        {
            user_online.setBackgroundColor(Color.RED);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.dashboard.handleFragment(new chat_message(chatfragment.all_users.get(position).get("chat_ref").toString(),chatfragment.all_users.get(position).get("mobile").toString()));
            }
        });
        return convertView;
    }
    private static String toTitleCase(String str) {

        if(str == null || str.isEmpty())
            return "";

        if(str.length() == 1)
            return str.toUpperCase();

        //split the string by space
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

}