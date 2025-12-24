package nl.rutgerkok.doughworldgenerator.config;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class FormulaTest {

    @Test
    void justNumbers() throws ParseException {
        assertEquals(5.0, new Formula("f(x) = 5").evaluate(0));
        assertEquals(-5.0, new Formula("f(x) = -5").evaluate(0));
    }

    @Test
    void justVariable() throws ParseException {
        assertEquals(10.0, new Formula("f(x) = x").evaluate(10));
        assertEquals(-3.0, new Formula("f(x) = x").evaluate(-3));
    }

    @Test
    void sumition() throws ParseException {
        assertEquals(7.0, new Formula("f(x) = sum(3, 4)").evaluate(0));
        assertEquals(1.0, new Formula("f(x) = sum(x, 4)").evaluate(-3));
    }

    @Test
    void gaussian() throws ParseException {
        float x = 0.5f;
        float mu = 1;
        float sigma = 0.3f;
        assertEquals(Math.exp(-0.5 * Math.pow((x - mu) / sigma, 2)), new Formula("f(x) = gauss(x, 1, 0.3)").evaluate(x), 1E-6);
    }

    @Test
    void clamping() throws ParseException {
        assertEquals(10.0, new Formula("f(x) = clamp(15, 0, 10)").evaluate(0));
        assertEquals(0.0, new Formula("f(x) = clamp(-5, 0, 10)").evaluate(0));
        assertEquals(5.0, new Formula("f(x) = clamp(5, 0, 10)").evaluate(0));
    }

    @Test
    void mean() throws ParseException {
        assertEquals(5.0, new Formula("f(x) = mean(2, 8)").evaluate(0));
        assertEquals(0.0, new Formula("f(x) = mean(-2, 2)").evaluate(0));
    }

    @Test
    void nested() throws ParseException {
        assertEquals(11.0, new Formula("f(x) = sum(3, mean(x, 10))").evaluate(6));
    }

    @Test
    void addToSum() throws ParseException {
        // 'add' is an alias for 'sum' - test if that works, and if toString converts it back to 'sum'
        Formula formula = new Formula("f(x) = add(3, 4)");
        assertEquals(7, formula.evaluate(0));
        assertEquals("f(x) = sum(3, 4)", formula.toString());
    }

    @Test
    void testToString() throws ParseException {
        assertEquals("f(x) = sum(3, 4)", new Formula("f(x) = sum(3, 4)").toString());
        assertEquals("f(x) = clamp(x, 0, 10.5)", new Formula("f(x) = clamp(x, 0, 10.5)").toString());
    }
}