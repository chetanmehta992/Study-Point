package app.aacr.studypoint.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.CubeGrid;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import app.aacr.studypoint.Activity.Completeyourprofile;
import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.ImageResizer;
import app.aacr.studypoint.R;
import app.aacr.studypoint.pojo.ClgstudentInfo;

import static app.aacr.studypoint.Activity.Completeyourprofile.fileUri;

public class Upload_document extends Fragment {

    private TextView file_name;
    private View root;
    private Spinner spinner;
    private DatabaseReference database ;
    private StorageReference storage;
    private ProgressBar progressBar;
    private TextInputEditText name,sem,university,branch,youd,subject_name;
    private Uri fileuri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        root = inflater.inflate(R.layout.fragment_upload_document, container, false);
        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference();
        progressBar = root.findViewById(R.id.spin_kit);
        progressBar.setIndeterminateDrawable(new CubeGrid());

        spinner = root.findViewById(R.id.spinner);
        name = root.findViewById(R.id.name1);
        sem = root.findViewById(R.id.sem1);
        university = root.findViewById(R.id.university1);
        branch = root.findViewById(R.id.branch1);
        youd = root.findViewById(R.id.youd1);
        subject_name = root.findViewById(R.id.subject_name1);
        file_name = root.findViewById(R.id.file_name);

        root.findViewById(R.id.attach).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("application/pdf");
                startActivityForResult(Intent.createChooser(gallery,"Choose ID-Proof"),1);
            }
        });

        root.findViewById(R.id.upload);

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout name = root.findViewById(R.id.name);
                if(!hasFocus) {
                    if (name.getEditText().getText().toString().isEmpty())
                        name.setError("Empty Field");
                    else if(!name.getEditText().getText().toString().matches("^[A-Z\\sa-z0-9]{2,}+$"))
                        name.setError("Enter AtLeast Three Characters");
                    else
                        name.setErrorEnabled(false);
                }
            }
        });

        sem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout age = root.findViewById(R.id.sem);
                if(!hasFocus) {
                    if (age.getEditText().getText().toString().isEmpty())
                        age.setError("Empty Field");
                    else if (Integer.parseInt(age.getEditText().getText().toString())>=8 && Integer.parseInt(age.getEditText().getText().toString())<1)
                        age.setError("Invalid age!");
                    else
                        age.setErrorEnabled(false);
                }
            }
        });

        university.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout university = root.findViewById(R.id.university);
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

        branch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout specfication = root.findViewById(R.id.branch);
                if(!hasFocus) {
                    if (specfication.getEditText().getText().toString().isEmpty())
                        specfication.setError("Empty Field");
                    else if (!specfication.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$"))
                        specfication.setError("Do not use Special Symbols");
                    else if(!specfication.getEditText().getText().toString().matches("^[A-Z\\sa-z]{2,}+$"))
                        specfication.setError("Enter AtLeast Two Characters");
                    else
                        specfication.setErrorEnabled(false);
                }
            }
        });


        youd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout yog = root.findViewById(R.id.youd);
                if(!hasFocus) {
                    if (yog.getEditText().getText().toString().isEmpty())
                        yog.setError("Empty Field");
                    else if (Integer.parseInt(yog.getEditText().getText().toString()) < 2000)
                        yog.setError("Invalid year of Graduation!");
                    else if (Integer.parseInt(yog.getEditText().getText().toString()) > Calendar.getInstance().get(Calendar.YEAR))
                        yog.setError("Invalid year of Graduation!");
                    else
                        yog.setErrorEnabled(false);
                }
            }
        });
        subject_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout name = root.findViewById(R.id.subject_name);
                if(!hasFocus) {
                    if (name.getEditText().getText().toString().isEmpty())
                        name.setError("Empty Field");
                    else if(!name.getEditText().getText().toString().matches("^[A-Z\\sa-z0-9]{2,}+$"))
                        name.setError("Enter AtLeast Three Characters");
                    else
                        name.setErrorEnabled(false);
                }
            }
        });
        root.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validation() && fileuri!=null)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    upload();
                }
                else
                {
                    Snackbar.make(root, "Please choose file", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void upload() {
        String name1 = name.getText().toString().toLowerCase();
        String sem1 = sem.getText().toString();
        String university1 = university.getText().toString();
        String branch1 = branch.getText().toString();
        String youd1 = youd.getText().toString();
        String subject_name1 = subject_name.getText().toString();
//        String unique = ""+System.currentTimeMillis();
        String unique = database.child("Documents").push().getKey();
        StorageReference path = storage.child("Documents").child(unique);
        path.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String file_downloaded = task.getResult().toString();
                                HashMap info = new HashMap();
                                info.put("uploader",MainActivity.mobile);
                                info.put("name",name1);
                                info.put("sem",sem1);
                                info.put("university",university1);
                                info.put("branch",branch1);
                                info.put("youd",youd1);
                                info.put("subject_name",subject_name1.toLowerCase());
                                info.put("file_uri",file_downloaded);
                                info.put("type",spinner.getSelectedItem().toString());
                                database.child("Documents").child(unique).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            HashMap upload_document_increase = new HashMap();
                                            if(MainActivity.info.containsKey("upload")) {
                                                upload_document_increase.put("upload",""+(Integer.parseInt(MainActivity.info.get("upload").toString())+1));
                                            }else {
                                                upload_document_increase.put("upload","1");
                                            }
                                            database.child("Users").child(MainActivity.mobile).updateChildren(upload_document_increase);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Snackbar.make(root, "Uploaded Successfully", Snackbar.LENGTH_LONG).show();
                                            fileuri = null;
                                            file_name.setText("No File");
                                            name.setText("");
                                            sem.setText("");
                                            university.setText("");
                                            branch.setText("");
                                            youd.setText("");
                                            subject_name.setText("");
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
                }
            }
        });
    }
    private boolean Validation() {
        TextInputLayout name = root.findViewById(R.id.name);
        TextInputLayout age = root.findViewById(R.id.sem);
        TextInputLayout university = root.findViewById(R.id.university);
        TextInputLayout specfication = root.findViewById(R.id.branch);
        TextInputLayout subject_name = root.findViewById(R.id.subject_name);
        TextInputLayout youd = root.findViewById(R.id.youd);
        if (name.getEditText().getText().toString().isEmpty()) {
            name.setError("Empty Field");
            return false;
        }
        else if(!name.getEditText().getText().toString().matches("^[A-Z\\sa-z0-9]{2,}+$")) {
            name.setError("Enter AtLeast Three Characters");
            return false;
        }
        else if (age.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setError("Empty Field");
            return false;
        }
        else if (Integer.parseInt(age.getEditText().getText().toString())>=8 && Integer.parseInt(age.getEditText().getText().toString())<1) {
            name.setErrorEnabled(false);
            age.setError("Invalid age!");
            return false;
        }
        else if (university.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setError("Empty Field");
            return false;
        }
        else if (!university.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$")) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setError("Do not use Special Symbols");
            return false;
        }
        else if(!university.getEditText().getText().toString().matches("^[A-Z\\sa-z]{3,}+$")) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setError("Enter AtLeast Three Characters");
            return false;
        }
        else if (specfication.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setError("Empty Field");
            return false;
        }
        else if (!specfication.getEditText().getText().toString().matches("^[A-Z\\sa-z]+$")) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setError("Do not use Special Symbols");
            return false;
        }
        else if(!specfication.getEditText().getText().toString().matches("^[A-Z\\sa-z0-9]{2,}+$")) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setError("Enter AtLeast Two Characters");
            return false;
        }
        else if (youd.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setErrorEnabled(false);
            youd.setError("Empty Field");
            return false;
        }
        else if (Integer.parseInt(youd.getEditText().getText().toString())<2000 || Integer.parseInt(youd.getEditText().getText().toString())>Calendar.getInstance().get(Calendar.YEAR)) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setErrorEnabled(false);
            youd.setError("Invalid year of Graduation!");
            return false;
        }
        if (subject_name.getEditText().getText().toString().isEmpty()) {
            subject_name.setError("Empty Field");
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setErrorEnabled(false);
            youd.setError("Invalid year of Graduation!");
            return false;
        }
        else if(!subject_name.getEditText().getText().toString().matches("^[A-Z\\sa-z0-9]{2,}+$")) {
            subject_name.setError("Enter AtLeast Three Characters");
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setErrorEnabled(false);
            youd.setError("Invalid year of Graduation!");
            return false;
        }
        name.setErrorEnabled(false);
        age.setErrorEnabled(false);
        university.setErrorEnabled(false);
        specfication.setErrorEnabled(false);
        youd.setErrorEnabled(false);
        subject_name.setErrorEnabled(false);
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == 1) {
            fileuri = data.getData();
            file_name.setText(data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/")+1));
        }
    }
}