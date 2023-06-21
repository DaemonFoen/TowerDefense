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
import org.nsu.fit.golenko_dmitriy.tdc.presenter.ActionListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO.EntityObject;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.GameDTO.EntityType;
import org.nsu.fit.golenko_dmitriy.tdc.utils.Configuration;
import org.nsu.fit.golenko_dmitriy.tdc.view.MainView.ViewStage;

@Log4j2
public class GameView implements AbstractView, Initializable, ActionListener {

    private static final String MAIN_TOWER = "mainTower";
    Graph<String, String> graph;
    Map<Long, Pair<Vertex<String>, Edge<String, String>>> entitiesObj = new HashMap<>();
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


    @Override
    public void initialize(URL location, ResourceBundle resource) {
        exitButton.setOnAction(event -> {
            MainView.getPresenter().end(Integer.parseInt(score.getText()));
            MainView.setView(ViewStage.MENU);
        });
        createTowerButton.setOnAction(event -> createTower());
        Thread game = new Thread(() -> MainView.getPresenter().start());
        this.roadLength = Configuration.getInstance().roadLength();
        this.graph = initGraphRoad();
        this.graphView = initGraphView(graph);
        graphView.setMinWidth(700);
        graphView.setMaxHeight(700);
        box.getChildren().add(graphView);
        log.info("Game initialize");
        game.start();
        Platform.runLater(graphView::init);
    }

    @Override
    public void end(int score) {
        this.score.setText(String.valueOf(score));
        graphView.getStylableVertex(MAIN_TOWER).setStyleClass("mainTower-dead");
    }

    @Override
    public void update(GameDTO data) {
        score.setText(String.valueOf(data.defeatedEnemy() * 5));
        guildTowerHealth.setText(String.valueOf(data.mainTowerHealth()));
        data.entityObjects().stream().filter(it -> entitiesObj.containsKey(it.id())).forEach(it -> removeEntity(it.id()));
        data.entityObjects().forEach(it -> {
            if (!entitiesObj.containsKey(it.id())) {
                createEntity(it);
                return;
            }
            moveEntity(it);
        });
        graphView.update();
    }

    private void createEntity(EntityObject entity) {
        String start = getVertexNameByEntity(entity);
        Vertex<String> entityVertex = graph.insertVertex(start);
        String end = getVertexNameByIndex(entity.cell());
        Edge<String, String> edge = graph.insertEdge(end, start, "Entity_" + start);
        entitiesObj.put(entity.id(), new Pair<>(entityVertex, edge));
        graphView.updateAndWait();
        SmartStylableNode vertex = graphView.getStylableVertex(start);
        if (entity.type().equals(EntityType.ENEMY)) {
            vertex.setStyleClass("default-enemy");
        } else if (entity.type().equals(EntityType.ALLY)) {
            vertex.setStyleClass("default-tower");
        }
    }

    private void moveEntity(EntityObject entity) {
        String start = getVertexNameByEntity(entity);
        graph.removeEdge(entitiesObj.get(entity.id()).getValue());
        String end = getVertexNameByIndex(entity.cell());
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
        graph.insertVertex(MAIN_TOWER);
        createRoad(graph);
        return graph;
    }

    private void createRoad(Graph<String, String> graph) {
        String lastVertex = MAIN_TOWER;
        for (int i = 0; i < roadLength; ++i) {
            String newVertex = getVertexNameByIndex(i);
            graph.insertVertex(newVertex);
            graph.insertEdge(lastVertex, newVertex, newVertex);
            lastVertex = newVertex;
        }
    }

    public static String getVertexNameByIndex(int index) {
        return index + "_";
    }

    public static String getVertexNameByEntity(EntityObject entity) {
        return "Entity_" + entity.id();
    }
}