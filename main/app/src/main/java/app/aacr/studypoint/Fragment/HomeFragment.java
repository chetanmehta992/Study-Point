package app.aacr.studypoint.Fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.Activity.webview;
import app.aacr.studypoint.Adapter.MostViewedAdapter;
import app.aacr.studypoint.pojo.MostViewedHelperClass;
import app.aacr.studypoint.Adapter.NotesAdapter;
import app.aacr.studypoint.R;


public class HomeFragment extends Fragment{

    static RecyclerView r1,r2,r3;
    MostViewedAdapter adapter1,adapter2,adapter3;
    ArrayList a1,a2,a3;

     public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        r1=view.findViewById(R.id.recycler1);
        r2=view.findViewById(R.id.recycler2);
        r3=view.findViewById(R.id.recycler3);
        view.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.dashboard.handleFragment(new books("search"));
            }
        });
        view.findViewById(R.id.books).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.dashboard.handleFragment(new books("books"));
            }
        });
        view.findViewById(R.id.notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.dashboard.handleFragment(new books("notes"));
            }
        });
        view.findViewById(R.id.paper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.dashboard.handleFragment(new books("paper"));
            }
        });
        view.findViewById(R.id.doubts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(view, "Coming Soon...", Snackbar.LENGTH_LONG).show();
            }
        });
        a1 = new ArrayList();
        a2 = new ArrayList();
        a3 = new ArrayList();

        r1.setHasFixedSize(true);
        r2.setHasFixedSize(true);
        r3.setHasFixedSize(true);
        r1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        r2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        r3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter1 = new MostViewedAdapter(a1,"books");
        adapter2 = new MostViewedAdapter(a2,"notes");
        adapter3 = new MostViewedAdapter(a3,"paper");

        r1.setAdapter(adapter1);
        r2.setAdapter(adapter2);
        r3.setAdapter(adapter3);

        FirebaseDatabase.getInstance().getReference().child("Documents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    HashMap model = (HashMap) snapshot1.getValue();
                    model.put("id",snapshot1.getKey());
                    if(model.containsKey("type"))
                        if(model.get("type").toString().equals("Book")) {
                            for(Object check : a1)
                            {
                                if(((HashMap)check).containsValue(snapshot1.getKey()))
                                {
                                    a1.remove(check);
                                    break;
                                }
                            }
                            a1.add(model);
                            sort_data(a1);
                            adapter1.notifyDataSetChanged();
                        }else if(model.get("type").toString().equals("Notes")) {
                            for(Object check : a2)
                            {
                                if(((HashMap)check).containsValue(snapshot1.getKey()))
                                {
                                    a2.remove(check);
                                    break;
                                }
                            }
                            a2.add(model);
                            sort_data(a2);
                            adapter2.notifyDataSetChanged();
                        }else if(model.get("type").toString().equals("Paper")) {
                            for(Object check : a3)
                            {
                                if(((HashMap)check).containsValue(snapshot1.getKey()))
                                {
                                    a3.remove(check);
                                   break;
                                }
                            }
                            a3.add(model);
                            sort_data(a3);
                            adapter3.notifyDataSetChanged();
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
        return view;
    }
    void sort_data(ArrayList modelList)
    {
    Collections.sort(modelList, new Comparator<HashMap>() {

        @Override
        public int compare(HashMap o1, HashMap o2) {
            int x=0,y=0,x1=0,y1=0;
            if(o1.containsKey("rate"))
            {
                x = Collections.frequency(new ArrayList<String>(((HashMap)o1.get("rate")).values()), "like");
                x1 = Collections.frequency(new ArrayList<String>(((HashMap)o1.get("rate")).values()), "unlike");
            }
            if(o2.containsKey("rate"))
            {
                y = Collections.frequency(new ArrayList<String>(((HashMap)o2.get("rate")).values()), "like");
                y1 = Collections.frequency(new ArrayList<String>(((HashMap)o2.get("rate")).values()), "unlike");
            }
            if(x>y)
            {
                return -1;
            }
            else if(x<y)
            {
                return 1;
            }
            else if(x1>y1){
                return 1;
            }
            else if(x1<y1){
                return -1;
            }
            else {
                return 0;
            }
        }
    });
    }
}