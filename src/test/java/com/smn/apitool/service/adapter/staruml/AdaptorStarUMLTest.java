package com.smn.apitool.service.adapter.staruml;

import com.smn.apitool.model.Entity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AdaptorStarUMLTest {

	private AdaptorStarUML adaptor;

	@BeforeEach
	void setUp() {
		adaptor = new AdaptorStarUML();
	}

	@Test
	void testReadUMLFile_success() {
		// Minimal valid UML JSON with one class and one attribute
		String json = """
		{
		  "ownedElements": [
			{
			  "ownedElements": [
				{
				  "_type": "UMLClass",
				  "_id": "1",
				  "name": "Person",
				  "attributes": [
					{
					  "name": "id",
					  "type": "string",
					  "defaultValue": "0",
					  "isID": true,
					  "isReadOnly": false
					}
				  ]
				}
			  ]
			}
		  ]
		}
		""";
		byte[] fileContent = json.getBytes();
		AdaptorStarUML.DtoReadUMLFile result = adaptor.readUMLFile(fileContent);

		assertNull(result.getError());
		List<Entity> entities = result.getEntities();
		assertEquals(1, entities.size());
		Entity entity = entities.get(0);
		assertEquals("Person", entity.getName());
		assertTrue(result.getIssues().isEmpty());
	}

	@Test
	void testReadUMLFile_invalidJson() {
		byte[] fileContent = "{invalid json}".getBytes();
		AdaptorStarUML.DtoReadUMLFile result = adaptor.readUMLFile(fileContent);

		assertNotNull(result.getError());
		assertTrue(result.getEntities().isEmpty());
	}

	@Test
	void testReadUMLFile_emptyContent() {
		byte[] fileContent = "".getBytes();
		AdaptorStarUML.DtoReadUMLFile result = adaptor.readUMLFile(fileContent);

		assertNotNull(result.getError());
		assertTrue(result.getEntities().isEmpty());
	}

	@Test
	void testReadUMLFile_unsupportedAttributeType() {
		String json = """
		{
		  "ownedElements": [
			{
			  "ownedElements": [
				{
				  "_type": "UMLClass",
				  "_id": "2",
				  "name": "Car",
				  "attributes": [
					{
					  "name": "engine",
					  "type": "complexType",
					  "defaultValue": "",
					  "isID": false,
					  "isReadOnly": false
					}
				  ]
				}
			  ]
			}
		  ]
		}
		""";
		byte[] fileContent = json.getBytes();
		AdaptorStarUML.DtoReadUMLFile result = adaptor.readUMLFile(fileContent);

		assertNull(result.getError());
		List<Entity> entities = result.getEntities();
		assertEquals(1, entities.size());
		Map<Entity, List<String>> issues = result.getIssues();
		assertFalse(issues.isEmpty());
		boolean foundIssue = issues.values().stream()
			.flatMap(List::stream)
			.anyMatch(msg -> msg.contains("not supported by OpenAPI"));
		assertTrue(foundIssue);
	}

	@Test
	void testReadUMLFile_missingFields() {
		// UMLClass missing 'name' and 'attributes'
		String json = """
		{
		  "ownedElements": [
			{
			  "ownedElements": [
				{
				  "_type": "UMLClass",
				  "_id": "3"
				}
			  ]
			}
		  ]
		}
		""";
		byte[] fileContent = json.getBytes();
		AdaptorStarUML.DtoReadUMLFile result = adaptor.readUMLFile(fileContent);

		assertNull(result.getError());
		List<Entity> entities = result.getEntities();
		assertEquals(1, entities.size());
		assertEquals(null, entities.get(0).getName());
	}
}