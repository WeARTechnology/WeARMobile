package com.example.wearmobile;

import android.widget.Button;

public class RecycleCarrinho {

    private String titulo_carrinho;
    private String cifao_carrinho;
    private String preco_carrinho;
    private String quantidade;
    private int num_da_quant;
    private int btnmais;
    private int btnmenos;
    private int finalizar_pedido;

    public RecycleCarrinho(String titulo_carrinho, String cifao_carrinho, String preco_carrinho, String quantidade, int num_da_quant, int btnmais, int btnmenos, int finalizar_pedido) {
        this.titulo_carrinho = titulo_carrinho;
        this.cifao_carrinho = cifao_carrinho;
        this.preco_carrinho = preco_carrinho;
        this.quantidade = quantidade;
        this.num_da_quant = num_da_quant;
        this.btnmais = btnmais;
        this.btnmenos = btnmenos;
        this.finalizar_pedido = finalizar_pedido;
    }

    public String getTitulo_carrinho() {
        return titulo_carrinho;
    }

    public void setTitulo_carrinho(String titulo_carrinho) {
        this.titulo_carrinho = titulo_carrinho;
    }

    public String getCifao_carrinho() {
        return cifao_carrinho;
    }

    public void setCifao_carrinho(String cifao_carrinho) {
        this.cifao_carrinho = cifao_carrinho;
    }

    public String getPreco_carrinho() {
        return preco_carrinho;
    }

    public void setPreco_carrinho(String preco_carrinho) {
        this.preco_carrinho = preco_carrinho;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public int getNum_da_quant() {
        return num_da_quant;
    }

    public void setNum_da_quant(int num_da_quant) {
        this.num_da_quant = num_da_quant;
    }

    public int getBtnmais() {
        return btnmais;
    }

    public void setBtnmais(int btnmais) {
        this.btnmais = btnmais;
    }

    public int getBtnmenos() {
        return btnmenos;
    }

    public void setBtnmenos(int btnmenos) {
        this.btnmenos = btnmenos;
    }

    public int getFinalizar_pedido() {
        return finalizar_pedido;
    }

    public void setFinalizar_pedido(int finalizar_pedido) {
        this.finalizar_pedido = finalizar_pedido;
    }
}
