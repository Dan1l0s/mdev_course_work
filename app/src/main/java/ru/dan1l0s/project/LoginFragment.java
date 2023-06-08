package ru.dan1l0s.project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class LoginFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public String getEmail(){
        EditText username = (EditText) getView().findViewById(R.id.etLoginEmail);
        return username.getText().toString();
    }

    public String getPassword()
    {
        EditText username = (EditText) getView().findViewById(R.id.etLoginPass);
        return username.getText().toString();
    }


}