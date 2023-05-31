package com.example.wearmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoViewHolder> {

    private Context context;
    private ArrayList<RecycleCarrinho> itens;  // Itens que serão adicionados ao recycler
    private FragmentManager fragmentManager; //Item que gerencia o fragment, para troca
    private ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o
    // retorno do SharedPreferences
    private Gson gson = new Gson(); //Objeto da classe GSON que trabalha com objetos em JSON
    private Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
    private Runnable updateQuantityRunnable;


    public CarrinhoAdapter(Context context, ArrayList<RecycleCarrinho> itens,
                           FragmentManager fragmentManager) {
        this.context = context;
        this.itens = itens;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CarrinhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_carrinho, parent,
                false); // Replace with the correct layout file name
        CarrinhoViewHolder viewHolder = new CarrinhoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarrinhoViewHolder holder, int position) {
        RecycleCarrinho item = itens.get(position);
        //Define os valores a serem escritos na tela
        holder.txtNome.setText(item.getNome());
        holder.txtPreco.setText("R$" + item.getPreco());
        holder.edtQntd.setText(item.getEdtQntd());
        holder.ivProduto.setImageBitmap(item.getImagem());

        if (item.getTxtTamanho() == null) { //Se não tiver tamanho, o txtTamanho fica invisivel
            holder.txtTamanho.setVisibility(View.GONE);
        } else {
            holder.txtTamanho.append(item.getTxtTamanho()); //Adiciono ao tamanho o que foi
            // passado pela classe
            leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //Define
            // que o Jackson não vai falhar se encontrar algo que não conhece

            SharedPreferences.Editor gravarCart = context.getSharedPreferences("CartItens",
                    Context.MODE_PRIVATE).edit(); //Gravar no Shared do Cart
            SharedPreferences ler = context.getSharedPreferences("CartItens",
                    Context.MODE_PRIVATE); //Ler o Shared do Cart

            Produto p = new Produto();
            try {
                p = leitor.readValue(ler.getString("Produto" + item.getProdId(), ""),
                        Produto.class); //Pega os valores do Shared
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            p.tamanho = Integer.parseInt(item.getTxtTamanho()); //Define o tamanho como o tamanho
            // passado pela classe
            gravarCart.putString("Produto" + p.id, gson.toJson(p)); //Salva esse novo produto no
            // Shared

            gravarCart.commit();

        }

        //Botão de remover produto
        holder.btnRemover.setOnClickListener(v -> {
            mostrarPopUPConfirmacao(item.getNome(), item.getEdtQntd(), item.getProdId(),
                    item.getTxtTamanho()); //Envia um popup confirmando a remoção
        });

        //EditText da quantidade
        holder.edtQntd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Remove o que estava sendo feito, se ele escrever denovo antes do delay
                if (updateQuantityRunnable != null) {
                    handler.removeCallbacks(updateQuantityRunnable);
                }

                // Cria um novo runnable que espera 2 segundos para fazer a requisição
                updateQuantityRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // Configura o leitor pra não falhar perante erros
                        leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                        //Define os Leitores e Gravadores do SharedPreferences
                        SharedPreferences.Editor gravarCart = context.getSharedPreferences(
                                "CartItens", Context.MODE_PRIVATE).edit();
                        SharedPreferences ler = context.getSharedPreferences("CartItens",
                                Context.MODE_PRIVATE);
                        SharedPreferences lerProd = context.getSharedPreferences("Produtos",
                                Context.MODE_PRIVATE); //Ler o Shared do Cart


                        if (item.getTxtTamanho() == null) { //Se não tiver tamanho
                            alterQuantity(ler, s, lerProd, gravarCart, item, holder, false);
                        } else {
                            alterQuantity(ler, s, lerProd, gravarCart, item, holder, true);
                        }
                    }
                };

                // Schedule the Runnable to run after a 2-second delay
                handler.postDelayed(updateQuantityRunnable, 500);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("ID", item.getProdId());

                ProdutoFragment produto = new ProdutoFragment();
                produto.setArguments(bundle);

                fragmentManager.beginTransaction().replace(R.id.fragmentHolder, produto).addToBackStack(null).commit();
            }
        });


    }

    //Método que mostra PopUP de confirmaçao na tela
    private void mostrarPopUPConfirmacao(String nomeProd, String qntd, int id,
                                         @Nullable String tamanho) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); //Cria um novo Alert no
        // contexto atual
        builder.setTitle("Confirmar Remoção"); //Define o título
        builder.setMessage("Tem certeza que deseja remover " + nomeProd + " do carrinho?");
        //Define a mensagem usando o nome do produto

        //Caso a resposta do alert for sim
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cria um para gravar Cart
                SharedPreferences.Editor gravarCart = context.getSharedPreferences("CartItens",
                        Context.MODE_PRIVATE).edit();

                if (tamanho == null) {
                    //Apaga do carrinho, o produto com o id clicado
                    gravarCart.remove("Produto" + id);
                } else {
                    //Apaga do carrinho, o produto com o id clicado
                    gravarCart.remove("Produto" + id + tamanho);
                }
                //Atualiza a página e envia o commit
                fragmentManager.beginTransaction().replace(R.id.fragmentHolder,
                        new CarrinhoFragment()).commit();
                gravarCart.commit();

            }
        });

        //Caso a resposta seja não, não faz nada
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //Envia o alert para a tela
        builder.show();

    }

    @Override
    public int getItemCount() {
        return itens.size();
    }


    public void alterQuantity(SharedPreferences ler, Editable s, SharedPreferences lerProd,
                              SharedPreferences.Editor gravarCart, RecycleCarrinho item,
                              CarrinhoViewHolder holder, boolean tamanho) {
        Produto p = new Produto();
        if (tamanho) {
            try {
                p = leitor.readValue(ler.getString("Produto" + item.getProdId() + item.getTxtTamanho(), ""), Produto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            try {
                p = leitor.readValue(ler.getString("Produto" + item.getProdId(), ""),
                        Produto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if (s.toString().isEmpty()) {
            return;
        } else {
            Produto prod = new Produto();
            try {
                prod = leitor.readValue(lerProd.getString("Produto" + p.id, ""), Produto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if (prod.qntd > Integer.parseInt(s.toString())) {
                p.qntd = Integer.parseInt(s.toString());
                gravarCart.putString("Produto" + p.id, gson.toJson(p));

                fragmentManager.beginTransaction().replace(R.id.fragmentHolder,
                        new CarrinhoFragment()).commit();
                gravarCart.commit();
            } else {
                Toast.makeText(context, "Valor maior do que disponível em estoque",
                        Toast.LENGTH_SHORT).show();
                holder.edtQntd.setText("" + (prod.qntd - 1));
                holder.edtQntd.setSelection(holder.edtQntd.length());
            }
        }
    }

}
