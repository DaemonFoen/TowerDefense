package org.nsu.fit.golenko_dmitriy.tdc.view;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartStylableNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import lombok.extern.log4j.Log4j2;


import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import org.nsu.fit.golenko_dmitriy.tdc.exception.WebClientException;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.GameClient;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.UserClient;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.OnFieldUpdateListener;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.Entity;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.Field;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.UserData;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.OnGameEndListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.Presenter;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
public class GameView implements AbstractView, Initializable, OnFieldUpdateListener, OnGameEndListener {
    public static final String ROOT = "guildhall";
    public static Vertex<String> ROOT_VERTEX;
    private final UserClient userClient;
    private final GameClient gameClient;
    Graph<String, String> graph;

    Map<Long, Pair<Vertex<String>, Edge<String, String>>> entitiesObj = new HashMap<>();
    Map<Long, Boolean> entities = new HashMap<>();
    private int ROAD_LENGTH;
    @FXML
    private TextField cellPromptField;
    @FXML
    private Button createTowerButton;
    @FXML
    private Button exitButton;
    @FXML
    private Text guildTowerHealth;
    @FXML
    private HBox box;

    private void createTower() {
        log.info("createTower()");
        int cellId;
        try {
            cellId = Integer.parseInt(cellPromptField.getText());
        } catch (NumberFormatException e) {
            cellPromptField.setText("");
            return;
        }
        if (cellId < 1 || cellId > ROAD_LENGTH) {
            cellPromptField.setText("");
            return;
        }
        log.info("createTower() : cellId=" + cellId);
        Thread thread = new Thread(() -> gameClient.createTower(cellId - 1));
        thread.start();
    }

    private SmartGraphPanel<String, String> graphView;

    public GameView(UserData data) {
        try {
            this.userClient = data.getUserClient();
            this.gameClient = new GameClient(data.getWebClient(), userClient);
            this.gameClient.setOnFieldUpdate(this);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new WebClientException(e);
        }
    }

    public static SmartGraphPanel<String, String> initGraphView(Graph<String, String> graph) {
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(graph, strategy);
        graphView.getStylableVertex(ROOT).setStyleClass("guildhall-alive");
        graphView.setAutomaticLayout(true);
        return graphView;
    }

    public static String getCellNameByIndex(String identifier, int index) {
        return index + "_" + identifier;
    }

    public static String getCellNameByEntity(Entity entity) {
        return "Entity_" + entity.getId();
    }

    public static String getCellNameByEntity(Long id) {
        return "Entity_" + id;
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        exitButton.setOnAction(event -> {
            gameClient.close();
            userClient.close();
            MainView.setView(ViewStage.MENU, MainView.getPresenter().getUserData());
        });
        createTowerButton.setOnAction(event -> createTower());
        Field field = gameClient.start();
        this.ROAD_LENGTH = field.getLength();
        this.graph = initGraphField(userClient.getLobby().getMembers());
        this.graphView = initGraphView(graph);
        this.graphView.setVertexPosition(ROOT_VERTEX, graphView.getScaleX() / 2, graphView.getScaleY() / 2);
        graphView.setMinWidth(500);
        graphView.setMaxHeight(500);
        box.getChildren().add(graphView);
        Platform.runLater(graphView::init);
    }

    @Override
    public void updated(Field data) {
        if (data.getGuildhall().getHealth() <= 0) {
            graphView.getStylableVertex(ROOT).setStyleClass("guildhall-dead");
        }
        guildTowerHealth.setText(String.valueOf(data.getGuildhall().getHealth()));
        data.getRoads().forEach((identifier, road) -> {
            road.getEntities().forEach(it -> {
                entities.put(it.getId(), true);
                if (!entitiesObj.containsKey(it.getId())) {
                    createEntity(identifier, it);
                    return;
                }
                updateEntity(identifier, it);
            });
        });
        entities.entrySet().stream().filter(t -> !t.getValue()).forEach(t -> removeEntity(t.getKey()));
        entities = entities.entrySet().stream().filter(Map.Entry::getValue).collect(Collectors.toMap(Map.Entry::getKey, t -> false));
        graphView.update();
    }

    private void createEntity(String identifier, Entity entity) {
        String start = getCellNameByEntity(entity);
        Vertex<String> vertexOr = insertVertex(start);
        String end = getCellNameByIndex(identifier, entity.getCell());
        Edge<String, String> edge = graph.insertEdge(end, start, "Entity_" + start);
        entitiesObj.put(entity.getId(), new Pair<>(vertexOr, edge));
        graphView.updateAndWait();
        SmartStylableNode vertex = graphView.getStylableVertex(start);
        if (entity.getName().equals("default_enemy")) {
            vertex.setStyleClass("default-enemy");
        } else if (entity.getName().equals("default_tower")) {
            vertex.setStyleClass("default-tower");
        }
    }

    private void updateEntity(String identifier, Entity entity) {
        log.info("updateEntity(road=%s, entity=%s)".formatted(identifier, entity));
        String start = getCellNameByEntity(entity);
        graph.removeEdge(entitiesObj.get(entity.getId()).getValue());
        String end = getCellNameByIndex(identifier, entity.getCell());
        Edge<String, String> edge = graph.insertEdge(start, end, "Entity_" + start);
        entitiesObj.put(entity.getId(), new Pair<>(entitiesObj.get(entity.getId()).getKey(), edge));
    }

    private void removeEntity(Long id) {
        log.info("removeEntity(entity.getId()=%s)".formatted(id));
        graph.removeEdge(entitiesObj.get(id).getValue());
        graph.removeVertex(entitiesObj.get(id).getKey());
        entitiesObj.remove(id);
    }

    @Override
    public void end() {
        graphView.getStylableVertex(ROOT).setStyleClass("guildhall-dead");
        graphView.update();
    }

    private void appendRoad(Graph<String, String> graph, String identifier) {
        String lastVertex = ROOT;
        for (int i = 0; i < ROAD_LENGTH; ++i) {
            String newVertex = getCellNameByIndex(identifier, i);
            graph.insertVertex(newVertex);
            graph.insertEdge(lastVertex, newVertex, newVertex);
            lastVertex = newVertex;
        }
    }

    private Graph<String, String> initGraphField(List<String> members) {
        Graph<String, String> graph = new GraphEdgeList<>();
        ROOT_VERTEX = graph.insertVertex(ROOT);
        members.forEach(identifier -> appendRoad(graph, identifier));
        return graph;
    }

    public Vertex<String> insertVertex(String name) {
        return graph.insertVertex(name);
    }

}