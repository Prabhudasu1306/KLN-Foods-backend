package Food_Orders.Service;

import Food_Orders.Entity.Food;
import Food_Orders.Repository.FoodRepository;
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
public class FoodService {
    private static final Logger logger = LoggerFactory.getLogger(FoodService.class);

    private final FoodRepository foodRepository;

    public void saveFoodsToDatabase(MultipartFile file) {
        try {
            if (!FoodExcelUploadService.isValidExcelFile(file)) {
                throw new IllegalArgumentException("Invalid file format. Only Excel files are allowed.");
            }
            List<Food> foods = FoodExcelUploadService.getFoodsDataFromExcel(file.getInputStream());
            foodRepository.saveAll(foods);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving foods to the database: " + e.getMessage());
        }
    }

    public void exportFoodsToExcel(HttpServletResponse response) throws IOException {
        System.out.println("Starting exportFoodsToExcel");
        List<Food> foods = foodRepository.findAll();
        System.out.println("Foods data to be exported: " + foods);
        new FoodExcelExportUtils(foods).exportDataToExcel(response);
        System.out.println("Foods data successfully exported to Excel");
    }

    // CSV Export method
    public String convertFoodsToCSV(List<Food> foods) {
        if (foods == null || foods.isEmpty()) {
            return "";
        }

        StringBuilder csvBuilder = new StringBuilder();
        // Header
        csvBuilder.append("ID,Name,Price,State GST,Central GST,Total GST,Total Price,Description,Category Name,Image URL\n");

        // Data rows
        for (Food food : foods) {
            csvBuilder.append(food.getId()).append(",");
            csvBuilder.append(escapeCsvField(food.getName())).append(",");
            csvBuilder.append(food.getPrice()).append(",");
            csvBuilder.append(food.getStateGST()).append(",");
            csvBuilder.append(food.getCentralGST()).append(",");
            csvBuilder.append(food.getTotalGST()).append(",");
            csvBuilder.append(food.getTotalPrice()).append(",");
            csvBuilder.append(escapeCsvField(food.getDescription())).append(",");
            csvBuilder.append(escapeCsvField(food.getCategoryName())).append(",");
            csvBuilder.append(escapeCsvField(food.getImageUrl())).append("\n");
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
    public List<Food> importFoodsFromCSV(MultipartFile file) throws Exception {
        List<Food> foods = new ArrayList<>();

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

                Food food = parseFoodFromCSV(line);
                if (food != null) {
                    foods.add(food);
                }
            }
        }

        // Save all imported foods
        return foodRepository.saveAll(foods);
    }

    private Food parseFoodFromCSV(String csvLine) {
        try {
            String[] fields = parseCSVLine(csvLine);

            if (fields.length < 9) {
                logger.warn("Skipping invalid CSV line - insufficient fields: " + csvLine);
                return null; // Skip invalid lines
            }

            Food food = new Food();
            // Skip ID (field[0]) as it will be auto-generated
            food.setName(fields[1].trim());
            food.setPrice(parseDoubleSafely(fields[2].trim()));
            food.setStateGST(parseDoubleSafely(fields[3].trim()));
            food.setCentralGST(parseDoubleSafely(fields[4].trim()));
            food.setTotalGST(parseDoubleSafely(fields[5].trim()));
            food.setTotalPrice(parseDoubleSafely(fields[6].trim()));
            food.setDescription(fields[7].trim());
            food.setCategoryName(fields[8].trim());
            food.setImageUrl(fields.length > 9 ? fields[9].trim() : "");

            return food;
        } catch (Exception e) {
            logger.warn("Failed to parse CSV line: " + csvLine, e);
            return null;
        }
    }

    private double parseDoubleSafely(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format: " + value + ", using 0.0 instead");
            return 0.0;
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