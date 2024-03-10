package com.exemple.socialapp.controller;

import com.exemple.socialapp.SocialApp;
import com.exemple.socialapp.domain.*;
import com.exemple.socialapp.repository.FriendshipDBRepository;
import com.exemple.socialapp.repository.MessagesDBRepository;
import com.exemple.socialapp.repository.RequestDBRepository;
import com.exemple.socialapp.repository.UserDBRepository;
import com.exemple.socialapp.service.ServiceFriendship;
import com.exemple.socialapp.service.ServiceMessages;
import com.exemple.socialapp.service.ServiceRequest;
import com.exemple.socialapp.service.ServiceUsers;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MainWindowController {

    @FXML
    private TableView<User> userTable = new TableView<>();

    @FXML
    private TableColumn<User, String> addFriendColumn = new TableColumn<>();

    @FXML
    private TableColumn<User, String> usernameColumn = new TableColumn<>();

    @FXML
    private TableColumn<User, String> firstNameColumn = new TableColumn<>();

    @FXML
    private TableColumn<User, String> lastNameColumn = new TableColumn<>();

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private HBox chatBox;

    @FXML
    private Label welcomeUserLabel = new Label();

    @FXML
    private ListView<User> friendsList = new ListView<>();

    @FXML
    private ListView<Message> messageList = new ListView<>();

    @FXML
    private Label replyLabel = new Label();

    private Message originalMessage;

    @FXML
    private TextField messageField;

    @FXML
    private ContextMenu messageContextMenu;

    @FXML
    private TextField pageNumber = new TextField();

    @FXML
    private TextField nrElems = new TextField();

//    @FXML
//    private TextField setPageNumber = new TextField();
//
//    @FXML
//    private TextField setNrElemsFriends = new TextField();

    private static User loggedInUser;

    private int page_number = 0, page_size = 2, totalPageCount;

    //    private final ServiceFriendship<Tuple<Long,Long>, Friendship> friendshipService;

    private final ServiceMessages<Long, Message> messageService;

    private final ServiceUsers<Long, User> userService;

    private final ServiceRequest<Tuple<Long,Long>, FriendRequest> requestService;

    public MainWindowController(){
//        this.friendshipService = new ServiceFriendship<>(new FriendshipDBRepository(url,username,password));
        this.messageService = new ServiceMessages<>(new MessagesDBRepository());
        this.userService = new ServiceUsers<>(new UserDBRepository());
        this.requestService = new ServiceRequest<>(new RequestDBRepository());

    }

    @FXML
    private void chat_page(){
        mainBorderPane.setCenter(chatBox);
        page_size = 10;
        page_number = 0;
        initChatPage();
    }

    @FXML
    private void settings_page(){
        load_page("settings-window");
    }

    @FXML
    private void profile_page(){
        load_page("profile-window");
    }

    @FXML
    private void people_page(){
        load_page("people-window");
        initUsersPage(page_number, page_size);
    }

    @FXML
    private int getNrElements(){
        if(nrElems.getText().isEmpty())
            return page_size;
        page_size = Integer.parseInt(nrElems.getText());
        page_number = 0;
        clearSettingsField();
        loadUsers(page_number, page_size);
        load_users_list(page_number, page_size);
        return page_size;
    }

    private void load_page(String page){
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(SocialApp.class.getResource(page + ".fxml")));

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MainWindowController controller = loader.getController();
        controller.setLoggedInUser(loggedInUser);

        mainBorderPane.setCenter(root);
    }

    public void setLoggedInUser(User user) {
        loggedInUser = user;
        welcomeUserLabel.setText("Welcome " + user.getUsername() + "!");
        init();
    }

    private void handleFriendSelection(User selectedFriend) {

        if (loggedInUser != null && selectedFriend != null) {

            List<Message> sentMessages = messageService.findMessagesBetweenUsers(loggedInUser.getId(), selectedFriend.getId());
            List<Message> receivedMessages = messageService.findMessagesBetweenUsers(selectedFriend.getId(), loggedInUser.getId());

            List<Message> allMessages = new ArrayList<>();
            if (sentMessages != null) {
                allMessages.addAll(sentMessages);
            }
            if (receivedMessages != null) {
                allMessages.addAll(receivedMessages);
            }

            messageContextMenu = new ContextMenu();
            MenuItem deleteMenuItem = new MenuItem("Delete");
            MenuItem replyMenuItem = new MenuItem("Reply");
            deleteMenuItem.setOnAction(event -> handleDeleteMessage());
            replyMenuItem.setOnAction(event -> handleReplyMessage());

            messageContextMenu.getItems().addAll(replyMenuItem);
            messageContextMenu.getItems().addAll(deleteMenuItem);

            allMessages.sort(Comparator.comparing(Message::getDate));

            ObservableList<Message> messagesObservableList;
            messagesObservableList = FXCollections.observableArrayList(allMessages);


            messageList.setItems(messagesObservableList);

            messageList.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Message message, boolean empty) {
                    super.updateItem(message, empty);
                    if (empty || message == null) {
                        setText(null);
                        setContextMenu(null);
                    } else {
                        setText(message.toString());

                        setContextMenu(messageContextMenu);

                        if (message.getFrom_id().equals(loggedInUser.getId())) {
                            setAlignment(Pos.CENTER_RIGHT);
//                            setStyle("-fx-background-color: #cce0ff; -fx-border-color: #3366cc; -fx-border-width: 1;");
                        } else {
                            setAlignment(Pos.CENTER_LEFT);
//                            setStyle("-fx-background-color: #e6e6e6; -fx-border-color: #c44f4f; -fx-border-width: 1;");
                        }

                        setMinWidth(Region.USE_PREF_SIZE);
                        setMaxWidth(Region.USE_PREF_SIZE);
                        setPrefWidth(Region.USE_COMPUTED_SIZE);
                    }
                }
            });
        }
    }

    @FXML
    private void sendMessage() {
        String messageText = messageField.getText();
        if (!messageText.isEmpty()) {
            User selectedFriend = friendsList.getSelectionModel().getSelectedItem();
            if (selectedFriend != null) {
                if (originalMessage != null) {
                    Message replyMessage = new ReplyMessage(loggedInUser.getId(), List.of(selectedFriend.getId()), messageText, originalMessage.getId());
                    messageService.add(replyMessage);
                } else {
                    Message message = new Message(loggedInUser.getId(), List.of(selectedFriend.getId()), messageText);
                    messageService.add(message);
                }

                handleFriendSelection(selectedFriend);

                clearReply();
            } else {
                showAlert("Please select a friend to send a message.");
            }
        }
    }

    private void clearReply() {

        replyLabel.setText("");
        replyLabel.setVisible(false);

        messageField.clear();

        originalMessage = null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearSettingsField(){
        nrElems.clear();
    }

    public void init() {
        initChatPage();
        initUsersPage(page_number, page_size);
    }

    private void initChatPage(){
        MenuItem deleteMenuItem = new MenuItem("Delete Message");
        deleteMenuItem.setOnAction(event -> handleDeleteMessage());

        load_users_list(page_number, page_size);

        friendsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getUsername());
                    setMinWidth(USE_PREF_SIZE);
                    setMaxWidth(USE_PREF_SIZE);
                }
            }
        });

        friendsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleFriendSelection(newValue);
            }
        });
    }

    private void load_users_list(int page_number, int page_size){
        Optional<User> userOptional = userService.findOne(loggedInUser.getId());
        User user = userOptional.get();
        List<Long> friendIds = user.getFriends();

        List<User> friends = new ArrayList<>();
        for (Long friendId : friendIds) {
            Optional<User> friendOptional = userService.findOne(friendId);
            friendOptional.ifPresent(friends::add);
        }

        List<User> paginatedList = paginate(friends, page_number, page_size);

        totalPageCount = getTotalPageCount(friends.size(), page_size);

        ObservableList<User> data = FXCollections.observableArrayList(paginatedList);
        friendsList.setItems(data);
    }

    private void initUsersPage(int page_number, int page_size){
        messageContextMenu = new ContextMenu();
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        addFriendColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Add"));
        addFriendColumn.setCellFactory(column -> new ButtonCell());

        loadUsers(page_number, page_size);

    }

    private void handleDeleteMessage() {

        Message selectedMessage = messageList.getSelectionModel().getSelectedItem();
        if (selectedMessage != null) {
            messageService.remove(selectedMessage.getId());
            handleFriendSelection(friendsList.getSelectionModel().getSelectedItem());
        }
    }

    private void handleReplyMessage() {
        Message selectedMessage = messageList.getSelectionModel().getSelectedItem();
        if (selectedMessage != null) {
            originalMessage = selectedMessage;

            replyLabel.setText("Replying to: " + selectedMessage.getMessage());
            replyLabel.setVisible(true);

            messageField.requestFocus();
        }
    }

    private void rejectRequest(FriendRequest existingRequest){
        requestService.reject(existingRequest);
        initChatPage();
    }

    private void loadUsers(int pageNumber, int pageSize) {
        userTable.getItems().clear();

        Iterable<User> users = userService.findAll();
        List<User> userList = new ArrayList<>();
        users.forEach(userList::add);

        loggedInUser = userService.findOne(loggedInUser.getId()).get();

        List<User> filteredList = userList.stream()
                .filter(user -> !shouldFilter(user))
                .collect(Collectors.toList());

        List<User> paginatedList = paginate(filteredList, pageNumber, pageSize);

        totalPageCount = getTotalPageCount(filteredList.size(), pageSize);

        ObservableList<User> data = FXCollections.observableArrayList(paginatedList);
        userTable.setItems(data);
    }

    private List<User> paginate(List<User> userList, int pageNumber, int pageSize) {
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min((pageNumber + 1) * pageSize, userList.size());
        return userList.subList(fromIndex, toIndex);
    }

    private boolean shouldFilter(User user) {
        return Objects.equals(user.getId(), loggedInUser.getId()) ||
                loggedInUser.getFriends().contains(user.getId()) ||
                user.isAdmin();
    }

    @FXML
    private void nextPage(){
        if(page_number < totalPageCount - 1){
            page_number++;
            loadUsers(page_number, page_size);
            load_users_list(page_number, page_size);
        }
    }

    @FXML
    private void previousPage(){
        if(page_number > 0){
            page_number--;
            loadUsers(page_number, page_size);
            load_users_list(page_number, page_size);
        }
    }

    @FXML
    private void goToPage(){
        String pageNumberText = pageNumber.getText();
        if(!pageNumberText.isEmpty()){
            page_number = Integer.parseInt(pageNumberText);
            page_number = page_number % totalPageCount;
            loadUsers(page_number, page_size);
            load_users_list(page_number, page_size);
            pageNumber.clear();
        }
        else {
            showAlert("Field is empty!");
        }

    }

    private int getTotalPageCount(int totalItems, int pageSize) {
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    private class ButtonCell extends TableCell<User, String> {
        private final Button actionButton = new Button();
        private final ServiceRequest<Tuple<Long, Long>, FriendRequest> requestService;
        private final ServiceFriendship<Tuple<Long,Long>, Friendship> friendshipService;
        @FXML
        private final ContextMenu rejectContextMenu;

        ButtonCell() {
            this.requestService = new ServiceRequest<>(new RequestDBRepository());
            this.friendshipService = new ServiceFriendship<>(new FriendshipDBRepository());

            rejectContextMenu = new ContextMenu();

            actionButton.setOnAction((ActionEvent event) -> {
                User user = getTableView().getItems().get(getIndex());
                FriendRequest existingRequest = getExistingFriendRequest(user.getId());

                if (existingRequest == null) {
                    FriendRequest newRequest = new FriendRequest(loggedInUser.getId(), user.getId());
                    this.requestService.add(newRequest);
                } else {
                    handleFriendRequest(existingRequest, user.getId());
                }
            });

        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                User user = getTableView().getItems().get(getIndex());
                FriendRequest existingRequest = getExistingFriendRequest(user.getId());

                if (existingRequest == null) {
                    setButtonProperties("Add Friend", true, (ActionEvent event) -> {
                        requestService.add(new FriendRequest(loggedInUser.getId(), user.getId()));
                        initUsersPage(page_number, page_size);
                    });

                } else {
                    handleFriendRequest(existingRequest, user.getId());
                }
            } else {
                setGraphic(null);
            }
        }

        private FriendRequest getExistingFriendRequest(Long userId) {
            return requestService.findOne(new Tuple<>(loggedInUser.getId(), userId))
                    .orElse(requestService.findOne(new Tuple<>(userId, loggedInUser.getId())).orElse(null));
        }

        private void handleFriendRequest(FriendRequest existingRequest, Long userId) {
            boolean loggedInUserIsSender = loggedInUser.getId().equals(existingRequest.getSenderId());

            switch (existingRequest.getStatus()) {
                case PENDING:
                    if (loggedInUserIsSender) {
                        setButtonProperties("Pending", false, null);
                    } else {
                        MenuItem rejectRequestItem = new MenuItem("Reject");
                        rejectRequestItem.setOnAction(event -> rejectRequest(existingRequest));
                        rejectContextMenu.getItems().addAll(rejectRequestItem);
                        setContextMenu(rejectContextMenu);
                        setButtonProperties("Accept", true, (ActionEvent event) -> {
                            Friendship friendship = requestService.accept(existingRequest).get();
                            friendshipService.add(friendship);
                            init();
                        });
                    }
                    break;
                case REJECTED:
                    setButtonProperties("Rejected", false, null);
                    break;
                case APPROVED:
                    setButtonProperties("Accepted", false, null);
                    break;
            }
        }

        private void setButtonProperties(String buttonText, boolean isEnabled, EventHandler<ActionEvent> onAction) {
            actionButton.setText(buttonText);
            actionButton.setDisable(!isEnabled);
            actionButton.setOnAction(onAction);
            setGraphic(actionButton);
        }
    }


}
