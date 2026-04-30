JC = javac
JVM = java

# Procura todos os ficheiros .java recursivamente (exceto testes JUnit)
SOURCES = $(shell find . -name "*.java" -not -path "./testes/*")

.PHONY: all run clean test junit

# 'make' ou 'make all' compilam o projeto
all:
	$(JC) -encoding UTF-8 $(SOURCES)

# 'make run' compila e executa a interface de utilizador
run: all
	$(JVM) UI.Main

# 'make test' executa os testes de requisitos
test: all
	$(JVM) UI.TesteRequisitosBaseGestao

# 'make junit' compila e corre os testes JUnit
junit: all
	$(JC) -encoding UTF-8 -cp ".:lib/*" testes/*.java
	$(JVM) -jar lib/junit-platform-console-standalone.jar --class-path . --scan-classpath

# 'make clean' apaga todos os ficheiros .class compilados
clean:
	find . -name "*.class" -delete
