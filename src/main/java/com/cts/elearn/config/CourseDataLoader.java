package com.cts.elearn.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cts.elearn.dao.CourseRepository;
import com.cts.elearn.entity.Course;
import com.cts.elearn.entity.Course.Category;

@Configuration
public class CourseDataLoader {

    @Bean
    CommandLineRunner loadCourses(
            CourseRepository repo) {

        return args -> {

            if(repo.count() == 0) {

                Course java = Course.builder()
                        .courseName("Java Backend Development")
                        .description(
                                "Complete Java, Spring Boot and Microservices")
                        .category(Category.INSTRUCTOR_LED)
                        .vendorId(3L)
                        .price(new BigDecimal("4999"))
                        .avgRating(new BigDecimal("4.8"))
                        .reviewCount(120)
                        .build();

                repo.save(java);

                Course kafka = Course.builder()
                        .courseName("Apache Kafka Masterclass")
                        .description(
                                "Kafka Event Driven Architecture")
                        .category(Category.SELF_LEARNING)
                        .vendorId(3L)
                        .price(new BigDecimal("2999"))
                        .avgRating(new BigDecimal("4.6"))
                        .reviewCount(85)
                        .build();

                repo.save(kafka);

                Course systemDesign = Course.builder()
                        .courseName("System Design For Backend Engineers")
                        .description(
                                "Scalable Distributed Systems")
                        .category(Category.INSTRUCTOR_LED)
                        .vendorId(3L)
                        .price(new BigDecimal("6999"))
                        .avgRating(new BigDecimal("4.9"))
                        .reviewCount(210)
                        .build();

                repo.save(systemDesign);

                Course llb = Course.builder()
                        .courseName("LLB Entrance Preparation")
                        .description(
                                "TS LAWCET Preparation")
                        .category(Category.SELF_LEARNING)
                        .vendorId(3L)
                        .price(new BigDecimal("1499"))
                        .avgRating(new BigDecimal("4.4"))
                        .reviewCount(45)
                        .build();

                repo.save(llb);

                System.out.println(
                        "Sample Courses Loaded Successfully");
            }
        };
    }
}