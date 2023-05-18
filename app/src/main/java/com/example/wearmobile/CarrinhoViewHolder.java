package com.example.wearmobile;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarrinhoViewHolder extends RecyclerView.ViewHolder{

    static TextView titulo_carrinho;
    static TextView cifao_carrinho;
    TextView preco_carrrinho;
    TextView quantidade;
    TextView num_da_quantidade;
    Button btnmais, btnmenos, finalizar_pedido;

    public CarrinhoViewHolder(@NonNull View itemView) {
        super(itemView);
        titulo_carrinho = itemView.findViewById(R.id.titulo_carrinho);
        cifao_carrinho = itemView.findViewById(R.id.cifao_carrinho);
        preco_carrrinho = itemView.findViewById(R.id.preco_carrinho);
        quantidade = itemView.findViewById(R.id.quantidade);
        num_da_quantidade = itemView.findViewById(R.id.num_da_quant);
        btnmais = itemView.findViewById(R.id.btnmais);
        btnmenos = itemView.findViewById(R.id.btnmenos);
        finalizar_pedido = itemView.findViewById(R.id.finalizar_pedido);

    }
}
