package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class V3__add_foreign_key_indexes extends BaseJavaMigration {

	private static final List<IndexSpec> INDEX_SPECS = List.of(
			new IndexSpec("route_stops", "idx_route_stops_stop_id", List.of("stop_id")),
			new IndexSpec("buses", "idx_buses_route_direction_id", List.of("route_direction_id")),
			new IndexSpec("buses", "idx_buses_current_stop_id", List.of("current_stop_id")),
			new IndexSpec("buses", "idx_buses_next_stop_id", List.of("next_stop_id")),
			new IndexSpec("bus_events", "idx_bus_events_bus_id", List.of("bus_id")));

	@Override
	public void migrate(Context context) throws Exception {
		Connection connection = context.getConnection();
		DatabaseKind databaseKind = DatabaseKind.from(connection.getMetaData().getDatabaseProductName());

		for (IndexSpec indexSpec : INDEX_SPECS) {
			ensureIndex(connection, databaseKind, indexSpec);
		}
	}

	private static void ensureIndex(Connection connection, DatabaseKind databaseKind, IndexSpec indexSpec)
			throws SQLException {
		Map<String, IndexDefinition> indexes = findIndexes(connection, indexSpec.table());
		String normalizedIndexName = normalize(indexSpec.name());
		if (indexes.containsKey(normalizedIndexName)) {
			return;
		}

		Optional<IndexDefinition> existingIndex = findExactIndex(indexes, indexSpec.columns());
		if (existingIndex.isPresent() && renameIndex(connection, databaseKind, indexSpec, existingIndex.get())) {
			return;
		}

		if (existingIndex.isPresent() && databaseKind == DatabaseKind.MYSQL) {
			throw new IllegalStateException("Failed to rename existing index " + existingIndex.get().actualName()
					+ " to " + indexSpec.name());
		}

		createIndex(connection, indexSpec);
	}

	private static Map<String, IndexDefinition> findIndexes(Connection connection, String tableName) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		Map<String, IndexColumns> indexColumns = new LinkedHashMap<>();
		for (String schema : schemaCandidates(connection)) {
			for (String table : tableCandidates(tableName)) {
				try (ResultSet resultSet = metaData.getIndexInfo(null, schema, table, false, false)) {
					while (resultSet.next()) {
						String indexName = resultSet.getString("INDEX_NAME");
						String columnName = resultSet.getString("COLUMN_NAME");
						short ordinalPosition = resultSet.getShort("ORDINAL_POSITION");
						if (indexName == null || columnName == null || ordinalPosition <= 0) {
							continue;
						}

						String normalizedIndexName = normalize(indexName);
						indexColumns.computeIfAbsent(normalizedIndexName, ignored -> new IndexColumns(indexName))
							.columnsByPosition()
							.put(ordinalPosition, normalize(columnName));
					}
				}
			}
		}

		Map<String, IndexDefinition> indexes = new LinkedHashMap<>();
		indexColumns.forEach((indexName, columns) -> indexes.put(indexName,
				new IndexDefinition(columns.actualName(), new ArrayList<>(columns.columnsByPosition().values()))));
		return indexes;
	}

	private static List<String> schemaCandidates(Connection connection) throws SQLException {
		List<String> candidates = new ArrayList<>();
		String schema = connection.getSchema();
		if (schema != null && !schema.isBlank()) {
			candidates.add(schema);
			candidates.add(schema.toLowerCase(Locale.ROOT));
			candidates.add(schema.toUpperCase(Locale.ROOT));
		}
		candidates.add(null);
		return candidates.stream().distinct().toList();
	}

	private static List<String> tableCandidates(String tableName) {
		return List.of(tableName, tableName.toLowerCase(Locale.ROOT), tableName.toUpperCase(Locale.ROOT))
			.stream()
			.distinct()
			.toList();
	}

	private static Optional<IndexDefinition> findExactIndex(Map<String, IndexDefinition> indexes, List<String> columns) {
		List<String> normalizedColumns = columns.stream().map(V3__add_foreign_key_indexes::normalize).toList();
		return indexes.values().stream().filter(index -> index.columns().equals(normalizedColumns)).findFirst();
	}

	private static boolean renameIndex(Connection connection, DatabaseKind databaseKind, IndexSpec indexSpec,
			IndexDefinition existingIndex) throws SQLException {
		String sql = switch (databaseKind) {
			case MYSQL -> "ALTER TABLE " + indexSpec.table() + " RENAME INDEX " + existingIndex.actualName() + " TO "
					+ indexSpec.name();
			case H2 -> "ALTER INDEX " + existingIndex.actualName() + " RENAME TO " + indexSpec.name();
			case OTHER -> null;
		};
		if (sql == null) {
			return false;
		}

		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
			return true;
		}
		catch (SQLException exception) {
			if (databaseKind == DatabaseKind.MYSQL) {
				throw exception;
			}
			return false;
		}
	}

	private static void createIndex(Connection connection, IndexSpec indexSpec) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE INDEX " + indexSpec.name() + " ON " + indexSpec.table() + " ("
					+ String.join(", ", indexSpec.columns()) + ")");
		}
	}

	private static String normalize(String value) {
		return value.toLowerCase(Locale.ROOT);
	}

	private record IndexSpec(String table, String name, List<String> columns) {
	}

	private record IndexDefinition(String actualName, List<String> columns) {
	}

	private record IndexColumns(String actualName, TreeMap<Short, String> columnsByPosition) {

		private IndexColumns(String actualName) {
			this(actualName, new TreeMap<>());
		}
	}

	private enum DatabaseKind {

		MYSQL,
		H2,
		OTHER;

		private static DatabaseKind from(String productName) {
			String normalizedProductName = normalize(productName);
			if (normalizedProductName.contains("mysql")) {
				return MYSQL;
			}
			if (normalizedProductName.contains("h2")) {
				return H2;
			}
			return OTHER;
		}
	}
}
