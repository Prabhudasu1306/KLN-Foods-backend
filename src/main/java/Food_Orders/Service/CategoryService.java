package Food_Orders.Service;

import Food_Orders.Entity.Category;
import Food_Orders.Repository.CategoryRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public void saveCategoriesToDatabase(MultipartFile multipartFile) {
        if (ExcelUploadService.isValidExcelFile(multipartFile)) {
            try {
                List<Category> categories = ExcelUploadService.getCategoriesDataFromExcel(multipartFile.getInputStream());
                this.categoryRepository.saveAll(categories);
            } catch (IOException e) {
                throw new IllegalArgumentException("Error reading Excel file", e);
            } catch (Exception e) {
                throw new IllegalStateException("An error occurred while saving categories", e);
            }
        } else {
            throw new IllegalArgumentException("The uploaded file is not a valid Excel file");
        }
    }

    public List<Category> exportCategoriesToExcel(HttpServletResponse response) throws IOException {
        List<Category> categories = categoryRepository.findAll();
        ExcelExportUtils excelExportUtils = new ExcelExportUtils(categories);
        excelExportUtils.exportDataToExcel(response);
        return categories;
    }

    // CSV Export method
    public String convertCategoriesToCSV(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return "";
        }

        StringBuilder csvBuilder = new StringBuilder();
        // Header
        csvBuilder.append("Category ID,Name,Description,Image URL\n");

        // Data rows
        for (Category category : categories) {
            csvBuilder.append(category.getCategory_id()).append(",");
            csvBuilder.append(escapeCsvField(category.getName())).append(",");
            csvBuilder.append(escapeCsvField(category.getDescription())).append(",");
            csvBuilder.append(escapeCsvField(category.getImageUrl())).append("\n");
        }

        return csvBuilder.toString();
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // Escape quotes and wrap in quotes if contains comma
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = field.replace("\"", "\"\"");
            return "\"" + field + "\"";
        }
        return field;
    }

    // CSV Import method
    public List<Category> importCategoriesFromCSV(MultipartFile file) throws Exception {
        List<Category> categories = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Skip header row
                }

                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                Category category = parseCategoryFromCSV(line);
                if (category != null) {
                    categories.add(category);
                }
            }
        }

        // Save all imported categories
        return categoryRepository.saveAll(categories);
    }

    private Category parseCategoryFromCSV(String csvLine) {
        try {
            String[] fields = parseCSVLine(csvLine);

            if (fields.length < 3) {
                return null; // Skip invalid lines
            }

            Category category = new Category();
            // Skip category_id (field[0]) as it will be auto-generated
            category.setName(fields[1].trim());
            category.setDescription(fields[2].trim());
            category.setImageUrl(fields.length > 3 ? fields[3].trim() : "");

            return category;
        } catch (Exception e) {
            logger.warn("Failed to parse CSV line: " + csvLine, e);
            return null;
        }
    }

    private String[] parseCSVLine(String csvLine) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < csvLine.length(); i++) {
            char c = csvLine.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString().trim());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }

        fields.add(field.toString().trim());
        return fields.toArray(new String[0]);
    }
}