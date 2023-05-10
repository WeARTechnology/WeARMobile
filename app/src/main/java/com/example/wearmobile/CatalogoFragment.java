package com.example.wearmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CatalogoFragment extends Fragment {

    private RecyclerView recycler;
    private CatalogoAdapter adapter;
    private ArrayList<RecycleCatalogo> itens;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recycler = container.findViewById(R.id.recycler);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalogo, container, false);



    }
}