package com.banco;

public class TransferenciaCommand implements Comando {
    private final ContaBancaria origem;
    private final ContaBancaria destino;
    private final double valor;
    private boolean executada = false;

    public TransferenciaCommand(ContaBancaria origem, ContaBancaria destino, double valor) {
        this.origem = origem;
        this.destino = destino;
        this.valor = valor;
    }

    @Override
    public void executar() {
        origem.sacar(valor);
        try {
            destino.depositar(valor);
        } catch (RuntimeException e) {
            origem.depositar(valor); // rollback: não perde dinheiro se o depósito falhar
            throw e;
        }
        executada = true;
    }

    @Override
    public void desfazer() {
        if (executada) {
            destino.sacar(valor);
            origem.depositar(valor);
            executada = false;
        }
    }

    @Override
    public String getDescricao() {
        return String.format("Transferência de R$%.2f (%s -> %s)",
                valor, origem.getTitular(), destino.getTitular());
    }
}
