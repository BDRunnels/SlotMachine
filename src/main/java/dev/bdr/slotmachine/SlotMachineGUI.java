package dev.bdr.slotmachine;

//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//public class SlotMachineGUI extends Application {
////    private static final SlotMachine slotMachine = new SlotMachine();
//
//    private Label balanceLabel;
//    private Label resultLabel;
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Slot Machine");
//
//        // Balance label
//        balanceLabel = new Label("Balance: " /*+ slotMachine.getBalance()*/);
//
//        // Result label
//        resultLabel = new Label("");
//
//        // Spin button
//        Button spinButton = new Button("Spin");
//        spinButton.setOnAction(e -> spin());
//
//        // Reset button
//        Button resetButton = new Button("Reset");
//        resetButton.setOnAction(e -> reset());
//
//        // VBox layout
//        VBox vbox = new VBox(10);
//        vbox.setPadding(new Insets(10));
//        vbox.setAlignment(Pos.CENTER);
//        vbox.getChildren().addAll(balanceLabel, resultLabel, spinButton, resetButton);
//
//        // Scene
//        Scene scene = new Scene(vbox, 300, 200);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void spin() {
//        /*slotMachine.spin()*/;
//        updateLabels();
//    }
//
//    private void reset() {
//        /*slotMachine.reset()*/;
//        updateLabels();
//    }
//
//    private void updateLabels() {
//        balanceLabel.setText("Balance: " /*+ slotMachine.getBalance()*/);
//        resultLabel.setText("Result: " /*+ slotMachine.getResult()*/);
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class SlotMachineGUI extends Application {
    private static final String[] SYMBOLS = {"Cherry", "Lemon", "Orange", "Apple", "Banana"};
    private static final int[] PAYOUTS = {10, 20, 30, 40, 50};
    private static final int INITIAL_BALANCE = 100;
    private static final int BET_AMOUNT = 10;

    private int balance = INITIAL_BALANCE;
    private final Random random = new Random();

    private Label balanceLabel;
    private Label[] reelLabels;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Slot Machine");

        Label titleLabel = new Label("Welcome to the Slot Machine Game!");
        titleLabel.setFont(Font.font(25));
        titleLabel.setUnderline(true);

        balanceLabel = new Label("Current balance: " + balance);
        balanceLabel.setFont(Font.font(16));

        Button spinButton = new Button("Spin");
        Button addCoinsButton = new Button("Add Coins");

        spinButton.setOnAction(e -> spin());
        addCoinsButton.setOnAction(e -> addCoins());

        HBox reelBox = createReelBox();

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(titleLabel, balanceLabel, reelBox, spinButton, addCoinsButton);

        Scene scene = new Scene(vbox, 450, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set initial text color to white
        for (Label reelLabel : reelLabels) {
            reelLabel.setTextFill(Color.WHITE);
            reelLabel.setText("Good Luck!");
        }
    }

    private HBox createReelBox() {
        HBox reelBox = new HBox(10);
        reelBox.setAlignment(Pos.CENTER);
        reelLabels = new Label[3];
        for (int i = 0; i < 3; i++) {
            StackPane reel = new StackPane();
            Rectangle reelFrame = new Rectangle(75, 75);
            reelFrame.setStroke(Color.WHITE);
            reelFrame.setStrokeWidth(2);
            reelFrame.setArcWidth(10);
            reelFrame.setArcHeight(10);
            Label reelLabel = new Label();
            reelLabels[i] = reelLabel;
            reel.getChildren().addAll(reelFrame, reelLabel);
            reelBox.getChildren().add(reel);
        }
        return reelBox;
    }

    private void addCoins() {
        balance += 100;
        balanceLabel.setText("Current balance: " + balance);
    }
    private void spin() {
        reelLabels[0].setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        reelLabels[1].setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        reelLabels[2].setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        if (balance >= BET_AMOUNT) {
            balance -= BET_AMOUNT;
            balanceLabel.setText("Current balance: " + balance);

            // Animate the reels
            Timeline[] timelines = new Timeline[3];
            for (int i = 0; i < 3; i++) {
                timelines[i] = createReelAnimation(i);
                timelines[i].setCycleCount(Animation.INDEFINITE);
                timelines[i].play();
            }

            // Stop the reels after a duration
            Timeline stopTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                for (Timeline timeline : timelines) {
                    timeline.stop();
                }
                checkWin();
            }));
            stopTimeline.play();

            // Set text color to black while spinning
            for (Label reelLabel : reelLabels) {
                reelLabel.setTextFill(Color.WHITE);
            }
        } else {
            balanceLabel.setText("Not enough balance to spin. Current balance: " + balance);
        }
    }

    private Timeline createReelAnimation(int index) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> reelLabels[index].setText(SYMBOLS[random.nextInt(SYMBOLS.length)])));
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    private void checkWin() {
        String[] reelSymbols = new String[3];
        for (int i = 0; i < 3; i++) {
            reelSymbols[i] = reelLabels[i].getText();
        }

        if (reelSymbols[0].equals(reelSymbols[1]) && reelSymbols[1].equals(reelSymbols[2])) {
            int payout = PAYOUTS[getIndex(reelSymbols[0])];
            balance += payout;
            balanceLabel.setText("Congratulations! You win " + payout + " coins! Current balance: " + balance);
            for (Label reelLabel : reelLabels) {
                reelLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        } else if (reelSymbols[0].equals(reelSymbols[1]) || reelSymbols[1].equals(reelSymbols[2]) || reelSymbols[0].equals(reelSymbols[2])) {
//            balanceLabel.setText("Current balance: " + balance + "\nYou didn't win, but you got a match! ");
            for (int i = 0; i < 3; i++) {
                if (reelSymbols[i].equals(reelSymbols[(i + 1) % 3]) || reelSymbols[i].equals(reelSymbols[(i + 2) % 3])) {
                    reelLabels[i].setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                } else {
                    reelLabels[i].setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
        } else {
//            balanceLabel.setText("Sorry, you didn't win. Current balance: " + balance);
            for (Label reelLabel : reelLabels) {
                reelLabel.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }


    private int getIndex(String symbol) {
        for (int i = 0; i < SYMBOLS.length; i++) {
            if (SYMBOLS[i].equals(symbol)) {
                return i;
            }
        }
        return -1;
    }
}
