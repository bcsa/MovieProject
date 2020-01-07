package com.bcsa.movieproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bcsa.movieproject.R;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private Button changePassButton, changePic;
    private ImageView profilePicture;
    private Image currPic;
    private EditText passFieldOld, passFieldNew;
    private DatabaseHelper databaseHelper;
    private Uri selectedImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        //textView.setText("This is profile fragment");

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changePassButton = view.findViewById(R.id.changePassButton);
        changePic = view.findViewById(R.id.changePic);
        passFieldOld = view.findViewById(R.id.passFieldOld);
        passFieldNew = view.findViewById(R.id.passFieldNew);
        profilePicture = view.findViewById(R.id.profilePic);

        databaseHelper = new DatabaseHelper(getContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LOGGED_USER", MODE_PRIVATE);
        String currEmail = sharedPreferences.getString("idName","");
        User.LOGGEDIN = databaseHelper.getUser(currEmail);

        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sOldPassword = passFieldOld.getText().toString();
                String sNewPassword = passFieldNew.getText().toString();
                String userPassword = User.LOGGEDIN.getPassword();

                    if (sOldPassword.equals(userPassword)) {
                        if (!sOldPassword.equals(sNewPassword)) {
                            if (sNewPassword.length() >= 4) {
                                User user = new User(User.LOGGEDIN.getId(), User.LOGGEDIN.getUserName(), User.LOGGEDIN.getEmail(), sNewPassword);
                                User.LOGGEDIN.setPassword(sNewPassword);
                                int index = databaseHelper.updateUser(user);
                                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Old password same as new", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Old pass wrong", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, 1);
            }
        });

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        this.setImage();

    }

    private void setImage(){
        currPic = databaseHelper.getImage(User.LOGGEDIN.getId());
        if (currPic != null) {
            profilePicture.setImageURI(Uri.parse(currPic.getName()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 1:
                    selectedImage = data.getData();
                    profilePicture.setImageURI(selectedImage);
                    databaseHelper = new DatabaseHelper(getContext());
                    if (currPic != null) {
                        Image image2 = new Image(currPic.getId(), String.valueOf(selectedImage), Integer.parseInt(User.LOGGEDIN.getId()), null);
                        databaseHelper.UpdateImage(image2);
                    } else {
                        databaseHelper.InsertImage(String.valueOf(selectedImage), null);
                    }
                    break;
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    currPic = databaseHelper.getImage(User.LOGGEDIN.getId());
                    if (currPic != null) {
                        profilePicture.setImageURI(Uri.parse(currPic.getName()));
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }

}