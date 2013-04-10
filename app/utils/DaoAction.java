package utils;

import java.sql.Connection;
import java.sql.SQLException;
import play.db.DB;

public abstract class DaoAction<A, R> {
    public static class DaoException extends Exception {
	public DaoException(Exception e) {
	    super(e);
	}
    }

    public R execute(A a) throws DaoException {
	R result = null;
	Connection connection = DB.getConnection();
	try {
	    connection.setAutoCommit(false);
	    result = doSql(connection, a);
	    connection.commit();
	} catch (SQLException e1) {
	    e1.printStackTrace();
	    try {
		connection.rollback();
	    } catch (SQLException e2) {
		e2.printStackTrace();
	    }
	    throw new DaoException(e1);
	} finally {
	    try {
		connection.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return result;
    }

    public abstract R doSql(Connection connection, A a) throws SQLException;
}
