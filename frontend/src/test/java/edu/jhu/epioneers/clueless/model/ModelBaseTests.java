package edu.jhu.epioneers.clueless.model;
import com.google.gson.Gson;
import org.junit.Test;

/**
 * Created by Phillip on 11/2/2015.
 */
public class ModelBaseTests {

    private String name = "name";
    private int id = 3;

    private final String json = "{\"name\":\"name\",\"id\":3}";

    @Test
    public void When_serializing_Then_expected_results()
    {
        //Arrange
        Gson gson = new Gson();
        ModelBase model = new ModelBase();
        model.setId(id);
        model.setName(name);

        //Act
        String result = gson.toJson(model);

        //Assert
        assert(result.equals(json));
    }

    @Test
    public void When_deserializing_Then_expected_results()
    {
        //Arrange
        Gson gson = new Gson();
        ModelBase model = gson.fromJson(json, ModelBase.class);

        //Act
        String result = gson.toJson(model);

        //Assert
        assert(model.getId()==id && model.getName().equals(name));
    }
}
