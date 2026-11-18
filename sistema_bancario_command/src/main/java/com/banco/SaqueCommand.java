package com.banco;

public class SaqueCommand implements Comando {
    private ContaBancaria conta;
    private double valor;
    private boolean executado = false;

    public SaqueCommand(ContaBancaria conta, double valor) {
        this.conta = conta;
        this.valor = valor;
    }

    @Override
    public void executar() {
        conta.sacar(valor);
        executado = true; // Só chega aqui se sacar() não lançar exceção
    }

    @Override
    public void desfazer() {
        if (executado) {
            conta.depositar(valor);
            executado = false;
        }
    }

    public String getDescricao() {
        return "Saque de R$" + valor + " na conta de " + conta.getTitular();
    }
}