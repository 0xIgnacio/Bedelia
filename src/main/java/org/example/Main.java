package org.example;
import java.sql.*;
@SuppressWarnings({"SqlNoDataSourceInspection", "CallToPrintStackTrace", "FieldMayBeFinal", "FieldCanBeLocal"})

public class Main {
    private static String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static String USER = "postgres";
    private static String PASSWORD = "1234";
    private static String DB_NAME = "bedeliadb";

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            createDatabase(conn);
            conn.close();

            conn = DriverManager.getConnection(
                    URL.replace("/postgres", "/" + DB_NAME), USER, PASSWORD);

            createTables(conn);
            insertDefaultExams(conn);
            conn.close();

            System.out.println("Datos iniciales cargados");

        } catch (SQLException e) {
            System.out.println("Conexión fallida a la base de datos.");
            e.printStackTrace();
        }
    }

    private static void createDatabase(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + DB_NAME + "'");

        if (!rs.next()) {
            stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
            System.out.println("Base de datos creada correctamente");
        } else {
            System.out.println("Base de datos ya existe");
        }

        rs.close();
        stmt.close();
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        String createExamenesTable =
            "CREATE TABLE IF NOT EXISTS Examenes (" +
            "codigo VARCHAR(45) PRIMARY KEY," +
            "materia VARCHAR(45)," +
            "periodo VARCHAR(45))";
        stmt.executeUpdate(createExamenesTable);

        String createResultadosTable =
            "CREATE TABLE IF NOT EXISTS Resultados (" +
            "cedula INT," +
            "codigo VARCHAR(45)," +
            "calificacion INT," +
            "FOREIGN KEY (codigo) REFERENCES Examenes(codigo))";

        stmt.executeUpdate(createResultadosTable);

        stmt.close();
    }

    private static void insertDefaultExams(Connection conn) throws SQLException {
        String insertExam =
                "INSERT INTO Examenes(codigo, materia, periodo) " +
                        "VALUES (?, ?, ?) ON CONFLICT (codigo) DO NOTHING";

        PreparedStatement pstmt = conn.prepareStatement(insertExam);

        String[][] exams = {
            {"MD2020Dic", "Matemática discreta", "Diciembre 2020"},
            {"P12020Dic", "Programación 1", "Diciembre 2020"},
            {"BD2020Dic", "Bases de datos", "Diciembre 2020"},
            {"MD2021Feb", "Matemática discreta", "Febrero 2021"},
            {"SO2021Feb", "Sistemas Operativos", "Febrero 2021"},

            // Datos míos de prueba
            {"AL2021Jun", "Álgebra Lineal", "Junio 2021"},
            {"ED2021Jun", "Estructuras de Datos", "Junio 2021"},
            {"IA2021Sep", "Inteligencia Artificial", "Septiembre 2021"},
            {"RC2021Sep", "Redes de Computadoras", "Septiembre 2021"},
            {"IS2021Dic", "Ingeniería de Software", "Diciembre 2021"},
            {"BD2022Feb", "Bases de Datos Avanzadas", "Febrero 2022"},
            {"SO2022Feb", "Sistemas Operativos Avanzados", "Febrero 2022"},
            {"AL2022Jun", "Algoritmos", "Junio 2022"},
            {"SE2022Jun", "Seguridad Informática", "Junio 2022"},
            {"CC2022Sep", "Computación en la Nube", "Septiembre 2022"}
        };

        for (String[] exam : exams) {
            pstmt.setString(1, exam[0]);
            pstmt.setString(2, exam[1]);
            pstmt.setString(3, exam[2]);
            pstmt.executeUpdate();
        }

        pstmt.close();
    }
}
