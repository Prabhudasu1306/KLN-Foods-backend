package Food_Orders.Controller;

import Food_Orders.Entity.Feedback;
import Food_Orders.Repository.FeedbackRepository;
import Food_Orders.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/add")
    public ResponseEntity<Feedback> createFeedback(@RequestBody Feedback feedback) {
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return ResponseEntity.ok(savedFeedback);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Long id) {
        return feedbackRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedbackDetails) {
        return feedbackRepository.findById(id)
                .map(feedback -> {
                    feedback.setEmail(feedbackDetails.getEmail());
                    feedback.setText(feedbackDetails.getText());
                    Feedback updatedFeedback = feedbackRepository.save(feedback);
                    return ResponseEntity.ok(updatedFeedback);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        if (feedbackRepository.existsById(id)) {
            feedbackRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/export-csv-email")
    public ResponseEntity<String> exportFeedbacksToCSVAndEmail(@RequestParam String adminEmail) {
        try {
            List<Feedback> feedbacks = feedbackRepository.findAll();

            if (feedbacks.isEmpty()) {
                return ResponseEntity.badRequest().body("No feedbacks available to export.");
            }

            // Generate CSV data
            String csvData = generateFeedbacksCSV(feedbacks);
            String fileName = "feedbacks_export_" + System.currentTimeMillis() + ".csv";

            // Send email with CSV attachment
            emailService.sendCSVAttachment(
                    adminEmail,
                    "Feedbacks Data Export - KLN Food Court",
                    "Please find attached the exported feedbacks data in CSV format.",
                    csvData,
                    fileName,
                    "feedbacks"
            );

            return ResponseEntity.ok("Feedbacks CSV exported and sent to " + adminEmail + " successfully!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error exporting feedbacks: " + e.getMessage());
        }
    }

    @PostMapping("/import-csv")
    public ResponseEntity<String> importFeedbacksFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest().body("Please upload a CSV file");
            }

            int importedCount = 0;
            int errorCount = 0;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    // Skip empty lines and comments
                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    // Skip header
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }

                    try {
                        // Simple CSV parsing
                        String[] fields = parseCSVLine(line);

                        if (fields.length >= 2) {
                            Feedback feedback = new Feedback();
                            feedback.setEmail(fields[0].trim());
                            feedback.setText(fields[1].trim());

                            feedbackRepository.save(feedback);
                            importedCount++;
                        }
                    } catch (Exception e) {
                        errorCount++;
                        System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                    }
                }
            }

            String result = String.format("Successfully imported %d feedbacks. %d errors occurred.", importedCount, errorCount);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing CSV: " + e.getMessage());
        }
    }

    // Generate CSV data from feedbacks
    private String generateFeedbacksCSV(List<Feedback> feedbacks) {
        StringBuilder csv = new StringBuilder();

        // CSV header
        csv.append("ID,Email,Feedback Text\n");

        // CSV rows
        for (Feedback feedback : feedbacks) {
            csv.append(feedback.getId()).append(",");
            csv.append(escapeCsvField(feedback.getEmail())).append(",");
            csv.append(escapeCsvField(feedback.getText())).append("\n");
        }

        return csv.toString();
    }

    // Helper method to escape CSV fields
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // If field contains comma, quote, or newline, wrap in quotes and escape existing quotes
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    // Helper method to parse CSV lines
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString()); // Add last field

        return fields.toArray(new String[0]);
    }
}