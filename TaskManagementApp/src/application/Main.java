package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

public class Main extends Application {
	private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private ObservableList<Task> completedTasks = FXCollections.observableArrayList();
    private ListView<Task> taskListView;
    private ListView<Task> completedTasksListView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("To Do List");

            VBox root = new VBox();
            root.getStyleClass().add("root"); // Apply root style

            root.setSpacing(10);
            root.setPadding(new Insets(10));

            Label titleLabel = new Label("To Do List");
            titleLabel.setStyle("-fx-font-size: 24px;");

            TextField taskInput = new TextField();
            taskInput.getStyleClass().add("text-field"); // Apply text-field style
            taskInput.setPromptText("Enter a task");

            ComboBox<Priority> priorityComboBox = new ComboBox<>(FXCollections.observableArrayList(Priority.values()));
            priorityComboBox.getStyleClass().add("combo-box"); // Apply combo-box style
            priorityComboBox.setPromptText("Select Priority");

            DatePicker datePicker = new DatePicker();
            datePicker.getStyleClass().add("date-picker"); // Apply date-picker style
            datePicker.setPromptText("Select Due Date");

            TextField searchInput = new TextField();
            searchInput.getStyleClass().add("text-field"); // Apply text-field style
            searchInput.setPromptText("Search tasks");
            searchInput.textProperty().addListener((observable, oldValue, newValue) -> searchTasks(newValue));

            Button addButton = new Button("Add Task");
            addButton.getStyleClass().add("button"); // Apply button style
            addButton.setOnAction(e -> addTask(taskInput.getText(), priorityComboBox.getValue(), datePicker.getValue()));

            taskListView = new ListView<>(tasks);
            taskListView.getStyleClass().add("list-view"); // Apply list-view style
            taskListView.setCellFactory(param -> new TaskListCell());

            completedTasksListView = new ListView<>(completedTasks);
            completedTasksListView.getStyleClass().add("list-view"); // Apply list-view style

            VBox completedTasksBox = new VBox();
            completedTasksBox.getStyleClass().add("root"); // Apply root style

            Label completedTitleLabel = new Label("Completed Tasks");
            completedTitleLabel.setStyle("-fx-font-size: 24px;");

            completedTasksBox.getChildren().addAll(completedTitleLabel, completedTasksListView);


            Button sortByDateButton = new Button("Sort by Date");
            Button sortByPriorityButton = new Button("Sort by Priority");

            sortByDateButton.getStyleClass().add("button");
            sortByPriorityButton.getStyleClass().add("button");

            sortByDateButton.setOnAction(e -> sortTasksByDueDate());
            sortByPriorityButton.setOnAction(e -> sortTasksByPriority());

            HBox sortingButtonsBox = new HBox(10, sortByDateButton, sortByPriorityButton);
            sortingButtonsBox.setAlignment(Pos.CENTER);

            

            root.getChildren().addAll(
                titleLabel, taskInput, priorityComboBox, datePicker, addButton,
                sortingButtonsBox , taskListView, completedTasksBox
            );
            Scene scene = new Scene(root, 600, 600);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTask(String description, Priority priority, LocalDate dueDate) {
        if (!description.isEmpty() && dueDate != null) {
            Task task = new Task(description, priority, dueDate);
            tasks.add(task);
            tasks.sort(Comparator.comparing(Task::getDueDate)); // Sort tasks by due date
        }
    }

    private void searchTasks(String searchTerm) {
        taskListView.getItems().clear();

        tasks.stream()
                .filter(task -> task.getDescription().contains(searchTerm))
                .forEach(task -> taskListView.getItems().add(task));
    }

    private void markTaskAsCompleted(Task task) {
        completedTasks.add(task);
        tasks.remove(task);
        completedTasksListView.refresh();
    }
    private void sortTasksByDueDate() {
        tasks.sort(Comparator.comparing(Task::getDueDate));
        taskListView.refresh();
    }

    // Add a method to sort tasks by priority
    private void sortTasksByPriority() {
        tasks.sort(Comparator.comparing(Task::getPriority));
        taskListView.refresh();
    }


    private class TaskListCell extends ListCell<Task> {
    	private HBox hbox = new HBox();
        private Label taskLabel = new Label(); // Label for displaying task name
        private HBox buttonBox = new HBox(); // HBox for buttons

        private Button editButton = new Button("Edit");
        private Button deleteButton = new Button("Delete");
        private Button completeButton = new Button("Complete");
        

        // Add a method to sort tasks by completion status
        private void sortTasksByCompletionStatus() {
            tasks.sort(Comparator.comparing(Task::isCompleted).reversed());
            taskListView.refresh();
            }

        // Add a method to filter tasks by priority
        private void filterTasksByPriority(Priority priority) {
            taskListView.getItems().clear();
            tasks.stream()
                    .filter(task -> task.getPriority() == priority)
                    .forEach(task -> taskListView.getItems().add(task));
        }

        public TaskListCell() {
        	editButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white;");
            deleteButton.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white;");
            completeButton.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white;");

            // Customize button sizes
            editButton.setPrefWidth(60);
            deleteButton.setPrefWidth(60);
            completeButton.setPrefWidth(80);
            
            editButton.setOnAction(event -> editTask(getItem()));
            deleteButton.setOnAction(event -> deleteTask(getItem()));
            completeButton.setOnAction(event -> markTaskAsCompleted(getItem()));
            editButton.setOnAction(event -> editTask(getItem()));
            
            completeButton.setOnAction(event -> markTaskAsCompleted(getItem()));
            deleteButton.setOnAction(event -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Delete Task");
                confirmAlert.setContentText("Are you sure you want to delete this task?");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    deleteTask(getItem());
                }
            });

            buttonBox.getChildren().addAll(editButton, deleteButton, completeButton);

            // Arrange label and buttonBox in the hbox
            hbox.getChildren().addAll(taskLabel, buttonBox);
            hbox.setSpacing(10); // Add spacing between label and buttons
            hbox.setAlignment(Pos.CENTER_LEFT); // Align label to the left
        }


        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);

            if (empty || task == null) {
                setText(null);
                setGraphic(null);
            } else {
                String formattedDueDate = task.getDueDate() != null ? task.getDueDate().toString() : "No due date";
                setText(task.getDescription() + " (Due: " + formattedDueDate + ", Priority: " + task.getPriority() + ")");
                setGraphic(hbox);

                // Set text color based on priority
                if (task.getPriority() == Priority.LOW) {
                    setTextFill(javafx.scene.paint.Color.GREEN);
                } else if (task.getPriority() == Priority.MEDIUM) {
                    setTextFill(javafx.scene.paint.Color.BLUE);
                } else if (task.getPriority() == Priority.HIGH) {
                    setTextFill(javafx.scene.paint.Color.RED);
                }
            }
        }
    }
        
        /*protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);

            if (empty || task == null) {
                setText(null);
                setGraphic(null);
            } else {
                String formattedDueDate = task.getDueDate() != null ? task.getDueDate().toString() : "No due date";
                setText(task.getDescription() + " (Due: " + formattedDueDate + ", Priority: " + task.getPriority() + ")");
                setGraphic(hbox);
            }
        }
    }*/
    
    private void editTask(Task task) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Edit the task:");

        // Set up the dialog buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create content for the dialog
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(10));

        TextField taskDescriptionField = new TextField(task.getDescription());
        DatePicker dueDatePicker = new DatePicker(task.getDueDate());
        ComboBox<Priority> priorityComboBox = new ComboBox<>(FXCollections.observableArrayList(Priority.values()));
        priorityComboBox.setValue(task.getPriority());

        content.getChildren().addAll(
            new Label("Task Name:"),
            taskDescriptionField,
            new Label("Due Date:"),
            dueDatePicker,
            new Label("Priority:"),
            priorityComboBox
        );

        dialog.getDialogPane().setContent(content);

        // Convert the result to a Task object when the "Save" button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                task.setDescription(taskDescriptionField.getText());
                task.setDueDate(dueDatePicker.getValue());
                task.setPriority(priorityComboBox.getValue());
                taskListView.refresh(); // Refresh the list view
            }
            return task;
        });

        // Show the dialog and handle the result
        Optional<Task> result = dialog.showAndWait();
        result.ifPresent(updatedTask -> {
            // You can perform additional actions after the task is updated
        });
    }



		/*
		 * private void editTask(Task task) { TextInputDialog dialog = new
		 * TextInputDialog(task.getDescription()); dialog.setTitle("Edit Task");
		 * dialog.setHeaderText("Edit the task:"); dialog.setContentText("Task:");
		 * 
		 * Optional<String> result = dialog.showAndWait(); result.ifPresent(editedTask
		 * -> { task.setDescription(editedTask); taskListView.refresh(); }); }
		 */

    private void deleteTask(Task task) {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Task Deleted");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Task has been deleted successfully.");

        tasks.remove(task);
        taskListView.refresh();
        successAlert.showAndWait();
    }
    
    

    private enum Priority {
        LOW, MEDIUM, HIGH
    }

    private class Task {
        private String description;
        private Priority priority;
        private LocalDate dueDate;
        private boolean completed;

        public Task(String description, Priority priority, LocalDate dueDate) {
            this.description = description;
            this.priority = priority;
            this.dueDate = dueDate;
            this.completed = false;

        }

        public String getDescription() {
            return description;
        }
        
        public String toString() {
            return description; // Return the task description (name)
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Priority getPriority() {
            return priority;
        }
        
        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        // You can add setters if needed
    }
}
