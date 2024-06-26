package co.edu.sena.credifamiliacf.controller;

import co.edu.sena.credifamiliacf.model.City;
import co.edu.sena.credifamiliacf.model.Client;
import co.edu.sena.credifamiliacf.persistence.ConnectionJDBC;

import java.io.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "clientList", value = "/client-list")
public class ClientListController extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConnectionJDBC conn = new ConnectionJDBC();
        List<Client> listaUsuarios = getAllUsers();
        HttpSession miSesion = request.getSession();
        miSesion.setAttribute("listaUsuarios",listaUsuarios);

        response.sendRedirect("usuarios.jsp");
    }
    //dao facade factory
    public List<Client> getAllUsers() {
        List<Client> clientList = new ArrayList<>();
        Connection connection = ConnectionJDBC.getConnection();

        try {
            String query = "SELECT * FROM clientes";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("numero_documento");
                String nombre = resultSet.getString("nombre");
                String apellido = resultSet.getString("apellido");
                LocalDate fechaNacimiento = resultSet.getDate("fecha_nacimiento").toLocalDate();
                int ciudad = resultSet.getInt("ciudad_id");
                String correoElectronico = resultSet.getString("correo_electronico");
                String telefono = resultSet.getString("telefono");
                String ocupacion = resultSet.getString("ocupacion");
                Boolean viabilidad = resultSet.getBoolean("viable");

                ResultSet foreignResult = connection.prepareStatement("select * from ciudad where id ="+ciudad).executeQuery();
                foreignResult.next();

                String nombreCiudad = foreignResult.getString("nombre_ciudad");
                int idCiudad = foreignResult.getInt("id");
                City ciudadfk = new City(idCiudad, nombreCiudad);

                Client user = new Client(id, nombre,apellido,fechaNacimiento,ciudadfk,correoElectronico,telefono,ocupacion);
                user.setViabilidad(viabilidad);
                clientList.add(user);
            }

            // Cierra recursos
            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientList;
    }


}