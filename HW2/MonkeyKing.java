public class MonkeyKing {

    static Monkey monkeys[] = new Monkey[100];

    public static void main(String[] args) {
        MonkeyKing king = new MonkeyKing();
    
        for (int i =0; i < monkeys.length; i++) {
            monkeys[i] = new Monkey(king, 0 == (int)(Math.random()*2), 3);
            new Thread(monkeys[i]).start();
        } 
    }
    
    final static int NO_FLOW = 0,
                     GOING_LEFT = 1,
                     GOING_RIGHT = 2;

    int trafficDirection,
        monkeysOnRope;

    public MonkeyKing() {
        trafficDirection = NO_FLOW;
        monkeysOnRope = 0;
    }

    public synchronized void ClimbRope(int direction) {
        while (monkeysOnRope > 0 && 
               !(monkeysOnRope < 3 && trafficDirection == direction)) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        // System.out.println("monkey going "+((direction == GOING_LEFT) ? "left" : "right"));

        monkeysOnRope += 1;
        trafficDirection = direction;
    }

    public synchronized void LeaveRope() {
        monkeysOnRope -= 1;
        // System.out.println("monkey leaving "+((trafficDirection == GOING_LEFT) ? "left" : "right"));
        notifyAll(); 
    }
    
    // for testing purposes:

    static class Monkey implements Runnable {
        final static boolean ON_LEFT = true,
                             ON_RIGHT = false;

        MonkeyKing king;

        // false = right side, true = left side
        boolean side;
        public int direction;
        int runs;
        
        public Monkey(MonkeyKing king, boolean side, int runs) {
            this.king = king;
            this.side = side;
            this.runs = runs;
        }

        static synchronized void checkMonkeys(MonkeyKing king) {
            int dir = king.trafficDirection,
                count = 0;

            for (int i =0; i < monkeys.length; i++) {
                if (monkeys[i] != null) {
                    if (monkeys[i].direction == dir) {
                        count++;
                    } else if (monkeys[i].direction != NO_FLOW) {
                        System.out.println("monkey collision!!!");
                    }
                }
            } 

            if (count > 3) {
                System.out.println("too many monkeys!!!");
            }
        }

        private int getDesiredDirection() {
            if (ON_LEFT == side) {
                // on left side, going to right
                return GOING_RIGHT;
            } else {
                // on right side, going to left
                return GOING_LEFT;
            }
        }

        public void run() {
            for (int i = 0; i < runs; i++) {
                king.ClimbRope(getDesiredDirection());
                direction = getDesiredDirection();
                
                checkMonkeys(king);
                
                try {
                    Thread.sleep(10 + ((int)(Math.random()*10)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                direction = NO_FLOW;
                side = !side;
                king.LeaveRope();
            }
        }
    }
}
