package fr.marieteamclient.models;

/**
 * Classe représentant un équipement dans l'application.
 * Un équipement est caractérisé par un identifiant unique et un libellé.
 */
public class Equipement {
    private int idEquipement;
    private String labelle;

    /**
     * Constructeur de la classe Equipement.
     *
     * @param idEquipement L'identifiant unique de l'équipement
     * @param labelle Le libellé de l'équipement
     */
    public Equipement(int idEquipement, String labelle) {
        this.idEquipement = idEquipement;
        this.labelle = labelle;
    }

    /**
     * Retourne l'identifiant de l'équipement.
     *
     * @return L'identifiant de l'équipement
     */
    public int getIdEquipement() { return idEquipement; }

    /**
     * Retourne le libellé de l'équipement.
     *
     * @return Le libellé de l'équipement
     */
    public String getLabelle() { return labelle; }

    /**
     * Définit l'identifiant de l'équipement.
     *
     * @param idEquipement Le nouvel identifiant de l'équipement
     */
    public void setIdEquipement(int idEquipement) { this.idEquipement = idEquipement; }

    /**
     * Définit le libellé de l'équipement.
     *
     * @param labelle Le nouveau libellé de l'équipement
     */
    public void setLabelle(String labelle) { this.labelle = labelle; }
} 