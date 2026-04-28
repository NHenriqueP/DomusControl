JC = javac
JVM = java

# Procura todos os ficheiros .java recursivamente
SOURCES = $(shell find . -name "*.java")

.PHONY: all run clean test

# 'make' ou 'make all' compilam o projeto
all:
	$(JC) -encoding UTF-8 $(SOURCES)

# 'make run' compila e executa a interface de utilizador
run: all
	$(JVM) UI.Main

# 'make test' executa os testes de requisitos
test: all
	$(JVM) UI.TesteRequisitosBaseGestao

# 'make clean' apaga todos os ficheiros .class compilados
clean:
	find . -name "*.class" -delete
