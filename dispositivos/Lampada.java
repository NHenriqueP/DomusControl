package dispositivos;

public class Lampada extends Dispositivo {
    private int intensidade;
    private boolean suportaCor;
    private int temperaturaCor;

    public Lampada() {
        super();
        this.intensidade = 0;
        this.suportaCor = false;
        this.temperaturaCor = 2700;
    }

    public Lampada(String id, String marca, String modelo, double consumoPorHora, boolean estado,
                   int intensidade, boolean suportaCor, int temperaturaCor) {
        super(id, marca, modelo, consumoPorHora, estado);
        setIntensidade(intensidade);
        this.suportaCor = suportaCor;

        if (suportaCor) {
            setTemperaturaCor(temperaturaCor);
        } else {
            this.temperaturaCor = 2700;
        }
    }

    public Lampada(Lampada l1) {
        super(l1);
        this.intensidade = l1.getIntensidade();
        this.suportaCor = l1.getSuportaCor();
        this.temperaturaCor = l1.getTemperaturaCor();
    }

    public int getIntensidade() {
        return this.intensidade;
    }

    public boolean getSuportaCor() {
        return this.suportaCor;
    }

    public int getTemperaturaCor() {
        return this.temperaturaCor;
    }

    public void setIntensidade(int intensidade) {
        if (intensidade < 0 || intensidade > 100) {
            throw new IllegalArgumentException("Intensidade invalida. Use um valor entre 0 e 100.");
        }

        this.intensidade = intensidade;
    }

    public void setSuportaCor(boolean suportaCor) {
        this.suportaCor = suportaCor;

        if (!suportaCor) {
            this.temperaturaCor = 2700;
        }
    }

    public void setTemperaturaCor(int temperaturaCor) {
        if (!this.suportaCor) {
            throw new IllegalStateException("Esta lampada nao suporta alteracao de cor.");
        }

        if (temperaturaCor < 2700 || temperaturaCor > 4000) {
            throw new IllegalArgumentException("Temperatura de cor invalida. Use um valor entre 2700K e 4000K.");
        }

        this.temperaturaCor = temperaturaCor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", Intensidade= ").append(this.intensidade);
        sb.append(", Suporta Cor= ").append(this.suportaCor);
        sb.append(", Temperatura Cor= ").append(this.temperaturaCor).append("K");
        return sb.toString().replace("]", "") + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        
        if (o == null || this.getClass() != o.getClass()) return false;
        
        Lampada lampada = (Lampada) o;
        
        return this.intensidade == lampada.getIntensidade() &&
               this.suportaCor == lampada.getSuportaCor() &&
               this.temperaturaCor == lampada.getTemperaturaCor();
    }

    @Override
    public Dispositivo clone() {
        return new Lampada(this);
    }
}
