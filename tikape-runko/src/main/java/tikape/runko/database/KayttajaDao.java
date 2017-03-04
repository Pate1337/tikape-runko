/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Kayttaja;
import tikape.database.collector.UserCollector;

public class KayttajaDao implements Dao<Kayttaja, Integer> {

    private Database database;

    public KayttajaDao(Database database) {
        this.database = database;
    }

    @Override
    public Kayttaja findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");
        String tunnus = rs.getString("tunnus");
        String salasana = rs.getString("salasana");

        Kayttaja o = new Kayttaja(id, nimi, tunnus, salasana);

        rs.close();
        stmt.close();
        connection.close();

        return o;
    }
    public Kayttaja findByUsernameAndPassword(String username, String password) throws SQLException {
        String koodi = "SELECT * FROM Kayttaja WHERE tunnus = ? AND salasana = ?";
        UserCollector keraaja = new UserCollector();
        List<Kayttaja> kayttajat = this.database.queryAndCollect(koodi, keraaja, username, password);
        System.out.println(kayttajat.size());
        // TODO: implement
        if (kayttajat.isEmpty()) {
            return null;
        }
        return kayttajat.get(0);
    }

    @Override
    public List<Kayttaja> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja");

        ResultSet rs = stmt.executeQuery();
        List<Kayttaja> kayttajat = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimi");
            String tunnus = rs.getString("tunnus");
            String salasana = rs.getString("salasana");

            kayttajat.add(new Kayttaja(id, nimi, tunnus, salasana));
        }

        rs.close();
        stmt.close();
        connection.close();

        return kayttajat;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }
    
    public void luoKayttaja(String nimi, String tunnus, String salasana) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Kayttaja(nimi, tunnus, salasana, super) VALUES (?, ?, ?, ?)");
        
        stmt.setString(1, nimi);
        stmt.setString(2, tunnus);
        stmt.setString(3, salasana);
        stmt.setString(4, "FALSE");
        
        stmt.execute();
        
        stmt.close();
        connection.close();
    }

}
