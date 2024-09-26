package smartcv.auth.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import smartcv.auth.model.*;
import smartcv.auth.security.JwtUtil;
import smartcv.auth.serviceImpl.CustomUserDetailsService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rest/auth")
public class AuthController {

    private EmailRequest  emailRequest;

    private final AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    private JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;

    }

    @ResponseBody
    @RequestMapping(value = "/C", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginReq loginReq) {


        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword()));
            String email = authentication.getName();

            User user = new User(email, "");
            String token = jwtUtil.createToken(user);
            System.out.println("token generated: " + token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);


            User userdto = userDetailsService.findByEmail(email);

            if (userdto == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }


            System.out.println("token generated: " + token);


            LoginRes loginRes = new LoginRes(email, token, userdto.getId());

            return ResponseEntity.ok(loginRes);

        } catch (BadCredentialsException e) {
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, "Invalid username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            System.out.println(e);
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody User user) {
        try {
            userDetailsService.createUser(user);

            // String token = jwtUtil.createToken(user);

            //LoginRes loginRes = new LoginRes(user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            ErrorRes errorResponse = new ErrorRes(HttpStatus.BAD_REQUEST, e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            ErrorRes errorResponse = new ErrorRes(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while creating the user.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<User>> getUser(@PathVariable Long userId) {
        User user = userDetailsService.getUserById(userId);
        List<User> userList = new ArrayList<>();
        if (user != null) {
            userList.add(user);
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/user/role")
    public ResponseEntity<UserRoleResponse> getUserRoleByEmail(@RequestBody EmailRequest emailRequest) {
        String email = emailRequest.getEmail();
        try {
            System.out.println("Received request to get user role for email: " + email);

            String role = userDetailsService.getUserRoleByEmail(email);
            if (role != null) {
                System.out.println("User role found: " + role);
                UserRoleResponse response = new UserRoleResponse(role);
                return ResponseEntity.ok(response);
            } else {
                System.out.println("User role not found for email: " + email);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("An error occurred while fetching user role for email: " + email);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}