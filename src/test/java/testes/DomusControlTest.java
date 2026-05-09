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

    @Test
    public void testGravarECarregarEstado() throws Exception {
        String ficheiro = "teste_estado.bin";

        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9.0, false, 50, true, 3000), "Sala");
        model.ligarDispositivo(idCasa, EMAIL, "L1");

        model.guardarEstadoBinario(ficheiro);
        DomusControl carregado = DomusControl.lerEstadoBinario(ficheiro);

        assertNotNull(carregado.obterCasa(idCasa));
        assertTrue(carregado.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
        assertTrue(carregado.utilizadorExiste(EMAIL));
        new File(ficheiro).delete();
    }

    @Test
    public void testAutomacaoExecutaQuandoCondicaoVerdadeira() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, true), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, false, 50, false, 2700), "Quarto");

        Automacao auto = new Automacao("Auto1", "Teste",
            casa -> casa.getDispositivos().get("R1") != null && casa.getDispositivos().get("R1").getEstado(),
            casa -> casa.ligarDispositivosDivisao("Quarto"));

        model.associarAutomacaoCasa(idCasa, EMAIL, auto);
        model.executarAutomacoesCasa(idCasa);

        assertTrue(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
    }

    @Test
    public void testAutomacaoNaoExecutaQuandoCondicaoFalsa() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, false, 50, false, 2700), "Quarto");

        Automacao auto = new Automacao("Auto1", "Teste",
            casa -> casa.getDispositivos().get("R1") != null && casa.getDispositivos().get("R1").getEstado(),
            casa -> casa.ligarDispositivosDivisao("Quarto"));

        model.associarAutomacaoCasa(idCasa, EMAIL, auto);
        model.executarAutomacoesCasa(idCasa);

        assertFalse(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
    }

    @Test
    public void testEscalonamentoExecutaNoInstanteCerto() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        LocalDateTime instante = model.getTempoAtual().plusHours(2);
        model.criarEscalonamento("Ligar Sala", idCasa, instante,
            casa -> casa.ligarDispositivosDivisao("Sala"));

        assertEquals(0, model.executarEscalonamentosPendentes());
        model.avancarHoras(2);
        assertEquals(1, model.executarEscalonamentosPendentes());
        assertTrue(model.obterCasa(idCasa).getDispositivos().get("R1").getEstado());
    }

    @Test
    public void testEscalonamentoNaoReexecuta() {
        model.criarEscalonamento("Teste", idCasa, model.getTempoAtual().plusHours(1),
            casa -> casa.ligarDispositivosDivisao("Sala"));

        model.avancarHoras(1);
        assertEquals(1, model.executarEscalonamentosPendentes());

        assertEquals(0, model.executarEscalonamentosPendentes());
    }

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

        Cenario meuCenario = new Cenario("Modo Trabalho", "Liga escritório");
        meuCenario.adicionarAcao(casa -> casa.desligarDispositivosDivisao("Quarto"));
        meuCenario.adicionarAcao(casa -> casa.ligarDispositivosDivisao("Sala"));
        model.criarCenario(meuCenario);

        model.aplicarCenario(meuCenario.getId(), idCasa);

        assertTrue(model.obterCasa(idCasa).getDispositivos().get("R1").getEstado());
        assertFalse(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
    }

    @Test
    public void testCenarioJantarComAmigosLigaSalaECozinha() {
        model.adicionarDivisaoACasa(idCasa, EMAIL, "Cozinha");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, false, 50, false, 2700), "Quarto");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new ColunaSom("CS1", "Sonos", "One", 8, false, 10), "Cozinha");

        model.aplicarCenarioJantarComAmigos(idCasa);

        assertTrue(model.obterCasa(idCasa).getDispositivos().get("R1").getEstado());
        assertTrue(model.obterCasa(idCasa).getDispositivos().get("CS1").getEstado());
        assertFalse(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
    }

    @Test
    public void testCenarioDeitarLigaQuartoDesligaResto() {
        model.adicionarDivisaoACasa(idCasa, EMAIL, "Cozinha");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, true), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, true, 50, false, 2700), "Quarto");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new ColunaSom("CS1", "Sonos", "One", 8, true, 10), "Cozinha");

        model.aplicarCenarioDeitar(idCasa);

        assertFalse(model.obterCasa(idCasa).getDispositivos().get("R1").getEstado());
        assertTrue(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
        assertFalse(model.obterCasa(idCasa).getDispositivos().get("CS1").getEstado());
    }

    @Test
    public void testCenarioAcordarLigaQuartoECozinha() {
        model.adicionarDivisaoACasa(idCasa, EMAIL, "Cozinha");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, false, 50, false, 2700), "Quarto");
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new ColunaSom("CS1", "Sonos", "One", 8, false, 10), "Cozinha");

        model.aplicarCenarioAcordar(idCasa);

        assertFalse(model.obterCasa(idCasa).getDispositivos().get("R1").getEstado());
        assertTrue(model.obterCasa(idCasa).getDispositivos().get("L1").getEstado());
        assertTrue(model.obterCasa(idCasa).getDispositivos().get("CS1").getEstado());
    }

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

    @Test
    public void testPortaoGaragemPercentagemInvalida() {
        assertThrows(IllegalArgumentException.class, () ->
            new PortaoGaragem("P1", "Nice", "Mod", 20, false, -1));
    }

    @Test
    public void testPortaoGaragemAbrirFechar() {
        PortaoGaragem portao = new PortaoGaragem("P1", "Nice", "Mod", 20, false, 0);

        portao.abrirPortao();
        assertEquals(100, portao.getPercentagemAbertura());

        portao.fecharPortao();
        assertEquals(0, portao.getPercentagemAbertura());
    }

    @Test
    public void testReleCloneIgual() {
        Rele r1 = new Rele("R1", "Shelly", "Plus1", 10, false);
        Rele r2 = (Rele) r1.clone();

        assertEquals(r1, r2);
        assertNotSame(r1, r2);
    }

    @Test
    public void testArCondicionadoValidacoes() {
        ArCondicionado ac = new ArCondicionado("AC1", "LG", "X", 1.0, false, 3, "FRIO", 22.0);

        ac.setVelocidade(0);
        assertEquals(3, ac.getVelocidade());

        ac.setModo("INVALIDO");
        assertEquals("FRIO", ac.getModo());

        ac.setTemperatura(40.0);
        assertEquals(22.0, ac.getTemperatura(), 0.01);
    }

    @Test
    public void testSistemaDeRegaValidacoes() {
        SistemaDeRega sr = new SistemaDeRega("SR1", "Rain", "Pro", 2.0, false, 50, 10, "Relvado");

        sr.setPercentagemIrrigacao(200);
        assertEquals(50, sr.getPercentagemIrrigacao());

        sr.setDuracao(0);
        assertEquals(10, sr.getDuracao());

        sr.setZona(null);
        assertEquals("Relvado", sr.getZona());
    }

    @Test
    public void testSmartTVValidacoes() {
        SmartTV tv = new SmartTV("TV1", "Sony", "Bravia", 3.0, false, 20, 3, "HDMI-1");

        tv.setVolume(-1);
        assertEquals(20, tv.getVolume());

        tv.setCanalAtual(0);
        assertEquals(3, tv.getCanalAtual());

        tv.setFonteEntrada("USB");
        assertEquals("HDMI-1", tv.getFonteEntrada());
    }

    @Test
    public void testFechaduraPinETroca() {
        FechaduraInteligente f = new FechaduraInteligente("F1", "Yale", "X", 1.0, false, true, "1234");

        assertTrue(f.getEstadoTranca());
        assertFalse(f.destrancar("0000"));
        assertTrue(f.getEstadoTranca());

        assertTrue(f.destrancar("1234"));
        assertFalse(f.getEstadoTranca());

        f.trancar();
        assertTrue(f.getEstadoTranca());

        assertFalse(f.alterarCodigo("0000", "5678"));
        assertEquals("1234", f.getCodigoAcesso());

        assertTrue(f.alterarCodigo("1234", "5678"));
        assertEquals("5678", f.getCodigoAcesso());
    }

    @Test
    public void testOperarSobreDispositivoCortina() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Cortina("C1", "Somfy", "IO", 5, false, 0), "Sala");

        model.operarSobreDispositivo(idCasa, EMAIL, "C1", d -> ((Cortina) d).abrirCortina());

        Cortina cortina = (Cortina) model.obterDispositivoCasa(idCasa, "C1");
        assertEquals(100, cortina.getPercentagemAbertura());
    }

    @Test
    public void testOperarSobreDispositivoLampadaComCor() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, false, 50, true, 3000), "Sala");

        model.operarSobreDispositivo(idCasa, EMAIL, "L1", d -> ((Lampada) d).setTemperaturaCor(3500));

        Lampada lampada = (Lampada) model.obterDispositivoCasa(idCasa, "L1");
        assertEquals(3500, lampada.getTemperaturaCor());
    }

    @Test
    public void testOperarSobreDispositivoLampadaSemCorLancaExcecao() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Lampada("L1", "Philips", "Hue", 9, false, 50, false, 2700), "Sala");

        assertThrows(IllegalStateException.class, () ->
            model.operarSobreDispositivo(idCasa, EMAIL, "L1", d -> ((Lampada) d).setTemperaturaCor(3500)));
    }

    @Test
    public void testOperarSobreDispositivoPortaoPercentagemIntermedia() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new PortaoGaragem("P1", "Nice", "Mod", 20, false, 0), "Sala");

        model.operarSobreDispositivo(idCasa, EMAIL, "P1", d -> ((PortaoGaragem) d).setPercentagemAbertura(40));

        PortaoGaragem portao = (PortaoGaragem) model.obterDispositivoCasa(idCasa, "P1");
        assertEquals(40, portao.getPercentagemAbertura());
    }

    @Test
    public void testOperarSobreDispositivoArCondicionadoModo() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new ArCondicionado("AC1", "LG", "X", 1.0, false, 3, "AUTO", 24.0), "Sala");

        model.operarSobreDispositivo(idCasa, EMAIL, "AC1", d -> ((ArCondicionado) d).setModo("FRIO"));

        ArCondicionado ac = (ArCondicionado) model.obterDispositivoCasa(idCasa, "AC1");
        assertEquals("FRIO", ac.getModo());
    }

    @Test
    public void testOperarSobreDispositivoSmartTVCanal() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new SmartTV("TV1", "Sony", "Bravia", 3.0, false, 20, 3, "HDMI-1"), "Sala");

        model.operarSobreDispositivo(idCasa, EMAIL, "TV1", d -> ((SmartTV) d).setCanalAtual(10));

        SmartTV tv = (SmartTV) model.obterDispositivoCasa(idCasa, "TV1");
        assertEquals(10, tv.getCanalAtual());
    }

    @Test
    public void testAutomacaoDesativadaNaoExecuta() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        Automacao auto = new Automacao("AutoOff", "Nao executa",
            casa -> true,
            casa -> casa.ligarDispositivosDivisao("Sala"));
        auto.setAtiva(false);

        model.associarAutomacaoCasa(idCasa, EMAIL, auto);
        model.executarAutomacoesCasa(idCasa);

        assertFalse(model.obterCasa(idCasa).getDispositivos().get("R1").getEstado());
    }

    @Test
    public void testCloneDomusControlIndependente() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        DomusControl clone = model.clone();
        assertEquals(model, clone);
        assertNotSame(model, clone);

        clone.criarUtilizador("Outro", "outro@mail.pt");
        assertFalse(model.utilizadorExiste("outro@mail.pt"));
    }

    @Test
    public void testSugestaoGeradaParaDispositivoMuitoUsado() {
        model.adicionarDispositivoACasa(idCasa, EMAIL,
            new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

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

        model.ligarDispositivo(idCasa, EMAIL, "R1");
        model.desligarDispositivo(idCasa, EMAIL, "R1");
        model.ligarDispositivo(idCasa, EMAIL, "R1");
        model.desligarDispositivo(idCasa, EMAIL, "R1");

        java.util.List<sugestoes.Sugestao> sugestoes = model.gerarSugestoes();
        assertTrue(sugestoes.stream().noneMatch(s -> s.getTipo().equals("AUTOMACAO")),
            "Não deve haver sugestão AUTOMACAO para dispositivos pouco usados.");
    }
}
