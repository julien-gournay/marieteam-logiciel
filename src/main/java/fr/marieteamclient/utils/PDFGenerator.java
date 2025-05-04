package fr.marieteamclient.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.io.image.ImageDataFactory;
import fr.marieteamclient.models.Bateau;
import fr.marieteamclient.models.Equipement;
import fr.marieteamclient.database.DatabaseConnection;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT e.labelle FROM equipement e " +
                          "JOIN possede p ON e.idEquipement = p.idEquipement " +
                          "WHERE p.idBateau = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idBateau);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                equipements.add(rs.getString("labelle"));
            }
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
                // Création d'un div pour chaque bateau
                Div bateauDiv = new Div();
                bateauDiv.setMarginBottom(30);
                
                try {
                    // Ajout de l'image du bateau si disponible
                    String imagePath = "src/main/resources/fr/marieteamclient/images/" + bateau.getNomBateau().toLowerCase().replace(" ", "_") + ".jpg";
                    Image img = new Image(ImageDataFactory.create(imagePath));
                    img.setWidth(UnitValue.createPercentValue(80));
                    img.setMarginBottom(10);
                    bateauDiv.add(img);
                } catch (Exception e) {
                    System.err.println("Image non trouvée pour le bateau: " + bateau.getNomBateau());
                }

                // Informations du bateau
                bateauDiv.add(new Paragraph("Nom du bateau : " + bateau.getNomBateau())
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(5));

                bateauDiv.add(new Paragraph("Longueur : " + bateau.getLongueur() + " mètres")
                    .setMarginBottom(2));
                bateauDiv.add(new Paragraph("Largeur : " + bateau.getLargeur() + " mètres")
                    .setMarginBottom(2));
                bateauDiv.add(new Paragraph("Vitesse : " + bateau.getVitesse() + " noeuds")
                    .setMarginBottom(10));

                // Liste des équipements
                bateauDiv.add(new Paragraph("Liste des équipements du bateau :")
                    .setBold()
                    .setMarginBottom(5));

                try {
                    List<String> equipements = getEquipementsForBateau(bateau.getIdBateau());
                    for (String equipement : equipements) {
                        bateauDiv.add(new Paragraph("- " + equipement)
                            .setMarginLeft(20)
                            .setMarginBottom(2));
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la récupération des équipements pour le bateau " + bateau.getNomBateau());
                    e.printStackTrace();
                }

                // Ajout d'une ligne de séparation
                bateauDiv.add(new Paragraph("").setBorderBottom(new SolidBorder(1))
                    .setMarginTop(20));

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