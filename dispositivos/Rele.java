package dispositivos;
public class Rele extends Dispositivo {
    public Rele() {
        super();
    }
    public Rele(String id, String marca, String modelo, double consumoPorHora, boolean estado) {
        super(id, marca, modelo, consumoPorHora, estado);
    }
    public Rele(Rele r1) {
        super(r1);
    }
    @Override
    public String toString() {
        return super.toString();
    }
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o == null || this.getClass() != o.getClass()) return false;
        return true;
    }
    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode());
    }
    @Override
    public Dispositivo clone() {
        return new Rele(this);
    }
}
