package com.banco;

public class TransferenciaCommand implements Comando {
    private ContaBancaria origem;
    private ContaBancaria destino;
    private double valor;
    private boolean executada = false;

    public TransferenciaCommand(ContaBancaria origem, ContaBancaria destino, double valor) {
        this.origem = origem;
        this.destino = destino;
        this.valor = valor;
    }

    @Override
    public void executar() {
        origem.sacar(valor);      // Se não houver saldo, lança exceção aqui e não deposita no destino
        destino.depositar(valor);
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

    public String getDescricao() {
        return "Transferência de R$" + valor + " (" + origem.getTitular() + " -> " + destino.getTitular() + ")";
    }
}