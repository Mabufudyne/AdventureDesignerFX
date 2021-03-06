package org.mabufudyne.designer.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mabufudyne.designer.core.StoryPiece;

public class StoryPieceViewController extends WindowSubController {
    @FXML private Spinner<Integer> spinOrder;
    @FXML private TextField textTitle;
    @FXML private TextArea textStory;

    StoryPieceViewController(Spinner<Integer> spinOrder, TextField textTitle, TextArea textStory) {
        this.spinOrder = spinOrder;
        this.textTitle = textTitle;
        this.textStory = textStory;
    }

    public StoryPieceViewController() {}

    @Override
    public void setupListeners() {
        // WORKAROUND:
        // On the first value change and listener call (oldValue (null), newValue(1)),
        // NPE is thrown somewhere inside Java code for some unknown reason
        // Setting a temporary value factory (such that first value change = oldValue (1), newValue(1)) avoids this
        spinOrder.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,1));
        spinOrder.valueProperty().addListener((observable, oldValue, newValue) -> onOrderSpinnerValueChange(oldValue, newValue));

        // Set up StoryPieces table selection listener
        OverviewController oc = (OverviewController) mainController.getController("OverviewController");
        TableView<StoryPiece> storyPiecesTable = oc.getStoryPiecesTable();
        storyPiecesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelectedSP, newSelectedSP) ->
                onStoryPiecesTableNewSelection(oldSelectedSP, newSelectedSP));
    }

    /** Event Listeners **/

    void onStoryPiecesTableNewSelection(StoryPiece oldSP, StoryPiece newSP) {
        SpinnerValueFactory.IntegerSpinnerValueFactory spinnerRange = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1, app.getActiveAdventure().getMaxUsedOrder(), newSP.getOrder());

        spinOrder.setValueFactory(spinnerRange);

        if (oldSP != null) {
            textTitle.textProperty().unbindBidirectional(oldSP.titleProperty());
            textStory.textProperty().unbindBidirectional(oldSP.storyProperty());
        }
        textTitle.textProperty().bindBidirectional(newSP.titleProperty());
        textStory.textProperty().bindBidirectional(newSP.storyProperty());
    }

    void onOrderSpinnerValueChange(int oldOrder, int newOrder) {
        if (newOrder < 1 || newOrder > app.getActiveAdventure().getMaxUsedOrder()) {
            spinOrder.getValueFactory().setValue(oldOrder);
        } else {
            OverviewController oc = (OverviewController) mainController.getController("OverviewController");
            StoryPiece selectedSP = oc.getSelectedStoryPiece();
            app.getActiveAdventure().switchStoryPieceOrder(selectedSP, spinOrder.getValue());
        }
    }
}
