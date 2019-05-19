package smartspace.dao.rdb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import smartspace.dao.ElementDao;
import smartspace.dao.EnhancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class RdbElementDaoTest {

    private ElementDao<String> elementDao;

    private EnhancedElementDao<String> enhancedDao;

    private EntityFactory factory;

    @Autowired
    public void setElementDao(ElementDao<String> elementDao) {
        this.elementDao = elementDao;
    }

    @Autowired
    public void setFactory(EntityFactory factory) {
        this.factory = factory;
    }

    @Autowired
    public void setEnhancedDao(EnhancedElementDao<String> enhancedDao) {
        this.enhancedDao = enhancedDao;
    }

    @Before
    public void setUp() {

        elementDao.deleteAll();
    }

    @After
    public void tearDown(){
        this.elementDao.deleteAll();
    }

    @Test
    public void testCreate() {
        // GIVEN nothing

        // WHEN  we try to  create one element entity
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // THEN the element is created Checking if id was generated and set to the elementEntity.
        assertNotNull("Id is null", elementEntity.getElementId());
    }

    @Test
    public void testReadById() {
        //  GIVEN the database contains one element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read an element by id
        Optional<ElementEntity> result = elementDao.readById(elementEntity.getKey());

        // THEN the element we read is the element we created
        assertTrue("Row was not created/found.", result.isPresent());
        assertEquals(elementEntity.getKey(),result.get().getKey());
    }


    @Test
    public void testReadAll() {
        //GIVEN the database contain one element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN Read all the rows
        List<ElementEntity> elementEntities = elementDao.readAll();

        // THEN the list contains only one element and it is the one we created
        assertEquals("Wrong row number",1,elementEntities.size());
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity);
    }


    @Test
    public void testReadAllThreeRows() {
        // GIVEN the database contains three elements
        ElementEntity elementEntity1 = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        ElementEntity elementEntity2 = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        ElementEntity elementEntity3 = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN Read all rows
        List<ElementEntity> elementEntities = elementDao.readAll();

        // THEN the list contains three elements and al of them are the same as above
        assertEquals("Wrong row number", 3, elementEntities.size());
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity1);
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity2);
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity3);
    }


    @Test(expected = Throwable.class)
    public void testUpdateIllegalRow() {
        // GIVEN the database contains one element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we update the id for a wrong id.
        elementEntity.setKey(elementEntity.getElementId()+1+"#"+elementEntity.getElementSmartspace());
        elementDao.update(elementEntity);

        // THEN exception is Expected
    }

    @Test
    public void testUpdate() {
        //GIVEN the database contain one row
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we update row Name
        elementEntity.setName("Name");
        elementDao.update(elementEntity);

        // THEN the row name has changed
        Optional<ElementEntity> elementEntity1 = elementDao.readById(elementEntity.getKey());
        elementEntity1.ifPresent(elementEntity2 -> assertEquals("Name field was not updated", elementEntity.getName(), elementEntity2.getName()));
    }

    @Test
    public void testDeleteByKey() {
        //GIVEN the database contains one element
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we delete row by key
        elementDao.deleteByKey(elementEntity.getKey());

        // THEN the database does not contain that element
        assertFalse("Failed deleting elementEntity by id", elementDao.readById(elementEntity.getElementId()).isPresent());
    }

    @Test
    public void testDelete() {
        // GIVEN the database contain one row
        ElementEntity elementEntity = elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we delete entity
        elementDao.delete(elementEntity);

        // THEN the entity does not exist in the database
        assertFalse("Failed deleting elementEntity by object", elementDao.readById(elementEntity.getElementId()).isPresent());
    }

    @Test
    public void testDeleteAll() {
        //GIVEN the database contains three elements
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we delete all the rows
        elementDao.deleteAll();

        // THEN the database is empty
        assertEquals("Failed deleting all", 0, elementDao.readAll().size());
    }

    @Test
    public void saveEntity(){
        // GIVEN nothing

        // WHEN we save entity in the database
        ElementEntity elementEntity = factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>());
        elementEntity.setKey("1#2019B.zuru");
        enhancedDao.upsert(elementEntity);

        // THEN the database contains one row which is that element
        List<ElementEntity> elementEntities = enhancedDao.readAll();
        assertEquals("Wrong row number",1,elementEntities.size());
        assertThat(elementEntities).usingElementComparatorOnFields("key").contains(elementEntity);
    }

    @Test
    public void testReadAllUsingPaginationAndSortingByKey (){
        // GIVEN the database contains 5 elements
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read up to 5 elements from the beginning sorted by key
        List<ElementEntity> actual = this.enhancedDao.readAll(5, 0, "key");

        // THEN we receive the first 5 elements
        assertThat(actual)
                .usingElementComparatorOnFields("key")
                .containsExactlyElementsOf(
                        actual
                                .stream()
                                .sorted(Comparator.comparing(ElementEntity::getKey))
                                .limit(5)
                                .collect(Collectors.toList()));
    }

    @Test
    public void testReadAllUsingPagination (){
        // GIVEN the database contains 5 elements
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        elementDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read up to 5 elements from the beginning
        List<ElementEntity> actual = this.enhancedDao.readAll(5, 0);

        // THEN we receive 5 elements exactly
        assertThat(actual)
                .hasSize(5);
    }

    @Test
    public void testReadAllByTypeUsingPaginationPageZeroSizeFive (){
        // GIVEN the database contains 1 elements with type scooter
        String type = "scooter";
        enhancedDao.create(factory.createNewElement("name",type,new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read up to 1 element by type from the beginning
        List<ElementEntity> actual = this.enhancedDao.getAllElementsByType(5, 0, type,"creationTimestamp");

        // THEN we receive 1 elements exactly
        assertThat(actual)
                .hasSize(1);
    }

    @Test
    public void testReadAllByTypeUsingPaginationPageZeroSize2 (){
        // GIVEN the database contains 2 elements with type scooter and one with another type
        String type = "scooter";
        ElementEntity entity1 = enhancedDao.create(factory.createNewElement("name",type,new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        ElementEntity entity2 = enhancedDao.create(factory.createNewElement("name",type,new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        enhancedDao.create(factory.createNewElement("name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read up to 3 element by type from the beginning
        List<ElementEntity> actual = this.enhancedDao.getAllElementsByType(3, 0, type,"creationTimestamp");

        // THEN we receive 2 elements exactly
        assertThat(actual)
                .hasSize(2);
        assertThat(actual).usingElementComparatorOnFields("key").contains(entity1);
        assertThat(actual).usingElementComparatorOnFields("key").contains(entity2);
    }

    @Test
    public void testReadAllByNameUsingPaginationPageZeroSizeFive (){
        // GIVEN the database contains 1 elements with type scooter
        String name = "name";
        enhancedDao.create(factory.createNewElement(name,null,new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read up to 1 element by type from the beginning
        List<ElementEntity> actual = this.enhancedDao.getAllElementsByName(5, 0, name,"creationTimestamp");

        // THEN we receive 1 elements exactly
        assertThat(actual)
                .hasSize(1);
    }

    @Test
    public void testReadAllByNameUsingPaginationPageZeroSize2 (){
        // GIVEN the database contains 2 elements with name name and one with another type
        String name = "name";
        ElementEntity entity1 = enhancedDao.create(factory.createNewElement(name,null,new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        ElementEntity entity2 = enhancedDao.create(factory.createNewElement(name,null,new Location(5,4),new Date(),null,null,false,new HashMap<>()));
        enhancedDao.create(factory.createNewElement("Not name","type",new Location(5,4),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read up to 3 element by type from the beginning
        List<ElementEntity> actual = this.enhancedDao.getAllElementsByName(3, 0, name,"creationTimestamp");

        // THEN we receive 2 elements exactly
        assertThat(actual)
                .hasSize(2);
        assertThat(actual).usingElementComparatorOnFields("key").contains(entity1);
        assertThat(actual).usingElementComparatorOnFields("key").contains(entity2);
    }

    @Test
    public void testGetAllElementsByLocation() {
        // GIVEN the database contains 3 elements within the specified location "rectangle"
        ElementEntity entity1 = enhancedDao.create(factory.createNewElement("mark1",null,new Location(50,50),new Date(),null,null,false,new HashMap<>()));
        ElementEntity entity2 = enhancedDao.create(factory.createNewElement("mark2",null,new Location(45,45),new Date(),null,null,false,new HashMap<>()));
        ElementEntity entity3 = enhancedDao.create(factory.createNewElement("mark3","type",new Location(40,40),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read all elements by location nearby to those 3
        List<ElementEntity> actual = this.enhancedDao.getAllElementsByLocation(3, 0, 46, 46, 10, "creationTimestamp");

        // THEN we receive the 3 elements since they are actually nearby the specified "rectangle" radius.
        assertThat(actual).hasSize(3);
        assertThat(actual).usingElementComparatorOnFields("key").contains(entity1);
        assertThat(actual).usingElementComparatorOnFields("key").contains(entity2);
        assertThat(actual).usingElementComparatorOnFields("key").contains(entity3);
    }

    @Test
    public void testGetAllElementsByLocation2() {
        // GIVEN the database contains 3 elements within the specified location "rectangle"
        enhancedDao.create(factory.createNewElement("mark1",null,new Location(50,50),new Date(),null,null,false,new HashMap<>()));
        enhancedDao.create(factory.createNewElement("mark2",null,new Location(45,45),new Date(),null,null,false,new HashMap<>()));
        enhancedDao.create(factory.createNewElement("mark3","type",new Location(40,40),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read all elements by location which are not near the specified "rectangle" radius.
        List<ElementEntity> actual = this.enhancedDao.getAllElementsByLocation(3, 0, 70, 70, 10, "creationTimestamp");

        // THEN we receive an empty list.
        assertThat(actual).hasSize(0);
    }

    @Test
    public void testGetAllElementsByLocation3() {
        // GIVEN the database contains 3 elements within the specified location "rectangle"
        ElementEntity entity1 = enhancedDao.create(factory.createNewElement("mark1",null,new Location(50,50),new Date(),null,null,false,new HashMap<>()));
        enhancedDao.create(factory.createNewElement("mark2",null,new Location(45,45),new Date(),null,null,false,new HashMap<>()));
        enhancedDao.create(factory.createNewElement("mark3","type",new Location(40,40),new Date(),null,null,false,new HashMap<>()));

        // WHEN we read all elements by location where only 1 of them is nearby the specified "rectangle".
        List<ElementEntity> actual = this.enhancedDao.getAllElementsByLocation(3, 0, 55, 55, 7, "creationTimestamp");

        // THEN we receive only the nearby element.
        assertThat(actual).hasSize(1);
        assertThat(actual).usingElementComparatorOnFields("key").contains(entity1);
    }
}