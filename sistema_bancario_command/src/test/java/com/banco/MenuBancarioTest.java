package com.banco;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class MenuBancarioTest {
    private ByteArrayOutputStream bytes;

    /** Roda o menu com as linhas digitadas pelo "usuário" e devolve o menu para consulta. */
    private MenuBancario roda(String... linhas) {
        String entrada = String.join("\n", linhas) + "\n";
        bytes = new ByteArrayOutputStream();
        MenuBancario menu = new MenuBancario(
                new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(bytes, true, StandardCharsets.UTF_8));
        menu.iniciar();
        return menu;
    }

    private String saida() {
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void criaContaEDeposita() {
        MenuBancario m = roda("1", "Ana", "2", "Ana", "100", "0");
        assertEquals(100, m.getConta("Ana").getSaldo());
    }

    @Test
    void aceitaVirgulaComoSeparadorDecimal() {
        MenuBancario m = roda("1", "Ana", "2", "Ana", "49,90", "0");
        assertEquals(49.90, m.getConta("Ana").getSaldo(), 0.001);
    }

    @Test
    void saqueMaiorQueSaldoNaoAlteraConta() {
        MenuBancario m = roda("1", "Ana", "2", "Ana", "50", "3", "Ana", "80", "0");
        assertEquals(50, m.getConta("Ana").getSaldo());
        assertTrue(saida().contains("Saldo insuficiente"));
    }

    @Test
    void transferenciaDesfazerERefazer() {
        MenuBancario m = roda("1", "Ana", "1", "Bia", "2", "Ana", "100",
                "4", "Ana", "Bia", "30",
                "6",
                "7",
                "0");
        assertEquals(70, m.getConta("Ana").getSaldo());
        assertEquals(30, m.getConta("Bia").getSaldo());
        assertTrue(saida().contains("DESFEITO"));
        assertTrue(saida().contains("REFEITO"));
    }

    @Test
    void desfazerRestauraSaldos() {
        MenuBancario m = roda("1", "Ana", "1", "Bia", "2", "Ana", "100",
                "4", "Ana", "Bia", "30", "6", "0");
        assertEquals(100, m.getConta("Ana").getSaldo());
        assertEquals(0, m.getConta("Bia").getSaldo());
    }

    @Test
    void valorInvalidoERejeitado() {
        MenuBancario m = roda("1", "Ana", "2", "Ana", "abc", "2", "Ana", "-5", "0");
        assertEquals(0, m.getConta("Ana").getSaldo());
        assertTrue(saida().contains("Valor inválido"));
    }

    @Test
    void contaInexistenteEInformada() {
        roda("2", "Fantasma", "0");
        assertTrue(saida().contains("Conta não encontrada"));
    }

    @Test
    void naoPermiteContaDuplicadaIgnorandoMaiusculas() {
        roda("1", "Ana", "1", "ANA", "0");
        assertTrue(saida().contains("Já existe"));
    }

    @Test
    void naoPermiteTransferirParaMesmaConta() {
        MenuBancario m = roda("1", "Ana", "2", "Ana", "100", "4", "Ana", "Ana", "10", "0");
        assertEquals(100, m.getConta("Ana").getSaldo());
        assertTrue(saida().contains("diferentes"));
    }

    @Test
    void desfazerERefazerSemHistoricoAvisam() {
        roda("6", "7", "0");
        assertTrue(saida().contains("nenhuma operação para desfazer"));
        assertTrue(saida().contains("nenhuma operação para refazer"));
    }

    @Test
    void opcaoInvalidaNaoQuebra() {
        roda("9", "xyz", "0");
        assertTrue(saida().contains("Opção inválida"));
    }

    @Test
    void fimDaEntradaEncerraSemErro() {
        assertDoesNotThrow(() -> roda("1", "Ana"));
        assertTrue(saida().contains("Encerrando"));
    }
}
