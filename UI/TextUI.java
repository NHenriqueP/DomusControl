package UI;

import controlador.DomusControl;
import dispositivos.ColunaSom;
import dispositivos.Cortina;
import dispositivos.Dispositivo;
import dispositivos.Lampada;
import dispositivos.PortaoGaragem;
import dispositivos.Rele;
import casas.Casa;
import utilizadores.Utilizador;
import java.util.Map;
import java.util.Scanner;

public class TextUI {
    private DomusControl model;
    private Scanner sc;

    public TextUI() {
        this.model = new DomusControl();
        sc = NewMenu.getScanner();
    }

    public void run() {
        NewMenu menu = new NewMenu(new String[] {
                "Gestão de Utilizadores",
                "Gestão de Casas",
                "Avançar no Tempo",
                "Estatísticas"
        });

        menu.setHandler(1, () -> menuUtilizadores());
        menu.setHandler(2, () -> menuCasas());
        menu.setHandler(3, () -> avancarTempo());
        menu.setHandler(4, () -> mostrarEstatisticas());

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

    private void addCasa() {
        System.out.print("Email do Proprietário: ");
        String email = sc.nextLine();

        if (!model.utilizadorExiste(email)) {
            System.out.println("Erro: Utilizador não encontrado. Crie-o primeiro.");
            return;
        }

        System.out.print("Código Postal: ");
        int cp;
        try {
            cp = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Código Postal inválido.");
            return;
        }
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
        if (casas.isEmpty()) {
            System.out.println("Nenhuma casa registada.");
        } else {
            for (Casa c : casas.values()) {
                System.out.println(c);
            }
        }
    }

    private void remCasa() {
        System.out.print("ID da Casa a remover: ");
        String idCasa = sc.nextLine();

        if (model.obterCasa(idCasa) != null) {
            model.removerCasa(idCasa);
            System.out.println("Casa removida com sucesso.");
        } else {
            System.out.println("Erro: Casa não encontrada.");
        }
    }

    private void gerirCasaEspecifica() {
        System.out.print("ID da Casa a gerir: ");
        String idCasa = sc.nextLine();

        if (model.obterCasa(idCasa) == null) {
            System.out.println("Erro: Casa não encontrada.");
            return;
        }

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
                "Operar Dispositivo"
        });

        menu.setHandler(1, () -> addDivisao(idCasa, email));
        menu.setHandler(2, () -> listDivisoes(idCasa));
        menu.setHandler(3, () -> addDispositivo(idCasa, email));
        menu.setHandler(4, () -> listDispositivos(idCasa));
        menu.setHandler(5, () -> toggleDispositivo(idCasa, email));
        menu.setHandler(6, () -> operarDispositivo(idCasa, email));

        menu.run();
    }

    // ========================= Divisões =========================

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

    // ========================= Dispositivos =========================

    private void addDispositivo(String idCasa, String email) {
        System.out.println("Tipo de Dispositivo:");
        System.out.println("1 - Lâmpada");
        System.out.println("2 - Coluna de Som");
        System.out.println("3 - Cortina");
        System.out.println("4 - Portão de Garagem");
        System.out.println("5 - Relé");
        System.out.print("Opção: ");
        
        int tipo;
        try {
            tipo = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opção inválida.");
            return;
        }

        System.out.print("ID do Dispositivo: ");
        String id = sc.nextLine();
        System.out.print("Marca: ");
        String marca = sc.nextLine();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine();
        System.out.print("Consumo Por Hora (Wh): ");
        double consumo;
        try {
            consumo = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor de consumo inválido.");
            return;
        }
        
        System.out.print("Estado Inicial (1 para Ligado, 0 para Desligado): ");
        boolean estado = sc.nextLine().equals("1");

        Dispositivo d = null;
        try {
            switch (tipo) {
                case 1:
                    System.out.print("Intensidade (0 a 100): ");
                    int intLum = Integer.parseInt(sc.nextLine());
                    System.out.print("Suporta cor? (1 para Sim, 0 para Não): ");
                    boolean cor = sc.nextLine().equals("1");
                    int tempCor = 2700;
                    if (cor) {
                        System.out.print("Temperatura de cor (2700 a 4000): ");
                        tempCor = Integer.parseInt(sc.nextLine());
                    }
                    d = new Lampada(id, marca, modelo, consumo, estado, intLum, cor, tempCor);
                    break;
                case 2:
                    System.out.print("Volume (0 a 100): ");
                    int vol = Integer.parseInt(sc.nextLine());
                    d = new ColunaSom(id, marca, modelo, consumo, estado, vol);
                    break;
                case 3:
                    System.out.print("Percentagem de Abertura (0 a 100): ");
                    int grauCortina = Integer.parseInt(sc.nextLine());
                    d = new Cortina(id, marca, modelo, consumo, estado, grauCortina);
                    break;
                case 4:
                    System.out.print("Percentagem de Abertura (0 a 100): ");
                    int grauPortao = Integer.parseInt(sc.nextLine());
                    d = new PortaoGaragem(id, marca, modelo, consumo, estado, grauPortao);
                    break;
                case 5:
                    d = new Rele(id, marca, modelo, consumo, estado);
                    break;
                default:
                    System.out.println("Opção inválida.");
                    return;
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
            return;
        }

        System.out.print("Nome da Divisão onde o dispositivo será colocado: ");
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
        if (dispositivos.isEmpty()) {
            System.out.println("A casa não possui dispositivos registados.");
        } else {
            for (Dispositivo d : dispositivos.values()) {
                System.out.println(d);
            }
        }
    }

    private void toggleDispositivo(String idCasa, String email) {
        System.out.print("ID do Dispositivo: ");
        String id = sc.nextLine();

        Dispositivo d = model.obterDispositivoCasa(idCasa, id);
        if (d == null) {
            System.out.println("Erro: Dispositivo não encontrado.");
            return;
        }

        try {
            if (d.getEstado()) {
                model.desligarDispositivo(idCasa, email, id);
                System.out.println("Dispositivo desligado.");
            } else {
                model.ligarDispositivo(idCasa, email, id);
                System.out.println("Dispositivo ligado.");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ========================= Operações Específicas =========================

    private void operarDispositivo(String idCasa, String email) {
        System.out.print("ID do Dispositivo: ");
        String id = sc.nextLine();

        Dispositivo d = model.obterDispositivoCasa(idCasa, id);
        if (d == null) {
            System.out.println("Erro: Dispositivo não encontrado.");
            return;
        }

        System.out.println("Estado atual: " + d);

        if (d instanceof Lampada) {
            menuLampada(idCasa, email, id);
        } else if (d instanceof ColunaSom) {
            menuColunaSom(idCasa, email, id);
        } else if (d instanceof Cortina) {
            menuCortina(idCasa, email, id);
        } else if (d instanceof PortaoGaragem) {
            menuPortaoGaragem(idCasa, email, id);
        } else {
            System.out.println("Este dispositivo apenas suporta ligar/desligar.");
        }
    }

    private void menuLampada(String idCasa, String email, String idDisp) {
        NewMenu menu = new NewMenu(new String[] {
                "Ajustar Intensidade",
                "Ajustar Temperatura de Cor"
        });

        menu.setHandler(1, () -> {
            System.out.print("Nova intensidade (0-100): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((Lampada) dispositivo).setIntensidade(val));
                System.out.println("Intensidade ajustada para " + val + ".");
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.setHandler(2, () -> {
            System.out.print("Nova temperatura de cor (2700-4000 K): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((Lampada) dispositivo).setTemperaturaCor(val));
                System.out.println("Temperatura de cor ajustada para " + val + "K.");
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.run();
    }

    private void menuColunaSom(String idCasa, String email, String idDisp) {
        NewMenu menu = new NewMenu(new String[] {
                "Ajustar Volume"
        });

        menu.setHandler(1, () -> {
            System.out.print("Novo volume (0-100): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((ColunaSom) dispositivo).setVolume(val));
                System.out.println("Volume ajustado para " + val + ".");
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.run();
    }

    private void menuCortina(String idCasa, String email, String idDisp) {
        NewMenu menu = new NewMenu(new String[] {
                "Ajustar Abertura",
                "Abrir Totalmente",
                "Fechar Totalmente"
        });

        menu.setHandler(1, () -> {
            System.out.print("Percentagem de abertura (0-100): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((Cortina) dispositivo).setPercentagemAbertura(val));
                System.out.println("Abertura ajustada para " + val + "%.");
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.setHandler(2, () -> {
            try {
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((Cortina) dispositivo).abrirCortina());
                System.out.println("Cortina aberta totalmente.");
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.setHandler(3, () -> {
            try {
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((Cortina) dispositivo).fecharCortina());
                System.out.println("Cortina fechada totalmente.");
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.run();
    }

    private void menuPortaoGaragem(String idCasa, String email, String idDisp) {
        NewMenu menu = new NewMenu(new String[] {
                "Ajustar Abertura",
                "Abrir Totalmente",
                "Fechar Totalmente"
        });

        menu.setHandler(1, () -> {
            System.out.print("Percentagem de abertura (0-100): ");
            try {
                int val = Integer.parseInt(sc.nextLine());
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((PortaoGaragem) dispositivo).setPercentagemAbertura(val));
                System.out.println("Abertura ajustada para " + val + "%.");
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.setHandler(2, () -> {
            try {
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((PortaoGaragem) dispositivo).abrirPortao());
                System.out.println("Portão aberto totalmente.");
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.setHandler(3, () -> {
            try {
                model.operarSobreDispositivo(idCasa, email, idDisp,
                    dispositivo -> ((PortaoGaragem) dispositivo).fecharPortao());
                System.out.println("Portão fechado totalmente.");
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        });

        menu.run();
    }

    // ========================= Tempo e Estatísticas =========================

    private void avancarTempo() {
        System.out.print("Avançar quantos dias? ");
        int dias;
        try {
            dias = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor de dias inválido.");
            return;
        }

        System.out.print("Avançar quantas horas? ");
        int horas;
        try {
            horas = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor de horas inválido.");
            return;
        }

        model.avancarDias(dias);
        model.avancarHoras(horas);
        System.out.println("Tempo avançado com sucesso. Tempo atual: " + model.getTempoAtual());
    }

    private void mostrarEstatisticas() {
        System.out.println(model.obterEstatisticas());
    }
}
