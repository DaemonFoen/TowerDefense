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
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import org.nsu.fit.golenko_dmitriy.tdc.model.Entity;
import org.nsu.fit.golenko_dmitriy.tdc.model.GameData;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.UpdateListener;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
public class GameView implements AbstractView, Initializable, UpdateListener {
    public static final String ROOT = "guildhall";
    public static Vertex<String> ROOT_VERTEX;
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
    private Text score;
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
        Thread thread = new Thread(() -> MainView.getPresenter().createTower(cellId - 1));
        thread.start();
    }

    private SmartGraphPanel<String, String> graphView;

    public GameView() {
        MainView.getPresenter().setUpdateListener(this);
    }

    public static SmartGraphPanel<String, String> initGraphView(Graph<String, String> graph) {
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(graph, strategy);
        graphView.getStylableVertex(ROOT).setStyleClass("guildhall-alive");
        graphView.setAutomaticLayout(true);
        return graphView;
    }

    public static String getCellNameByIndex(int index) {
        return index + "_";
    }

    public static String getCellNameByEntity(Entity entity) {
        return "Entity_" + entity.getId();
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        exitButton.setOnAction(event -> {
            MainView.getPresenter().end(Integer.parseInt(score.getText()));
            MainView.setView(ViewStage.MENU);
        });
        createTowerButton.setOnAction(event -> createTower());
        Thread game = new Thread(() -> MainView.getPresenter().start());
        this.ROAD_LENGTH = MainView.getPresenter().getRoadLen();
        this.graph = initGraphField();
        this.graphView = initGraphView(graph);
        this.graphView.setVertexPosition(ROOT_VERTEX, graphView.getScaleX() / 2, graphView.getScaleY() / 2);
        graphView.setMinWidth(700);
        graphView.setMaxHeight(700);
        box.getChildren().add(graphView);
        log.info("Game initialize");
        game.start();
        Platform.runLater(graphView::init);
    }

    @Override
    public void update(GameData data) {
        if (data.mainTower().getHealth() <= 0) {
            graphView.getStylableVertex(ROOT).setStyleClass("guildhall-dead");
        }
        score.setText(String.valueOf(data.DefeatedEnemy()*5));
        guildTowerHealth.setText(String.valueOf(data.mainTower().getHealth()));
        data.road().getEntities().forEach(it -> {
                entities.put(it.getId(), true);
                if (!entitiesObj.containsKey(it.getId())) {
                    createEntity(it);
                    return;
                }
                updateEntity(it);
            });
        entities.entrySet().stream().filter(t -> !t.getValue()).forEach(t -> removeEntity(t.getKey()));
        entities = entities.entrySet().stream().filter(Map.Entry::getValue).collect(Collectors.toMap(Map.Entry::getKey, t -> false));
        graphView.update();
    }

    private void createEntity(Entity entity) {
        String start = getCellNameByEntity(entity);
        Vertex<String> vertexOr = insertVertex(start);
        String end = getCellNameByIndex(entity.getCell());
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

    private void updateEntity(Entity entity) {
        String start = getCellNameByEntity(entity);
        graph.removeEdge(entitiesObj.get(entity.getId()).getValue());
        String end = getCellNameByIndex(entity.getCell());
        Edge<String, String> edge = graph.insertEdge(start, end, "Entity_" + start);
        entitiesObj.put(entity.getId(), new Pair<>(entitiesObj.get(entity.getId()).getKey(), edge));
    }

    private void removeEntity(Long id) {
        log.info("removeEntity(entity.getId()=%s)".formatted(id));
        graph.removeEdge(entitiesObj.get(id).getValue());
        graph.removeVertex(entitiesObj.get(id).getKey());
        entitiesObj.remove(id);
    }


    private void appendRoad(Graph<String, String> graph) {
        String lastVertex = ROOT;
        for (int i = 0; i < ROAD_LENGTH; ++i) {
            String newVertex = getCellNameByIndex(i);
            graph.insertVertex(newVertex);
            graph.insertEdge(lastVertex, newVertex, newVertex);
            lastVertex = newVertex;
        }
    }

    private Graph<String, String> initGraphField() {
        Graph<String, String> graph = new GraphEdgeList<>();
        ROOT_VERTEX = graph.insertVertex(ROOT);
        appendRoad(graph);
        return graph;
    }

    public Vertex<String> insertVertex(String name) {
        return graph.insertVertex(name);
    }

}