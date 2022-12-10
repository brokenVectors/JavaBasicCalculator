import java.util.*;
public class Main {
    public static boolean isNumeric(char ch, boolean allowMinus) {
        if(allowMinus && ch == '-') return true;
        return ch == '0' ||
                ch == '1' ||
                ch == '2' ||
                ch == '3' ||
                ch == '4' ||
                ch == '5' ||
                ch == '6' ||
                ch == '7' ||
                ch == '8' ||
                ch == '9' ||
                ch == '.';
    }
    public static boolean isNumeric(char ch) {
        return isNumeric(ch, false);
    }
    public static boolean isNumeric(String str) {
        for(char ch: str.toCharArray()) {
            if(!isNumeric(ch, str.length() > 1)) return false;
        }
        return true;
    }
    public static boolean isOperation(String token) {
        return token.equals("+") ||
                token.equals("-") ||
                token.equals("*") ||
                token.equals("/") ||
                token.equals("%");
    }
    private static Stack<String> tokenize(String expression) {
        expression = expression.replaceAll(" ", ""); // Filter out whitespace
        Stack<String> tokens = new Stack<>();
        char[] chars = expression.toCharArray();
        Character prevChar = null;
        Character prevPrevChar = null;
        for (char ch : chars) {
            // Check if prevChar is - and that the second previous character isn't a number.
            boolean canAppendCharacter = prevChar != null && (
                    (isNumeric(prevChar) && isNumeric(ch))
                            || (prevChar.equals('-') && (prevPrevChar == null || !isNumeric(prevPrevChar)))
            );
            if (canAppendCharacter) {
                tokens.set(tokens.size()-1, tokens.get(tokens.size()-1) + ch);
            }
            else if(ch != ' ') tokens.push(Character.toString(ch));
            prevPrevChar = prevChar;
            prevChar = ch;
        }
        return tokens;
    }
    private static int getPrecedence(String op) {
        switch(op) {
            case "+":
            case "-":
                return 2;
            case "/":
            case "%":
            case "*":
                return 3;
            case "^":
                return 4;
            default:
                return 0;
        }
    }
    private static double evaluatePostfix(Stack<String> tokens) {
        Stack<Double> numbers = new Stack<>();
        for(String token: tokens) {
            if(isNumeric(token)) {
                numbers.push(Double.parseDouble(token));
            }
            else {
                double b = numbers.pop();
                double a = numbers.pop();
                switch(token) {
                    case "+": numbers.push(a + b); break;
                    case "-": numbers.push(a - b); break;
                    case "*": numbers.push(a * b); break;
                    case "/": numbers.push(a / b); break;
                    case "^": numbers.push(Math.pow(a, b)); break;
                    case "%": numbers.push(a % b); break;
                }
            }
        }
        return numbers.peek();
    }
    private static double evaluate(String expression) {
        // Shunting-yard(Infix -> Postfix) and then evaluate postfix.
        Stack<String> outputQueue = new Stack<>();
        Stack<String> operatorStack = new Stack<>();
        for(String token: tokenize(expression)) {
            if(isOperation(token)) {
                while(operatorStack.size() > 0 && (getPrecedence(operatorStack.peek()) > getPrecedence(token))) {
                    outputQueue.push(operatorStack.pop());
                }
                operatorStack.push(token);
            }
            else if(token.equals("(")) operatorStack.push(token);
            else if(token.equals(")")) {
                while(operatorStack.size() > 0 && !operatorStack.peek().equals("("))
                    outputQueue.push(operatorStack.pop());
                operatorStack.pop();
            }
            else outputQueue.push(token);
        }
        // While there are operators on the stack, pop them to the queue
        while(operatorStack.size() > 0) {
            outputQueue.add(operatorStack.pop());
        }
        return evaluatePostfix(outputQueue);
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String expression = scanner.nextLine();
            try {
                System.out.println(evaluate(expression));
            } catch (Exception e) {
                System.out.println("Invalid expression!");
            }
        }
    }
}
