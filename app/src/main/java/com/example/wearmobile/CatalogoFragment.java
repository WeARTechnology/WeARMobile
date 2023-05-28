package com.example.wearmobile;

import static android.content.Context.MODE_PRIVATE;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class CatalogoFragment extends Fragment {

    //Criação de objetos
    private RecyclerView recycler; //Recycler da tela que mostra o catálogo
    private CatalogoAdapter adapter; //Adapter do catálogo
    private ArrayList<RecycleCatalogo> itens = new ArrayList<>(); //Itens do catálogo
    private ImageView voltar; //Botão de voltar
    private List<Produto> produtos = new ArrayList<>(); //Produtos que serão colocados no catálogo
    private ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o retorno do SharedPreferences
    Button sol, grau, anel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogo, container, false); //Pega o valor da view



        //Instanciando objetos da tela
        sol = view.findViewById(R.id.btnFiltroSol);
        grau = view.findViewById(R.id.btnFiltroGrau);
        anel = view.findViewById(R.id.btnFiltroAnel);

        //Configuração de objetos
        leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //Define que o Jackson não vai falhar se encontrar algo que não conhece
        recycler = view.findViewById(R.id.recyclerCatalogo); //Define a qual item na view o recycler se direciona
        voltar = view.findViewById(R.id.btnVoltarCatalogo); //Define qual o item na view que o botão de voltar se direciona

        //Ler os itens do SharedPreferences
        SharedPreferences ler = getContext().getSharedPreferences("Produtos", MODE_PRIVATE); //Cria o leitor
        try {
            //Define a Lista de produtos, com os valores que retornarem do SharedPreferences, que são dados em formato JSON e convertidos
            //pelo jackson com ObjectMapper
            produtos = leitor.readValue(ler.getString("Produtos", ""), new TypeReference<List<Produto>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        //Se, o produto lido do sharedPreferences, for 0, significa que ainda não foi salvo nada no SharedPreferences
        if (produtos.size() == 0) {
            //Troca da tela atual para a Home, e retorna uma mensagem dizendo que ainda está carregando os valores para o SharedPreferences
            getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomeFragment()).commit();

            //Cria um objeto da MainActivity
            MainActivity mainActivity = new MainActivity();
            //Roda o método para limpar o item selecionado na navbar
            mainActivity.cleanSelected(getActivity());

            Toast.makeText(getContext(), "Espere um pouco, ainda estamos carregando :)", Toast.LENGTH_SHORT).show();
        } else {
            //Método que adiciona os valores da lista com base nos produtos passados do SharedPreferences
            createRecycler(produtos, null);
        }

        //Define o onClick do botão voltar, que ao ser clicado, retorna para a home
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Troca da tela atual para a Home
                getParentFragmentManager().popBackStack();

                //Cria um objeto da MainActivity
                MainActivity mainActivity = new MainActivity();
                //Roda o método para limpar o item selecionado na navbar
                mainActivity.cleanSelected(getActivity());
            }
        });

        //Adicionando onClickListener dos botões de filtro
        sol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecycler(produtos, "Sol");
            }
        });
        grau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecycler(produtos, "Grau");
            }
        });
        anel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecycler(produtos, "Anel");
            }
        });

        //Define esta como selecionada na navbar
        MainActivity main = new MainActivity();
        main.setSelected(getActivity(), false);

        return view;
    }


    //Método que cria o recycler
    private void createRecycler(List<Produto> produtos, @Nullable String categoria) {
        if (categoria == null) {
            //Para cada produto existente
            for (int i = 0; i < produtos.size(); i++) {
                //Adiciona para os itens, os valores do produto
                itens.add(new RecycleCatalogo(produtos.get(i).id, produtos.get(i).nome, "Quantidade: " + produtos.get(i).qntd,
                        "R$: " + produtos.get(i).preco, decodeBase64ToBitmap(produtos.get(i).imagem)));
            }
        } else if (categoria.equals("Sol")) //Se a categoria for Óculos de sol
        {
            //Remove todos os valores que já existiam em itens
            for (int j = itens.size() - 1; j >= 0; j--) {
                itens.remove(j);
            }

            //Para cada produto existente,se o tipo dele for igual ao da categoria, adiciona aos itens
            for (int i = 0; i < produtos.size(); i++) {
                if (produtos.get(i).tipo.equals(categoria)) {
                    //Adiciona para os itens, os valores do produto
                    itens.add(new RecycleCatalogo(produtos.get(i).id, produtos.get(i).nome, "Quantidade: " + produtos.get(i).qntd,
                            "R$: " + produtos.get(i).preco, decodeBase64ToBitmap(produtos.get(i).imagem)));
                }
            }

        } else if (categoria.equals("Grau")) { //Se a categoria for Óculos de Grau

            //Remove todos os valores que já existiam em itens
            for (int j = itens.size() - 1; j >= 0; j--) {
                itens.remove(j);
            }

            //Para cada produto existente,se o tipo dele for igual ao da categoria, adiciona aos itens
            for (int i = 0; i < produtos.size(); i++) {
                if (produtos.get(i).tipo.equals(categoria)) {
                    //Adiciona para os itens, os valores do produto
                    itens.add(new RecycleCatalogo(produtos.get(i).id, produtos.get(i).nome, "Quantidade: " + produtos.get(i).qntd,
                            "R$: " + produtos.get(i).preco, decodeBase64ToBitmap(produtos.get(i).imagem)));
                }
            }


        } else if (categoria.equals("Anel")) { //Se for um anel

            //Remove todos os valores já existentes em itens
            for (int j = itens.size() - 1; j >= 0; j--) {
                itens.remove(j);
            }

            //Para cada produto, se o tamanho dele não for 0 (é um anel) adiciona a itens
            for (int i = 0; i < produtos.size(); i++) {
                if (produtos.get(i).tamanho != 0) {
                    //Adiciona para os itens, os valores do produto
                    itens.add(new RecycleCatalogo(produtos.get(i).id, produtos.get(i).nome, "Quantidade: " + produtos.get(i).qntd,
                            "R$: " + produtos.get(i).preco, decodeBase64ToBitmap(produtos.get(i).imagem)));
                }
            }

        }


        //Termina de definir a Recycler, com Layout Manager, adapter, e etc..
        adapter = new CatalogoAdapter(getContext(), itens, getParentFragmentManager()); //Define o adapter, e passa os itens
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());

    }

    //Método que transforma de Base64 para Bitmap
    public Bitmap decodeBase64ToBitmap(String base64Image) {
        //Pega a imagem em String, decodifica para Base64 e adiciona para um array de bytes
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        //Decodifica um objeto Bitmap com base em um array de bytes, que é o array acima
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


}