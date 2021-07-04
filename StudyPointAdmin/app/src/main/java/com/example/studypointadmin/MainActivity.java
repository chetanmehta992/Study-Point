package com.example.studypointadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        recyclerView = findViewById(R.id.recycleview);

        FirebaseRecyclerOptions<HashMap> options = new FirebaseRecyclerOptions.Builder<HashMap>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("verified"), snapshot -> {
                    HashMap info = (HashMap) snapshot.getValue();
                    info.put("id",snapshot.getKey());
                    return info;
                }).build();
        recycle_adapter adapter = new recycle_adapter(options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
class recycle_adapter extends FirebaseRecyclerAdapter<HashMap,recycle_adapter.holderclass> {


    public recycle_adapter(@NonNull FirebaseRecyclerOptions options) { super(options); }

    @NonNull
    @Override
    public recycle_adapter.holderclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout, parent, false);
        holderclass holderclass = new holderclass(view);
        return holderclass;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onBindViewHolder(@NonNull @NotNull holderclass holder, int position, @NonNull @NotNull HashMap model) {
        holder.id = model.get("id").toString();
        holder.name.setText(model.get("name").toString());
        Picasso.get().load(model.get("id_proof").toString()).into(holder.image);
        if(model.get("verified").equals("true"))
            holder.button.setChecked(true);
        else if(model.get("verified").equals("false"))
            holder.button.setChecked(false);
        holder.type.setText(model.get("type").toString());
        if (model.containsKey("university"))
            holder.university.setText(model.get("university").toString());
        else
            holder.university.setText(model.get("school").toString());
    }

    class holderclass extends RecyclerView.ViewHolder {
        View pdf_view;
        ImageView image;
        TextView name,type,university;
        Switch button;
        String id;

        public holderclass(@NonNull View itemView) {
            super(itemView);
            pdf_view = itemView;
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
            university = itemView.findViewById(R.id.university);
            button = itemView.findViewById(R.id.button);
            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        HashMap verified = new HashMap();
                        verified.put("verified","true");
                        FirebaseDatabase.getInstance().getReference().child("Users").child(id).updateChildren(verified);
                    }
                    else{
                        HashMap verified = new HashMap();
                        verified.put("verified","false");
                        FirebaseDatabase.getInstance().getReference().child("Users").child(id).updateChildren(verified);
                    }

                }
            });
//            button

//            Random rnd = new Random();
//            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        }
    }
}