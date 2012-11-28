package fr.proline.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public final class SQLUtils {
	
	/**
     * Execute SQL queries contained in a SQL script provided as an <code>InputStream</code> object.
     * 
     * @param conn Database <code>Connection</code> object.
     * @param in SQL script provided as an <code>InputStream</code> object.
     */
	public static void executeSQLScript( Connection conn, InputStream in) throws SQLException {
		Scanner s = new Scanner(in);
		s.useDelimiter("(;(\r)?\n)|(--\n)");
		
		Statement st = null;
		try {
			st = conn.createStatement();
			while (s.hasNext()) {
				String line = s.next();
				
				if (line.startsWith("/*!") && line.endsWith("*/")) {
					int i = line.indexOf(' ');
					line = line.substring(i + 1, line.length() - " */".length());
				}

				if (line.trim().length() > 0) {
					//System.out.println(line);
					st.executeUpdate(line);
				}
			}
		}
		finally {
			if (st != null) st.close();
		}
	}
}
