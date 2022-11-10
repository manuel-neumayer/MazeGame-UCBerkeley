package byow.Core;

class Position {
    public static Step Up = new Step(0, 1);
    public static Step Down = new Step(0, -1);
    public static Step Left = new Step(-1, 0);
    public static Step Right = new Step(1, 0);
    public static Step[] Steps = new Step[]{Up, Down, Left, Right};
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void add(Step step) {
        x += step.x;
        y += step.y;
    }

    public static Position add(Position position, Step step) {
        return new Position(position.x + step.x, position.y + step.y);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public static class Step {
        public int x;
        public int y;

        public Step(int stepX, int stepY) {
            if (!((stepX == 0 && stepY != 0) || (stepX != 0 && stepY == 0))) {
                throw new IllegalArgumentException("A step has to be parallel to the grid!");
            }

            x = stepX;
            y = stepY;
        }

        /* Return the two steps that are orthogonal to this step */
        public Step[] orthogonalSteps() {
            if (this.x == 0) {
                return new Step[]{Left, Right};
            } else {
                return new Step[]{Down, Up};
            }
        }
    }
}
