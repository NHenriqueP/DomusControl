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
}
