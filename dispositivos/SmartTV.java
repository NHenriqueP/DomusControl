package dispositivos;

import java.util.Objects;

public class SmartTV extends Dispositivo {
    private static final long serialVersionUID = 1L;

    private int volume;
    private int canalAtual;
    private String fonteEntrada;

    public SmartTV() {
        super();
        this.volume = 10;
        this.canalAtual = 1;
        this.fonteEntrada = "ANTENA";
    }

    public SmartTV(String id, String marca, String modelo, double consumoPorHora, boolean estado,
                   int volume, int canalAtual, String fonteEntrada) {
        super(id, marca, modelo, consumoPorHora, estado);
        this.volume = isVolumeValido(volume) ? volume : 10;
        this.canalAtual = isCanalValido(canalAtual) ? canalAtual : 1;
        this.fonteEntrada = isFonteValida(fonteEntrada) ? fonteEntrada : "ANTENA";
    }

    public SmartTV(SmartTV s) {
        super(s);
        this.volume = s.getVolume();
        this.canalAtual = s.getCanalAtual();
        this.fonteEntrada = s.getFonteEntrada();
    }

    public int getVolume() {
        return this.volume;
    }

    public void setVolume(int volume) {
        if (isVolumeValido(volume)) {
            this.volume = volume;
        }
    }

    public int getCanalAtual() {
        return this.canalAtual;
    }

    public void setCanalAtual(int canalAtual) {
        if (isCanalValido(canalAtual)) {
            this.canalAtual = canalAtual;
        }
    }

    public String getFonteEntrada() {
        return this.fonteEntrada;
    }

    public void setFonteEntrada(String fonteEntrada) {
        if (isFonteValida(fonteEntrada)) {
            this.fonteEntrada = fonteEntrada;
        }
    }

    private boolean isVolumeValido(int volume) {
        return volume >= 0 && volume <= 100;
    }

    private boolean isCanalValido(int canalAtual) {
        return canalAtual > 0;
    }

    private boolean isFonteValida(String fonteEntrada) {
        return "HDMI-1".equals(fonteEntrada) || "HDMI-2".equals(fonteEntrada) ||
               "ANTENA".equals(fonteEntrada) || "CABO".equals(fonteEntrada);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" | SmartTV [Volume= ").append(this.volume);
        sb.append(", Canal Atual= ").append(this.canalAtual);
        sb.append(", Fonte Entrada= ").append(this.fonteEntrada);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SmartTV s = (SmartTV) o;
        return this.volume == s.getVolume() &&
               this.canalAtual == s.getCanalAtual() &&
               Objects.equals(this.fonteEntrada, s.getFonteEntrada());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), volume, canalAtual, fonteEntrada);
    }

    @Override
    public SmartTV clone() {
        return new SmartTV(this);
    }
}
