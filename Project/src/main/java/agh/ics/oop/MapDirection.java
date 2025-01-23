package agh.ics.oop;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    NORTHWEST,
    SOUTH,
    SOUTHEAST,
    SOUTHWEST,
    WEST,
    EAST;

    public String toString() {
        return switch (this) {
            case NORTH -> "Polnoc";
            case SOUTH -> "Poludnie";
            case WEST -> "Zachod";
            case EAST -> "Wschod";
            case NORTHEAST -> "Polnocny-wschod";
            case NORTHWEST -> "Polnocny-zachod";
            case SOUTHEAST -> "Poludniowy-wschod";
            case SOUTHWEST -> "Poludniowy-zachod";
        };
    }
    public MapDirection new_direction(int direction) {
        return switch (this) {
            case NORTH -> switch (direction) {
                case 0 -> NORTH;
                case 1 -> NORTHEAST;
                case 2 -> EAST;
                case 3 -> SOUTHEAST;
                case 4 -> SOUTH;
                case 5 -> SOUTHWEST;
                case 6 -> WEST;
                case 7 -> NORTHWEST;
                default -> throw new IllegalArgumentException(direction + " to nie poprawna sygnatura ruchu");
            };
            case NORTHEAST -> switch (direction) {
                case 0 -> NORTHEAST;
                case 1 -> EAST;
                case 2 -> SOUTHEAST;
                case 3 -> SOUTH;
                case 4 -> SOUTHWEST;
                case 5 -> WEST;
                case 6 -> NORTHWEST;
                case 7 -> NORTH;
                default -> throw new IllegalArgumentException(direction + " to nie poprawna sygnatura ruchu");
            };
            case EAST -> switch (direction) {
                case 0 -> EAST;
                case 1 -> SOUTHEAST;
                case 2 -> SOUTH;
                case 3 -> SOUTHWEST;
                case 4 -> WEST;
                case 5 -> NORTHWEST;
                case 6 -> NORTH;
                case 7 -> NORTHEAST;
                default -> throw new IllegalArgumentException(direction + " to nie poprawna sygnatura ruchu");
            };
            case SOUTHEAST -> switch (direction) {
                case 0 -> SOUTHEAST;
                case 1 -> SOUTH;
                case 2 -> SOUTHWEST;
                case 3 -> WEST;
                case 4 -> NORTHWEST;
                case 5 -> NORTH;
                case 6 -> NORTHEAST;
                case 7 -> EAST;
                default -> throw new IllegalArgumentException(direction + " to nie poprawna sygnatura ruchu");
            };
            case SOUTH -> switch (direction) {
                case 0 -> SOUTH;
                case 1 -> SOUTHWEST;
                case 2 -> WEST;
                case 3 -> NORTHWEST;
                case 4 -> NORTH;
                case 5 -> NORTHEAST;
                case 6 -> EAST;
                case 7 -> SOUTHEAST;
                default -> throw new IllegalArgumentException(direction + " to nie poprawna sygnatura ruchu");
            };
            case SOUTHWEST -> switch (direction) {
                case 0 -> SOUTHWEST;
                case 1 -> WEST;
                case 2 -> NORTHWEST;
                case 3 -> NORTH;
                case 4 -> NORTHEAST;
                case 5 -> EAST;
                case 6 -> SOUTHEAST;
                case 7 -> SOUTH;
                default -> throw new IllegalArgumentException(direction + " to nie poprawna sygnatura ruchu");
            };
            case WEST -> switch (direction) {
                case 0 -> WEST;
                case 1 -> NORTHWEST;
                case 2 -> NORTH;
                case 3 -> NORTHEAST;
                case 4 -> EAST;
                case 5 -> SOUTHEAST;
                case 6 -> SOUTH;
                case 7 -> SOUTHWEST;
                default -> throw new IllegalArgumentException(direction + " to nie poprawna sygnatura ruchu");
            };
            case NORTHWEST -> switch (direction) {
                case 0 -> NORTHWEST;
                case 1 -> NORTH;
                case 2 -> NORTHEAST;
                case 3 -> EAST;
                case 4 -> SOUTHEAST;
                case 5 -> SOUTH;
                case 6 -> SOUTHWEST;
                case 7 -> WEST;
                default -> throw new IllegalArgumentException(direction + " to nie poprawna sygnatura ruchu");
            };
        };
    }

    public Vector2d toUnitVector()  {
        return switch (this){
            case NORTH -> new Vector2d(0,1);
            case NORTHEAST -> new Vector2d(1,1);
            case EAST -> new Vector2d(1,0);
            case SOUTHEAST -> new Vector2d(1,-1);
            case SOUTH -> new Vector2d(0,-1);
            case SOUTHWEST -> new Vector2d(-1,-1);
            case WEST -> new Vector2d(-1,0);
            case NORTHWEST -> new Vector2d(-1,1);

        };
    }

    public MapDirection back(){
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
            case NORTHEAST -> SOUTHWEST;
            case NORTHWEST -> SOUTHEAST;
            case SOUTHEAST -> NORTHWEST;
            case SOUTHWEST -> NORTHEAST;
        };
    }

}
