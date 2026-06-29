package connect5plus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.function.IntConsumer;

/**
 * 盤面を描画し、マウス操作を受け付けるパネル
 * @author 羽井出
 */
public class BoardPanel extends JPanel {
    /** セルのデフォルトサイズ（ピクセル） */
    private static final int DEFAULT_CELL = 60;
    /** 落下位置プレビューの透明度 */
    private static final float PREVIEW_ALPHA = 0.45f;

    /** 表示・操作対象の試合 */
    private GuiGame game;
    /** 列がクリックされたときに呼び出す処理 */
    private final IntConsumer onColumnClick;
    /** マウスが載っている列（未選択の場合は -1） */
    private int hoverColumn = -1;

    /**
     * 盤面パネルを生成する
     * @param initialGame 表示する試合
     * @param onColumnClick 列がクリックされたときに x 座標を渡して呼び出す処理
     */
    public BoardPanel(GuiGame initialGame, IntConsumer onColumnClick) {
        this.game = initialGame;
        this.onColumnClick = onColumnClick;
        int boardSize = initialGame.getBoard().boardSize;
        setPreferredSize(new Dimension(boardSize * DEFAULT_CELL, boardSize * DEFAULT_CELL));
        setBackground(new Color(0x4A90D9));

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHoverColumn(-1);
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
    }

    /**
     * 左クリックを処理し、盤面上の列が押された場合にコールバックを呼び出す
     * @param e マウスイベント
     */
    private void handleMousePressed(MouseEvent e) {
        if (!game.acceptsHumanInput() || e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        int x = columnAt(e.getX(), e.getY());
        if (x >= 0) {
            onColumnClick.accept(x);
        }
    }

    /**
     * マウス移動に応じてホバー中の列を更新する
     * @param e マウスイベント
     */
    private void handleMouseMoved(MouseEvent e) {
        updateHoverColumn(e.getX(), e.getY());
    }

    /**
     * 表示する試合を差し替える（新しいゲーム開始時など）
     * @param game 新しい試合
     */
    public void setGame(GuiGame game) {
        this.game = game;
        hoverColumn = -1;
        repaint();
    }

    /**
     * マウス位置からホバー中の列を更新する
     * @param pixelX マウスの x 座標（ピクセル）
     * @param pixelY マウスの y 座標（ピクセル）
     */
    private void updateHoverColumn(int pixelX, int pixelY) {
        if (!game.acceptsHumanInput()) {
            setHoverColumn(-1);
            return;
        }
        setHoverColumn(columnAt(pixelX, pixelY));
    }

    /**
     * ホバー中の列を設定し、変更があれば再描画する
     * @param column 列の x 座標（未選択の場合は -1）
     */
    private void setHoverColumn(int column) {
        if (hoverColumn != column) {
            hoverColumn = column;
            repaint();
        }
    }

    /**
     * 現在のパネルサイズに合わせた1マスの辺の長さを返す
     * @return セルサイズ（ピクセル）
     */
    private int cellSize() {
        int n = game.getBoard().boardSize;
        if (n == 0) {
            return DEFAULT_CELL;
        }
        return Math.max(1, Math.min(getWidth(), getHeight()) / n);
    }

    /**
     * 盤面左上の x 座標（ピクセル）を返す
     * @return 盤面の左端の x 座標
     */
    private int boardOffsetX() {
        int n = game.getBoard().boardSize;
        return (getWidth() - cellSize() * n) / 2;
    }

    /**
     * 盤面左上の y 座標（ピクセル）を返す
     * @return 盤面の上端の y 座標
     */
    private int boardOffsetY() {
        int n = game.getBoard().boardSize;
        return (getHeight() - cellSize() * n) / 2;
    }

    /**
     * ピクセル座標から盤面上の列（x 座標）を求める
     * @param pixelX マウスの x 座標（ピクセル）
     * @param pixelY マウスの y 座標（ピクセル）
     * @return 列の x 座標。盤面外の場合は -1
     */
    private int columnAt(int pixelX, int pixelY) {
        int n = game.getBoard().boardSize;
        int cell = cellSize();
        int offsetX = boardOffsetX();
        int offsetY = boardOffsetY();
        int boardPixels = cell * n;

        if (pixelX < offsetX || pixelX >= offsetX + boardPixels
                || pixelY < offsetY || pixelY >= offsetY + boardPixels) {
            return -1;
        }

        int x = (pixelX - offsetX) / cell;
        return game.getBoard().isInsideBoard(x) ? x : -1;
    }

    /**
     * 指定位置に駒を描画する
     * @param g2 描画コンテキスト
     * @param x 列の x 座標
     * @param y 行の y 座標
     * @param cell セルサイズ（ピクセル）
     * @param offsetX 盤面左端の x 座標（ピクセル）
     * @param offsetY 盤面上端の y 座標（ピクセル）
     * @param player プレイヤー番号
     * @param alpha 透明度（0.0 ～ 1.0）
     */
    private void drawToken(Graphics2D g2, int x, int y, int cell, int offsetX, int offsetY, int player, float alpha) {
        int px = offsetX + x * cell;
        int py = offsetY + y * cell;
        Composite previous = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(player == 1 ? Color.BLACK : Color.WHITE);
        g2.fillOval(px + 8, py + 8, cell - 16, cell - 16);
        if (alpha >= 1.0f) {
            g2.setColor(player == 1 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
            g2.drawOval(px + 8, py + 8, cell - 16, cell - 16);
        }
        g2.setComposite(previous);
    }

    /**
     * 盤面・駒・落下位置プレビューを描画する
     * @param g 描画コンテキスト
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Board board = game.getBoard();
        int n = board.boardSize;
        int cell = cellSize();
        int offsetX = boardOffsetX();
        int offsetY = boardOffsetY();
        Set<Position> winningPositions = game.getWinningPositions();

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                int px = offsetX + x * cell;
                int py = offsetY + y * cell;
                Position pos = new Position(x, y);

                g2.setColor(new Color(0x357ABD));
                g2.fillOval(px + 2, py + 2, cell - 4, cell - 4);

                if (winningPositions.contains(pos)) {
                    g2.setColor(Color.YELLOW);
                    g2.fillOval(px + 4, py + 4, cell - 8, cell - 8);
                }

                Token t = board.getSpace(x, y);
                if (t != null) {
                    drawToken(g2, x, y, cell, offsetX, offsetY, t.getType(), 1.0f);
                }
            }
        }

        if (game.acceptsHumanInput() && hoverColumn >= 0) {
            int dropRow = game.getBoard().getDropRow(hoverColumn);
            if (dropRow >= 0) {
                drawToken(g2, hoverColumn, dropRow, cell, offsetX, offsetY,
                        game.getCurrentPlayer(), PREVIEW_ALPHA);
            }
        }
    }
}
