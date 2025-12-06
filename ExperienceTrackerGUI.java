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
        this.refreshButton.setEnabled(false); // Disabled until a player is loaded
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

        // Clear existing data
        this.skillsPanel.removeAll();
        this.skillsPanel.revalidate();
        this.skillsPanel.repaint();

        try {
            this.currentPlayer = new Player(username);
            displayCurrentPlayer();
            this.refreshButton.setEnabled(true); // Enable refresh button after successful load
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
        gbc.weightx = 0.2;
        addHeaderLabel("Skill", gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.2;
        addHeaderLabel("Rank", gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.2;
        addHeaderLabel("Level", gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.4;
        addHeaderLabel("Experience", gbc);
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
        }
    }

    private void addSkillNameCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        JLabel nameLabel = new JLabel(skill.getName().toString());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(nameLabel, gbc);
    }

    private void addRankCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 1;
        gbc.weightx = 0.2;
        JLabel rankLabel = new JLabel(formatNumber(skill.getRank()));
        rankLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(rankLabel, gbc);
    }

    private void addLevelCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        JLabel levelLabel = new JLabel(skill.formattedLevelString());
        levelLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(levelLabel, gbc);
    }

    private void addExperienceCell(Skill skill, GridBagConstraints gbc) {
        gbc.gridx = 3;
        gbc.weightx = 0.4;
        JLabel expLabel = new JLabel(formatNumber(skill.getExperience()));
        expLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
        this.skillsPanel.add(expLabel, gbc);
    }

    private void addHeaderLabel(String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 150));
        this.skillsPanel.add(label, gbc);
    }

    // Helper method to format numbers with commas
    private String formatNumber(int number) {
        return String.format("%,d", number);
    }

    // Updates player info label with information w/ username and timestamp
    private void updatePlayerInfo() {
        String username = this.currentPlayer.getUsername();
        String timestamp = this.currentPlayer.getLastRefreshedAt().toString();
        this.playerInfoLabel.setText("Player: " + username + " | " + timestamp);
    }
}