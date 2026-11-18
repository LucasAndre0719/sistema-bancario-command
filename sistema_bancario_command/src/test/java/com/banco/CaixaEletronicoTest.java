package com.banco;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CaixaEletronicoTest {
    private ContaBancaria a;
    private ContaBancaria b;
    private CaixaEletronico caixa;

    @BeforeEach
    void setUp() {
        a = new ContaBancaria("A");
        b = new ContaBancaria("B");
        caixa = new CaixaEletronico();
    }

    @Test
    void depositoEDesfazer() {
        caixa.executar(new DepositoCommand(a, 100));
        assertEquals(100, a.getSaldo());
        assertTrue(caixa.desfazerUltimo());
        assertEquals(0, a.getSaldo());
    }

    @Test
    void saqueEDesfazer() {
        caixa.executar(new DepositoCommand(a, 100));
        caixa.executar(new SaqueCommand(a, 40));
        assertEquals(60, a.getSaldo());
        caixa.desfazerUltimo();
        assertEquals(100, a.getSaldo());
    }

    @Test
    void transferenciaEDesfazer() {
        caixa.executar(new DepositoCommand(a, 100));
        caixa.executar(new TransferenciaCommand(a, b, 30));
        assertEquals(70, a.getSaldo());
        assertEquals(30, b.getSaldo());
        caixa.desfazerUltimo();
        assertEquals(100, a.getSaldo());
        assertEquals(0, b.getSaldo());
    }

    @Test
    void refazerReaplicaOperacao() {
        caixa.executar(new DepositoCommand(a, 100));
        caixa.desfazerUltimo();
        assertTrue(caixa.refazerUltimo());
        assertEquals(100, a.getSaldo());
    }

    @Test
    void novaAcaoLimpaRefazer() {
        caixa.executar(new DepositoCommand(a, 100));
        caixa.desfazerUltimo();
        caixa.executar(new DepositoCommand(a, 10));
        assertFalse(caixa.podeRefazer());
    }

    @Test
    void comandoInvalidoNaoVaiParaHistorico() {
        assertThrows(IllegalArgumentException.class,
                () -> caixa.executar(new SaqueCommand(a, 50)));
        assertFalse(caixa.podeDesfazer());
        assertTrue(caixa.getHistorico().isEmpty());
    }

    @Test
    void desfazerERefazerSemHistoricoRetornamFalso() {
        assertFalse(caixa.desfazerUltimo());
        assertFalse(caixa.refazerUltimo());
    }

    @Test
    void desfazerSemTerExecutadoNaoAltera() {
        new DepositoCommand(a, 100).desfazer();
        assertEquals(0, a.getSaldo());
    }

    @Test
    void historicoRegistraOperacoes() {
        caixa.executar(new DepositoCommand(a, 100));
        caixa.desfazerUltimo();
        caixa.refazerUltimo();
        assertEquals(3, caixa.getHistorico().size());
        assertTrue(caixa.getHistorico().get(1).startsWith("DESFEITO"));
    }
}
