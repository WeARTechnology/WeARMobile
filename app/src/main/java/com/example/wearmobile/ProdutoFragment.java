package com.example.wearmobile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProdutoFragment extends Fragment {
    //Inicializando variaveis
    int id; //Id do produto desejado
    EditText edtQuantidade; //Quantidade que for inserida pelo usuário
    TextView txtNome, txtQntdEstoque, txtDesc, txtpreco; //Textos da tela
    ImageView imgProduto, btnVoltar, imgRecomendadoProduto1, imgRecomendadoProduto2, imgRecomendadoProduto3; //Imagens da tela
    Button btnComprar, btnTryON; //Botões da tela
    Spinner spinTamanhos; //Spinner com os tamanhos de aneis
    Produto p; //Objeto da classe Produto
    ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o retorno do SharedPreferences
    List<Produto> imagensRecomendadas = new ArrayList<>();
    int[] tamanhosProds;
    Boolean isClickableButton = false;
    Boolean isglass = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_produto, container, false);
        Bundle bundle = getArguments();
        id = bundle.getInt("ID");

        //Limpa a seleção da navbar
        MainActivity main = (MainActivity) getActivity();
        main.cleanSelected(getActivity());

        //Atribuindo objetos aos itens na tela
        edtQuantidade = view.findViewById(R.id.edtQuantidade);
        txtNome = view.findViewById(R.id.txtNomeProd);
        txtQntdEstoque = view.findViewById(R.id.txtQuantidadeEstoque);
        txtDesc = view.findViewById(R.id.txtDescProd);
        txtpreco = view.findViewById(R.id.txtPreco);
        imgProduto = view.findViewById(R.id.imgProdutoPag);
        btnVoltar = view.findViewById(R.id.btnVoltarProduto);
        imgRecomendadoProduto1 = view.findViewById(R.id.imgRecomendadoProduto1);
        imgRecomendadoProduto2 = view.findViewById(R.id.imgRecomendadoProduto2);
        imgRecomendadoProduto3 = view.findViewById(R.id.imgRecomendadoProduto3);
        btnComprar = view.findViewById(R.id.btnComprar);
        btnTryON = view.findViewById(R.id.btnTryON);
        spinTamanhos = view.findViewById(R.id.spinnerTamanhos);

        leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //Define que o Jackson não vai falhar se encontrar algo que não conhece
        //Ler os itens do SharedPreferences
        SharedPreferences ler = getContext().getSharedPreferences("Produtos", MODE_PRIVATE); //Cria o leitor
        SharedPreferences lerQntd = view.getContext().getSharedPreferences("ProdutoQntd", Context.MODE_PRIVATE); //Lê todos os produtos do banco
        int qntd = 0; //Define uma variável quantidade
        try {
            //Define o objeto de Produto, com os valores que retornarem do SharedPreferences, que são dados em formato JSON e convertidos pelo jackson com ObjectMapper
            p = leitor.readValue(ler.getString("Produto" + id, ""), Produto.class);
            qntd = lerQntd.getInt("Produto" + id, 0); //Pega a quantidade do SharedPreferences
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        //Definindo os textos com base nos valores pegos pelo SharedPreferences
        txtNome.setText(p.nome);
        txtDesc.setText(p.desc);
        txtQntdEstoque.append("  " + (p.qntd - qntd));
        txtpreco.append(" " + p.preco);
        imgProduto.setImageBitmap(decodeBase64ToBitmap(p.imagem));

        //Se tiver o atributo modelo 3d,adiciona o onClick, se não, deixa ele invisivel
        if (p.modelo3d != true) {
            btnTryON.setVisibility(View.INVISIBLE);
        } else {
            btnTryON.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentTryON = new Intent(getContext(), TryOn.class);
                    intentTryON.putExtra("ID", id);
                    intentTryON.putExtra("Glass",isglass);
                    startActivity(intentTryON);
                }
            });
        }

        //Pega o tamanho do produto do banco, se for um Anel, devolve um int[], caso contrário isso volta vazio
        pegarTamanhos(id, new Callback() {
            @Override
            public void onSucess() {
                p.tamanho = tamanhosProds[1]; //Define o tamanho do objeto como o primeiro valor, apenas para saber se é anel ou oculos

                if (tamanhosProds[1] != 0) { //Se o primeiro valor não for 0 (na API, qnd não há nada, define o valor como 0)
                    List<String> ringSizes = new ArrayList<>(); //Cria uma lista para os valores

                    //Para cada valor encontrado, se não for 0, adiciona ele na lista
                    for (int i : tamanhosProds) {
                        if (i != 0) {
                            ringSizes.add("" + i);
                        }
                    }

                    //Define o adapter usando os tamanhos
                    SpinnerTamanhosAdapter ringSizeAdapter = new SpinnerTamanhosAdapter(getContext(), ringSizes);
                    spinTamanhos.setAdapter(ringSizeAdapter);
                } else {
                    isglass = true;
                    //Se não tiver nada, é um oculos, então some com o objeto
                    spinTamanhos.setVisibility(View.INVISIBLE);

                }

                //Define os produtos similares que ficarão abaixo da tela, apenas se conseguir pegar o tamanho
                definirSimilares(new Callback() {
                    @Override
                    public void onSucess() {
                        imgRecomendadoProduto1.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(1).imagem));
                        imgRecomendadoProduto2.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(2).imagem));
                        imgRecomendadoProduto3.setImageBitmap(decodeBase64ToBitmap(imagensRecomendadas.get(3).imagem));
                        isClickableButton = true;
                    }

                });
            }
        });


            //Adicionando os Clicklisteners dos itens recomendados, que redireciona para uma nova página de produto
            imgRecomendadoProduto1.setOnClickListener(v -> {
                int idRecomendado = imagensRecomendadas.get(1).idSimilar;
                onClickRecomendado(v, idRecomendado);

            });
            imgRecomendadoProduto2.setOnClickListener(v -> {
                int idRecomendado = imagensRecomendadas.get(2).idSimilar;
                onClickRecomendado(v, idRecomendado);
            });
            imgRecomendadoProduto3.setOnClickListener(v -> {
                int idRecomendado = imagensRecomendadas.get(3).idSimilar;
                onClickRecomendado(v, idRecomendado);
            });


        //Adicionando Listener do botão voltar, que dá popBackStack, voltando pra fragment anterior
        btnVoltar.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); //Volta a fragment anterior
        });

        //Adiciona o OnClick de compra , que redireciona ao carrinho
        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickableButton) {
                    //Cria um Bundle
                    Bundle bundle = new Bundle();
                    //Passa o ID do produto
                    bundle.putInt("ID", id);
                    //Passa o tamanho do produto, se houver
                    if (spinTamanhos.getVisibility() != View.INVISIBLE) {
                        bundle.putString("tamanho", spinTamanhos.getSelectedItem().toString());
                    }
                    //Passa a quantidade que o usuário selecionou
                    if (!edtQuantidade.getText().toString().isEmpty()) {
                        int qntdComprar = Integer.parseInt(edtQuantidade.getText().toString());
                        int estoque = Integer.parseInt(txtQntdEstoque.getText().toString().replace("Quantidade em Estoque: ","").trim());
                        if( qntdComprar > estoque ){
                            Toast.makeText(main, "Valor maior do que disponivel em estoque", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            bundle.putString("qntd", edtQuantidade.getText().toString());
                        }
                    } else {
                        Toast.makeText(getContext(), "É necessário selecionar uma quantidade", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    CarrinhoFragment cart = new CarrinhoFragment();
                    cart.setArguments(bundle);

                    //Redireciona de tela
                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, cart).addToBackStack(null).commit();
                }
            }
        });

        //Listener do edittext que não permite que seu valor seja maior que a quantidade disponível em estoque
        edtQuantidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    return;
                }

                int inputValue = Integer.parseInt(s.toString());
                if (inputValue > p.qntd) {
                    //Define o texto como o máximo disponível
                    edtQuantidade.setText(String.valueOf(p.qntd));

                    //Leva o cursor de seleção para o final do input
                    edtQuantidade.setSelection(edtQuantidade.length());

                    Toast.makeText(getContext(), "Valor maior do que disponível em estoque", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }
    /*Inicio dos métodos da classe*/

    //Método de onClick das imagens de produto recomendado
    private void onClickRecomendado(View v, int idRecomend) {
        if(isClickableButton) {
            Bundle bundle = new Bundle();
            bundle.putInt("ID", idRecomend);

            ProdutoFragment produto = new ProdutoFragment();
            produto.setArguments(bundle);

            getParentFragmentManager().beginTransaction().replace(R.id.fragmentHolder, produto).addToBackStack(null).commit();
        }
    }


    //Interface de Callback para esperar a chamada da requisição na API
    private interface Callback {
        void onSucess();
    }

    //Método que pega os produtos similares aquele da página atual, na API
    private void definirSimilares(Callback callback) {
        RequestQueue requisicao = Volley.newRequestQueue(getContext());
        String url = "http://weartech.somee.com/api/WebService/similaresID"; //Define a URL a ser consultada

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


    //Método que pega todos os tamanhos do produto, se ele for um anel
    private void pegarTamanhos(int id, Callback callback) {
        RequestQueue requisicao = Volley.newRequestQueue(getContext());
        String url = "http://weartech.somee.com/api/WebService/aneisTamanho"; //Define a URL a ser consultada

        //Cria o request para buscar produtos
        JsonArrayRequest buscaProdutos = new JsonArrayRequest(
                Request.Method.GET, //Define que será um GET
                url + "?id=" + id, //Define a URL que será chamada para a requisição, passando o id junto
                null, //Define os valores a serem passados no body da requisição, no caso, nada
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try { //Passa os valores da resposta para o array de int
                            tamanhosProds = leitor.readValue(String.valueOf(response), new TypeReference<int[]>() {
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