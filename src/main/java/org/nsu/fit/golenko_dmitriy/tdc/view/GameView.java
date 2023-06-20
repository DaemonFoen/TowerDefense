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
import org.nsu.fit.golenko_dmitriy.tdc.presenter.ActionListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO.EntityObject;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
public class GameView implements AbstractView, Initializable, ActionListener {

    private static final String MAIN_TOWER = "mainTower";
    private static Vertex<String> MAIN_TOWER_VERTEX;
    // CR: List<EntityObject>?
    Graph<String, String> graph;
    Map<Long, Pair<Vertex<String>, Edge<String, String>>> entitiesObj = new HashMap<>();
    Map<Long, Boolean> entities = new HashMap<>();
    private int roadLength;
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

    private SmartGraphPanel<String, String> graphView;

    public GameView() {
        MainView.getPresenter().setActionListener(this);
    }

    private void createTower() {
        log.info("createTower()");
        int cellId;
        try {
            cellId = Integer.parseInt(cellPromptField.getText());
        } catch (NumberFormatException e) {
            cellPromptField.setText("");
            return;
        }
        if (cellId < 1 || cellId > roadLength) {
            cellPromptField.setText("");
            return;
        }
        Thread thread = new Thread(() -> MainView.getPresenter().createTower(cellId - 1));
        thread.start();
    }

    public static String getCellNameByIndex(int index) {
        return index + "_";
    }

    public static String getCellNameByEntity(EntityObject entity) {
        return "Entity_" + entity.id();
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        exitButton.setOnAction(event -> {
            MainView.getPresenter().getUserData().setScore(Integer.parseInt(score.getText()));
            MainView.getPresenter().end();
            MainView.setView(ViewStage.MENU);
        });
        createTowerButton.setOnAction(event -> createTower());
        Thread game = new Thread(() -> MainView.getPresenter().start());
        this.roadLength = Configuration.getInstance().roadLength();
        this.graph = initGraphRoad();
        this.graphView = initGraphView(graph);
        this.graphView.setVertexPosition(MAIN_TOWER_VERTEX, graphView.getScaleX() / 2, graphView.getScaleY() / 2);
        graphView.setMinWidth(700);
        graphView.setMaxHeight(700);
        box.getChildren().add(graphView);
        log.info("Game initialize");
        game.start();
        Platform.runLater(graphView::init);
    }

    @Override
    public void end() {
        MainView.getPresenter().getUserData().setScore(Integer.parseInt(score.getText()));
        graphView.getStylableVertex(MAIN_TOWER).setStyleClass("mainTower-dead");
    }

    @Override
    public void update(GameDTO data) {
        score.setText(String.valueOf(data.defeatedEnemy() * 5));
        guildTowerHealth.setText(String.valueOf(data.mainTowerHealth()));
        data.entityObjects().forEach(it -> {
            entities.put(it.id(), true);
            if (!entitiesObj.containsKey(it.id())) {
                createEntity(it);
                return;
            }
            moveEntity(it);
        });
        entities.entrySet().stream().filter(t -> !t.getValue()).forEach(t -> removeEntity(t.getKey()));
        entities = entities.entrySet().stream().filter(Map.Entry::getValue)
                .collect(Collectors.toMap(Map.Entry::getKey, t -> false));
        graphView.update();
    }

    private void createEntity(EntityObject entity) {
        String start = getCellNameByEntity(entity);
        Vertex<String> vertexOr = insertVertex(start);
        String end = getCellNameByIndex(entity.cell());
        Edge<String, String> edge = graph.insertEdge(end, start, "Entity_" + start);
        entitiesObj.put(entity.id(), new Pair<>(vertexOr, edge));
        graphView.updateAndWait();
        SmartStylableNode vertex = graphView.getStylableVertex(start);
        if (entity.name().equals("default_enemy")) {
            vertex.setStyleClass("default-enemy");
        } else if (entity.name().equals("default_tower")) {
            vertex.setStyleClass("default-tower");
        }
    }

    private void moveEntity(EntityObject entity) {
        String start = getCellNameByEntity(entity);
        graph.removeEdge(entitiesObj.get(entity.id()).getValue());
        String end = getCellNameByIndex(entity.cell());
        Edge<String, String> edge = graph.insertEdge(start, end, "Entity_" + start);
        entitiesObj.put(entity.id(), new Pair<>(entitiesObj.get(entity.id()).getKey(), edge));
    }

    private void removeEntity(Long id) {
        log.info("removeEntity(entity.getId()=%s)".formatted(id));
        graph.removeEdge(entitiesObj.get(id).getValue());
        graph.removeVertex(entitiesObj.get(id).getKey());
        entitiesObj.remove(id);
    }

    public static SmartGraphPanel<String, String> initGraphView(Graph<String, String> graph) {
        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(graph, strategy);
        graphView.getStylableVertex(MAIN_TOWER).setStyleClass("mainTower-alive");
        graphView.setAutomaticLayout(true);
        return graphView;
    }

    private Graph<String, String> initGraphRoad() {
        Graph<String, String> graph = new GraphEdgeList<>();
        MAIN_TOWER_VERTEX = graph.insertVertex(MAIN_TOWER);
        createRoad(graph);
        return graph;
    }

    private void createRoad(Graph<String, String> graph) {
        String lastVertex = MAIN_TOWER;
        for (int i = 0; i < roadLength; ++i) {
            String newVertex = getCellNameByIndex(i);
            graph.insertVertex(newVertex);
            graph.insertEdge(lastVertex, newVertex, newVertex);
            lastVertex = newVertex;
        }
    }

    public Vertex<String> insertVertex(String name) {
        return graph.insertVertex(name);
    }

}