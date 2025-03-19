/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aereolinea;

/**
 *
 * @author LUIS ALBERTO
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import conexion.conexionMysql;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import aereolinea.navegador;
import com.toedter.calendar.JDateChooser;
import java.util.ArrayList;
import java.util.List;

public class ManejodeTickets {
    // Método para insertar el ticket en la base de datos
    public void insertarTicket(int idUsuario, String destino, String tipoViaje, int cantidadAdultos, int cantidadJovenes, int cantidadNinos, Date fechaIda, Date fechaVuelta, String estadoVuelo) {
        conexionMysql conexion = new conexionMysql();
        Connection conn = conexion.conectar();

        // Consulta SQL para insertar el ticket
        String query = "INSERT INTO tickets (id, destino, tipo_viaje, cant_adultos, cantidad_jovenes, cantidad_niños, fecha_ida, fecha_vuelta, estado_ticket) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Preparamos la consulta
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, idUsuario); // Establecemos el idUsuario
            pst.setString(2, destino); // Establecemos el destino
            pst.setString(3, tipoViaje); // Establecemos el tipo de viaje
            pst.setInt(4, cantidadAdultos); // Establecemos la cantidad de adultos
            pst.setInt(5, cantidadJovenes); // Establecemos la cantidad de jóvenes
            pst.setInt(6, cantidadNinos); // Establecemos la cantidad de niños
            pst.setDate(7, new java.sql.Date(fechaIda.getTime())); // Establecemos la fecha de ida

            // Si es solo ida, no guardamos la fecha de vuelta
            if (fechaVuelta != null) {
                pst.setDate(8, new java.sql.Date(fechaVuelta.getTime())); // Establecemos la fecha de vuelta
            } else {
                pst.setNull(8, java.sql.Types.DATE); // Si no hay fecha de vuelta, ponemos NULL
            }

            pst.setString(9, estadoVuelo); // Establecemos el estado del vuelo

            // Ejecutamos la consulta
            int filasInsertadas = pst.executeUpdate();
            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(null, "Ticket registrado exitosamente.");
            }

        } catch (SQLException e) {
            // Si hay un error, mostramos el mensaje
            JOptionPane.showMessageDialog(null, "Error al insertar el ticket: " + e.getMessage());
        }
    }
    
    public List<Ticket> obtenerTicketsPorUsuario(int idUsuario) {
    conexionMysql conexion = new conexionMysql();
    Connection conn = conexion.conectar();

    String query = "SELECT * FROM tickets WHERE id = ?";
    List<Ticket> listaTickets = new ArrayList<>();

    try {
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setInt(1, idUsuario);

        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Ticket ticket = new Ticket();
            ticket.setId(rs.getInt("id"));
            ticket.setDestino(rs.getString("destino"));
            ticket.setTipoViaje(rs.getString("tipo_viaje"));
            ticket.setCantidadAdultos(rs.getInt("cantidad_adultos"));
            ticket.setCantidadJovenes(rs.getInt("cantidad_jovenes"));
            ticket.setCantidadNinos(rs.getInt("cantidad_ninos"));
            ticket.setFechaIda(rs.getDate("fecha_ida"));
            ticket.setFechaVuelta(rs.getDate("fecha_vuelta"));
            ticket.setEstadoVuelo(rs.getString("estado_vuelo"));

            listaTickets.add(ticket);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al obtener los tickets: " + e.getMessage());
    }

    return listaTickets;
}
}
