import automacoes.Automacao;
import casas.TipoAcessoCasa;
import controlador.DomusControl;
import dispositivos.Lampada;
import dispositivos.Rele;

import java.time.LocalDateTime;

public class TesteRequisitosBaseGestao {

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException("FALHOU: " + message);
        }
        System.out.println("OK: " + message);
    }

    private static void expectException(Runnable action, String message) {
        try {
            action.run();
            throw new IllegalStateException("FALHOU: " + message + " (nao lancou excecao)");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("OK: " + message + " -> " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        DomusControl dc = new DomusControl();

        // 1) Criar utilizadores
        dc.criarUtilizador("Henriqueta Turista", "henriqueta@domus.pt");
        dc.criarUtilizador("Nuna Afonso", "nuna@domus.pt");
        dc.criarUtilizador("Gustavo Lopes", "gustavo@domus.pt");
        assertCondition(dc.utilizadorExiste("henriqueta@domus.pt"), "utilizador Henriqueta Turista criado");

        // 2) Criar casa e atribuir acesso
        String idCasa = dc.criarCasa("henriqueta@domus.pt", 4700, "EDP");
        dc.atribuirAcessoCasa(idCasa, "nuna@domus.pt", TipoAcessoCasa.UTILIZADOR);
        assertCondition(dc.obterCasa(idCasa) != null, "casa criada com id valido");

        // 3) Criar divisoes e associar dispositivos
        dc.adicionarDivisaoACasa(idCasa, "henriqueta@domus.pt", "Sala");
        dc.adicionarDivisaoACasa(idCasa, "henriqueta@domus.pt", "Quarto");
        dc.adicionarDivisaoACasa(idCasa, "henriqueta@domus.pt", "Cozinha");

        Rele releSala = new Rele("RELE-1", "Shelly", "Plus1", 15.0, false);
        Lampada lampadaQuarto = new Lampada("LAMP-1", "Philips", "Hue", 9.5, false, 70, true, 3000);

        dc.adicionarDispositivoACasa(idCasa, "henriqueta@domus.pt", releSala, "Sala");
        dc.adicionarDispositivoACasa(idCasa, "henriqueta@domus.pt", lampadaQuarto, "Quarto");
        dc.adicionarDispositivoACasa(idCasa, "henriqueta@domus.pt",
            new Rele("RELE-COZ-1", "Shelly", "Kitchen", 11.0, false), "Cozinha");

        assertCondition(!dc.obterCasa(idCasa).getDispositivos().get("RELE-1").getEstado(), "rele inicia desligado");
        assertCondition(!dc.obterCasa(idCasa).getDispositivos().get("LAMP-1").getEstado(), "lampada inicia desligada");

        // 4) Atuar num dispositivo especifico
        dc.ligarDispositivo(idCasa, "nuna@domus.pt", "RELE-1");
        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("RELE-1").getEstado(), "ligar dispositivo especifico");

        dc.desligarDispositivo(idCasa, "nuna@domus.pt", "RELE-1");
        assertCondition(!dc.obterCasa(idCasa).getDispositivos().get("RELE-1").getEstado(), "desligar dispositivo especifico");

        // 5) Operar por divisao
        dc.ligarDispositivosDivisao(idCasa, "Quarto");
        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("LAMP-1").getEstado(), "ligar dispositivos da divisao");

        dc.desligarDispositivosDivisao(idCasa, "Quarto");
        assertCondition(!dc.obterCasa(idCasa).getDispositivos().get("LAMP-1").getEstado(), "desligar dispositivos da divisao");

        // 6) Testar regra de acesso (utilizador sem acesso)
        expectException(
            () -> dc.ligarDispositivo(idCasa, "gustavo@domus.pt", "RELE-1"),
            "bloquear operacao de utilizador sem acesso"
        );

        // 7) Estatisticas de uso (tempo + ativacoes)
        dc.ligarDispositivo(idCasa, "nuna@domus.pt", "RELE-1");
        dc.avancarHoras(2);
        dc.desligarDispositivo(idCasa, "nuna@domus.pt", "RELE-1");

        assertCondition(dc.obterAtivacoesDispositivoCasa(idCasa, "RELE-1") >= 2,
            "ativacoes do RELE-1 contabilizadas");
        assertCondition(dc.obterHorasUsoDispositivoCasa(idCasa, "RELE-1") >= 2.0,
            "tempo de uso do RELE-1 contabilizado");

        // 8) Criar segunda casa para testar rankings globais
        String idCasa2 = dc.criarCasa("henriqueta@domus.pt", 4710, "GALP");
        dc.adicionarDivisaoACasa(idCasa2, "henriqueta@domus.pt", "Cozinha");

        dc.adicionarDispositivoACasa(idCasa2, "henriqueta@domus.pt",
            new Rele("RELE-2", "Shelly", "Plus2", 12.0, false), "Cozinha");
        dc.adicionarDispositivoACasa(idCasa2, "henriqueta@domus.pt",
            new Lampada("LAMP-2", "Ikea", "Tradfri", 8.0, false, 60, false, 2700), "Cozinha");
        dc.adicionarDispositivoACasa(idCasa2, "henriqueta@domus.pt",
            new Rele("RELE-3", "Shelly", "Mini", 10.0, false), "Cozinha");

        String topDivisoes = dc.top3DivisoesComMaisDispositivos();
        assertCondition(topDivisoes.contains("Cozinha"), "top 3 divisoes inclui Cozinha");

        String topCasa = dc.top3DispositivosMaisUsadosCasa(idCasa);
        assertCondition(topCasa.contains("RELE-1"), "top da casa inclui RELE-1");

        // 9) Persistencia binaria (gravar e ler)
        String ficheiro = "estado_domus.bin";
        dc.guardarEstadoBinario(ficheiro);
        DomusControl carregado = DomusControl.lerEstadoBinario(ficheiro);

        assertCondition(carregado.obterCasa(idCasa) != null, "leitura binaria recupera a casa 1");
        assertCondition(carregado.obterCasa(idCasa2) != null, "leitura binaria recupera a casa 2");
        assertCondition(carregado.obterAtivacoesDispositivoCasa(idCasa, "RELE-1") >= 2,
            "leitura binaria preserva estatisticas de ativacao");

        // 10) Criar duas automacoes e verificar ativacao
        dc.desligarDispositivo(idCasa, "nuna@domus.pt", "LAMP-1");
        dc.desligarDispositivo(idCasa, "nuna@domus.pt", "RELE-1");

        Automacao automacaoLigarLampada = new Automacao(
            "Auto ligar lampada",
            "Liga LAMP-1 quando estiver desligada",
            casa -> casa.getDispositivos().containsKey("LAMP-1") && !casa.getDispositivos().get("LAMP-1").getEstado(),
            casa -> casa.ligarDispositivosDivisao("Quarto")
        );

        Automacao automacaoLigarRele = new Automacao(
            "Auto ligar rele",
            "Liga RELE-1 quando estiver desligado",
            casa -> casa.getDispositivos().containsKey("RELE-1") && !casa.getDispositivos().get("RELE-1").getEstado(),
            casa -> casa.ligarDispositivosDivisao("Sala")
        );

        dc.associarAutomacaoCasa(idCasa, "henriqueta@domus.pt", automacaoLigarLampada);
        dc.associarAutomacaoCasa(idCasa, "henriqueta@domus.pt", automacaoLigarRele);
        dc.executarAutomacoesCasa(idCasa);

        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("LAMP-1").getEstado(),
            "automacao 1 ativada");
        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("RELE-1").getEstado(),
            "automacao 2 ativada");

        // 11) Definir quatro escalonamentos e testar execucao
        LocalDateTime base = dc.getTempoAtual();
        dc.criarEscalonamento("Escalonamento 1", idCasa, base.plusHours(1), casa -> casa.ligarDispositivosDivisao("Sala"));
        dc.criarEscalonamento("Escalonamento 2", idCasa, base.plusHours(2), casa -> casa.desligarDispositivosDivisao("Sala"));
        dc.criarEscalonamento("Escalonamento 3", idCasa, base.plusHours(3), casa -> casa.ligarDispositivosDivisao("Quarto"));
        dc.criarEscalonamento("Escalonamento 4", idCasa, base.plusHours(4), casa -> casa.desligarDispositivosDivisao("Quarto"));

        dc.avancarHoras(5);
        int totalExecutados = dc.executarEscalonamentosPendentes();
        assertCondition(totalExecutados >= 4, "quatro escalonamentos executados");

        String listaEscalonamentos = dc.listarEscalonamentos();
        assertCondition(listaEscalonamentos.contains("Escalonamento 1") && listaEscalonamentos.contains("Executado: true"),
            "lista de escalonamentos atualizada");

        // 12) Criar e testar quatro cenarios
        dc.ligarDispositivo(idCasa, "nuna@domus.pt", "RELE-1");
        dc.ligarDispositivo(idCasa, "nuna@domus.pt", "LAMP-1");
        dc.ligarDispositivo(idCasa, "nuna@domus.pt", "RELE-COZ-1");

        dc.aplicarCenarioSairDeCasa(idCasa);
        assertCondition(dc.totalDispositivosLigadosCasa(idCasa) == 0, "cenario sair de casa aplicado");

        dc.aplicarCenarioJantarComAmigos(idCasa);
        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("RELE-1").getEstado(),
            "cenario jantar com amigos liga sala");
        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("RELE-COZ-1").getEstado(),
            "cenario jantar com amigos liga cozinha");
        assertCondition(!dc.obterCasa(idCasa).getDispositivos().get("LAMP-1").getEstado(),
            "cenario jantar com amigos desliga quarto");

        dc.aplicarCenarioDeitar(idCasa);
        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("LAMP-1").getEstado(),
            "cenario deitar liga quarto");
        assertCondition(!dc.obterCasa(idCasa).getDispositivos().get("RELE-1").getEstado(),
            "cenario deitar desliga sala");

        dc.aplicarCenarioAcordar(idCasa);
        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("LAMP-1").getEstado(),
            "cenario acordar mantem quarto ligado");
        assertCondition(dc.obterCasa(idCasa).getDispositivos().get("RELE-COZ-1").getEstado(),
            "cenario acordar liga cozinha");

        System.out.println("\nTeste completo concluido com sucesso: base, estatisticas, persistencia, automacoes, escalonamentos e cenarios.");
    }
}
