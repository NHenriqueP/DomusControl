package automacoes;
import casas.Casa;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
public class Automacao implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final CondicaoAutomacao CONDICAO_FALSA = new CondicaoAutomacao() {
        @Override
        public boolean verificar(Casa casa) {
            return false;
        }
    };
    private static final AcaoAutomacao ACAO_VAZIA = new AcaoAutomacao() {
        @Override
        public void executar(Casa casa) {
        }
    };
    private String id;
    private String nome;
    private String descricao;
    private boolean ativa;
    private CondicaoAutomacao condicao;
    private AcaoAutomacao acao;
    public Automacao() {
        this.id = UUID.randomUUID().toString();
        this.nome = "";
        this.descricao = "";
        this.ativa = true;
        this.condicao = CONDICAO_FALSA;
        this.acao = ACAO_VAZIA;
    }
    public Automacao(String nome, String descricao, CondicaoAutomacao condicao, AcaoAutomacao acao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.ativa = true;
        this.condicao = condicao;
        this.acao = acao;
    }
    public Automacao(Automacao a1) {
        this.id = a1.getId();
        this.nome = a1.getNome();
        this.descricao = a1.getDescricao();
        this.ativa = a1.isAtiva();
        this.condicao = a1.getCondicao();
        this.acao = a1.getAcao();
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
    public boolean isAtiva() {
        return this.ativa;
    }
    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
    public CondicaoAutomacao getCondicao() {
        return this.condicao;
    }
    public void setCondicao(CondicaoAutomacao condicao) {
        if (condicao == null) {
            throw new IllegalArgumentException("Condicao de automacao nao pode ser null.");
        }
        this.condicao = condicao;
    }
    public AcaoAutomacao getAcao() {
        return this.acao;
    }
    public void setAcao(AcaoAutomacao acao) {
        if (acao == null) {
            throw new IllegalArgumentException("Acao de automacao nao pode ser null.");
        }
        this.acao = acao;
    }
    public boolean deveExecutar(Casa casa) {
        return this.ativa && this.condicao.verificar(casa);
    }
    public void executar(Casa casa) {
        if (deveExecutar(casa)) {
            this.acao.executar(casa);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Automacao automacao = (Automacao) o;
        return ativa == automacao.ativa &&
                Objects.equals(id, automacao.id) &&
                Objects.equals(nome, automacao.nome) &&
                Objects.equals(descricao, automacao.descricao);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descricao, ativa);
    }
    @Override
    public Automacao clone() {
        return new Automacao(this);
    }
    @Override
    public String toString() {
        return "Automacao{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", ativa=" + ativa +
                '}';
    }
}
