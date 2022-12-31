### Orcale DCN

오라클의 권한도 설정이 되어 있어야 한다.

```java
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by yoonsm@daou.co.kr on 2022-12-15
 */
public class OracleDCN {
    static final String USERNAME = "xxx";
    static final String PASSWORD = "xxx";
    
    static String URL = "jdbc:oracle:thin:@123.2.134.XX:1521:ABC";

    static String SELECT_FROM_NOTIFICATIONS = "select * from ddt_xxxx_info";

    public static void main(String[] args) {
        OracleDCN oracleDCN = new OracleDCN();
        try {
            oracleDCN.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void run() throws Exception {
        OracleConnection conn = connect();
        Properties props = new Properties();
        props.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
        DatabaseChangeRegistration dcr = conn.registerDatabaseChangeNotification(props);
        DCNListener listener = new DCNListener();

        System.out.println("system patrol start!!");

        try {
            dcr = conn.registerDatabaseChangeNotification(props);
            Statement statement = conn.createStatement();
            ((OracleStatement) statement).setDatabaseChangeRegistration(dcr);

            statement.executeQuery(SELECT_FROM_NOTIFICATIONS).close();
            dcr.addListener(listener);

            String[] tableNames = dcr.getTables();
            Arrays.stream(tableNames)
                    .forEach(i -> System.out.println("Table {}" + " registered. :" + i));

            listener.setDatabaseChangeRegistration(dcr);

        } catch (SQLException ex) {
            if (conn != null) {
                conn.unregisterDatabaseChangeNotification(dcr);
                conn.close();
            }
            throw ex;
        }
    }

    OracleConnection connect() throws SQLException {
        OracleDriver dr = new OracleDriver();
        Properties prop = new Properties();
        prop.setProperty("user", OracleDCN.USERNAME);
        prop.setProperty("password", OracleDCN.PASSWORD);
        return (OracleConnection) dr.connect(OracleDCN.URL, prop);
    }

    class DCNListener implements DatabaseChangeListener {

        private DatabaseChangeRegistration databaseChangeRegistration;

        public void setDatabaseChangeRegistration(DatabaseChangeRegistration databaseChangeRegistration) {
            this.databaseChangeRegistration = databaseChangeRegistration;
        }

        public void onDatabaseChangeNotification(DatabaseChangeEvent databaseChangeEvent) {
            if (databaseChangeEvent.getRegId() == databaseChangeRegistration.getRegId()) {
                System.out.println(("Database change event received for table = " + databaseChangeEvent.getDatabaseName()));
                QueryChangeDescription[] queryChanges = databaseChangeEvent.getQueryChangeDescription();
                if (queryChanges != null) {

                    for (QueryChangeDescription queryChange : queryChanges) {

                        TableChangeDescription[] tcds = queryChange.getTableChangeDescription();
                        for (TableChangeDescription tableChange : tcds) {
                            RowChangeDescription[] rcds = tableChange.getRowChangeDescription();
                            for (RowChangeDescription rcd : rcds) {
                                System.out.println("Registration information changed with rowid {} and type operation {}" + rcd.getRowid() + " - " + rcd.getRowOperation().name());
                            }

                        }
                    }
                }
            }
        }
    }
}

```