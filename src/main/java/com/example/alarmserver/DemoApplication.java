package com.example.alarmserver;

import org.pjsip.pjsua2.AuthCredInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		AuthCredInfo cred = new AuthCredInfo();

        cred.setRealm("Hello world");
    
        System.out.println(cred.getRealm());
		SpringApplication.run(DemoApplication.class, args);
	}

}
