package dispositivos;
import java.io.Serializable;
import java.util.Objects;
public abstract class Dispositivo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String marca;
    private String modelo;
    private double consumoPorHora;
    private boolean estado;
    public Dispositivo(String id, String marca, String modelo, double consumoPorHora, boolean estado) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.consumoPorHora = consumoPorHora;
        this.estado = estado;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public void setConsumoPorHora(double consumoPorHora) {
        this.consumoPorHora = consumoPorHora;
    }
    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    public String getId() {
        return this.id;
    }
    public String getMarca() {
        return this.marca;
    }
    public String getModelo() {
        return this.modelo;
    }
    public double getConsumoPorHora() {
        return this.consumoPorHora;
    }
    public boolean getEstado() {
        return this.estado;
    }
    public Dispositivo() {
        this.id = "";
        this.marca = "";
        this.modelo = "";
        this.consumoPorHora = 0.0;
        this.estado = false;
    }
    public Dispositivo(Dispositivo d1) {
        this.id = d1.getId();
        this.marca = d1.getMarca();
        this.modelo = d1.getModelo();
        this.consumoPorHora = d1.getConsumoPorHora();
        this.estado = d1.getEstado();
    }
    public void ligarDispositivo() {
        this.estado = true;
    }
    public void desligarDispositivo() {
        this.estado = false;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dispositivo [ID= ").append(this.id);
        sb.append(", Marca= ").append(this.marca);
        sb.append(", Modelo= ").append(this.modelo);
        sb.append(", Consumo Por Hora= ").append(this.consumoPorHora).append(" Wh");
        sb.append(", Estado= ").append(this.estado ? "Ligado" : "Desligado");
        sb.append("]");
        return sb.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Dispositivo d = (Dispositivo) o;
        return this.estado == d.getEstado() && 
               Double.compare(d.getConsumoPorHora(), this.consumoPorHora) == 0 &&
               Objects.equals(this.id, d.getId()) && 
               Objects.equals(this.marca, d.getMarca()) && 
               Objects.equals(this.modelo, d.getModelo());
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, marca, modelo, consumoPorHora, estado);
    }
    public abstract Dispositivo clone();
}
