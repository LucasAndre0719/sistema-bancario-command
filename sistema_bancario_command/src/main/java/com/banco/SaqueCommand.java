package com.banco;

public class SaqueCommand implements Comando {
    private final ContaBancaria conta;
    private final double valor;
    private boolean executado = false;

    public SaqueCommand(ContaBancaria conta, double valor) {
        this.conta = conta;
        this.valor = valor;
    }

    @Override
    public void executar() {
        conta.sacar(valor);
        executado = true;
    }

    @Override
    public void desfazer() {
        if (executado) {
            conta.depositar(valor);
            executado = false;
        }
    }

    @Override
    public String getDescricao() {
        return String.format("Saque de R$%.2f na conta de %s", valor, conta.getTitular());
    }
}
