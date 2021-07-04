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
import android.widget.Toast;

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
import java.util.Calendar;

import app.aacr.studypoint.Activity.Completeyourprofile;
import app.aacr.studypoint.Activity.MainActivity;
import app.aacr.studypoint.pojo.ClgstudentInfo;
import app.aacr.studypoint.ImageResizer;
import app.aacr.studypoint.R;

public class clg_registertation extends Fragment {

    private Uri fileUri;
    private TextView id_proof_name;
    private DatabaseReference database ;
    private StorageReference storage;
    private TextInputEditText fullname,age,univercity,specification,securitycode,yog;
    private View root;
    private ProgressBar progressBar;
    private String profile = null;
    private String Id_proof = null;
    private String gender = "Male";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root=inflater.inflate(R.layout.fragment_clg_registertation, container, false);
        database = FirebaseDatabase.getInstance().getReference("Users");
        storage = FirebaseStorage.getInstance().getReference();

        progressBar = getActivity().findViewById(R.id.spin_kit);
        progressBar.setIndeterminateDrawable(new CubeGrid());

        fullname= root.findViewById(R.id.user_fullname);
        age= root.findViewById(R.id.user_age);
        univercity= root.findViewById(R.id.user_university);
        specification= root.findViewById(R.id.user_specification);
        securitycode= root.findViewById(R.id.user_security_code);
        yog= root.findViewById(R.id.user_yog);
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
                    else if (Integer.parseInt(age.getEditText().getText().toString())<20)
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

        specification.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout specfication = root.findViewById(R.id.specification);
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

        yog.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout yog = root.findViewById(R.id.yog);
                if(!hasFocus) {
                    if (yog.getEditText().getText().toString().isEmpty())
                        yog.setError("Empty Field");
                    else if (Integer.parseInt(yog.getEditText().getText().toString())<2000)
                        yog.setError("Invalid year of Graduation!");
                    else if (Integer.parseInt(yog.getEditText().getText().toString())>Calendar.getInstance().get(Calendar.YEAR)+3)
                        yog.setError("Invalid year of Graduation!");
                    else
                        yog.setErrorEnabled(false);
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
        String specialization1 = specification.getText().toString();
        String yog1 = yog.getText().toString();
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
                                ClgstudentInfo clgstudentInfo = new ClgstudentInfo(profile, name1, age1, gender, university1, specialization1, yog1, security_code1, Id_proof);
                                database.child(Completeyourprofile.mobile).setValue(clgstudentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            if(profile!=null) getActivity().getApplicationContext().getSharedPreferences("User_profile",Context.MODE_PRIVATE).edit().putString("profile",profile).apply();
                                            getActivity().getApplicationContext().getSharedPreferences("User_name", Context.MODE_PRIVATE).edit().putString("name",name1).apply();
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
        TextInputLayout specfication = root.findViewById(R.id.specification);
        TextInputLayout securitycode = root.findViewById(R.id.security_code);
        TextInputLayout yog = root.findViewById(R.id.yog);
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
        else if (Integer.parseInt(age.getEditText().getText().toString())<20) {
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
        else if(!specfication.getEditText().getText().toString().matches("^[A-Z\\sa-z]{2,}+$")) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setError("Enter AtLeast Two Characters");
            return false;
        }
        else if (yog.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setErrorEnabled(false);
            yog.setError("Empty Field");
            return false;
        }
        else if (Integer.parseInt(yog.getEditText().getText().toString())<2000 || Integer.parseInt(yog.getEditText().getText().toString())>Calendar.getInstance().get(Calendar.YEAR)+3) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setErrorEnabled(false);
            yog.setError("Invalid year of Graduation!");
            return false;
        }
        else if (securitycode.getEditText().getText().toString().isEmpty()) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setErrorEnabled(false);
            yog.setErrorEnabled(false);
            securitycode.setError("Empty Field");
            return false;
        }
        else if (!securitycode.getEditText().getText().toString().matches("^[0-9]{6}$")) {
            name.setErrorEnabled(false);
            age.setErrorEnabled(false);
            university.setErrorEnabled(false);
            specfication.setErrorEnabled(false);
            yog.setErrorEnabled(false);
            securitycode.setError("Enter AtLeast Six Digits");
            return false;
        }
        name.setErrorEnabled(false);
        age.setErrorEnabled(false);
        university.setErrorEnabled(false);
        specfication.setErrorEnabled(false);
        yog.setErrorEnabled(false);
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