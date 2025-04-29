package fr.marieteamclient.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bateau {
    private int idBateau;
    private Integer idCapitaine;
    private String nomBateau;
    private String marque;
    private Float longueur;
    private Float largeur;
    private Integer vitesse;

    public Bateau(int idBateau, Integer idCapitaine, String nomBateau, String marque, Float longueur, Float largeur, Integer vitesse) {
        this.idBateau = idBateau;
        this.idCapitaine = idCapitaine;
        this.nomBateau = nomBateau;
        this.marque = marque;
        this.longueur = longueur;
        this.largeur = largeur;
        this.vitesse = vitesse;
    }

    // Getters
    public int getIdBateau() { return idBateau; }
    public Integer getIdCapitaine() { return idCapitaine; }
    public String getNomBateau() { return nomBateau; }
    public String getMarque() { return marque; }
    public Float getLongueur() { return longueur; }
    public Float getLargeur() { return largeur; }
    public Integer getVitesse() { return vitesse; }

    // Setters
    public void setIdBateau(int idBateau) { this.idBateau = idBateau; }
    public void setIdCapitaine(Integer idCapitaine) { this.idCapitaine = idCapitaine; }
    public void setNomBateau(String nomBateau) { this.nomBateau = nomBateau; }
    public void setMarque(String marque) { this.marque = marque; }
    public void setLongueur(Float longueur) { this.longueur = longueur; }
    public void setLargeur(Float largeur) { this.largeur = largeur; }
    public void setVitesse(Integer vitesse) { this.vitesse = vitesse; }
}
