/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aereolinea;

import java.util.Date;

/**
 *
 * @author LUIS ALBERTO
 */
public class Ticket {
    private int id;
    private String destino;
    private String tipoViaje;
    private int cantidadAdultos;
    private int cantidadJovenes;
    private int cantidadNinos;
    private Date fechaIda;
    private Date fechaVuelta;
    private String estadoVuelo;

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getTipoViaje() { return tipoViaje; }
    public void setTipoViaje(String tipoViaje) { this.tipoViaje = tipoViaje; }

    public int getCantidadAdultos() { return cantidadAdultos; }
    public void setCantidadAdultos(int cantidadAdultos) { this.cantidadAdultos = cantidadAdultos; }

    public int getCantidadJovenes() { return cantidadJovenes; }
    public void setCantidadJovenes(int cantidadJovenes) { this.cantidadJovenes = cantidadJovenes; }

    public int getCantidadNinos() { return cantidadNinos; }
    public void setCantidadNinos(int cantidadNinos) { this.cantidadNinos = cantidadNinos; }

    public Date getFechaIda() { return fechaIda; }
    public void setFechaIda(Date fechaIda) { this.fechaIda = fechaIda; }

    public Date getFechaVuelta() { return fechaVuelta; }
    public void setFechaVuelta(Date fechaVuelta) { this.fechaVuelta = fechaVuelta; }

    public String getEstadoVuelo() { return estadoVuelo; }
    public void setEstadoVuelo(String estadoVuelo) { this.estadoVuelo = estadoVuelo; }
}
