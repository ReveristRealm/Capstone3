package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        ArrayList<Category> allCat = new ArrayList<>();
        String query = "SELECT * FROM categories;";

        try(Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet results = stmt.executeQuery();
        ){
            while(results.next()){
                int category_id = results.getInt("category_id");
                String name = results.getString("name");
                String description = results.getString("description");
                Category category = new Category(category_id,name,description);
                allCat.add(category);
            }

        }catch(Exception e){
            System.out.println("hi");
        }
        return allCat;
    }

    @Override
    public Category getById(int categoryId)
    {
        Category category = null;
       String query = "SELECT * FROM categories WHERE category_id = ?;";
       try(Connection connection = getConnection();
           PreparedStatement stmt = connection.prepareStatement(query);
       ){
           stmt.setInt(1,categoryId);

           try(ResultSet results = stmt.executeQuery()){
               if (results.next()){
                  category = mapRow(results);
               }
           }

       }catch(Exception e){
           System.out.println("hi");
       }
        return category;
    }

    @Override
    public Category create(Category category)
    {
        String query = "INSERT INTO categories (category_id,name,description) VALUES (?,?,?);";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, category.getCategoryId());
            stmt.setString(2, category.getName());
            stmt.setString(3, category.getDescription());

            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Creating category failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int key = generatedKeys.getInt(1);
                    category.setCategoryId(key);
                } else {
                    throw new SQLException("Creating category failed.");
                }

            }
            } catch (Exception e) {
                System.out.println("Whatever you did, did not work");
            }

            return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {
       String query = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
        ){
            stmt.setString(1,category.getName());
            stmt.setString(2,category.getDescription());
            stmt.setInt(3,category.getCategoryId());

            stmt.executeUpdate();
        }catch(Exception e){
            System.out.println("Whatever you did, did not work");
        }
    }

    @Override
    public void delete(int categoryId)
    {
       String query = "DELETE FROM categories WHERE category_id = ?";
       try (Connection connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
       ){

        stmt.setInt(1,categoryId);

        stmt.executeUpdate();
       }catch(Exception e){
           System.out.println("Whatever you did, didn't work.");
        }
    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
