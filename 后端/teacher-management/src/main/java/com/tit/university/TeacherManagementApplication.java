package com.tit.university;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tit.university.mapper")
public class TeacherManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeacherManagementApplication.class, args);
    }

}
