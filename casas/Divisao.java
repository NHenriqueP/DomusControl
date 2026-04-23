package casas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe que representa uma Divisão genérica dentro de uma Casa.
 */
public class Divisao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private List<String> idDispositivos; // Apenas guardamos os IDs por segurança

    /**
     * Construtor por omissão.
     */
    public Divisao() {
        this.nome = "";
        this.idDispositivos = new ArrayList<>();
    }

    /**
     * Construtor parametrizado com nome.
     */
    public Divisao(String nome) {
        this.nome = nome;
        this.idDispositivos = new ArrayList<>();
    }

    /**
     * Construtor parametrizado completo.
     */
    public Divisao(String nome, List<String> idDispositivos) {
        this.nome = nome;
        setIdDispositivos(idDispositivos); 
    }

    /**
     * Construtor de cópia.
     */
    public Divisao(Divisao d) {
        this.nome = d.getNome();
        this.idDispositivos = d.getIdDispositivos();
    }

    // --- GETTERS e SETTERS ---

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getIdDispositivos() {
        // Devolvemos uma cópia da lista para manter o encapsulamento
        return new ArrayList<>(this.idDispositivos);
    }

    public void setIdDispositivos(List<String> idDispositivos) {
        // Guardamos numa nova lista para evitar partilha de referências
        this.idDispositivos = new ArrayList<>(idDispositivos);
    }

    // --- MÉTODOS DE NEGÓCIO ---

    public void addDispositivo(String id) {
        if (!this.idDispositivos.contains(id)) {
            this.idDispositivos.add(id);
        }
    }

    public void removeDispositivo(String id) {
        this.idDispositivos.remove(id);
    }

    public boolean existeDispositivo(String id) {
        return this.idDispositivos.contains(id);
    }

    // --- MÉTODOS USUAIS OBJECT ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Divisao divisao = (Divisao) o;
        return Objects.equals(nome, divisao.nome) &&
               Objects.equals(idDispositivos, divisao.idDispositivos);
    }

    @Override
    public Divisao clone() {
        return new Divisao(this);
    }

    @Override
    public String toString() {
        return "Divisao{" +
                "nome='" + nome + '\'' +
                ", numDispositivos=" + idDispositivos.size() +
                '}';
    }
}
