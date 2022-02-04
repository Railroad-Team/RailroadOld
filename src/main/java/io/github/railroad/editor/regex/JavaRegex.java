package io.github.railroad.editor.regex;

import java.util.regex.Pattern;

/**
 * @author TurtyWurty
 */
public final class JavaRegex {
    private static final String[] KEYWORDS = { "abstract", "assert", "boolean", "break", "byte", "case", "catch",
        "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
        "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new",
        "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
        "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while", "record",
        "sealed" };
    
    private static final String[] LITERALS = { "null", "true", "false" };
    
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String VAR_PATTERN = "\\b(var)\\b";
    private static final String LITERAL_PATTERN = "\\b(" + String.join("|", LITERALS) + ")\\b";
    private static final String CONST_VARIABLE_PATTERN = "((?<=\\s)([A-Z_][A-Z_-]*)\\b(?=;))|((?<=\\s)([A-Z_][A-Z_-]*)\\b(?=\\s\\=))|((?<=,\\s|\\()([A-Z_][A-Z_-]*)\\b(?=[\\s]*)(?=[,]+|\\)[,;]+))|((?<=(?<!instanceof)\\s)([A-Z_][A-Z_-]*)\\b(?=[,\\)])|(?<=\\s|\\(|\\)|!)([A-Z][A-Z_-]*)\\b(?=\\s*[\\.]+|\\s+\\.|\\s*\\+|\\s*\\-|\\s*\\/|\\s*\\||\\s*\\&|\\s*\\*|\\s*\\%|\\s*\\^|\\s*:|\\s*!|\\s*<|\\s*>|\\s*=|\\s*instanceof\\s*)(?!\\()|(?<=\\.)(\\s*[A-Z_][A-Z_-]*)\\b(?=[,\\.\\)]))";
    private static final String VARIABLE_PATTERN = "((?<=\\s)([a-zA-Z_][\\w-]*)\\b(?=;))|((?<=\\s)([a-zA-Z_][\\w-]*)\\b(?=\\s\\=))|((?<=,\\s|\\()([a-zA-Z_][\\w-]*)\\b(?=[\\s]*)(?=[,]+|\\)[,;]+))|((?<=(?<!instanceof)\\s)([a-zA-Z_][\\w-]*)\\b(?=[,\\)])|(?<=\\s|\\(|\\)|!)([a-z][\\w-]*)\\b(?=\\s*[\\.]+|\\s+\\.|\\s*\\+|\\s*\\-|\\s*\\/|\\s*\\||\\s*\\&|\\s*\\*|\\s*\\%|\\s*\\^|\\s*:|\\s*!|\\s*<|\\s*>|\\s*=|\\s*instanceof\\s*)(?!\\()|(?<=\\.)(\\s*[a-zA-Z_][\\w-]*)\\b(?=[,\\.\\)]))";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String DIAMOND_PATTERN = "\\<|\\>|::|-";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*\\|\\/\\*(.|\\R)*?\\*/";
    private static final String ANNOTATION_PATTERN = "\\@\\w+\\.*\\w*(?=.*)";
    private static final String METHOD_PATTERN = "(\\w+\\s*(?=\\s*\\())|(?<=::)(?!(new\\w+)*)\\w+\\s*(?=\\))|((?<=::)(?!new)\\w+\\s*(?=\\)|,))";
    private static final String GENERIC_CONSTR_PATTERN = "(?<=new)[\\s]+(?>[\\w.]+)";
    private static final String NUMBER_PATTERN = "\\b\\d*[fdlFDL]?\\.?\\d+[fdlFDL]?\\b";
    
    public static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<VAR>"
        + VAR_PATTERN + ")" + "|(?<LITERAL>" + LITERAL_PATTERN + ")" + "|(?<CONSTVAR>" + CONST_VARIABLE_PATTERN + ")"
        + "|(?<VARIABLE>" + VARIABLE_PATTERN + ")" + "|(?<PAREN>" + PAREN_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN
        + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<DIAMOND>" + DIAMOND_PATTERN + ")" + "|(?<SEMICOLON>"
        + SEMICOLON_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        + "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")" + "|(?<METHOD>" + METHOD_PATTERN + ")" + "|(?<GENERICCONSTR>"
        + GENERIC_CONSTR_PATTERN + ")" + "|(?<NUMBER>" + NUMBER_PATTERN + ")");
    
    private JavaRegex() {
    }
}
