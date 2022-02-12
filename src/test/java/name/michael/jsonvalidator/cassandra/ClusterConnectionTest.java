package name.michael.jsonvalidator.cassandra;

import com.datastax.driver.core.Session;
import name.michael.cassandra.CluserConnection;
import name.michael.cassandra.repository.KeyspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class ClusterConnectionTest {
    private KeyspaceRepository schemaRepository;
    private Session session;

    @BeforeEach
    public void connect() {
        CluserConnection client = new CluserConnection();
        client.connect("127.0.0.1", 9142);
        this.session = client.getSession();
        schemaRepository = new KeyspaceRepository(session);
    }

    @Test
    public void whenCreatingAKeyspace_thenCreated() {
        String keyspaceName = "library";
        schemaRepository.createKeyspace(keyspaceName, "SimpleStrategy", 1);

        ResultSet result =
                session.execute("SELECT * FROM system_schema.keyspaces;");

        List<String> matchedKeyspaces = result.all()
                .stream()
                .filter(r -> r.getString(0).equals(keyspaceName.toLowerCase()))
                .map(r -> r.getString(0))
                .collect(Collectors.toList());

        assertEquals(matchedKeyspaces.size(), 1);
        assertEquals(keyspaceName.toLowerCase(), matchedKeyspaces.get(0));
    }
}
