package sugestoes;
import java.io.Serializable;
import java.util.Objects;
public class Sugestao implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idCasa;
    private String idDispositivo;
    private String nomeDivisao;
    private String tipo;
    private String descricao;
    private int ativacoes;
    private double horasUso;
    public Sugestao() {
        this.idCasa = "";
        this.idDispositivo = "";
        this.nomeDivisao = "";
        this.tipo = "";
        this.descricao = "";
        this.ativacoes = 0;
        this.horasUso = 0.0;
    }
    public Sugestao(String idCasa, String idDispositivo, String nomeDivisao, String tipo, String descricao, int ativacoes, double horasUso) {
        this.idCasa = idCasa;
        this.idDispositivo = idDispositivo;
        this.nomeDivisao = nomeDivisao;
        this.tipo = tipo;
        this.descricao = descricao;
        this.ativacoes = ativacoes;
        this.horasUso = horasUso;
    }
    public Sugestao(Sugestao s) {
        this.idCasa = s.idCasa;
        this.idDispositivo = s.idDispositivo;
        this.nomeDivisao = s.nomeDivisao;
        this.tipo = s.tipo;
        this.descricao = s.descricao;
        this.ativacoes = s.ativacoes;
        this.horasUso = s.horasUso;
    }
    public String getIdCasa() { return this.idCasa; }
    public String getIdDispositivo() { return this.idDispositivo; }
    public String getNomeDivisao() { return this.nomeDivisao; }
    public String getTipo() { return this.tipo; }
    public String getDescricao() { return this.descricao; }
    public int getAtivacoes() { return this.ativacoes; }
    public double getHorasUso() { return this.horasUso; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sugestao s = (Sugestao) o;
        return this.ativacoes == s.ativacoes &&
               Double.compare(this.horasUso, s.horasUso) == 0 &&
               Objects.equals(this.idCasa, s.idCasa) &&
               Objects.equals(this.idDispositivo, s.idDispositivo) &&
               Objects.equals(this.nomeDivisao, s.nomeDivisao) &&
               Objects.equals(this.tipo, s.tipo) &&
               Objects.equals(this.descricao, s.descricao);
    }
    @Override
    public int hashCode() {
        return Objects.hash(idCasa, idDispositivo, nomeDivisao, tipo, descricao, ativacoes, horasUso);
    }
    @Override
    public Sugestao clone() {
        return new Sugestao(this);
    }
    @Override
    public String toString() {
        return String.format("[%s] Casa %s | Dispositivo %s (%s) | %d ativacoes, %.1fh uso | %s",
            tipo, idCasa.substring(0, Math.min(8, idCasa.length())),
            idDispositivo, nomeDivisao, ativacoes, horasUso, descricao);
    }
}
