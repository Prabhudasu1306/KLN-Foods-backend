package Food_Orders.Controller;

import Food_Orders.Dto.AddressRequest;
import Food_Orders.Dto.CartItemDto;
import Food_Orders.Dto.OrderRequest;
import Food_Orders.Entity.Address;
import Food_Orders.Entity.Cart;
import Food_Orders.Entity.CartItem;
import Food_Orders.Entity.User;
import Food_Orders.Repository.AddressRepository;
import Food_Orders.Repository.CartItemRepository;
import Food_Orders.Repository.CartRepository;
import Food_Orders.Repository.UserRepository;
import Food_Orders.Service.CartService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CartService cartService;

    @GetMapping("/user")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        try {
            return userRepository.findByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching user by email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<Address>> getAddressesByEmail(@RequestParam String email) {
        try {
            return ResponseEntity.ok(addressRepository.findByEmail(email));
        } catch (Exception e) {
            logger.error("Error fetching addresses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/address")
    public ResponseEntity<Address> getAddressByEmailAndHouseNumber(
            @RequestParam String email, @RequestParam String houseNumber) {
        try {
            List<Address> addresses = addressRepository.findByEmailAndHouseNumber(email, houseNumber);
            return addresses.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(addresses.get(0));
        } catch (Exception e) {
            logger.error("Error fetching address", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/addresses")
    public ResponseEntity<Address> saveAddress(@RequestBody AddressRequest addressRequest) {
        try {
            if (addressRequest.getEmail() == null || addressRequest.getHouseNumber() == null)
                return ResponseEntity.badRequest().build();

            Address address = new Address();
            address.setEmail(addressRequest.getEmail());
            address.setHouseNumber(addressRequest.getHouseNumber());
            address.setLandMark(addressRequest.getLandMark());
            address.setStreet(addressRequest.getStreet());
            address.setCity(addressRequest.getCity());
            address.setState(addressRequest.getState());
            address.setZipCode(addressRequest.getZipCode());

            return ResponseEntity.ok(addressRepository.save(address));
        } catch (Exception e) {
            logger.error("Error saving address", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/addresses")
    public ResponseEntity<Address> updateAddress(
            @RequestParam String email,
            @RequestParam String houseNumber,
            @RequestBody AddressRequest addressRequest) {
        try {
            List<Address> addresses = addressRepository.findByEmailAndHouseNumber(email, houseNumber);
            if (addresses.isEmpty()) return ResponseEntity.notFound().build();

            Address address = addresses.get(0);
            address.setHouseNumber(addressRequest.getHouseNumber());
            address.setLandMark(addressRequest.getLandMark());
            address.setStreet(addressRequest.getStreet());
            address.setCity(addressRequest.getCity());
            address.setState(addressRequest.getState());
            address.setZipCode(addressRequest.getZipCode());
            return ResponseEntity.ok(addressRepository.save(address));

        } catch (Exception e) {
            logger.error("Error updating address", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/addresses")
    public ResponseEntity<Object> deleteAddress(@RequestParam String email, @RequestParam String houseNumber) {
        try {
            List<Address> addresses = addressRepository.findByEmailAndHouseNumber(email, houseNumber);
            if (addresses.isEmpty()) return ResponseEntity.notFound().build();

            for (Address address : addresses) {
                if (address.getCart() == null) addressRepository.delete(address);
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting address", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        try {
            Optional<Address> addressOpt = addressRepository.findById(id);
            if (addressOpt.isPresent()) {
                Address address = addressOpt.get();

                Cart linkedCart = address.getCart();
                if (linkedCart != null) {
                    linkedCart.setAddress(null);
                    cartRepository.save(linkedCart);
                }

                addressRepository.delete(address);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ✅ Get all carts
    @GetMapping("/carts")
    public ResponseEntity<List<Cart>> getAllCarts() {
        try {
            return ResponseEntity.ok(cartRepository.findAll());
        } catch (Exception e) {
            logger.error("Error fetching carts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // ✅ Get cart by ID
    @GetMapping("/carts/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        try {
            return cartRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching cart by id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ Get carts by email
    @GetMapping("/carts/user")
    public ResponseEntity<List<Cart>> getCartsByEmail(@RequestParam String email) {
        try {
            return ResponseEntity.ok(cartRepository.findByEmail(email));
        } catch (Exception e) {
            logger.error("Error fetching carts by email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // ✅ Get cart items by cart ID
    @GetMapping("/carts/{cartId}/items")
    public ResponseEntity<List<CartItem>> getCartItemsByCartId(@PathVariable int cartId) {
        try {
            return ResponseEntity.ok(cartItemRepository.findByCartId(cartId));
        } catch (Exception e) {
            logger.error("Error fetching cart items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // ✅ Add item to cart
    @PostMapping("/cart-items")
    public ResponseEntity<CartItem> addCartItem(@RequestBody CartItemDto dto) {
        try {
            CartItem item = new CartItem();
            item.setName(dto.getName());
            item.setPrice(dto.getPrice());
            item.setQuantity(dto.getQuantity());
            item.setTotalGST(dto.getTotalGST());

            if (dto.getCartId() != null) {
                cartRepository.findById(dto.getCartId()).ifPresent(item::setCart);
            }

            return ResponseEntity.ok(cartItemRepository.save(item));
        } catch (Exception e) {
            logger.error("Error adding cart item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<Object> deleteCartItem(@PathVariable Long id) {
        try {
            if (cartItemRepository.existsById(id)) {
                cartItemRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting cart item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            Cart cart = new Cart();
            cart.setTotalAmount(orderRequest.getTotalAmount());
            cart.setPhoneNumber(orderRequest.getPhoneNumber());
            cart.setEmail(orderRequest.getEmail());
            cart.setDate(new Date());

            double sumAllGST = 0.0, totalPrice = 0.0;
            List<CartItem> items = new ArrayList<>();
            for (CartItemDto dto : orderRequest.getItems()) {
                CartItem item = new CartItem();
                item.setCart(cart);
                item.setName(dto.getName());
                item.setPrice(dto.getPrice());
                item.setQuantity(dto.getQuantity());
                item.setTotalGST(dto.getTotalGST());
                sumAllGST += dto.getTotalGST() * dto.getQuantity();
                totalPrice += dto.getPrice() * dto.getQuantity();
                items.add(item);
            }

            cart.setCartItems(items);
            cart.setSumAllGST(sumAllGST);
            cart.setTotalPrice(totalPrice);
            Cart saved = cartRepository.save(cart);

            Address address = new Address();
            address.setCart(saved);
            address.setEmail(orderRequest.getEmail());
            address.setHouseNumber(orderRequest.getAddress().getHouseNumber());
            address.setLandMark(orderRequest.getAddress().getLandMark());
            address.setStreet(orderRequest.getAddress().getStreet());
            address.setCity(orderRequest.getAddress().getCity());
            address.setState(orderRequest.getAddress().getState());
            address.setZipCode(orderRequest.getAddress().getZipCode());
            addressRepository.save(address);

            return ResponseEntity.ok(Map.of(
                    "message", "Order placed successfully",
                    "orderId", String.valueOf(saved.getId())
            ));
        } catch (Exception e) {
            logger.error("Error placing order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to place order"));
        }
    }

    // ✅ Order history
    @GetMapping("/order-history")
    public ResponseEntity<List<Cart>> getOrderHistory(@RequestParam String email) {
        try {
            return ResponseEntity.ok(cartRepository.findByEmailOrderByDateDesc(email));
        } catch (Exception e) {
            logger.error("Error fetching order history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    // ✅ Export CSV
    @GetMapping("/export-orders")
    public void exportOrders(@RequestParam String email, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=orders_" + email + ".csv");
            List<Cart> orders = cartRepository.findByEmail(email);
            cartService.exportOrdersToCsv(orders, response.getWriter());
        } catch (Exception e) {
            logger.error("Error exporting orders", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "timestamp", new Date().toString()
        ));
    }
}
