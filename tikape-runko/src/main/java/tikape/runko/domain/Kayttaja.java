package tikape.runko.domain;

public class Kayttaja {

    private Integer id;
    private String nimi;
    private String tunnus;
    private String salasana;

    public Kayttaja(Integer id, String nimi, String tunnus, String salasana) {
        this.id = id;
        this.nimi = nimi;
        this.tunnus = tunnus;
        this.salasana = salasana;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    public String getTunnus() {
        return this.tunnus;
    }

}
