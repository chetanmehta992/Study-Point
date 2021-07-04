package app.aacr.studypoint.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.io.IOException;

import app.aacr.studypoint.Activity.Completeyourprofile;
import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.ImageResizer;
import app.aacr.studypoint.R;
import app.aacr.studypoint.pojo.SchoolStudentInfo;

public class school_registration extends Fragment {

    private Uri fileUri;
    private TextView id_proof_name;
    private DatabaseReference database ;
    private StorageReference storage;
    private TextInputEditText fullname,age,univercity,Class,securitycode;
    private View root;
    private ProgressBar progressBar;
    private String profile = null;
    private String Id_proof = null;
    private String gender = "Male";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root=inflater.inflate(R.layout.fragment_school_registration, container, false);
        database = FirebaseDatabase.getInstance().getReference("Users");
        storage = FirebaseStorage.getInstance().getReference();

        progressBar = getActivity().findViewById(R.id.spin_kit);
        progressBar.setIndeterminateDrawable(new CubeGrid());

        fullname= root.findViewById(R.id.user_fullname);
        age= root.findViewById(R.id.user_age);
        univercity= root.findViewById(R.id.user_university);
        Class= root.findViewById(R.id.user_class);
        securitycode= root.findViewById(R.id.user_security_code);
        id_proof_name=root.findViewById(R.id.id_proof_name);

        fullname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout name = root.findViewById(R.id.name);
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

        age.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout age = root.findViewById(R.id.age);
                if(!hasFocus) {
                    if (age.getEditText().getText().toString().isEmpty())
                        age.setError("Empty Field");
                    else if (Integer.parseInt(age.getEditText().getText().toString())<10)
                        age.setError("Invalid age!");
                    else
                        age.setErrorEnabled(false);
                }
            }
        });

        univercity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        Class.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout Class = root.findViewById(R.id.Class);
                if(!hasFocus) {
                    if (Class.getEditText().getText().toString().isEmpty())
                        Class.setError("Empty Field");
                    else if(Integer.parseInt(Class.getEditText().getText().toString()) < 10 || Integer.parseInt(Class.getEditText().getText().toString()) > 12 )
                        Class.setError("Invalid Class!!");
                    else
                        Class.setErrorEnabled(false);
                }
            }
        });

        securitycode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout securitycode = root.findViewById(R.id.security_code);
                if(!hasFocus) {
                    if (securitycode.getEditText().getText().toString().isEmpty())
                        securitycode.setError("Empty Field");
                    else if (!securitycode.getEditText().getText().toString().matches("^[0-9]{6}$"))
                        securitycode.setError("Enter AtLeast Six Digits");
                    else
                        securitycode.setErrorEnabled(false);
                }
            }
        });

        root.findViewById(R.id.id_proof).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(Intent.createChooser(gallery,"Choose ID-Proof"),1);
            }
        });

        root.findViewById(R.id.Done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validation()) {
                    if(fileUri == null) {
                        Snackbar.make(getActivity().findViewById(R.id.parent), "Please choose your ID proof", Snackbar.LENGTH_LONG).show();
                    }
                    else if(Completeyourprofile.fileUri != null) {

                        progressBar.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        StorageReference path = storage.child("PROFILES").child(Completeyourprofile.mobile);
                        path.putFile(Completeyourprofile.fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()) {
                                    path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful()) {
                                                profile = task.getResult().toString();
                                                uploadinfo();
                                            }else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                Snackbar.make(getActivity().findViewById(R.id.parent), "Network issue", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Snackbar.make(getActivity().findViewById(R.id.parent), "Network issue", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        uploadinfo();
                    }
                }
            }
        });
        return root;
    }

    private void uploadinfo() {

        String name1 = fullname.getText().toString().toLowerCase();
        String age1 = age.getText().toString();
        String university1 = univercity.getText().toString();
        String grade = Class.getText().toString();
        String security_code1 = securitycode.getText().toString();
        RadioButton radiobutton = root.findViewById(R.id.male);
        if (radiobutton.isChecked()) gender = "Male";
        else gender = "Female";

        StorageReference path = storage.child("ID_PROOFS").child(Completeyourprofile.mobile);
        path.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Id_proof = task.getResult().toString();
                                SchoolStudentInfo schoolStudentInfo = new SchoolStudentInfo(profile, name1, age1, gender, university1, grade, security_code1, Id_proof);
                                database.child(Completeyourprofile.mobile).setValue(schoolStudentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            getActivity().getApplicationContext().getSharedPreferences("User_name", Context.MODE_PRIVATE).edit().putString("name",name1).apply();
                                            if(profile!=null) getActivity().getApplicationContext().getSharedPreferences("User_profile",Context.MODE_PRIVATE).edit().putString("profile",profile).apply();
                                            startActivity(new Intent(getActivity(), MainActivity.class));
                                            getActivity().finish();
                                        }else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Snackbar.make(getActivity().findViewById(R.id.parent), "Network issue", Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Snackbar.make(getActivity().findViewById(R.id.parent), "Network issue", Snackbar.LENGTH_LONG).show();
                            }

                        }
                    });
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar.make(getActivity().findViewById(R.id.parent), "Network issue", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean Validation() {
        TextInputLayout name = root.findViewById(R.id.name);
        TextInputLayout age = root.findViewById(R.id.age);
        TextInputLayout university = root.findViewById(R.id.university);
        TextInputLayout Class = root.findViewById(R.id.Class);
        TextInputLayout securitycode = root.findViewById(R.id.security_code);
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
        else if (age.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setError("Empty Field");
            return false;
        }
        else if (Integer.parseInt(age.getEditText().getText().toString())<10) {
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
        if (Class.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            Class.setError("Empty Field");
            return false;
        }
        else if(Integer.parseInt(Class.getEditText().getText().toString()) < 10 || Integer.parseInt(Class.getEditText().getText().toString()) > 12 ) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            Class.setError("Invalid Class!!");
            return false;
        }
        else if (securitycode.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            Class.setErrorEnabled(false);
            securitycode.setError("Empty Field");
            return false;
        }
        else if (!securitycode.getEditText().getText().toString().matches("^[0-9]{6}$")) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            Class.setErrorEnabled(false);
            securitycode.setError("Enter AtLeast Six Digits");
            return false;
        }
        name.setErrorEnabled(false);
        age.setErrorEnabled(false);
        university.setErrorEnabled(false);
        Class.setErrorEnabled(false);
        securitycode.setErrorEnabled(false);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == 1) {
            Uri image = data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),image);
                Bitmap reduce = ImageResizer.reduceBitmapSize(bitmap,200000);
                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), reduce, null, null);
                fileUri = Uri.parse(path);
                id_proof_name.setText(data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/")+1));
            }
            catch (IOException e)
            {
                Snackbar.make(getActivity().findViewById(R.id.parent), "Reselect the Id-Proof", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}