package Food_Orders.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column
    private Double price;

    private Integer quantity;
    private String category;
    private String image;

    @Column(name = "total_gst")
    private Double totalGST;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public CartItem() {}

    public CartItem(String name, Double price, Integer quantity, String category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Double getTotalGST() { return totalGST; }
    public void setTotalGST(Double totalGST) { this.totalGST = totalGST; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }


    public Double getTotalPrice() {
        if (price == null || quantity == null) return 0.0;
        return price * quantity;
    }
}
