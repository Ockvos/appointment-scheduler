package DAO;

import java.sql.SQLException;

// Used to fetch a string from a MySQL database given a key (ID)
// See ContactsQuery.fetchContactName() for implementation
public interface FetchString {
    String fetchString(int id) throws SQLException;
}
