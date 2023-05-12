package com.example.wearmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class CarrinhoFragment extends Fragment {
    ImageView voltar;

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


        return view;


    }
}