package com.banco;

/**
 * Interface Command: todo comando bancário sabe executar, desfazer
 * e descrever a si mesmo (usada no histórico).
 */
public interface Comando {
    void executar();
    void desfazer();
    String getDescricao();
}
