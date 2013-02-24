package utils;

import java.sql.*;
import play.db.*;

public abstract class DaoAction<A, R> {
    public R execute(A a) {
	Connection connection = DB.getConnection();
	try {
	    connection.setAutoCommit(false);
	    R result = doSql(connection, a);
	    connection.commit();
	    return result;
	} catch (SQLException e1) {
	    e1.printStackTrace();
	    try {
		connection.rollback();
	    } catch (SQLException e2) {
		e2.printStackTrace();
	    }
	} finally {
	    try {
		connection.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return null;
    }

    public abstract R doSql(Connection connection, A a) throws SQLException;
}
