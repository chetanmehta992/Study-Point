package app.aacr.studypoint.Adapter;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.Activity.webview;
import app.aacr.studypoint.R;
import app.aacr.studypoint.pojo.MostViewedHelperClass;

public class MostViewedAdapter extends  RecyclerView.Adapter<MostViewedAdapter.MostViewedViewHolder> {

    ArrayList data;
    String type;

    public MostViewedAdapter(ArrayList data, String type) {
        this.data = data;
        this.type = type;
    }
    
    @NonNull
    @Override
    public MostViewedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_recycler_view_data, parent, false);
        MostViewedViewHolder mostViewedViewHolder = new MostViewedViewHolder(view);
        return mostViewedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MostViewedViewHolder holder, int position) {
         holder.id = ((HashMap)data.get(position)).get("id").toString();
            holder.subject.setText(((HashMap)data.get(position)).get("subject_name").toString().toUpperCase());
            holder.branch.setText(((HashMap)data.get(position)).get("branch").toString().toUpperCase());
            holder.sem.setText(((HashMap)data.get(position)).get("sem").toString());
            holder.university.setText(((HashMap)data.get(position)).get("university").toString().toUpperCase());
            holder.uri.setText(((HashMap)data.get(position)).get("file_uri").toString());
            holder.type.setText(((HashMap)data.get(position)).get("type").toString().toUpperCase());
            if(((HashMap)data.get(position)).containsKey("rate"))
            {
                holder.thumbup.setText( ""+ Collections.frequency(new ArrayList<String>(((HashMap)((HashMap)data.get(position)).get("rate")).values()), "like"));
                holder.thumbdown.setText( ""+Collections.frequency(new ArrayList<String>(((HashMap)((HashMap)data.get(position)).get("rate")).values()), "unlike"));
                if(((HashMap)((HashMap)data.get(position)).get("rate")).containsKey(MainActivity.mobile))
                {
                    if(((HashMap)((HashMap)data.get(position)).get("rate")).get(MainActivity.mobile).equals("like"))
                    {
                        holder.thumbdown.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_down,0,0,0);
                        holder.thumbup.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up_filled,0,0,0);
                        holder.thumbup.setClickable(false);
                        holder.thumbdown.setClickable(true);
                    }
                    else
                    {
                        holder.thumbup.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up,0,0,0);
                        holder.thumbdown.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_down_filled,0,0,0);
                        holder.thumbup.setClickable(true);
                        holder.thumbdown.setClickable(false);
                    }
                }
            }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MostViewedViewHolder extends RecyclerView.ViewHolder {
        View pdf_view;
        private final View top;
        ImageView image;
        TextView subject, branch, sem, university, uri, thumbup, thumbdown, type;
        String id;

        public MostViewedViewHolder(@NonNull View itemView) {
            super(itemView);
            pdf_view = itemView;
            image = itemView.findViewById(R.id.image);
            subject = itemView.findViewById(R.id.subject_name);
            branch = itemView.findViewById(R.id.branch_name);
            sem = itemView.findViewById(R.id.sem);
            university = itemView.findViewById(R.id.clg_name);
            uri = itemView.findViewById(R.id.uri);
            thumbup = itemView.findViewById(R.id.thumb_up);
            thumbdown = itemView.findViewById(R.id.thumb_down);
            top = itemView.findViewById(R.id.top);
            type = itemView.findViewById(R.id.type);
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            top.setBackgroundColor(color);
            itemView.findViewById(R.id.view_pdf).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.mainActivity, webview.class);
                    intent.putExtra("uri", uri.getText().toString());
                    intent.putExtra("id", id);
                    MainActivity.mainActivity.startActivity(intent);
                }
            });
            thumbup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap info = new HashMap();
                    info.put(MainActivity.mobile, "like");
                    FirebaseDatabase.getInstance().getReference().child("Documents").child(id).child("rate").updateChildren(info);
                    thumbdown.setClickable(true);
                    thumbup.setClickable(false);
                }
            });
            thumbdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap info = new HashMap();
                    info.put(MainActivity.mobile, "unlike");
                    FirebaseDatabase.getInstance().getReference().child("Documents").child(id).child("rate").updateChildren(info);
                    thumbdown.setClickable(false);
                    thumbup.setClickable(true);
                }
            });

        }
    }
}
