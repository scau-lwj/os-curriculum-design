package view.processManagement;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.CPU;
import model.PCB;

public class  ProcessManagementController {
    @FXML
    private Label systemTime;

    @FXML
    private Label processId;

    @FXML
    private Label implementingCommand;

    @FXML
    private Label implementResolve;

    @FXML
    private Label remainTimePart;

    @FXML
    private ListView readyListView;

    @FXML
    private ListView blockingListView;

    private static ObservableList<String> readyList = FXCollections.observableArrayList();

    private static ObservableList<String> blockingList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // 设置列表视图的列表项
        readyListView.setItems(readyList);
        blockingListView.setItems(blockingList);



        processId.setText("0000");
        implementingCommand.setText("add x,y");
        implementResolve.setText("4");
        remainTimePart.setText("2");
    }

    /**
     * 更新界面数据
     * @param pcb
     */
    public void updateData(PCB pcb) {
        systemTime.setText(CPU.getSystemTime() + "");

        implementResolve.setText(pcb.getIntermediateResult());

        implementingCommand.setText(pcb.getCurrentInstruction());

        processId.setText(pcb.getProcessID());

        remainTimePart.setText(pcb.getRestTime() + "");

        readyList.clear();
        for (PCB readyPCB : PCB.getReadyProcessPCBList()) {
            readyList.add(readyPCB.getProcessID());
        }

        blockingList.clear();
        for (PCB blockingPCB : PCB.getBlockedProcessPCBList()) {
            blockingList.add(blockingPCB.getProcessID());
        }
    }

}