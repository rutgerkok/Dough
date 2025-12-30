package nl.rutgerkok.doughworldgenerator.config;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Formula {

    /**
     * The identity formula: f(x) = x
     */
    public static final Formula IDENTITY;

    static {
        try {
            IDENTITY = new Formula("f(x) = x");
        } catch (ParseException e) {
            throw new AssertionError(e);
        }
    }

    private interface Expression {
        float calculate(float x);
        
        String toString();
    }
    
    private static class Literal implements Expression {
        private final float value;
        
        public Literal(float value) {
            this.value = value;
        }
        
        @Override
        public float calculate(float x) {
            return value;
        }
        
        @Override
        public String toString() {
            if (value == (int) value) {
                return Integer.toString((int) value);
            }
            return Float.toString(value);
        }
    }
    
    private static class Variable implements Expression {
        @Override
        public float calculate(float x) {
            return x;
        }
        
        @Override
        public String toString() {
            return "x";
        }
    }

    private enum UniOperator {
        EXP,
        ABS,
        SQUARE,
        SQRT,
        SIGN;

        public String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public float operate(float a) {
            return switch (this) {
                case EXP -> (float) Math.exp(a);
                case ABS -> Math.abs(a);
                case SQUARE -> a * a;
                case SQRT -> (float) Math.sqrt(a);
                case SIGN -> Math.signum(a);
            };
        }

        public static UniOperator parseName(String str, int parseOffset) throws ParseException {
            try {
                return UniOperator.valueOf(str.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                String availableOperators = Arrays.stream(UniOperator.values())
                        .map(UniOperator::toString)
                        .collect(Collectors.joining(", "));
                throw new ParseException("Operator not found: " + str + ". Available operators with one argument: [" + availableOperators + "]", parseOffset);
            }
        }
    }
    
    private enum BiOperator {
        ADD, // Alias for SUM
        SUB, 
        MUL, 
        DIV,
        POW,
        MIN,
        MAX,
        MEAN,
        SUM;
        
        public String toString() {
            if (this == ADD) {
                return "sum"; // Normalize naming
            }
            return this.name().toLowerCase(Locale.ROOT);
        }
        
        public float operate(float a, float b) {
            return switch (this) {
                case ADD, SUM -> a + b;
                case SUB -> a - b;
                case MUL -> a * b;
                case DIV -> a / b;
                case POW -> (float) Math.pow(a, b);
                case MIN -> Math.min(a, b);
                case MAX -> Math.max(a, b);
                case MEAN -> 0.5f * (a + b);
            };
        }

        public static BiOperator parseName(String str, int parseOffset) throws ParseException {
            try {
                return BiOperator.valueOf(str.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                String availableOperators = Arrays.stream(BiOperator.values())
                    .map(BiOperator::toString)
                    .collect(Collectors.joining(", "));
                throw new ParseException("Operator not found: " + str + ". Available operators with two arguments: [" + availableOperators + "]", parseOffset);
            }
        }
    }

    private enum TriOperator {
        CLAMP,
        GAUSS,
        MEDIAN;
        public String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public float operate(float a, float b, float c) {
            return switch (this) {
                case CLAMP -> Math.max(b, Math.min(a, c)); // Args: value, min, max
                case GAUSS -> (float) (Math.exp(-Math.pow(a - b, 2) / (2 * Math.pow(c, 2)))); // Args: x, mean, stddev
                case MEDIAN -> {
                    float[] values = {a, b, c};
                    Arrays.sort(values);
                    yield values[1];
                }
            };
        }

        public static TriOperator parseName(String str, int parseOffset) throws ParseException {
            try {
                return TriOperator.valueOf(str.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                String availableOperators = Arrays.stream(TriOperator.values())
                    .map(TriOperator::toString)
                    .collect(Collectors.joining(", "));
                throw new ParseException("Operator not found: " + str + ". Available operators with three arguments: [" + availableOperators + "]", parseOffset);
            }
        }
    }
    


    private record UniOperation(UniOperator operator, Expression operand) implements Expression {
        @Override
        public float calculate(float x) {
            return operator.operate(operand.calculate(x));
        }

        @Override
        public String toString() {
            return this.operator + "(" + operand.toString() + ")";
        }

    }
    
    private record BiOperation(BiOperator operator, Expression first, Expression second) implements Expression {
        @Override
        public float calculate(float x) {
            return operator.operate(first.calculate(x), second.calculate(x));
        }
        
        @Override
        public String toString() {
            return this.operator + "(" + first.toString() + ", " + second.toString() + ")";
        }

    }

    private record TriOperation(TriOperator operator, Expression first, Expression second, Expression third) implements Expression {
        @Override
        public float calculate(float x) {
            return operator.operate(first.calculate(x), second.calculate(x), third.calculate(x));
        }

        @Override
        public String toString() {
            return this.operator + "(" + first.toString() + ", " + second.toString() + ", " + third.toString() + ")";
        }

    }

    private static Expression parse(int offset, String toParse) throws ParseException {
        int firstParen = toParse.indexOf('(');
        if (firstParen == -1) {
            // No parentheses, must be literal or variable
            String toParseTrimmed = toParse.trim();
            if (toParseTrimmed.equalsIgnoreCase("x")) {
                return new Variable();
            } else {
                try {
                    float value = Float.parseFloat(toParseTrimmed);
                    return new Literal(value);
                } catch (NumberFormatException e) {
                    throw new ParseException(toParse, offset);
                }
            }
        } else {
            String operatorString = toParse.substring(0, firstParen);
            String insideParens = toParse.substring(firstParen + 1, toParse.length() - 1);

            // Now walk through insideParens to find the main comma (if any)
            int mainCommaIndex = findMainComma(insideParens);

            if (mainCommaIndex != -1) {
                // Found a comma, must be a binary operator
                String firstArgument = insideParens.substring(0, mainCommaIndex);
                String secondArgument = insideParens.substring(mainCommaIndex + 1);
                int commaInSecondArgument = findMainComma(secondArgument);
                if (commaInSecondArgument != -1) {
                    // Found a second comma, must be a ternary operator
                    String thirdArgument = secondArgument.substring(commaInSecondArgument + 1);
                    secondArgument = secondArgument.substring(0, commaInSecondArgument);
                    TriOperator operator = TriOperator.parseName(operatorString, offset);
                    Expression firstExpr = parse(offset + firstParen + 1, firstArgument);
                    Expression secondExpr = parse(offset + firstParen + 1 + mainCommaIndex + 1, secondArgument);
                    Expression thirdExpr = parse(offset + firstParen + 1 + mainCommaIndex + 1 + commaInSecondArgument + 1, thirdArgument);
                    return new TriOperation(operator, firstExpr, secondExpr, thirdExpr);
                }

                BiOperator operator = BiOperator.parseName(operatorString, offset);
                Expression firstExpr = parse(offset + firstParen + 1, firstArgument);
                Expression secondExpr = parse(offset + firstParen + 1 + mainCommaIndex + 1, secondArgument);
                return new BiOperation(operator, firstExpr, secondExpr);
            } else {
                // Unary operator
                UniOperator operator = UniOperator.parseName(operatorString, offset);
                Expression operand = parse(offset + firstParen + 1, insideParens);
                return new UniOperation(operator, operand);
            }
    }}

    /**
     * Finds the index of the main comma in a string, ignoring commas inside parentheses.
     * @param searchString The string to search
     * @return The index of the main comma, or -1 if none found.
     */
    private static int findMainComma(String searchString) {
        int parenDepth = 0;
        int mainCommaIndex = -1;
        for (int i = 0; i < searchString.length(); i++) {
            char c = searchString.charAt(i);
            if (c == '(') {
                parenDepth++;
            } else if (c == ')') {
                parenDepth--;
            } else if (c == ',' && parenDepth == 0) {
                mainCommaIndex = i;
                break;
            }
        }
        return mainCommaIndex;
    }

    private final Expression rootExpression;

    public Formula(String formulaString) throws ParseException {
        // Use regex to make sure the string starts with "f(x) = "
        Pattern prefixPattern = Pattern.compile("^[Ff]\\s*\\(\\s*[Xx]\\s*\\)\\s*=\\s*");
        Matcher matcher = prefixPattern.matcher(formulaString);
        if (!matcher.find()) {
            throw new ParseException("Formula must start with 'f(x) = '", 0);
        }
        int prefixLength = matcher.end();

        this.rootExpression = parse(prefixLength, formulaString.substring(prefixLength));
    }

    public float evaluate(float x) {
        float value = rootExpression.calculate(x);
        if (Float.isNaN(value)) {
            return 0.0f;
        }
        return value;
    }

    @Override
    public String toString() {
        return "f(x) = " + rootExpression;
    }
}
