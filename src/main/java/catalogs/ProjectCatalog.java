package catalogs;

import org.example.models.Project;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjectCatalog {

    private List<Project> projects = new ArrayList<Project>();

    public ProjectCatalog(Connection conn){

        String query = "SELECT * FROM project";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Project project = new Project(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                );

                projects.add(project);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
