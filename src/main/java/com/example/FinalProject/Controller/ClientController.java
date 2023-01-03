package com.example.FinalProject.Controller;

import com.example.FinalProject.Module.Client;
import com.example.FinalProject.OTP.CacheRepository;
import com.example.FinalProject.OTP.OtpGenerator;
import com.example.FinalProject.OTP.OtpRequest;
import com.example.FinalProject.OTP.VerifyOTP;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ClientController {

    @Autowired
    MongoTemplate mt;

    @Autowired
    CacheRepository cacheRepository;

    @Autowired
    OtpGenerator otpGenerator;


    // Registration API
    @PostMapping("/Register")
    public String CreateClient(@RequestBody Client client) {
        Client client1 = mt.save(client);
        if (client1 != null) {
            return "Registration Successfully";
        }
        return "Registration Failed";
    }


    // Login API
    @RequestMapping("/ClientLogin/{emailid}/{password}")
    public String ClientLogin(@PathVariable String emailid, @PathVariable String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("emailid").is(emailid).and("password").is(password));
        String result = mt.findOne(query, String.class, "ClientInformation");
        if (result != null) {
            JsonObject response = new JsonObject(result);
            System.out.println(response.getJson());
            return "Login Successfully";
        }
        return "Login Failed";
    }


    @PostMapping("/generate-otp")
    public ResponseEntity<String> addToCache(@RequestBody OtpRequest key) {

        int value = otpGenerator.generateOtp();
        cacheRepository.put(key.getContactNo(), value);
        return ResponseEntity.ok("Otp Generated Successfully : " + value);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<String> addToCache(@RequestBody VerifyOTP key) {

        if (cacheRepository.get(key.getMobileNo()).isPresent() && cacheRepository.get(key.getMobileNo()).get().equals(key.getOtp())) {
            return ResponseEntity.ok("OTP verified Succesfully");
        }
        return ResponseEntity.ok("Invalid OTP!");
    }


}
