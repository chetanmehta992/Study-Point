package app.aacr.studypoint.Fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.R;

public class Search_Fragment extends Fragment {
static Search_Fragment search_fragment;
    RecyclerView display_people;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Dashboard.dashboard.handleFragment(new chatfragment());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        search_fragment= this;
        View root = inflater.inflate(R.layout.fragment_search_, container, false);
        EditText search_bar = root.findViewById(R.id.search_bar);
        display_people = root.findViewById(R.id.people1);

        display_people.setLayoutManager(new LinearLayoutManager(getActivity()));
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!search_bar.getText().toString().isEmpty())
                processsearch(search_bar.getText().toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return root;
    }

    private void processsearch(String s)
    {
        FirebaseRecyclerOptions<HashMap> options = new FirebaseRecyclerOptions.Builder<HashMap>().setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name").startAt(s).endAt(s + "\uf8ff"), snapshot -> {
            if(!snapshot.getKey().equals(MainActivity.mobile)) {
                HashMap info = (HashMap) snapshot.getValue();
                info.put("mobile",snapshot.getKey());
                return info;
            }
            return new HashMap();
        }).build();
        recycleAdapter adapter = new recycleAdapter(options);
        display_people.setAdapter(adapter);
        adapter.startListening();
    }
}

class recycleAdapter extends FirebaseRecyclerAdapter<HashMap,recycleAdapter.myviewholder>
{
    public recycleAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull HashMap model) {
      if(model.isEmpty()) {
          holder.chat_layout_gone.setLayoutParams(new RecyclerView.LayoutParams(0,0));
      }
      else{
          if(model.get("Contacts")!=null)
              if(((HashMap)model.get("Contacts")).get(MainActivity.mobile)!=null)
              {
                  holder.chat_ref.setText((((HashMap)((HashMap)model.get("Contacts")).get(MainActivity.mobile)).get("chat_ref")).toString());
              }
          holder.name.setText(toTitleCase(model.get("name").toString()));
          holder.status.setText(model.get("status").toString());
          holder.mobile.setText(model.get("mobile").toString());
          if(model.get("online").toString().equals("true"))
          {
              holder.online.setBackgroundColor(Color.GREEN);
          }else
          {
              holder.online.setBackgroundColor(Color.RED);
          }
          if(model.get("profile") != null)
          Glide.with(holder.img.getContext()).load(model.get("profile")).into(holder.img);
          else
             holder.img.setBackgroundResource(R.drawable.profile);
      }}

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user,parent,false);
        return new myviewholder(view);
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
    class myviewholder extends RecyclerView.ViewHolder
    {
        ImageView img;
        TextView name,status,mobile,chat_ref,unread;
        CardView online;
        View chat_layout_gone;
        public myviewholder(@NonNull View itemView)
        {
            super(itemView);
            chat_layout_gone = itemView;
            img = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            online = itemView.findViewById(R.id.online);
            mobile = itemView.findViewById(R.id.mobile);
            unread = itemView.findViewById(R.id.unread);
            chat_ref = itemView.findViewById(R.id.chat_ref);
            chat_layout_gone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(chat_ref.getText().toString().isEmpty()) {
                        Dashboard.dashboard.handleFragment(new chat_message(null,mobile.getText().toString()));
                    }
                    else {
                        Dashboard.dashboard.handleFragment(new chat_message(chat_ref.getText().toString(),mobile.getText().toString()));
                    }
                }
            });
        }
    }
}