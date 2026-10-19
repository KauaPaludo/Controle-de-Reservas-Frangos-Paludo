// services/PedidoService.java  (agora é interface)
package services;

import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;

import java.time.LocalTime;
import java.util.List;

public interface PedidoService {
    Pedido registrar(Cliente cliente, TipoPedido tipo, LocalTime horario, int quant, String aob);
    List<Pedido> listarPorTipo(TipoPedido tipo);
    List<Pedido> listarTodos();
    List<Pedido> buscarPorNome(String nome);
    int totalPorTipo(TipoPedido tipo);
    void marcarRetirado(int id, boolean retirado);
}