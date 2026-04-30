# Repositório POO 25/26

João Pedro Cunha Cerqueira - A111753 - Cerqueira4618
Rafael Figueiras Esteves - A112032 - Rafabass1
Nuno Henrique Dias Pereira - A110067 - NHenriqueP

## Como Compilar e Testar o Programa

Para facilitar o processo, o projeto possui uma `Makefile`. Abra o terminal na pasta raiz do projeto e pode utilizar os seguintes comandos:

- **Para compilar o código:**
  ```bash
  make
  ```

- **Para compilar e iniciar logo o programa de imediato:**
  ```bash
  make run
  ```

- **Para executar os testes de requisitos:**
  ```bash
  make test
  ```

- **Para executar os testes unitários JUnit:**
  ```bash
  make junit
  ```

- **Para limpar ficheiros compilados:**
  ```bash
  make clean
  ```

## Sugestões Inteligentes

O sistema inclui um motor de sugestões automáticas que analisa os padrões de uso dos dispositivos em todas as casas e gera recomendações ao utilizador. As sugestões são acessíveis no menu principal (opção 6).

São gerados três tipos de sugestões, com base nas seguintes condições:

| Tipo | Condição | Descrição |
| **AUTOMACAO** | Dispositivo ligado/desligado ≥ 5 vezes | Sugere a criação de uma automação para ativar o dispositivo automaticamente, reduzindo operações manuais repetitivas. |
| **ESCALONAMENTO** | Dispositivo com ≥ 10 horas de uso acumulado | Sugere agendar um ligar/desligar automático, para que o dispositivo não fique ligado indefinidamente. |
| **EFICIENCIA** | Dispositivo ligado com consumo ≥ 50 Wh | Alerta o utilizador para um dispositivo com consumo energético elevado que está atualmente ligado, sugerindo que o desligue quando não estiver em uso. |

As sugestões são apresentadas por ordem de relevância (maior número de ativações primeiro).