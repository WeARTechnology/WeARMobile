package com.example.wearmobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navbar; //Objecto da navbar
    FloatingActionButton tryONButton; //Botão central que leva ao TRYON
    private List<Produto> produtos = new ArrayList<>(); //Produtos que serão colocados no catálogo
    private ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o retorno do banco
    Gson gson = new Gson(); //Objeto de GSON pra transformar a lista de produtos em JSON mais pra frente


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Logo ao iniciar, define o fragmentHolder, como a HomeFragment, assim haverá informações na tela, que são as do fragment Home
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomeFragment()).commit();


        tryONButton = findViewById(R.id.floatingActionButton2); //Instancia o botão
        navbar = findViewById(R.id.bottomNavigationView); //Instancia a navbar
        cleanSelected(this); //Limpa o selecionado da navbar

        //Definição de itens para requisição
        String url = "https://weartechhost.azurewebsites.net/api/WebService/produtos"; //Define a URL a ser consultada
        RequestQueue requisicao = Volley.newRequestQueue(this); //Cria o objeto de requisição

        //Faz a requisição ao banco, através do método, e salva os valores no SharedPreferences
        pegarProdutos(url, requisicao);


        //Adiciona on OnClick do botão de TryON
        tryONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Redireciona para a Activity de Intent
                Intent tryOnIntent = new Intent(getApplicationContext(), TryOn.class);
                startActivity(tryOnIntent);

            }
        });

        //Adiciona o onClick dos itens da navbar
        navbar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //SwitchCase para definir qual item foi clicado
                switch (item.getItemId()) {
                    case R.id.carrinho: //Se foi o carrinho, troca o fragment pelo fragment do carrinho
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).addToBackStack(null).commit();
                        break;
                    case R.id.produtos: //Se foi o catálogo, troca o fragment pelo fragment do catálogo
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new CatalogoFragment()).addToBackStack(null).commit();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    //Método que pega os produtos do banco de dados, todos seus valores são nao nulos
    private void pegarProdutos(@NonNull String url, @NonNull RequestQueue requisicao) {
        //Cria o request para buscar produtos
        JsonArrayRequest buscaProdutos = new JsonArrayRequest(
                Request.Method.GET, //Define que será um GET
                url, //Define a URL que será chamada para a requisição
                null, //Não passa nenhum valor no body, pois é um get
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) { //Quando tiver alguma resposta
                        //Configura o leitor para não falhar quando achar algo que não conhece
                        leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        //Pega os valores retornados da resposta, e converte de JSON para List<Produto>, salvando em produtos
                        try {
                            produtos = leitor.readValue(String.valueOf(response), new TypeReference<List<Produto>>() {
                            });
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }


                        //Cria um gravador do SharedPreferences
                        SharedPreferences.Editor gravar = getSharedPreferences("Produtos", MODE_PRIVATE).edit();
                        //Coloca todos os produtos que foram encontrados, transformando a List produtos, em JSON
                        gravar.putString("Produtos", gson.toJson(produtos));

                        //Para cada produto que existe em produtos
                        for (Produto p: produtos){
                            //Define o SharedPreferences ProdutoX onde X é o id do produto, com os dados do produto desejado
                            gravar.putString("Produto" + p.id, gson.toJson(p));
                        }

                        gravar.commit();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { //Caso dê erro
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflar o menu do aplicativo a partir do recurso XML (menu_search) no objeto 'menu'
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Encontrar o item 'action_search' no objeto 'menu' e obter a 'SearchView' associada a ele
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        // Definir uma dica para os usuários sobre o que pesquisar
        searchView.setQueryHint("Clique aqui para pesquisar");

        // Criar um 'OnQueryTextListener' para a 'SearchView' para lidar com eventos de mudança de texto e envio de texto
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Neste exemplo, não estamos realizando nenhuma ação ao enviar o texto
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Neste exemplo, não estamos realizando nenhuma ação ao alterar o texto
                return false;
            }
        });

        // A chamada 'super.onCreateOptionsMenu(menu)' é feita para garantir que os itens do menu de opções padrão ainda sejam exibidos
        return super.onCreateOptionsMenu(menu);

    }


    //Método que "limpa" a seleção da navbar
    public void cleanSelected(Activity tela) {
        //Instancia o objeto da navbar
        navbar = tela.findViewById(R.id.bottomNavigationView);
        //Define o item selecionado como o de camera (item vazio que não está na tela)
        navbar.setSelectedItemId(R.id.camera);
    }
    //Método que define a seleção do navbar
    public void setSelected(Activity tela, boolean cart) {
        navbar = tela.findViewById(R.id.bottomNavigationView);
        int targetId = cart ? R.id.carrinho : R.id.produtos;

        if (navbar.getSelectedItemId() != targetId) {
            navbar.setSelectedItemId(targetId);
        }
    }


}
