package connect5plus;

import javax.swing.*;
import java.awt.*;

/**
 * GUI版 Connect 5 Plus のメインウィンドウ
 * @author 羽井出
 */
public class GameFrame extends JFrame {
    private static final int COMPUTER_MOVE_DELAY_MS = 400;

    /** コンピュータとの対戦かどうか */
    private final boolean vsComputer;
    /** 進行中の試合 */
    private GuiGame game;
    /** 盤面を表示するパネル */
    private final BoardPanel boardPanel;
    /** 手番・勝敗などの表示 */
    private final JLabel playerStatusLabel = new JLabel("", SwingConstants.CENTER);
    /** 盤面操作に関するメッセージの表示 */
    private final JLabel boardStatusLabel = new JLabel("", SwingConstants.CENTER);
    /** コンピュータの手番を遅延実行するタイマー */
    private Timer computerMoveTimer;

    /**
     * 2人対戦のゲームウィンドウを生成して表示する
     */
    public GameFrame() {
        this(false);
    }

    /**
     * ゲームウィンドウを生成して表示する
     * @param vsComputer コンピュータとの対戦の場合 true
     */
    public GameFrame(boolean vsComputer) {
        this.vsComputer = vsComputer;
        game = createGame();
        boardPanel = new BoardPanel(game, this::handleMove);

        setTitle(vsComputer ? "Connect 5+ vs CPU" : "Connect 5+");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 8));
        updateStatus();

        playerStatusLabel.setFont(playerStatusLabel.getFont().deriveFont(Font.BOLD, 28f));
        playerStatusLabel.setBorder(BorderFactory.createEmptyBorder(12, 8, 4, 8));
        boardStatusLabel.setFont(boardStatusLabel.getFont().deriveFont(Font.BOLD));
        boardStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));
        reserveBoardStatusSpace();

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        playerStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        boardStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        north.add(playerStatusLabel);
        north.add(boardStatusLabel);
        add(north, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        boardPanel.setMinimumSize(boardPanel.getPreferredSize());

        JButton resetButton = new JButton("新しいゲーム");
        resetButton.addActionListener(e -> resetGame());
        JPanel south = new JPanel();
        south.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        south.add(resetButton);
        add(south, BorderLayout.SOUTH);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * 試合インスタンスを生成する
     * @return 新しい試合
     */
    private GuiGame createGame() {
        return vsComputer ? new GuiSemiAutoGame() : new GuiGame();
    }

    /**
     * メッセージラベルの表示領域を固定し、文言の有無で盤面サイズが変わらないようにする
     */
    private void reserveBoardStatusSpace() {
        FontMetrics metrics = boardStatusLabel.getFontMetrics(boardStatusLabel.getFont());
        int height = metrics.getHeight();
        int width = Math.max(
                metrics.stringWidth("この列は満杯です"),
                metrics.stringWidth("そこには置けません")
        );
        Dimension size = new Dimension(width, height);
        boardStatusLabel.setPreferredSize(size);
        boardStatusLabel.setMinimumSize(size);
        boardStatusLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    /**
     * 試合を初期状態に戻す
     */
    private void resetGame() {
        stopComputerMoveTimer();
        game = createGame();
        boardPanel.setGame(game);
        boardStatusLabel.setText("");
        updateStatus();
        boardPanel.requestFocusInWindow();
    }

    /**
     * 列がクリックされたときの処理
     * @param x クリックされた列の x 座標
     */
    private void handleMove(int x) {
        if (!game.acceptsHumanInput()) {
            return;
        }

        if (!game.makeMove(x)) {
            boardStatusLabel.setText(game.getBoard().canDrop(x)
                    ? "そこには置けません"
                    : "この列は満杯です");
            return;
        }

        afterMove();
    }

    /**
     * 手を進めたあとの盤面更新とコンピュータの手番処理
     */
    private void afterMove() {
        boardStatusLabel.setText("");
        boardPanel.repaint();
        updateStatus();
        scheduleComputerMove();
    }

    /**
     * コンピュータの手番であれば、少し待ってから自動で着手する
     */
    private void scheduleComputerMove() {
        if (!(game instanceof GuiSemiAutoGame semi) || !semi.isComputerTurn()) {
            return;
        }

        stopComputerMoveTimer();
        computerMoveTimer = new Timer(COMPUTER_MOVE_DELAY_MS, e -> {
            semi.makeComputerMove();
            boardPanel.repaint();
            updateStatus();
        });
        computerMoveTimer.setRepeats(false);
        computerMoveTimer.start();
    }

    /**
     * コンピュータ着手用タイマーを停止する
     */
    private void stopComputerMoveTimer() {
        if (computerMoveTimer != null && computerMoveTimer.isRunning()) {
            computerMoveTimer.stop();
        }
    }

    /**
     * 手番・勝敗の表示を現在の試合状態に合わせて更新する
     */
    private void updateStatus() {
        if (game.getWinner() > 0) {
            StringBuilder sb = new StringBuilder().repeat('！', game.getWinningPositions().size() - 5);
            int winner = game.getWinner();
            playerStatusLabel.setText(
                    "Player" + winner
                            + " (" + Token.typeToString(winner) + ") の勝ち！"
                            + sb
            );
        } else if (game.getBoard().isFull()) {
            playerStatusLabel.setText("引き分け");
        } else {
            int player = game.getCurrentPlayer();
            playerStatusLabel.setText(
                    "Player" + player
                            + " (" + Token.typeToString(player) + ") の番"
            );
        }
    }
}
