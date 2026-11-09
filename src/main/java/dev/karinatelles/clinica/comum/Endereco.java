package dev.karinatelles.clinica.comum;

import jakarta.persistence.Embeddable;

@Embeddable
public class Endereco {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    protected Endereco() {
    }

    public Endereco(DadosEndereco dados) {
        this.logradouro = dados.logradouro();
        this.numero = dados.numero();
        this.complemento = dados.complemento();
        this.bairro = dados.bairro();
        this.cidade = dados.cidade();
        this.uf = dados.uf().toUpperCase();
        this.cep = dados.cep().replaceAll("\\D", "");
    }

    public DadosEndereco paraDados() {
        return new DadosEndereco(logradouro, numero, complemento, bairro, cidade, uf, cep);
    }
}
