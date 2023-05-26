package com.example.wearmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CatalogoAdapter extends RecyclerView.Adapter<CatalogoViewHolder> {

    private Context context;
    private ArrayList<RecycleCatalogo> itens;

    public CatalogoAdapter(Context context, ArrayList<RecycleCatalogo> itens) {
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public CatalogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_catalogo, parent, false);
        CatalogoViewHolder viewHolder = new CatalogoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogoViewHolder holder, int position) {
        RecycleCatalogo recycleCatalogo = itens.get(position);
        holder.nome_do_produto.setText(recycleCatalogo.getNome_do_produto());
        holder.quantidade_do_produto.setText(recycleCatalogo.getQuantidade_do_produto());
        holder.preco.setText(recycleCatalogo.getPreco());
        holder.imgProduto.setImageBitmap(recycleCatalogo.getImgProduto());







    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
