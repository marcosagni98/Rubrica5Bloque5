package bookAuthorsExample.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import bookAuthorsExample.model.Author;

@Repository
public class AuthorRepository extends BaseRepository {
	
	public void insert(Author author) {
		Connection conn = createConnection();
		
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement("INSERT INTO Authors (name, birthday) VALUES (?, ?)");
			preparedStatement.setString(1, author.getName());
			preparedStatement.setDate(2, new java.sql.Date(author.getBirthday().getTime()));
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			close(preparedStatement);
		}

		closeConnection(conn);
	}

	public List<Author> searchAll() {
		List<Author> listAuthors = new ArrayList<Author>();
		Connection conn = createConnection();
		ResultSet resultSet = null;
		
		PreparedStatement prepareStatement = null;
		try {
			prepareStatement = conn.prepareStatement("SELECT * FROM Authors");
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				Integer id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				String birthday = resultSet.getString(3); 
				
				Author author = new Author(id, name, birthday);
				listAuthors.add(author);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			close(resultSet);
			close(prepareStatement);
			closeConnection(conn);
		}

		return listAuthors;
	}
	
	public List<Author> search(Author author) {
		List<Author> listAuthors = new ArrayList<Author>();
		Connection conn = createConnection();
		ResultSet resultSet = null;
		
		PreparedStatement prepareStatement = null;
		try {
			boolean nullName = author.getName() == "";
			boolean nullBirthday = author.getBirthday().compareTo(new SimpleDateFormat("yyyy-MM-dd").parse("0001-01-01")) == 0;
			String where = "";
			if (!nullName) {
				where = where.concat("LOWER(name) LIKE ?");
			}
			if (!nullBirthday) {
				if (where.length() > 0) {
					where = where.concat(" OR ");
				}
				where = where.concat("birthday LIKE ?");
			}
			String query = "SELECT * FROM Authors";
			if (where.length() > 0) {
				query = query.concat(" WHERE ".concat(where));
			}
			prepareStatement = conn.prepareStatement(query);
			
			Integer parameterId = 1;
			if (!nullName) {
				prepareStatement.setString(parameterId, "%".concat(author.getName()).concat("%"));
				parameterId++;
			}
			if (!nullBirthday) {
				prepareStatement.setDate(parameterId, new java.sql.Date(author.getBirthday().getTime()));
			}
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				Integer id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				String birthday = resultSet.getString(3); 
				Author _author = new Author(id, name, birthday);
				listAuthors.add(_author);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			close(resultSet);
			close(prepareStatement);
			closeConnection(conn);
		}

		return listAuthors;
	}
	
	public Author search(Integer searchId) {
		Author author = null;
		ResultSet resultSet = null;
		PreparedStatement prepareStatement = null;
		Connection conn = createConnection();
		try {
			prepareStatement = conn.prepareStatement("SELECT * FROM USER WHERE id = ?");
			prepareStatement.setInt(1, searchId);
			resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				Integer id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				String birthday = resultSet.getString(3); 
				
				author = new Author(id, name, birthday);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			close(resultSet);
			close(prepareStatement);
			
		}
		closeConnection(conn);;
		return author;
	}
}