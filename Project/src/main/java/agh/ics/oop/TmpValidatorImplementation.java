package agh.ics.oop;

public class TmpValidatorImplementation implements MoveValidator{
    @Override
    public boolean canMoveTo(Vector2d position) {
        if (position.getY() <= 4 && position.getY() >= 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
