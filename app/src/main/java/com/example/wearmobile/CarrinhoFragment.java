package com.example.wearmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CarrinhoFragment extends Fragment {

    private ImageView voltar; //Button que volta
    private ViewGroup layoutCarrinho; //LinearLayout onde ficam todas as informações do carrinho
    private RecyclerView recycler_carrinho; //Objeto do Recycler
    private CarrinhoAdapter adapter; //Objeto do Adapter
    private ArrayList<RecycleCarrinho> itens = new ArrayList<>(); //ArrayList com os itens da recycler
    private TextView totalCompra; //TextView com o total da compra
    private Button finalizarCompra; //Botão que finaliza a compra
    private int id, qntd,tamanho; //Define os inteiros que pegam o valor do bundle
    private Gson gson = new Gson(); //Objeto de gson, para codificar e decodificar a classe
    private ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o retorno do SharedPreferences


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);
        //Pega os dados que vieram através da Fragment
        Bundle dados = getArguments();
        if (dados != null) {
            id = dados.getInt("ID");
            qntd = Integer.parseInt(dados.getString("qntd"));
            tamanho = Integer.parseInt(dados.getString("tamanho"));
        }



        //Criando o leitor e gravador no SharedPreferences
        SharedPreferences lerCart = view.getContext().getSharedPreferences("CartItens", Context.MODE_PRIVATE);
        SharedPreferences lerProd = view.getContext().getSharedPreferences("Produtos", Context.MODE_PRIVATE);
        SharedPreferences.Editor gravarCart = view.getContext().getSharedPreferences("CartItens", Context.MODE_PRIVATE).edit();

        //Atribuindo os objetos aos itens da tela
        voltar = view.findViewById(R.id.btnVoltarProduto);
        layoutCarrinho = view.findViewById(R.id.layoutCarrinho);
        totalCompra = view.findViewById(R.id.txtValordaCompra);
        finalizarCompra = view.findViewById(R.id.btnFinalizarCompra);
        recycler_carrinho = view.findViewById(R.id.recycler_carrinho);

        //Define que o Jackson não vai falhar se encontrar algo que não conhece
        leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Map<String, String> allCartShared = (Map<String, String>) lerCart.getAll();

        //Se não tiver nada no lerCart, e não tiver ID, nem Quantidade
        if (allCartShared.isEmpty() && id == 0 && qntd == 0) {
            //Remove todas as coisas da tela, e adiciona a mensagem dizendo que o carrinho está vazio
            layoutCarrinho.removeAllViews();
            View messageCarrinho = getLayoutInflater().inflate(R.layout.messagecarrinho, layoutCarrinho, false);
            layoutCarrinho.addView(messageCarrinho);

        }
        else if (!allCartShared.isEmpty() && id == 0 && qntd == 0) //Se tiver valores no shared, mas não tiver id nem quantidade
        {

            //Defino chave e valor, e pego os valores deles com base nos valores do Map allCartShared
            String chave = null;
            String valor = null;
            List<Produto> p = new ArrayList<>();
            for (Map.Entry<String, String> entry : allCartShared.entrySet()) {
                chave = entry.getKey();
                valor = entry.getValue();
                //Pego os valores no sharedPreferences, com base no que foi passado pelo Map
                try {
                    p.add(leitor.readValue(valor, Produto.class)); //Pega os valores com o leitor
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            //Define a quantidade da compra como 0
            int valorTotalCompra = 0;
            //Para cada produto, pega-se o valor somando a multiplicação da quantidade pelo preço
            for (Produto prod: p){
                valorTotalCompra += (prod.qntd*prod.preco);
            }
            //Adiciona o preço descoberto ao textview
            totalCompra.append(" R$" + valorTotalCompra + ".00");
            addCarrinho(p); //Adiciona o valor ao carrinho


        } else if (allCartShared.isEmpty() && id != 0 && qntd != 0) //Se tiver id e quantidade, mas não tiver valores no shared
        {
            Produto p = new Produto();
            Produto sharedProduct = new Produto();

            //Pego os valores no sharedPreferences
            try {
                sharedProduct = leitor.readValue(lerProd.getString("Produto" + id, ""), Produto.class); //Pega os valores com o leitor
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            p = sharedProduct;
            p.qntd = qntd;

            List<Produto> prod = new ArrayList<>();
            prod.add(p);
            addCarrinho(prod);

            gravarCart.putString("Produto" + id, gson.toJson(p));
        } else if (!allCartShared.isEmpty() && id != 0 && qntd != 0) { //Se tiver valor no carrinho, id e quantidade

            //Três objetos
            Produto p = new Produto(); //Produto que será retornado
            Produto sharedProduct = new Produto(); //Produto com todos os valores do Shared
            Produto valorCarinho = new Produto(); //Produto com os valores do carrinho
            List<Produto> valoresCarrinho = new ArrayList<>();

            //Pego os valores no sharedPreferences
            try {
                sharedProduct = leitor.readValue(lerProd.getString("Produto" + id, ""), Produto.class); //Pega os valores com o leitor
                valorCarinho = leitor.readValue(lerCart.getString("Produto" + id, ""), Produto.class); //Pega os valores com o leitor
                valoresCarrinho = leitor.readValue(lerCart.getAll().toString(), new TypeReference<List<Produto>>() { });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


            //Define p como os valores do Shared
            p = sharedProduct;
            //Define a quantidade de P, como a quantidade que já havia no carrinho mais a quantidade do Bundle
            int quantidade = 0;
            quantidade = qntd + valorCarinho.qntd;
            //Se esse valor for maior do que o disponível em estoque, altera ele pro máximo disponível em estoque, e volta uma mensagem pro usuário
            if (quantidade > sharedProduct.qntd) {
                p.qntd = sharedProduct.qntd;
                Toast.makeText(getContext(), "Valor maior do que disponível em estoque", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();
            } else {
                p.qntd = quantidade;
                gravarCart.putString("Produto" + id, gson.toJson(p));
                valoresCarrinho.add(p);
                addCarrinho(valoresCarrinho);
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();

            }




        }


        //Ao clicar no botão de voltar, ele faz popBackStack na fragment
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });



        gravarCart.commit();

        //Define esta como selecionada na navbar
        MainActivity main = new MainActivity();
        main.setSelected(getActivity(),true);
        return view;


    }

    private void addCarrinho(@NonNull List<Produto> p) {

        for (Produto prod : p) {
            itens.add(new RecycleCarrinho(prod.nome, String.valueOf(prod.qntd), String.valueOf(prod.preco), String.valueOf(prod.qntd),
                    decodeBase64ToBitmap(prod.imagem),prod.id, tamanho != 0? String.valueOf(tamanho): null));

        }
        adapter = new CarrinhoAdapter(getContext(), itens, getParentFragmentManager()); //Define o adapter, e passa os itens
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler_carrinho.setAdapter(adapter);
        recycler_carrinho.setLayoutManager(layoutManager);
        recycler_carrinho.setItemAnimator(new DefaultItemAnimator());


    }

    //Método que transforma de Base64 para Bitmap
    public Bitmap decodeBase64ToBitmap(String base64Image) {
        //Pega a imagem em String, decodifica para Base64 e adiciona para um array de bytes
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        //Decodifica um objeto Bitmap com base em um array de bytes, que é o array acima
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}