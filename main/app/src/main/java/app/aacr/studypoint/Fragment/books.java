package app.aacr.studypoint.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.solver.GoalRow;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.Activity.webview;
import app.aacr.studypoint.R;


public class books extends Fragment {

    public static DatabaseReference ref;
    private View root;
    public static String type;
    private RecyclerView recyclerView;

    public books(String type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ref = FirebaseDatabase.getInstance().getReference().child("Documents");
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Dashboard.dashboard.handleFragment(new HomeFragment());

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_books, container, false);
        recyclerView = root.findViewById(R.id.books);
        EditText search_bar = root.findViewById(R.id.search_bar);
        search("");
        search_bar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if(!search_bar.getText().toString().isEmpty())
                        search(search_bar.getText().toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });
        return root;
    }

    private void search(String s) {
        FirebaseRecyclerOptions<HashMap> options = new FirebaseRecyclerOptions.Builder<HashMap>()
                .setQuery(ref.orderByChild("subject_name").startAt(s).endAt(s + "\uf8ff"), snapshot -> {
                    HashMap info = (HashMap) snapshot.getValue();
                    info.put("id",snapshot.getKey());
                    return info;
                }).build();
        book_recycle_adapter adapter = new book_recycle_adapter(options,type);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        recyclerView.post(new Runnable()
        {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
class book_recycle_adapter extends FirebaseRecyclerAdapter<HashMap,book_recycle_adapter.bookholderclass> {

    private final String type;

    public book_recycle_adapter(@NonNull FirebaseRecyclerOptions options, String type) { super(options); 
    this.type=type;}

    @NonNull
    @Override
    public book_recycle_adapter.bookholderclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_recycler_view_data, parent, false);
        bookholderclass holderclass = new bookholderclass(view);
        return holderclass;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onBindViewHolder(@NonNull @NotNull bookholderclass holder, int position, @NonNull @NotNull HashMap model) {
//        holder.image.setImageBitmap(generateImageFromPdf(Uri.parse(model.get("file_uri").toString())));
        if(type.equals("search") ||
                type.equals("books") &&
                        model.containsKey("type") &&
                        model.get("type").equals("Book") &&
                        MainActivity.info.containsKey("downloaded_docs") &&
                        ((HashMap)MainActivity.info.get("downloaded_docs")).containsKey(model.get("id")) ||
                type.equals("notes") &&
                        model.containsKey("type") &&
                        model.get("type").equals("Notes") &&
                        MainActivity.info.containsKey("downloaded_docs") &&
                        ((HashMap)MainActivity.info.get("downloaded_docs")).containsKey(model.get("id")) ||
                type.equals("paper") &&
                        model.containsKey("type") &&
                        model.get("type").equals("Paper") &&
                        MainActivity.info.containsKey("downloaded_docs") &&
                        ((HashMap)MainActivity.info.get("downloaded_docs")).containsKey(model.get("id")))
        {
        holder.id = model.get("id").toString();
        holder.subject.setText(model.get("subject_name").toString().toUpperCase());
        holder.branch.setText(model.get("branch").toString().toUpperCase());
        holder.sem.setText(model.get("sem").toString());
        holder.university.setText(model.get("university").toString().toUpperCase());
        holder.uri.setText(model.get("file_uri").toString());
        holder.type.setText(model.get("type").toString().toUpperCase());
        if(model.containsKey("rate"))
        {
            holder.thumbup.setText( ""+Collections.frequency(new ArrayList<String>(((HashMap)model.get("rate")).values()), "like"));
            holder.thumbdown.setText( ""+Collections.frequency(new ArrayList<String>(((HashMap)model.get("rate")).values()), "unlike"));
            if(((HashMap)model.get("rate")).containsKey(MainActivity.mobile))
            {
                if(((HashMap)model.get("rate")).get(MainActivity.mobile).equals("like"))
                {
                    holder.thumbup.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_up_filled,0,0,0);
                    holder.thumbup.setClickable(false);
                    holder.thumbdown.setClickable(true);
                }
                else
                {
                    holder.thumbdown.setCompoundDrawablesWithIntrinsicBounds(R.drawable.thumb_down_filled,0,0,0);
                    holder.thumbup.setClickable(true);
                    holder.thumbdown.setClickable(false);
                }
            }
        }}
        else
        {
            model.clear();
            holder.pdf_view.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }
    }

    class bookholderclass extends RecyclerView.ViewHolder {
        View pdf_view;
        private final View top;
        ImageView image;
        TextView subject,branch,sem,university,uri,thumbup,thumbdown,type;
        String id;

        public bookholderclass(@NonNull View itemView) {
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
                   intent.putExtra("uri",uri.getText().toString());
                   intent.putExtra("id",id);
                   MainActivity.mainActivity.startActivity(intent);
               }
           });
           thumbup.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   HashMap info = new HashMap();
                   info.put(MainActivity.mobile,"like");
                   books.ref.child(id).child("rate").updateChildren(info);
                   thumbdown.setClickable(true);
                   thumbup.setClickable(false);
               }
           });
           thumbdown.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   HashMap info = new HashMap();
                   info.put(MainActivity.mobile,"unlike");
                   books.ref.child(id).child("rate").updateChildren(info);
                   thumbdown.setClickable(false);
                   thumbup.setClickable(true);
               }
           });

        }
    }
}