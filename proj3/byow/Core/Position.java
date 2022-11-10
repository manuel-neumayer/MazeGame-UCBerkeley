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

    /* Return an array of all four possible step directions, but in random order! */
    public static Step[] randomSteps() {
        /*NOT IMPLEMENTED YET*/
        return Steps;
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
        public Step[] orthogonalSteps;

        public Step(int x, int y) {
            if (!(x == 0 && y != 0) || (x != 0 && y == 0)) {
                throw new IllegalArgumentException("A step has to be parallel to the grid!");
            }

            this.x = x;
            this.y = y;

            /* Determine what two steps are orthognal to this step */

            if (this.x == 0) {
                orthogonalSteps = new Step[]{new Step(-1, 0), new Step(1, 0)};
            } else {
                orthogonalSteps = new Step[]{new Step(0, -1), new Step(0, 1)};
            }
        }
    }
}
