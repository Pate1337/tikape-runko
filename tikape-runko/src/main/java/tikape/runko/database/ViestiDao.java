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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Keskustelu;
import tikape.runko.domain.Viesti;
import tikape.runko.database.KeskusteluDao;

/**
 *
 * @author paavo
 */
public class ViestiDao implements Dao<Viesti, Integer> {

    private Database database;

    public ViestiDao(Database database) {
        this.database = database;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        int id = rs.getInt("id");
        int keskusteluId = rs.getInt("keskustelu_id");
        int kayttajaId = rs.getInt("kayttaja_id");
        String teksti = rs.getString("teksti");
        Timestamp aika = Timestamp.valueOf(rs.getString("aika"));

        rs.close();
        stmt.close();
        conn.close();

        return new Viesti(id, kayttajaId, keskusteluId, teksti, aika);
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Viesti");

        return null;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Timestamp keskustelunUusimmanViestinAika(int key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Viesti WHERE keskustelu_id = ? ORDER BY aika Desc LIMIT 1");
        stmt.setObject(1, key);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        Timestamp aika = Timestamp.valueOf(rs.getString("aika"));

        rs.close();
        stmt.close();
        conn.close();

        return aika;
    }

    public Timestamp alueenUusimmanViestinAika(int key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Keskustelu WHERE alue_id = ?");
        stmt.setInt(1, key);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        List<Keskustelu> keskustelut = new ArrayList<>();
        Timestamp uusin = Timestamp.from(Instant.EPOCH);

        while (rs.next()) {
            ResultSet aika = conn.createStatement().executeQuery("SELECT * FROM Viesti WHERE keskustelu_id = " + rs.getString("id") + " ORDER BY aika Asc LIMIT 1");

            Timestamp uusinViesti = Timestamp.valueOf(aika.getString("aika"));
            if (uusinViesti.after(uusin)) {
                uusin = uusinViesti;
            }

            aika.close();
        }

        rs.close();
        stmt.close();
        conn.close();

        return uusin;
    }

    public List<Viesti> etsiKeskustelunViestit(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE keskustelu_id = ? ORDER BY aika Asc");

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        List<Viesti> viestit = new ArrayList<>();

        while (rs.next()) {
            int viestiID = rs.getInt("id");
            int kayttajaID = rs.getInt("kayttaja_id");
            int keskusteluID = rs.getInt("keskustelu_id");
            String teksti = rs.getString("teksti");
            Timestamp aika = Timestamp.valueOf(rs.getString("aika"));

            viestit.add(new Viesti(viestiID, kayttajaID, keskusteluID, teksti, aika));
        }

        rs.close();
        stmt.close();
        connection.close();
        return viestit;
    }

    public void luoViesti(int kayttajaId, int keskusteluId, String teksti) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti(kayttaja_id, keskustelu_id, teksti, aika) VALUES (?,?,?,datetime())");
        stmt.setObject(1, kayttajaId);
        stmt.setObject(2, keskusteluId);
        stmt.setObject(3, teksti);

        stmt.execute();
        
        stmt.close();
        connection.close();
    }
}
