package com.example.wearmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {

    private RecyclerView recycler;
    private CatalogoAdapter adapter;
    private ArrayList<RecycleCatalogo> itens;
    List<Produto> produtos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogo, container, false);


        Gson gson = new Gson();
        String url = "https://weartechhost.azurewebsites.net/api/WebService/produtos";
        RequestQueue requisicao = Volley.newRequestQueue(view.getContext());
        StringRequest busca = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Type productListType = new TypeToken<List<Produto>>() {}.getType();
                        produtos = gson.fromJson(response,productListType);
                        Log.i("Resultado" , response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requisicao.add(busca);

        recycler = view.findViewById(R.id.recycler);
        itens = new ArrayList<RecycleCatalogo>();
        for (Produto p: produtos) {
            itens.add(new RecycleCatalogo(p.nome,"a","a","a","R$",  p.preco.floatValue()));
        }
        adapter = new CatalogoAdapter(getContext(), itens);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        recycler.setItemAnimator(new DefaultItemAnimator());
        // Inflate the layout for this fragment



        return view;
    }
}