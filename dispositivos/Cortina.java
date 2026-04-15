public class Cortina extends Dispositivo {
    private int percentagemAbertura;

    public Cortina() {
        super();
        this.percentagemAbertura = 0;
    }

    public Cortina(String id, String marca, String modelo, double consumoPorHora, boolean estado,
                   int percentagemAbertura) {
        super(id, marca, modelo, consumoPorHora, estado);
        setPercentagemAbertura(percentagemAbertura);
    }

    public Cortina(Cortina c1) {
        super(c1);
        this.percentagemAbertura = c1.getPercentagemAbertura();
    }

    public int getPercentagemAbertura() {
        return this.percentagemAbertura;
    }

    public void setPercentagemAbertura(int percentagemAbertura) {
        if (percentagemAbertura < 0 || percentagemAbertura > 100) {
            throw new IllegalArgumentException("Percentagem invalida. Use um valor entre 0 e 100.");
        }

        this.percentagemAbertura = percentagemAbertura;
    }

    public void abrirCortina() {
        this.percentagemAbertura = 100;
    }

    public void fecharCortina() {
        this.percentagemAbertura = 0;
    }
}
