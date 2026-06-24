package connect5plus;

import java.util.Random;

public class AutoGame extends Game{
    @Override
    protected int selectPosition() {

        return new Random().nextInt(getBoard().boardSize);
    }
}
