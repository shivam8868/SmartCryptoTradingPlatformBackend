package com.tradingplatform.config;
// This class is responsible for storing the constants that are used in the JWT token validation process. It will contain the secret key that is used to sign the token, the expiration time of the token, and any other constants that are required for the JWT token validation process.
public class JwtConstant {
    public static final String SECRET_KEY = "mySuperSecretKeyForJwtAuth1234569871237896544321"; // secret key that is used to sign the token "mysecretkey"
    public static final long EXPIRATION_TIME = 86400000; // 1 day in millisecondspub
    public static final String JWT_HEADER = "Authorization";
}   
