package catalogs;

import org.example.models.Tag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TagCatalog {

    private List<Tag> tags = new ArrayList<>();

    public TagCatalog(Connection conn){
        String query = "SELECT * FROM tag";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Tag tag = new Tag(
                        rs.getInt("id"),
                        rs.getString("keyword")
                );

                tags.add(tag);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
