import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

/**
 * The ExperienceTrackerGUI class displays Player data for the game
 * OldSchool Runescape. The GUI has the following features:
 * - Display Skills data for a Player, either from a saved file or API
 * - Display and update Goals and Experience Rates for Skills
 * - Display progress towards set goals in percentages and hours
 * - Saving current configuration of Goals and Rates to files for later usage
 */
public class ExperienceTrackerGUI extends JFrame {
    // Text field for entering Player to load from API
    private JTextField usernameField;
    // Button for loading new Player from API
    private JButton loadAPIButton;
    // Button for refreshing skill data from API for current Player
    // DOES NOT RESET GOAL OR EXPERIENCE RATES
    private JButton refreshButton;
    // Button for saving current Player to a file
    private JButton savePlayerButton;
    // Button for loading a Player from a file
    private JButton loadFileButton;
    // Label for displaying information about the current Player
    private JLabel playerInfoLabel;
    // Panel for displaying Skills, Goals, Experience Rates, etc.
    private JPanel skillsPanel;
    // The current Player that has been loaded for display
    private Player currentPlayer;

    /**
     * Creates a new ExperienceTrackerGUI and initializes/lays out components
     */
    public ExperienceTrackerGUI() {
        setTitle("OSRS Experience Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);

        initializeComponents();
        layoutComponents();
    }

    /**
     * Initializes all components and adds action listeners
     */
    private void initializeComponents() {
        initializeInputComponents();
        initializePlayerInfoLabel();
        initializeSkillsPanel();
        addActionListeners();
    }

    /**
     * Initializes input components at the top of the GUI
     * Also sets intial enabled states for when a Player is not loaded
     */
    private void initializeInputComponents() {
        this.usernameField = new JTextField(15);
        this.loadAPIButton = new JButton("Load Player from API");
        this.loadFileButton = new JButton("Load Player from File");
        this.refreshButton = new JButton("Refresh Skills");
        this.savePlayerButton = new JButton("Save Player");
        this.savePlayerButton.setEnabled(false);
        this.refreshButton.setEnabled(false);
    }

    /**
     * Initialize label for displaying the current Player information
     * Includes the username and last refreshed date time.
     */
    private void initializePlayerInfoLabel() {
        this.playerInfoLabel = new JLabel(" ");
        this.playerInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.playerInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    /**
     * Initializes the JPanel which displays the skill data for the current Player
     */
    private void initializeSkillsPanel() {
        this.skillsPanel = new JPanel(new GridBagLayout());
    }

    /**
     * Adds action listeners to buttons on the top of the GUI
     */
    private void addActionListeners() {
        // Listener for load from API button
        this.loadAPIButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPlayer();
            }
        });

        // Listener for load from file button
        this.loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPlayerFromFile();
            }
        });

        // Listener for refresh skills button
        this.refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPlayer();
            }
        });

        // Listener for save player button
        this.savePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePlayer();
            }
        });

        // Listener for the ENTER key when typing a username
        this.usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPlayer();
            }
        });
    }

    /**
     * Lays out all components for the GUI using the BorderLayout
     * Makes the skill data scrollable in case components don't fit in GUI
     */
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = createTopPanel();
        JPanel playerInfoPanel = createPlayerInfoPanel();
        JPanel northPanel = createNorthPanel(topPanel, playerInfoPanel);
        JScrollPane scrollPane = createSkillsScrollPane();

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the JPanel which contains:
     * - The username text field
     * - The load from API button
     * - The load from file button
     * - The refresh button
     * - The save to file button
     * 
     * @return JPanel with text field and buttons
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(new JLabel("Username:"));
        topPanel.add(this.usernameField);
        topPanel.add(this.loadAPIButton);
        topPanel.add(this.loadFileButton);
        topPanel.add(this.refreshButton);
        topPanel.add(this.savePlayerButton);
        return topPanel;
    }

    /**
     * Creates a JPanel that displays the current player's information
     * Username + last refreshed time
     * 
     * @return JPanel with player info
     */
    private JPanel createPlayerInfoPanel() {
        JPanel playerInfoPanel = new JPanel(new BorderLayout());
        playerInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        playerInfoPanel.add(this.playerInfoLabel, BorderLayout.CENTER);
        return playerInfoPanel;
    }

    /**
     * Creates a JPanel which contains both the input text field/buttons and
     * the player information.
     * 
     * @param topPanel        panel containing the username text field + buttons
     * @param playerInfoPanel panel containing the current player information
     * @return
     */
    private JPanel createNorthPanel(JPanel topPanel, JPanel playerInfoPanel) {
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(playerInfoPanel, BorderLayout.SOUTH);
        return northPanel;
    }

    /**
     * Creates the JScrollPane for the skills panel. Allows for scrolling
     * if the table is larger than the GUI window.
     * 
     * @return JScrollPane for skills panel
     */
    private JScrollPane createSkillsScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this.skillsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    /**
     * Fetches data for the username in the text field from the API.
     * Displays option panes if an error occurs. This will reset set Goals
     * and Experience Rates.
     */
    private void loadPlayer() {
        String username = this.usernameField.getText().trim();

        // Display error if username is empty
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a username",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Create the new player and enable buttons that require a player
            this.currentPlayer = new Player(username);
            this.refreshButton.setEnabled(true);
            this.savePlayerButton.setEnabled(true);
            displayCurrentPlayer();
        } catch (PlayerNotFoundException e) {
            // Player was not found, display an error
            JOptionPane.showMessageDialog(this,
                    "Player not found: " + username,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Some unexpected API error occured, display an error
            JOptionPane.showMessageDialog(this,
                    "Error loading player: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads new player from a .osrs file by displaying a file chooser the user can
     * select the Player files from.
     * Displays option panes if encountering an error.
     * 
     */
    private void loadPlayerFromFile() {
        // Create and display a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Player");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                // Create the new Player and enable buttons
                this.currentPlayer = Player.loadFromFile(
                        fileChooser.getSelectedFile().getAbsolutePath());
                this.refreshButton.setEnabled(true);
                this.savePlayerButton.setEnabled(true);
                displayCurrentPlayer();
            } catch (IOException | ClassNotFoundException e) {
                // If provided an invalid file or there was an error, display it
                JOptionPane.showMessageDialog(this,
                        "Error loading player: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refreshes the skill data for the current player. Does not reset Goals
     * or Experience Rates. Does nothing if there is no current player.
     */
    private void refreshPlayer() {
        // Handle if currentPlayer is not loaded yet. Should be unreachable.
        if (this.currentPlayer == null) {
            return;
        }

        try {
            // Refresh skills data and display new data
            this.currentPlayer.refreshSkills();
            displayCurrentPlayer();
        } catch (Exception e) {
            // Some unexpected API error occured, display an error
            JOptionPane.showMessageDialog(this,
                    "Error refreshing player: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Saves the current player to an .osrs file. Displays a file chooser for
     * the user to select the file path and name.
     */
    private void savePlayer() {
        // Handle if currentPlayer is not loaded yet. Should be unreachable.
        if (this.currentPlayer == null) {
            JOptionPane.showMessageDialog(this,
                    "No player loaded to save",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create and display a file chooser
        JFileChooser fileChooser = new JFileChooser();
        String defaultFilename = this.currentPlayer.getUsername() + ".osrs";
        fileChooser.setDialogTitle("Save Player");
        fileChooser.setSelectedFile(new File(defaultFilename));
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                // Attempt to save the current player to a file
                this.currentPlayer.saveToFile(fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Player saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                // If some IO error occurs, display it
                JOptionPane.showMessageDialog(this,
                        "Error saving player: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Clears and updates the player information panel as well as the skills table.
     * The skills panel is laid out using a GridBagConstraints.
     */
    private void displayCurrentPlayer() {
        updatePlayerInfo();
        this.skillsPanel.removeAll();

        // GBC with anhor at WEST to align components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        // Add the components using the GridBagConstraints
        addSkillHeaders(gbc);
        addSkillRows(this.currentPlayer.getSkills(), gbc);

        // Repaint the skills panel
        this.skillsPanel.revalidate();
        this.skillsPanel.repaint();
    }

    /**
     * Adds the headers for the skills panel.
     * 
     * @param gbc the GridBagConstraints used to format the table
     */
    private void addSkillHeaders(GridBagConstraints gbc) {
        // Top-most Row
        gbc.gridy = 0;

        // Skill Header
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        addHeaderLabel("Skill", gbc);

        // Rank Header
        gbc.gridx = 1;
        gbc.weightx = 0.1;
        addHeaderLabel("Rank", gbc);

        // Level Header
        gbc.gridx = 2;
        gbc.weightx = 0.08;
        addHeaderLabel("Level", gbc);

        // Experience Header
        gbc.gridx = 3;
        gbc.weightx = 0.12;
        addHeaderLabel("Experience", gbc);

        // Goal Header
        gbc.gridx = 4;
        gbc.weightx = 0.1;
        addHeaderLabel("Goal", gbc);

        // Placeholder for Update Goal button
        gbc.gridx = 5;
        gbc.weightx = 0.08;
        addHeaderLabel("", gbc);

        // Progress Header
        gbc.gridx = 6;
        gbc.weightx = 0.12;
        addHeaderLabel("Progress", gbc);

        // Experience Rate Header
        gbc.gridx = 7;
        gbc.weightx = 0.1;
        addHeaderLabel("Exp/Hour", gbc);

        // Placeholder for Update Experience Rate button
        gbc.gridx = 8;
        gbc.weightx = 0.08;
        addHeaderLabel("", gbc);

        // Hours to Goal Header
        gbc.gridx = 9;
        gbc.weightx = 0.12;
        addHeaderLabel("Hours to Goal", gbc);
    }

    /**
     * Adds a row for each skill, cell by cell.
     * 
     * @param skills the skills for the current player
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addSkillRows(ArrayList<Skill> skills, GridBagConstraints gbc) {
        // Take up as much space as alloted
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            gbc.gridy = i + 1;

            addSkillNameCell(skill, gbc);
            addRankCell(skill, gbc);
            addLevelCell(skill, gbc);
            addExperienceCell(skill, gbc);
            addGoalCell(skill, gbc);
            addUpdateGoalButtonCell(skill, gbc);
            addProgressCell(skill, gbc);
            addExpRateCell(skill, gbc);
            addUpdateExpRateButtonCell(skill, gbc);
            addTimeToGoalCell(skill, gbc);
        }
    }

    /**
     * Adds a label for the skill's name
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addSkillNameCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        JLabel nameLabel = new JLabel(skill.getName().toString());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(nameLabel, gbc);
    }

    /**
     * Adds a label for the skill's rank
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addRankCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.weightx = 0.1;
        JLabel rankLabel = new JLabel(formatNumber(skill.getRank()));
        rankLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(rankLabel, gbc);
    }

    /**
     * Adds a label for the skill's level
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addLevelCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 2;
        gbc.weightx = 0.08;
        JLabel levelLabel = new JLabel(skill.formattedLevelString());
        levelLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(levelLabel, gbc);
    }

    /**
     * Adds a label for the skill's experience
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addExperienceCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 3;
        gbc.weightx = 0.12;
        JLabel expLabel = new JLabel(formatNumber(skill.getExperience()));
        expLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(expLabel, gbc);
    }

    /**
     * Adds a label for the skill's goal. Blank cell for overall.
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addGoalCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 4;
        gbc.weightx = 0.1;

        String goalText;
        // No goals for overall skill
        if (skill.isOverall()) {
            goalText = "";
        } else {
            Goal goal = this.currentPlayer.getGoal(skill.getName());
            goalText = goal.toString();
        }

        JLabel goalLabel = new JLabel(goalText);
        goalLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(goalLabel, gbc);
    }

    /**
     * Adds a button cell for updating the goal for the skill
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addUpdateGoalButtonCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 5;
        gbc.weightx = 0.08;

        // Don't allow setting goals for Overall
        if (skill.isOverall()) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            this.skillsPanel.add(emptyLabel, gbc);
        } // Add update button which will display a dialog to update the goal
        else {
            JButton updateGoalButton = new JButton("Update");
            updateGoalButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            updateGoalButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showUpdateGoalDialog(skill);
                }
            });
            this.skillsPanel.add(updateGoalButton, gbc);
        }
    }

    /**
     * Adds a progress bar for the skill's progress to goal. Blank cell for overall.
     * Displays progress as a percentage value, based on the experience needed
     * to meet the skill's Goal.
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addProgressCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 6;
        gbc.weightx = 0.12;

        // Don't display progress for overall skill
        if (skill.isOverall()) {
            JLabel naLabel = new JLabel("");
            naLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            this.skillsPanel.add(naLabel, gbc);
        } // Add progress for the specified skill and display as a progress bar
        else {
            int progress = this.currentPlayer.getProgressToGoal(skill.getName());
            JProgressBar progressBar = createProgressBar(progress);
            this.skillsPanel.add(progressBar, gbc);
        }
    }

    /**
     * Adds a label for the skill's experience rate.
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addExpRateCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 7;
        gbc.weightx = 0.1;

        String expRateText;
        // Don't display an experience rate for overall
        if (skill.isOverall()) {
            expRateText = "";
        } // Fetch and format the experience rate for the current skill
        else {
            Integer expRate = this.currentPlayer.getExperienceRate(skill.getName());
            expRateText = (expRate == null) ? "N/A" : formatNumber(expRate);
        }

        JLabel expRateLabel = new JLabel(expRateText);
        expRateLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(expRateLabel, gbc);
    }

    /**
     * Adds a button cell for updating the experience rate for the skill
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addUpdateExpRateButtonCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 8;
        gbc.weightx = 0.08;

        // Don't allow setting experience rate for overall
        if (skill.isOverall()) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            this.skillsPanel.add(emptyLabel, gbc);
        } // Add update button which displays dialog to update the exp rate
        else {
            JButton updateExpRateButton = new JButton("Update");
            updateExpRateButton.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            updateExpRateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showUpdateExpRateDialog(skill);
                }
            });
            this.skillsPanel.add(updateExpRateButton, gbc);
        }
    }

    /**
     * Adds a label for the time to reach the skill's goal
     * 
     * @param skills the current skill
     * @param gbc    the GridBagConstraint for the skills panel
     */
    private void addTimeToGoalCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 9;
        gbc.weightx = 0.12;

        String timeText;
        // Don't display time for overall skill
        if (skill.isOverall()) {
            timeText = "";
        } // Format time to goal text for skill
        else {
            double timeToGoal = this.currentPlayer.getTimeToGoal(skill.getName());
            if (timeToGoal < 0) {
                timeText = "N/A";
            } else {
                timeText = String.format("%.2f hours", timeToGoal);
            }
        }

        JLabel timeLabel = new JLabel(timeText);
        timeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(timeLabel, gbc);
    }

    /**
     * Creates a progress bar with color based on percentage
     * 
     * @param progress the percentange progress
     * @return JProgressBar for progress to Goal
     */
    private JProgressBar createProgressBar(int progress) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress);
        progressBar.setStringPainted(true);
        progressBar.setString(progress + "%");
        progressBar.setForeground(getProgressBarColor(progress));
        progressBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        return progressBar;
    }

    /**
     * Returns the Color based on the given progress.
     * RED > ORANGE > YELLOW > GREEN
     * 
     * @param progress progress to Goal
     * @return Color to color progress bar as
     */
    private Color getProgressBarColor(int progress) {
        if (progress < 25) {
            return Color.RED;
        } else if (progress < 50) {
            return Color.ORANGE;
        } else if (progress < 75) {
            return Color.YELLOW;
        } else if (progress < 100) {
            return Color.GREEN;
        } else {
            return Color.GREEN.darker();
        }
    }

    /**
     * Adds formatted header label to skills panel
     * 
     * @param text the text for the label
     * @param gbc  the GridBagConstraint for the skills panel
     */
    private void addHeaderLabel(String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 150));
        this.skillsPanel.add(label, gbc);
    }

    /**
     * Helper for formatting numbers with commas
     * 
     * @param number number to format
     * @return a String of the number formatted with commas
     */
    private String formatNumber(int number) {
        return String.format("%,d", number);
    }

    /**
     * Updates the contents of the player information label
     */
    private void updatePlayerInfo() {
        String username = this.currentPlayer.getUsername();
        String timestamp = this.currentPlayer.getLastRefreshedAt().toString();
        this.playerInfoLabel.setText("Player: " + username + " | Last Refreshed: " + timestamp);
    }

    /**
     * Displays a dialog for updating the given skill's Goal
     * 
     * @param skill the skill to update the Goal for
     */
    private void showUpdateGoalDialog(Skill skill) {
        JDialog dialog = createUpdateGoalDialog(skill);
        dialog.setVisible(true);
    }

    /**
     * Creates a JDialog which allows users to set a Goal for a given skill.
     * Handles invalid inputs such as non-number or negative inputs.
     * 
     * @param skill the skill to update the goal for
     * @return JDialog for updating the Goal for the skill
     */
    private JDialog createUpdateGoalDialog(Skill skill) {
        // Create Dialog
        JDialog dialog = new JDialog(this, "Set Goal for " + skill.getName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        // Layout for the Dialog Window
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        // Radio buttons for choosing goal type
        JRadioButton levelGoalRadio = new JRadioButton("Level Goal", true);
        JRadioButton expGoalRadio = new JRadioButton("Experience Goal");
        ButtonGroup goalTypeGroup = new ButtonGroup();
        goalTypeGroup.add(levelGoalRadio);
        goalTypeGroup.add(expGoalRadio);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(levelGoalRadio, gbc);

        gbc.gridy = 1;
        mainPanel.add(expGoalRadio, gbc);

        // Add text field for goal target level or experience
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Target: "), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField targetField = new JTextField(10);
        mainPanel.add(targetField, gbc);

        // Hint label showing user what are valid values
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel hintLabel = new JLabel("Level: 2-126 | Experience: 1-200,000,000");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        mainPanel.add(hintLabel, gbc);

        // Add saving and cancelling buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save button action listener with error handling
                try {
                    int target = Integer.parseInt(targetField.getText().trim());
                    boolean isLevel = levelGoalRadio.isSelected();

                    currentPlayer.updateGoal(skill.getName(), isLevel, target);
                    dialog.dispose();
                    displayCurrentPlayer();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a valid number",
                            "Invalid Goal",
                            JOptionPane.ERROR_MESSAGE);
                } catch (InvalidGoalException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Invalid target for your goal!",
                            "Invalid Goal",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            // Cancel button action listener
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        return dialog;
    }

    /**
     * Displays a dialog for updating the given skill's Experience Rate
     * 
     * @param skill the skill to update the Experience Rate for
     */
    private void showUpdateExpRateDialog(Skill skill) {
        JDialog dialog = createUpdateExpRateDialog(skill);
        dialog.setVisible(true);
    }

    /**
     * Creates a JDialog which allows users to set an Experience Rate for a given
     * skill. Handles invalid inputs such as non-number or negative inputs.
     * 
     * @param skill the skill to update the experience rate for
     * @return JDialog for updating the experience rate for the skill
     */
    private JDialog createUpdateExpRateDialog(Skill skill) {
        // Create Dialog
        JDialog dialog = new JDialog(this, "Set Experience Rate for " + skill.getName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(this);

        // Layout for the Dialog Window
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        // Experience Rate Input
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Experience Rate (exp/hour): "), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField expRateField = new JTextField(10);
        mainPanel.add(expRateField, gbc);

        // Hint label showing user what are valid values
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel hintLabel = new JLabel("Rate: 1-200,000,000 exp/hour");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        mainPanel.add(hintLabel, gbc);

        // Add saving and cancelling buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save button action listener with error handling
                try {
                    int rate = Integer.parseInt(expRateField.getText().trim());

                    currentPlayer.updateExperienceRate(skill.getName(), rate);
                    dialog.dispose();
                    displayCurrentPlayer();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a valid number",
                            "Invalid Experience Rate",
                            JOptionPane.ERROR_MESSAGE);
                } catch (InvalidExperienceRateException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Invalid experience rate!",
                            "Invalid Experience Rate",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            // Cancel button action listener
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        return dialog;
    }
}