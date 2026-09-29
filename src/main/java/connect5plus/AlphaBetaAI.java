package connect5plus;

import java.util.List;

/**
 * アルファベータ法で Connect 5 Plus の最善手を探索するクラス
 * @author 羽井出
 */
public class AlphaBetaAI {
    private static final int WIN_SCORE = 1_000_000;

    private static final int[][] LINE_SCORES = {
            {0, 0, 1, 10, 100, WIN_SCORE},
    };

    private final int aiPlayer;
    private final int searchDepth;

    /**
     * AI プレイヤーを指定してインスタンスを生成する（探索深さ 5）
     * @param aiPlayer AI のプレイヤー番号
     */
    public AlphaBetaAI(int aiPlayer) {
        this(aiPlayer, 5);
    }

    /**
     * AI プレイヤーと探索深さを指定してインスタンスを生成する
     * @param aiPlayer AI のプレイヤー番号
     * @param searchDepth 探索深さ
     */
    public AlphaBetaAI(int aiPlayer, int searchDepth) {
        this.aiPlayer = aiPlayer;
        this.searchDepth = searchDepth;
    }

    /**
     * 現在の盤面から AI の最善手を探索する
     * @param board 盤面
     * @param playerToMove 次に打つプレイヤー
     * @return 最善手の x 座標。見つからない場合 -1
     */
    public int findBestMove(Board board, int playerToMove) {
        SearchState root = SearchState.from(board, playerToMove);
        List<Integer> moves = root.legalMoves();
        if (moves.isEmpty()) {
            return -1;
        }

        int opponent = opponentOf(playerToMove);

        for (int x : moves) {
            SearchState child = root.applyMove(x);
            if (child != null && child.winner == playerToMove) {
                return x;
            }
        }

        for (int x : moves) {
            SearchState blockTest = SearchState.from(board, opponent);
            SearchState blockResult = blockTest.applyMove(x);
            if (blockResult != null && blockResult.winner == opponent) {
                return x;
            }
        }

        int bestX = moves.getFirst();
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (int x : moves) {
            SearchState child = root.applyMove(x);
            if (child == null) {
                continue;
            }
            int score = alphaBeta(child, searchDepth - 1, alpha, beta, false);
            if (score > bestScore) {
                bestScore = score;
                bestX = x;
            }
            alpha = Math.max(alpha, score);
        }

        return bestX;
    }

    /**
     * アルファベータ法で局面を評価する
     * @param state 探索状態
     * @param depth 残り探索深さ
     * @param alpha アルファ値
     * @param beta ベータ値
     * @param maximizing AI 側の最大化手番か
     * @return 評価値
     */
    private int alphaBeta(SearchState state, int depth, int alpha, int beta, boolean maximizing) {
        if (state.isTerminal()) {
            return terminalScore(state);
        }
        if (depth == 0) {
            return evaluate(state.board);
        }

        List<Integer> moves = state.legalMoves();
        if (moves.isEmpty()) {
            return evaluate(state.board);
        }

        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int x : moves) {
                SearchState child = state.applyMove(x);
                if (child == null) {
                    continue;
                }
                int eval = alphaBeta(child, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        }

        int minEval = Integer.MAX_VALUE;
        for (int x : moves) {
            SearchState child = state.applyMove(x);
            if (child == null) {
                continue;
            }
            int eval = alphaBeta(child, depth - 1, alpha, beta, true);
            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return minEval;
    }

    /**
     * 終局局面の評価値を返す
     * @param state 探索状態
     * @return 評価値
     */
    private int terminalScore(SearchState state) {
        if (state.winner == aiPlayer) {
            return WIN_SCORE;
        }
        if (state.winner != 0) {
            return -WIN_SCORE;
        }
        return 0;
    }

    /**
     * 非終局局面を評価する（AI 視点で正の値ほど有利）
     * @param board 盤面
     * @return 評価値
     */
    private int evaluate(Board board) {
        int aiScore = scoreBoard(board, aiPlayer);
        int opponentScore = scoreBoard(board, opponentOf(aiPlayer));
        int centerBonus = centerColumnBonus(board);
        return aiScore - opponentScore + centerBonus;
    }

    /**
     * 指定プレイヤーの盤面スコアを計算する
     * @param board 盤面
     * @param player プレイヤー番号
     * @return スコア
     */
    private int scoreBoard(Board board, int player) {
        int score = 0;
        int opponent = opponentOf(player);
        int size = board.boardSize;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                score += scoreLineWindow(board, x, y, 1, 0, player, opponent);
                score += scoreLineWindow(board, x, y, 0, 1, player, opponent);
                score += scoreLineWindow(board, x, y, 1, 1, player, opponent);
                score += scoreLineWindow(board, x, y, 1, -1, player, opponent);
            }
        }

        return score;
    }

    /**
     * 長さ 5 の連続マス窓を評価する
     */
    private int scoreLineWindow(Board board, int startX, int startY, int dx, int dy, int player, int opponent) {
        int size = board.boardSize;
        int playerCount = 0;
        int opponentCount = 0;
        int emptyCount = 0;

        for (int i = 0; i < 5; i++) {
            int x = startX + dx * i;
            int y = startY + dy * i;
            if (!board.isInsideBoard(x, y)) {
                return 0;
            }

            Token token = board.getSpace(x, y);
            if (token == null) {
                emptyCount++;
            } else if (token.type() == player) {
                playerCount++;
            } else if (token.type() == opponent) {
                opponentCount++;
            }
        }

        if (playerCount > 0 && opponentCount > 0) {
            return 0;
        }
        return lineScore(playerCount);
    }

    /**
     * 連続駒数に対応するスコアを返す
     */
    private int lineScore(int consecutive) {
        return LINE_SCORES[0][Math.min(consecutive, 5)];
    }

    /**
     * 中央列へのボーナスを計算する
     */
    private int centerColumnBonus(Board board) {
        int bonus = 0;
        int center = board.boardSize / 2;
        for (int x = 0; x < board.boardSize; x++) {
            if (board.canDrop(x)) {
                bonus += (center + 1 - Math.abs(x - center));
            }
        }
        return bonus;
    }

    private int opponentOf(int player) {
        return player == 1 ? 2 : 1;
    }
}
