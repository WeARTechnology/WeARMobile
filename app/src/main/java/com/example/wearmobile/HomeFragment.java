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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Definir a classe HomeFragment que estende a classe Fragment
public class HomeFragment extends Fragment {

    Produto p; //Objeto da classe Produto
    ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o retorno do SharedPreferences
    List<Produto> imagensRecomendadas = new ArrayList<>();
    ImageView ivProdPrincipal, ivProdAnel, ivProdOculos,imgRecomendadoProduto1, imgRecomendadoProduto2, imgRecomendadoProduto3; //Imagens da tela
    int id;


    // Sobrescrever o método onCreateView para inflar o layout fragment_home e retornar a view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar o layout fragment_home e retornar a view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ivProdAnel = view.findViewById(R.id.imgAnelHome);
        ivProdOculos = view.findViewById(R.id.imgOculosHome);
        ivProdPrincipal = view.findViewById(R.id.imgProdutoPag);
        imgRecomendadoProduto1 = view.findViewById(R.id.imgRecomendadoProduto1);
        imgRecomendadoProduto2 = view.findViewById(R.id.imgRecomendadoProduto2);
        imgRecomendadoProduto3 = view.findViewById(R.id.imgRecomendadoProduto3);


        ivProdOculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("ID", 5);

                ProdutoFragment produto = new ProdutoFragment();
                produto.setArguments(bundle);

                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, produto).addToBackStack(null).commit();
            }
        });

        ivProdAnel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("ID", 14);

                ProdutoFragment produto = new ProdutoFragment();
                produto.setArguments(bundle);

                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, produto).addToBackStack(null).commit();
            }
        });

        ivProdPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("ID", 9);

                ProdutoFragment produto = new ProdutoFragment();
                produto.setArguments(bundle);

                getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, produto).addToBackStack(null).commit();
            }
        });





        //Adicionando os Clicklisteners dos itens recomendados, que redireciona para uma nova página de produto
        imgRecomendadoProduto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idRecomendado = imagensRecomendadas.get(1).idSimilar;
                onClickRecomendado(v, idRecomendado);
            }
        });
        imgRecomendadoProduto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idRecomendado = imagensRecomendadas.get(2).idSimilar;
                onClickRecomendado(v, idRecomendado);
            }
        });
        imgRecomendadoProduto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idRecomendado = imagensRecomendadas.get(3).idSimilar;
                onClickRecomendado(v, idRecomendado);
            }
        });

        SharedPreferences lerImagens = getContext().getSharedPreferences("ImagensSimilaresHome", MODE_PRIVATE);
        //Define os produtos similares que ficarão abaixo da tela, apenas se conseguir pegar o tamanho
        if(lerImagens.getAll().size() > 0) {
            try {
                imagensRecomendadas = leitor.readValue(lerImagens.getString("Imagens", ""), new TypeReference<List<Produto>>() {});
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            imgRecomendadoProduto1.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(1).imagem));
            imgRecomendadoProduto2.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(2).imagem));
            imgRecomendadoProduto3.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(3).imagem));

        }
        else
        {
            //Ler os itens do SharedPreferences
            SharedPreferences ler = getContext().getSharedPreferences("Produtos", MODE_PRIVATE); //Cria o leitor
            int id = definirID(ler);
            try {
                //Define o objeto de Produto, com os valores que retornarem do SharedPreferences, que são dados em formato JSON e convertidos pelo jackson com ObjectMapper
                p = leitor.readValue(ler.getString("Produto" + id, ""), Produto.class);
            } catch (
                    JsonProcessingException e) {
                e.printStackTrace();
            }


            definirSimilares(new HomeFragment.Callback() {
                @Override
                public void onSucess() {
                    imgRecomendadoProduto1.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(1).imagem));
                    imgRecomendadoProduto2.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(2).imagem));
                    imgRecomendadoProduto3.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(3).imagem));
                    SharedPreferences.Editor gravarImagem = getContext().getSharedPreferences("ImagensSimilaresHome", MODE_PRIVATE).edit();
                    Gson gson = new Gson();
                    gravarImagem.putString("Imagens", gson.toJson(imagensRecomendadas));
                    gravarImagem.commit();
                }

            });
        }
        return view;


    }

    private int definirID(SharedPreferences ler) {
        Random rnd = new Random();
        return rnd.nextInt(ler.getAll().size() - 1);

    }

    //Método de onClick das imagens de produto recomendado
    private void onClickRecomendado(View v, int idRecomend) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID", idRecomend);

        ProdutoFragment produto = new ProdutoFragment();
        produto.setArguments(bundle);

        getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, produto).addToBackStack(null).commit();
    }


    //Interface de Callback para esperar a chamada da requisição na API
    private interface Callback {
        void onSucess();
    }

    //Método que pega os produtos similares aquele da página atual, na API
    private void definirSimilares(HomeFragment.Callback callback) {
        RequestQueue requisicao = Volley.newRequestQueue(getContext());
        String url = "https://weartech.somee.com/api/WebService/similaresID"; //Define a URL a ser consultada

        String tabela = p.tamanho == 0 ? "Oculos" : "Anel";
        ; //Descobre qual tabela deve ser chamada


        //Cria o request para buscar produtos
        JsonArrayRequest buscaProdutos = new JsonArrayRequest(
                Request.Method.GET, //Define que será um GET
                url + "?id=" + id + "&tabela=" + tabela, //Define a URL que será chamada para a requisição, enviando junto o id e a tabela
                null, //Define os valores a serem passados no body da requisição
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try { //Passa os valores da resposta para o similares id
                            imagensRecomendadas = leitor.readValue(String.valueOf(response), new TypeReference<List<Produto>>() {
                            });
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        callback.onSucess();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Erro ao pegar produtos recomendados", Toast.LENGTH_SHORT).show();

                    }
                }
        );


        requisicao.add(buscaProdutos); //Inicia a requisição com o Request criado

    }

    //Método que transforma de Base64 para Bitmap
    public Bitmap decodeBase64ToBitmap(String base64Image) {
        //Pega a imagem em String, decodifica para Base64 e adiciona para um array de bytes
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        //Decodifica um objeto Bitmap com base em um array de bytes, que é o array acima
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
