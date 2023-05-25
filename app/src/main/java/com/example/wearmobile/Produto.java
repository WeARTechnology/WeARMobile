package com.example.wearmobile;

public class Produto {
    //Definição de atributos da classe
    //Valores do objeto de Produto que é retornado do banco, como nome, quantidade, etc..
    public String desc, nome, tipo;
    public Double preco;
    public int qntd;
    public int tamanho = 0;
    public boolean modelo3d;
    public int id;



    //Construtor vazio
    public Produto()
    {
    }

    //Construtor do Anel (Somente tamanho, sem tipo)
    public Produto(String desc, String nome, double preco, int qntd, int tamanho)
    {
        this.desc = desc;
        this.nome = nome;
        this.preco = preco;
        this.qntd = qntd;
        this.tamanho = tamanho;
    }

    //Construtor do Óculos (Somente tipo, sem tamanho)
    public Produto(String desc, String nome, String tipo, double preco, int qntd)
    {
        this.desc = desc;
        this.nome = nome;
        this.tipo = tipo;
        this.preco = preco;
        this.qntd = qntd;
    }
}
