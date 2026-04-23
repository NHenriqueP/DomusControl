package utilizadores;

import casas.TipoAcessoCasa;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utilizador implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String email;
    private Map<String, TipoAcessoCasa> acessosCasas;

    public Utilizador() {
        this.nome = "";
        this.email = "";
        this.acessosCasas = new HashMap<>();
    }

    public Utilizador(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.acessosCasas = new HashMap<>();
    }

    public Utilizador(Utilizador u1) {
        this.nome = u1.getNome();
        this.email = u1.getEmail();
        this.acessosCasas = u1.getAcessosCasas();
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, TipoAcessoCasa> getAcessosCasas() {
        return new HashMap<>(this.acessosCasas);
    }

    public void setAcessosCasas(Map<String, TipoAcessoCasa> acessosCasas) {
        this.acessosCasas = new HashMap<>(acessosCasas);
    }

    public void registarAcessoCasa(String idCasa, TipoAcessoCasa tipoAcesso) {
        this.acessosCasas.put(idCasa, tipoAcesso);
    }

    public void removerAcessoCasa(String idCasa) {
        this.acessosCasas.remove(idCasa);
    }

    public boolean temAcessoCasa(String idCasa) {
        return this.acessosCasas.containsKey(idCasa);
    }

    public boolean isAdministradorCasa(String idCasa) {
        return this.acessosCasas.get(idCasa) == TipoAcessoCasa.ADMINISTRADOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilizador that = (Utilizador) o;
        return Objects.equals(nome, that.nome)
                && Objects.equals(email, that.email)
                && Objects.equals(acessosCasas, that.acessosCasas);
    }

    @Override
    public Utilizador clone() {
        return new Utilizador(this);
    }

    @Override
    public String toString() {
        return "Utilizador{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", casasComAcesso=" + acessosCasas.size() +
                '}';
    }
}
