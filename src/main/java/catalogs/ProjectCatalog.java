package catalogs;

import org.example.models.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectCatalog {
    private Connection connection;
    private List<Project> projects = new ArrayList<Project>();

    public ProjectCatalog(Connection conn){
        this.connection = conn;
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

    public int findIdByName(String name) {
        for (Project p : projects) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p.getId();
            }
        }
        return -1;
    }

    public int createProject(String name, String description) {
        String sql = "INSERT INTO project (name, description) VALUES (?, ?)";
        int generatedId = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    projects.add(new Project(generatedId, name, description));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating project: " + e.getMessage());
        }

        return generatedId;
    }


}
