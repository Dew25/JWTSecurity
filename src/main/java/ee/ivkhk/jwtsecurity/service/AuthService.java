package ee.ivkhk.jwtsecurity.service;

import ee.ivkhk.jwtsecurity.dto.ReqRes;
import ee.ivkhk.jwtsecurity.entity.OurUsers;
import ee.ivkhk.jwtsecurity.repository.OurUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
@Service
public class AuthService {
    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public ReqRes signUp(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();
        try {
            OurUsers ourUsers = new OurUsers();
            ourUsers.setEmail(registrationRequest.getEmail());
            ourUsers.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUsers.setRole(registrationRequest.getRole());
            OurUsers ourUsersResult = ourUserRepo.save(ourUsers);
            if (ourUsersResult != null && ourUsersResult.getId() > 0) {
                resp.setOurUsers(ourUsersResult);
                resp.setMessage("User saved Successfully");
                resp.setStatusCode(200);
            }
        }catch(Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    public ReqRes signIn(ReqRes signinRequest){
        ReqRes response = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
            var user = ourUserRepo.findByEmail(signinRequest.getEmail()).orElseThrow();
            System.out.println("User is: "+user);
            var jwt = jwtUtils.generateToken(user);
            var refrashToken = jwtUtils.generateRefreshToken(new HashMap<>(),user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refrashToken);
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully signed in");

        }catch (Exception e){
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }
    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
        OurUsers users = ourUserRepo.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenReqiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }
}
