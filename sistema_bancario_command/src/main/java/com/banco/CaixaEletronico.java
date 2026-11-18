package com.banco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Invoker do padrão Command: executa comandos, guarda o histórico
 * e permite desfazer / refazer operações.
 */
public class CaixaEletronico {
    private final Stack<Comando> pilhaDesfazer = new Stack<>();
    private final Stack<Comando> pilhaRefazer = new Stack<>();
    private final List<String> historico = new ArrayList<>();

    /** Executa o comando. Se falhar, nada é registrado e a exceção sobe. */
    public void executar(Comando comando) {
        comando.executar();
        pilhaDesfazer.push(comando);
        pilhaRefazer.clear(); // nova ação invalida o "refazer"
        historico.add("EXECUTADO: " + comando.getDescricao());
    }

    public boolean desfazerUltimo() {
        if (pilhaDesfazer.isEmpty()) {
            return false;
        }
        Comando comando = pilhaDesfazer.peek();
        comando.desfazer(); // se lançar exceção, o comando continua na pilha
        pilhaDesfazer.pop();
        pilhaRefazer.push(comando);
        historico.add("DESFEITO: " + comando.getDescricao());
        return true;
    }

    public boolean refazerUltimo() {
        if (pilhaRefazer.isEmpty()) {
            return false;
        }
        Comando comando = pilhaRefazer.peek();
        comando.executar();
        pilhaRefazer.pop();
        pilhaDesfazer.push(comando);
        historico.add("REFEITO: " + comando.getDescricao());
        return true;
    }

    public boolean podeDesfazer() {
        return !pilhaDesfazer.isEmpty();
    }

    public boolean podeRefazer() {
        return !pilhaRefazer.isEmpty();
    }

    public List<String> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public void imprimirHistorico() {
        System.out.println("--- Histórico ---");
        historico.forEach(System.out::println);
    }
}
