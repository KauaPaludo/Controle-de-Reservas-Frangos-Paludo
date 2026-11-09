package entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoTest {

    @Test
    void formataFrangoInteiroEMeio() {
        assertEquals("3 un", Pedido.formatarQuantidade(TipoPedido.FRANGO, 3.0));
        assertEquals("1,5 un", Pedido.formatarQuantidade(TipoPedido.FRANGO, 1.5));
    }

    @Test
    void formataCarneEmKg() {
        assertEquals("1,50 kg", Pedido.formatarQuantidade(TipoPedido.CARNE, 1.5));
    }
}