package se.fk.github.rimfrost.workflow;

import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.junit.jupiter.api.BeforeEach;
import se.fk.github.rimfrost.workflow.storage.StorageTestCleaner;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostHandlaggningProcessRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostHandlaggningProcessResponse;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeRequest;
import se.fk.rimfrost.workflow.jaxrsspec.controllers.generatedsource.model.PostYrkandeResponse;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;

public abstract class WorkflowTestBase
{
   protected final static String handlaggningResponsesChannel = "handlaggning-responses";
   protected static final String handlaggningRequestsChannel = "handlaggning-requests";
   protected static final String handlaggningDoneChannel = "handlaggning-done";

   @Inject
   @Connector("smallrye-in-memory")
   InMemoryConnector inMemoryConnector;

   @Inject
   StorageTestCleaner storageTestCleaner;

   @BeforeEach
   void setup()
   {
      var wireMockServer = WireMockTestResource.getWireMockServer();
      if (wireMockServer != null && wireMockServer.isRunning())
      {
         wireMockServer.resetToDefaultMappings();
      }

      clearChannel(handlaggningRequestsChannel);
      clearChannel(handlaggningDoneChannel);

      storageTestCleaner.clearAll();
   }

   protected void sendRestartProcessRequest(UUID handlaggningId, PostHandlaggningProcessRequest request,
         int expectedStatusCode)
   {
      given().contentType(ContentType.JSON)
            .body(request)
            .post("/handlaggning/{id}/process", handlaggningId)
            .then()
            .statusCode(expectedStatusCode);
   }

   protected PostHandlaggningProcessResponse sendRestartProcessRequest(UUID handlaggningId,
         PostHandlaggningProcessRequest request)
   {
      return given().contentType(ContentType.JSON)
            .body(request)
            .post("/handlaggning/{id}/process", handlaggningId)
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(PostHandlaggningProcessResponse.class);
   }

   protected void sendCreateYrkandeRequest(PostYrkandeRequest request, int expectedStatusCode)
   {
      given().contentType(ContentType.JSON).body(request).post("/yrkande").then().statusCode(expectedStatusCode);
   }

   protected PostYrkandeResponse sendCreateYrkandeRequest(PostYrkandeRequest request)
   {
      return given().contentType(ContentType.JSON).body(request).post("/yrkande").then().statusCode(200).extract().body()
            .as(PostYrkandeResponse.class);
   }

   protected void sendKafkaMessage(String channel, Object responseMsg)
   {
      inMemoryConnector.source(channel).send(responseMsg);
   }

   protected List<? extends Message<?>> waitForMessages(String channel)
   {
      await().atMost(5, TimeUnit.SECONDS).until(() -> !inMemoryConnector.sink(channel).received().isEmpty());
      return inMemoryConnector.sink(channel).received();
   }

   protected void clearChannel(String channel)
   {
      inMemoryConnector.sink(channel).clear();
   }
}
