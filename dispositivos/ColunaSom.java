package dispositivos;

public class ColunaSom extends Dispositivo {
    private int volume;

    public ColunaSom(String id, String marca, String modelo, double consumoPorHora, 
                     boolean estado, int volume) {
        super(id, marca, modelo, consumoPorHora, estado);
        setVolume(volume);
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
        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("Volume invalido. Use um valor entre 0 e 100.");
        }
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

