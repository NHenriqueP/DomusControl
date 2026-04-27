# Repositório POO 25/26

João Pedro Cunha Cerqueira - A111753 - Cerqueira4618
Rafael Figueiras Esteves - A112032 - a112032
Nuno Henrique Dias Pereira - A110067 - NHenriqueP

## Como Compilar e Testar o Programa

Para compilar todos os ficheiros Java do projeto, abra o terminal na pasta raiz do projeto e execute:

```bash
javac Main.java casas/*.java controlador/*.java dispositivos/*.java automacoes/*.java
```

Depois de compilar com sucesso, pode iniciar a aplicação interativa executando:

```bash
java Main
```

O programa irá arrancar com os menus da interface de texto (`TextUI`) onde pode interagir com as várias funcionalidades da *SmartHome*.

### Limpeza de Ficheiros Compilados
Caso queira apagar todos os ficheiros `.class` gerados e limpar a sua área de trabalho do projeto, pode executar o seguinte comando:

```bash
find . -name "*.class" -delete
```
