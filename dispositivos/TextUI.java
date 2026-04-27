package dispositivos;

import controlador.DomusControl;
import casas.Casa;
import utilizadores.Utilizador;
import java.util.Scanner;
import java.util.Map;

public class TextUI {
    private DomusControl model;
    private Scanner sc;

    public TextUI() {
        this.model = new DomusControl();
        sc = new Scanner(System.in);
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

        if (model.utilizadorExiste(email)) {
            System.out.println("Erro: Utilizador com este email já existe.");
        } else {
            model.adicionarUtilizador(email, new Utilizador(nome, email));
            System.out.println("Utilizador adicionado com sucesso.");
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
        System.out.print("Nome do Proprietário: ");
        String nome = sc.nextLine();
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

        Casa novaCasa = new Casa(nome, cp, fornecedor);
        model.adicionarCasa(novaCasa.getIdCasa(), novaCasa);
        System.out.println("Casa adicionada com sucesso. ID: " + novaCasa.getIdCasa());
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
        Casa casa = model.obterCasa(idCasa);

        if (casa == null) {
            System.out.println("Erro: Casa não encontrada.");
            return;
        }

        NewMenu menu = new NewMenu(new String[] {
                "Adicionar Dispositivo",
                "Listar Dispositivos",
                "Ligar/Desligar Dispositivo",
                "Adicionar Divisão",
                "Listar Divisões"
        });

        menu.setHandler(1, () -> addDispositivo(casa));
        menu.setHandler(2, () -> listDispositivos(casa));
        menu.setHandler(3, () -> toggleDispositivo(casa));
        menu.setHandler(4, () -> addDivisao(casa));
        menu.setHandler(5, () -> listDivisoes(casa));

        menu.run();
    }

    private void addDispositivo(Casa casa) {
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
        System.out.print("Consumo Por Hora: ");
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

        casa.addDispositivo(d, divisao);
        System.out.println("Dispositivo adicionado com sucesso.");
    }

    private void listDispositivos(Casa casa) {
        Map<String, Dispositivo> dispositivos = casa.getDispositivos();
        if (dispositivos.isEmpty()) {
            System.out.println("A casa não possui dispositivos registados.");
        } else {
            for (Dispositivo d : dispositivos.values()) {
                System.out.println(d);
            }
        }
    }

    private void toggleDispositivo(Casa casa) {
        System.out.print("ID do Dispositivo a Ligar/Desligar: ");
        String id = sc.nextLine();
        Dispositivo d = casa.getDispositivos().get(id);

        if (d != null) {
            if (d.getEstado()) {
                d.desligarDispositivo();
                System.out.println("Dispositivo desligado.");
            } else {
                d.ligarDispositivo();
                System.out.println("Dispositivo ligado.");
                model.registarUsoDispositivo(id);
            }
        } else {
            System.out.println("Erro: Dispositivo não encontrado.");
        }
    }

    private void addDivisao(Casa casa) {
        System.out.print("Nome da nova Divisão: ");
        String nome = sc.nextLine();
        casa.addDivisao(nome);
        System.out.println("Divisão adicionada com sucesso.");
    }

    private void listDivisoes(Casa casa) {
        if (casa.getDivisoes().isEmpty()) {
            System.out.println("A casa não possui divisões registadas.");
        } else {
            System.out.println("Divisões: " + casa.getDivisoes().keySet());
        }
    }

    private void avancarTempo() {
        System.out.print("Avançar quantos dias? ");
        int dias;
        System.out.print("Avançar quantas horas? ");
        int horas;
        try {
            dias = Integer.parseInt(sc.nextLine());
            horas = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor de tempo inválido.");
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
