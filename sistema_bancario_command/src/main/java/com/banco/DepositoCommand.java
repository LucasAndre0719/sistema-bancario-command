package com.banco;

public class DepositoCommand implements Comando {
    private final ContaBancaria conta;
    private final double valor;
    private boolean executado = false;

    public DepositoCommand(ContaBancaria conta, double valor) {
        this.conta = conta;
        this.valor = valor;
    }

    @Override
    public void executar() {
        conta.depositar(valor);
        executado = true;
    }

    @Override
    public void desfazer() {
        if (executado) {
            conta.sacar(valor);
            executado = false;
        }
    }

    @Override
    public String getDescricao() {
        return String.format("Depósito de R$%.2f na conta de %s", valor, conta.getTitular());
    }
}
