package app.aacr.studypoint.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.R;

public class chat_message extends Fragment {
    EmojiEditText emojiEditText;
    EmojiPopup emojiPopup;
    ImageView emojikey;
    View root;
    public static String mobile;
    String chat_ref_id;
    String time;
//    String reciver_name;
    String unread = null;

    static RecyclerView recyclerView;
    private DatabaseReference ref;

    public chat_message(String chat_ref_Id, String mobile) {
        this.chat_ref_id = chat_ref_Id;
        chat_message.mobile = mobile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new GoogleEmojiProvider());
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(emojiPopup.isShowing())
                {
                    emojiPopup.dismiss();
                    emojikey.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/keyboard",null, getActivity().getPackageName())));
                }
                else
                {
                    Dashboard.dashboard.handleFragment(new chatfragment());
                    chat_ref_id = null;
                    mobile = null;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_chat_message, container, false);
        ref = FirebaseDatabase.getInstance().getReference();
        recyclerView = root.findViewById(R.id.messages);
        ref.child("Users").child(MainActivity.mobile).child("Contacts").child(mobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                HashMap info = (HashMap) snapshot.getValue();
                if(info!=null && info.get("unread")!=null)
                {
                    if( Integer.parseInt(info.get("unread").toString())>0 && mobile!=null)
                    {
                        info.put("unread","0");
                        ref.child("Users").child(MainActivity.mobile).child("Contacts").child(mobile).updateChildren(info);
                        chat_message.recyclerView.smoothScrollToPosition(chat_message.recyclerView.getAdapter().getItemCount());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });ref.child("Users").child(mobile).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (getActivity() == null) {
                    return;
                }
                HashMap info = (HashMap) snapshot.getValue();
                if(info.containsKey("Contacts") && ((HashMap)info.get("Contacts")).containsKey(MainActivity.mobile) && ((HashMap)((HashMap)info.get("Contacts")).get(MainActivity.mobile)).containsKey("unread"))
                    unread = ((HashMap)((HashMap)info.get("Contacts")).get(MainActivity.mobile)).get("unread").toString();
                 ((TextView) root.findViewById(R.id.name)).setText(toTitleCase(info.get("name").toString()));
                Glide.with(getActivity()).load(info.get("profile")).into(((ImageView)root.findViewById(R.id.profile)));
                if(info.get("online").toString().equals("true"))
                {
                    root.findViewById(R.id.online).setBackgroundColor(Color.GREEN);
                }else
                {
                    root.findViewById(R.id.online).setBackgroundColor(Color.RED);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        if(chat_ref_id!=null) {
            loadchats();
        }
        root.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.dashboard.handleFragment(new chatfragment());
                chat_ref_id = null;
                mobile = null;
            }
        });
        emojiEditText = root.findViewById(R.id.new_message);
        emojiPopup = EmojiPopup.Builder.fromRootView(emojiEditText).build(emojiEditText);
        emojikey = root.findViewById(R.id.attachment_emoji);
        emojikey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiPopup.isShowing())
                {
                    emojiPopup.dismiss();
                    emojikey.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/ic_insert_emoticon_gray_40dp",null, getActivity().getPackageName())));
                }
                else
                {
                    emojiPopup.toggle();
                    emojikey.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/keyboard",null, getActivity().getPackageName())));
                }
            }
        });
        root.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!emojiEditText.getText().toString().trim().isEmpty())
                {
                    if(chat_ref_id==null)
                    {
                        chat_ref_id = ref.child("Chats").push().getKey();
                        loadchats();
                    }

                    HashMap message = new HashMap();
                    message.put("message",emojiEditText.getText().toString().trim());

                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
                    Date now = new Date();
                    time = sdfDate.format(now) +"-"+ MainActivity.mobile;

                    ref.child("Chats").child(chat_ref_id).child(time).setValue(message);

                    HashMap message_info = new HashMap();
                    message_info.put("chat_ref", chat_ref_id);
                    message_info.put("last_msg_time", time);
                    if(unread!=null)
                    {
                        message_info.put("unread",""+(Integer.parseInt(unread)+1));
                    }
                    else
                    {
                        message_info.put("unread","1");
                    }
                    ref.child("Users").child(mobile).child("Contacts").child(MainActivity.mobile).updateChildren(message_info);
                    ref.child("Users").child(MainActivity.mobile).child("Contacts").child(mobile).updateChildren(message_info);
                    chat_message.recyclerView.smoothScrollToPosition(chat_message.recyclerView.getAdapter().getItemCount());

                    emojiEditText.setText("");
                }
                else{
                    emojiEditText.setText("");
                }
            }
        });
        return root;

    }

    private void loadchats() {
        FirebaseRecyclerOptions<HashMap> options = new FirebaseRecyclerOptions.Builder<HashMap>()
                .setQuery(ref.child("Chats").child(chat_ref_id).orderByKey(), snapshot -> {
            HashMap info = (HashMap) snapshot.getValue();
            info.put("time",snapshot.getKey());
            return info;
        }).build();
        recycleAdapter1 adapter = new recycleAdapter1(options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
class recycleAdapter1 extends FirebaseRecyclerAdapter<HashMap,RecyclerView.ViewHolder>
{
    View senderchat,reciverchat;
    myviewholder1 myviewholder1_obj;
    myviewholder2 myviewholder2_obj;
    public recycleAdapter1(@NonNull FirebaseRecyclerOptions options) {
        super(options);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        senderchat = LayoutInflater.from(parent.getContext()).inflate(R.layout.senderchat,parent,false);
        reciverchat = LayoutInflater.from(parent.getContext()).inflate(R.layout.reciverchat,parent,false);
        myviewholder1_obj = new myviewholder1(senderchat);
        myviewholder2_obj = new myviewholder2(reciverchat);
        if(viewType==0) return myviewholder1_obj;
        else return myviewholder2_obj;
    }

    @Override
    public int getItemViewType(int position) {
        String time = getItem(position).get("time").toString();
        if(time.substring(time.length()-10).equals(MainActivity.mobile))
        {
            return 0;
        }
        return 1;
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position, @NonNull @NotNull HashMap model) {
        String time = model.get("time").toString();
        time = time.substring(0, time.length() - 11);
        Date simpledateformate = null;
        try {
            simpledateformate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date now = new Date();
        if(getItemViewType(position)==0) {
            if (simpledateformate.getDate() == now.getDate() && simpledateformate.getMonth() == now.getMonth() && simpledateformate.getYear() == now.getYear()) {
                ((myviewholder1) holder).time.setText(simpledateformate.getHours() + ":" + simpledateformate.getMinutes());
            } else {
                ((myviewholder1) holder).time.setText(simpledateformate.getHours() + ":" + simpledateformate.getMinutes() + "\n" + new SimpleDateFormat("MMM").format(simpledateformate) + " " + simpledateformate.getDate());
                ((myviewholder1) holder).time.setTextSize(12);
            }
            ((myviewholder1)holder).message.setText(model.get("message").toString());
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutparams.setMargins(6,6,10,6);
            ((myviewholder1)holder).message.setLayoutParams(layoutparams);
        }
        else{
            if (simpledateformate.getDate() == now.getDate() && simpledateformate.getMonth() == now.getMonth() && simpledateformate.getYear() == now.getYear()) {
                ((myviewholder2) holder).time.setText(simpledateformate.getHours() + ":" + simpledateformate.getMinutes());
            } else {
                ((myviewholder2) holder).time.setText(simpledateformate.getHours() + ":" + simpledateformate.getMinutes() + "\n" + new SimpleDateFormat("MMM").format(simpledateformate) + " " + simpledateformate.getDate());
                ((myviewholder2) holder).time.setTextSize(12);
            }
            ((myviewholder2)holder).message.setText(model.get("message").toString().trim());
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutparams.setMargins(6,6,10,6);
            ((myviewholder2)holder).message.setLayoutParams(layoutparams);
        }
    }

    class myviewholder1 extends RecyclerView.ViewHolder
    {
        TextView message,time;
        public myviewholder1(@NonNull View itemView)
        {
            super(itemView);
            message = itemView.findViewById(R.id.sender_message);
            time = itemView.findViewById(R.id.sender_time);
        }
    }

    private class myviewholder2 extends RecyclerView.ViewHolder{

        TextView message,time;
        public myviewholder2(View reciverchat) {
            super(reciverchat);
            message = itemView.findViewById(R.id.reciver_message);
            time = itemView.findViewById(R.id.reciver_time);
        }
    }

}