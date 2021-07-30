package io.github.railroad.editor.regex;

import java.util.regex.Pattern;

public final class JsonRegex {

	private static final String KEY_PATTERN = "[\\w-\\\".]+?(?=:)";
	private static final String VALUE_PATTERN = "(?<=:\s)\s*[\\w\\\".-]+";

	private static final String BRACE_PATTERN = "[\\{\\}]";
	private static final String BRACKET_PATTERN = "[\\[\\]]";
	private static final String LITERAL_PATTERN = "\\b(true|false)\\b";

	public static final Pattern PATTERN = Pattern
			.compile("(?<KEY>" + KEY_PATTERN + ")" + "|(?<VALUE>" + VALUE_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")"
					+ "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<LITERAL>" + LITERAL_PATTERN + ")");

	private JsonRegex() {
	}
}
