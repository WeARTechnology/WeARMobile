package com.example.wearmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;


public class CarrinhoFragment extends Fragment {

    ImageView voltar;
    private RecyclerView recycler_carrinho;
    private CarrinhoAdapter adapter;
    private ArrayList<RecycleCarrinho> itens;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);
        voltar = view.findViewById(R.id.btnVoltar);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomeFragment()).commit();
                MainActivity mainActivity = new MainActivity();
                mainActivity.cleanSelected(getActivity());
            }
        });

        recycler_carrinho = view.findViewById(R.id.recycler_carrinho);
        itens = new ArrayList<RecycleCarrinho>();
        itens.add(new RecycleCarrinho("a","a","a","a",1,1,1,1));
        adapter = new CarrinhoAdapter(getContext(), itens);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler_carrinho.setLayoutManager(layoutManager);
        recycler_carrinho.setAdapter(adapter);
        recycler_carrinho.setItemAnimator(new DefaultItemAnimator());


        return view;


    }
}