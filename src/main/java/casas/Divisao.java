package casas;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class Divisao implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nome;
    private List<String> idDispositivos; 
    public Divisao() {
        this.nome = "";
        this.idDispositivos = new ArrayList<>();
    }
    public Divisao(String nome) {
        this.nome = nome;
        this.idDispositivos = new ArrayList<>();
    }
    public Divisao(String nome, List<String> idDispositivos) {
        this.nome = nome;
        setIdDispositivos(idDispositivos); 
    }
    public Divisao(Divisao d) {
        this.nome = d.getNome();
        this.idDispositivos = d.getIdDispositivos();
    }
    public String getNome() {
        return nome;
    }
    public List<String> getIdDispositivos() {
        return new ArrayList<>(this.idDispositivos);
    }
    public void setIdDispositivos(List<String> idDispositivos) {
        this.idDispositivos = new ArrayList<>(idDispositivos);
    }
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Divisao divisao = (Divisao) o;
        return Objects.equals(nome, divisao.nome) &&
               Objects.equals(idDispositivos, divisao.idDispositivos);
    }
    @Override
    public int hashCode() {
        return Objects.hash(nome, idDispositivos);
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
