package com.example.wearmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {

    private RecyclerView recycler;
    private CatalogoAdapter adapter;
    private ArrayList<RecycleCatalogo> itens;
    List<Produto> produtos = new ArrayList<>();

    Gson gson = new Gson();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogo, container, false);



        String url = "https://weartechhost.azurewebsites.net/api/WebService/produtos";
        RequestQueue requisicao = Volley.newRequestQueue(view.getContext());
        recycler = view.findViewById(R.id.recycler);
        itens = new ArrayList<RecycleCatalogo>();

        pegarProdutos(url, requisicao, new ProdutoCallback() {
            @Override
            public void onSuccess(List<Produto> produtosCallback) {

                itens.add(new RecycleCatalogo("","","",null));
            }

            @Override
            public void onError() {

            }
        });


        return view;
    }


    public Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    public interface ProdutoCallback {
        void onSuccess(List<Produto> produtosCallback);
        void onError();
    }

    private void pegarProdutos(String url, RequestQueue requisicao, ProdutoCallback callback) {
        JsonArrayRequest buscaProdutos = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i <response.length(); i++) {
                            try {
                                Produto p = gson.fromJson(response.getJSONObject(i).toString(),Produto.class);
                                produtos.add(p);
                                itens.add(new RecycleCatalogo(produtos.get(i).nome,"Quantidade: " + produtos.get(i).qntd,
                                        "R$: " + produtos.get(i).preco,decodeBase64ToBitmap(produtos.get(i).imagem)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        adapter = new CatalogoAdapter(getContext(), itens);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        recycler.setLayoutManager(layoutManager);
                        recycler.setItemAnimator(new DefaultItemAnimator());
                        recycler.setAdapter(adapter);

                        callback.onSuccess(produtos);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        callback.onError();
                    }
                }
        );
        requisicao.add(buscaProdutos);

    }
}