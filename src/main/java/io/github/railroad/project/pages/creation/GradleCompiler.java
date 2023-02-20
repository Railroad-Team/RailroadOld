package io.github.railroad.project.pages.creation;

import org.apache.commons.lang3.StringUtils;

public class GradleCompiler {
    public static class Parser {
        public Parser() {
        }

        public SyntaxToken[] parse(String input) {
            String[] split = input.split(" ");
            SyntaxToken[] tokens = new SyntaxToken[split.length + 1];
            for (int index = 0; index < split.length; index++) {
                String str = split[index];
                if (StringUtils.isNumeric(str)) {
                    tokens[index] = SyntaxToken.NUMBER;
                } else if(str.equals("+")) {
                    tokens[index] = SyntaxToken.PLUS;
                } else if (str.equals("-")) {
                    tokens[index] = SyntaxToken.MINUS;
                } else if (str.equals("*")) {
                    tokens[index] = SyntaxToken.MULTIPLY;
                } else if (str.equals("/")) {
                    tokens[index] = SyntaxToken.DIVIDE;
                } else if (str.equals("(")) {
                    tokens[index] = SyntaxToken.LEFT_PARENTHESIS;
                } else if (str.equals(")")) {
                    tokens[index] = SyntaxToken.RIGHT_PARENTHESIS;
                } else {
                    throw new IllegalArgumentException("Invalid token: " + str);
                }
            }

            return tokens;
        }
    }

    public enum SyntaxToken {
        PLUS, MINUS, MULTIPLY, DIVIDE, LEFT_PARENTHESIS, RIGHT_PARENTHESIS, NUMBER
    }
}
