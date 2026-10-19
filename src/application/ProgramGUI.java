// application/ProgramGUI.java
package application;

import database.Conexao;
import repositories.PedidoRepositoryJDBC;
import services.PedidoService;
import services.PedidoServiceIMPL;
import view.TelaPedidos;

import javax.swing.SwingUtilities;

public class ProgramGUI {
    public static void main(String[] args) {
        Conexao.criarTabelas();
        PedidoService service = new PedidoServiceIMPL(new PedidoRepositoryJDBC());

        SwingUtilities.invokeLater(() -> new TelaPedidos(service).setVisible(true));
    }
}