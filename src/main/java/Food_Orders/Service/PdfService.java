package Food_Orders.Service;

import Food_Orders.Entity.Order;
import Food_Orders.Entity.CartItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(Order order) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Add Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("KLN FOOD COURT - INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add Invoice Details
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            // Customer Details
            Paragraph customerHeader = new Paragraph("Customer Details:", headerFont);
            customerHeader.setSpacingAfter(10);
            document.add(customerHeader);

            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);
            customerTable.setSpacingAfter(15);

            addCustomerDetail(customerTable, "Customer Name:", order.getCustomerName(), normalFont);
            addCustomerDetail(customerTable, "Email:", order.getCustomerEmail(), normalFont);
            addCustomerDetail(customerTable, "Phone:", order.getPhoneNumber(), normalFont);
            addCustomerDetail(customerTable, "Order Date:",
                    order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont);
            addCustomerDetail(customerTable, "Order ID:", order.getId().toString(), normalFont);
            addCustomerDetail(customerTable, "Transaction ID:",
                    order.getTransactionId() != null ? order.getTransactionId() : "N/A", normalFont);

            document.add(customerTable);

            // Order Items Table
            Paragraph orderHeader = new Paragraph("Order Items:", headerFont);
            orderHeader.setSpacingAfter(10);
            document.add(orderHeader);

            PdfPTable itemsTable = new PdfPTable(4);
            itemsTable.setWidthPercentage(100);
            itemsTable.setSpacingAfter(20);

            // Table Headers
            addTableHeader(itemsTable, "Item Name", headerFont);
            addTableHeader(itemsTable, "Quantity", headerFont);
            addTableHeader(itemsTable, "Price (₹)", headerFont);
            addTableHeader(itemsTable, "Total (₹)", headerFont);

            // Table Rows
            double grandTotal = 0;
            for (CartItem item : order.getCartItems()) {
                addTableCell(itemsTable, item.getName(), normalFont);
                addTableCell(itemsTable, item.getQuantity().toString(), normalFont);
                addTableCell(itemsTable, String.format("%.2f", item.getPrice()), normalFont);
                double itemTotal = item.getPrice() * item.getQuantity();
                addTableCell(itemsTable, String.format("%.2f", itemTotal), normalFont);
                grandTotal += itemTotal;
            }

            document.add(itemsTable);

            // Total Amount
            Paragraph total = new Paragraph("Grand Total: ₹" + String.format("%.2f", grandTotal), headerFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingAfter(10);
            document.add(total);

            // Payment Status
            Paragraph status = new Paragraph("Payment Status: " +
                    (order.getPaymentStatus() != null ? order.getPaymentStatus() : "PENDING"),
                    headerFont);
            status.setAlignment(Element.ALIGN_RIGHT);
            status.setSpacingAfter(20);
            document.add(status);

            // Thank You Message
            Font thankYouFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
            Paragraph thankYou = new Paragraph("Thank you for your order! Visit us again at KLN Food Court.", thankYouFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            document.add(thankYou);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF invoice: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    private void addCustomerDetail(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", font));
        valueCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell header = new PdfPCell(new Phrase(text, font));
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(5);
        table.addCell(header);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}