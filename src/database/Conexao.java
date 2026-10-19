// database/Conexao.java
package database;

import java.sql.*;

public class Conexao {
    private static final String URL = "jdbc:sqlite:pedidos.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void criarTabelas() {
        // quantidade: unidades (frango) ou kg (carne assada); o SQLite guarda 1.5 como número real.
        // telefone: texto vazio quando o cliente não informa (a coluna é NOT NULL desde a primeira versão).
        String sql = """
                CREATE TABLE IF NOT EXISTS pedido (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome_cliente TEXT NOT NULL,
                    telefone TEXT NOT NULL,
                    tipo TEXT NOT NULL,
                    horario TEXT NOT NULL,
                    quantidade INTEGER NOT NULL,
                    observacao TEXT,
                    retirado INTEGER NOT NULL DEFAULT 0,
                    data_retirada TEXT
                )
                """;
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute(sql);
            adicionarColunaSeFaltar(conn, "retirado", "INTEGER NOT NULL DEFAULT 0");
            adicionarColunaSeFaltar(conn, "data_retirada", "TEXT");
            // Pedidos antigos (de antes de existir a data) passam a valer para hoje
            st.executeUpdate("UPDATE pedido SET data_retirada = date('now','localtime') WHERE data_retirada IS NULL");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas", e);
        }
    }

    private static void adicionarColunaSeFaltar(Connection conn, String coluna, String definicao) throws SQLException {
        boolean existe = false;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(pedido)")) {
            while (rs.next()) {
                if (coluna.equalsIgnoreCase(rs.getString("name"))) {
                    existe = true;
                }
            }
        }
        if (!existe) {
            try (Statement st = conn.createStatement()) {
                st.execute("ALTER TABLE pedido ADD COLUMN " + coluna + " " + definicao);
            }
        }
    }
}
