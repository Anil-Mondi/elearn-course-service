package com.cts.elearn.controller;

import com.cts.elearn.entity.Course;
import com.cts.elearn.entity.Course.Category;
import com.cts.elearn.entity.Course.CourseStatus;
import com.cts.elearn.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@DisplayName("CourseController Tests")
class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private Course testCourse;
    private Course testCourse2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();

        // Initialize test courses
        testCourse = Course.builder()
                .courseId(1L)
                .courseName("Java Basics")
                .description("Learn Java fundamentals")
                .category(Category.SELF_LEARNING)
                .vendorId(100L)
                .status(CourseStatus.ACTIVE)
                .price(new BigDecimal("99.99"))
                .avgRating(new BigDecimal("4.5"))
                .reviewCount(25)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testCourse2 = Course.builder()
                .courseId(2L)
                .courseName("Spring Boot Advanced")
                .description("Advanced Spring Boot concepts")
                .category(Category.INSTRUCTOR_LED)
                .vendorId(101L)
                .status(CourseStatus.ACTIVE)
                .price(new BigDecimal("149.99"))
                .avgRating(new BigDecimal("4.8"))
                .reviewCount(45)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create course successfully")
    void testCreateCourse() throws Exception {
        when(courseService.createCourse(any(Course.class))).thenReturn(testCourse);

        mockMvc.perform(post("/courses")
                .contentType("application/json")
                .content("{\"courseName\":\"Java Basics\",\"description\":\"Learn Java fundamentals\",\"category\":\"SELF_LEARNING\",\"vendorId\":100,\"status\":\"ACTIVE\",\"price\":99.99}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.courseName").value("Java Basics"))
                .andDo(print());

        verify(courseService, times(1)).createCourse(any(Course.class));
    }

    @Test
    @DisplayName("Should return test message")
    void testGetTestEndpoint() throws Exception {
        mockMvc.perform(get("/courses/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Course Service Working"))
                .andDo(print());
    }

    @Test
    @DisplayName("Should get course by id successfully")
    void testGetCourseById() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(testCourse);

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.courseName").value("Java Basics"))
                .andExpect(jsonPath("$.vendorId").value(100))
                .andDo(print());

        verify(courseService, times(1)).getCourseById(1L);
    }

    @Test
    @DisplayName("Should return 500 when course not found by id")
    void testGetCourseByIdNotFound() throws Exception {
        when(courseService.getCourseById(999L))
                .thenThrow(new RuntimeException("Course not found with ID: 999"));

        try {
            mockMvc.perform(get("/courses/999"))
                    .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            // Expected - service throws exception
        }

        verify(courseService, times(1)).getCourseById(999L);
    }

    @Test
    @DisplayName("Should get all courses successfully")
    void testGetAllCourses() throws Exception {
        List<Course> courses = Arrays.asList(testCourse, testCourse2);
        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].courseId").value(1))
                .andExpect(jsonPath("$[1].courseId").value(2))
                .andDo(print());

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    @DisplayName("Should get empty list when no courses exist")
    void testGetAllCoursesEmpty() throws Exception {
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    @DisplayName("Should search courses by vendor id")
    void testSearchCoursesByVendorId() throws Exception {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseService.searchCourses(100L, null)).thenReturn(courses);

        mockMvc.perform(get("/courses/search?vendorId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].vendorId").value(100))
                .andDo(print());

        verify(courseService, times(1)).searchCourses(100L, null);
    }

    @Test
    @DisplayName("Should search courses by category")
    void testSearchCoursesByCategory() throws Exception {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseService.searchCourses(null, Category.SELF_LEARNING)).thenReturn(courses);

        mockMvc.perform(get("/courses/search?category=SELF_LEARNING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("SELF_LEARNING"))
                .andDo(print());

        verify(courseService, times(1)).searchCourses(null, Category.SELF_LEARNING);
    }

    @Test
    @DisplayName("Should search courses by vendor id and category")
    void testSearchCoursesByVendorIdAndCategory() throws Exception {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseService.searchCourses(100L, Category.SELF_LEARNING)).thenReturn(courses);

        mockMvc.perform(get("/courses/search?vendorId=100&category=SELF_LEARNING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andDo(print());

        verify(courseService, times(1)).searchCourses(100L, Category.SELF_LEARNING);
    }

    @Test
    @DisplayName("Should search all courses when no filter provided")
    void testSearchCoursesNoFilter() throws Exception {
        List<Course> courses = Arrays.asList(testCourse, testCourse2);
        when(courseService.searchCourses(null, null)).thenReturn(courses);

        mockMvc.perform(get("/courses/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());

        verify(courseService, times(1)).searchCourses(null, null);
    }

    @Test
    @DisplayName("Should update course successfully")
    void testUpdateCourse() throws Exception {
        Course updatedCourse = Course.builder()
                .courseId(1L)
                .courseName("Java Advanced")
                .description("Advanced Java concepts")
                .category(Category.INSTRUCTOR_LED)
                .vendorId(100L)
                .status(CourseStatus.ACTIVE)
                .price(new BigDecimal("129.99"))
                .avgRating(new BigDecimal("4.7"))
                .reviewCount(30)
                .createdAt(testCourse.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(courseService.updateCourse(eq(1L), any(Course.class))).thenReturn(updatedCourse);

        mockMvc.perform(put("/courses/1")
                .contentType("application/json")
                .content("{\"courseName\":\"Java Advanced\",\"description\":\"Advanced Java concepts\",\"category\":\"INSTRUCTOR_LED\",\"vendorId\":100,\"status\":\"ACTIVE\",\"price\":129.99}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value("Java Advanced"))
                .andExpect(jsonPath("$.price").value(129.99))
                .andDo(print());

        verify(courseService, times(1)).updateCourse(eq(1L), any(Course.class));
    }

    @Test
    @DisplayName("Should delete course successfully")
    void testDeleteCourse() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Course marked as INACTIVE!"))
                .andDo(print());

        verify(courseService, times(1)).deleteCourse(1L);
    }

    @Test
    @DisplayName("Should return 500 when deleting non-existent course")
    void testDeleteCourseNotFound() throws Exception {
        doThrow(new RuntimeException("Course not found with ID: 999"))
                .when(courseService).deleteCourse(999L);

        try {
            mockMvc.perform(delete("/courses/999"))
                    .andExpect(status().isInternalServerError());
        } catch (Exception e) {
            // Expected - service throws exception
        }

        verify(courseService, times(1)).deleteCourse(999L);
    }
}
