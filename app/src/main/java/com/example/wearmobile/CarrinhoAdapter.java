package com.example.wearmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.camera.video.VideoRecordEvent;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoViewHolder> {


    private Context context;
    private ArrayList<RecycleCarrinho> itens;

    public CarrinhoAdapter(Context context, ArrayList<RecycleCarrinho> itens) {
        this.context = context;
        this.itens = itens;
    }


    @NonNull
    @Override
    public CarrinhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_carrinho, parent, false);
        CarrinhoViewHolder viewHolder = new CarrinhoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarrinhoViewHolder holder, int position) {

        RecycleCarrinho item = itens.get(position);

        RecycleCarrinho recycleCarrinho = itens.get(position);
        CarrinhoViewHolder.titulo_carrinho.setText(recycleCarrinho.getTitulo_carrinho());
        CarrinhoViewHolder.cifao_carrinho.setText(recycleCarrinho.getCifao_carrinho());
        CarrinhoViewHolder.preco_carrrinho.setText(recycleCarrinho.getPreco_carrinho());
        CarrinhoViewHolder.quantidade.setText(recycleCarrinho.getQuantidade());
        CarrinhoViewHolder.num_da_quantidade.setText(recycleCarrinho.getNum_da_quant());
        CarrinhoViewHolder.btnmais.setCompoundDrawablesWithIntrinsicBounds(item.getBtnmais(),0,0,0);
        CarrinhoViewHolder.btnmenos.setCompoundDrawablesWithIntrinsicBounds(item.getBtnmenos(), 0, 0, 0);
        CarrinhoViewHolder.finalizar_pedido.setCompoundDrawablesWithIntrinsicBounds(item.getFinalizar_pedido(), 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
