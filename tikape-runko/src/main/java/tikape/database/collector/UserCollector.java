/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.database.collector;

import java.sql.ResultSet;
import java.sql.SQLException;
import tikape.runko.database.Collector;
import tikape.runko.domain.Kayttaja;

/**
 *
 * @author hcpaavo
 */
public class UserCollector implements Collector<Kayttaja> {
    
    @Override
    public Kayttaja collect(ResultSet rs) throws SQLException {
        return new Kayttaja(rs.getInt("id"), rs.getString("nimi"), rs.getString("tunnus"), rs.getString("salasana"));
    }
    
}
