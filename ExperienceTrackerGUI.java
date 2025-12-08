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
        setSize(1000, 800);

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
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.anchor = GridBagConstraints.WEST;

        addSkillHeaders(gbc);
        addSkillRows(this.currentPlayer.getSkills(), gbc);

        this.skillsPanel.revalidate();
        this.skillsPanel.repaint();
    }

    private void addSkillHeaders(GridBagConstraints gbc) {
        gbc.gridy = 0;

        gbc.gridx = 0;
        gbc.weightx = 0.15;
        addHeaderLabel("Skill", gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.15;
        addHeaderLabel("Rank", gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.15;
        addHeaderLabel("Level", gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.15;
        addHeaderLabel("Experience", gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.15;
        addHeaderLabel("Goal", gbc);

        gbc.gridx = 5;
        gbc.weightx = 0.25;
        addHeaderLabel("Progress", gbc);
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
            addProgressCell(skill, gbc);
        }
    }

    private void addSkillNameCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.weightx = 0.15;
        JLabel nameLabel = new JLabel(skill.getName().toString());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(nameLabel, gbc);
    }

    private void addRankCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.weightx = 0.15;
        JLabel rankLabel = new JLabel(formatNumber(skill.getRank()));
        rankLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(rankLabel, gbc);
    }

    private void addLevelCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 2;
        gbc.weightx = 0.15;
        JLabel levelLabel = new JLabel(skill.formattedLevelString());
        levelLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(levelLabel, gbc);
    }

    private void addExperienceCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 3;
        gbc.weightx = 0.15;
        JLabel expLabel = new JLabel(formatNumber(skill.getExperience()));
        expLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(expLabel, gbc);
    }

    private void addGoalCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 4;
        gbc.weightx = 0.15;

        String goalText;
        if (skill.isOverall()) {
            goalText = "N/A";
        } else {
            Goal goal = this.currentPlayer.getGoal(skill.getName());
            goalText = goal.toString();
        }

        JLabel goalLabel = new JLabel(goalText);
        goalLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(goalLabel, gbc);
    }

    private void addProgressCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 5;
        gbc.weightx = 0.25;

        if (skill.isOverall()) {
            JLabel naLabel = new JLabel("N/A");
            naLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            this.skillsPanel.add(naLabel, gbc);
        } else {
            int progress = this.currentPlayer.getProgressToGoal(skill.getName());
            JProgressBar progressBar = createProgressBar(progress);
            this.skillsPanel.add(progressBar, gbc);
        }
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
}