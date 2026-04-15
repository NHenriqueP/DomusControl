public class ColunaSom extends Dispositivo {
    private int volume;

    public ColunaSom(String id, String marca, String modelo, double consumoPorHora, 
                     boolean estado, int volume) {
        super(id, marca, modelo, consumoPorHora, estado);
        this.volume = volume;
    }

    public ColunaSom() {
        super();
        this.volume = 0;
    }

    public ColunaSom(ColunaSom coluna) {
        super(coluna);
        this.volume = coluna.getVolume();
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return this.volume;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", Volume= ").append(this.volume);
        return sb.toString().replace("]", "") + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        
        if (o == null || this.getClass() != o.getClass()) return false;
        
        ColunaSom coluna = (ColunaSom) o;
        
        return this.volume == coluna.getVolume();
    }

    @Override
    public Dispositivo clone() {
        return new ColunaSom(this);
    }
}

