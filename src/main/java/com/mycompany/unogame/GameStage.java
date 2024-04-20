package com.mycompany.unogame;

import java.util.HashMap;
import java.util.Map;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.SwingConstants;

public class GameStage extends javax.swing.JFrame {

    private final Game game;
    private List<Player> players;
    private TimerWorker worker;
    private final Music musicThread;
    private Map<String, JLabel> playerLabels;
    private EventLabel el = new EventLabel();
    private class TimerWorker extends SwingWorker<Void, String> {
        private int seconds;

        @Override
        protected Void doInBackground() throws Exception {
            while (!isCancelled()) {
                seconds++;
                String time = String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
                publish(time);
                Thread.sleep(1000);
            }
            return null;
        }

        @Override
        protected void process(java.util.List<String> chunks) {
            jLabel4.setText(chunks.get(chunks.size() - 1));
        }

        @Override
        protected void done() {
            worker = null;
        }
    }

    public GameStage(int playersCount, List<Integer> scores, String playerIcon, String myName) {
        musicThread = new Music("/music.wav");
        musicThread.run();
        initComponents();
        customComponents();
        jButton11.setEnabled(false);
        jButton10.setEnabled(false);
        worker = new TimerWorker();
        worker.execute();

        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                worker.cancel(true);
                musicThread.stopMusic();
            }
        });

        MouseAdapter listener = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                var c = (JLabel) e.getSource();
                Player me = game.getPlayers().get(0);
                me.setUno(false);
                jButton10.setEnabled(false);
                JLabel comp = (JLabel) e.getComponent();
                String temp = comp.getName();
                JLabel dropTarg = jLabel1;
                String tempTarg = dropTarg.getName();
                String[] parts = temp.split("_");
                String SourceColor = parts[0].split("/")[1];
                String SourceValue = parts[1].split(".png")[0];
                String[] partsTarg = tempTarg.split("_");
                String TargetColor = partsTarg[0].split("/")[1];
                String TargetValue = partsTarg[1].split(".png")[0];

                if (TargetColor.equals("NONE")) {
                    TargetColor = game.getChangedColor();
                }

                if (SourceColor.equals(TargetColor) || SourceValue.equals(TargetValue) || SourceColor.equals("NONE")) {
                    switch (SourceValue) {
                        case "TAKEFOUR" -> {
                            Object[] possibilities = {"RED", "YELLOW", "GREEN", "BLUE"};
                            String s = (String) JOptionPane.showInputDialog(null, "CHOOSE COLOR...", "CHANGING COLOR", JOptionPane.PLAIN_MESSAGE, null, possibilities, "RED");
                            if (s == null) {
                                s = "RED";
                            }
                            SourceColor = s;

                            PlayerLabelStatus.inactivePlayerLabel(playerLabels, me.getId());
                            Player player = game.nextPlayer();
                            el.eventLabel(playerLabels.get("event"), "+4", player);
                            for (int i = 0; i < 4; i++) {
                                player.TakeCard(game.getDeck());
                            }
                            player.repaintCards();
                        }
                        case "CHANGECOLOR" -> {
                            Object[] possibilities = {"RED", "YELLOW", "GREEN", "BLUE"};
                            String s = (String) JOptionPane.showInputDialog(null, "CHOOSE COLOR...", "CHANGING COLOR", JOptionPane.PLAIN_MESSAGE, null, possibilities, "RED");
                            if (s == null) {
                                s = "RED";
                            }
                            SourceColor = s;
                        }
                        case "TAKETWO" -> {
                            PlayerLabelStatus.inactivePlayerLabel(playerLabels, me.getId());
                            Player player = game.nextPlayer();
                            el.eventLabel(playerLabels.get("event"), "+2", player);
                            for (int i = 0; i < 2; i++) {
                                player.TakeCard(game.getDeck());
                            }
                            player.repaintCards();
                        }
                        case "REVERSE" -> {
                            el.eventLabel(playerLabels.get("event"), "REVERSE", me);
                            game.setReverse(!game.isReverse());
                            if (game.isReverse()) {
                                jLabel10.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/currs.png")).getImage().getScaledInstance(51, 51, Image.SCALE_SMOOTH)));
                            } else {
                                jLabel10.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/currs_reversed.png")).getImage().getScaledInstance(51, 51, Image.SCALE_SMOOTH)));
                            }
                        }
                        case "SKIP" -> {
                            game.nextPlayer();
                            el.eventLabel(playerLabels.get("event"), "SKIP", game.getCurrentPlayer());
                        }
                    }
                    dropTarg.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(temp)).getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH)));
                    dropTarg.setName(temp);
                    game.setChangedColor(SourceColor);
                    PlayerLabelStatus.inactivePlayerLabel(playerLabels, me.getId());
                    game.nextPlayer();
                    me.setMadeMove(true);
                    for (int k = 0; k < me.getCards().size(); k++) {
                        me.getCardsImg().get(k).setEnabled(false);
                    }
                    jPanel1.repaint();
                }

                if (me.isMadeMove()) {
                    game.settingBgColor(game.getChangedColor());
                    jPanel1.remove(c);
                    jPanel1.repaint();
                    for (int i = 0; i < me.getCards().size(); i++) {
                        me.getCardsImg().get(i).setEnabled(false);
                        if (c == me.getCardsImg().get(i)) {
                            me.getCards().remove(i);
                            me.getCardsImg().remove(i);
                        }
                    }
                    me.repaintCards();
                    if (me.getCards().size() == 1) {
                        jButton10.setEnabled(true);
                    }

                    if (me.getCards().isEmpty()) {
                        game.SummingUpResults(me);
                    }
                    jButton11.setEnabled(false);
                    jButton2.setEnabled(false);
                    me.setMadeMove(false);
                    game.gameProcess(playerLabels);
                }
            }
        };
        
        game = new Game(playersCount , myName, playerIcon, scores, jLabel1, jPanel1, jLabel3, jButton2,jButton10, jLabel10, listener, musicThread);
        game.settingBgColor(game.getChangedColor().split("_")[0]);
        
        if(myName != null && !myName.isEmpty()){
            player1Name.setText(myName);
        }else{
            player1Name.setText("New player");
        }

        String temp = "/" + game.getChangedColor() + ".png";
        jLabel1.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource(temp)).getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH)));
        jLabel1.setTransferHandler(new TransferHandler("icon"));
        jLabel1.setName(temp);
        game.getDeck().getCards().remove(game.getDeck().getCount());
        initPlayers(game, playersCount);
        jLabel6.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource(playerIcon)).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        jLabel10.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/currs.png")).getImage().getScaledInstance(51, 51, Image.SCALE_SMOOTH)));
        jButton2.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/default1.png")).getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH)));
        setLocationRelativeTo(null);
    }

    private GameStage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        player3Name = new javax.swing.JLabel();
        player3Icon = new javax.swing.JLabel();
        player2Icon = new javax.swing.JLabel();
        player4Icon = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        player1Name = new javax.swing.JLabel();
        player4Name = new javax.swing.JLabel();
        player2Name = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();

        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(252, 252, 252)
                .addComponent(jLabel2)
                .addContainerGap(254, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(159, 159, 159)
                .addComponent(jLabel2)
                .addContainerGap(186, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(61, 87, 126));
        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 720));

        jButton1.setBackground(new java.awt.Color(208, 150, 0));
        jButton1.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("BACK");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.setBorderPainted(false);
        jButton1.setDefaultCapable(false);
        jButton1.setFocusPainted(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setContentAreaFilled(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 204, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jButton10.setBackground(new java.awt.Color(208, 150, 0));
        jButton10.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("UNO!");
        jButton10.setBorder(null);
        jButton10.setBorderPainted(false);
        jButton10.setDefaultCapable(false);
        jButton10.setFocusPainted(false);
        jButton10.setMaximumSize(new java.awt.Dimension(29, 16));
        jButton10.setMinimumSize(new java.awt.Dimension(29, 16));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(208, 150, 0));
        jButton11.setFont(new java.awt.Font("Segoe UI", 0, 30)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("SKIP");
        jButton11.setBorder(null);
        jButton11.setBorderPainted(false);
        jButton11.setDefaultCapable(false);
        jButton11.setFocusPainted(false);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        player3Name.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        player3Name.setForeground(new java.awt.Color(255, 187, 11));
        player3Name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        player3Name.setText("PLAYER 1");

        player3Icon.setBackground(new java.awt.Color(1.0f,1.0f,1.0f,.0f));
        player3Icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        player3Icon.setOpaque(true);

        player2Icon.setBackground(new java.awt.Color(1.0f,1.0f,1.0f,.0f));
        player2Icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        player2Icon.setOpaque(true);

        player4Icon.setBackground(player2Icon.getBackground());
        player4Icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        player4Icon.setOpaque(true);

        jLabel6.setBackground(player2Icon.getBackground());
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setOpaque(true);

        player1Name.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        player1Name.setForeground(player3Name.getForeground());
        player1Name.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        player1Name.setText("MY NAME");

        player4Name.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        player4Name.setForeground(player3Name.getForeground());
        player4Name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        player4Name.setText("PLAYER 3");

        player2Name.setFont(new java.awt.Font("Times New Roman", 0, 38));
        player2Name.setForeground(player3Name.getForeground());
        player2Name.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        player2Name.setText("PLAYER 2");

        jButton3.setBackground(new java.awt.Color(1.0f,1.0f,1.0f,.0f));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jLabel3.setOpaque(true);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel4.setForeground(player3Name.getForeground());
        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(player1Name, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(135, 135, 135)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(178, 178, 178)
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(61, 61, 61)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(56, 56, 56)
                                    .addComponent(player2Icon, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(16, 16, 16))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(player3Icon, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(player2Name, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(player3Name, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(139, 139, 139)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(743, 743, 743)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(player4Name, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(player4Icon, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(13, 13, 13)))
                                .addGap(12, 12, 12)))))
                .addGap(16, 16, 16))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(36, 36, 36)
                                        .addComponent(player4Icon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(player2Icon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(player2Name))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(67, 67, 67)
                                        .addComponent(player3Icon, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(player3Name)
                            .addComponent(player4Name, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(46, 46, 46)
                        .addComponent(player1Name, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(!Music.isTurnedOff()){
            musicThread.pauseMusic();
            jButton3.setIcon(pauseIcon);
        }else{
            musicThread.resumeMusic();
            jButton3.setIcon(resumeIcon);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        PlayerLabelStatus.inactivePlayerLabel(playerLabels, game.getCurrentPlayer().getId());
        game.nextPlayer();
        for(int k = 0; k < players.get(0).getCards().size(); k++){
            players.get(0).getCardsImg().get(k).setEnabled(false);
        }
        game.getPlayers().get(0).setMadeMove(true);
        game.gameProcess(playerLabels);
        players.get(0).setMadeMove(false);
        jButton11.setEnabled(false);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        players.get(0).setUno(true);
        jButton10.setEnabled(false);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        players.get(0).TakeCard(game.getDeck());
        players.get(0).repaintCards();
        jPanel1.repaint();
        jButton2.setEnabled(false);
        jButton11.setEnabled(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog (null, "Exit to main menu?","Warning",dialogButton);
        if(dialogResult == JOptionPane.YES_OPTION){
            this.dispose();
            new Menu().setVisible(true);
            musicThread.stopMusic();
        }
        // Saving code here
    }//GEN-LAST:event_jButton1ActionPerformed

    ImageIcon resumeIcon = new ImageIcon(new ImageIcon(getClass().getResource("/resume_icon.png")).getImage().getScaledInstance(51, 51, Image.SCALE_SMOOTH));
    ImageIcon pauseIcon = new ImageIcon(new ImageIcon(getClass().getResource("/pause_icon.png")).getImage().getScaledInstance(51, 51, Image.SCALE_SMOOTH));
    private void initPlayers(Game game, int count){
        player2Icon.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/player2.png")).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        player2Name.setText("MISHA");
        switch (count) {
            case 2 -> {
                playerLabels.put("player2", player2Name);
                player3Icon.setVisible(false);
                player3Name.setVisible(false);
                player4Icon.setVisible(false);
                player4Name.setVisible(false);
            }
            case 3 -> {
                playerLabels.put("player2", player3Name);
                playerLabels.put("player3", player2Name);
                player3Icon.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/player3.png")).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
                player3Icon.setVisible(true);
                player3Name.setText("IVAN");
                player3Name.setVisible(true);
                player4Icon.setVisible(false);
                player4Name.setVisible(false);
            }
            case 4 -> {
                playerLabels.put("player2", player3Name);
                playerLabels.put("player3", player2Name);
                playerLabels.put("player4", player4Name);
                player3Icon.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/player3.png")).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
                player3Icon.setVisible(true);
                player3Name.setText("IVAN");
                player3Name.setVisible(true);
                player4Icon.setIcon(new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/player4.png")).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
                player4Icon.setVisible(true);
                player4Name.setText("MASHA");
                player4Name.setVisible(true);
            }
        }

        players = game.getPlayers();
        for ( var player : players){
            player.paintCards();
        }
    }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameStage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameStage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameStage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameStage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameStage().setVisible(true);
                
            }
        });
    }

    public void customComponents() {
       Font fredokaOne = CustomFont.getCustomFont(24);
       player2Name.setFont(fredokaOne);
       player3Name.setFont(fredokaOne);
       player4Name.setFont(fredokaOne);
       player1Name.setFont(fredokaOne);
       jLabel4.setFont(jButton1.getFont().deriveFont((float) 30));
       jButton1.setFont(fredokaOne);
       jButton10.setFont(fredokaOne);
       jButton11.setFont(fredokaOne);
       jButton2.setFont(fredokaOne);
       if(Music.isTurnedOff()){
            jButton3.setIcon(pauseIcon);
        }else{
            jButton3.setIcon(resumeIcon);
        }
       UIManager.put("OptionPane.messageFont", jButton1.getFont().deriveFont((float) 14));
       UIManager.put("OptionPane.buttonFont", jButton1.getFont().deriveFont((float) 14));
       playerLabels = new HashMap<>();
       playerLabels.put("player1", player1Name);
       PlayerLabelStatus.activePlayerLabel(playerLabels,1);
       JPanel glassPane = new JPanel();
       this.setGlassPane(glassPane);
       glassPane.setOpaque(false);
       glassPane.setLayout(null);
       JLabel eventCard = new JLabel("event", SwingConstants.CENTER);
       eventCard.setFont(jButton1.getFont().deriveFont((float) 72));
       eventCard.setForeground(Color.GREEN);
       eventCard.setVisible(false);
       playerLabels.put("event", eventCard);
       glassPane.add(eventCard);
       glassPane.setVisible(true);
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel player1Name;
    private javax.swing.JLabel player2Icon;
    private javax.swing.JLabel player2Name;
    private javax.swing.JLabel player3Icon;
    private javax.swing.JLabel player3Name;
    private javax.swing.JLabel player4Icon;
    private javax.swing.JLabel player4Name;
    // End of variables declaration//GEN-END:variables
}
