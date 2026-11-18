package com.banco;

public class Main {
    public static void main(String[] args) {
        ContaBancaria conta = new ContaBancaria("Mestre");

        conta.depositar(100);
        conta.sacar(30);
        System.out.println("Saldo: " + conta.getSaldo()); // esperado: 70.0

        try {
            conta.sacar(500);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage()); // esperado: Saldo insuficiente.
        }
    }
}