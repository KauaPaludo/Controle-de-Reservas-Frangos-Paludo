// database/Conexao.java
package database;

import java.sql.*;
import java.sql.ResultSet;

public class Conexao {
    private static final String URL = "jdbc:sqlite:pedidos.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void criarTabelas() {
        String sql = """
                CREATE TABLE IF NOT EXISTS pedido (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome_cliente TEXT NOT NULL,
                    telefone TEXT NOT NULL,
                    tipo TEXT NOT NULL,
                    horario TEXT NOT NULL,
                    quantidade INTEGER NOT NULL,
                    observacao TEXT,
                    retirado INTEGER NOT NULL DEFAULT 0
                )
                """;
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute(sql);
            adicionarColunaRetiradoSeFaltar(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas", e);
        }
    }

    private static void adicionarColunaRetiradoSeFaltar(Connection conn) throws SQLException {
        boolean existe = false;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(pedido)")) {
            while (rs.next()) {
                if ("retirado".equalsIgnoreCase(rs.getString("name"))) {
                    existe = true;
                }
            }
        }
        if (!existe) {
            try (Statement st = conn.createStatement()) {
                st.execute("ALTER TABLE pedido ADD COLUMN retirado INTEGER NOT NULL DEFAULT 0");
            }
        }
    }
}
