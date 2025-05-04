package fr.marieteamclient.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe représentant un bateau dans l'application.
 * Un bateau est caractérisé par son identifiant, son capitaine, son nom, sa marque,
 * ses dimensions (longueur et largeur) et sa vitesse.
 */
public class Bateau {
    private int idBateau;
    private Integer idCapitaine;
    private String nomBateau;
    private String marque;
    private Float longueur;
    private Float largeur;
    private Integer vitesse;

    /**
     * Constructeur de la classe Bateau.
     *
     * @param idBateau L'identifiant unique du bateau
     * @param idCapitaine L'identifiant du capitaine du bateau
     * @param nomBateau Le nom du bateau
     * @param marque La marque du bateau
     * @param longueur La longueur du bateau en mètres
     * @param largeur La largeur du bateau en mètres
     * @param vitesse La vitesse du bateau en nœuds
     */
    public Bateau(int idBateau, Integer idCapitaine, String nomBateau, String marque, Float longueur, Float largeur, Integer vitesse) {
        this.idBateau = idBateau;
        this.idCapitaine = idCapitaine;
        this.nomBateau = nomBateau;
        this.marque = marque;
        this.longueur = longueur;
        this.largeur = largeur;
        this.vitesse = vitesse;
    }

    /**
     * Retourne l'identifiant du bateau.
     *
     * @return L'identifiant du bateau
     */
    public int getIdBateau() { return idBateau; }

    /**
     * Retourne l'identifiant du capitaine.
     *
     * @return L'identifiant du capitaine
     */
    public Integer getIdCapitaine() { return idCapitaine; }

    /**
     * Retourne le nom du bateau.
     *
     * @return Le nom du bateau
     */
    public String getNomBateau() { return nomBateau; }

    /**
     * Retourne la marque du bateau.
     *
     * @return La marque du bateau
     */
    public String getMarque() { return marque; }

    /**
     * Retourne la longueur du bateau.
     *
     * @return La longueur du bateau en mètres
     */
    public Float getLongueur() { return longueur; }

    /**
     * Retourne la largeur du bateau.
     *
     * @return La largeur du bateau en mètres
     */
    public Float getLargeur() { return largeur; }

    /**
     * Retourne la vitesse du bateau.
     *
     * @return La vitesse du bateau en nœuds
     */
    public Integer getVitesse() { return vitesse; }

    /**
     * Définit l'identifiant du bateau.
     *
     * @param idBateau Le nouvel identifiant du bateau
     */
    public void setIdBateau(int idBateau) { this.idBateau = idBateau; }

    /**
     * Définit l'identifiant du capitaine.
     *
     * @param idCapitaine Le nouvel identifiant du capitaine
     */
    public void setIdCapitaine(Integer idCapitaine) { this.idCapitaine = idCapitaine; }

    /**
     * Définit le nom du bateau.
     *
     * @param nomBateau Le nouveau nom du bateau
     */
    public void setNomBateau(String nomBateau) { this.nomBateau = nomBateau; }

    /**
     * Définit la marque du bateau.
     *
     * @param marque La nouvelle marque du bateau
     */
    public void setMarque(String marque) { this.marque = marque; }

    /**
     * Définit la longueur du bateau.
     *
     * @param longueur La nouvelle longueur du bateau en mètres
     */
    public void setLongueur(Float longueur) { this.longueur = longueur; }

    /**
     * Définit la largeur du bateau.
     *
     * @param largeur La nouvelle largeur du bateau en mètres
     */
    public void setLargeur(Float largeur) { this.largeur = largeur; }

    /**
     * Définit la vitesse du bateau.
     *
     * @param vitesse La nouvelle vitesse du bateau en nœuds
     */
    public void setVitesse(Integer vitesse) { this.vitesse = vitesse; }
}
