package com.banco;

public class DepositoCommand implements Comando {
	private ContaBancaria conta;
	private double valor;
	
	
	public DepositoCommand(ContaBancaria conta, double valor) {
		this.conta = conta;
		this.valor = valor;
		
	}



	
	public void executar() {
		conta.depositar(valor);
	}
	public void desfazer() {
		conta.sacar(valor);
	}
	

}
