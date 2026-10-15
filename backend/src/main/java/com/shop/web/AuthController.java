package com.shop.web;

import com.shop.dto.Dtos.*;
import com.shop.entity.*;
import com.shop.repo.UserRepository;
import com.shop.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    @PostMapping("/register")
    public AuthRes register(@Valid @RequestBody RegisterReq r) {
        String email = r.email().trim().toLowerCase();
        if (users.existsByEmail(email)) throw new ApiException(HttpStatus.CONFLICT, "That email is already registered");
        // Public sign-up always creates CUSTOMERs. Admins are provisioned by the seeder / DB.
        User u = users.save(new User(r.name().trim(), email, encoder.encode(r.password()), Role.CUSTOMER));
        return res(u);
    }

    @PostMapping("/login")
    public AuthRes login(@Valid @RequestBody LoginReq r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .filter(x -> encoder.matches(r.password(), x.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Wrong email or password"));
        return res(u);
    }

    private AuthRes res(User u) { return new AuthRes(jwt.generate(u), u.getName(), u.getEmail(), u.getRole().name()); }
}
