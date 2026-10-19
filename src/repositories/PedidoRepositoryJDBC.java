// repositories/PedidoRepositoryJDBC.java
package repositories;

import database.Conexao;
import entities.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepositoryJDBC implements PedidoRepository {

    @Override
    public void salvar(Pedido p) {
        String sql = "INSERT INTO pedido (nome_cliente, telefone, tipo, horario, quantidade, observacao) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCliente().getName());
            ps.setString(2, p.getCliente().getNumber());
            ps.setString(3, p.getTipo().name());
            ps.setString(4, p.getHorario().toString());
            ps.setInt(5, p.getQuant());
            ps.setString(6, p.getAob());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pedido", e);
        }
    }
    @Override
    public void marcarRetirado(int id, boolean retirado) {
        String sql = "UPDATE pedido SET retirado = ? WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, retirado ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido", e);
        }
    }

    @Override
    public List<Pedido> listarTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido ORDER BY retirado, horario";
        try (Connection conn = Conexao.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Cliente c = new Cliente(rs.getString("nome_cliente"), rs.getString("telefone"));
                lista.add(new Pedido(
                        rs.getInt("id"),
                        c,
                        TipoPedido.valueOf(rs.getString("tipo")),
                        LocalTime.parse(rs.getString("horario")),
                        rs.getInt("quantidade"),
                        rs.getString("observacao"),
                        rs.getInt("retirado") == 1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos", e);
        }
        return lista;
    }
}