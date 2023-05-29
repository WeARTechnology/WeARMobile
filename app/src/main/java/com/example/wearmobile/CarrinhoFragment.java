package com.example.wearmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
    private int id, qntd, tamanho; //Define os inteiros que pegam o valor do bundle
    private Gson gson = new Gson(); //Objeto de gson, para codificar e decodificar a classe
    private ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o retorno do SharedPreferences


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);
        //Pega os dados que vieram através da Fragment
        Bundle dados = getArguments();
        if (dados != null) { //Se não forem nulos, pega ID e QNTD, que vem para o Bundle independente do produto
            id = dados.getInt("ID");
            qntd = Integer.parseInt(dados.getString("qntd"));

            if (dados.getString("tamanho") != null) { //Tamanho só vem nos aneis, então é necessário verificar se não é nulo
                tamanho = Integer.parseInt(dados.getString("tamanho"));
            }
        }


        //Criando o leitor e gravador no SharedPreferences
        SharedPreferences lerCart = view.getContext().getSharedPreferences("CartItens", Context.MODE_PRIVATE); //Lê os valores que já estão no carrinho
        SharedPreferences lerProd = view.getContext().getSharedPreferences("Produtos", Context.MODE_PRIVATE); //Lê todos os produtos do banco
        SharedPreferences lerQntd = view.getContext().getSharedPreferences("ProdutoQntd", Context.MODE_PRIVATE); //Lê todos os produtos do banco
        SharedPreferences.Editor gravarCart = view.getContext().getSharedPreferences("CartItens", Context.MODE_PRIVATE).edit(); //Grava novos valores no carrinho
        SharedPreferences.Editor gravarQntd = view.getContext().getSharedPreferences("ProdutoQntd", Context.MODE_PRIVATE).edit(); //Grava novos valores no carrinho

        //Atribuindo os objetos aos itens da tela
        voltar = view.findViewById(R.id.btnVoltarProduto);
        layoutCarrinho = view.findViewById(R.id.layoutCarrinho);
        totalCompra = view.findViewById(R.id.txtValordaCompra);
        finalizarCompra = view.findViewById(R.id.btnFinalizarCompra);
        recycler_carrinho = view.findViewById(R.id.recycler_carrinho);

        //Define que o Jackson não vai falhar se encontrar algo que não conhece
        leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //Pega todos os valores ja existentes no carrinho com Map
        Map<String, String> allCartShared = (Map<String, String>) lerCart.getAll();

        //Se não tiver nada no lerCart, e não tiver ID, nem Quantidade
        if (allCartShared.isEmpty() && id == 0 && qntd == 0) {
            carrinhoVazio();

        } else if (!allCartShared.isEmpty() && id == 0 && qntd == 0) //Se tiver valores no shared, mas não tiver id nem quantidade
        {

            //Defino chave e valor, e pego os valores deles com base nos valores do Map allCartShared
            String valor = null;
            List<Produto> p = new ArrayList<>(); //Lista dos produtos que estão no carrinho

            for (Map.Entry<String, String> entry : allCartShared.entrySet()) { //Para cada chave e valor dentro do Map, pega esse valor e adiciona a lista
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
            for (Produto prod : p) {
                if(prod.id > 0) {
                    valorTotalCompra += (prod.qntd * prod.preco);
                }
            }

            //Adiciona o preço descoberto ao textview
            totalCompra.append(" R$" + valorTotalCompra + ".00");
            addCarrinho(p); //Adiciona o valor ao carrinho


        } else if (allCartShared.isEmpty() && id != 0 && qntd != 0) //Se tiver id e quantidade, mas não tiver valores no shared
        {
            //Cria um objeto de Produto, e um para pegar os valores do Shared
            Produto p = new Produto();
            Produto sharedProduct = new Produto();

            //Pego os valores no sharedPreferences
            try {
                sharedProduct = leitor.readValue(lerProd.getString("Produto" + id, ""), Produto.class); //Pega os valores com o leitor
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            p = sharedProduct;
            p.qntd = qntd; //Define a quantidade como a passada pelo Bundle

            if (tamanho != 0) //Se o Bundle tiver retornado um tamanho
            {
                p.tamanho = tamanho;
                gravarCart.putString("Produto" + id + p.tamanho, gson.toJson(p)); //Grava no carrinho o nome "ProdutoXX" com ID e tamanho
            } else //Se não tiver retornado, grava apenas o ID
            {
                gravarCart.putString("Produto" + id, gson.toJson(p));
            }

            //Cria uma lista de produtos para retonar, e adiciona a esta lista o produto criado
            List<Produto> prod = new ArrayList<>();
            prod.add(p);
            addCarrinho(prod);


        } else if (!allCartShared.isEmpty() && id != 0 && qntd != 0) { //Se tiver valor no carrinho, id e quantidade

            //Três objetos
            Produto p = new Produto(); //Produto que será retornado
            Produto sharedProduct = new Produto(); //Produto com todos os valores do Shared
            Produto valorCarinho = new Produto(); //Produto com os valores do carrinho
            List<Produto> valoresCarrinho = new ArrayList<>();

            try {
                //Defino valor, e pego o valor om base nos valores do Map allCartShared
                String valor = null;
                for (Map.Entry<String, String> entry : allCartShared.entrySet()) { //Para cada chave e valor dentro do Map, pega esse valor e adiciona a lista
                    valor = entry.getValue();
                    //Pego os valores no sharedPreferences, com base no que foi passado pelo Map
                    valoresCarrinho.add(leitor.readValue(valor, Produto.class)); //Pega os valores com o leitor

                }

                //Pego do produto atual
                sharedProduct = leitor.readValue(lerProd.getString("Produto" + id, ""), Produto.class); //Pega os valores com o leitor
                if (tamanho == 0) { //Se não tiver tamanho, a busca é normal
                    valorCarinho = leitor.readValue(lerCart.getString("Produto" + id, ""), Produto.class); //Pega os valores com o leitor
                } else { //Se tiver tamanho, busco com o tamanho junto, assim como foi definido no if (allCartShared.isEmpty() && id != 0 && qntd != 0)
                    for (int i = 0; i < valoresCarrinho.size(); i++) {
                        if(valoresCarrinho.get(i).tamanho == tamanho){
                            valorCarinho = leitor.readValue(lerCart.getString("Produto" + id + tamanho, ""), Produto.class); //Pega os valores com o leitor
                        }
                    }

                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


            //Define p como os valores do Shared
            p = sharedProduct;

            int quantidade = 0; //Cria uma quantidade = 0

            quantidade = qntd + valorCarinho.qntd; //A Quantidade = quantidade passada + o que já havia no carrinho

            if (quantidade > sharedProduct.qntd) { //Se ela for maior do que a disponível em estoque
                p.qntd = sharedProduct.qntd;
                Toast.makeText(getContext(), "Valor maior do que disponível em estoque", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();
            } else {
                p.qntd = quantidade; //Defino a quantidade
                if (tamanho != 0) { //Se tiver tamanho, salva-se produto+id+tamanho, caso contrário só produto+id
                    p.tamanho = tamanho;
                    gravarCart.putString("Produto" + p.id + p.tamanho, gson.toJson(p));
                } else {
                    gravarCart.putString("Produto" + p.id, gson.toJson(p));
                }

                //Adiciona o produto p a lista de valores no carrinho, e adiciona a tela
                valoresCarrinho.add(p);
                addCarrinho(valoresCarrinho);
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();


            }
        }


        //Ao clicar no botão de voltar, ele faz popBackStack na fragment
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomeFragment()).commit();
                //Cria um objeto da MainActivity
                MainActivity mainActivity = (MainActivity) getContext();
                //Roda o método para limpar o item selecionado na navbar
                mainActivity.cleanSelected(getActivity());
            }
        });


        finalizarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Produto p = new Produto();
                for (int i = (itens.size() - 1); i >=0;i--) {
                    p.id = itens.get(i).getProdId();
                    if(lerQntd.getInt("Produto"+p.id, 0) == 0)
                    {
                        p.qntd = Integer.parseInt(itens.get(i).getQuantidade());

                    }
                    else
                    {
                        p.qntd = lerQntd.getInt("Produto"+p.id,0);
                        p.qntd += Integer.parseInt(itens.get(i).getQuantidade());
                    }
                    gravarQntd.putInt("Produto"+p.id, p.qntd);
                    gravarQntd.apply();
                    if(itens.get(i).getTxtTamanho() == null) {
                        gravarCart.remove("Produto" + p.id);
                    }
                    else
                    {
                        gravarCart.remove("Produto" + p.id + itens.get(i).getTxtTamanho());
                    }

                    itens.remove(i);
                }

                gravarCart.apply();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();
            }
        });


        gravarCart.apply();
        gravarQntd.apply();

        //Define esta como selecionada na navbar
        MainActivity main = (MainActivity) getActivity();
        main.setSelected(main, true);
        return view;


    }

    private void carrinhoVazio() {
        //Remove todas as coisas da tela, e adiciona a mensagem dizendo que o carrinho está vazio
        layoutCarrinho.removeAllViews();
        View messageCarrinho = getLayoutInflater().inflate(R.layout.messagecarrinho, layoutCarrinho, false);
        layoutCarrinho.addView(messageCarrinho);
    }

    private void addCarrinho(@NonNull List<Produto> p) {

        for (Produto prod : p) {
            if(prod.id > 0) {
                itens.add(new RecycleCarrinho(prod.nome, String.valueOf(prod.qntd), String.valueOf(prod.preco), String.valueOf(prod.qntd),
                        decodeBase64ToBitmap(prod.imagem), prod.id, prod.tamanho != 0 ? String.valueOf(prod.tamanho) : null));
            }
        }
        if(itens.size() > 0) {
            adapter = new CarrinhoAdapter(getContext(), itens, getParentFragmentManager()); //Define o adapter, e passa os itens
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recycler_carrinho.setAdapter(adapter);
            recycler_carrinho.setLayoutManager(layoutManager);
            recycler_carrinho.setItemAnimator(new DefaultItemAnimator());
        }
        else
        {
            carrinhoVazio();
        }


    }

    //Método que transforma de Base64 para Bitmap
    public Bitmap decodeBase64ToBitmap(String base64Image) {
        //Pega a imagem em String, decodifica para Base64 e adiciona para um array de bytes
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        //Decodifica um objeto Bitmap com base em um array de bytes, que é o array acima
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}