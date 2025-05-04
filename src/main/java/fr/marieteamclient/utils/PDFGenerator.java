package fr.marieteamclient.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import fr.marieteamclient.constants.Constants;
import fr.marieteamclient.database.DatabaseConnection;
import fr.marieteamclient.models.Bateau;
import fr.marieteamclient.models.Equipement;

/**
 * Classe utilitaire pour la génération de documents PDF.
 * Permet de créer des rapports PDF pour les bateaux et leurs équipements.
 */
public class PDFGenerator {
    
    /**
     * Récupère la liste des équipements associés à un bateau.
     *
     * @param idBateau L'identifiant du bateau
     * @return Une liste des libellés des équipements du bateau
     * @throws SQLException Si une erreur survient lors de l'accès à la base de données
     */
    private static List<String> getEquipementsForBateau(int idBateau) throws SQLException {
        List<String> equipements = new ArrayList<>();
        DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
        Connection conn = database.getConnection();
        
        try {
            String query = "SELECT e.labelle FROM equipement e " +
                          "JOIN possede p ON e.idEquipement = p.idEquipement " +
                          "WHERE p.idBateau = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idBateau);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                equipements.add(rs.getString("labelle"));
            }
            
            rs.close();
            stmt.close();
        } finally {
            conn.close();
        }
        return equipements;
    }
    
    /**
     * Génère un PDF contenant les informations des bateaux et leurs équipements.
     * Crée un document PDF avec des images, des descriptions et des listes d'équipements.
     *
     * @param bateaux La liste des bateaux à inclure dans le PDF
     * @param filePath Le chemin où sauvegarder le fichier PDF
     */
    public static void generateBateauxPDF(List<Bateau> bateaux, String filePath) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            
            // Titre du document
            Paragraph title = new Paragraph("Brochure des bateaux voyageurs")
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setMarginBottom(20);
            document.add(title);

            for (Bateau bateau : bateaux) {
                // Création d'une nouvelle page pour chaque bateau
                document.add(new AreaBreak());
                
                // Création d'un div pour chaque bateau
                Div bateauDiv = new Div();
                bateauDiv.setMarginBottom(30);
                
                try {
                    // Ajout de l'image du bateau si disponible
                    if (bateau.getImage() != null && !bateau.getImage().isEmpty()) {
                        Image img = new Image(ImageDataFactory.create(bateau.getImage()));
                        img.setWidth(UnitValue.createPercentValue(80));
                        img.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                        img.setMarginBottom(10);
                        bateauDiv.add(img);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image pour le bateau: " + bateau.getNomBateau());
                    e.printStackTrace();
                }

                // Informations du bateau
                bateauDiv.add(new Paragraph("Nom du bateau : " + bateau.getNomBateau())
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

                bateauDiv.add(new Paragraph("Marque : " + bateau.getMarque())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2));
                bateauDiv.add(new Paragraph("Longueur : " + bateau.getLongueur() + " mètres")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2));
                bateauDiv.add(new Paragraph("Largeur : " + bateau.getLargeur() + " mètres")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2));
                bateauDiv.add(new Paragraph("Vitesse : " + bateau.getVitesse() + " noeuds")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

                // Liste des équipements
                bateauDiv.add(new Paragraph("Liste des équipements du bateau :")
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

                try {
                    List<String> equipements = getEquipementsForBateau(bateau.getIdBateau());
                    for (String equipement : equipements) {
                        bateauDiv.add(new Paragraph("- " + equipement)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginBottom(2));
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la récupération des équipements pour le bateau " + bateau.getNomBateau());
                    e.printStackTrace();
                }

                document.add(bateauDiv);
            }

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Génère un PDF contenant la liste des équipements.
     * Crée un document PDF avec un tableau des équipements.
     *
     * @param equipements La liste des équipements à inclure dans le PDF
     * @param filePath Le chemin où sauvegarder le fichier PDF
     * @throws IOException Si une erreur survient lors de la création du PDF
     */
    public static void generateEquipementsPDF(List<Equipement> equipements, String filePath) throws IOException {
        // Création du document PDF
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Ajout du titre
        Paragraph title = new Paragraph("Liste des Équipements")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20);
        document.add(title);

        // Création du tableau
        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        
        // Ajout des en-têtes
        table.addHeaderCell("ID");
        table.addHeaderCell("Nom de l'Équipement");

        // Ajout des données
        for (Equipement equipement : equipements) {
            table.addCell(String.valueOf(equipement.getIdEquipement()));
            table.addCell(equipement.getLabelle());
        }

        document.add(table);
        document.close();
    }
} 