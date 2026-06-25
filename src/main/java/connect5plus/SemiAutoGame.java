package connect5plus;

import java.util.Random;

public class SemiAutoGame extends Game{
    @Override
    protected int selectPosition() {
        if (getPlayer() != 1){
            return new Random().nextInt(getBoard().boardSize);
        } else {
            return super.selectPosition();
        }
    }
}
