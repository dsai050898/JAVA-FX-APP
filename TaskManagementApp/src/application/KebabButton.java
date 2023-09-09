package application;

import javafx.scene.control.Button;

public class KebabButton extends Button {
    public KebabButton(String text) {
        super(text);
        getStyleClass().add("kebab-button"); // Apply your CSS class for kebab button styling

        setOnAction(event -> {
            // Handle the filtering based on the button's text
            String buttonText = getText();
            if (buttonText.equals("Filter by Date")) {
                filterTasksByDate();
            } else if (buttonText.equals("Filter by Task")) {
                filterTasksByPriority();
            }
        });
    }

    private void filterTasksByDate() {
        // Implement the filtering logic for date filtering
        // You can use DatePicker or other UI components to take user input for filtering
        // For example:
        // DatePicker datePicker = new DatePicker();
        // LocalDate selectedDate = datePicker.getValue();
        // Filter tasks based on selectedDate
    }

    private void filterTasksByPriority() {
        // Implement the filtering logic for task priority filtering
        // You can use ComboBox or other UI components to take user input for filtering
        // For example:
        // ComboBox<Priority> priorityComboBox = new ComboBox<>(FXCollections.observableArrayList(Priority.values()));
        // Priority selectedPriority = priorityComboBox.getValue();
        // Filter tasks based on selectedPriority
    }
}
