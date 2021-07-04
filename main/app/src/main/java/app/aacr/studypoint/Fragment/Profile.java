package app.aacr.studypoint.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.aacr.studypoint.Activity.Completeyourprofile;
import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.ImageResizer;
import app.aacr.studypoint.R;
import app.aacr.studypoint.pojo.ClgstudentInfo;


public class Profile extends Fragment {

    private View root;
    private ImageView profile;
    private TextView fullname,status_display,download_lable,upload_lable;
    private TextInputLayout name,university,status,security_code,branch;
    private TextInputEditText edit_name,edit_university,edit_status,edit_security_code,edit_branch;
    private DatabaseReference ref;
    private HashMap info;
    private Uri fileUri;
    private boolean update_profile = false;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.mobile);

        profile = root.findViewById(R.id.profile_image);
        fullname = root.findViewById(R.id.fullname);
        status_display = root.findViewById(R.id.status_display);

        download_lable = root.findViewById(R.id.downloads_label);
        upload_lable = root.findViewById(R.id.uploads_label);

        name = root.findViewById(R.id.layout_full_name);
        university = root.findViewById(R.id.layout_university);
        branch = root.findViewById(R.id.layout_branch);
        status = root.findViewById(R.id.layout_status);
        security_code = root.findViewById(R.id.layout_security_code);

        edit_name = root.findViewById(R.id.edit_full_name);
        edit_university = root.findViewById(R.id.edit_university);
        edit_branch = root.findViewById(R.id.edit_branch);
        edit_status = root.findViewById(R.id.edit_status);
        edit_security_code = root.findViewById(R.id.edit_security_code);

        progressBar = root.findViewById(R.id.spin_kit);
        progressBar.setIndeterminateDrawable(new CubeGrid());

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery,"Select Profile Picture"),1);
            }
        });

        edit_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (name.getEditText().getText().toString().isEmpty())
                        name.setError("Empty Field");
                    else if (!name.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$"))
                        name.setError("Do not use Special Symbols");
                    else if(!name.getEditText().getText().toString().matches("^[A-Z\\sa-z]{3,}+$"))
                        name.setError("Enter AtLeast Three Characters");
                    else
                        name.setErrorEnabled(false);
                }
            }
        });

        edit_university.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (university.getEditText().getText().toString().isEmpty())
                        university.setError("Empty Field");
                    else if (!university.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$"))
                        university.setError("Do not use Special Symbols");
                    else if(!university.getEditText().getText().toString().matches("^[A-Z\\sa-z]{3,}+$"))
                        university.setError("Enter AtLeast Three Characters");
                    else
                        university.setErrorEnabled(false);
                }
            }
        });

        edit_branch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(branch.getHint().equals("grade"))
                    if(!hasFocus) {
                        if (branch.getEditText().getText().toString().isEmpty())
                            branch.setError("Empty Field");
                        else if(Integer.parseInt(branch.getEditText().getText().toString()) < 10 || Integer.parseInt(branch.getEditText().getText().toString()) > 12 )
                            branch.setError("Invalid Class!!");
                        else
                            branch.setErrorEnabled(false);
                    }
                    else
                    if(!hasFocus) {
                        if (branch.getEditText().getText().toString().isEmpty())
                            branch.setError("Empty Field");
                        else if (!branch.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$"))
                            branch.setError("Do not use Special Symbols");
                        else if(!branch.getEditText().getText().toString().matches("^[A-Z\\sa-z]{2,}+$"))
                            branch.setError("Enter AtLeast Two Characters");
                        else
                            branch.setErrorEnabled(false);
                    }
            }
        });

        edit_security_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (security_code.getEditText().getText().toString().isEmpty())
                        security_code.setError("Empty Field");
                    else if (!security_code.getEditText().getText().toString().matches("^[0-9]{6}$"))
                        security_code.setError("Enter AtLeast Six Digits");
                    else
                        security_code.setErrorEnabled(false);
                }
            }
        });

        edit_status.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (status.getEditText().getText().toString().isEmpty())
                        status.setError("Empty Field");
                    else
                        status.setErrorEnabled(false);
                }
            }
        });

        fetch_data();

        root.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap update = new HashMap();
                if(validation())
                {
                    progressBar.setVisibility(View.VISIBLE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    update.put("name", edit_name.getText().toString().toLowerCase());
                    update.put(university.getHint().toString(), edit_university.getText().toString());
                    update.put(branch.getHint().toString(), edit_branch.getText().toString());
                    update.put("security_code", edit_security_code.getText().toString());
                    update.put("status", edit_status.getText().toString());

                    if(fileUri !=null && update_profile)
                    {
                        StorageReference path = FirebaseStorage.getInstance().getReference().child("ID_PROOFS").child(MainActivity.mobile);
                        path.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                String Id_proof = task.getResult().toString();
                                                update.put("profile",Id_proof);
                                                ref.updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            if(Id_proof!=null) getActivity().getApplicationContext().getSharedPreferences("User_profile",Context.MODE_PRIVATE).edit().putString("profile",Id_proof).apply();
                                                            getActivity().getApplicationContext().getSharedPreferences("User_name", Context.MODE_PRIVATE).edit().putString("name",edit_name.getText().toString()).apply();
                                                            Snackbar.make(root, "Successful updated!", Snackbar.LENGTH_LONG).show();
                                                        }else {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            Snackbar.make(root, "Network issue", Snackbar.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                Snackbar.make(root, "Network issue", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Snackbar.make(root, "Network issue", Snackbar.LENGTH_LONG).show();
                                    Snackbar.make(root, "Successful updated!", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else {
                        if(!info.containsKey("profile"))
                            update.put("profile",null);
                        else
                            update.put("profile",info.get("profile"));
                        ref.updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    getActivity().getApplicationContext().getSharedPreferences("User_name", Context.MODE_PRIVATE).edit().putString("name",edit_name.getText().toString()).apply();
                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Snackbar.make(root, "Network issue", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        return root;
    }

    private boolean validation() {
        if (name.getEditText().getText().toString().isEmpty()) {
            name.setError("Empty Field");
            return false;
        }
        else if (!name.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$")) {
            name.setError("Do not use Special Symbols");
            return false;
        }
        else if(!name.getEditText().getText().toString().matches("^[A-Z\\sa-z]{3,}+$")) {
            name.setError("Enter AtLeast Three Characters");
            return false;
        }
        name.setErrorEnabled(false);
        if (university.getEditText().getText().toString().isEmpty()) {
            university.setError("Empty Field");
            return false;
        }
        else if (!university.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$")) {
            university.setError("Do not use Special Symbols");
            return false;
        }
        else if(!university.getEditText().getText().toString().matches("^[A-Z\\sa-z]{3,}+$")) {
            university.setError("Enter AtLeast Three Characters");
            return false;
        }
        university.setErrorEnabled(false);
        if(branch.getHint().equals("grade")) {
            if (branch.getEditText().getText().toString().isEmpty()) {
                branch.setError("Empty Field");
                return false;
            }
            else if(Integer.parseInt(branch.getEditText().getText().toString()) < 10 || Integer.parseInt(branch.getEditText().getText().toString()) > 12 ) {
                branch.setError("Invalid Class!!");
                return false;
            }
            branch.setErrorEnabled(false);
        }
        else {
            if (branch.getEditText().getText().toString().isEmpty()) {
                branch.setError("Empty Field");
                return false;
            }
            else if (!branch.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$")) {
                branch.setError("Do not use Special Symbols");
                return false;
            }
            else if(!branch.getEditText().getText().toString().matches("^[A-Z\\sa-z]{2,}+$")) {
                branch.setError("Enter AtLeast Two Characters");
                return false;
            }
            branch.setErrorEnabled(false);
        }
        if (security_code.getEditText().getText().toString().isEmpty()) {
            security_code.setError("Empty Field");
            return false;
        }
        else if (!security_code.getEditText().getText().toString().matches("^[0-9]{6}$")) {
            security_code.setError("Enter AtLeast Six Digits");
            return false;
        }
        security_code.setErrorEnabled(false);
        if (status.getEditText().getText().toString().isEmpty()) {
            status.setError("Empty Field");
            return false;
        }
        status.setErrorEnabled(false);
        return true;
    }

    private void fetch_data() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                info = (HashMap) snapshot.getValue();
                if (info.containsKey("profile")) {
                    Glide.with(getActivity()).load(info.get("profile")).into(profile);
                }
                fullname.setText(info.get("name").toString());
                status_display.setText(info.get("status").toString());

                edit_name.setText(info.get("name").toString());
                if (info.containsKey("university")) {
                    edit_university.setText(info.get("university").toString());
                } else {
                    edit_university.setText(info.get("school").toString());
                    university.setHint("school");
                }
                if (info.containsKey("branch")) {
                    edit_branch.setText(info.get("branch").toString());
                } else if (info.containsKey("specialization")) {
                    edit_branch.setText(info.get("specialization").toString());
                    branch.setHint("specialization");
                } else {
                    edit_branch.setText(info.get("grade").toString());
                    branch.setHint("grade");
                }
                edit_security_code.setText(info.get("security_code").toString());
                edit_status.setText(info.get("status").toString());
                if (info.containsKey("downloaded_docs")) {
                    download_lable.setText(""+((HashMap)info.get("downloaded_docs")).size());
                }
                if (info.containsKey("upload")) {
                    upload_lable.setText(info.get("upload").toString());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            Uri image = data.getData();
            try {
                update_profile = true;
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),image);
                Bitmap reduce = ImageResizer.reduceBitmapSize(bitmap,15000);
                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), reduce, null, null);
                fileUri = Uri.parse(path);
                profile.setImageURI(data.getData());
            }
            catch (IOException e)
            {
                Snackbar.make(root, "Reselect the Image", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}