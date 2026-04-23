package casas;

import automacoes.Automacao;
import dispositivos.Dispositivo;
import utilizadores.Utilizador;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


public class Casa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idCasa;
    private String nomeProprietario;
    private int codigoPostal;
    private String fornecedorEnergia;
    private Map<String, TipoAcessoCasa> acessosUtilizadores;
    
    // Mapeia o ID do dispositivo para o objeto Dispositivo em si
    private Map<String, Dispositivo> dispositivos;
    
    // Mapeia o nome da divisão para o objeto Divisao
    private Map<String, Divisao> divisoes;

    // Mapeia o ID da automacao para o objeto Automacao
    private Map<String, Automacao> automacoes;


    public Casa() {
        this.idCasa = UUID.randomUUID().toString();
        this.nomeProprietario = "";
        this.codigoPostal = 0;
        this.fornecedorEnergia = "";
        this.acessosUtilizadores = new HashMap<>();
        this.dispositivos = new HashMap<>();
        this.divisoes = new HashMap<>();
        this.automacoes = new HashMap<>();
    }

    public Casa(String nomeProprietario, int codigoPostal, String fornecedorEnergia) {
        this.idCasa = UUID.randomUUID().toString();
        this.nomeProprietario = nomeProprietario;
        this.codigoPostal = codigoPostal;
        this.fornecedorEnergia = fornecedorEnergia;
        this.acessosUtilizadores = new HashMap<>();
        this.dispositivos = new HashMap<>();
        this.divisoes = new HashMap<>();
        this.automacoes = new HashMap<>();
    }


    public Casa(Casa umaCasa) {
        this.idCasa = umaCasa.getIdCasa();
        this.nomeProprietario = umaCasa.getNomeProprietario();
        this.codigoPostal = umaCasa.getCodigoPostal();
        this.fornecedorEnergia = umaCasa.getFornecedorEnergia();
        this.acessosUtilizadores = umaCasa.getAcessosUtilizadores();
        this.dispositivos = umaCasa.getDispositivos();
        this.divisoes = umaCasa.getDivisoes();
        this.automacoes = umaCasa.getAutomacoes();
    }



    public String getIdCasa() {
        return idCasa;
    }

    public void setIdCasa(String idCasa) {
        this.idCasa = idCasa;
    }

    public String getNomeProprietario() {
        return nomeProprietario;
    }

    public void setNomeProprietario(String nomeProprietario) {
        this.nomeProprietario = nomeProprietario;
    }

    public int getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(int codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getFornecedorEnergia() {
        return fornecedorEnergia;
    }

    public void setFornecedorEnergia(String fornecedorEnergia) {
        this.fornecedorEnergia = fornecedorEnergia;
    }

    public Map<String, TipoAcessoCasa> getAcessosUtilizadores() {
        return new HashMap<>(this.acessosUtilizadores);
    }

    public void setAcessosUtilizadores(Map<String, TipoAcessoCasa> acessosUtilizadores) {
        this.acessosUtilizadores = new HashMap<>(acessosUtilizadores);
    }


    public Map<String, Dispositivo> getDispositivos() {
        return this.dispositivos.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().clone()
                ));
    }

    public void setDispositivos(Map<String, Dispositivo> dispositivos) {
        this.dispositivos = new HashMap<>(dispositivos);
    }


    public Map<String, Divisao> getDivisoes() {
        Map<String, Divisao> copia = new HashMap<>();
        for (Map.Entry<String, Divisao> entry : this.divisoes.entrySet()) {
            copia.put(entry.getKey(), entry.getValue().clone());
        }
        return copia;
    }

    public void setDivisoes(Map<String, Divisao> divisoes) {
        this.divisoes = new HashMap<>();
        for (Map.Entry<String, Divisao> entry : divisoes.entrySet()) {
            this.divisoes.put(entry.getKey(), entry.getValue().clone());
        }
    }

    public Map<String, Automacao> getAutomacoes() {
        Map<String, Automacao> copia = new HashMap<>();
        for (Map.Entry<String, Automacao> entry : this.automacoes.entrySet()) {
            copia.put(entry.getKey(), entry.getValue().clone());
        }
        return copia;
    }

    public void setAutomacoes(Map<String, Automacao> automacoes) {
        this.automacoes = new HashMap<>();
        for (Map.Entry<String, Automacao> entry : automacoes.entrySet()) {
            this.automacoes.put(entry.getKey(), entry.getValue().clone());
        }
    }


    public void addDivisao(String nomeDivisao) {
        this.divisoes.putIfAbsent(nomeDivisao, new Divisao(nomeDivisao));
    }

    public void addDivisao(Utilizador utilizador, String nomeDivisao) {
        validarAdministrador(utilizador);
        addDivisao(nomeDivisao);
    }


    public void addDispositivo(Dispositivo d, String nomeDivisao) {
        if (!this.divisoes.containsKey(nomeDivisao)) {
            addDivisao(nomeDivisao);
        }
        this.dispositivos.put(d.getId(), d);
        this.divisoes.get(nomeDivisao).addDispositivo(d.getId());
    }

    public void addDispositivo(Utilizador utilizador, Dispositivo d, String nomeDivisao) {
        validarAdministrador(utilizador);
        addDispositivo(d, nomeDivisao);
    }

    public void associarAutomacao(Utilizador utilizador, Automacao automacao) {
        validarAcesso(utilizador);
        this.automacoes.put(automacao.getId(), automacao.clone());
    }

    public void removerAutomacao(Utilizador utilizador, String idAutomacao) {
        validarAcesso(utilizador);
        this.automacoes.remove(idAutomacao);
    }

    public void executarAutomacao(String idAutomacao) {
        Automacao automacao = this.automacoes.get(idAutomacao);
        if (automacao != null) {
            automacao.executar(this);
        }
    }

    public void executarAutomacoes() {
        for (Automacao automacao : this.automacoes.values()) {
            automacao.executar(this);
        }
    }

    public void atribuirAcesso(Utilizador utilizador, TipoAcessoCasa tipoAcesso) {
        this.acessosUtilizadores.put(utilizador.getEmail(), tipoAcesso);
        utilizador.registarAcessoCasa(this.idCasa, tipoAcesso);
    }

    public boolean temAcesso(Utilizador utilizador) {
        return this.acessosUtilizadores.containsKey(utilizador.getEmail());
    }

    public boolean isAdministrador(Utilizador utilizador) {
        return this.acessosUtilizadores.get(utilizador.getEmail()) == TipoAcessoCasa.ADMINISTRADOR;
    }

    public void ligarDispositivo(Utilizador utilizador, String idDispositivo) {
        validarAcesso(utilizador);
        Dispositivo d = this.dispositivos.get(idDispositivo);
        if (d != null) {
            d.ligarDispositivo();
        }
    }

    public void desligarDispositivo(Utilizador utilizador, String idDispositivo) {
        validarAcesso(utilizador);
        Dispositivo d = this.dispositivos.get(idDispositivo);
        if (d != null) {
            d.desligarDispositivo();
        }
    }


    public void ligarDispositivosDivisao(String nomeDivisao) {
        Divisao div = this.divisoes.get(nomeDivisao);
        if (div != null) {
            for (String id : div.getIdDispositivos()) {
                Dispositivo d = this.dispositivos.get(id);
                if (d != null) {
                    d.ligarDispositivo();
                }
            }
        }
    }


    public void desligarDispositivosDivisao(String nomeDivisao) {
        Divisao div = this.divisoes.get(nomeDivisao);
        if (div != null) {
            for (String id : div.getIdDispositivos()) {
                Dispositivo d = this.dispositivos.get(id);
                if (d != null) {
                    d.desligarDispositivo();
                }
            }
        }
    }

    private void validarAdministrador(Utilizador utilizador) {
        if (!isAdministrador(utilizador)) {
            throw new IllegalStateException("Operacao permitida apenas a administradores da casa.");
        }
    }

    private void validarAcesso(Utilizador utilizador) {
        if (!temAcesso(utilizador)) {
            throw new IllegalStateException("Utilizador sem acesso a esta casa.");
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Casa casa = (Casa) o;
        return codigoPostal == casa.codigoPostal &&
            Objects.equals(idCasa, casa.idCasa) &&
                Objects.equals(nomeProprietario, casa.nomeProprietario) &&
                Objects.equals(fornecedorEnergia, casa.fornecedorEnergia) &&
            Objects.equals(acessosUtilizadores, casa.acessosUtilizadores) &&
                Objects.equals(dispositivos, casa.dispositivos) &&
                Objects.equals(divisoes, casa.divisoes) &&
                Objects.equals(automacoes, casa.automacoes);
    }

    @Override
    public Casa clone() {
        return new Casa(this);
    }

    @Override
    public String toString() {
        return "Casa{" +
            "idCasa='" + idCasa + '\'' +
                "Proprietário='" + nomeProprietario + '\'' +
                ", CodigoPostal=" + codigoPostal +
                ", Fornecedor='" + fornecedorEnergia + '\'' +
            ", UtilizadoresComAcesso=" + acessosUtilizadores.size() +
                ", Total Divisões=" + divisoes.size() +
                ", Total Dispositivos=" + dispositivos.size() +
                ", Total Automacoes=" + automacoes.size() +
                '}';
    }
}
