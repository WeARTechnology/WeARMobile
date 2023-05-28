package com.example.wearmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoViewHolder> {

    private Context context;
    private ArrayList<RecycleCarrinho> itens;
    private FragmentManager fragmentManager;
    private ObjectMapper leitor = new ObjectMapper(); //Leitor de JSON do Jackson para ler o retorno do SharedPreferences
    private Gson gson = new Gson();


    public CarrinhoAdapter(Context context, ArrayList<RecycleCarrinho> itens, FragmentManager fragmentManager) {
        this.context = context;
        this.itens = itens;
        this.fragmentManager = fragmentManager;

    }

    @NonNull
    @Override
    public CarrinhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_carrinho, parent, false); // Replace with the correct layout file name
        CarrinhoViewHolder viewHolder = new CarrinhoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarrinhoViewHolder holder, int position) {
        RecycleCarrinho item = itens.get(position);
        holder.txtNome.setText(item.getNome());
        holder.txtPreco.setText("R$" +item.getPreco());
        holder.edtQntd.setText(item.getEdtQntd());
        holder.ivProduto.setImageBitmap(item.getImagem());
        if(item.getTxtTamanho() == null) {
            holder.txtTamanho.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.txtTamanho.append(item.getTxtTamanho());
            //Define que o Jackson não vai falhar se encontrar algo que não conhece
            leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SharedPreferences.Editor gravarCart = context.getSharedPreferences("CartItens", Context.MODE_PRIVATE).edit();
            SharedPreferences ler = context.getSharedPreferences("CartItens", Context.MODE_PRIVATE);
            Produto p = new Produto();
            try {
                p = leitor.readValue(ler.getString("Produto"+item.getProdId(),""),Produto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
                p.tamanho = Integer.parseInt(item.getTxtTamanho());
                gravarCart.putString("Produto" + p.id, gson.toJson(p));

                fragmentManager.beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();

                gravarCart.commit();

        }

        holder.btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationPopUP(item.getNome(),item.getEdtQntd(), item.getProdId());


            }
        });

        holder.edtQntd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Define que o Jackson não vai falhar se encontrar algo que não conhece
                leitor.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                SharedPreferences.Editor gravarCart = context.getSharedPreferences("CartItens", Context.MODE_PRIVATE).edit();
                SharedPreferences ler = context.getSharedPreferences("CartItens", Context.MODE_PRIVATE);
                Produto p = new Produto();
                try {
                    p = leitor.readValue(ler.getString("Produto"+item.getProdId(),""),Produto.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                if(s.toString().isEmpty()){
                    return;
                }else {
                    p.qntd = Integer.parseInt(s.toString());
                    gravarCart.putString("Produto" + p.id, gson.toJson(p));

                    fragmentManager.beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();

                    gravarCart.commit();
                }


            }
        });



    }

    private void showConfirmationPopUP(String nomeProd, String qntd, int id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirmar Remoção");
            builder.setMessage("Tem certeza que deseja remover " + nomeProd + " do carrinho?");

            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor gravarProduto = context.getSharedPreferences("ProdutoQntd" , Context.MODE_PRIVATE).edit();
                    SharedPreferences.Editor gravarCart = context.getSharedPreferences("CartItens", Context.MODE_PRIVATE).edit();
                    gravarProduto.putString("Produto"+id , qntd);
                    gravarCart.remove("Produto"+id);

                    fragmentManager.beginTransaction().replace(R.id.fragmentHolder, new CarrinhoFragment()).commit();

                    gravarCart.commit();
                    gravarProduto.commit();

                }
            });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.show();

    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
