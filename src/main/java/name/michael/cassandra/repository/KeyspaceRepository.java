package name.michael.cassandra.repository;

import com.datastax.driver.core.Session;

public class KeyspaceRepository {
    private Session session;

    public KeyspaceRepository(Session session) {
        this.session = session;
    }

    /**
     * Method used to create any keyspace - schema.
     *
     * @param keyspaceName         the name of the schema.
     * @param replicationStrategy the replication strategy.
     * @param numberOfReplicas     the number of replicas.
     */
    public void createKeyspace(String keyspaceName, String replicationStrategy, int numberOfReplicas) {
        final String query = "CREATE KEYSPACE IF NOT EXISTS " + keyspaceName +
                " WITH replication = " +
                "{" + "'class':'" + replicationStrategy + "','replication_factor':" + numberOfReplicas + "};";

        session.execute(query);
    }

    public void useKeyspace(String keyspace) {
        session.execute("USE " + keyspace);
    }

    /**
     * Method used to delete the specified schema.
     * It results in the immediate, irreversable removal of the keyspace, including all tables and data contained in the keyspace.
     *
     * @param keyspaceName the name of the keyspace to delete.
     */
    public void deleteKeyspace(String keyspaceName) {
        StringBuilder sb = new StringBuilder("DROP KEYSPACE ").append(keyspaceName);

        final String query = sb.toString();

        session.execute(query);
    }
}