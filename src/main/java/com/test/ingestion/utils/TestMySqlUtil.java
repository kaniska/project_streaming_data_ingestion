/**
 * 
 */
package com.test.ingestion.utils;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Kaniska_Mandal
 * http://dev.mysql.com/doc/refman/5.1/en/string-literals.html#character-escape-sequences
 * http://dev.mysql.com/doc/refman/5.1/en/reserved-words.html
 * 
Escape Sequence			Character Represented by Sequence
\0				An ASCII NUL (0x00) character.
\'				A single quote (�'�) character.
\"				A double quote (�"�) character.
\b				A backspace character.
\n				A newline (linefeed) character.
\r				A carriage return character.
\t				A tab character.
\Z				ASCII 26 (Control+Z). See note following the table.
\\				A backslash (�\�) character.
\%				A �%� character. See note following the table.
\_				A �_� character. See note following the table.

 * 
 * 
 */
public final class TestMySqlUtil {

	public static String escapeMySqlSpecialCharacters(String input) {
		return  StringEscapeUtils.escapeSql(input);
	}
	
	
}
