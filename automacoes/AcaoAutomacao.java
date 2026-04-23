package automacoes;

import casas.Casa;
import java.io.Serializable;

public interface AcaoAutomacao extends Serializable {
    void executar(Casa casa);
}
