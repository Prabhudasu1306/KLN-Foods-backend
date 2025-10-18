package Food_Orders.Service;

import Food_Orders.Entity.Order;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    public ByteArrayInputStream generateInvoice(Order order) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));

            // Order details table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{1f, 2f});

            addTableRow(table, "Transaction ID", order.getTransactionId());
            addTableRow(table, "Customer Name", order.getCustomerName());
            addTableRow(table, "Customer Email", order.getCustomerEmail());
            addTableRow(table, "Total Amount", "₹" + order.getTotalAmount());
            addTableRow(table, "Status", order.getStatus());
            addTableRow(table, "Date", order.getDate().toString());

            document.add(table);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableRow(PdfPTable table, String key, String value) {
        Font keyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA);

        PdfPCell keyCell = new PdfPCell(new Phrase(key, keyFont));
        keyCell.setBorder(Rectangle.NO_BORDER);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(keyCell);
        table.addCell(valueCell);
    }
}
