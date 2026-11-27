package mn.edu.must.sqat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScholarshipCalculatorTest {

    ScholarshipCalculator calc;

    @BeforeEach
    void setUp() { calc = new ScholarshipCalculator(); }

    // ---- letterGrade: ЯГ утга шалгах ----
    @Test
    void letterGrade_returnsExactGrades() {
        assertEquals("A", calc.letterGrade(95));
        assertEquals("B", calc.letterGrade(85));
        assertEquals("C", calc.letterGrade(75));
        assertEquals("D", calc.letterGrade(65));
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---- gpa: ЯГ утга шалгах ----
    @Test
    void gpa_returnsExactValues() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), 0.001); // A+B = 7/2
        assertEquals(1.5, calc.gpa(new double[]{75, 65}), 0.001); // C+D = 3/2
        assertEquals(0.0, calc.gpa(new double[]{40}), 0.001);      // F = 0
        assertEquals(4.0, calc.gpa(new double[]{100}), 0.001);     // A = 4
        assertEquals(0.0, calc.gpa(new double[]{0}), 0.001);       // F = 0
	assertEquals(3.0, calc.gpa(new double[]{90, 70}), 0.001);  // A+
    }
    @Test
    void gpa_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    // ---- scholarship: ЯГ утга шалгах ----
    @Test
    void scholarship_returnsExactValues() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
        assertEquals(70, calc.scholarship(3.5, 13, false));
        assertEquals(50, calc.scholarship(3.1, 5, false));
        assertEquals(0, calc.scholarship(2.0, 1, false));
        assertEquals(0, calc.scholarship(4.0, 20, true)); // violation
    }

    @Test
    void scholarship_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.1, 10, false));
    }
    // ---- B2: Зааг утгын тестүүд ----
    @Test
    void letterGrade_boundaryValues() {
        assertEquals("A", calc.letterGrade(90.0));
        assertEquals("B", calc.letterGrade(89.99));
        assertEquals("B", calc.letterGrade(80.0));
        assertEquals("C", calc.letterGrade(79.99));
        assertEquals("C", calc.letterGrade(70.0));
        assertEquals("D", calc.letterGrade(69.99));
        assertEquals("D", calc.letterGrade(60.0));
        assertEquals("F", calc.letterGrade(59.99));
        assertEquals("F", calc.letterGrade(0.0));
        assertEquals("A", calc.letterGrade(100.0));
    }

    @Test
    void scholarship_boundaryValues() {
        // 3.8 / 15 зааг
        assertEquals(100, calc.scholarship(3.8, 15, false));
        assertEquals(70, calc.scholarship(3.79, 15, false));  // 3.8-аас доош
        assertEquals(70, calc.scholarship(3.8, 14, false));   // 15-аас доош
        // 3.4 / 12 зааг
        assertEquals(70, calc.scholarship(3.4, 12, false));
        assertEquals(50, calc.scholarship(3.39, 12, false));  // 3.4-өөс доош
        assertEquals(50, calc.scholarship(3.4, 11, false));   // 12-оос доош
        // 3.0 зааг
        assertEquals(50, calc.scholarship(3.0, 0, false));
        assertEquals(0, calc.scholarship(2.99, 100, false));
        // Оролтын хязгаар
        assertEquals(100, calc.scholarship(4.0, 15, false));
        assertEquals(0, calc.scholarship(0.0, 0, false));
    }

    @Test
    void gpa_allLetterGradesContribute() {
        // A=4, B=3, C=2, D=1, F=0
        assertEquals(4.0, calc.gpa(new double[]{95}), 0.001);
        assertEquals(3.0, calc.gpa(new double[]{85}), 0.001);
        assertEquals(2.0, calc.gpa(new double[]{75}), 0.001);
        assertEquals(1.0, calc.gpa(new double[]{65}), 0.001);
        assertEquals(0.0, calc.gpa(new double[]{55}), 0.001);
        // Бүх дүн холилдсон
        assertEquals(2.0, calc.gpa(new double[]{95, 85, 75, 65, 55}), 0.001);
    }
}
