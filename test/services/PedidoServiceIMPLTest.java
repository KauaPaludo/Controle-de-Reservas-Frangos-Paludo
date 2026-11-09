package services;

import database.Conexao;
import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repositories.PedidoRepositoryJDBC;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoServiceIMPLTest {

    private static final LocalDate DIA = LocalDate.of(2026, 11, 10);
    private static final LocalTime HORA = LocalTime.of(12, 0);

    private PedidoService service;

    @BeforeEach
    void preparar(@TempDir Path pasta) {
        System.setProperty("pedidos.db", pasta.resolve("teste.db").toString());
        Conexao.criarTabelas();
        service = new PedidoServiceIMPL(new PedidoRepositoryJDBC());
    }

    @AfterEach
    void limpar() {
        System.clearProperty("pedidos.db");
    }

    private void frango(String nome, double qtd) {
        service.registrar(new Cliente(nome, ""), TipoPedido.FRANGO, DIA, HORA, qtd, "Sem observações");
    }

    @Test
    void guardaMeioFrangoSemArredondar() {
        frango("Maria", 1.5);

        List<Pedido> lista = service.listarPorTipo(TipoPedido.FRANGO);
        assertEquals(1, lista.size());
        assertEquals(1.5, lista.get(0).getQuantidade());
    }

    @Test
    void totalSomaMeiosEIgnoraRetirados() {
        frango("Ana", 1.5);
        frango("Bia", 2);
        frango("Caio", 1);
        assertEquals(4.5, service.totalPorTipo(TipoPedido.FRANGO, DIA));

        int idCaio = service.buscarPorNome("Caio").get(0).getId();
        service.marcarRetirado(idCaio, true);
        assertEquals(3.5, service.totalPorTipo(TipoPedido.FRANGO, DIA));
    }

    @Test
    void totalConsideraSoOsPedidosDoDia() {
        frango("Ana", 1.5);
        service.registrar(new Cliente("Bia", ""), TipoPedido.FRANGO,
                DIA.plusDays(1), HORA, 2, "Sem observações");

        assertEquals(1.5, service.totalPorTipo(TipoPedido.FRANGO, DIA));
        assertEquals(3.5, service.totalPorTipo(TipoPedido.FRANGO, null));
    }
}