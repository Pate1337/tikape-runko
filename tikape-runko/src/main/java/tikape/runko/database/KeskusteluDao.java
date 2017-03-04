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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Alue;
import tikape.runko.domain.Kayttaja;
import tikape.runko.domain.Keskustelu;

/**
 *
 * @author paavo
 */
public class KeskusteluDao implements Dao<Keskustelu, Integer> {
    private Database database;
    
    public KeskusteluDao(Database database) {
        this.database = database;
    }
    

    @Override
    public Keskustelu findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE id = ?");
        stmt.setObject(1, key);
        
        ResultSet rs = stmt.executeQuery();
        
        if (!rs.next())
            return null;
    
        int id = rs.getInt("id");
        String otsikko = rs.getString("otsikko");
        int alueID = rs.getInt("alue_id");
        
        ResultSet viestit = connection.createStatement().executeQuery("SELECT aika FROM viesti WHERE keskustelu_id = " + Integer.toString(key) + " ORDER BY aika ASC LIMIT 1");
        
        Timestamp uusin = Timestamp.valueOf(viestit.getString("aika"));
        
        viestit.close();
        rs.close();
        stmt.close();
        connection.close();
        
        return new Keskustelu(id, otsikko, alueID, uusin);
    }

    @Override
    public List<Keskustelu> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Keskustelu> etsiAlueenKeskustelut(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE alue_id = ?");

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        List<Keskustelu> keskustelut = new ArrayList<>();

        while (rs.next()) {
            int keskusteluID = rs.getInt("id");
            String otsikko = rs.getString("otsikko");
            int alueID = rs.getInt("alue_id");
            
            stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE keskustelu_id = ? ORDER BY aika Asc LIMIT 1");
            stmt.setInt(1, keskusteluID);
            ResultSet aika = stmt.executeQuery();
            Timestamp uusinViesti = Timestamp.valueOf(aika.getString("aika"));

            aika.close();
            
            keskustelut.add(new Keskustelu(keskusteluID, otsikko, alueID, uusinViesti));
        }

        rs.close();
        stmt.close();
        connection.close();
        return keskustelut;
    }
    
    public void luoKeskustelu(String otsikko, int alueId, int kayttajaId, String teksti) throws SQLException {
        Connection connection = database.getConnection();
        int keskusteluId = connection.createStatement().executeQuery("SELECT Count(*) as uusin_id FROM Keskustelu").getInt("uusin_id");
        
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelu (id, otsikko, alue_id) VALUES (?, ?,?)");
        stmt.setObject(1, keskusteluId);
        stmt.setObject(2, otsikko);
        stmt.setObject(3, alueId);
        
        stmt.execute();
        
        PreparedStatement stmt2 = connection.prepareStatement("INSERT INTO Viesti(kayttaja_id, keskustelu_id, teksti, aika) VALUES (?,?,?,datetime())");
        stmt2.setObject(1, kayttajaId);
        stmt2.setObject(2, keskusteluId);
        stmt2.setObject(3, teksti);
        
        stmt2.execute();
        stmt.close();
        stmt2.close();
        connection.close();
    }
    
}
