import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * CLASSE ExcelExporter
 *
 * Convertit un fichier CSV (format de createTextFile) en classeur Excel (.xlsx).
 *
 * Structure du fichier Excel :
 *   - 1 feuille nommée "MNIST_Data"
 *   - Ligne 0 : en-têtes (pixel0, pixel1, ..., pixel783, label)
 *   - Lignes 1..N : données (une image par ligne)
 *
 * Dépendance : Apache POI (voir pom.xml / libs)
 */
public class ExcelExporter extends MNISTProvider {

    // ── Attributs ────────────────────────────────────────────────────────────────

    /** Chemin du fichier CSV source */
    private String csvPath;

    /** Chemin du fichier Excel destination */
    private String excelPath;

    // ── Constructeur ─────────────────────────────────────────────────────────────

    /**
     * @param csvPath   chemin vers chiffres.txt (CSV source)
     * @param excelPath chemin de sortie (ex: "chiffres.xlsx")
     */
    public ExcelExporter(String csvPath, String excelPath) {
        super();
        this.csvPath   = csvPath;
        this.excelPath = excelPath;
    }

    // ── Implémentation de DataProcessor ─────────────────────────────────────────

    /**
     * load() : lit le fichier CSV et compte le nombre de lignes.
     */
    @Override
    public void load() throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            int count = 0;
            while (br.readLine() != null) count++;
            this.nbImages = count;
        }
        System.out.println("ExcelExporter : " + nbImages + " lignes CSV chargées.");
    }

    /**
     * export() : alias de createExcelFile(excelPath).
     */
    @Override
    public void export() throws Exception {
        createExcelFile(excelPath);
    }

    // ── MÉTHODE PRINCIPALE : createExcelFile ─────────────────────────────────────

    /**
     * Convertit le fichier CSV en classeur Excel.
     *
     * Astuce performance : on utilise SXSSFWorkbook pour les très grands fichiers
     * (streaming), mais XSSFWorkbook suffit pour 800 lignes.
     *
     * @param nomFichier chemin de destination du fichier .xlsx
     * @throws IOException              si lecture/écriture échoue
     * @throws DataFormatMismatchException si une ligne CSV est malformée
     */
    public void createExcelFile(String nomFichier)
            throws IOException, DataFormatMismatchException {

        // Création du classeur Excel en mémoire
        try (Workbook workbook = new XSSFWorkbook();
             BufferedReader br = new BufferedReader(new FileReader(csvPath))) {

            // Création d'une feuille de calcul
            Sheet sheet = workbook.createSheet("MNIST_Data");

            // ── Ligne d'en-tête ──────────────────────────────────────────────────
            Row headerRow = sheet.createRow(0);

            // Style pour l'en-tête (fond bleu, texte blanc, gras)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int col = 0; col < 784; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue("pixel" + col);
                cell.setCellStyle(headerStyle);
            }
            // Dernière colonne : label
            Cell labelHeader = headerRow.createCell(784);
            labelHeader.setCellValue("label");
            labelHeader.setCellStyle(headerStyle);

            // ── Données ──────────────────────────────────────────────────────────
            String line;
            int rowIndex = 1; // on commence après l'en-tête

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                // Validation du format
                if (parts.length != 785) {
                    throw new DataFormatMismatchException(
                            rowIndex,
                            "attendu 785 champs, trouvé " + parts.length
                    );
                }

                Row row = sheet.createRow(rowIndex);

                // 784 valeurs numériques
                for (int col = 0; col < 784; col++) {
                    try {
                        int val = Integer.parseInt(parts[col].trim());
                        row.createCell(col).setCellValue(val);
                    } catch (NumberFormatException e) {
                        throw new DataFormatMismatchException(
                                rowIndex,
                                "valeur non entière colonne " + col + " : '" + parts[col] + "'"
                        );
                    }
                }

                // Label texte (dernière colonne)
                row.createCell(784).setCellValue(parts[784].trim());
                rowIndex++;
            }

            this.nbImages = rowIndex - 1;

            // ── Écriture sur disque ───────────────────────────────────────────────
            try (FileOutputStream fos = new FileOutputStream(nomFichier)) {
                workbook.write(fos);
            }
        }

        System.out.println("✅ Excel créé : " + nomFichier
                + " (" + nbImages + " lignes de données).");
    }
}