// services/PedidoService.java
package services;

import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PedidoService {
    /** quantidade: unidades para FRANGO, kg para CARNE. */
    Pedido registrar(Cliente cliente, TipoPedido tipo, LocalDate data, LocalTime horario,
                     double quantidade, String observacao);
    List<Pedido> listarPorTipo(TipoPedido tipo);
    List<Pedido> listarTodos();
    List<Pedido> buscarPorNome(String nome);
    /** Total ainda não retirado (unidades ou kg). Com data = null, soma todos os dias. */
    double totalPorTipo(TipoPedido tipo, LocalDate data);
    void marcarRetirado(int id, boolean retirado);
}
