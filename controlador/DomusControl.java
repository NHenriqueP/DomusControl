package controlador;

import automacoes.AcaoAutomacao;
import automacoes.Automacao;
import casas.Casa;
import casas.Divisao;
import casas.TipoAcessoCasa;
import cenarios.Cenario;
import dispositivos.Dispositivo;
import escalonamentos.Escalonamento;
import utilizadores.Utilizador;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class DomusControl implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Utilizador> utilizadores;
    private Map<String, Casa> casas;
    private LocalDateTime tempoAtual;

    // Estatisticas de ativacoes por ID de dispositivo (global no sistema)
    private Map<String, Integer> usosDispositivos;

    // Estatisticas por casa+dispositivo
    private Map<String, Integer> ativacoesDispositivoCasa;
    private Map<String, Double> horasUsoDispositivoCasa;
    private Map<String, LocalDateTime> instanteLigacaoDispositivoCasa;
    private List<Escalonamento> escalonamentos;
    private Map<String, Cenario> cenarios;

    public DomusControl() {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.tempoAtual = LocalDateTime.now();
        this.usosDispositivos = new HashMap<>();
        this.ativacoesDispositivoCasa = new HashMap<>();
        this.horasUsoDispositivoCasa = new HashMap<>();
        this.instanteLigacaoDispositivoCasa = new HashMap<>();
        this.escalonamentos = new ArrayList<>();
        this.cenarios = new HashMap<>();
        inicializarCenariosBase();
    }

    public DomusControl(LocalDateTime tempoInicial) {
        this.utilizadores = new HashMap<>();
        this.casas = new HashMap<>();
        this.tempoAtual = tempoInicial;
        this.usosDispositivos = new HashMap<>();
        this.ativacoesDispositivoCasa = new HashMap<>();
        this.horasUsoDispositivoCasa = new HashMap<>();
        this.instanteLigacaoDispositivoCasa = new HashMap<>();
        this.escalonamentos = new ArrayList<>();
        this.cenarios = new HashMap<>();
        inicializarCenariosBase();
    }

    public DomusControl(DomusControl dc) {
        this.utilizadores = new HashMap<>();
        for (Map.Entry<String, Utilizador> entry : dc.utilizadores.entrySet()) {
            this.utilizadores.put(entry.getKey(), entry.getValue().clone());
        }

        this.casas = new HashMap<>();
        for (Map.Entry<String, Casa> entry : dc.casas.entrySet()) {
            this.casas.put(entry.getKey(), entry.getValue().clone());
        }

        this.tempoAtual = dc.tempoAtual;
        this.usosDispositivos = new HashMap<>(dc.usosDispositivos);
        this.ativacoesDispositivoCasa = new HashMap<>(dc.ativacoesDispositivoCasa);
        this.horasUsoDispositivoCasa = new HashMap<>(dc.horasUsoDispositivoCasa);
        this.instanteLigacaoDispositivoCasa = new HashMap<>(dc.instanteLigacaoDispositivoCasa);
        this.escalonamentos = new ArrayList<>();
        for (Escalonamento e : dc.escalonamentos) {
            this.escalonamentos.add(e.clone());
        }
        this.cenarios = new HashMap<>();
        for (Map.Entry<String, Cenario> entry : dc.cenarios.entrySet()) {
            this.cenarios.put(entry.getKey(), entry.getValue().clone());
        }
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
        this.casas.put(id, casa.clone());
    }

    public String criarCasa(String emailProprietario, int codigoPostal, String fornecedorEnergia) {
        Utilizador proprietario = obterUtilizadorOuErro(emailProprietario);
        Casa casa = new Casa(proprietario.getNome(), codigoPostal, fornecedorEnergia);
        casa.atribuirAcesso(proprietario, TipoAcessoCasa.ADMINISTRADOR);
        this.casas.put(casa.getIdCasa(), casa);
        return casa.getIdCasa();
    }

    public Casa obterCasa(String id) {
        Casa casa = this.casas.get(id);
        return casa == null ? null : casa.clone();
    }

    public void removerCasa(String id) {
        this.casas.remove(id);
        limparEstatisticasCasa(id);
    }

    public Map<String, Casa> obterTodasCasas() {
        Map<String, Casa> copia = new HashMap<>();
        for (Map.Entry<String, Casa> entry : this.casas.entrySet()) {
            copia.put(entry.getKey(), entry.getValue().clone());
        }
        return copia;
    }

    public void adicionarUtilizador(String email, Utilizador utilizador) {
        this.utilizadores.put(email, utilizador.clone());
    }

    public void criarUtilizador(String nome, String email) {
        if (utilizadorExiste(email)) {
            throw new IllegalArgumentException("Ja existe um utilizador com o email indicado.");
        }
        this.utilizadores.put(email, new Utilizador(nome, email));
    }

    public Utilizador obterUtilizador(String email) {
        Utilizador utilizador = this.utilizadores.get(email);
        return utilizador == null ? null : utilizador.clone();
    }

    public void removerUtilizador(String email) {
        this.utilizadores.remove(email);
    }

    public Map<String, Utilizador> obterTodosUtilizadores() {
        Map<String, Utilizador> copia = new HashMap<>();
        for (Map.Entry<String, Utilizador> entry : this.utilizadores.entrySet()) {
            copia.put(entry.getKey(), entry.getValue().clone());
        }
        return copia;
    }

    public boolean utilizadorExiste(String email) {
        return this.utilizadores.containsKey(email);
    }

    public void atribuirAcessoCasa(String idCasa, String emailUtilizador, TipoAcessoCasa tipoAcesso) {
        Casa casa = obterCasaOuErro(idCasa);
        Utilizador utilizador = obterUtilizadorOuErro(emailUtilizador);
        casa.atribuirAcesso(utilizador, tipoAcesso);
    }

    public void adicionarDivisaoACasa(String idCasa, String emailUtilizador, String nomeDivisao) {
        Casa casa = obterCasaOuErro(idCasa);
        Utilizador utilizador = obterUtilizadorOuErro(emailUtilizador);
        casa.addDivisao(utilizador, nomeDivisao);
    }

    public void adicionarDispositivoACasa(String idCasa, String emailUtilizador, Dispositivo dispositivo,
                                          String nomeDivisao) {
        Casa casa = obterCasaOuErro(idCasa);
        Utilizador utilizador = obterUtilizadorOuErro(emailUtilizador);

        limparEstatisticasDispositivoCasa(idCasa, dispositivo.getId());
        casa.addDispositivo(utilizador, dispositivo.clone(), nomeDivisao);

        if (dispositivo.getEstado()) {
            String chave = chaveCasaDispositivo(idCasa, dispositivo.getId());
            this.instanteLigacaoDispositivoCasa.put(chave, this.tempoAtual);
        }
    }

    public void ligarDispositivo(String idCasa, String emailUtilizador, String idDispositivo) {
        Casa casa = obterCasaOuErro(idCasa);
        Utilizador utilizador = obterUtilizadorOuErro(emailUtilizador);

        Boolean estadoAntes = estadoDispositivoCasa(casa, idDispositivo);
        casa.ligarDispositivo(utilizador, idDispositivo);
        Boolean estadoDepois = estadoDispositivoCasa(casa, idDispositivo);

        if (Boolean.FALSE.equals(estadoAntes) && Boolean.TRUE.equals(estadoDepois)) {
            registarAtivacao(idCasa, idDispositivo);
        }
    }

    public void desligarDispositivo(String idCasa, String emailUtilizador, String idDispositivo) {
        Casa casa = obterCasaOuErro(idCasa);
        Utilizador utilizador = obterUtilizadorOuErro(emailUtilizador);

        Boolean estadoAntes = estadoDispositivoCasa(casa, idDispositivo);
        casa.desligarDispositivo(utilizador, idDispositivo);
        Boolean estadoDepois = estadoDispositivoCasa(casa, idDispositivo);

        if (Boolean.TRUE.equals(estadoAntes) && Boolean.FALSE.equals(estadoDepois)) {
            registarDesligamento(idCasa, idDispositivo);
        }
    }

    public void ligarDispositivosDivisao(String idCasa, String nomeDivisao) {
        Casa casa = obterCasaOuErro(idCasa);
        Map<String, Boolean> estadoAntes = estadosDispositivosDaDivisao(casa, nomeDivisao);

        casa.ligarDispositivosDivisao(nomeDivisao);

        for (String idDispositivo : estadoAntes.keySet()) {
            Boolean depois = estadoDispositivoCasa(casa, idDispositivo);
            if (Boolean.FALSE.equals(estadoAntes.get(idDispositivo)) && Boolean.TRUE.equals(depois)) {
                registarAtivacao(idCasa, idDispositivo);
            }
        }
    }

    public void desligarDispositivosDivisao(String idCasa, String nomeDivisao) {
        Casa casa = obterCasaOuErro(idCasa);
        Map<String, Boolean> estadoAntes = estadosDispositivosDaDivisao(casa, nomeDivisao);

        casa.desligarDispositivosDivisao(nomeDivisao);

        for (String idDispositivo : estadoAntes.keySet()) {
            Boolean depois = estadoDispositivoCasa(casa, idDispositivo);
            if (Boolean.TRUE.equals(estadoAntes.get(idDispositivo)) && Boolean.FALSE.equals(depois)) {
                registarDesligamento(idCasa, idDispositivo);
            }
        }
    }

    public void operarSobreDispositivo(String idCasa, String emailUtilizador, String idDispositivo,
                                        Consumer<Dispositivo> operacao) {
        Casa casa = obterCasaOuErro(idCasa);
        Utilizador utilizador = obterUtilizadorOuErro(emailUtilizador);
        casa.operarSobreDispositivo(utilizador, idDispositivo, operacao);
    }

    public Dispositivo obterDispositivoCasa(String idCasa, String idDispositivo) {
        Casa casa = obterCasaOuErro(idCasa);
        Dispositivo d = casa.getDispositivos().get(idDispositivo);
        return d;
    }

    public void associarAutomacaoCasa(String idCasa, String emailUtilizador, Automacao automacao) {
        Casa casa = obterCasaOuErro(idCasa);
        Utilizador utilizador = obterUtilizadorOuErro(emailUtilizador);
        casa.associarAutomacao(utilizador, automacao);
    }

    public void executarAutomacoesCasa(String idCasa) {
        Casa casa = obterCasaOuErro(idCasa);
        casa.executarAutomacoes();
    }

    public String criarEscalonamento(String nome, String idCasa, LocalDateTime instanteExecucao, AcaoAutomacao acao) {
        obterCasaOuErro(idCasa);
        Escalonamento escalonamento = new Escalonamento(nome, idCasa, instanteExecucao, acao);
        this.escalonamentos.add(escalonamento);
        return escalonamento.getId();
    }

    public int executarEscalonamentosPendentes() {
        int executados = 0;

        for (Escalonamento escalonamento : this.escalonamentos) {
            if (escalonamento.deveExecutar(this.tempoAtual)) {
                Casa casa = this.casas.get(escalonamento.getIdCasa());
                if (casa != null) {
                    escalonamento.executar(casa);
                    executados++;
                }
            }
        }

        return executados;
    }

    public List<Escalonamento> obterEscalonamentos() {
        List<Escalonamento> copia = new ArrayList<>();
        for (Escalonamento e : this.escalonamentos) {
            copia.add(e.clone());
        }
        return copia;
    }

    public String listarEscalonamentos() {
        if (this.escalonamentos.isEmpty()) {
            return "Sem escalonamentos definidos.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Escalonamentos:\n");
        for (Escalonamento e : this.escalonamentos) {
            sb.append("- ")
                .append(e.getNome())
                .append(" | Casa ")
                .append(e.getIdCasa())
                .append(" | Instante ")
                .append(e.getInstanteExecucao())
                .append(" | Executado: ")
                .append(e.isExecutado())
                .append("\n");
        }
        return sb.toString();
    }

    // ============== Cenários ==============

    public void criarCenario(Cenario cenario) {
        this.cenarios.put(cenario.getId(), cenario.clone());
    }

    public void removerCenario(String idCenario) {
        this.cenarios.remove(idCenario);
    }

    public Map<String, Cenario> obterCenarios() {
        Map<String, Cenario> copia = new HashMap<>();
        for (Map.Entry<String, Cenario> entry : this.cenarios.entrySet()) {
            copia.put(entry.getKey(), entry.getValue().clone());
        }
        return copia;
    }

    public void aplicarCenario(String idCenario, String idCasa) {
        Cenario cenario = this.cenarios.get(idCenario);
        if (cenario == null) {
            throw new IllegalArgumentException("Cenario inexistente: " + idCenario);
        }
        Casa casa = obterCasaOuErro(idCasa);
        cenario.executar(casa);
    }

    // Mantém retrocompatibilidade com os 4 cenários exigidos pelo enunciado
    public void aplicarCenarioSairDeCasa(String idCasa) {
        Casa casa = obterCasaOuErro(idCasa);
        for (String divisao : casa.getDivisoes().keySet()) {
            desligarDispositivosDivisao(idCasa, divisao);
        }
    }

    public void aplicarCenarioJantarComAmigos(String idCasa) {
        Casa casa = obterCasaOuErro(idCasa);
        for (String divisao : casa.getDivisoes().keySet()) {
            desligarDispositivosDivisao(idCasa, divisao);
        }
        ligarDivisaoSeExistir(casa, idCasa, "Sala");
        ligarDivisaoSeExistir(casa, idCasa, "Cozinha");
    }

    public void aplicarCenarioDeitar(String idCasa) {
        Casa casa = obterCasaOuErro(idCasa);
        for (String divisao : casa.getDivisoes().keySet()) {
            desligarDispositivosDivisao(idCasa, divisao);
        }
        ligarDivisaoSeExistir(casa, idCasa, "Quarto");
    }

    public void aplicarCenarioAcordar(String idCasa) {
        Casa casa = obterCasaOuErro(idCasa);
        ligarDivisaoSeExistir(casa, idCasa, "Quarto");
        ligarDivisaoSeExistir(casa, idCasa, "Cozinha");
    }

    private void inicializarCenariosBase() {
        Cenario sairDeCasa = new Cenario("Sair de Casa", "Desliga todos os dispositivos");
        sairDeCasa.adicionarAcao(casa -> casa.desligarDispositivosDivisao("Sala"));
        sairDeCasa.adicionarAcao(casa -> casa.desligarDispositivosDivisao("Quarto"));
        sairDeCasa.adicionarAcao(casa -> casa.desligarDispositivosDivisao("Cozinha"));
        this.cenarios.put(sairDeCasa.getId(), sairDeCasa);

        Cenario jantar = new Cenario("Jantar com Amigos", "Liga Sala e Cozinha, desliga o resto");
        jantar.adicionarAcao(casa -> casa.desligarDispositivosDivisao("Quarto"));
        jantar.adicionarAcao(casa -> casa.ligarDispositivosDivisao("Sala"));
        jantar.adicionarAcao(casa -> casa.ligarDispositivosDivisao("Cozinha"));
        this.cenarios.put(jantar.getId(), jantar);

        Cenario deitar = new Cenario("Deitar", "Desliga tudo e liga o Quarto");
        deitar.adicionarAcao(casa -> casa.desligarDispositivosDivisao("Sala"));
        deitar.adicionarAcao(casa -> casa.desligarDispositivosDivisao("Cozinha"));
        deitar.adicionarAcao(casa -> casa.ligarDispositivosDivisao("Quarto"));
        this.cenarios.put(deitar.getId(), deitar);

        Cenario acordar = new Cenario("Acordar", "Liga Quarto e Cozinha");
        acordar.adicionarAcao(casa -> casa.ligarDispositivosDivisao("Quarto"));
        acordar.adicionarAcao(casa -> casa.ligarDispositivosDivisao("Cozinha"));
        this.cenarios.put(acordar.getId(), acordar);
    }

    public void registarUsoDispositivo(String idDispositivo) {
        this.usosDispositivos.put(idDispositivo, this.usosDispositivos.getOrDefault(idDispositivo, 0) + 1);
    }

    public String casaQueMaisConsome() {
        if (casas.isEmpty()) {
            return "Nenhuma casa registada.";
        }

        String casaMaisConsome = null;
        double maiorConsumo = -1;

        for (Map.Entry<String, Casa> entry : casas.entrySet()) {
            double consumoCasa = calcularConsumoCasaAtual(entry.getValue());
            if (consumoCasa > maiorConsumo) {
                maiorConsumo = consumoCasa;
                casaMaisConsome = entry.getKey();
            }
        }

        return casaMaisConsome != null ?
            String.format("Casa: %s | Proprietario: %s | Consumo Atual: %.2f Wh",
                casaMaisConsome,
                casas.get(casaMaisConsome).getNomeProprietario(),
                maiorConsumo) :
            "Nenhuma casa com dispositivos.";
    }

    public String top3DispositivosMaisUsadosCasa(String idCasa) {
        Casa casa = obterCasaOuErro(idCasa);
        Map<String, Dispositivo> dispositivosCasa = casa.getDispositivos();

        if (dispositivosCasa.isEmpty()) {
            return "Casa sem dispositivos.";
        }

        List<String> ids = new ArrayList<>(dispositivosCasa.keySet());

        List<String> porTempo = new ArrayList<>(ids);
        porTempo.sort((a, b) -> Double.compare(
            obterHorasUsoDispositivoCasa(idCasa, b),
            obterHorasUsoDispositivoCasa(idCasa, a)
        ));

        List<String> porAtivacoes = new ArrayList<>(ids);
        porAtivacoes.sort((a, b) -> Integer.compare(
            obterAtivacoesDispositivoCasa(idCasa, b),
            obterAtivacoesDispositivoCasa(idCasa, a)
        ));

        StringBuilder sb = new StringBuilder();
        sb.append("Top 3 dispositivos da casa ").append(idCasa).append("\n");
        sb.append("Por tempo de uso:\n");
        appendTopDispositivos(sb, porTempo, idCasa, true);
        sb.append("Por numero de ativacoes:\n");
        appendTopDispositivos(sb, porAtivacoes, idCasa, false);
        return sb.toString();
    }

    // Estatisticas: 3 dispositivos mais usados no sistema (por ativacoes)
    public String top3DispositivosMaisUsados() {
        if (this.usosDispositivos.isEmpty()) {
            return "Top 3 dispositivos mais usados: sem dados.";
        }

        List<Map.Entry<String, Integer>> lista = new ArrayList<>(this.usosDispositivos.entrySet());
        lista.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        StringBuilder sb = new StringBuilder();
        sb.append("Top 3 dispositivos mais usados (global):\n");
        for (int i = 0; i < Math.min(3, lista.size()); i++) {
            Map.Entry<String, Integer> entry = lista.get(i);
            sb.append(i + 1)
                .append(". Dispositivo ")
                .append(entry.getKey())
                .append(" -> ")
                .append(entry.getValue())
                .append(" ativacoes\n");
        }
        return sb.toString();
    }

    // Estatisticas: 3 divisoes (com casa) com mais dispositivos
    public String top3DivisoesComMaisDispositivos() {
        List<DivisaoRanking> ranking = new ArrayList<>();

        for (Map.Entry<String, Casa> entryCasa : this.casas.entrySet()) {
            String idCasa = entryCasa.getKey();
            Map<String, Divisao> divisoes = entryCasa.getValue().getDivisoes();

            for (Map.Entry<String, Divisao> entryDivisao : divisoes.entrySet()) {
                ranking.add(new DivisaoRanking(
                    idCasa,
                    entryDivisao.getKey(),
                    entryDivisao.getValue().getIdDispositivos().size()
                ));
            }
        }

        if (ranking.isEmpty()) {
            return "Top 3 divisoes com mais dispositivos: sem dados.";
        }

        ranking.sort((a, b) -> Integer.compare(b.totalDispositivos, a.totalDispositivos));

        StringBuilder sb = new StringBuilder();
        sb.append("Top 3 divisoes com mais dispositivos:\n");
        for (int i = 0; i < Math.min(3, ranking.size()); i++) {
            DivisaoRanking item = ranking.get(i);
            sb.append(i + 1)
                .append(". Casa ")
                .append(item.idCasa)
                .append(" | Divisao ")
                .append(item.nomeDivisao)
                .append(" -> ")
                .append(item.totalDispositivos)
                .append(" dispositivos\n");
        }
        return sb.toString();
    }

    // Metodos de consulta adicionais
    public double consumoAtualCasa(String idCasa) {
        Casa casa = obterCasaOuErro(idCasa);
        return calcularConsumoCasaAtual(casa);
    }

    public int totalDispositivosLigadosCasa(String idCasa) {
        Casa casa = obterCasaOuErro(idCasa);
        int total = 0;
        for (Dispositivo d : casa.getDispositivos().values()) {
            if (d.getEstado()) {
                total++;
            }
        }
        return total;
    }

    public int obterAtivacoesDispositivoCasa(String idCasa, String idDispositivo) {
        return this.ativacoesDispositivoCasa.getOrDefault(chaveCasaDispositivo(idCasa, idDispositivo), 0);
    }

    public double obterHorasUsoDispositivoCasa(String idCasa, String idDispositivo) {
        String chave = chaveCasaDispositivo(idCasa, idDispositivo);
        double acumulado = this.horasUsoDispositivoCasa.getOrDefault(chave, 0.0);

        LocalDateTime inicioLigacao = this.instanteLigacaoDispositivoCasa.get(chave);
        if (inicioLigacao != null) {
            acumulado += horasEntre(inicioLigacao, this.tempoAtual);
        }

        return acumulado;
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

    public void guardarEstadoBinario(String caminhoFicheiro) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(caminhoFicheiro))) {
            out.writeObject(this);
        }
    }

    public static DomusControl lerEstadoBinario(String caminhoFicheiro) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(caminhoFicheiro))) {
            Object obj = in.readObject();
            if (!(obj instanceof DomusControl)) {
                throw new InvalidObjectException("Ficheiro binario nao contem um DomusControl valido.");
            }
            return (DomusControl) obj;
        }
    }

    private double calcularConsumoCasaAtual(Casa casa) {
        double consumoTotal = 0;
        for (Dispositivo d : casa.getDispositivos().values()) {
            if (d.getEstado()) {
                consumoTotal += d.getConsumoPorHora();
            }
        }
        return consumoTotal;
    }

    private void appendTopDispositivos(StringBuilder sb, List<String> ordenados, String idCasa, boolean porTempo) {
        int limite = Math.min(3, ordenados.size());
        for (int i = 0; i < limite; i++) {
            String idDispositivo = ordenados.get(i);
            sb.append(i + 1)
                .append(". ")
                .append(idDispositivo)
                .append(" -> ");

            if (porTempo) {
                sb.append(String.format("%.2f horas", obterHorasUsoDispositivoCasa(idCasa, idDispositivo)));
            } else {
                sb.append(obterAtivacoesDispositivoCasa(idCasa, idDispositivo)).append(" ativacoes");
            }
            sb.append("\n");
        }
    }

    private Map<String, Boolean> estadosDispositivosDaDivisao(Casa casa, String nomeDivisao) {
        Map<String, Boolean> estados = new HashMap<>();
        Divisao divisao = casa.getDivisoes().get(nomeDivisao);

        if (divisao == null) {
            return estados;
        }

        for (String idDispositivo : divisao.getIdDispositivos()) {
            Boolean estado = estadoDispositivoCasa(casa, idDispositivo);
            if (estado != null) {
                estados.put(idDispositivo, estado);
            }
        }

        return estados;
    }

    private Boolean estadoDispositivoCasa(Casa casa, String idDispositivo) {
        Dispositivo d = casa.getDispositivos().get(idDispositivo);
        return d == null ? null : d.getEstado();
    }

    private void registarAtivacao(String idCasa, String idDispositivo) {
        String chave = chaveCasaDispositivo(idCasa, idDispositivo);

        registarUsoDispositivo(idDispositivo);
        this.ativacoesDispositivoCasa.put(chave, this.ativacoesDispositivoCasa.getOrDefault(chave, 0) + 1);
        this.instanteLigacaoDispositivoCasa.put(chave, this.tempoAtual);
    }

    private void registarDesligamento(String idCasa, String idDispositivo) {
        String chave = chaveCasaDispositivo(idCasa, idDispositivo);
        LocalDateTime inicioLigacao = this.instanteLigacaoDispositivoCasa.get(chave);

        if (inicioLigacao != null) {
            double horas = horasEntre(inicioLigacao, this.tempoAtual);
            this.horasUsoDispositivoCasa.put(chave, this.horasUsoDispositivoCasa.getOrDefault(chave, 0.0) + horas);
            this.instanteLigacaoDispositivoCasa.remove(chave);
        }
    }

    private String chaveCasaDispositivo(String idCasa, String idDispositivo) {
        return idCasa + "|" + idDispositivo;
    }

    private double horasEntre(LocalDateTime inicio, LocalDateTime fim) {
        long minutos = Duration.between(inicio, fim).toMinutes();
        return Math.max(0, minutos / 60.0);
    }

    private void limparEstatisticasCasa(String idCasa) {
        String prefixo = idCasa + "|";

        this.ativacoesDispositivoCasa.keySet().removeIf(chave -> chave.startsWith(prefixo));
        this.horasUsoDispositivoCasa.keySet().removeIf(chave -> chave.startsWith(prefixo));
        this.instanteLigacaoDispositivoCasa.keySet().removeIf(chave -> chave.startsWith(prefixo));
    }

    private void limparEstatisticasDispositivoCasa(String idCasa, String idDispositivo) {
        String chave = chaveCasaDispositivo(idCasa, idDispositivo);
        this.ativacoesDispositivoCasa.remove(chave);
        this.horasUsoDispositivoCasa.remove(chave);
        this.instanteLigacaoDispositivoCasa.remove(chave);
    }

    private void ligarDivisaoSeExistir(Casa casa, String idCasa, String nomeDivisao) {
        if (casa.getDivisoes().containsKey(nomeDivisao)) {
            ligarDispositivosDivisao(idCasa, nomeDivisao);
        }
    }

    private Casa obterCasaOuErro(String idCasa) {
        Casa casa = this.casas.get(idCasa);
        if (casa == null) {
            throw new IllegalArgumentException("Casa inexistente: " + idCasa);
        }
        return casa;
    }

    private Utilizador obterUtilizadorOuErro(String emailUtilizador) {
        Utilizador utilizador = this.utilizadores.get(emailUtilizador);
        if (utilizador == null) {
            throw new IllegalArgumentException("Utilizador inexistente: " + emailUtilizador);
        }
        return utilizador;
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
               this.utilizadores.equals(that.utilizadores) &&
               this.usosDispositivos.equals(that.usosDispositivos) &&
               this.ativacoesDispositivoCasa.equals(that.ativacoesDispositivoCasa) &&
               this.horasUsoDispositivoCasa.equals(that.horasUsoDispositivoCasa) &&
               this.instanteLigacaoDispositivoCasa.equals(that.instanteLigacaoDispositivoCasa) &&
               this.escalonamentos.equals(that.escalonamentos) &&
               this.cenarios.equals(that.cenarios);
    }

    @Override
    public DomusControl clone() {
        return new DomusControl(this);
    }

    private static class DivisaoRanking {
        private final String idCasa;
        private final String nomeDivisao;
        private final int totalDispositivos;

        private DivisaoRanking(String idCasa, String nomeDivisao, int totalDispositivos) {
            this.idCasa = idCasa;
            this.nomeDivisao = nomeDivisao;
            this.totalDispositivos = totalDispositivos;
        }
    }
}
