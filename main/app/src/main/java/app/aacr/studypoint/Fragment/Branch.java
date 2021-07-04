package app.aacr.studypoint.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.Activity.webview;
import app.aacr.studypoint.R;
import tellh.com.recyclertreeview_lib.LayoutItemType;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class Branch extends Fragment {

    private RecyclerView rv;
    private TreeViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_branch, container, false);
        rv = (RecyclerView) root.findViewById(R.id.rv);
        initData();
        return root;
    }

    private void initData() {
        List<TreeNode> nodes = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Documents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    TreeNode<Dir> year = null,sem = null,university = null,subject_name = null,type = null,branch = null;
                    boolean y = false,u = false,b = false,s = false,sub = false,t = false;
                    HashMap info = (HashMap) snapshot1.getValue();
                    int i;boolean for_part;
                    for(i = 0,for_part = true ; i < nodes.size();i++)
                    {
                        if (((Dir)nodes.get(i).getContent()).dirName.equals(info.get("youd").toString())) {
                            year = nodes.get(i);
                            for_part = false;
                            break;
                        }
                    }
                    if(for_part) {
                        year = new TreeNode<>(new Dir(info.get("youd").toString()));
                        y = true;
                    }
                    for(i = 0,for_part = true ; year.getChildList()!=null && i < year.getChildList().size();i++)
                    {
                        if (((Dir)year.getChildList().get(i).getContent()).dirName.equals(info.get("university").toString().toUpperCase())) {
                            university = year.getChildList().get(i);
                            for_part = false;
                            break;
                        }
                    }
                    if(for_part) {
                        university = new TreeNode<>(new Dir(info.get("university").toString().toUpperCase()));
                        u = true;
                    } 
                    for(i = 0,for_part = true ; university.getChildList()!=null && i < university.getChildList().size();i++)
                    {
                        if (((Dir)university.getChildList().get(i).getContent()).dirName.equals(info.get("branch").toString().toUpperCase())) {
                            branch = university.getChildList().get(i);
                            for_part = false;
                            break;
                        }
                    }
                    if(for_part) {
                        branch = new TreeNode(new Dir(info.get("branch").toString().toUpperCase()));
                        b = true;
                    }
                    for(i = 0,for_part = true ; branch.getChildList()!=null && i < branch.getChildList().size();i++)
                    {
                        if (((Dir)branch.getChildList().get(i).getContent()).dirName.equals("Sem - "+info.get("sem"))) {
                            sem = branch.getChildList().get(i);
                            for_part = false;
                            break;
                        }
                    }
                    if(for_part) {
                        sem = new TreeNode(new Dir("Sem - "+info.get("sem").toString()));
                        s = true;
                    }
                    for(i = 0,for_part = true ; sem.getChildList()!=null && i < sem.getChildList().size();i++)
                    {
                        if (((Dir)sem.getChildList().get(i).getContent()).dirName.equals(info.get("subject_name").toString().toUpperCase())) {
                            subject_name = sem.getChildList().get(i);
                            for_part = false;
                            break;
                        }
                    }
                    if(for_part) {
                        subject_name = new TreeNode(new Dir(info.get("subject_name").toString().toUpperCase()));
                        sub = true;
                    } 
                    for(i = 0,for_part = true ; subject_name.getChildList()!=null && i < subject_name.getChildList().size();i++)
                    {
                        if (((Dir)subject_name.getChildList().get(i).getContent()).dirName.equals(info.get("type").toString().toUpperCase())) {
                            type = subject_name.getChildList().get(i);
                            for_part = false;
                            break;
                        }
                    }
                    if(for_part) {
                        type = new TreeNode(new Dir(info.get("type").toString().toUpperCase()));
                        t = true;
                    }
                    type.addChild(new TreeNode<>(new File1(info.get("name").toString().toUpperCase(),info.get("file_uri").toString(),snapshot1.getKey())));
                    if(t) subject_name.addChild(type);
                    if(sub) sem.addChild(subject_name);
                    if(s) branch.addChild(sem);
                    if(b) university.addChild(branch);
                    if(u) year.addChild(university);
                    if(y) nodes.add(year);
                }
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new TreeViewAdapter(nodes, Arrays.asList(new FileNodeBinder(), new DirectoryNodeBinder()));
                adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
                    @Override
                    public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {
                        if (!node.isLeaf()) {
                            onToggle(!node.isExpand(), holder);
                            if (!node.isExpand())
                                adapter.collapseBrotherNode(node);
                        }
                        return false;
                    }

                    @Override
                    public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                        DirectoryNodeBinder.ViewHolder dirViewHolder = (DirectoryNodeBinder.ViewHolder) holder;
                        final ImageView ivArrow = dirViewHolder.getIvArrow();
                        int rotateDegree = isExpand ? 90 : -90;
                        ivArrow.animate().rotationBy(rotateDegree)
                                .start();
                    }
                });
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
class Dir implements LayoutItemType {
    public String dirName;

    public Dir(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_dir;
    }
}
class File1 implements LayoutItemType {
    public String file_uri;
    public String fileName;
    public String id;

    public File1(String fileName, String file_uri, String id) {
        this.fileName = fileName;
        this.file_uri = file_uri;
        this.id = id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_file;
    }
}
class FileNodeBinder extends TreeViewBinder<FileNodeBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {
        File1 fileNode = (File1) node.getContent();
        holder.tvName.setText(fileNode.fileName);
        holder.uri.setText(fileNode.file_uri);
        holder.id = fileNode.id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_file;
    }

    static class ViewHolder extends TreeViewBinder.ViewHolder {
        public TextView tvName,uri;
        public String id;

        public ViewHolder(View rootView) {
            super(rootView);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
            this.uri = (TextView) rootView.findViewById(R.id.uri);
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.mainActivity, webview.class);
                    intent.putExtra("uri",uri.getText().toString());
                    intent.putExtra("id",id);
                    MainActivity.mainActivity.startActivity(intent);
                }
            });
        }

    }
}
class DirectoryNodeBinder extends TreeViewBinder<DirectoryNodeBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {
        holder.ivArrow.setRotation(0);
        holder.ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_18dp);
        int rotateDegree = node.isExpand() ? 90 : 0;
        holder.ivArrow.setRotation(rotateDegree);
        Dir dirNode = (Dir) node.getContent();
        holder.tvName.setText(dirNode.dirName);
        if (node.isLeaf())
            holder.ivArrow.setVisibility(View.INVISIBLE);
        else holder.ivArrow.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_dir;
    }

    public static class ViewHolder extends TreeViewBinder.ViewHolder {
        private final ImageView ivArrow;
        private final TextView tvName;

        public ViewHolder(View rootView) {
            super(rootView);
            this.ivArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
            this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
        }

        public ImageView getIvArrow() {
            return ivArrow;
        }

        public TextView getTvName() {
            return tvName;
        }
    }
}