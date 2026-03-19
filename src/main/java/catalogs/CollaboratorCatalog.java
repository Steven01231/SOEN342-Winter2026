package catalogs;

import org.example.models.Collaborator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CollaboratorCatalog {
    private ArrayList<Collaborator> collaborators;

    public CollaboratorCatalog(Connection conn) {

        collaborators = new ArrayList<>();

        String query = "SELECT * FROM collaborator";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Collaborator collaborator = new Collaborator(
                        rs.getInt("id"),
                        rs.getString("category"),
                        rs.getInt("task_limit"),
                        rs.getInt("project_id")
                );

                collaborators.add(collaborator);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Collaborator> getCollaborators() {
        return collaborators;
    }
}
