package byow.Core;

class Position {
    public static Step Up = new Step(0, 1);
    public static Step Down = new Step(0, -1);
    public static Step Left = new Step(-1, 0);
    public static Step Right = new Step(1, 0);
    public static Step[] Steps = new Step[]{Up, Down, Left, Right};
    private int x;
    private int y;

    public static Position[][] positionGrid;

    public static void setUpPositionGrid(int w, int h) {
        positionGrid = new Position[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                positionGrid[i][j] = new Position(i, j);
            }
        }
    }

    public static Position getReferencePosition(Position position) {
        return positionGrid[position.x()][position.y()];
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object position) {
        if (position instanceof Position) {
            if (((Position) position).x() == x && ((Position) position).y() == y) {
                return true;
            }
        }
        return false;
    }

    public Position add(Step step) {
        x += step.x;
        y += step.y;
        return this;
    }

    public static Position add(Position position, Step step) {
        return new Position(position.x + step.x, position.y + step.y);
    }

    public static Position sub(Position position, Step step) {
        return new Position(position.x - step.x, position.y - step.y);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public Position copy() {
        return new Position(x, y);
    }

    public static class Step {
        public int x;
        public int y;

        private Step inverse = null;
        private Step[] orthogonalSteps = null;

        public Step(int stepX, int stepY) {
            if (!((stepX == 0 && stepY != 0) || (stepX != 0 && stepY == 0))) {
                throw new IllegalArgumentException("A step has to be parallel to the grid!");
            }

            x = stepX;
            y = stepY;
        }

        @Override
        public boolean equals(Object step) {
            if (step instanceof Step) {
                if (((Step) step).x == x && ((Step) step).y == y) {
                    return true;
                }
            }
            return false;
        }

        public Position.Step inverse() {
            if (inverse != null) {
                return inverse;
            }
            if (x == 0) {
                inverse = new Step(0, -y);
            } else {
                inverse = new Step(-x, 0);
            }
            return inverse;
        }

        /* Return the two steps that are orthogonal to this step */
        public Step[] orthogonalSteps() {
            if (orthogonalSteps != null) {
                return orthogonalSteps;
            }
            if (this.x == 0) {
                orthogonalSteps = new Step[]{Left, Right};
            } else {
                orthogonalSteps = new Step[]{Down, Up};
            }
            return orthogonalSteps;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
