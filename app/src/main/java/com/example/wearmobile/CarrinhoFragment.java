package com.example.wearmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
        itens.add(new RecycleCarrinho("a"));
        itens.add(new RecycleCarrinho("a"));
        itens.add(new RecycleCarrinho("a"));
        itens.add(new RecycleCarrinho("a"));
        itens.add(new RecycleCarrinho("a"));
        itens.add(new RecycleCarrinho("a"));
        itens.add(new RecycleCarrinho("a"));
        return view;


    }
}