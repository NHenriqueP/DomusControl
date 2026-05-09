package dispositivos;

import java.util.Objects;

public class SistemaDeRega extends Dispositivo {
    private static final long serialVersionUID = 1L;

    private int percentagemIrrigacao;
    private int duracao;
    private String zona;

    public SistemaDeRega() {
        super();
        this.percentagemIrrigacao = 0;
        this.duracao = 1;
        this.zona = "";
    }

    public SistemaDeRega(String id, String marca, String modelo, double consumoPorHora, boolean estado,
                         int percentagemIrrigacao, int duracao, String zona) {
        super(id, marca, modelo, consumoPorHora, estado);
        setPercentagemIrrigacao(percentagemIrrigacao);
        setDuracao(duracao);
        setZona(zona);
    }

    public SistemaDeRega(SistemaDeRega s) {
        super(s);
        this.percentagemIrrigacao = s.getPercentagemIrrigacao();
        this.duracao = s.getDuracao();
        this.zona = s.getZona();
    }

    public int getPercentagemIrrigacao() {
        return this.percentagemIrrigacao;
    }

    public void setPercentagemIrrigacao(int percentagemIrrigacao) {
        if (!isPercentagemValida(percentagemIrrigacao)) {
            throw new IllegalArgumentException("Percentagem de irrigacao invalida. Use um valor entre 0 e 100.");
        }
        this.percentagemIrrigacao = percentagemIrrigacao;
    }

    public int getDuracao() {
        return this.duracao;
    }

    public void setDuracao(int duracao) {
        if (!isDuracaoValida(duracao)) {
            throw new IllegalArgumentException("Duracao invalida. Use um valor maior que 0.");
        }
        this.duracao = duracao;
    }

    public String getZona() {
        return this.zona;
    }

    public void setZona(String zona) {
        if (zona == null) {
            throw new IllegalArgumentException("Zona nao pode ser null.");
        }
        this.zona = zona;
    }

    private boolean isPercentagemValida(int percentagemIrrigacao) {
        return percentagemIrrigacao >= 0 && percentagemIrrigacao <= 100;
    }

    private boolean isDuracaoValida(int duracao) {
        return duracao > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" | SistemaDeRega [Percentagem Irrigacao= ").append(this.percentagemIrrigacao);
        sb.append(", Duracao= ").append(this.duracao).append(" min");
        sb.append(", Zona= ").append(this.zona);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SistemaDeRega s = (SistemaDeRega) o;
        return this.percentagemIrrigacao == s.getPercentagemIrrigacao() &&
               this.duracao == s.getDuracao() &&
               Objects.equals(this.zona, s.getZona());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), percentagemIrrigacao, duracao, zona);
    }

    @Override
    public SistemaDeRega clone() {
        return new SistemaDeRega(this);
    }
}
