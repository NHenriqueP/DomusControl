package escalonamentos;
import automacoes.AcaoAutomacao;
import casas.Casa;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
public class Escalonamento implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String nome;
    private String idCasa;
    private LocalDateTime instanteExecucao;
    private AcaoAutomacao acao;
    private boolean executado;
    public Escalonamento() {
        this.id = UUID.randomUUID().toString();
        this.nome = "";
        this.idCasa = "";
        this.instanteExecucao = LocalDateTime.now();
        this.acao = casa -> {};
        this.executado = false;
    }
    public Escalonamento(String nome, String idCasa, LocalDateTime instanteExecucao, AcaoAutomacao acao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.idCasa = idCasa;
        this.instanteExecucao = instanteExecucao;
        this.acao = acao;
        this.executado = false;
    }
    public Escalonamento(Escalonamento other) {
        this.id = other.id;
        this.nome = other.nome;
        this.idCasa = other.idCasa;
        this.instanteExecucao = other.instanteExecucao;
        this.acao = other.acao;
        this.executado = other.executado;
    }
    public String getId() {
        return this.id;
    }
    public String getNome() {
        return this.nome;
    }
    public String getIdCasa() {
        return this.idCasa;
    }
    public LocalDateTime getInstanteExecucao() {
        return this.instanteExecucao;
    }
    public boolean isExecutado() {
        return this.executado;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public boolean deveExecutar(LocalDateTime tempoAtual) {
        return !this.executado && !tempoAtual.isBefore(this.instanteExecucao);
    }
    public void executar(Casa casa) {
        this.acao.executar(casa);
        this.executado = true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Escalonamento that = (Escalonamento) o;
        return executado == that.executado &&
            Objects.equals(id, that.id) &&
            Objects.equals(nome, that.nome) &&
            Objects.equals(idCasa, that.idCasa) &&
            Objects.equals(instanteExecucao, that.instanteExecucao);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, nome, idCasa, instanteExecucao, executado);
    }
    @Override
    public Escalonamento clone() {
        return new Escalonamento(this);
    }
    @Override
    public String toString() {
        return "Escalonamento{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", idCasa='" + idCasa + '\'' +
                ", instante=" + instanteExecucao +
                ", executado=" + executado +
                '}';
    }
}
