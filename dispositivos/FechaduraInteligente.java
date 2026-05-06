package dispositivos;

import java.util.Objects;

public class FechaduraInteligente extends Dispositivo {
    private static final long serialVersionUID = 1L;

    private boolean estadoTranca;
    private String codigoAcesso;

    public FechaduraInteligente() {
        super();
        this.estadoTranca = true;
        this.codigoAcesso = "0000";
    }

    public FechaduraInteligente(String id, String marca, String modelo, double consumoPorHora, boolean estado,
                                boolean estadoTranca, String codigoAcesso) {
        super(id, marca, modelo, consumoPorHora, estado);
        this.estadoTranca = estadoTranca;
        this.codigoAcesso = isCodigoValido(codigoAcesso) ? codigoAcesso : "0000";
    }

    public FechaduraInteligente(FechaduraInteligente f) {
        super(f);
        this.estadoTranca = f.getEstadoTranca();
        this.codigoAcesso = f.getCodigoAcesso();
    }

    public boolean getEstadoTranca() {
        return this.estadoTranca;
    }

    public void setEstadoTranca(boolean estadoTranca) {
        this.estadoTranca = estadoTranca;
    }

    public String getCodigoAcesso() {
        return this.codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        if (isCodigoValido(codigoAcesso)) {
            this.codigoAcesso = codigoAcesso;
        }
    }

    public boolean alterarCodigo(String codigoAtual, String novoCodigo) {
        if (Objects.equals(this.codigoAcesso, codigoAtual) && isCodigoValido(novoCodigo)) {
            this.codigoAcesso = novoCodigo;
            return true;
        }
        return false;
    }

    public void trancar() {
        this.estadoTranca = true;
    }

    public boolean destrancar(String pinIntroduzido) {
        if (Objects.equals(this.codigoAcesso, pinIntroduzido)) {
            this.estadoTranca = false;
            return true;
        }
        return false;
    }

    private boolean isCodigoValido(String codigoAcesso) {
        if (codigoAcesso == null || codigoAcesso.isEmpty()) {
            return false;
        }
        for (int i = 0; i < codigoAcesso.length(); i++) {
            if (!Character.isDigit(codigoAcesso.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" | FechaduraInteligente [Estado Tranca= ").append(this.estadoTranca ? "Trancada" : "Destrancada");
        sb.append(", Codigo Acesso= ").append(this.codigoAcesso);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FechaduraInteligente f = (FechaduraInteligente) o;
        return this.estadoTranca == f.getEstadoTranca() &&
               Objects.equals(this.codigoAcesso, f.getCodigoAcesso());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), estadoTranca, codigoAcesso);
    }

    @Override
    public FechaduraInteligente clone() {
        return new FechaduraInteligente(this);
    }
}
