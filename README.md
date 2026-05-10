# Repositório POO 25/26

João Pedro Cunha Cerqueira - A111753 - Cerqueira4618
Rafael Figueiras Esteves - A112032 - Rafabass1
Nuno Henrique Dias Pereira - A110067 - NHenriqueP


## Como executar

O ponto de entrada da aplicacao e `UI.Main`, e o programa corre em modo interativo no terminal.

Ao iniciar, o menu principal inclui as seguintes areas:

- Gestão de Utilizadores
- Gestão de Casas
- Cenários
- Avançar no Tempo
- Estatísticas
- Sugestões Inteligentes
- Gravar Estado
- Carregar Estado

Dentro de cada menu temos varias opções, ex: 

**Gestão de Utilizadores**
1. Adicionar Utilizador
2. Listar Utilizadores
3. Remover Utilizador
4. Atualizar Utilizador
5. Gerir Utilizador
6. Sair

**Gestão de Casas**
1. Adicionar Casa
2. Listar Casas
3. Remover Casa
4. Atualizar Casa
5. Gerir Casa Específica
6. Sair

**Cenários**
1. Listar Cenários
2. Criar Cenário Personalizado
3. Remover Cenário
4. Sair

**Avançar no Tempo**
1. Avançar X horas
2. Avançar até instante específico
3. Sair

**Estatísticas**
1. Estatísticas do Sistema
2. Estatísticas da Casa
3. Sair

**Sugestões Inteligentes**
1. Sugestões Automáticas
2. Sair

**Gravar Estado**
1. Gravar Estado
2. Sair

**Carregar Estado**
1. Carregar Estado
2. Sair


## Persistencia de estado

O estado da aplicacao pode ser gravado e carregado a partir do ficheiro `estado_domus.bin` no diretorio do projeto.


### Utilizando Gradle

Alternativamente, se tiver o Gradle instalado, pode gerir o projeto com os seguintes comandos:

- **Para compilar:**
  ```bash
  ./gradlew build -x test
  ```

- **Para iniciar o programa:**
  ```bash
  ./gradlew run
  ```

- **Para executar os testes unitários JUnit:**
  ```bash
  ./gradlew test
  ```

- **Para limpar os ficheiros de build:**
  ```bash
  ./gradlew clean
  ```

## Sugestões Inteligentes

O sistema inclui um motor de sugestões automáticas que analisa os padrões de uso dos dispositivos em todas as casas e gera recomendações ao utilizador. As sugestões são acessíveis no menu principal (opção 6).

São gerados três tipos de sugestões, com base nas seguintes condições:

| Tipo | Condição | Descrição |
| **AUTOMACAO** | Dispositivo ligado/desligado ≥ 5 vezes | Sugere a criação de uma automação para ativar o dispositivo automaticamente, reduzindo operações manuais repetitivas. |
| **ESCALONAMENTO** | Dispositivo com ≥ 10 horas de uso acumulado | Sugere agendar um ligar/desligar automático, para que o dispositivo não fique ligado indefinidamente. |
| **EFICIENCIA** | Dispositivo ligado com consumo ≥ 50 Wh | Alerta o utilizador para um dispositivo com consumo energético elevado que está atualmente ligado, sugerindo que o desligue quando não estiver em uso. |

As sugestões são apresentadas por ordem de relevância (maior número de ativações primeiro).