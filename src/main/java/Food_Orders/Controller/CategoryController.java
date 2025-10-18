package Food_Orders.Controller;

import Food_Orders.Entity.Category;
import Food_Orders.Repository.CategoryRepository;
import Food_Orders.Service.CategoryService;
import Food_Orders.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("imageUrl") String imageUrl) {
        try {
            Category category = new Category(name, description, imageUrl);
            Category savedCategory = categoryRepository.save(category);
            logger.info("Created new category with ID: {}", savedCategory.getCategory_id());
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            logger.error("Failed to create category", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.info("Fetching all categories...");
        try {
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error fetching categories", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        logger.info("Fetching category with ID: {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            logger.warn("Category with ID {} is not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("Attempting to delete category with ID: {}", id);
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            logger.info("Deleted category with ID: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Category with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> uploadCategoryData(@RequestParam("file") MultipartFile file) {
        this.categoryService.saveCategoriesToDatabase(file);
        return ResponseEntity
                .ok(Map.of("Message", "Categories data uploaded and saved to database successfully"));
    }

    @GetMapping("/Export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Categories_Information.xlsx";
        response.setHeader(headerKey, headerValue);
        categoryService.exportCategoriesToExcel(response);
    }

    // New endpoint for CSV export and email
    @PostMapping("/export-csv")
    public ResponseEntity<Map<String, Object>> exportCategoriesToCSV(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Category> categories = categoryRepository.findAll();
            String csvData = categoryService.convertCategoriesToCSV(categories);
            String fileName = request.get("fileName");
            String subject = request.get("subject");
            String message = request.get("message");
            String adminEmail = request.get("adminEmail");

            if (csvData == null || csvData.trim().isEmpty()) {
                throw new RuntimeException("No categories data to export");
            }

            // Send CSV via email
            emailService.sendCSVAttachment(adminEmail, subject, message, csvData, fileName);

            response.put("success", true);
            response.put("message", "Categories CSV file sent via email successfully");
            response.put("categoriesCount", categories.size());
        } catch (Exception e) {
            logger.error("Failed to export categories to CSV", e);
            response.put("success", false);
            response.put("message", "Failed to export categories: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // New endpoint for CSV import
    @PostMapping("/import-csv")
    public ResponseEntity<Map<String, Object>> importCategoriesFromCSV(@RequestParam("file") MultipartFile file) {
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

            List<Category> importedCategories = categoryService.importCategoriesFromCSV(file);
            response.put("success", true);
            response.put("message", "Successfully imported " + importedCategories.size() + " categories");
            response.put("importedCount", importedCategories.size());
            response.put("categories", importedCategories);

            logger.info("Imported {} categories from CSV file", importedCategories.size());
        } catch (Exception e) {
            logger.error("Failed to import categories from CSV", e);
            response.put("success", false);
            response.put("message", "Failed to import categories: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}