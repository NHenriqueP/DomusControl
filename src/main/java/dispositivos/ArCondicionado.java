package dispositivos;

import java.util.Objects;

public class ArCondicionado extends Dispositivo {
    private static final long serialVersionUID = 1L;

    private int velocidade;
    private String modo;
    private double temperatura;

    public ArCondicionado() {
        super();
        this.velocidade = 1;
        this.modo = "AUTO";
        this.temperatura = 24.0;
    }

    public ArCondicionado(String id, String marca, String modelo, double consumoPorHora, boolean estado,
                          int velocidade, String modo, double temperatura) {
        super(id, marca, modelo, consumoPorHora, estado);
        setVelocidade(velocidade);
        setModo(modo);
        setTemperatura(temperatura);
    }

    public ArCondicionado(ArCondicionado a) {
        super(a);
        this.velocidade = a.getVelocidade();
        this.modo = a.getModo();
        this.temperatura = a.getTemperatura();
    }

    public int getVelocidade() {
        return this.velocidade;
    }

    public void setVelocidade(int velocidade) {
        if (!isVelocidadeValida(velocidade)) {
            throw new IllegalArgumentException("Velocidade invalida. Use um valor entre 1 e 5.");
        }
        this.velocidade = velocidade;
    }

    public String getModo() {
        return this.modo;
    }

    public void setModo(String modo) {
        if (!isModoValido(modo)) {
            throw new IllegalArgumentException("Modo invalido. Use FRIO, QUENTE, VENTOINHA ou AUTO.");
        }
        this.modo = modo;
    }

    public double getTemperatura() {
        return this.temperatura;
    }

    public void setTemperatura(double temperatura) {
        if (!isTemperaturaValida(temperatura)) {
            throw new IllegalArgumentException("Temperatura invalida. Use um valor entre 16.0 e 30.0.");
        }
        this.temperatura = temperatura;
    }

    private boolean isVelocidadeValida(int velocidade) {
        return velocidade >= 1 && velocidade <= 5;
    }

    private boolean isModoValido(String modo) {
        return "FRIO".equals(modo) || "QUENTE".equals(modo) || "VENTOINHA".equals(modo) || "AUTO".equals(modo);
    }

    private boolean isTemperaturaValida(double temperatura) {
        return temperatura >= 16.0 && temperatura <= 30.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" | ArCondicionado [Velocidade= ").append(this.velocidade);
        sb.append(", Modo= ").append(this.modo);
        sb.append(", Temperatura= ").append(this.temperatura);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ArCondicionado a = (ArCondicionado) o;
        return this.velocidade == a.getVelocidade() &&
               Double.compare(a.getTemperatura(), this.temperatura) == 0 &&
               Objects.equals(this.modo, a.getModo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), velocidade, modo, temperatura);
    }

    @Override
    public ArCondicionado clone() {
        return new ArCondicionado(this);
    }
}
