package fr.marieteamclient.models;

public class Equipement {
    private int idEquipement;
    private String labelle;

    public Equipement(int idEquipement, String labelle) {
        this.idEquipement = idEquipement;
        this.labelle = labelle;
    }

    // Getters
    public int getIdEquipement() { return idEquipement; }
    public String getLabelle() { return labelle; }

    // Setters
    public void setIdEquipement(int idEquipement) { this.idEquipement = idEquipement; }
    public void setLabelle(String labelle) { this.labelle = labelle; }
} 