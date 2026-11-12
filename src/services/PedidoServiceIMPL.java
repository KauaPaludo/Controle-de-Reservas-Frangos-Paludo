// services/PedidoServiceIMPL.java
package services;

import entities.Cliente;
import entities.Pedido;
import entities.TipoPedido;
import repositories.PedidoRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PedidoServiceIMPL implements PedidoService {
    private final PedidoRepository repository;

    public PedidoServiceIMPL(PedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pedido registrar(Cliente cliente, TipoPedido tipo, LocalDate data, LocalTime horario,
                            double quantidade, String observacao) {
        Pedido pedido = new Pedido(cliente, tipo, data, horario, quantidade, observacao);
        repository.salvar(pedido);
        return pedido;
    }

    @Override
    public List<Pedido> listarPorTipo(TipoPedido tipo) {
        List<Pedido> resultado = listarTodos().stream().
                filter(p->p.getTipo()==tipo).
                collect(Collectors.toList());
        return resultado;

    }

    @Override
    public List<Pedido> listarTodos() {
        return repository.listarTodos();
    }

    @Override
    public List<Pedido> buscarPorNome(String nome) {
            String nomeBusca = nome.trim();
            return repository.listarTodos().stream()
                    .filter(p -> p.getCliente().getName().equalsIgnoreCase(nomeBusca))
                    .collect(Collectors.toList());
        }

    @Override
    public double totalPorTipo(TipoPedido tipo, LocalDate data) {
        return repository.listarTodos().stream()
                .filter(p -> p.getTipo() == tipo)
                .filter(p -> !p.isRetirado())
                .filter(p -> data == null || p.getData().equals(data))
                .mapToDouble(Pedido::getQuantidade)
                .sum();
    }

    @Override
    public void marcarRetirado(int id, boolean retirado) {
        repository.marcarRetirado(id, retirado);
    }
}
