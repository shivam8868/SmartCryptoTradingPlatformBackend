package com.tradingplatform.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.tradingplatform.config.JwtProvider;
import com.tradingplatform.domain.USER_ROLE;
import com.tradingplatform.modal.TwoFactorOTP;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Wallet;
import com.tradingplatform.repository.UserRepository;
import com.tradingplatform.repository.WalletRepository;
import com.tradingplatform.response.AuthResponse;
import com.tradingplatform.service.CustomUserDetailsService;
import com.tradingplatform.service.EmailService;
import com.tradingplatform.service.TwoFactorServiceOtpService;
import com.tradingplatform.service.WatchlistService;
import com.tradingplatform.utils.OtpUtils;

import jakarta.mail.MessagingException;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthController {
     @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
     @Autowired
    private TwoFactorServiceOtpService twoFactorServiceOtpService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // This class is responsible for handling the authentication and registration of the users. It will receive the user details from the client in the request body and will perform the authentication and registration process. It will also generate a JWT token for the authenticated user and return it in the response body so that the client can use it for subsequent requests to the protected endpoints of the application.
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user ) { // or add throws Exception at line no 36 write Exception instead of RuntimeException 



        // Check if email already exists
        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if(isEmailExist!=null) {
            throw new RuntimeException("email is already used with another account ");
        }

        User newUser=new User();
        newUser.setEmail(user.getEmail());
        // newUser.setPassword(user.getPassword());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setFullName(user.getFullName());
        newUser.setRole(USER_ROLE.ROLE_CUSTOMER);
        User savedUser=userRepository.save(newUser);

        watchlistService.createWatchlist(savedUser);
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);  //  yahan save karna mandatory hai



        Authentication auth = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword()
                );
                
        SecurityContextHolder.getContext().setAuthentication(auth);
        // generate a JWT token for the authenticated user and return it in the response body so that the client can use it for subsequent requests to the protected endpoints of the application
        String jwt = JwtProvider.generateToken(auth);

        AuthResponse authResponse=new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setStatus("true");
        authResponse.setMessage("User registered successfully");

        return new ResponseEntity<>(authResponse,HttpStatus.CREATED);



    }




    // This method is responsible for authenticating the user by checking the email and password that are sent by the client in the request body. It will check if the email and password are correct and if they are correct, it will return an authentication object that contains the user details and the authorities of the user. If the email and password are incorrect, it will throw an exception.
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user ) throws MessagingException { // or add throws Exception at line no 36 write Exception instead of RuntimeException 
        String userName=user.getEmail();
        String password=user.getPassword();
        Authentication auth = authenticate(userName,password);
                
        SecurityContextHolder.getContext().setAuthentication(auth);
        // generate a JWT token for the authenticated user and return it in the response body so that the client can use it for subsequent requests to the protected endpoints of the application
        User authUser = userRepository.findByEmail(userName);
        String jwt = JwtProvider.generateToken(auth);

        if(authUser.getTwoFactorAuth().isEnabled()) {
            AuthResponse res=new AuthResponse();
            res.setMessage("Two factor authentication is enabled for this account. Please enter the OTP sent to your email to complete the login process.");
            res.setTwoFactorAuthEnabled(true);
            // generate a random 6 digit OTP
            // String otp = String.valueOf((int)(Math.random()*1000000)); 

            String otp=OtpUtils.generateOTP();

            TwoFactorOTP oldTwoFactorOTP = twoFactorServiceOtpService.findByUser(authUser.getId());
            if(oldTwoFactorOTP!=null) {
                twoFactorServiceOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }

            TwoFactorOTP newTwoFactorOTP = twoFactorServiceOtpService.createTwoFactorOtp(authUser, otp, jwt);

            emailService.sendVerificationOtpEmail(userName, otp);

            res.setSession(newTwoFactorOTP.getId());
            return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
        }
        System.out.println(jwt.length());

        AuthResponse res=new AuthResponse();
        res.setJwt(jwt);
        res.setStatus("true");
        res.setMessage("User logged in successfully"); 
        return new ResponseEntity<>(res,HttpStatus.CREATED);



    }

    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails= customUserDetailsService.loadUserByUsername(userName);
        if(userDetails==null) {
            throw new BadCredentialsException("Invalid email or password");
        }
        // Check if the password is correct
        // if (!password.equals(userDetails.getPassword())) {
        //     throw new BadCredentialsException("invalid password");
        // }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigninOtp(@PathVariable String otp,@RequestParam String id) throws Exception {
       
       TwoFactorOTP twoFactorOTP = twoFactorServiceOtpService.findById(id);
       if(twoFactorServiceOtpService.verifyTwoFactorOtp(twoFactorOTP,otp)) {
        AuthResponse res = new AuthResponse();
            res.setMessage("Two factor authentication verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(res, HttpStatus.OK);
       }
       throw new Exception("Invalid OTP");
          
    }

}