package Food_Orders.Controller;

import Food_Orders.Entity.Food;
import Food_Orders.Repository.FoodRepository;
import Food_Orders.Service.FoodService;
import Food_Orders.Service.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/foods")
@CrossOrigin(origins = "http://localhost:3000")
public class FoodController {

    private final FoodRepository foodRepository;
    private final FoodService foodService;

    @Autowired
    private EmailService emailService;

    @Autowired
    public FoodController(FoodService foodService, FoodRepository foodRepository) {
        this.foodService = foodService;
        this.foodRepository = foodRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Food> createFood(@RequestBody Food food) {
        try {
            Food savedFood = foodRepository.save(food);
            return ResponseEntity.ok(savedFood);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Food>> getAllFoods() {
        try {
            List<Food> foods = foodRepository.findAll();
            return ResponseEntity.ok(foods);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFoodById(@PathVariable Long id) {
        try {
            Optional<Food> food = foodRepository.findById(id);
            if (food.isPresent()) {
                return ResponseEntity.ok(food.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Food item with ID " + id + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving food item: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateFood(@PathVariable Long id, @RequestBody Food updatedFood) {
        try {
            Optional<Food> foodOptional = foodRepository.findById(id);
            if (foodOptional.isPresent()) {
                Food food = foodOptional.get();
                food.setName(updatedFood.getName());
                food.setPrice(updatedFood.getPrice());
                food.setStateGST(updatedFood.getStateGST());
                food.setCentralGST(updatedFood.getCentralGST());
                food.setTotalGST(updatedFood.getTotalGST());
                food.setTotalPrice(updatedFood.getTotalPrice());
                food.setDescription(updatedFood.getDescription());
                food.setCategoryName(updatedFood.getCategoryName());
                food.setImageUrl(updatedFood.getImageUrl());

                Food savedFood = foodRepository.save(food);
                return ResponseEntity.ok(savedFood);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Food item not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating food item: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable Long id) {
        try {
            Optional<Food> foodOptional = foodRepository.findById(id);
            if (foodOptional.isPresent()) {
                foodRepository.deleteById(id);
                return ResponseEntity.ok("Food item successfully deleted.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Food item not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting food item: " + e.getMessage());
        }
    }

    @GetMapping("/Export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Foods_Information.xlsx";
        response.setHeader(headerKey, headerValue);
        foodService.exportFoodsToExcel(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFoods(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Received request to upload file: " + file.getOriginalFilename());
            foodService.saveFoodsToDatabase(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("File uploaded and data saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    // CSV Export and Email endpoint - INTEGRATED WITH EMAIL SERVICE
    @PostMapping("/export-csv")
    public ResponseEntity<Map<String, Object>> exportFoodsToCSV(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Food> foods = foodRepository.findAll();
            String csvData = foodService.convertFoodsToCSV(foods);

            // Provide default values if request is null or fields are missing
            String fileName = (request != null && request.containsKey("fileName")) ?
                    request.get("fileName") : "foods-export.csv";
            String subject = (request != null && request.containsKey("subject")) ?
                    request.get("subject") : "Food Items Data Export - KLN Food Court";
            String message = (request != null && request.containsKey("message")) ?
                    request.get("message") : "Please find attached the food items data export CSV file.";
            String adminEmail = (request != null && request.containsKey("adminEmail")) ?
                    request.get("adminEmail") : "prabhudasuparusu1306@gmail.com";

            if (csvData == null || csvData.trim().isEmpty()) {
                throw new RuntimeException("No food items data to export");
            }

            // Send CSV via email using the existing EmailService
            emailService.sendCSVAttachment(adminEmail, subject, message, csvData, fileName, "foods");

            response.put("success", true);
            response.put("message", "Food items CSV file sent via email successfully");
            response.put("foodsCount", foods.size());
            response.put("sentTo", adminEmail);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to export food items: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // CSV Import endpoint
    @PostMapping("/import-csv")
    public ResponseEntity<Map<String, Object>> importFoodsFromCSV(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Check if file is empty
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Please select a CSV file to upload");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if file is CSV
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".csv")) {
                response.put("success", false);
                response.put("message", "Please upload a CSV file");
                return ResponseEntity.badRequest().body(response);
            }

            List<Food> importedFoods = foodService.importFoodsFromCSV(file);
            response.put("success", true);
            response.put("message", "Successfully imported " + importedFoods.size() + " food items");
            response.put("importedCount", importedFoods.size());
            response.put("foods", importedFoods);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Failed to import food items: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}