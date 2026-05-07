package UI;
import controlador.DomusControl;
import sugestoes.Sugestao;
import dispositivos.ColunaSom;
import dispositivos.Cortina;
import dispositivos.Dispositivo;
import dispositivos.Lampada;
import dispositivos.PortaoGaragem;
import dispositivos.Rele;
import casas.Casa;
import cenarios.Cenario;
import escalonamentos.Escalonamento;
import automacoes.Automacao;
import utilizadores.Utilizador;
import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class TextUI {
    private DomusControl model;
    private Scanner sc;
    private static final String FICHEIRO_ESTADO = "estado_domus.bin";
    public TextUI() {
        this.model = new DomusControl();
        sc = NewMenu.getScanner();
    }
    public void run() {
        NewMenu menu = new NewMenu(new String[] {
                "Gestão de Utilizadores",
                "Gestão de Casas",
                "Cenários",
                "Avançar no Tempo",
                "Estatísticas",
                "Sugestões Inteligentes",
                "Gravar Estado",
                "Carregar Estado"
        });
        menu.setHandler(1, () -> menuUtilizadores());
        menu.setHandler(2, () -> menuCasas());
        menu.setHandler(3, () -> menuCenarios());
        menu.setHandler(4, () -> avancarTempo());
        menu.setHandler(5, () -> mostrarEstatisticas());
        menu.setHandler(6, () -> mostrarSugestoes());
        menu.setHandler(7, () -> guardarEstado());
        menu.setHandler(8, () -> carregarEstado());
        menu.run();
    }
    private void menuUtilizadores() {
        NewMenu menu = new NewMenu(new String[] {
                "Adicionar Utilizador",
                "Listar Utilizadores",
                "Remover Utilizador"
        });
        menu.setHandler(1, () -> addUtilizador());
        menu.setHandler(2, () -> listUtilizadores());
        menu.setHandler(3, () -> remUtilizador());
        menu.run();
    }
    private void addUtilizador() {
        System.out.print("Nome do Utilizador: ");
        String nome = sc.nextLine();
        System.out.print("Email do Utilizador: ");
        String email = sc.nextLine();
        try {
            model.criarUtilizador(nome, email);
            System.out.println("Utilizador adicionado com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    private void listUtilizadores() {
        Map<String, Utilizador> utilizadores = model.obterTodosUtilizadores();
        if (utilizadores.isEmpty()) {
            System.out.println("Nenhum utilizador registado.");
        } else {
            for (Utilizador u : utilizadores.values()) {
                System.out.println(u);
            }
        }
    }
    private void remUtilizador() {
        System.out.print("Email do Utilizador a remover: ");
        String email = sc.nextLine();
        if (model.utilizadorExiste(email)) {
            model.removerUtilizador(email);
            System.out.println("Utilizador removido com sucesso.");
        } else {
            System.out.println("Erro: Utilizador não encontrado.");
        }
    }
    private void menuCasas() {
        NewMenu menu = new NewMenu(new String[] {
                "Adicionar Casa",
                "Listar Casas",
                "Remover Casa",
                "Gerir Casa Específica"
        });
        menu.setHandler(1, () -> addCasa());
        menu.setHandler(2, () -> listCasas());
        menu.setHandler(3, () -> remCasa());
        menu.setHandler(4, () -> gerirCasaEspecifica());
        menu.run();
    }
    private void addCasa() {
        System.out.print("Email do Proprietário: ");
        String email = sc.nextLine();
        if (!model.utilizadorExiste(email)) {
            System.out.println("Erro: Utilizador não encontrado. Crie-o primeiro.");
            return;
        }
        System.out.print("Código Postal: ");
        int cp;
        try { cp = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Código Postal inválido."); return; }
        System.out.print("Fornecedor de Energia: ");
        String fornecedor = sc.nextLine();
        try {
            String idCasa = model.criarCasa(email, cp, fornecedor);
            System.out.println("Casa adicionada com sucesso. ID: " + idCasa);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    private void listCasas() {
        Map<String, Casa> casas = model.obterTodasCasas();
        if (casas.isEmpty()) { System.out.println("Nenhuma casa registada."); }
        else { for (Casa c : casas.values()) System.out.println(c); }
    }
    private String selecionarCasa() {
        Map<String, Casa> casas = model.obterTodasCasas();
        if (casas.isEmpty()) {
            System.out.println("Nenhuma casa registada.");
            return null;
        }
        List<String> ids = new ArrayList<>(casas.keySet());
        System.out.println("Casas disponíveis:");
        for (int i = 0; i < ids.size(); i++) {
            Casa c = casas.get(ids.get(i));
            System.out.println("  " + (i + 1) + ". " + c.getNomeProprietario() + " (CP: " + c.getCodigoPostal() + ") — " + c.getFornecedorEnergia());
        }
        System.out.print("Escolha (número): ");
        int escolha;
        try { escolha = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Opção inválida."); return null; }
        if (escolha < 1 || escolha > ids.size()) {
            System.out.println("Opção inválida.");
            return null;
        }
        return ids.get(escolha - 1);
    }
    private void remCasa() {
        String idCasa = selecionarCasa();
        if (idCasa == null) return;
        model.removerCasa(idCasa);
        System.out.println("Casa removida com sucesso.");
    }
    private void gerirCasaEspecifica() {
        String idCasa = selecionarCasa();
        if (idCasa == null) return;
        System.out.print("Email do Utilizador: ");
        String email = sc.nextLine();
        if (!model.utilizadorExiste(email)) {
            System.out.println("Erro: Utilizador não encontrado.");
            return;
        }
        NewMenu menu = new NewMenu(new String[] {
                "Adicionar Divisão",
                "Listar Divisões",
                "Adicionar Dispositivo",
                "Listar Dispositivos",
                "Ligar/Desligar Dispositivo",
                "Operar Dispositivo",
                "Automações",
                "Escalonamentos",
                "Aplicar Cenário"
        });
        menu.setHandler(1, () -> addDivisao(idCasa, email));
        menu.setHandler(2, () -> listDivisoes(idCasa));
        menu.setHandler(3, () -> addDispositivo(idCasa, email));
        menu.setHandler(4, () -> listDispositivos(idCasa));
        menu.setHandler(5, () -> toggleDispositivo(idCasa, email));
        menu.setHandler(6, () -> operarDispositivo(idCasa, email));
        menu.setHandler(7, () -> menuAutomacoes(idCasa, email));
        menu.setHandler(8, () -> menuEscalonamentos(idCasa));
        menu.setHandler(9, () -> aplicarCenario(idCasa));
        menu.run();
    }
    private void addDivisao(String idCasa, String email) {
        System.out.print("Nome da nova Divisão: ");
        String nome = sc.nextLine();
        try {
            model.adicionarDivisaoACasa(idCasa, email, nome);
            System.out.println("Divisão adicionada com sucesso.");
        } catch (IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    private void listDivisoes(String idCasa) {
        Casa casa = model.obterCasa(idCasa);
        if (casa.getDivisoes().isEmpty()) {
            System.out.println("A casa não possui divisões registadas.");
        } else {
            for (var entry : casa.getDivisoes().entrySet()) {
                System.out.println(" - " + entry.getKey() + " (" + entry.getValue().getIdDispositivos().size() + " dispositivos)");
            }
        }
    }
    private void addDispositivo(String idCasa, String email) {
        System.out.println("Tipo de Dispositivo:");
        System.out.println("1 - Lâmpada  2 - Coluna de Som  3 - Cortina  4 - Portão de Garagem  5 - Relé");
        System.out.print("Opção: ");
        int tipo;
        try { tipo = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Opção inválida."); return; }
        System.out.print("ID do Dispositivo: ");
        String id = sc.nextLine();
        System.out.print("Marca: ");
        String marca = sc.nextLine();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine();
        System.out.print("Consumo Por Hora (Wh): ");
        double consumo;
        try { consumo = Double.parseDouble(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Valor de consumo inválido."); return; }
        System.out.print("Estado Inicial (1 para Ligado, 0 para Desligado): ");
        boolean estado = sc.nextLine().equals("1");
        Dispositivo d = null;
        try {
            switch (tipo) {
                case 1:
                    System.out.print("Intensidade (0 a 100): ");
                    int intLum = Integer.parseInt(sc.nextLine());
                    System.out.print("Suporta cor? (1/0): ");
                    boolean cor = sc.nextLine().equals("1");
                    int tempCor = 2700;
                    if (cor) { System.out.print("Temperatura de cor (2700-4000): "); tempCor = Integer.parseInt(sc.nextLine()); }
                    d = new Lampada(id, marca, modelo, consumo, estado, intLum, cor, tempCor);
                    break;
                case 2:
                    System.out.print("Volume (0 a 100): ");
                    d = new ColunaSom(id, marca, modelo, consumo, estado, Integer.parseInt(sc.nextLine()));
                    break;
                case 3:
                    System.out.print("Abertura (0-100%): ");
                    d = new Cortina(id, marca, modelo, consumo, estado, Integer.parseInt(sc.nextLine()));
                    break;
                case 4:
                    System.out.print("Abertura (0-100%): ");
                    d = new PortaoGaragem(id, marca, modelo, consumo, estado, Integer.parseInt(sc.nextLine()));
                    break;
                case 5:
                    d = new Rele(id, marca, modelo, consumo, estado);
                    break;
                default: System.out.println("Opção inválida."); return;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage()); return;
        }
        System.out.print("Nome da Divisão: ");
        String divisao = sc.nextLine();
        try {
            model.adicionarDispositivoACasa(idCasa, email, d, divisao);
            System.out.println("Dispositivo adicionado com sucesso.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    private void listDispositivos(String idCasa) {
        Casa casa = model.obterCasa(idCasa);
        Map<String, Dispositivo> dispositivos = casa.getDispositivos();
        if (dispositivos.isEmpty()) { System.out.println("Sem dispositivos registados."); }
        else { for (Dispositivo d : dispositivos.values()) System.out.println(d); }
    }
    private void toggleDispositivo(String idCasa, String email) {
        System.out.print("ID do Dispositivo: ");
        String id = sc.nextLine();
        Dispositivo d = model.obterDispositivoCasa(idCasa, id);
        if (d == null) { System.out.println("Erro: Dispositivo não encontrado."); return; }
        try {
            if (d.getEstado()) { model.desligarDispositivo(idCasa, email, id); System.out.println("Dispositivo desligado."); }
            else { model.ligarDispositivo(idCasa, email, id); System.out.println("Dispositivo ligado."); }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    private void operarDispositivo(String idCasa, String email) {
        System.out.print("ID do Dispositivo: ");
        String id = sc.nextLine();
        Dispositivo d = model.obterDispositivoCasa(idCasa, id);
        if (d == null) { System.out.println("Erro: Dispositivo não encontrado."); return; }
        System.out.println("Estado atual: " + d);
        if (d instanceof Lampada) menuLampada(idCasa, email, id);
        else if (d instanceof ColunaSom) menuColunaSom(idCasa, email, id);
        else if (d instanceof Cortina) menuCortina(idCasa, email, id);
        else if (d instanceof PortaoGaragem) menuPortaoGaragem(idCasa, email, id);
        else System.out.println("Este dispositivo apenas suporta ligar/desligar.");
    }
    private void menuLampada(String idCasa, String email, String idDisp) {
        NewMenu menu = new NewMenu(new String[] { "Ajustar Intensidade", "Ajustar Temperatura de Cor" });
        menu.setHandler(1, () -> {
            System.out.print("Nova intensidade (0-100): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((Lampada) disp).setIntensidade(val));
                System.out.println("Intensidade ajustada para " + val + ".");
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.setHandler(2, () -> {
            System.out.print("Nova temperatura de cor (2700-4000 K): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((Lampada) disp).setTemperaturaCor(val));
                System.out.println("Temperatura de cor ajustada para " + val + "K.");
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.run();
    }
    private void menuColunaSom(String idCasa, String email, String idDisp) {
        NewMenu menu = new NewMenu(new String[] { "Ajustar Volume" });
        menu.setHandler(1, () -> {
            System.out.print("Novo volume (0-100): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((ColunaSom) disp).setVolume(val));
                System.out.println("Volume ajustado para " + val + ".");
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.run();
    }
    private void menuCortina(String idCasa, String email, String idDisp) {
        NewMenu menu = new NewMenu(new String[] { "Ajustar Abertura", "Abrir Totalmente", "Fechar Totalmente" });
        menu.setHandler(1, () -> {
            System.out.print("Percentagem de abertura (0-100): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((Cortina) disp).setPercentagemAbertura(val));
                System.out.println("Abertura ajustada para " + val + "%.");
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.setHandler(2, () -> {
            try { model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((Cortina) disp).abrirCortina()); System.out.println("Cortina aberta."); }
            catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.setHandler(3, () -> {
            try { model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((Cortina) disp).fecharCortina()); System.out.println("Cortina fechada."); }
            catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.run();
    }
    private void menuPortaoGaragem(String idCasa, String email, String idDisp) {
        NewMenu menu = new NewMenu(new String[] { "Ajustar Abertura", "Abrir Totalmente", "Fechar Totalmente" });
        menu.setHandler(1, () -> {
            System.out.print("Percentagem de abertura (0-100): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((PortaoGaragem) disp).setPercentagemAbertura(val));
                System.out.println("Abertura ajustada para " + val + "%.");
            } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.setHandler(2, () -> {
            try { model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((PortaoGaragem) disp).abrirPortao()); System.out.println("Portão aberto."); }
            catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.setHandler(3, () -> {
            try { model.operarSobreDispositivo(idCasa, email, idDisp, disp -> ((PortaoGaragem) disp).fecharPortao()); System.out.println("Portão fechado."); }
            catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.run();
    }
    private void menuAutomacoes(String idCasa, String email) {
        NewMenu menu = new NewMenu(new String[] {
                "Criar Automação",
                "Listar Automações",
                "Remover Automação",
                "Executar Automações"
        });
        menu.setHandler(1, () -> criarAutomacao(idCasa, email));
        menu.setHandler(2, () -> listarAutomacoes(idCasa));
        menu.setHandler(3, () -> removerAutomacao(idCasa, email));
        menu.setHandler(4, () -> {
            try { model.executarAutomacoesCasa(idCasa); System.out.println("Automações executadas."); }
            catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
        });
        menu.run();
    }
    private void criarAutomacao(String idCasa, String email) {
        System.out.print("Nome da automação: ");
        String nome = sc.nextLine();
        System.out.print("Descrição: ");
        String desc = sc.nextLine();
        System.out.println("Condição — dispositivo a verificar:");
        System.out.print("  ID do dispositivo de condição: ");
        String idCondDisp = sc.nextLine();
        System.out.print("  Condição: 1=ligado, 0=desligado: ");
        boolean condLigado = sc.nextLine().equals("1");
        System.out.println("Ação — divisão a atuar:");
        System.out.print("  Nome da divisão: ");
        String divAcao = sc.nextLine();
        System.out.print("  Ação: 1=ligar, 0=desligar: ");
        boolean acaoLigar = sc.nextLine().equals("1");
        Automacao automacao = new Automacao(nome, desc,
            casa -> {
                Dispositivo d = casa.getDispositivos().get(idCondDisp);
                return d != null && d.getEstado() == condLigado;
            },
            casa -> {
                if (acaoLigar) casa.ligarDispositivosDivisao(divAcao);
                else casa.desligarDispositivosDivisao(divAcao);
            }
        );
        try {
            model.associarAutomacaoCasa(idCasa, email, automacao);
            System.out.println("Automação criada com sucesso.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }
    private void listarAutomacoes(String idCasa) {
        Casa casa = model.obterCasa(idCasa);
        if (casa == null) { System.out.println("Casa não encontrada."); return; }
        Map<String, Automacao> automacoes = casa.getAutomacoes();
        if (automacoes.isEmpty()) { System.out.println("Sem automações definidas."); }
        else { for (Automacao a : automacoes.values()) System.out.println(a); }
    }
    private void removerAutomacao(String idCasa, String email) {
        Casa casa = model.obterCasa(idCasa);
        if (casa == null) { System.out.println("Casa não encontrada."); return; }
        Map<String, Automacao> automacoes = casa.getAutomacoes();
        if (automacoes.isEmpty()) { System.out.println("Sem automações definidas."); return; }
        List<String> ids = new ArrayList<>(automacoes.keySet());
        System.out.println("Automações:");
        for (int i = 0; i < ids.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + automacoes.get(ids.get(i)));
        }
        System.out.print("Escolha (número): ");
        int escolha;
        try { escolha = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Opção inválida."); return; }
        if (escolha < 1 || escolha > ids.size()) { System.out.println("Opção inválida."); return; }
        try {
            model.removerAutomacaoCasa(idCasa, email, ids.get(escolha - 1));
            System.out.println("Automação removida com sucesso.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }
    private void menuEscalonamentos(String idCasa) {
        NewMenu menu = new NewMenu(new String[] {
                "Criar Escalonamento",
                "Listar Escalonamentos",
                "Executar Pendentes"
        });
        menu.setHandler(1, () -> criarEscalonamento(idCasa));
        menu.setHandler(2, () -> System.out.println(model.listarEscalonamentos()));
        menu.setHandler(3, () -> {
            int n = model.executarEscalonamentosPendentes();
            System.out.println(n + " escalonamento(s) executado(s). Tempo atual: " + model.getTempoAtual());
        });
        menu.run();
    }
    private void criarEscalonamento(String idCasa) {
        System.out.print("Nome do escalonamento: ");
        String nome = sc.nextLine();
        System.out.print("Divisão a atuar: ");
        String divisao = sc.nextLine();
        System.out.print("Ação: 1=ligar, 0=desligar: ");
        boolean ligar = sc.nextLine().equals("1");
        System.out.print("Daqui a quantas horas? ");
        int horas;
        try { horas = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Valor inválido."); return; }
        LocalDateTime instante = model.getTempoAtual().plusHours(horas);
        try {
            String id = model.criarEscalonamento(nome, idCasa, instante,
                casa -> { if (ligar) casa.ligarDispositivosDivisao(divisao); else casa.desligarDispositivosDivisao(divisao); });
            System.out.println("Escalonamento criado. Execução às " + instante);
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }
    private void menuCenarios() {
        NewMenu menu = new NewMenu(new String[] {
                "Listar Cenários",
                "Criar Cenário Personalizado",
                "Remover Cenário"
        });
        menu.setHandler(1, () -> listarCenarios());
        menu.setHandler(2, () -> criarCenario());
        menu.setHandler(3, () -> removerCenario());
        menu.run();
    }
    private void listarCenarios() {
        Map<String, Cenario> cenarios = model.obterCenarios();
        if (cenarios.isEmpty()) { System.out.println("Sem cenários definidos."); }
        else { for (Cenario c : cenarios.values()) System.out.println(c); }
    }
    private void criarCenario() {
        System.out.print("Nome do cenário: ");
        String nome = sc.nextLine();
        System.out.print("Descrição: ");
        String desc = sc.nextLine();
        Cenario cenario = new Cenario(nome, desc);
        System.out.println("Adicione ações (divisão + ligar/desligar). Escreva 'fim' para terminar.");
        while (true) {
            System.out.print("  Divisão (ou 'fim'): ");
            String div = sc.nextLine();
            if (div.equalsIgnoreCase("fim")) break;
            System.out.print("  Ação: 1=ligar, 0=desligar: ");
            boolean ligar = sc.nextLine().equals("1");
            if (ligar) cenario.adicionarAcao(casa -> casa.ligarDispositivosDivisao(div));
            else cenario.adicionarAcao(casa -> casa.desligarDispositivosDivisao(div));
            System.out.println("  Ação adicionada.");
        }
        model.criarCenario(cenario);
        System.out.println("Cenário '" + nome + "' criado com " + cenario.getAcoes().size() + " ações.");
    }
    private void removerCenario() {
        Map<String, Cenario> cenarios = model.obterCenarios();
        if (cenarios.isEmpty()) { System.out.println("Sem cenários definidos."); return; }
        List<String> ids = new ArrayList<>(cenarios.keySet());
        System.out.println("Cenários:");
        for (int i = 0; i < ids.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + cenarios.get(ids.get(i)));
        }
        System.out.print("Escolha (número): ");
        int escolha;
        try { escolha = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Opção inválida."); return; }
        if (escolha < 1 || escolha > ids.size()) { System.out.println("Opção inválida."); return; }
        model.removerCenario(ids.get(escolha - 1));
        System.out.println("Cenário removido.");
    }
    private void aplicarCenario(String idCasa) {
        Map<String, Cenario> cenarios = model.obterCenarios();
        if (cenarios.isEmpty()) { System.out.println("Sem cenários disponíveis."); return; }
        List<String> ids = new ArrayList<>(cenarios.keySet());
        System.out.println("Cenários disponíveis:");
        for (int i = 0; i < ids.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + cenarios.get(ids.get(i)));
        }
        System.out.print("Escolha (número): ");
        int escolha;
        try { escolha = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Opção inválida."); return; }
        if (escolha < 1 || escolha > ids.size()) { System.out.println("Opção inválida."); return; }
        try {
            model.aplicarCenario(ids.get(escolha - 1), idCasa);
            System.out.println("Cenário aplicado com sucesso.");
        } catch (Exception e) { System.out.println("Erro: " + e.getMessage()); }
    }
    private void avancarTempo() {
        System.out.print("Avançar quantos dias? ");
        int dias;
        try { dias = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Valor inválido."); return; }
        System.out.print("Avançar quantas horas? ");
        int horas;
        try { horas = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) { System.out.println("Valor inválido."); return; }
        model.avancarDias(dias);
        model.avancarHoras(horas);
        model.executarEscalonamentosPendentes();
        System.out.println("Tempo avançado. Tempo atual: " + model.getTempoAtual());
    }
    private void mostrarEstatisticas() {
        System.out.println(model.obterEstatisticas());
    }
    private void mostrarSugestoes() {
        List<Sugestao> sugestoes = model.gerarSugestoes();
        if (sugestoes.isEmpty()) {
            System.out.println("Sem sugestões no momento. Use mais o sistema para gerar dados.");
            return;
        }
        System.out.println("=== Sugestões Inteligentes (" + sugestoes.size() + ") ===");
        for (int i = 0; i < sugestoes.size(); i++) {
            System.out.println((i + 1) + ". " + sugestoes.get(i));
        }
        System.out.println("\nDeseja aceitar alguma sugestão?");
        System.out.print("Introduza o número da sugestão, 'todos' para aceitar todas, ou 'nao' para sair: ");
        String resposta = sc.nextLine().trim();
        if (resposta.equalsIgnoreCase("nao") || resposta.equalsIgnoreCase("não") || resposta.equals("0")) {
            System.out.println("Nenhuma sugestão aceite.");
            return;
        }
        if (resposta.equalsIgnoreCase("todos") || resposta.equalsIgnoreCase("all")) {
            for (Sugestao s : sugestoes) {
                try {
                    String resultado = model.aceitarSugestao(s);
                    System.out.println("  ✓ " + resultado);
                } catch (Exception e) {
                    System.out.println("  ✗ Erro: " + e.getMessage());
                }
            }
            System.out.println("Todas as sugestões foram processadas.");
            return;
        }
        try {
            int indice = Integer.parseInt(resposta);
            if (indice < 1 || indice > sugestoes.size()) {
                System.out.println("Número inválido.");
                return;
            }
            String resultado = model.aceitarSugestao(sugestoes.get(indice - 1));
            System.out.println("✓ " + resultado);
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida.");
        } catch (Exception e) {
            System.out.println("Erro ao aceitar sugestão: " + e.getMessage());
        }
    }
    private void guardarEstado() {
        try {
            model.guardarEstadoBinario(FICHEIRO_ESTADO);
            System.out.println("Estado guardado com sucesso em '" + FICHEIRO_ESTADO + "'.");
        } catch (IOException e) {
            System.out.println("Erro ao gravar: " + e.getMessage());
        }
    }
    private void carregarEstado() {
        try {
            this.model = DomusControl.lerEstadoBinario(FICHEIRO_ESTADO);
            System.out.println("Estado carregado com sucesso de '" + FICHEIRO_ESTADO + "'.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar: " + e.getMessage());
        }
    }
}
