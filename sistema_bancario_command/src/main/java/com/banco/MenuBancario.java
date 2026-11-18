package com.banco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Client do padrão Command: interface de console com o usuário.
 * Lê as opções, cria os comandos (depósito, saque, transferência)
 * e os entrega ao Invoker {@link CaixaEletronico}.
 */
public class MenuBancario {
    private final BufferedReader entrada;
    private final PrintStream saida;
    private final CaixaEletronico caixa = new CaixaEletronico();
    private final Map<String, ContaBancaria> contas = new LinkedHashMap<>();

    public MenuBancario(InputStream in, PrintStream out) {
        this.entrada = new BufferedReader(new InputStreamReader(in));
        this.saida = out;
    }

    /** Laço principal: roda até o usuário escolher 0 ou a entrada acabar. */
    public void iniciar() {
        saida.println("=== SISTEMA BANCÁRIO (Padrão Command) ===");
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            String opcao = lerLinha("Escolha uma opção: ");
            if (opcao == null) {
                break; // fim da entrada (Ctrl+D / Ctrl+Z)
            }
            switch (opcao.trim()) {
                case "1" -> criarConta();
                case "2" -> depositar();
                case "3" -> sacar();
                case "4" -> transferir();
                case "5" -> consultarSaldos();
                case "6" -> desfazer();
                case "7" -> refazer();
                case "8" -> verHistorico();
                case "0" -> continuar = false;
                default -> saida.println("Opção inválida. Digite um número do menu.");
            }
        }
        saida.println("Encerrando. Até logo!");
    }

    /** Usado nos testes para consultar uma conta criada pelo menu. */
    public ContaBancaria getConta(String titular) {
        return contas.get(chave(titular));
    }

    // ---------- opções do menu ----------

    private void mostrarMenu() {
        saida.println();
        saida.println("----------------- MENU -----------------");
        saida.println("1 - Criar conta");
        saida.println("2 - Depositar");
        saida.println("3 - Sacar");
        saida.println("4 - Transferir");
        saida.println("5 - Consultar saldos");
        saida.println("6 - Desfazer última operação");
        saida.println("7 - Refazer operação desfeita");
        saida.println("8 - Ver histórico");
        saida.println("0 - Sair");
    }

    private void criarConta() {
        String nome = lerLinha("Nome do titular: ");
        if (nome == null) {
            return;
        }
        nome = nome.trim();
        if (nome.isEmpty()) {
            saida.println("O nome não pode ser vazio.");
            return;
        }
        if (contas.containsKey(chave(nome))) {
            saida.println("Já existe uma conta para " + nome + ".");
            return;
        }
        contas.put(chave(nome), new ContaBancaria(nome));
        saida.println("Conta criada para " + nome + " com saldo R$0,00.");
    }

    private void depositar() {
        ContaBancaria conta = buscarConta("Titular da conta: ");
        if (conta == null) {
            return;
        }
        Double valor = lerValor();
        if (valor == null) {
            return;
        }
        executar(new DepositoCommand(conta, valor), conta);
    }

    private void sacar() {
        ContaBancaria conta = buscarConta("Titular da conta: ");
        if (conta == null) {
            return;
        }
        Double valor = lerValor();
        if (valor == null) {
            return;
        }
        executar(new SaqueCommand(conta, valor), conta);
    }

    private void transferir() {
        ContaBancaria origem = buscarConta("Titular da conta de origem: ");
        if (origem == null) {
            return;
        }
        ContaBancaria destino = buscarConta("Titular da conta de destino: ");
        if (destino == null) {
            return;
        }
        if (origem == destino) {
            saida.println("Origem e destino devem ser contas diferentes.");
            return;
        }
        Double valor = lerValor();
        if (valor == null) {
            return;
        }
        executar(new TransferenciaCommand(origem, destino, valor), origem, destino);
    }

    private void consultarSaldos() {
        if (contas.isEmpty()) {
            saida.println("Nenhuma conta criada ainda.");
            return;
        }
        contas.values().forEach(c ->
                saida.printf("%s: R$%.2f%n", c.getTitular(), c.getSaldo()));
    }

    private void desfazer() {
        if (!caixa.podeDesfazer()) {
            saida.println("Não há nenhuma operação para desfazer.");
            return;
        }
        try {
            caixa.desfazerUltimo();
            saida.println(ultimaLinhaDoHistorico());
        } catch (IllegalArgumentException e) {
            saida.println("Não foi possível desfazer: " + e.getMessage());
        }
    }

    private void refazer() {
        if (!caixa.podeRefazer()) {
            saida.println("Não há nenhuma operação para refazer.");
            return;
        }
        try {
            caixa.refazerUltimo();
            saida.println(ultimaLinhaDoHistorico());
        } catch (IllegalArgumentException e) {
            saida.println("Não foi possível refazer: " + e.getMessage());
        }
    }

    private void verHistorico() {
        List<String> historico = caixa.getHistorico();
        if (historico.isEmpty()) {
            saida.println("Nenhuma operação registrada.");
            return;
        }
        saida.println("--- Histórico ---");
        for (int i = 0; i < historico.size(); i++) {
            saida.println((i + 1) + ". " + historico.get(i));
        }
    }

    // ---------- auxiliares ----------

    /** Entrega o comando ao Invoker e mostra o resultado ou o erro. */
    private void executar(Comando comando, ContaBancaria... afetadas) {
        try {
            caixa.executar(comando);
            saida.println("OK: " + comando.getDescricao());
            for (ContaBancaria c : afetadas) {
                saida.printf("Saldo de %s: R$%.2f%n", c.getTitular(), c.getSaldo());
            }
        } catch (IllegalArgumentException e) {
            saida.println("Operação não realizada: " + e.getMessage());
        }
    }

    private ContaBancaria buscarConta(String rotulo) {
        String nome = lerLinha(rotulo);
        if (nome == null) {
            return null;
        }
        ContaBancaria conta = contas.get(chave(nome));
        if (conta == null) {
            saida.println("Conta não encontrada: " + nome.trim());
        }
        return conta;
    }

    /** Lê um valor positivo; aceita vírgula ou ponto como separador decimal. */
    private Double lerValor() {
        String texto = lerLinha("Valor: ");
        if (texto == null) {
            return null;
        }
        texto = texto.trim();
        if (!texto.matches("\\d+([.,]\\d+)?")) {
            saida.println("Valor inválido. Use um número positivo, ex.: 100 ou 49,90.");
            return null;
        }
        return Double.parseDouble(texto.replace(',', '.'));
    }

    private String lerLinha(String prompt) {
        saida.print(prompt);
        saida.flush();
        try {
            return entrada.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    private String ultimaLinhaDoHistorico() {
        List<String> h = caixa.getHistorico();
        return h.get(h.size() - 1);
    }

    private static String chave(String nome) {
        return nome.trim().toLowerCase();
    }
}
