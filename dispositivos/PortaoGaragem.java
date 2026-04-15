public class PortaoGaragem extends Dispositivo {
    private int percentagemAbertura;

    public PortaoGaragem() {
        super();
        this.percentagemAbertura = 0;
    }

    public PortaoGaragem(String id, String marca, String modelo, double consumoPorHora, boolean estado,
                         int percentagemAbertura) {
        super(id, marca, modelo, consumoPorHora, estado);
        setPercentagemAbertura(percentagemAbertura);
    }

    public PortaoGaragem(PortaoGaragem p1) {
        super(p1);
        this.percentagemAbertura = p1.getPercentagemAbertura();
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

    public void abrirPortao() {
        this.percentagemAbertura = 100;
    }

    public void fecharPortao() {
        this.percentagemAbertura = 0;
    }
}
