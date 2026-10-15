package com.shop;

import com.shop.entity.*;
import com.shop.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/** Seeds demo data on first run: admin@shop.com / Admin@123 and customer@shop.com / Customer@123. */
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users; private final CategoryRepository categories;
    private final ProductRepository products; private final PasswordEncoder encoder;

    public DataSeeder(UserRepository u, CategoryRepository c, ProductRepository p, PasswordEncoder e) {
        users = u; categories = c; products = p; encoder = e;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;
        users.save(new User("Store Admin", "admin@shop.com", encoder.encode("Admin@123"), Role.ADMIN));
        users.save(new User("Demo Customer", "customer@shop.com", encoder.encode("Customer@123"), Role.CUSTOMER));

        Category electronics = categories.save(new Category("Electronics"));
        Category books = categories.save(new Category("Books"));
        Category home = categories.save(new Category("Home & Kitchen"));
        Category fashion = categories.save(new Category("Fashion"));

        add("Wireless Headphones", "Over-ear, 30-hour battery, active noise cancellation.", "4999", 40, electronics);
        add("Mechanical Keyboard", "Hot-swappable switches, compact 75% layout.", "3499", 25, electronics);
        add("27-inch Monitor", "QHD IPS panel, 144 Hz, adjustable stand.", "18999", 12, electronics);
        add("USB-C Hub", "7-in-1: HDMI, SD, 3x USB-A, PD charging.", "1799", 60, electronics);
        add("Clean Code", "A handbook of agile software craftsmanship.", "799", 80, books);
        add("Designing Data-Intensive Applications", "The big ideas behind reliable, scalable systems.", "1299", 50, books);
        add("Cast Iron Skillet", "Pre-seasoned 10-inch skillet, oven safe.", "1599", 30, home);
        add("French Press", "1-litre borosilicate glass, stainless steel filter.", "999", 45, home);
        add("Canvas Backpack", "20-litre, water-resistant, laptop sleeve.", "2199", 35, fashion);
        add("Running Shoes", "Lightweight mesh, cushioned sole.", "3999", 20, fashion);
    }

    private void add(String name, String desc, String price, int stock, Category cat) {
        Product p = new Product();
        p.setName(name); p.setDescription(desc); p.setPrice(new BigDecimal(price)); p.setStock(stock); p.setCategory(cat);
        p.setImageUrl("https://picsum.photos/seed/" + name.replaceAll("[^A-Za-z]", "") + "/480/320");
        products.save(p);
    }
}
