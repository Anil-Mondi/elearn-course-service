package com.cts.elearn.service;

import com.cts.elearn.dao.CourseRepository;
import com.cts.elearn.entity.Course;
import com.cts.elearn.entity.Course.Category;
import com.cts.elearn.entity.Course.CourseStatus;
import com.cts.elearn.event.CoursePurchasedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("CourseService Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private Course testCourse2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
    void testCreateCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.createCourse(testCourse);

        assertNotNull(result);
        assertEquals(1L, result.getCourseId());
        assertEquals("Java Basics", result.getCourseName());
        assertEquals(100L, result.getVendorId());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should retrieve course by id successfully")
    void testGetCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        Course result = courseService.getCourseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCourseId());
        assertEquals("Java Basics", result.getCourseName());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when course not found by id")
    void testGetCourseByIdNotFound() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.getCourseById(999L);
        });

        assertTrue(exception.getMessage().contains("Course not found with ID: 999"));
        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should retrieve all courses successfully")
    void testGetAllCourses() {
        List<Course> courses = Arrays.asList(testCourse, testCourse2);
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java Basics", result.get(0).getCourseName());
        assertEquals("Spring Boot Advanced", result.get(1).getCourseName());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no courses exist")
    void testGetAllCoursesEmpty() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        List<Course> result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should search courses by vendor id and category")
    void testSearchCoursesByVendorIdAndCategory() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByVendorIdAndCategory(100L, Category.SELF_LEARNING))
                .thenReturn(courses);

        List<Course> result = courseService.searchCourses(100L, Category.SELF_LEARNING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getCourseName());
        verify(courseRepository, times(1))
                .findByVendorIdAndCategory(100L, Category.SELF_LEARNING);
    }

    @Test
    @DisplayName("Should search courses by vendor id only")
    void testSearchCoursesByVendorIdOnly() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByVendorId(100L)).thenReturn(courses);

        List<Course> result = courseService.searchCourses(100L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getVendorId());
        verify(courseRepository, times(1)).findByVendorId(100L);
    }

    @Test
    @DisplayName("Should search courses by category only")
    void testSearchCoursesByCategory() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByCategory(Category.SELF_LEARNING))
                .thenReturn(courses);

        List<Course> result = courseService.searchCourses(null, Category.SELF_LEARNING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Category.SELF_LEARNING, result.get(0).getCategory());
        verify(courseRepository, times(1))
                .findByCategory(Category.SELF_LEARNING);
    }

    @Test
    @DisplayName("Should return all courses when no search filter provided")
    void testSearchCoursesNoFilter() {
        List<Course> courses = Arrays.asList(testCourse, testCourse2);
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.searchCourses(null, null);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when search returns no results")
    void testSearchCoursesNoResults() {
        when(courseRepository.findByVendorId(999L)).thenReturn(Collections.emptyList());

        List<Course> result = courseService.searchCourses(999L, null);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(courseRepository, times(1)).findByVendorId(999L);
    }

    @Test
    @DisplayName("Should update course successfully")
    void testUpdateCourse() {
        Course updatedCourse = Course.builder()
                .courseName("Java Advanced")
                .description("Advanced Java concepts")
                .category(Category.INSTRUCTOR_LED)
                .vendorId(100L)
                .status(CourseStatus.ACTIVE)
                .price(new BigDecimal("129.99"))
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.updateCourse(1L, updatedCourse);

        assertNotNull(result);
        assertEquals("Java Advanced", testCourse.getCourseName());
        assertEquals("Advanced Java concepts", testCourse.getDescription());
        assertEquals(Category.INSTRUCTOR_LED, testCourse.getCategory());
        assertEquals(new BigDecimal("129.99"), testCourse.getPrice());
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent course")
    void testUpdateCourseNotFound() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        Course updatedCourse = Course.builder()
                .courseName("Java Advanced")
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.updateCourse(999L, updatedCourse);
        });

        assertTrue(exception.getMessage().contains("Course not found with ID: 999"));
        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should delete course by marking as inactive")
    void testDeleteCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        courseService.deleteCourse(1L);

        assertEquals(CourseStatus.INACTIVE, testCourse.getStatus());
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent course")
    void testDeleteCourseNotFound() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.deleteCourse(999L);
        });

        assertTrue(exception.getMessage().contains("Course not found with ID: 999"));
        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should send course purchase event successfully")
    void testPurchaseCourse() {
        Long learnerId = 1L;
        Long courseId = 1L;

        courseService.purchaseCourse(learnerId, courseId);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CoursePurchasedEvent> eventCaptor = ArgumentCaptor.forClass(CoursePurchasedEvent.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertEquals("course.purchased", topicCaptor.getValue());
        assertEquals("1", keyCaptor.getValue());

        CoursePurchasedEvent event = eventCaptor.getValue();
        assertNotNull(event);
        assertEquals(learnerId, event.getLearnerId());
        assertEquals(courseId, event.getCourseId());
        assertEquals(1L, event.getServiceId());
        assertNotNull(event.getEventId());
    }

    @Test
    @DisplayName("Should send course purchase event with different learner and course ids")
    void testPurchaseCourseWithDifferentIds() {
        Long learnerId = 5L;
        Long courseId = 10L;

        courseService.purchaseCourse(learnerId, courseId);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CoursePurchasedEvent> eventCaptor = ArgumentCaptor.forClass(CoursePurchasedEvent.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertEquals("course.purchased", topicCaptor.getValue());
        assertEquals("5", keyCaptor.getValue());

        CoursePurchasedEvent event = eventCaptor.getValue();
        assertEquals(5L, event.getLearnerId());
        assertEquals(10L, event.getCourseId());
    }

    @Test
    @DisplayName("Should handle update with all fields changed")
    void testUpdateCourseAllFieldsChanged() {
        Course updatedCourse = Course.builder()
                .courseName("Completely New Name")
                .description("Completely new description")
                .category(Category.INSTRUCTOR_LED)
                .vendorId(200L)
                .status(CourseStatus.INACTIVE)
                .price(new BigDecimal("299.99"))
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course result = courseService.updateCourse(1L, updatedCourse);

        assertNotNull(result);
        assertEquals("Completely New Name", testCourse.getCourseName());
        assertEquals("Completely new description", testCourse.getDescription());
        assertEquals(Category.INSTRUCTOR_LED, testCourse.getCategory());
        assertEquals(200L, testCourse.getVendorId());
        assertEquals(CourseStatus.INACTIVE, testCourse.getStatus());
        assertEquals(new BigDecimal("299.99"), testCourse.getPrice());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should search courses with both vendor id and category filters")
    void testSearchCoursesWithBothFilters() {
        Course course = Course.builder()
                .courseId(1L)
                .courseName("Test Course")
                .vendorId(100L)
                .category(Category.SELF_LEARNING)
                .build();

        List<Course> courses = Arrays.asList(course);
        when(courseRepository.findByVendorIdAndCategory(100L, Category.SELF_LEARNING))
                .thenReturn(courses);

        List<Course> result = courseService.searchCourses(100L, Category.SELF_LEARNING);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getVendorId());
        assertEquals(Category.SELF_LEARNING, result.get(0).getCategory());
        verify(courseRepository, times(1))
                .findByVendorIdAndCategory(100L, Category.SELF_LEARNING);
        verify(courseRepository, never()).findByVendorId(anyLong());
        verify(courseRepository, never()).findByCategory(any(Category.class));
    }

    @Test
    @DisplayName("Should verify correct search path when only vendorId is provided")
    void testSearchCoursesVendorIdPath() {
        when(courseRepository.findByVendorId(100L))
                .thenReturn(Arrays.asList(testCourse));

        List<Course> result = courseService.searchCourses(100L, null);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findByVendorId(100L);
        verify(courseRepository, never()).findByVendorIdAndCategory(anyLong(), any(Category.class));
        verify(courseRepository, never()).findByCategory(any(Category.class));
    }

    @Test
    @DisplayName("Should verify correct search path when only category is provided")
    void testSearchCoursesCategoryPath() {
        when(courseRepository.findByCategory(Category.INSTRUCTOR_LED))
                .thenReturn(Arrays.asList(testCourse2));

        List<Course> result = courseService.searchCourses(null, Category.INSTRUCTOR_LED);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findByCategory(Category.INSTRUCTOR_LED);
        verify(courseRepository, never()).findByVendorIdAndCategory(anyLong(), any(Category.class));
        verify(courseRepository, never()).findByVendorId(anyLong());
    }

    @Test
    @DisplayName("Should verify correct search path when no filters are provided")
    void testSearchCoursesDefaultPath() {
        when(courseRepository.findAll())
                .thenReturn(Arrays.asList(testCourse, testCourse2));

        List<Course> result = courseService.searchCourses(null, null);

        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
        verify(courseRepository, never()).findByVendorIdAndCategory(anyLong(), any(Category.class));
        verify(courseRepository, never()).findByVendorId(anyLong());
        verify(courseRepository, never()).findByCategory(any(Category.class));
    }
}
