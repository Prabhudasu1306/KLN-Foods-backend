package Food_Orders.Service;

import Food_Orders.Entity.Cart;
import Food_Orders.Repository.CartRepository;
import Food_Orders.Repository.CartItemRepository;
import Food_Orders.Repository.AddressRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@AllArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;


    public void saveCartsToDatabase(MultipartFile file) {
        try {
            if (!CartExcelUploadService.isValidExcelFile(file)) {
                throw new IllegalArgumentException("Invalid file format. Only Excel files are allowed.");
            }
            List<Cart> carts = CartExcelUploadService.getCartsDataFromExcel(file.getInputStream());
            cartRepository.saveAll(carts);
            System.out.println("Successfully saved carts to the database.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving carts to the database: " + e.getMessage());
        }
    }

    public void exportCartsToExcel(HttpServletResponse response) throws IOException {
        System.out.println("Starting exportCartsToExcel");
        List<Cart> carts = cartRepository.findAll();
        System.out.println("Carts data to be exported: " + carts);
        XSSFWorkbook workbook = new XSSFWorkbook();
        new CartExcelExportUtils(carts, cartItemRepository, addressRepository, workbook).exportDataToExcel(response);
        System.out.println("Carts data successfully exported to Excel.");
    }

    public void exportOrdersToCsv(List<Cart> orders, PrintWriter writer) {
    }
}
