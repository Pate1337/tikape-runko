/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

import java.sql.Timestamp;


/**
 *
 * @author paavo
 */
public class Viesti {
    private int id;
    private int kayttajaID;
    private int keskusteluID;
    private String teksti;
    private Timestamp aika;
    
    public Viesti(int id, int kayttajaID, int keskusteluID, String teksti, Timestamp aika) {
        this.id = id;
        this.kayttajaID = kayttajaID;
        this.keskusteluID = keskusteluID;
        this.teksti = teksti;
        this.aika = aika;
    }
    
    public String getTeksti() {
        return this.teksti;
    }
}
