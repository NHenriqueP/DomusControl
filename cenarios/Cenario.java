package cenarios;
import automacoes.AcaoAutomacao;
import casas.Casa;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
public class Cenario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nome;
    private String descricao;
    private List<AcaoAutomacao> acoes;
    public Cenario() {
        this.id = UUID.randomUUID().toString();
        this.nome = "";
        this.descricao = "";
        this.acoes = new ArrayList<>();
    }
    public Cenario(String nome, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.acoes = new ArrayList<>();
    }
    public Cenario(String nome, String descricao, List<AcaoAutomacao> acoes) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.acoes = new ArrayList<>(acoes);
    }
    public Cenario(Cenario c) {
        this.id = c.id;
        this.nome = c.nome;
        this.descricao = c.descricao;
        this.acoes = new ArrayList<>(c.acoes);
    }
    public String getId() {
        return this.id;
    }
    public String getNome() {
        return this.nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getDescricao() {
        return this.descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public List<AcaoAutomacao> getAcoes() {
        return new ArrayList<>(this.acoes);
    }
    public void setAcoes(List<AcaoAutomacao> acoes) {
        this.acoes = new ArrayList<>(acoes);
    }
    public void adicionarAcao(AcaoAutomacao acao) {
        this.acoes.add(acao);
    }
    public void executar(Casa casa) {
        for (AcaoAutomacao acao : this.acoes) {
            acao.executar(casa);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cenario cenario = (Cenario) o;
        return Objects.equals(id, cenario.id) &&
               Objects.equals(nome, cenario.nome) &&
               Objects.equals(descricao, cenario.descricao);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descricao);
    }
    @Override
    public Cenario clone() {
        return new Cenario(this);
    }
    @Override
    public String toString() {
        return "Cenario{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", acoes=" + acoes.size() +
                '}';
    }
}
