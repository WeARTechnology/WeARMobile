package com.example.wearmobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
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
        View view = inflater.inflate(R.layout.fragment_catalogo, container, false);

        recycler = view.findViewById(R.id.recycler);
        itens = new ArrayList<RecycleCatalogo>();
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        itens.add(new RecycleCatalogo("a","a","a","a","a", 10));
        adapter = new CatalogoAdapter(getContext(), itens);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        recycler.setItemAnimator(new DefaultItemAnimator());
        // Inflate the layout for this fragment



        return view;
    }
}