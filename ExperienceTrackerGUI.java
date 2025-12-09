import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ExperienceTrackerGUI extends JFrame {
    private JTextField usernameField;
    private JButton loadButton;
    private JButton refreshButton;
    private JLabel playerInfoLabel;
    private JPanel skillsPanel;
    private Player currentPlayer;

    public ExperienceTrackerGUI() {
        setTitle("OSRS Experience Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);

        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        initializeInputComponents();
        initializePlayerInfoLabel();
        initializeSkillsPanel();
        addActionListeners();
    }

    private void initializeInputComponents() {
        this.usernameField = new JTextField(15);
        this.loadButton = new JButton("Load Player");
        this.refreshButton = new JButton("Refresh Skills");
        this.refreshButton.setEnabled(false);
    }

    private void initializePlayerInfoLabel() {
        this.playerInfoLabel = new JLabel(" ");
        this.playerInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.playerInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    private void initializeSkillsPanel() {
        this.skillsPanel = new JPanel(new GridBagLayout());
    }

    private void addActionListeners() {
        this.loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPlayer();
            }
        });

        this.refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPlayer();
            }
        });

        this.usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPlayer();
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = createTopPanel();
        JPanel playerInfoPanel = createPlayerInfoPanel();
        JPanel northPanel = createNorthPanel(topPanel, playerInfoPanel);
        JScrollPane scrollPane = createSkillsScrollPane();

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(new JLabel("Username:"));
        topPanel.add(this.usernameField);
        topPanel.add(this.loadButton);
        topPanel.add(this.refreshButton);
        return topPanel;
    }

    private JPanel createPlayerInfoPanel() {
        JPanel playerInfoPanel = new JPanel(new BorderLayout());
        playerInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        playerInfoPanel.add(this.playerInfoLabel, BorderLayout.CENTER);
        return playerInfoPanel;
    }

    private JPanel createNorthPanel(JPanel topPanel, JPanel playerInfoPanel) {
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(playerInfoPanel, BorderLayout.SOUTH);
        return northPanel;
    }

    private JScrollPane createSkillsScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this.skillsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    private void loadPlayer() {
        String username = this.usernameField.getText().trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a username",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.skillsPanel.removeAll();
        this.skillsPanel.revalidate();
        this.skillsPanel.repaint();

        try {
            this.currentPlayer = new Player(username);
            displayCurrentPlayer();
            this.refreshButton.setEnabled(true);
        } catch (PlayerNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Player not found: " + username,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading player: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshPlayer() {
        if (this.currentPlayer == null) {
            return;
        }

        try {
            this.currentPlayer.refreshSkills();
            displayCurrentPlayer();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error refreshing player: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayCurrentPlayer() {
        updatePlayerInfo();
        this.skillsPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        addSkillHeaders(gbc);
        addSkillRows(this.currentPlayer.getSkills(), gbc);

        this.skillsPanel.revalidate();
        this.skillsPanel.repaint();
    }

    private void addSkillHeaders(GridBagConstraints gbc) {
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0.1;
        addHeaderLabel("Skill", gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.1;
        addHeaderLabel("Rank", gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.08;
        addHeaderLabel("Level", gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.12;
        addHeaderLabel("Experience", gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.1;
        addHeaderLabel("Goal", gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.08;
        addHeaderLabel("", gbc);

        gbc.gridx = 6;
        gbc.weightx = 0.12;
        addHeaderLabel("Progress", gbc);

        gbc.gridx = 7;
        gbc.weightx = 0.1;
        addHeaderLabel("Exp/Hour", gbc);

        gbc.gridx = 8;
        gbc.weightx = 0.08;
        addHeaderLabel("", gbc);

        gbc.gridx = 9;
        gbc.weightx = 0.12;
        addHeaderLabel("Hours to Goal", gbc);
    }

    private void addSkillRows(ArrayList<Skill> skills, GridBagConstraints gbc) {
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

    private void addSkillNameCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        JLabel nameLabel = new JLabel(skill.getName().toString());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(nameLabel, gbc);
    }

    private void addRankCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.weightx = 0.1;
        JLabel rankLabel = new JLabel(formatNumber(skill.getRank()));
        rankLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(rankLabel, gbc);
    }

    private void addLevelCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 2;
        gbc.weightx = 0.08;
        JLabel levelLabel = new JLabel(skill.formattedLevelString());
        levelLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(levelLabel, gbc);
    }

    private void addExperienceCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 3;
        gbc.weightx = 0.12;
        JLabel expLabel = new JLabel(formatNumber(skill.getExperience()));
        expLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(expLabel, gbc);
    }

    private void addGoalCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 4;
        gbc.weightx = 0.1;

        String goalText;
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

    private void addUpdateGoalButtonCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 5;
        gbc.weightx = 0.08;

        if (skill.isOverall()) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            this.skillsPanel.add(emptyLabel, gbc);
        } else {
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

    private void addProgressCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 6;
        gbc.weightx = 0.12;

        if (skill.isOverall()) {
            JLabel naLabel = new JLabel("");
            naLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            this.skillsPanel.add(naLabel, gbc);
        } else {
            int progress = this.currentPlayer.getProgressToGoal(skill.getName());
            JProgressBar progressBar = createProgressBar(progress);
            this.skillsPanel.add(progressBar, gbc);
        }
    }

    private void addExpRateCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 7;
        gbc.weightx = 0.1;

        String expRateText;
        if (skill.isOverall()) {
            expRateText = "";
        } else {
            Integer expRate = this.currentPlayer.getExperienceRate(skill.getName());
            expRateText = (expRate == null) ? "N/A" : formatNumber(expRate);
        }

        JLabel expRateLabel = new JLabel(expRateText);
        expRateLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(expRateLabel, gbc);
    }

    private void addUpdateExpRateButtonCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 8;
        gbc.weightx = 0.08;

        if (skill.isOverall()) {
            JLabel emptyLabel = new JLabel();
            emptyLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            this.skillsPanel.add(emptyLabel, gbc);
        } else {
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

    private void addTimeToGoalCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 9;
        gbc.weightx = 0.12;

        String timeText;
        if (skill.isOverall()) {
            timeText = "";
        } else {
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

    private JProgressBar createProgressBar(int progress) {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress);
        progressBar.setStringPainted(true);
        progressBar.setString(progress + "%");
        progressBar.setForeground(getProgressBarColor(progress));
        progressBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        return progressBar;
    }

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

    private void addHeaderLabel(String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 150));
        this.skillsPanel.add(label, gbc);
    }

    private String formatNumber(int number) {
        return String.format("%,d", number);
    }

    private void updatePlayerInfo() {
        String username = this.currentPlayer.getUsername();
        String timestamp = this.currentPlayer.getLastRefreshedAt().toString();
        this.playerInfoLabel.setText("Player: " + username + " | " + timestamp);
    }

    private void showUpdateGoalDialog(Skill skill) {
        JDialog dialog = createUpdateGoalDialog(skill);
        dialog.setVisible(true);
    }

    private JDialog createUpdateGoalDialog(Skill skill) {
        JDialog dialog = new JDialog(this, "Set Goal for " + skill.getName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

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

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Target: "), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField targetField = new JTextField(10);
        mainPanel.add(targetField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel hintLabel = new JLabel("Level: 2-126 | Experience: 1-200,000,000");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        mainPanel.add(hintLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

    private void showUpdateExpRateDialog(Skill skill) {
        JDialog dialog = createUpdateExpRateDialog(skill);
        dialog.setVisible(true);
    }

    private JDialog createUpdateExpRateDialog(Skill skill) {
        JDialog dialog = new JDialog(this, "Set Experience Rate for " + skill.getName(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Experience Rate (exp/hour): "), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField expRateField = new JTextField(10);
        mainPanel.add(expRateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel hintLabel = new JLabel("Rate: 1-200,000,000 exp/hour");
        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        mainPanel.add(hintLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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