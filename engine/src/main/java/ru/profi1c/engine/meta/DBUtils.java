package ru.profi1c.engine.meta;

import java.util.Collection;

public class DBUtils {

    /**
     * Quotes and escapes a list of values so that they can be used in a
     * query.
     *
     * @param values The collection of attribute values that will be quoted,
     *               escaped, and included in the returned string list.
     * @return A string representation of the list of specified values, with
     * individual values properly quoted and escaped.
     */
    public static String quoteValues(Collection<String> values) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (String s : values) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(quoteValue(s));
        }

        return sb.toString();
    }

    /**
     * Quotes and escapes an attribute value by wrapping it with single quotes
     * and escaping any single quotes inside the value.
     *
     * @param value The attribute value to quote and escape.
     * @return The properly quoted and escaped attribute value, ready to be used
     * in a select query.
     */
    public static String quoteValue(String value) {
        return "'" + quoteAll(value) + "'";
    }

    /**
     * Quotes value (escaping any single and double quotes inside the value).
     * @param value  The attribute value
     * @return
     */
    public static String quoteAll(String value) {
        String s = replaceChar(value, "'", "''");
        return replaceChar(s, "\"", "\"\"");
    }

    /**
     * Quotes value (escaping any single quotes inside the value).
     *
     * @param value The attribute value
     * @return
     */
    public static String quote(String value) {
        return replaceChar(value, "'", "''");
    }

    /**
     * Quotes and escapes an attribute name by wrapping it with
     * backticks and escaping any backticks inside the name.
     *
     * @param name The attribute name to quote and escape.
     * @return The properly quoted and escaped attribute name,
     * ready to be used in a select query.
     */
    public static String quoteName(String name) {
        return "`" + replaceChar(name, "`", "``") + "`";
    }

    public static String replaceChar(String value, String termToFind, String replacementTerm) {
        StringBuilder buffer = new StringBuilder(value);

        int searchIndex = 0;
        while (searchIndex < buffer.length()) {
            searchIndex = buffer.indexOf(termToFind, searchIndex);
            if (searchIndex == -1) {
                break;
            } else {
                buffer.replace(searchIndex, searchIndex + termToFind.length(), replacementTerm);
                searchIndex += replacementTerm.length();
            }
        }

        return buffer.toString();
    }

    private DBUtils() {
    }
}
