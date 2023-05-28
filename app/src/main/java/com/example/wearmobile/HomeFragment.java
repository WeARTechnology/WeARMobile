package com.example.wearmobile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Definir a classe HomeFragment que estende a classe Fragment
public class HomeFragment extends Fragment {

    // Sobrescrever o método onCreateView para inflar o layout fragment_home e retornar a view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar o layout fragment_home e retornar a view
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }
}
