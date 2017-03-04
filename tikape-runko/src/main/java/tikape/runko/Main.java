package tikape.runko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Session;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AlueDao;
import tikape.runko.database.Database;
import tikape.runko.database.KayttajaDao;
import tikape.runko.database.KeskusteluDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Alue;
import tikape.runko.domain.Kayttaja;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:src/main/resources/foorumi.db");
        database.setDebugMode(true);

        KayttajaDao KayttajaDao = new KayttajaDao(database);
        AlueDao alueDao = new AlueDao(database);
        KeskusteluDao keskusteluDao = new KeskusteluDao(database);
        ViestiDao viestiDao = new ViestiDao(database);
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "etusivu");
        }, new ThymeleafTemplateEngine());

        
        get("/kirjautuminen", (req, res) -> {
            HashMap map = new HashMap<>();
            
            return new ModelAndView(map, "kirjautuminen");
        }, new ThymeleafTemplateEngine());
        
        get("/kirjautuminen2", (req, res) -> {
            HashMap map = new HashMap<>();
            
            return new ModelAndView(map, "kirjautuminen2");
        }, new ThymeleafTemplateEngine());
        
        post("/login", (req, res) -> {
            String tunnus = req.queryParams("tunnus");
            String salasana = req.queryParams("salasana");

            Kayttaja user = KayttajaDao.findByUsernameAndPassword(tunnus, salasana);

            if (user == null) {
                res.redirect("/kirjautuminen2");
                return "";
            }

            req.session(true).attribute("user", user);
            res.redirect("/s/users/" + user.getId() + "/");
            return "";
        });
        
        get("/logout", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());
            req.session(true).attribute("user", null);

            return new ModelAndView(map, "etusivu");
        }, new ThymeleafTemplateEngine());
        
        before((req, res) -> {
            if(!req.url().contains("/s/")) {
                return;
            }
            
            Session sess = req.session();
            
            if (sess.attribute("user") == null) {
                sess.invalidate();
                res.redirect("/");
            }
        });
        
        get("/s/users/:id/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("kayttaja", KayttajaDao.findOne(Integer.parseInt(req.params(":id"))));
            map.put("alueet", alueDao.findAll());

            // get 10 chat messages and add them to the map
            // NB! use "tsats" as the name for the messages
            
            
            return new ModelAndView(map, "etusivu2");
        }, new ThymeleafTemplateEngine());
        //TÄTÄ MUUTETTU
        get("/s/users/:kayttajaid/alueet/:alueid/", (req, res) -> {
            HashMap map = new HashMap<>();
            int kayttajaID = Integer.parseInt(req.params(":kayttajaid"));
            int alueID = Integer.parseInt(req.params(":alueid"));
            map.put("kayttaja", KayttajaDao.findOne(kayttajaID));
            map.put("alue", alueDao.findOne(alueID));
            map.put("alueenKeskustelut", keskusteluDao.etsiAlueenKeskustelut(alueID));
            return new ModelAndView(map, "alueet2");
        }, new ThymeleafTemplateEngine());
        
        get("/alueet/:id/", (req, res) -> {
            HashMap map = new HashMap<>();
            int alueID = Integer.parseInt(req.params(":id"));
            map.put("alue", alueDao.findOne(alueID));
            map.put("alueenKeskustelut", keskusteluDao.etsiAlueenKeskustelut(alueID));
            return new ModelAndView(map, "alueet");
        }, new ThymeleafTemplateEngine());
        
        
        
        get("/rekisteroidy", (req, res) -> {
            HashMap map = new HashMap<>();
            
            return new ModelAndView(map, "rekisteroidy");
        }, new ThymeleafTemplateEngine());
        
        post("/luotunnus", (req, res) -> {
            String nimi = req.queryParams("nimi");
            String tunnus = req.queryParams("tunnus");
            String salasana = req.queryParams("salasana");
            
            KayttajaDao.luoKayttaja(nimi, tunnus, salasana);
            res.redirect("/");
            return "";
            
        });
        
        get("/alueet/:alueid/keskustelut/:keskusteluid/", (req, res) -> {
            HashMap map = new HashMap<>();
            int alueID = Integer.parseInt(req.params(":alueid"));
            int keskusteluID = Integer.parseInt(req.params(":keskusteluid"));
            map.put("alue", alueDao.findOne(alueID));
            map.put("keskustelu", keskusteluDao.findOne(keskusteluID));
            map.put("keskustelunViestit", viestiDao.etsiKeskustelunViestit(keskusteluID));
            return new ModelAndView(map, "keskustelu");
        }, new ThymeleafTemplateEngine());
        
        get("/s/users/:kayttajaid/alueet/:alueid/keskustelut/:keskusteluid/", (req, res) -> {
            HashMap map = new HashMap<>();
            int kayttajaID = Integer.parseInt(req.params(":kayttajaid"));
            int alueID = Integer.parseInt(req.params(":alueid"));
            int keskusteluID = Integer.parseInt(req.params(":keskusteluid"));
            map.put("kayttaja", KayttajaDao.findOne(kayttajaID));
            map.put("alue", alueDao.findOne(alueID));
            map.put("keskustelu", keskusteluDao.findOne(keskusteluID));
            map.put("keskustelunViestit", viestiDao.etsiKeskustelunViestit(keskusteluID));
            return new ModelAndView(map, "keskustelu2");
        }, new ThymeleafTemplateEngine());
        
        post("/lahetaviesti/:kayttajaid/:alueid/:keskusteluid/", (req, res) -> {
            HashMap map = new HashMap<>();
            String viesti = req.queryParams("viesti");
            int kayttajaID = Integer.parseInt(req.params(":kayttajaid"));
            int alueID = Integer.parseInt(req.params(":alueid"));
            int keskusteluID = Integer.parseInt(req.params(":keskusteluid"));
            String kayttajaTunnus = KayttajaDao.findOne(kayttajaID).getTunnus();
            
            viesti = viesti + " t: " + kayttajaTunnus;
            
            viestiDao.luoViesti(kayttajaID, keskusteluID, viesti);
            
            
            res.redirect("/s/users/" + kayttajaID + "/alueet/" + alueID + "/keskustelut/" + keskusteluID + "/");
            return "";
        });
 
    }
}
