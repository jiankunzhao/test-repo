import java.io.*;

class Calc {
    int token;
    int value;
    int ch;
    private PushbackInputStream input;
    final int NUMBER = 256;

    Calc(PushbackInputStream is) {
        input = is;
    }

    int getToken() { /* tokens are characters */
        while (true) {
            try {
                ch = input.read();
                if (ch == ' ' || ch == '\t' || ch == '\r')
                    ;
                else if (Character.isDigit(ch)) {
                    value = number();
                    input.unread(ch);
                    return NUMBER;
                } else
                    return ch;
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private int number() {
        /* number -> digit { digit } */
        int result = ch - '0';
        try {
            ch = input.read();
            while (Character.isDigit(ch)) {
                result = 10 * result + ch - '0';
                ch = input.read();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return result;
    }

    void error() {
        System.out.printf("parse error : %d\n", ch);
        // System.exit(1);
    }

    void match(int c) {
        if (token == c)
            token = getToken();
        else
            error();
    }

    void command() {
        /* command -> expr '\n' */
        // int result = aexp(); // TODO: [Remove this line!!]
        Object result = expr(); // TODO: [Use this line for solution]
        if (token == '\n') /* end the parse and print the result */
            System.out.println("The result is:" + result);
        else
            error();
    }

    Object expr() {
        /* <expr> -> <bexp> {& <bexp> | '|'<bexp>} | !<expr> | true | false */
        Object result;
        // result = ""; // TODO: [Remove this line!!]
        if (token == '!') {
            // !<expr>
            match('!');
            result = !(boolean) expr();
        } else if (token == 't') {
            // true
            match('t');
            result = (boolean) true;
        } else if (token == 'f') {
            // false
            // TODO: [Fill in your code here]
            match('f');
            result = (boolean) false;
        } else {
            /* <bexp> {& <bexp> | '|'<bexp>} */
            result = bexp();
            while (token == '&' || token == '|') {
                if (token == '&') {
                    // TODO: [Fill in your code here]
                    match('&');
                    Object right = bexp();
                    result = (boolean) result && (boolean) right;
                } else if (token == '|') {
                    // TODO: [Fill in your code here]
                    match('|');
                    Object right = bexp();
                    result = (boolean) result || (boolean) right;
                }
            }
        }
        return result;
    }

    Object bexp() {
        /* <bexp> -> <aexp> [<relop> <aexp>] */
        Object result;
        // result = ""; // TODO: [Remove this line!!]
        int aexp1 = aexp();
        if (token == '<' || token == '>' || token == '=' || token == '!') { // <relop>
            /* Check each string using relop(): "<", "<=", ">", ">=", "==", "!=" */
            // TODO: [Fill in your code here]
            String operator = relop(); // 관계 연산자 분석
            int aexp2 = aexp(); // 두 번째 산술 표현식 해석

            // 연산자에 따라 비교하고 결과를 result에 저장
            if (operator.equals("<")) {
                result = aexp1 < aexp2;
            } else if (operator.equals("<=")) {
                result = aexp1 <= aexp2;
            } else if (operator.equals(">")) {
                result = aexp1 > aexp2;
            } else if (operator.equals(">=")) {
                result = aexp1 >= aexp2;
            } else if (operator.equals("==")) {
                result = aexp1 == aexp2;
            } else if (operator.equals("!=")) {
                result = aexp1 != aexp2;
            } else {
                // 잘못된 연산자 처리
                throw new RuntimeException("잘못된 관계 연산자：" + operator);
            }
        } else {
            result = aexp1;
        }
        return result;
    }

    String relop() {
        /* <relop> -> ( < | <= | > | >= | == | != ) */
        String result = "";
        // TODO: [Fill in your code here]
        if (token == '<') {
            match('<');
            if (token == '=') {
                match('=');
                result = "<=";
            } else {
                result = "<";
            }
        } else if (token == '>') {
            match('>');
            if (token == '=') {
                match('=');
                result = ">=";
            } else {
                result = ">";
            }
        } else if (token == '=') {
            match('=');
            if (token == '=') {
                match('=');
                result = "==";
            } else {
                result = "=";
            }
        } else if (token == '!') {
            match('!');
            if (token == '=') {
                match('=');
                result = "!=";
            } else {
                result = "!";
            }
        }
        return result;
    }

    // TODO: [Modify code of aexp() for <aexp> -> <term> { + <term> | - <term> }]
    int aexp() {
        /* expr -> term { '+' term } */
        int result = term();
        while (token == '+' || token == '-') {
            if (token == '+') {
                match('+');
                result += term();
            } else if (token == '-') {
                match('-');
                result -= term();
            }
        }
        return result;
    }

    // TODO: [Modify code of term() for <term> -> <factor> { * <factor> | /
    // <factor>}]
    int term() {
        /* term -> factor { '*' factor } */
        int result = factor();
        while (token == '*' || token == '/') {
            if (token == '*') {
                match('*');
                result *= factor();
            } else if (token == '/') {
                match('/');
                int divisor = factor();
                if (divisor != 0) {
                    result /= divisor;
                } else {
                    // 0으로 나누기 오류
                    throw new ArithmeticException("제로 오류로 나누기");
                }
            }
        }
        return result;
    }

    int factor() {
        /* factor -> '(' expr ')' | number */
        int result = 0;
        if (token == '(') {
            match('(');
            result = aexp();
            match(')');
        } else if (token == NUMBER) {
            result = value;
            match(NUMBER); // token = getToken();
        }
        return result;
    }

    void parse() {
        token = getToken(); // get the first token
        command(); // call the parsing command
    }

    public static void main(String args[]) {
        Calc calc = new Calc(new PushbackInputStream(System.in));
        while (true) {
            System.out.print(">> ");
            calc.parse();
        }
    }
}