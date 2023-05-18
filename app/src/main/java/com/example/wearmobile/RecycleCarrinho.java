package com.example.wearmobile;

import android.widget.Button;

public class RecycleCarrinho {

    private String titulo_carrinho;
    private String cifao_carrinho;
    private String preco_carrinho;
    private String quantidade;
    private int num_da_quant;
    private Button btnmais;
    private Button btnmenos;
    private Button finalizar_pedido;


    public  RecycleCarrinho(String titulo_carrinho){
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

    public Button getBtnmais() {
        return btnmais;
    }

    public void setBtnmais(Button btnmais) {
        this.btnmais = btnmais;
    }

    public Button getBtnmenos() {
        return btnmenos;
    }

    public void setBtnmenos(Button btnmenos) {
        this.btnmenos = btnmenos;
    }

    public Button getFinalizar_pedido() {
        return finalizar_pedido;
    }

    public void setFinalizar_pedido(Button finalizar_pedido) {
        this.finalizar_pedido = finalizar_pedido;
    }
}
