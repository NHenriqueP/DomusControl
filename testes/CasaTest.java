package testes;

import casas.Casa;
import casas.Divisao;
import casas.TipoAcessoCasa;
import dispositivos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilizadores.Utilizador;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CasaTest {

    private Casa casa;
    private Utilizador admin;
    private Utilizador utilizador;
    private Utilizador semAcesso;

    @BeforeEach
    public void setUp() {
        admin = new Utilizador("Admin", "admin@mail.pt");
        utilizador = new Utilizador("User", "user@mail.pt");
        semAcesso = new Utilizador("Intruso", "intruso@mail.pt");

        casa = new Casa("Admin", 4700, "EDP");
        casa.atribuirAcesso(admin, TipoAcessoCasa.ADMINISTRADOR);
        casa.atribuirAcesso(utilizador, TipoAcessoCasa.UTILIZADOR);
    }

    @Test
    public void testAdminPodeCriarDivisao() {
        casa.addDivisao(admin, "Sala");
        assertTrue(casa.getDivisoes().containsKey("Sala"));
    }

    @Test
    public void testUtilizadorNaoPodeCriarDivisao() {
        assertThrows(IllegalStateException.class, () -> casa.addDivisao(utilizador, "Quarto"));
        assertFalse(casa.getDivisoes().containsKey("Quarto"));
    }

    @Test
    public void testUtilizadorSemAcessoNaoPodeLigar() {
        casa.addDivisao(admin, "Sala");
        casa.addDispositivo(admin, new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        assertThrows(IllegalStateException.class, () -> casa.ligarDispositivo(semAcesso, "R1"));
        assertFalse(casa.getDispositivos().get("R1").getEstado());
    }

    @Test
    public void testUtilizadorComAcessoPodeLigar() {
        casa.addDivisao(admin, "Sala");
        casa.addDispositivo(admin, new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        casa.ligarDispositivo(utilizador, "R1");
        assertTrue(casa.getDispositivos().get("R1").getEstado());
    }

    @Test
    public void testAdicionarDispositivoCriaDivisaoSeNaoExistir() {
        casa.addDispositivo(admin, new PortaoGaragem("P1", "Nice", "Mod", 20, false, 0), "Garagem");

        assertTrue(casa.getDivisoes().containsKey("Garagem"));
        assertTrue(casa.getDispositivos().containsKey("P1"));
    }

    @Test
    public void testLigarDesligarDispositivo() {
        casa.addDivisao(admin, "Sala");
        casa.addDispositivo(admin, new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        casa.ligarDispositivo(admin, "R1");
        assertTrue(casa.getDispositivos().get("R1").getEstado());

        casa.desligarDispositivo(admin, "R1");
        assertFalse(casa.getDispositivos().get("R1").getEstado());
    }

    @Test
    public void testLigarDispositivosDivisao() {
        casa.addDivisao(admin, "Sala");
        casa.addDispositivo(admin, new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");
        casa.addDispositivo(admin, new Lampada("L1", "Philips", "Hue", 9, false, 50, false, 2700), "Sala");

        casa.ligarDispositivosDivisao("Sala");

        assertTrue(casa.getDispositivos().get("R1").getEstado());
        assertTrue(casa.getDispositivos().get("L1").getEstado());
    }

    @Test
    public void testDispositivoPolimorfismo() {
        casa.addDivisao(admin, "Sala");
        Dispositivo rele = new Rele("R1", "Shelly", "Plus1", 10, false);
        Dispositivo lampada = new Lampada("L1", "Philips", "Hue", 9, false, 50, true, 3000);
        Dispositivo cortina = new Cortina("C1", "Somfy", "IO", 5, false, 0);
        Dispositivo coluna = new ColunaSom("CS1", "Sonos", "One", 8, false, 50);

        casa.addDispositivo(admin, rele, "Sala");
        casa.addDispositivo(admin, lampada, "Sala");
        casa.addDispositivo(admin, cortina, "Sala");
        casa.addDispositivo(admin, coluna, "Sala");

        casa.ligarDispositivosDivisao("Sala");
        Map<String, Dispositivo> dispositivos = casa.getDispositivos();
        assertEquals(4, dispositivos.size());
        for (Dispositivo d : dispositivos.values()) {
            assertTrue(d.getEstado(), "Dispositivo " + d.getId() + " deveria estar ligado.");
        }
    }

    @Test
    public void testDivisaoAddRemoveDispositivo() {
        Divisao divisao = new Divisao("Sala");

        divisao.addDispositivo("D1");
        divisao.addDispositivo("D1");
        assertTrue(divisao.existeDispositivo("D1"));
        assertEquals(1, divisao.getIdDispositivos().size());

        divisao.removeDispositivo("D1");
        assertFalse(divisao.existeDispositivo("D1"));
    }

    @Test
    public void testCasaOperarSobreDispositivo() {
        casa.addDivisao(admin, "Garagem");
        casa.addDispositivo(admin, new PortaoGaragem("P1", "Nice", "Mod", 20, false, 0), "Garagem");

        casa.operarSobreDispositivo(admin, "P1", d -> ((PortaoGaragem) d).abrirPortao());

        PortaoGaragem portao = (PortaoGaragem) casa.getDispositivos().get("P1");
        assertEquals(100, portao.getPercentagemAbertura());
    }

    @Test
    public void testCasaOperarSobreDispositivoInexistente() {
        assertThrows(IllegalArgumentException.class, () ->
            casa.operarSobreDispositivo(admin, "X1", d -> d.ligarDispositivo()));
    }

    @Test
    public void testGetDispositivosDevolveClone() {
        casa.addDivisao(admin, "Sala");
        casa.addDispositivo(admin, new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");
        Map<String, Dispositivo> copia = casa.getDispositivos();
        copia.get("R1").ligarDispositivo();
        assertFalse(casa.getDispositivos().get("R1").getEstado(),
            "Modificar o clone não deve afetar o dispositivo interno da casa.");
    }

    @Test
    public void testCloneCriaObjetoIndependente() {
        casa.addDivisao(admin, "Sala");
        casa.addDispositivo(admin, new Rele("R1", "Shelly", "Plus1", 10, false), "Sala");

        Casa clone = casa.clone();
        assertEquals(casa, clone);
        assertNotSame(casa, clone);
        clone.addDivisao(admin, "Quarto");
        assertFalse(casa.getDivisoes().containsKey("Quarto"),
            "Modificar o clone não deve afetar a casa original.");
    }

    @Test
    public void testEqualsReflexivoSimetricoTransitivo() {
        Casa casa2 = casa.clone();
        Casa casa3 = casa2.clone();

        assertEquals(casa, casa);
        assertEquals(casa, casa2);
        assertEquals(casa2, casa);
        assertEquals(casa, casa2);
        assertEquals(casa2, casa3);
        assertEquals(casa, casa3);
        assertNotEquals(null, casa);
    }
}
