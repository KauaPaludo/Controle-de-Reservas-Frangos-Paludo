package repositories;

import entities.Pedido;

import java.util.List;

public interface PedidoRepository {
    void salvar(Pedido pedido);
    List<Pedido> listarTodos();
    void marcarRetirado(int id, boolean retirado);
}