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
public class Keskustelu {
    private int id;
    private String otsikko;
    private int alueID;
    private Timestamp uusinViesti;
    
    public Keskustelu(int id, String otsikko, int alueID, Timestamp uusinVTimestamp) {
        this.id = id;
        this.otsikko = otsikko;
        this.alueID = alueID;
    }
    public String getOtsikko() {
        return this.otsikko;
    }
    public int getId() {
        return this.id;
    }
    
}
