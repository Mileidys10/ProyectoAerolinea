/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package aereolinea;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.toedter.calendar.JDateChooser;
import conexion.conexionMysql;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.Timer;
import loginandsignup.Login;

/**
 *
 * @author HP
 */
public class navegador extends javax.swing.JFrame {

    //CONEXION BD
    conexionMysql con = new conexionMysql();
    Connection cn = con.conectar();
    private int idUsuario;
    /**
     * Creates new form navegador
     */
    public Login lg;
    private int idUsuarioActual = -1;

    public navegador() {
        initComponents();
        configureDateChooser();
        agregarEventos();

        panelAdulto1.setVisible(false);
        panelAdulto2.setVisible(false);
        panelAdulto3.setVisible(false);
        panelAdulto4.setVisible(false);

        panelJoven1.setVisible(false);
        panelJoven2.setVisible(false);
        panelJoven3.setVisible(false);
        panelJoven4.setVisible(false);

        panelNiño1.setVisible(false);
        panelNiño2.setVisible(false);
        panelNiño3.setVisible(false);
        panelNiño4.setVisible(false);

        panelBebe1.setVisible(false);
        panelBebe2.setVisible(false);
        panelBebe3.setVisible(false);
        panelBebe4.setVisible(false);
        // Llamar al método para iniciar el timer del estado del vuelo
        iniciarEstadoVueloTimer();
        this.idUsuario = obtenerIDUsuario("email", "contraseña");
    }
    private JSpinner adultosSpinner;
    private JSpinner jovenesSpinner;
    private JSpinner ninosSpinner;
    private JSpinner bebesSpinner;

    public int obtenerIDUsuario(String usuario, String contraseña) {
        int idUsuario = -1; // Valor predeterminado si no se encuentra el usuario
        String query = "SELECT id FROM usuarios WHERE email = ? AND contraseña = ?";

        try (java.sql.PreparedStatement ps = cn.prepareStatement(query)) {
            // Establecemos los parámetros (nombreUsuario y contraseña)
            ps.setString(1, usuario);
            ps.setString(2, contraseña);

            // Ejecutamos la consulta
            ResultSet rs = ps.executeQuery();

            // Si hay un resultado, obtenemos el idUsuario
            if (rs.next()) {
                idUsuario = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idUsuario; // Devuelve el idUsuario o -1 si no se encontró
    }

    private String obtenerTipoViajeSeleccionado() {
        if (btnrVIP.isSelected()) {
            return "VIP";
        } else if (btnrBasic.isSelected()) {
            return "Basic";
        } else if (btnrClassic.isSelected()) {
            return "Classic";
        } else {
            return null; // En caso de que no se haya seleccionado nada
        }
    }

    private void iniciarEstadoVueloTimer() {
        // Tiempo de inicio del vuelo
        long tiempoInicioVuelo = System.currentTimeMillis();

        // Configuración del Timer para actualizar el estado cada 3 minutos
        Timer timer = new Timer(180000, e -> {
            // Calcula el estado del vuelo
            String estadoVuelo = obtenerEstadoVuelo(tiempoInicioVuelo);

            // Actualiza el JLabel con el estado actual
            txtEstadoVuelo.setText("Estado del vuelo: " + estadoVuelo);

            // Si el vuelo ha finalizado, detén el Timer
            if (estadoVuelo.equals("Vuelo finalizado")) {
                ((Timer) e.getSource()).stop();
            }
        });

        // Inicia el Timer
        timer.start();
    }

    private String obtenerEstadoVuelo(long tiempoInicio) {
        // Calcula el tiempo transcurrido en segundos desde el inicio
        long tiempoActual = System.currentTimeMillis();
        long tiempoTranscurrido = (tiempoActual - tiempoInicio) / 1000; // Convertimos a segundos

        // Determina el estado del vuelo en función del tiempo transcurrido
        if (tiempoTranscurrido < 60) {
            return "En espera"; // Menos de 1 minuto
        } else if (tiempoTranscurrido < 120) {
            return "Abordando pasajeros"; // Entre 1 y 2 minutos
        } else if (tiempoTranscurrido < 300) {
            return "Volando"; // Entre 2 y 5 minutos
        } else {
            return "Vuelo finalizado"; // Más de 5 minutos
        }
    }

    private void agregarEventos() {
        contAdultos.addChangeListener(evt -> calcularPrecio());
        contJovenes.addChangeListener(evt -> calcularPrecio());
        contNiños.addChangeListener(evt -> calcularPrecio());
        contBebes.addChangeListener(evt -> calcularPrecio());

        comboOrigen.addActionListener(evt -> calcularPrecio());
        comboDestino.addActionListener(evt -> calcularPrecio());

        btnrBasic.addActionListener(evt -> calcularPrecio());
        btnrClassic.addActionListener(evt -> calcularPrecio());
        btnrVIP.addActionListener(evt -> calcularPrecio());

        fechaIda.getDateEditor().addPropertyChangeListener("date", evt -> calcularPrecio());
        fechaVuelta.getDateEditor().addPropertyChangeListener("date", evt -> calcularPrecio());
    }

    private void calcularPrecio() {
        int precioBase = 0;

        // Obtén valores de los spinners
        int adultos = (int) contAdultos.getValue();
        int jovenes = (int) contJovenes.getValue();
        int ninos = (int) contNiños.getValue();
        int bebes = (int) contBebes.getValue();

        // Calcula según el tipo de pasajeros
        precioBase += adultos * 50000;
        precioBase += jovenes * 40000;
        precioBase += ninos * 35000;
        precioBase += bebes * 35000;

        String origen = (String) comboOrigen.getSelectedItem();
        String destino = (String) comboDestino.getSelectedItem();

        if (origen != null && destino != null && !origen.equals(destino)) {
            if ((origen.equals("Bogotá") && destino.equals("Cartagena"))
                    || (origen.equals("Cartagena") && destino.equals("Bogotá"))) {
                precioBase += 50000; // Suplemento Bogotá-Cartagena
            } else if ((origen.equals("Bogotá") && destino.equals("Cali"))
                    || (origen.equals("Cali") && destino.equals("Bogotá"))) {
                precioBase += 40000; // Suplemento Bogotá-Cali
            } else if ((origen.equals("Cartagena") && destino.equals("Cali"))
                    || (origen.equals("Cali") && destino.equals("Cartagena"))) {
                precioBase += 45000; // Suplemento Cartagena-Cali
            }
        }

        // Ajustar precio según la categoría seleccionada (Button Group)
        if (btnrBasic.isSelected()) {
            precioBase += 0; // Sin cambio
        } else if (btnrClassic.isSelected()) {
            precioBase += 40000; // Classic: +20,000
        } else if (btnrVIP.isSelected()) {
            precioBase += 80000; // VIP: +50,000
        }
        // Incrementos por fecha de ida y vuelta
        Date fechaSeleccionadaIda = fechaIda.getDate();
        if (fechaSeleccionadaIda != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaSeleccionadaIda);
            precioBase += calcularIncrementoMes(cal.get(Calendar.MONTH));
        }

        // Incrementos por fecha de ida (si la fecha está seleccionada)
        if (btnIda.isSelected()) {
            // Si es solo ida, no calculamos la fecha de vuelta ni aplicamos incremento para ella
            fechaVuelta.setEnabled(false); // Deshabilitar el selector de fecha de vuelta
        } else if (btnIdayVuelta.isSelected()) {
            // Si es ida y vuelta, verificamos la fecha de vuelta
            fechaVuelta.setEnabled(true); // Habilitar el selector de fecha de vuelta
            fechaVuelta.setVisible(true);
            Date fechaSeleccionadaVuelta = fechaVuelta.getDate();
            if (fechaSeleccionadaVuelta != null) {
                // Aseguramos que la fecha de vuelta no sea posterior a la de ida
                if (fechaSeleccionadaVuelta.before(fechaSeleccionadaIda)) {
                    JOptionPane.showMessageDialog(this, "La fecha de vuelta no puede ser anterior a la de ida.");
                    return;
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaSeleccionadaVuelta);
                precioBase += calcularIncrementoMes(cal.get(Calendar.MONTH));
            }
        }

        // Actualiza el JLabel con el precio total
        txtTotal.setText(precioBase + "");
    }

    private int calcularIncrementoMes(int mes) {
        switch (mes) {
            case Calendar.NOVEMBER:
                return 100000;
            case Calendar.DECEMBER:
                return 150000;
            case Calendar.JANUARY:
                return 90000;
            case Calendar.FEBRUARY:
                return 70000;
            default:
                return 0;
        }
    }

    private void mostrarPanelesAdultos(int cantidadAdultos) {
        // Ocultar todos los paneles
        panelAdulto1.setVisible(false);
        panelAdulto2.setVisible(false);
        panelAdulto3.setVisible(false);
        panelAdulto4.setVisible(false);

        // Mostrar solo los necesarios
        if (cantidadAdultos >= 1) {
            panelAdulto1.setVisible(true);
        }
        if (cantidadAdultos >= 2) {
            panelAdulto2.setVisible(true);
        }
        if (cantidadAdultos >= 3) {
            panelAdulto3.setVisible(true);
        }
        if (cantidadAdultos >= 4) {
            panelAdulto4.setVisible(true);
        }
    }

    private void configureDateChooser() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 1);
        fechaIda.setMinSelectableDate(calendar.getTime());
        fechaVuelta.setMinSelectableDate(calendar.getTime());

        fechaIda.setSelectableDateRange(calendar.getTime(), calendar.getTime());
        fechaVuelta.setSelectableDateRange(calendar.getTime(), calendar.getTime());

        calendar.set(2025, Calendar.FEBRUARY, 28);
        fechaVuelta.setMaxSelectableDate(calendar.getTime());
        fechaIda.setMaxSelectableDate(calendar.getTime());
    }

    private void actualizarFechaDeVuelta() {
        if (btnIda.isSelected()) {
            fechaVuelta.setVisible(false);
            txtVuelta.setVisible(false);
        } else if (btnIdayVuelta.isSelected()) {
            fechaVuelta.setVisible(true);
            txtVuelta.setVisible(true);
        }
        fechaVuelta.revalidate();
        fechaVuelta.repaint();
    }

    private void mostrarPanelesJovenes(int cantidadJovenes) {
        // Ocultar todos los paneles

        panelJoven1.setVisible(false);
        panelJoven2.setVisible(false);
        panelJoven3.setVisible(false);
        panelJoven4.setVisible(false);

        // Mostrar solo los necesarios
        if (cantidadJovenes >= 1) {
            panelJoven1.setVisible(true);
        }
        if (cantidadJovenes >= 2) {
            panelJoven2.setVisible(true);
        }
        if (cantidadJovenes >= 3) {
            panelJoven3.setVisible(true);
        }
        if (cantidadJovenes >= 4) {
            panelJoven4.setVisible(true);
        }

    }

    private void mostrarPanelesNiños(int cantidadNiños) {
        panelNiño1.setVisible(false);
        panelNiño2.setVisible(false);
        panelNiño3.setVisible(false);
        panelNiño4.setVisible(false);

        // Mostrar solo los necesarios
        if (cantidadNiños >= 1) {
            panelNiño1.setVisible(true);
        }
        if (cantidadNiños >= 2) {
            panelNiño2.setVisible(true);
        }
        if (cantidadNiños >= 3) {
            panelNiño3.setVisible(true);
        }
        if (cantidadNiños >= 4) {
            panelNiño4.setVisible(true);
        }
    }

    private void mostrarPanelesBebes(int cantidadBebes) {
        panelBebe1.setVisible(false);
        panelBebe2.setVisible(false);
        panelBebe3.setVisible(false);
        panelBebe4.setVisible(false);

        // Mostrar solo los necesarios
        if (cantidadBebes >= 1) {
            panelBebe1.setVisible(true);
        }
        if (cantidadBebes >= 2) {
            panelBebe2.setVisible(true);
        }
        if (cantidadBebes >= 3) {
            panelBebe3.setVisible(true);
        }
        if (cantidadBebes >= 4) {
            panelBebe4.setVisible(true);
        }}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrupoPlanes = new javax.swing.ButtonGroup();
        jLabel2 = new javax.swing.JLabel();
        btnGrupoIdaoVuelta = new javax.swing.ButtonGroup();
        menuPrincipal = new javax.swing.JPanel();
        compraTicket = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        contJovenes = new javax.swing.JSpinner();
        fechaIda = new com.toedter.calendar.JDateChooser();
        btnrClassic = new javax.swing.JRadioButton();
        comboOrigen = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        contBebes = new javax.swing.JSpinner();
        contAdultos = new javax.swing.JSpinner();
        btnrVIP = new javax.swing.JRadioButton();
        comboDestino = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        txtNiños = new javax.swing.JLabel();
        contNiños = new javax.swing.JSpinner();
        txtIda = new javax.swing.JLabel();
        btnrBasic = new javax.swing.JRadioButton();
        txtAdultos = new javax.swing.JLabel();
        txtIda1 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtJovenes = new javax.swing.JLabel();
        fechaVuelta = new com.toedter.calendar.JDateChooser();
        txtIda2 = new javax.swing.JLabel();
        txtBebes = new javax.swing.JLabel();
        txtVuelta = new javax.swing.JLabel();
        btnIda = new javax.swing.JRadioButton();
        btnIdayVuelta = new javax.swing.JRadioButton();
        txtprecio = new javax.swing.JLabel();
        btnContinuar = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        misTickets = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tipoDeVuelo = new javax.swing.JLabel();
        labelDestino = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnCancelar2 = new javax.swing.JButton();
        txtEstadoVuelo = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btnCompra = new javax.swing.JButton();
        pasajeros = new javax.swing.JPanel();
        panelNiño4 = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jComboBox21 = new javax.swing.JComboBox<>();
        jTextField19 = new javax.swing.JTextField();
        jTextField20 = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jComboBox22 = new javax.swing.JComboBox<>();
        fechaIda10 = new com.toedter.calendar.JDateChooser();
        jLabel82 = new javax.swing.JLabel();
        panelBebe4 = new javax.swing.JPanel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jComboBox23 = new javax.swing.JComboBox<>();
        jTextField21 = new javax.swing.JTextField();
        jTextField22 = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        jComboBox24 = new javax.swing.JComboBox<>();
        fechaIda11 = new com.toedter.calendar.JDateChooser();
        jLabel88 = new javax.swing.JLabel();
        panelJoven4 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jComboBox25 = new javax.swing.JComboBox<>();
        jTextField23 = new javax.swing.JTextField();
        jTextField24 = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        jComboBox26 = new javax.swing.JComboBox<>();
        fechaIda12 = new com.toedter.calendar.JDateChooser();
        jLabel94 = new javax.swing.JLabel();
        panelAdulto1 = new javax.swing.JPanel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jComboBox29 = new javax.swing.JComboBox<>();
        jTextField27 = new javax.swing.JTextField();
        jTextField28 = new javax.swing.JTextField();
        jLabel105 = new javax.swing.JLabel();
        jComboBox30 = new javax.swing.JComboBox<>();
        fechaIda14 = new com.toedter.calendar.JDateChooser();
        jLabel106 = new javax.swing.JLabel();
        panelAdulto2 = new javax.swing.JPanel();
        jLabel107 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        jComboBox31 = new javax.swing.JComboBox<>();
        jTextField29 = new javax.swing.JTextField();
        jTextField30 = new javax.swing.JTextField();
        jLabel111 = new javax.swing.JLabel();
        jComboBox32 = new javax.swing.JComboBox<>();
        fechaIda15 = new com.toedter.calendar.JDateChooser();
        jLabel112 = new javax.swing.JLabel();
        panelAdulto3 = new javax.swing.JPanel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        jComboBox33 = new javax.swing.JComboBox<>();
        jTextField31 = new javax.swing.JTextField();
        jTextField32 = new javax.swing.JTextField();
        jLabel117 = new javax.swing.JLabel();
        jComboBox34 = new javax.swing.JComboBox<>();
        fechaIda16 = new com.toedter.calendar.JDateChooser();
        jLabel118 = new javax.swing.JLabel();
        panelJoven1 = new javax.swing.JPanel();
        jLabel119 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        jComboBox35 = new javax.swing.JComboBox<>();
        jTextField33 = new javax.swing.JTextField();
        jTextField34 = new javax.swing.JTextField();
        jLabel123 = new javax.swing.JLabel();
        jComboBox36 = new javax.swing.JComboBox<>();
        fechaIda17 = new com.toedter.calendar.JDateChooser();
        jLabel124 = new javax.swing.JLabel();
        panelJoven2 = new javax.swing.JPanel();
        jLabel125 = new javax.swing.JLabel();
        jLabel126 = new javax.swing.JLabel();
        jLabel127 = new javax.swing.JLabel();
        jLabel128 = new javax.swing.JLabel();
        jComboBox37 = new javax.swing.JComboBox<>();
        jTextField35 = new javax.swing.JTextField();
        jTextField36 = new javax.swing.JTextField();
        jLabel129 = new javax.swing.JLabel();
        jComboBox38 = new javax.swing.JComboBox<>();
        fechaIda18 = new com.toedter.calendar.JDateChooser();
        jLabel130 = new javax.swing.JLabel();
        panelJoven3 = new javax.swing.JPanel();
        jLabel131 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        jLabel134 = new javax.swing.JLabel();
        jComboBox39 = new javax.swing.JComboBox<>();
        jTextField37 = new javax.swing.JTextField();
        jTextField38 = new javax.swing.JTextField();
        jLabel135 = new javax.swing.JLabel();
        jComboBox40 = new javax.swing.JComboBox<>();
        fechaIda19 = new com.toedter.calendar.JDateChooser();
        jLabel136 = new javax.swing.JLabel();
        panelNiño1 = new javax.swing.JPanel();
        jLabel137 = new javax.swing.JLabel();
        jLabel138 = new javax.swing.JLabel();
        jLabel139 = new javax.swing.JLabel();
        jLabel140 = new javax.swing.JLabel();
        jComboBox41 = new javax.swing.JComboBox<>();
        jTextField39 = new javax.swing.JTextField();
        jTextField40 = new javax.swing.JTextField();
        jLabel141 = new javax.swing.JLabel();
        jComboBox42 = new javax.swing.JComboBox<>();
        fechaIda20 = new com.toedter.calendar.JDateChooser();
        jLabel142 = new javax.swing.JLabel();
        panelNiño2 = new javax.swing.JPanel();
        jLabel143 = new javax.swing.JLabel();
        jLabel144 = new javax.swing.JLabel();
        jLabel145 = new javax.swing.JLabel();
        jLabel146 = new javax.swing.JLabel();
        jComboBox43 = new javax.swing.JComboBox<>();
        jTextField41 = new javax.swing.JTextField();
        jTextField42 = new javax.swing.JTextField();
        jLabel147 = new javax.swing.JLabel();
        jComboBox44 = new javax.swing.JComboBox<>();
        fechaIda21 = new com.toedter.calendar.JDateChooser();
        jLabel148 = new javax.swing.JLabel();
        panelNiño3 = new javax.swing.JPanel();
        jLabel149 = new javax.swing.JLabel();
        jLabel150 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        jLabel152 = new javax.swing.JLabel();
        jComboBox45 = new javax.swing.JComboBox<>();
        jTextField43 = new javax.swing.JTextField();
        jTextField44 = new javax.swing.JTextField();
        jLabel153 = new javax.swing.JLabel();
        jComboBox46 = new javax.swing.JComboBox<>();
        fechaIda22 = new com.toedter.calendar.JDateChooser();
        jLabel154 = new javax.swing.JLabel();
        panelBebe1 = new javax.swing.JPanel();
        jLabel155 = new javax.swing.JLabel();
        jLabel156 = new javax.swing.JLabel();
        jLabel157 = new javax.swing.JLabel();
        jLabel158 = new javax.swing.JLabel();
        jComboBox47 = new javax.swing.JComboBox<>();
        jTextField45 = new javax.swing.JTextField();
        jTextField46 = new javax.swing.JTextField();
        jLabel159 = new javax.swing.JLabel();
        jComboBox48 = new javax.swing.JComboBox<>();
        fechaIda23 = new com.toedter.calendar.JDateChooser();
        jLabel160 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel161 = new javax.swing.JLabel();
        jLabel162 = new javax.swing.JLabel();
        jLabel163 = new javax.swing.JLabel();
        jLabel164 = new javax.swing.JLabel();
        jComboBox49 = new javax.swing.JComboBox<>();
        jTextField47 = new javax.swing.JTextField();
        jTextField48 = new javax.swing.JTextField();
        jLabel165 = new javax.swing.JLabel();
        jComboBox50 = new javax.swing.JComboBox<>();
        fechaIda24 = new com.toedter.calendar.JDateChooser();
        jLabel166 = new javax.swing.JLabel();
        panelBebe2 = new javax.swing.JPanel();
        jLabel167 = new javax.swing.JLabel();
        jLabel168 = new javax.swing.JLabel();
        jLabel169 = new javax.swing.JLabel();
        jLabel170 = new javax.swing.JLabel();
        jComboBox51 = new javax.swing.JComboBox<>();
        jTextField49 = new javax.swing.JTextField();
        jTextField50 = new javax.swing.JTextField();
        jLabel171 = new javax.swing.JLabel();
        jComboBox52 = new javax.swing.JComboBox<>();
        fechaIda25 = new com.toedter.calendar.JDateChooser();
        jLabel172 = new javax.swing.JLabel();
        panelBebe3 = new javax.swing.JPanel();
        jLabel173 = new javax.swing.JLabel();
        jLabel174 = new javax.swing.JLabel();
        jLabel175 = new javax.swing.JLabel();
        jLabel176 = new javax.swing.JLabel();
        jComboBox53 = new javax.swing.JComboBox<>();
        jTextField51 = new javax.swing.JTextField();
        jTextField52 = new javax.swing.JTextField();
        jLabel177 = new javax.swing.JLabel();
        jComboBox54 = new javax.swing.JComboBox<>();
        fechaIda26 = new com.toedter.calendar.JDateChooser();
        jLabel178 = new javax.swing.JLabel();
        btnCancelar1 = new javax.swing.JButton();
        btnListo1 = new javax.swing.JButton();
        panelAdulto4 = new javax.swing.JPanel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jComboBox27 = new javax.swing.JComboBox<>();
        jTextField25 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        jComboBox28 = new javax.swing.JComboBox<>();
        fechaIda13 = new com.toedter.calendar.JDateChooser();
        jLabel100 = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        menuPrincipal.setLayout(new java.awt.CardLayout());

        compraTicket.setBackground(new java.awt.Color(255, 255, 255));
        compraTicket.setPreferredSize(new java.awt.Dimension(1500, 900));
        compraTicket.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(230, 247, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        contJovenes.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        contJovenes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 4, 1));
        contJovenes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contJovenesStateChanged(evt);
            }
        });
        jPanel1.add(contJovenes, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 280, 108, -1));

        fechaIda.setMaxSelectableDate(new java.util.Date(253370786508000L));
        jPanel1.add(fechaIda, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 60, 149, -1));

        btnGrupoPlanes.add(btnrClassic);
        btnrClassic.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        btnrClassic.setText("Classic");
        btnrClassic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrClassicActionPerformed(evt);
            }
        });
        jPanel1.add(btnrClassic, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 200, 100, -1));

        comboOrigen.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        comboOrigen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bogotá", "Cartagena", "Cali", " ", " " }));
        jPanel1.add(comboOrigen, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 60, 149, -1));

        jLabel23.setText("BENEFICIOS DE LA CLASICA");
        jPanel1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 200, -1, -1));

        contBebes.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        contBebes.setModel(new javax.swing.SpinnerNumberModel(0, 0, 4, 1));
        contBebes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contBebesStateChanged(evt);
            }
        });
        jPanel1.add(contBebes, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 280, 108, -1));

        contAdultos.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        contAdultos.setModel(new javax.swing.SpinnerNumberModel(0, 0, 4, 1));
        contAdultos.setToolTipText("");
        contAdultos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        contAdultos.setName(""); // NOI18N
        contAdultos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contAdultosStateChanged(evt);
            }
        });
        jPanel1.add(contAdultos, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, 108, -1));

        btnGrupoPlanes.add(btnrVIP);
        btnrVIP.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        btnrVIP.setText("V.I.P");
        btnrVIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrVIPActionPerformed(evt);
            }
        });
        jPanel1.add(btnrVIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 280, 100, -1));

        comboDestino.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        comboDestino.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bogotá", "Cartagena", "Cali" }));
        jPanel1.add(comboDestino, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, 149, -1));

        jLabel24.setText("BENEFICIOS DE LA VIP");
        jPanel1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 130, -1, -1));

        txtNiños.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtNiños.setText("Niños");
        jPanel1.add(txtNiños, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, 108, -1));

        contNiños.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        contNiños.setModel(new javax.swing.SpinnerNumberModel(0, 0, 4, 1));
        contNiños.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contNiñosStateChanged(evt);
            }
        });
        jPanel1.add(contNiños, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 170, 108, -1));

        txtIda.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtIda.setText("Destino");
        jPanel1.add(txtIda, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 20, 120, -1));

        btnGrupoPlanes.add(btnrBasic);
        btnrBasic.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        btnrBasic.setText("Basic");
        btnrBasic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrBasicActionPerformed(evt);
            }
        });
        jPanel1.add(btnrBasic, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 130, 100, -1));

        txtAdultos.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtAdultos.setText("Adultos");
        jPanel1.add(txtAdultos, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 108, -1));

        txtIda1.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtIda1.setText("Ida ");
        jPanel1.add(txtIda1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 120, -1));

        txtTotal.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jPanel1.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 350, 130, 20));

        jLabel22.setText("BENEFICIOS DE LA CLASE BASICA ");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 290, -1, -1));

        txtJovenes.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtJovenes.setText("Jóvenes");
        jPanel1.add(txtJovenes, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, 108, -1));
        jPanel1.add(fechaVuelta, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 60, 149, -1));

        txtIda2.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtIda2.setText("Origen");
        jPanel1.add(txtIda2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 20, 140, -1));

        txtBebes.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtBebes.setText("Bebés");
        jPanel1.add(txtBebes, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 230, 108, -1));

        txtVuelta.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtVuelta.setText("Vuelta");
        jPanel1.add(txtVuelta, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 20, 120, -1));

        btnGrupoIdaoVuelta.add(btnIda);
        btnIda.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        btnIda.setText("Solo ida");
        btnIda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdaActionPerformed(evt);
            }
        });
        jPanel1.add(btnIda, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        btnGrupoIdaoVuelta.add(btnIdayVuelta);
        btnIdayVuelta.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        btnIdayVuelta.setText("Ida y vuelta");
        btnIdayVuelta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdayVueltaActionPerformed(evt);
            }
        });
        jPanel1.add(btnIdayVuelta, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        txtprecio.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        txtprecio.setText("TOTAL:");
        jPanel1.add(txtprecio, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 350, 70, -1));

        compraTicket.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 200, 880, 390));

        btnContinuar.setBackground(new java.awt.Color(243, 156, 18));
        btnContinuar.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        btnContinuar.setText("Continuar");
        btnContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarActionPerformed(evt);
            }
        });
        compraTicket.add(btnContinuar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 620, 160, 50));

        jButton2.setBackground(new java.awt.Color(243, 156, 18));
        jButton2.setForeground(new java.awt.Color(45, 45, 45));
        jButton2.setText("COMPRAR TICKET");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        compraTicket.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 20, 200, 40));

        jButton3.setBackground(new java.awt.Color(243, 156, 18));
        jButton3.setForeground(new java.awt.Color(45, 45, 45));
        jButton3.setText("MI TICKET");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        compraTicket.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 20, 180, 40));

        jButton4.setBackground(new java.awt.Color(243, 156, 18));
        jButton4.setForeground(new java.awt.Color(45, 45, 45));
        jButton4.setText("CERRAR SESIÓN");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        compraTicket.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 10, 120, 30));

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/Picsart_24-11-20_20-19-33-268.png"))); // NOI18N
        compraTicket.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, -30, 410, 700));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/Picsart_24-11-20_21-03-30-661.png"))); // NOI18N
        compraTicket.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, 960, -1));

        menuPrincipal.add(compraTicket, "card2");

        misTickets.setBackground(new java.awt.Color(230, 247, 255));
        misTickets.setForeground(new java.awt.Color(0, 102, 102));
        misTickets.setPreferredSize(new java.awt.Dimension(1500, 900));
        misTickets.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Tipo de vuelo");
        misTickets.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 213, 40));

        jLabel4.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("Destino");
        misTickets.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 130, 213, 40));

        jLabel5.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Fecha de vuelta");
        misTickets.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 270, 213, 40));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("N/A");
        jLabel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        misTickets.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 200, 280, 40));

        tipoDeVuelo.setBackground(new java.awt.Color(255, 255, 255));
        tipoDeVuelo.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        tipoDeVuelo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tipoDeVuelo.setText("N/A");
        tipoDeVuelo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        misTickets.add(tipoDeVuelo, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 60, 280, 40));

        labelDestino.setBackground(new java.awt.Color(255, 255, 255));
        labelDestino.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        labelDestino.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDestino.setText("N/A");
        labelDestino.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        misTickets.add(labelDestino, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 130, 280, 40));

        jLabel10.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Fecha de ida ");
        misTickets.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 200, 213, 40));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("N/A");
        jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        misTickets.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 270, 280, 40));

        jLabel12.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("Cantidad de personas");
        misTickets.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 340, 213, 40));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("N/A");
        jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        misTickets.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 340, 280, 40));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("N/A");
        jLabel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        misTickets.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 480, 280, 40));

        jLabel18.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Estado  de  ticket");
        misTickets.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 410, 213, 40));
        misTickets.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(605, 0, -1, 647));

        btnCancelar2.setText("REGRESAR");
        btnCancelar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelar2ActionPerformed(evt);
            }
        });
        misTickets.add(btnCancelar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 640, 170, 50));

        txtEstadoVuelo.setBackground(new java.awt.Color(255, 255, 255));
        txtEstadoVuelo.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        txtEstadoVuelo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtEstadoVuelo.setText("N/A");
        txtEstadoVuelo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        misTickets.add(txtEstadoVuelo, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 410, 280, 40));

        jLabel28.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("Estado  de  Vuelo");
        misTickets.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 480, 213, 40));

        btnCompra.setText("MIS COMPRAS");
        btnCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompraActionPerformed(evt);
            }
        });
        misTickets.add(btnCompra, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 560, 330, -1));

        menuPrincipal.add(misTickets, "card3");

        pasajeros.setBackground(new java.awt.Color(230, 247, 255));
        pasajeros.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelNiño4.setBackground(new java.awt.Color(255, 255, 255));
        panelNiño4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelNiño4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel77.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel77.setText("Niño 4:");
        panelNiño4.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel78.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel78.setText("Nombre:");
        panelNiño4.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel79.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel79.setText("Apellido:");
        panelNiño4.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel80.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel80.setText("Genero:");
        panelNiño4.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox21.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jComboBox21.setToolTipText("");
        panelNiño4.add(jComboBox21, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField19.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelNiño4.add(jTextField19, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField20.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelNiño4.add(jTextField20, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel81.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel81.setText("Fecha de nacimiento:");
        panelNiño4.add(jLabel81, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox22.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox22ActionPerformed(evt);
            }
        });
        panelNiño4.add(jComboBox22, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda10.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelNiño4.add(fechaIda10, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel82.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel82.setText("Nacionalidad:");
        panelNiño4.add(jLabel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelNiño4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 330, 330, 150));

        panelBebe4.setBackground(new java.awt.Color(255, 255, 255));
        panelBebe4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelBebe4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel83.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel83.setText("Bebe 4:");
        panelBebe4.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel84.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel84.setText("Nombre:");
        panelBebe4.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel85.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel85.setText("Apellido:");
        panelBebe4.add(jLabel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel86.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel86.setText("Genero:");
        panelBebe4.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox23.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        panelBebe4.add(jComboBox23, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField21.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelBebe4.add(jTextField21, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField22.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelBebe4.add(jTextField22, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel87.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel87.setText("Fecha de nacimiento:");
        panelBebe4.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox24.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        panelBebe4.add(jComboBox24, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda11.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelBebe4.add(fechaIda11, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel88.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel88.setText("Nacionalidad:");
        panelBebe4.add(jLabel88, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelBebe4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 490, 330, 150));

        panelJoven4.setBackground(new java.awt.Color(255, 255, 255));
        panelJoven4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelJoven4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel89.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel89.setText("Joven 4:");
        panelJoven4.add(jLabel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel90.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel90.setText("Nombre:");
        panelJoven4.add(jLabel90, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel91.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel91.setText("Apellido:");
        panelJoven4.add(jLabel91, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel92.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel92.setText("Genero:");
        panelJoven4.add(jLabel92, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox25.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jComboBox25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox25ActionPerformed(evt);
            }
        });
        panelJoven4.add(jComboBox25, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField23.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelJoven4.add(jTextField23, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField24.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelJoven4.add(jTextField24, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel93.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel93.setText("Fecha de nacimiento:");
        panelJoven4.add(jLabel93, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox26.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox26ActionPerformed(evt);
            }
        });
        panelJoven4.add(jComboBox26, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda12.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelJoven4.add(fechaIda12, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel94.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel94.setText("Nacionalidad:");
        panelJoven4.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelJoven4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 170, 330, 150));

        panelAdulto1.setBackground(new java.awt.Color(255, 255, 255));
        panelAdulto1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelAdulto1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel101.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel101.setText("Adulto 1:");
        panelAdulto1.add(jLabel101, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel102.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel102.setText("Nombre:");
        panelAdulto1.add(jLabel102, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel103.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel103.setText("Apellido:");
        panelAdulto1.add(jLabel103, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel104.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel104.setText("Genero:");
        panelAdulto1.add(jLabel104, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox29.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        panelAdulto1.add(jComboBox29, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField27.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelAdulto1.add(jTextField27, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField28.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelAdulto1.add(jTextField28, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel105.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel105.setText("Fecha de nacimiento:");
        panelAdulto1.add(jLabel105, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox30.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox30ActionPerformed(evt);
            }
        });
        panelAdulto1.add(jComboBox30, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda14.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelAdulto1.add(fechaIda14, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel106.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel106.setText("Nacionalidad:");
        panelAdulto1.add(jLabel106, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelAdulto1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 330, 150));

        panelAdulto2.setBackground(new java.awt.Color(255, 255, 255));
        panelAdulto2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelAdulto2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel107.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel107.setText("Adulto 2:");
        panelAdulto2.add(jLabel107, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel108.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel108.setText("Nombre:");
        panelAdulto2.add(jLabel108, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel109.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel109.setText("Apellido:");
        panelAdulto2.add(jLabel109, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel110.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel110.setText("Genero:");
        panelAdulto2.add(jLabel110, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox31.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        panelAdulto2.add(jComboBox31, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField29.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelAdulto2.add(jTextField29, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField30.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelAdulto2.add(jTextField30, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel111.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel111.setText("Fecha de nacimiento:");
        panelAdulto2.add(jLabel111, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox32.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox32ActionPerformed(evt);
            }
        });
        panelAdulto2.add(jComboBox32, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda15.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelAdulto2.add(fechaIda15, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel112.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel112.setText("Nacionalidad:");
        panelAdulto2.add(jLabel112, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelAdulto2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 330, 150));

        panelAdulto3.setBackground(new java.awt.Color(255, 255, 255));
        panelAdulto3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelAdulto3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel113.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel113.setText("Adulto 3:");
        panelAdulto3.add(jLabel113, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel114.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel114.setText("Nombre:");
        panelAdulto3.add(jLabel114, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel115.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel115.setText("Apellido:");
        panelAdulto3.add(jLabel115, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel116.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel116.setText("Genero:");
        panelAdulto3.add(jLabel116, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox33.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        panelAdulto3.add(jComboBox33, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField31.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelAdulto3.add(jTextField31, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField32.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelAdulto3.add(jTextField32, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel117.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel117.setText("Fecha de nacimiento:");
        panelAdulto3.add(jLabel117, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox34.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox34ActionPerformed(evt);
            }
        });
        panelAdulto3.add(jComboBox34, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda16.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelAdulto3.add(fechaIda16, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel118.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel118.setText("Nacionalidad:");
        panelAdulto3.add(jLabel118, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelAdulto3, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 10, 330, 150));

        panelJoven1.setBackground(new java.awt.Color(255, 255, 255));
        panelJoven1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelJoven1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel119.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel119.setText("Joven 1:");
        panelJoven1.add(jLabel119, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel120.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel120.setText("Nombre:");
        panelJoven1.add(jLabel120, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel121.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel121.setText("Apellido:");
        panelJoven1.add(jLabel121, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel122.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel122.setText("Genero:");
        panelJoven1.add(jLabel122, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox35.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jComboBox35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox35ActionPerformed(evt);
            }
        });
        panelJoven1.add(jComboBox35, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField33.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelJoven1.add(jTextField33, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField34.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelJoven1.add(jTextField34, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel123.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel123.setText("Fecha de nacimiento:");
        panelJoven1.add(jLabel123, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox36.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox36ActionPerformed(evt);
            }
        });
        panelJoven1.add(jComboBox36, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda17.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelJoven1.add(fechaIda17, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel124.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel124.setText("Nacionalidad:");
        panelJoven1.add(jLabel124, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelJoven1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 330, 150));

        panelJoven2.setBackground(new java.awt.Color(255, 255, 255));
        panelJoven2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelJoven2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel125.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel125.setText("Joven 2:");
        panelJoven2.add(jLabel125, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel126.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel126.setText("Nombre:");
        panelJoven2.add(jLabel126, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel127.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel127.setText("Apellido:");
        panelJoven2.add(jLabel127, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel128.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel128.setText("Genero:");
        panelJoven2.add(jLabel128, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox37.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jComboBox37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox37ActionPerformed(evt);
            }
        });
        panelJoven2.add(jComboBox37, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField35.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelJoven2.add(jTextField35, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField36.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelJoven2.add(jTextField36, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel129.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel129.setText("Fecha de nacimiento:");
        panelJoven2.add(jLabel129, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox38.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox38ActionPerformed(evt);
            }
        });
        panelJoven2.add(jComboBox38, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda18.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelJoven2.add(fechaIda18, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel130.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel130.setText("Nacionalidad:");
        panelJoven2.add(jLabel130, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelJoven2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, 330, 150));

        panelJoven3.setBackground(new java.awt.Color(255, 255, 255));
        panelJoven3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelJoven3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel131.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel131.setText("Joven 3:");
        panelJoven3.add(jLabel131, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel132.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel132.setText("Nombre:");
        panelJoven3.add(jLabel132, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel133.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel133.setText("Apellido:");
        panelJoven3.add(jLabel133, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel134.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel134.setText("Genero:");
        panelJoven3.add(jLabel134, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox39.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jComboBox39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox39ActionPerformed(evt);
            }
        });
        panelJoven3.add(jComboBox39, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField37.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelJoven3.add(jTextField37, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField38.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelJoven3.add(jTextField38, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel135.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel135.setText("Fecha de nacimiento:");
        panelJoven3.add(jLabel135, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox40.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox40ActionPerformed(evt);
            }
        });
        panelJoven3.add(jComboBox40, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda19.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelJoven3.add(fechaIda19, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel136.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel136.setText("Nacionalidad:");
        panelJoven3.add(jLabel136, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelJoven3, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 170, 330, 150));

        panelNiño1.setBackground(new java.awt.Color(255, 255, 255));
        panelNiño1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelNiño1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel137.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel137.setText("Niño 1:");
        panelNiño1.add(jLabel137, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel138.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel138.setText("Nombre:");
        panelNiño1.add(jLabel138, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel139.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel139.setText("Apellido:");
        panelNiño1.add(jLabel139, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel140.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel140.setText("Genero:");
        panelNiño1.add(jLabel140, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox41.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jComboBox41.setToolTipText("");
        panelNiño1.add(jComboBox41, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField39.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelNiño1.add(jTextField39, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField40.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelNiño1.add(jTextField40, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel141.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel141.setText("Fecha de nacimiento:");
        panelNiño1.add(jLabel141, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox42.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox42ActionPerformed(evt);
            }
        });
        panelNiño1.add(jComboBox42, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda20.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelNiño1.add(fechaIda20, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel142.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel142.setText("Nacionalidad:");
        panelNiño1.add(jLabel142, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelNiño1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 330, 150));

        panelNiño2.setBackground(new java.awt.Color(255, 255, 255));
        panelNiño2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelNiño2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel143.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel143.setText("Niño 2:");
        panelNiño2.add(jLabel143, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel144.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel144.setText("Nombre:");
        panelNiño2.add(jLabel144, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel145.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel145.setText("Apellido:");
        panelNiño2.add(jLabel145, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel146.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel146.setText("Genero:");
        panelNiño2.add(jLabel146, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox43.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jComboBox43.setToolTipText("");
        panelNiño2.add(jComboBox43, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField41.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelNiño2.add(jTextField41, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField42.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelNiño2.add(jTextField42, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel147.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel147.setText("Fecha de nacimiento:");
        panelNiño2.add(jLabel147, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox44.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox44ActionPerformed(evt);
            }
        });
        panelNiño2.add(jComboBox44, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda21.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelNiño2.add(fechaIda21, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel148.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel148.setText("Nacionalidad:");
        panelNiño2.add(jLabel148, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelNiño2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 330, 330, 150));

        panelNiño3.setBackground(new java.awt.Color(255, 255, 255));
        panelNiño3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelNiño3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel149.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel149.setText("Niño 3:");
        panelNiño3.add(jLabel149, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel150.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel150.setText("Nombre:");
        panelNiño3.add(jLabel150, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel151.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel151.setText("Apellido:");
        panelNiño3.add(jLabel151, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel152.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel152.setText("Genero:");
        panelNiño3.add(jLabel152, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox45.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jComboBox45.setToolTipText("");
        panelNiño3.add(jComboBox45, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField43.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelNiño3.add(jTextField43, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField44.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelNiño3.add(jTextField44, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel153.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel153.setText("Fecha de nacimiento:");
        panelNiño3.add(jLabel153, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox46.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox46ActionPerformed(evt);
            }
        });
        panelNiño3.add(jComboBox46, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda22.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelNiño3.add(fechaIda22, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel154.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel154.setText("Nacionalidad:");
        panelNiño3.add(jLabel154, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelNiño3, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 330, 330, 150));

        panelBebe1.setBackground(new java.awt.Color(255, 255, 255));
        panelBebe1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelBebe1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel155.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel155.setText("Bebe 1:");
        panelBebe1.add(jLabel155, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel156.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel156.setText("Nombre:");
        panelBebe1.add(jLabel156, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel157.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel157.setText("Apellido:");
        panelBebe1.add(jLabel157, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel158.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel158.setText("Genero:");
        panelBebe1.add(jLabel158, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox47.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        panelBebe1.add(jComboBox47, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField45.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelBebe1.add(jTextField45, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField46.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelBebe1.add(jTextField46, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel159.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel159.setText("Fecha de nacimiento:");
        panelBebe1.add(jLabel159, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox48.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        panelBebe1.add(jComboBox48, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda23.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelBebe1.add(fechaIda23, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel160.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel160.setText("Nacionalidad:");
        panelBebe1.add(jLabel160, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        jPanel25.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel161.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel161.setText("Bebe1:");
        jPanel25.add(jLabel161, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel162.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel162.setText("Nombre:");
        jPanel25.add(jLabel162, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel163.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel163.setText("Apellido:");
        jPanel25.add(jLabel163, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel164.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel164.setText("Genero:");
        jPanel25.add(jLabel164, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox49.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        jPanel25.add(jComboBox49, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField47.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jPanel25.add(jTextField47, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField48.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jPanel25.add(jTextField48, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel165.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel165.setText("Fecha de nacimiento:");
        jPanel25.add(jLabel165, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox50.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jPanel25.add(jComboBox50, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda24.setMaxSelectableDate(new java.util.Date(253370786508000L));
        jPanel25.add(fechaIda24, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel166.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel166.setText("Nacionalidad:");
        jPanel25.add(jLabel166, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        panelBebe1.add(jPanel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 330, 150));

        pasajeros.add(panelBebe1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 490, 330, 150));

        panelBebe2.setBackground(new java.awt.Color(255, 255, 255));
        panelBebe2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelBebe2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel167.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel167.setText("Bebe 2:");
        panelBebe2.add(jLabel167, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel168.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel168.setText("Nombre:");
        panelBebe2.add(jLabel168, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel169.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel169.setText("Apellido:");
        panelBebe2.add(jLabel169, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel170.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel170.setText("Genero:");
        panelBebe2.add(jLabel170, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox51.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        panelBebe2.add(jComboBox51, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField49.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelBebe2.add(jTextField49, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField50.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelBebe2.add(jTextField50, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel171.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel171.setText("Fecha de nacimiento:");
        panelBebe2.add(jLabel171, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox52.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        panelBebe2.add(jComboBox52, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda25.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelBebe2.add(fechaIda25, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel172.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel172.setText("Nacionalidad:");
        panelBebe2.add(jLabel172, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelBebe2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 490, 330, 150));

        panelBebe3.setBackground(new java.awt.Color(255, 255, 255));
        panelBebe3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelBebe3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel173.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel173.setText("Bebe 3:");
        panelBebe3.add(jLabel173, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel174.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel174.setText("Nombre:");
        panelBebe3.add(jLabel174, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel175.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel175.setText("Apellido:");
        panelBebe3.add(jLabel175, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel176.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel176.setText("Genero:");
        panelBebe3.add(jLabel176, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox53.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        panelBebe3.add(jComboBox53, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField51.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelBebe3.add(jTextField51, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField52.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelBebe3.add(jTextField52, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel177.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel177.setText("Fecha de nacimiento:");
        panelBebe3.add(jLabel177, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox54.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        panelBebe3.add(jComboBox54, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda26.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelBebe3.add(fechaIda26, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel178.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel178.setText("Nacionalidad:");
        panelBebe3.add(jLabel178, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelBebe3, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 490, 330, 150));

        btnCancelar1.setText("REGRESAR");
        btnCancelar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelar1ActionPerformed(evt);
            }
        });
        pasajeros.add(btnCancelar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 650, 160, 40));

        btnListo1.setText("LISTO");
        btnListo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListo1ActionPerformed(evt);
            }
        });
        pasajeros.add(btnListo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 650, 210, 40));

        panelAdulto4.setBackground(new java.awt.Color(255, 255, 255));
        panelAdulto4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(44, 62, 80), 3, true));
        panelAdulto4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel95.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel95.setText("Adulto 4:");
        panelAdulto4.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 120, 20));

        jLabel96.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel96.setText("Nombre:");
        panelAdulto4.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 60, 20));

        jLabel97.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel97.setText("Apellido:");
        panelAdulto4.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 30, 50, 20));

        jLabel98.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel98.setText("Genero:");
        panelAdulto4.add(jLabel98, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 20));

        jComboBox27.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colombiana", "Venezolana", "Americana" }));
        panelAdulto4.add(jComboBox27, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 140, 25));

        jTextField25.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelAdulto4.add(jTextField25, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, 90, 20));

        jTextField26.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        panelAdulto4.add(jTextField26, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 30, 90, -1));

        jLabel99.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel99.setText("Fecha de nacimiento:");
        panelAdulto4.add(jLabel99, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 130, 20));

        jComboBox28.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Masculino", "Femenino", "Prefierono decirlo", "Canguro" }));
        jComboBox28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox28ActionPerformed(evt);
            }
        });
        panelAdulto4.add(jComboBox28, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 120, 100, -1));

        fechaIda13.setMaxSelectableDate(new java.util.Date(253370786508000L));
        panelAdulto4.add(fechaIda13, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 180, 23));

        jLabel100.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel100.setText("Nacionalidad:");
        panelAdulto4.add(jLabel100, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 100, 20));

        pasajeros.add(panelAdulto4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 10, 330, 150));

        menuPrincipal.add(pasajeros, "card4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 1370, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(menuPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        menuPrincipal.removeAll();
        menuPrincipal.add(compraTicket);
        menuPrincipal.repaint();
        menuPrincipal.revalidate();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        menuPrincipal.removeAll();
        menuPrincipal.add(misTickets);
        menuPrincipal.repaint();
        menuPrincipal.revalidate();
     
      
  

    




                
        
         
    }//GEN-LAST:event_jButton3ActionPerformed

    private void btnContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarActionPerformed
        int adultos = (int) contAdultos.getValue();

        Date fechaSeleccionadaVuelta = fechaVuelta.getDate();
        Date fechaSeleccionadaIda = fechaIda.getDate();

        if (!btnIda.isSelected() && !btnIdayVuelta.isSelected()) {
            JOptionPane.showMessageDialog(jPanel1, "Debe seleccionar un tipo de vuelo (Solo Ida o Ida y vuelta).");
            return;  // Detener la ejecución del código si no se ha seleccionado una opción
        }

        // Validación de fechas (ida y vuelta)
        if (fechaSeleccionadaIda == null) {
            JOptionPane.showMessageDialog(jPanel1, "Debe seleccionar una fecha de ida.");
            return;  // Detener ejecución si no se ha seleccionado una fecha de ida
        }

        if (fechaSeleccionadaVuelta == null && btnIdayVuelta.isSelected()) {
            JOptionPane.showMessageDialog(jPanel1, "Debe seleccionar una fecha de vuelta.");
            return;  // Detener ejecución si no se ha seleccionado una fecha de vuelta para "Ida y vuelta"
        }

        // Validación de adultos
        if (adultos == 0) {
            JOptionPane.showMessageDialog(jPanel1, "Debe ingresar al menos un adulto.");
            return;  // Detener ejecución si no se ha ingresado al menos un adulto
        }

        // Validación de fecha de vuelta (si existe)
        if (fechaSeleccionadaVuelta != null && fechaSeleccionadaVuelta.before(fechaSeleccionadaIda)) {
            JOptionPane.showMessageDialog(jPanel1, "La fecha de vuelta no puede ser anterior a la de ida.");
            return;  // Detener ejecución si la fecha de vuelta es anterior a la fecha de ida
        }

        // Verificar si no se ha seleccionado un plan en el ButtonGroup
        if (!btnrBasic.isSelected() && !btnrClassic.isSelected() && !btnrVIP.isSelected()) {
            JOptionPane.showMessageDialog(jPanel1, "Debe seleccionar un plan de vuelo (Basic, Classic o VIP).");
            return;  // Detener ejecución si no se ha seleccionado un plan
        }

        // Verificación de que el origen y destino no sean iguales
        String origen = (String) comboOrigen.getSelectedItem();
        String destino = (String) comboDestino.getSelectedItem();
        if (origen != null && destino != null && origen.equals(destino)) {
            JOptionPane.showMessageDialog(jPanel1, "El origen y el destino no pueden ser el mismo.");
            return;  // Detener ejecución si el origen es igual al destino
        }

        // Si todas las validaciones pasan
        menuPrincipal.removeAll();
        menuPrincipal.add(pasajeros);
        menuPrincipal.repaint();
        menuPrincipal.revalidate();
    }//GEN-LAST:event_btnContinuarActionPerformed

    private void btnrBasicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrBasicActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnrBasicActionPerformed

    private void btnrClassicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrClassicActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnrClassicActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        lg = new Login();
        lg.setVisible(true);
        lg.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jComboBox25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox25ActionPerformed

    }//GEN-LAST:event_jComboBox25ActionPerformed

    private void jComboBox22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox22ActionPerformed

    private void jComboBox26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox26ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox26ActionPerformed

    private void jComboBox28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox28ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox28ActionPerformed

    private void jComboBox30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox30ActionPerformed

    private void jComboBox32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox32ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox32ActionPerformed

    private void jComboBox34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox34ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox34ActionPerformed

    private void jComboBox35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox35ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox35ActionPerformed

    private void jComboBox36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox36ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox36ActionPerformed

    private void jComboBox37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox37ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox37ActionPerformed

    private void jComboBox38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox38ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox38ActionPerformed

    private void jComboBox39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox39ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox39ActionPerformed

    private void jComboBox40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox40ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox40ActionPerformed

    private void jComboBox42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox42ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox42ActionPerformed

    private void jComboBox44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox44ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox44ActionPerformed

    private void jComboBox46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox46ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox46ActionPerformed

    private void contAdultosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contAdultosStateChanged
        int valor = (int) contAdultos.getValue();
        mostrarPanelesAdultos(valor);

    }//GEN-LAST:event_contAdultosStateChanged

    private void contJovenesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contJovenesStateChanged
        int valor = (int) contJovenes.getValue();
        mostrarPanelesJovenes(valor);
    }//GEN-LAST:event_contJovenesStateChanged

    private void contNiñosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contNiñosStateChanged
        int valor = (int) contNiños.getValue();
        mostrarPanelesNiños(valor);
    }//GEN-LAST:event_contNiñosStateChanged

    private void contBebesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contBebesStateChanged
        int valor = (int) contBebes.getValue();
        mostrarPanelesBebes(valor);
    }//GEN-LAST:event_contBebesStateChanged

    private void btnCancelar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelar1ActionPerformed
        menuPrincipal.removeAll(); // Cambiar a la vista del panel de registro
        menuPrincipal.add(compraTicket);
        menuPrincipal.repaint();
        menuPrincipal.revalidate();
    }//GEN-LAST:event_btnCancelar1ActionPerformed

    private void btnCancelar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelar2ActionPerformed
        menuPrincipal.removeAll(); // Cambiar a la vista del panel de registro
        menuPrincipal.add(compraTicket);
        menuPrincipal.repaint();
        menuPrincipal.revalidate();    }//GEN-LAST:event_btnCancelar2ActionPerformed

    private void btnrVIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrVIPActionPerformed

        // TODO add your handling code here:
    }//GEN-LAST:event_btnrVIPActionPerformed

    private void btnIdaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdaActionPerformed
        // TODO add your handling code here:
        actualizarFechaDeVuelta();

    }//GEN-LAST:event_btnIdaActionPerformed

    private void btnIdayVueltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdayVueltaActionPerformed
        // TODO add your handling code here:
        actualizarFechaDeVuelta();

    }//GEN-LAST:event_btnIdayVueltaActionPerformed

    private void btnListo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListo1ActionPerformed
        int adultos = (int) contAdultos.getValue();
        int jovenes = (int) contJovenes.getValue();
        int niños = (int) contNiños.getValue();
        int bebes = (int) contBebes.getValue();

        // Validar adultos
        for (int i = 1; i <= adultos; i++) {
            if (!validarCamposPanel(obtenerPanelAdulto(i))) {
                JOptionPane.showMessageDialog(jPanel1, "Por favor complete todos los campos del Adulto " + i);
                return;
            }
        }

        // Validar jóvenes
        for (int i = 1; i <= jovenes; i++) {
            if (!validarCamposPanel(obtenerPanelJoven(i))) {
                JOptionPane.showMessageDialog(jPanel1, "Por favor complete todos los campos del Joven " + i);
                return;
            }
        }

        // Validar niños
        for (int i = 1; i <= niños; i++) {
            if (!validarCamposPanel(obtenerPanelNiño(i))) {
                JOptionPane.showMessageDialog(jPanel1, "Por favor complete todos los campos del Niño " + i);
                return;
            }
        }

        // Validar bebés
        for (int i = 1; i <= bebes; i++) {
            if (!validarCamposPanel(obtenerPanelBebe(i))) {
                JOptionPane.showMessageDialog(jPanel1, "Por favor complete todos los campos del Bebé " + i);
                return;
            }
        }

        int respuesta = JOptionPane.showConfirmDialog(
                jPanel1, "Por favor asegure que cada campo esté rellenado correctamente", "Confirmación",
                JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) {

            return;
        }
        JOptionPane.showMessageDialog(jPanel1, "Todos los datos están completos. ¡Puede Acceder a mis tickets!");

        //sentencia sql
        String destino = comboDestino.getSelectedItem().toString(); // Destino seleccionado
        String tipoViaje = obtenerTipoViajeSeleccionado();
        ManejodeTickets ticketManager = new ManejodeTickets();

        java.util.Date fechaIdaSeleccionada = fechaIda.getDate();
        java.util.Date fechaVueltaSeleccionada = fechaVuelta.getDate();
        // Convertir las fechas a formato java.sql.Date
        java.sql.Date fechaIdaSQL = new java.sql.Date(fechaIdaSeleccionada.getTime());
        java.sql.Date fechaVueltaSQL = (fechaVueltaSeleccionada != null)
                ? new java.sql.Date(fechaVueltaSeleccionada.getTime())
                : null;
        long tiempoInicioVuelo = System.currentTimeMillis();
        String EstadoVuelo = obtenerEstadoVuelo(tiempoInicioVuelo);
        ticketManager.insertarTicket(idUsuarioActual, destino, tipoViaje, adultos, jovenes, niños, fechaIdaSQL, fechaVueltaSQL, EstadoVuelo);
        menuPrincipal.removeAll();
        menuPrincipal.add(misTickets);
        menuPrincipal.repaint();
        menuPrincipal.revalidate();

        // Aquí puedes proceder con la acción después de validar
    }

// Método genérico para validar todos los campos de un panel
    private boolean validarCamposPanel(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JTextField) {
                JTextField textField = (JTextField) component;
                if (textField.getText().trim().isEmpty()) {
                    return false; // Campo vacío
                }
            } else if (component instanceof JDateChooser) {
                JDateChooser dateChooser = (JDateChooser) component;
                if (dateChooser.getDate() == null) {
                    return false; // Fecha no seleccionada
                }
            } else if (component instanceof JSpinner) {
                JSpinner spinner = (JSpinner) component;
                if (spinner.getValue() == null) {
                    return false; // Valor no seleccionado
                }
            }
        }
        return true; // Todos los campos están completos
    }

// Métodos para obtener paneles específicos
    private JPanel obtenerPanelAdulto(int index) {
        switch (index) {
            case 1:
                return panelAdulto1;
            case 2:
                return panelAdulto2;
            case 3:
                return panelAdulto3;
            case 4:
                return panelAdulto4;
            default:
                return null;
        }
    }

    private JPanel obtenerPanelJoven(int index) {
        switch (index) {
            case 1:
                return panelJoven1;
            case 2:
                return panelJoven2;
            case 3:
                return panelJoven3;
            case 4:
                return panelJoven4;
            default:
                return null;
        }
    }

    private JPanel obtenerPanelNiño(int index) {
        switch (index) {
            case 1:
                return panelNiño1;
            case 2:
                return panelNiño2;
            case 3:
                return panelNiño3;
            case 4:
                return panelNiño4;
            default:
                return null;
        }
    }

    private JPanel obtenerPanelBebe(int index) {
        switch (index) {
            case 1:
                return panelBebe1;
            case 2:
                return panelBebe2;
            case 3:
                return panelBebe3;
            case 4:
                return panelBebe4;
            default:
                return null;
        }


    }//GEN-LAST:event_btnListo1ActionPerformed

    private void btnCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompraActionPerformed

    }//GEN-LAST:event_btnCompraActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(navegador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(navegador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(navegador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(navegador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new navegador().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar1;
    private javax.swing.JButton btnCancelar2;
    private javax.swing.JButton btnCompra;
    private javax.swing.JButton btnContinuar;
    private javax.swing.ButtonGroup btnGrupoIdaoVuelta;
    private javax.swing.ButtonGroup btnGrupoPlanes;
    private javax.swing.JRadioButton btnIda;
    private javax.swing.JRadioButton btnIdayVuelta;
    private javax.swing.JButton btnListo1;
    private javax.swing.JRadioButton btnrBasic;
    private javax.swing.JRadioButton btnrClassic;
    private javax.swing.JRadioButton btnrVIP;
    private javax.swing.JComboBox<String> comboDestino;
    private javax.swing.JComboBox<String> comboOrigen;
    private javax.swing.JPanel compraTicket;
    private javax.swing.JSpinner contAdultos;
    private javax.swing.JSpinner contBebes;
    private javax.swing.JSpinner contJovenes;
    private javax.swing.JSpinner contNiños;
    public com.toedter.calendar.JDateChooser fechaIda;
    public com.toedter.calendar.JDateChooser fechaIda10;
    public com.toedter.calendar.JDateChooser fechaIda11;
    public com.toedter.calendar.JDateChooser fechaIda12;
    public com.toedter.calendar.JDateChooser fechaIda13;
    public com.toedter.calendar.JDateChooser fechaIda14;
    public com.toedter.calendar.JDateChooser fechaIda15;
    public com.toedter.calendar.JDateChooser fechaIda16;
    public com.toedter.calendar.JDateChooser fechaIda17;
    public com.toedter.calendar.JDateChooser fechaIda18;
    public com.toedter.calendar.JDateChooser fechaIda19;
    public com.toedter.calendar.JDateChooser fechaIda20;
    public com.toedter.calendar.JDateChooser fechaIda21;
    public com.toedter.calendar.JDateChooser fechaIda22;
    public com.toedter.calendar.JDateChooser fechaIda23;
    public com.toedter.calendar.JDateChooser fechaIda24;
    public com.toedter.calendar.JDateChooser fechaIda25;
    public com.toedter.calendar.JDateChooser fechaIda26;
    private com.toedter.calendar.JDateChooser fechaVuelta;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox21;
    private javax.swing.JComboBox<String> jComboBox22;
    private javax.swing.JComboBox<String> jComboBox23;
    private javax.swing.JComboBox<String> jComboBox24;
    private javax.swing.JComboBox<String> jComboBox25;
    private javax.swing.JComboBox<String> jComboBox26;
    private javax.swing.JComboBox<String> jComboBox27;
    private javax.swing.JComboBox<String> jComboBox28;
    private javax.swing.JComboBox<String> jComboBox29;
    private javax.swing.JComboBox<String> jComboBox30;
    private javax.swing.JComboBox<String> jComboBox31;
    private javax.swing.JComboBox<String> jComboBox32;
    private javax.swing.JComboBox<String> jComboBox33;
    private javax.swing.JComboBox<String> jComboBox34;
    private javax.swing.JComboBox<String> jComboBox35;
    private javax.swing.JComboBox<String> jComboBox36;
    private javax.swing.JComboBox<String> jComboBox37;
    private javax.swing.JComboBox<String> jComboBox38;
    private javax.swing.JComboBox<String> jComboBox39;
    private javax.swing.JComboBox<String> jComboBox40;
    private javax.swing.JComboBox<String> jComboBox41;
    private javax.swing.JComboBox<String> jComboBox42;
    private javax.swing.JComboBox<String> jComboBox43;
    private javax.swing.JComboBox<String> jComboBox44;
    private javax.swing.JComboBox<String> jComboBox45;
    private javax.swing.JComboBox<String> jComboBox46;
    private javax.swing.JComboBox<String> jComboBox47;
    private javax.swing.JComboBox<String> jComboBox48;
    private javax.swing.JComboBox<String> jComboBox49;
    private javax.swing.JComboBox<String> jComboBox50;
    private javax.swing.JComboBox<String> jComboBox51;
    private javax.swing.JComboBox<String> jComboBox52;
    private javax.swing.JComboBox<String> jComboBox53;
    private javax.swing.JComboBox<String> jComboBox54;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel150;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel152;
    private javax.swing.JLabel jLabel153;
    private javax.swing.JLabel jLabel154;
    private javax.swing.JLabel jLabel155;
    private javax.swing.JLabel jLabel156;
    private javax.swing.JLabel jLabel157;
    private javax.swing.JLabel jLabel158;
    private javax.swing.JLabel jLabel159;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel160;
    private javax.swing.JLabel jLabel161;
    private javax.swing.JLabel jLabel162;
    private javax.swing.JLabel jLabel163;
    private javax.swing.JLabel jLabel164;
    private javax.swing.JLabel jLabel165;
    private javax.swing.JLabel jLabel166;
    private javax.swing.JLabel jLabel167;
    private javax.swing.JLabel jLabel168;
    private javax.swing.JLabel jLabel169;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel170;
    private javax.swing.JLabel jLabel171;
    private javax.swing.JLabel jLabel172;
    private javax.swing.JLabel jLabel173;
    private javax.swing.JLabel jLabel174;
    private javax.swing.JLabel jLabel175;
    private javax.swing.JLabel jLabel176;
    private javax.swing.JLabel jLabel177;
    private javax.swing.JLabel jLabel178;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField38;
    private javax.swing.JTextField jTextField39;
    private javax.swing.JTextField jTextField40;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField43;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField50;
    private javax.swing.JTextField jTextField51;
    private javax.swing.JTextField jTextField52;
    private javax.swing.JLabel labelDestino;
    private javax.swing.JPanel menuPrincipal;
    private javax.swing.JPanel misTickets;
    private javax.swing.JPanel panelAdulto1;
    private javax.swing.JPanel panelAdulto2;
    private javax.swing.JPanel panelAdulto3;
    private javax.swing.JPanel panelAdulto4;
    private javax.swing.JPanel panelBebe1;
    private javax.swing.JPanel panelBebe2;
    private javax.swing.JPanel panelBebe3;
    private javax.swing.JPanel panelBebe4;
    private javax.swing.JPanel panelJoven1;
    private javax.swing.JPanel panelJoven2;
    private javax.swing.JPanel panelJoven3;
    private javax.swing.JPanel panelJoven4;
    private javax.swing.JPanel panelNiño1;
    private javax.swing.JPanel panelNiño2;
    private javax.swing.JPanel panelNiño3;
    private javax.swing.JPanel panelNiño4;
    private javax.swing.JPanel pasajeros;
    private javax.swing.JLabel tipoDeVuelo;
    private javax.swing.JLabel txtAdultos;
    private javax.swing.JLabel txtBebes;
    private javax.swing.JLabel txtEstadoVuelo;
    private javax.swing.JLabel txtIda;
    private javax.swing.JLabel txtIda1;
    private javax.swing.JLabel txtIda2;
    private javax.swing.JLabel txtJovenes;
    private javax.swing.JLabel txtNiños;
    private javax.swing.JLabel txtTotal;
    private javax.swing.JLabel txtVuelta;
    private javax.swing.JLabel txtprecio;
    // End of variables declaration//GEN-END:variables

}
