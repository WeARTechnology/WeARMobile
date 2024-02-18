package com.example.wearmobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    List<Produto> produtos = new ArrayList<>(); //Produtos que serão colocados no catálogo
    ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o retorno do banco
    Gson gson = new Gson(); //Objeto de GSON pra transformar a lista de produtos em JSON mais pra frente
    Boolean needtoLoading = true;
    List<Produto> items = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Cria um leitor do SharedPreferences
        SharedPreferences ler = getSharedPreferences("Produtos", MODE_PRIVATE);

        //Instanciando botões
        tryONButton = findViewById(R.id.floatingActionButton2); //Instancia o botão
        navbar = findViewById(R.id.bottomNavigationView); //Instancia a navbar
        cleanSelected(this); //Limpa o selecionado da navbar

        //Definição de itens para requisição
        String url = "https://weartech.netlify.app/api/WebService/produtos"; //Define a URL a ser consultada
        RequestQueue requisicao = Volley.newRequestQueue(this); //Cria o objeto de requisição

        //Se o leitor do SharedPreferences tiver vazio
        if (ler.getAll().isEmpty()) {
            //Troca o que haveria na tela, por uma mensagem de carregamento
            ViewGroup layoutManager = findViewById(R.id.fragmentHolder);
            View messageCarregando = getLayoutInflater().inflate(R.layout.messagecarregando, layoutManager, false);
            layoutManager.addView(messageCarregando);
        } else {
            //Termina de carregar a tela
            finishScreenLoading();
            needtoLoading = false;
        }

        //Faz a requisição ao banco, através do método, e salva os valores no SharedPreferences
        pegarProdutos(url, requisicao, new Callback() {
            @Override
            public void onSucess() {
                //Termina de carregar a tela
                if (needtoLoading) {
                    finishScreenLoading();
                }
            }
        });


    }

    private void finishScreenLoading() {
        //Logo ao iniciar, define o fragmentHolder, como a HomeFragment, assim haverá informações na tela, que são as do fragment Home
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomeFragment()).addToBackStack(null).commit();


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

        //Pega os valores para serem usados na SearchView
        getItemsName();
    }

    private interface Callback {
        void onSucess();
    }

    //Método que pega os produtos do banco de dados, todos seus valores são nao nulos
    private void pegarProdutos(@NonNull String url, @NonNull RequestQueue requisicao, Callback callback) {
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
                        for (Produto p : produtos) {
                            //Define o SharedPreferences ProdutoX onde X é o id do produto, com os dados do produto desejado
                            gravar.putString("Produto" + p.id, gson.toJson(p));
                        }

                        gravar.apply();
                        callback.onSucess();
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
                //Quando o texto é modificado, filtra os produtos com base no texto escrito
                List<Produto> filteredProducts = filterItems(newText);

                //Atualiza o recycler com os produtos que vieram filtrados
                updateProductList(filteredProducts);
                return false;
            }

        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Apaga o que estava escrito no searchView
                searchView.setQuery("", false);
                //Deixa o recycler invisivel e volta a visibilidade da tela (FrameLayout)
                RecyclerView recycler = findViewById(R.id.recyclerViewSearch);
                recycler.setVisibility(View.INVISIBLE);
                FrameLayout frameLayout = findViewById(R.id.fragmentHolder);
                frameLayout.setVisibility(View.VISIBLE);
                return true;
            }
        });

        // A chamada 'super.onCreateOptionsMenu(menu)' é feita para garantir que os itens do menu de opções padrão ainda sejam exibidos
        return super.onCreateOptionsMenu(menu);

    }

    private void updateProductList(List<Produto> filteredProducts) {
        RecyclerView recycler = findViewById(R.id.recyclerViewSearch); //Recycler da tela que mostra o catálogo
        CatalogoAdapter adapter; //Adapter do catálogo
        ArrayList<RecycleCatalogo> itens = new ArrayList<>(); //Itens do catálogo

        //For each filtered product
        for (Produto product : filteredProducts) {
            //Add the product values to the items
            itens.add(new RecycleCatalogo(product.id, product.nome, "Quantidade: " + product.qntd,
                    "R$: " + product.preco, decodeBase64ToBitmap(product.imagem)));
        }

        //Termina de definir a Recycler, com Layout Manager, adapter, e etc..
        adapter = new CatalogoAdapter(this, itens, getSupportFragmentManager(), this); //Define o adapter, e passa os itens
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler.setItemAnimator(new DefaultItemAnimator());

        recycler.setVisibility(View.VISIBLE);
        FrameLayout frameLayout = findViewById(R.id.fragmentHolder);
        frameLayout.setVisibility(View.INVISIBLE);


    }


    private List<Produto> filterItems(String searchText) {
        List<Produto> filteredItems = new ArrayList<>();

        for (Produto item : items) {
            if (item.nome.toLowerCase().contains(searchText.toLowerCase())) {
                filteredItems.add(item);
            }
        }

        return filteredItems;
    }


    private void getItemsName() {
        SharedPreferences ler = getSharedPreferences("Produtos", MODE_PRIVATE);

        try {
            items = leitor.readValue(ler.getString("Produtos", ""), new TypeReference<List<Produto>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public void backtoHome(){
        //Troca da tela atual para a Home
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentHolder, new HomeFragment()).commit();
        cleanSelected(this);
    }


}
