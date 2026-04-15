package controlador;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import casas.Casa;
import casas.Utilizador;
import dispositivos.Dispositivo;

public class DomusControl {
    
    private Map<String, Utilizador> utilizadores;
    private Map<String, Casa> casas;
    private LocalDateTime tempoAtual;
    private Map<String, Integer> usosDispositivos; // quantas vezes cada dispositivo foi usado

    public DomusControl() {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.tempoAtual = LocalDateTime.now();
        this.usosDispositivos = new HashMap<>();
    }

    public DomusControl(LocalDateTime tempoInicial) {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.tempoAtual = tempoInicial;
        this.usosDispositivos = new HashMap<>();
    }

    public DomusControl(DomusControl dc) {
        this.utilizadores = new HashMap<>(dc.utilizadores);
        this.casas = new HashMap<>(dc.casas);
        this.tempoAtual = dc.tempoAtual;
        this.usosDispositivos = new HashMap<>(dc.usosDispositivos);
    }

    public LocalDateTime getTempoAtual() {
        return this.tempoAtual;
    }

    public void setTempoAtual(LocalDateTime novoTempo) {
        this.tempoAtual = novoTempo;
    }

    public void avancarDias(int dias) {
        this.tempoAtual = this.tempoAtual.plusDays(dias);
    }

    public void avancarHoras(int horas) {
        this.tempoAtual = this.tempoAtual.plusHours(horas);
    }

    public void adicionarCasa(String id, Casa casa) {
        this.casas.put(id, casa);
    }

    public Casa obterCasa(String id) {
        return this.casas.get(id);
    }

    public void removerCasa(String id) {
        this.casas.remove(id);
    }

    public Map<String, Casa> obterTodasCasas() {
        return new HashMap<>(this.casas);
    }

    public void adicionarUtilizador(String email, Utilizador utilizador) {
        this.utilizadores.put(email, utilizador);
    }

    public Utilizador obterUtilizador(String email) {
        return this.utilizadores.get(email);
    }

    public void removerUtilizador(String email) {
        this.utilizadores.remove(email);
    }

    public Map<String, Utilizador> obterTodosUtilizadores() {
        return new HashMap<>(this.utilizadores);
    }

    public boolean utilizadorExiste(String email) {
        return this.utilizadores.containsKey(email);
    }

    public void registarUsoDispositivo(String idDispositivo) {
        usosDispositivos.put(idDispositivo, usosDispositivos.getOrDefault(idDispositivo, 0) + 1);
    }

    public String casaQueMaisConsome() {
        if (casas.isEmpty()) {
            return "Nenhuma casa registada.";
        }
        
        String casaMaisConsome = null;
        double maiorConsumo = -1;
        
        for (Map.Entry<String, Casa> entry : casas.entrySet()) {
            double consumoCasa = calcularConsumoCasa(entry.getValue());
            if (consumoCasa > maiorConsumo) {
                maiorConsumo = consumoCasa;
                casaMaisConsome = entry.getKey();
            }
        }
        
        return casaMaisConsome != null ? 
            String.format("Casa: %s | Proprietário: %s | Consumo: %.2f Wh", 
                casaMaisConsome, 
                casas.get(casaMaisConsome).getNomeProprietario(),
                maiorConsumo) : 
            "Nenhuma casa com dispositivos.";
    }

    private double calcularConsumoCasa(Casa casa) {
        double consumoTotal = 0;
        for (Dispositivo d : casa.getDispositivos().values()) {
            if (d.getEstado()) {
                consumoTotal += d.getConsumoPorHora();
            }
        }
        return consumoTotal;
    }

    // Estatísticas: 3 dispositivos mais usados
    public String top3DispositivosMaisUsados() {
        //POR FAZER
        return "";
    }

    // Estatísticas: 3 divisões com mais dispositivos
    public String top3DivisoesComMaisDispositivos() {
        // POR FAER
        return "";
    }

    public String obterEstatisticas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Estatisticas do Sistema\n");
        sb.append("Tempo Atual: ").append(this.tempoAtual).append("\n");
        sb.append("Total de Casas: ").append(casas.size()).append("\n\n");
        sb.append("Casa que Mais Consome:\n").append(casaQueMaisConsome()).append("\n\n");
        sb.append(top3DispositivosMaisUsados()).append("\n");
        sb.append(top3DivisoesComMaisDispositivos()).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DomusControl [\n");
        sb.append("  Tempo Atual: ").append(this.tempoAtual).append("\n");
        sb.append("  Casas: ").append(this.casas.size()).append("\n");
        sb.append("  Utilizadores: ").append(this.utilizadores.size()).append("\n");
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomusControl that = (DomusControl) o;
        return this.tempoAtual.equals(that.tempoAtual) &&
               this.casas.equals(that.casas) &&
               this.utilizadores.equals(that.utilizadores);
    }

    @Override
    public DomusControl clone() {
        return new DomusControl(this);
    }
}