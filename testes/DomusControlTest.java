package testes;

import automacoes.Automacao;
import casas.TipoAcessoCasa;
import cenarios.Cenario;
import controlador.DomusControl;
import dispositivos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a classe DomusControl.
 * Cobre: CRUD de entidades, estatísticas, persistência binária,
 * automações, escalonamentos e cenários.
 */
public class DomusControlTest {

    private DomusControl model;
    private String idCasa;
    private static final String EMAIL = "joao@mail.pt";

    @BeforeEach
    public void setUp() {
        model = new DomusControl(LocalDateTime.of(2026, 5, 1, 10, 0));
        model.criarUtilizador("Joao", EMAIL);
        idCasa = model.criarCasa(EMAIL, 4700, "EDP");
        model.adicionarDivisaoACasa(idCasa, EMAIL, "Sala");
        model.adicionarDivisaoACasa(idCasa, EMAIL, "Quarto");
    }

    // ==================== CRUD ====================

    @Test
    public void testCriarUtilizadorComSucesso() {
        model.criarUtilizador("Maria", "maria@mail.pt");
        assertTrue(model.utilizadorExiste("maria@mail.pt"));
        assertNotNull(model.obterUtilizador("maria@mail.pt"));
    }

    @Test
    public void testCriarUtilizadorDuplicadoLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> model.criarUtilizador("Outro", EMAIL));
    }

    @Test
    public void testCriarCasaComEmailInexistenteLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            model.criarCasa("naoexiste@mail.pt", 4700, "EDP"));
    }

    @Test
    public void testRemoverCasa() {
        model.removerCasa(idCasa);
        assertNull(model.obterCasa(idCasa));
    }

    @Test
    public void testRemoverUtilizador() {
        model.removerUtilizador(EMAIL);
        assertFalse(model.utilizadorExiste(EMAIL));
    }

    // ==================== Estatísticas ====================

    @Test
    public void testConsumoAtualZeroSemDispositivosLigados() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 15.0, false), "Sala");
        assertEquals(0.0, model.consumoAtualCasa(idCasa));
    }

    @Test
    public void testConsumoAtualAumentaAoLigar() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 15.0, false, 50, false, 2700), "Sala");

        model.ligarDispositivo(idCasa, EMAIL, "L1");
        assertEquals(15.0, model.consumoAtualCasa(idCasa));
    }

    @Test
    public void testAtivacoesSaoContabilizadas() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10.0, false), "Sala");

        model.ligarDispositivo(idCasa, EMAIL, "R1");
        model.desligarDispositivo(idCasa, EMAIL, "R1");
        model.ligarDispositivo(idCasa, EMAIL, "R1");

        assertEquals(2, model.obterAtivacoesDispositivoCasa(idCasa, "R1"));
    }

    @Test
    public void testHorasUsoCalculadasCorretamente() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10.0, false), "Sala");

        model.ligarDispositivo(idCasa, EMAIL, "R1");
        model.avancarHoras(3);
        model.desligarDispositivo(idCasa, EMAIL, "R1");

        assertEquals(3.0, model.obterHorasUsoDispositivoCasa(idCasa, "R1"), 0.01);
    }

    // ==================== Persistência Binária ====================

    @Test
    public void testGravarECarregarEstado() throws Exception {
        String ficheiro = "teste_estado.bin";

        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9.0, false, 50, true, 3000), "Sala");
        model.ligarDispositivo(idCasa, EMAIL, "L1");

        model.guardarEstadoBinario(ficheiro);
        DomusControl carregado = DomusControl.lerEstadoBinario(ficheiro);

        // Verificar que o estado foi preservado
        assertNotNull(carregado.obterCasa(idCasa));
        assertTrue(carregado.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
        assertTrue(carregado.utilizadorExiste(EMAIL));

        // Limpar ficheiro de teste
        new File(ficheiro).delete();
    }

    // ==================== Automações ====================

    @Test
    public void testAutomacaoExecutaQuandoCondicaoVerdadeira() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, true), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, false, 50, false, 2700), "Quarto");

        // Se R1 estiver ligado, ligar o Quarto
        Automacao auto = new Automacao("Auto1", "Teste",
            casa -> casa.getDispositivos().get("R1") != null && casa.getDispositivos().get("R1").getEstado(),
            casa -> casa.ligarDispositivosDivisao("Quarto"));

        model.associarAutomacaoCasa(idCasa, EMAIL, auto);
        model.executarAutomacoesCasa(idCasa);

        // R1 está ligado, logo a automação deve ter ligado L1
        assertTrue(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
    }

    @Test
    public void testAutomacaoNaoExecutaQuandoCondicaoFalsa() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, false, 50, false, 2700), "Quarto");

        // Se R1 estiver ligado, ligar o Quarto — mas R1 está desligado
        Automacao auto = new Automacao("Auto1", "Teste",
            casa -> casa.getDispositivos().get("R1") != null && casa.getDispositivos().get("R1").getEstado(),
            casa -> casa.ligarDispositivosDivisao("Quarto"));

        model.associarAutomacaoCasa(idCasa, EMAIL, auto);
        model.executarAutomacoesCasa(idCasa);

        // R1 está desligado, logo L1 deve continuar desligado
        assertFalse(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
    }

    // ==================== Escalonamentos ====================

    @Test
    public void testEscalonamentoExecutaNoInstanteCerto() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        LocalDateTime instante = model.getTempoAtual().plusHours(2);
        model.criarEscalonamento("Ligar Sala", idCasa, instante,
            casa -> casa.ligarDispositivosDivisao("Sala"));

        // Ainda não passou o tempo
        assertEquals(0, model.executarEscalonamentosPendentes());

        // Avançar 2 horas
        model.avancarHoras(2);
        assertEquals(1, model.executarEscalonamentosPendentes());

        // O dispositivo deve estar agora ligado
        assertTrue(model.obterCasa(idCasa).getDispositivos().get("R1").getEstado());
    }

    @Test
    public void testEscalonamentoNaoReexecuta() {
        model.criarEscalonamento("Teste", idCasa, model.getTempoAtual().plusHours(1),
            casa -> casa.ligarDispositivosDivisao("Sala"));

        model.avancarHoras(1);
        assertEquals(1, model.executarEscalonamentosPendentes());

        // Executar novamente — não deve reexecutar
        assertEquals(0, model.executarEscalonamentosPendentes());
    }

    // ==================== Cenários ====================

    @Test
    public void testCenarioSairDeCasaDesligaTudo() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, true), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, true, 50, false, 2700), "Quarto");

        model.aplicarCenarioSairDeCasa(idCasa);

        assertEquals(0, model.totalDispositivosLigadosCasa(idCasa));
    }

    @Test
    public void testCenarioDinamicoPersonalizado() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, true, 50, false, 2700), "Quarto");

        // Criar cenário personalizado: desligar Quarto e ligar Sala
        Cenario meuCenario = new Cenario("Modo Trabalho", "Liga escritório");
        meuCenario.adicionarAcao(casa -> casa.desligarDispositivosDivisao("Quarto"));
        meuCenario.adicionarAcao(casa -> casa.ligarDispositivosDivisao("Sala"));
        model.criarCenario(meuCenario);

        model.aplicarCenario(meuCenario.getId(), idCasa);

        assertTrue(model.obterCasa(idCasa).getDispositivos().get("R1").getEstado());
        assertFalse(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
    }

    // ==================== Validações de Dispositivos ====================

    @Test
    public void testLampadaIntensidadeInvalida() {
        assertThrows(IllegalArgumentException.class, () ->
            new Lampada("L1", "Philips", "Hue", 9, false, 150, false, 2700));
    }

    @Test
    public void testColunaSomVolumeInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
            new ColunaSom("CS1", "Sonos", "One", 8, false, -10));
    }

    @Test
    public void testCortinaPorcentagemInvalida() {
        assertThrows(IllegalArgumentException.class, () ->
            new Cortina("C1", "Somfy", "IO", 5, false, 120));
    }

    // ==================== Clone e Encapsulamento do DomusControl ====================

    @Test
    public void testCloneDomusControlIndependente() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        DomusControl clone = model.clone();
        assertEquals(model, clone);
        assertNotSame(model, clone);

        // Modificar o clone não deve afetar o original
        clone.criarUtilizador("Outro", "outro@mail.pt");
        assertFalse(model.utilizadorExiste("outro@mail.pt"));
    }

    // ==================== Sugestões Inteligentes ====================

    @Test
    public void testSugestaoGeradaParaDispositivoMuitoUsado() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        // Ligar e desligar 6 vezes (limiar é 5)
        for (int i = 0; i < 6; i++) {
            model.ligarDispositivo(idCasa, EMAIL, "R1");
            model.desligarDispositivo(idCasa, EMAIL, "R1");
        }

        java.util.List<sugestoes.Sugestao> sugestoes = model.gerarSugestoes();
        assertFalse(sugestoes.isEmpty(), "Devem existir sugestões para dispositivos muito usados.");
        assertTrue(sugestoes.stream().anyMatch(s -> s.getTipo().equals("AUTOMACAO")),
            "Deve haver uma sugestão do tipo AUTOMACAO.");
    }

    @Test
    public void testSemSugestoesParaDispositivoPoucoUsado() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 5, false), "Sala");

        // Apenas 2 ativações (abaixo do limiar)
        model.ligarDispositivo(idCasa, EMAIL, "R1");
        model.desligarDispositivo(idCasa, EMAIL, "R1");
        model.ligarDispositivo(idCasa, EMAIL, "R1");
        model.desligarDispositivo(idCasa, EMAIL, "R1");

        java.util.List<sugestoes.Sugestao> sugestoes = model.gerarSugestoes();
        assertTrue(sugestoes.stream().noneMatch(s -> s.getTipo().equals("AUTOMACAO")),
            "Não deve haver sugestão AUTOMACAO para dispositivos pouco usados.");
    }
}
