package automacoes;
import casas.Casa;
import java.io.Serializable;
public interface CondicaoAutomacao extends Serializable {
    boolean verificar(Casa casa);
}
