package alfatec.dao.conference;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import alfatec.dao.utils.Logging;
import alfatec.dao.utils.TableUtility;
import alfatec.model.conference.Field;
import database.Getter;
import database.DatabaseTable;

/**
 * DAO for table "field"
 * 
 * Double-checked locking in singleton.
 * 
 * @author jelena
 *
 */
public class FieldDAO {

	private static FieldDAO instance;

	public static FieldDAO getInstance() {
		if (instance == null)
			synchronized (FieldDAO.class) {
				if (instance == null)
					instance = new FieldDAO();
			}
		return instance;
	}

	private final TableUtility table;

	private Getter<Field> getField;

	private FieldDAO() {
		table = new TableUtility(new DatabaseTable("field", "field_id", new String[] { "field_name" }));
		getField = (ResultSet rs) -> {
			Field field = new Field();
			try {
				field.setFieldID(rs.getInt(table.getTable().getPrimaryKey()));
				field.setFieldName(rs.getString(table.getTable().getColumnName(1)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return field;
		};
	}

	/**
	 * Name of the field should be unique
	 */
	public Field createField(String field) {
		Field newField = table.create(new String[] { field }, new int[] {}, new long[] {}, getField);
		Logging.getInstance().change("Create", "Add new field " + field);
		return newField;
	}

	public void deleteField(Field field) {
		table.delete(field.getFieldID());
		Logging.getInstance().change("Delete", "Delete field " + field.getFieldName());
	}

	/**
	 * Full text in boolean mode by field name
	 * 
	 * @param startTyping
	 * @return all fields start with specified String
	 */
	public ObservableList<Field> findFields(String startTyping) {
		return table.searchInBooleanMode(startTyping, new String[] { table.getTable().getColumnName(1) }, getField);
	}

	public ObservableList<Field> getAllFields() {
		return table.getAll(getField);
	}

	/**
	 * Search table by primary key - id
	 */
	public Field getField(int id) {
		return table.findBy(id, getField);
	}

	/**
	 * Full text search in natural language by field name
	 * 
	 * @param field
	 * @return
	 */
	public Field getField(String field) {
		return table.findByFulltext(field, getField, 1);
	}

	public void updateField(Field field, String fieldName) {
		String past = field.getFieldName();
		table.update(field.getFieldID(), 1, fieldName);
		field.setFieldName(fieldName);
		Logging.getInstance().change("Update", "Rename field from " + past + " to " + fieldName);
	}
}
