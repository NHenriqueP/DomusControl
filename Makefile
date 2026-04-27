JC = javac
JVM = java

# Procura todos os ficheiros .java
SOURCES = Main.java casas/*.java controlador/*.java dispositivos/*.java automacoes/*.java

.PHONY: all run clean

# 'make' ou 'make all' compilam o projeto
all:
	$(JC) $(SOURCES)

# 'make run' compila (se necessario) e executa o projeto
run: all
	$(JVM) Main

# 'make clean' apaga todos os ficheiros .class compilados
clean:
	find . -name "*.class" -delete
