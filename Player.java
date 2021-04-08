import java.util.*;


import java.io.*;
import java.math.*;

/**
 * Save humans, destroy zombies!
 **/
class Player {

    static class Human extends Character {

        Map<Zombie, Double> zombieDistance = new HashMap<>();
		public Human(final int x, final int y, final int id, final int aX, final int aY) {
			super(x, y, id, aX, aY);
		}

        public void addZombie(final Zombie z) {
            final double d = this.distanceTo(z);
            zombieDistance.put(z, d);
            z.setDistanceToNearest(d, this);
        }

        public void addZombies(final List<Zombie> zombies) {
            zombies.forEach(z -> addZombie(z));
        }

        public void getZombiesDistance() {
            zombieDistance.entrySet().forEach(entry -> System.err.println("DIST->" +this.id +" "+entry.getKey().id + " " + entry.getValue()));
        }

        public Double getZombiesDistance(Zombie z) {
            return zombieDistance.get(z);
        }

        public boolean canBySaved() {
            boolean saved = true;
            for(final Map.Entry<Zombie, Double> entry: zombieDistance.entrySet()) {
                final double zombieSteps = (entry.getValue() - 400)/400;
                final double ashSteps = (distance - 2000)/1000;
                System.err.println(this.id +" " + entry.getKey().id +" " + zombieSteps +" " +ashSteps);
                if(zombieSteps<ashSteps) {
                    saved = false;
                    break;
                }
            }
            return saved;
        }

    }

    static class Zombie extends Character {
        double distanceToNearest = 1000000;
        Human nearestHuman;
        int nextX;
        int nextY;

		public Zombie(final int x, final int y, final int id, final int aX, final int aY, final int nextX, final int nextY) {
			super(x, y, id, aX, aY);
            this.nextX = nextX;
            this.nextY = nextY;
		}

        public void setDistanceToNearest(final double distanceToNearest, final Human h) {
            if(distanceToNearest < this.distanceToNearest) {
                this.distanceToNearest = distanceToNearest;
                nearestHuman = h;
            }
        }

        public double getDistanceToNearest() {
            return distanceToNearest;
        }

        public void goTo() {
             System.out.println(x +" " +y);
        }

		public void recalculate(List<Player.Human> humans) {
			distanceToNearest = 1000000;
            nearestHuman = null;
            for(Human h : humans) {
                setDistanceToNearest(h.getZombiesDistance(this), h);
            }
		}

    }

    static class Character implements Comparable<Character>{
        int x;
        int y;
        int id;
        double distance;

        public Character(final int x, final int y, final int id, final int aX, final int aY) {
            this.x = x;
            this.y = y;
            this.id = id;
            distance = Math.sqrt((aX - x) * (aX - x) + (aY -y) * (aY -y));
        }

        public double getDistance() {
            return distance;
        }

        public double distanceTo(final Character o) {
            return Math.sqrt((o.x - x) * (o.x - x) + (o.y -y) * (o.y -y));
        }

        public void goTo() {
             System.out.println(x +" " +y);
        }

		@Override
		public int compareTo(final Character o) {
			return (int)(distance - o.distance);
		}

    }

    public static void main(final String args[]) {
        final Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            final int x = in.nextInt();
            final int y = in.nextInt();
            final int humanCount = in.nextInt();
            final List<Human> humans = new ArrayList<>();
            for (int i = 0; i < humanCount; i++) {
                final int humanId = in.nextInt();
                final int humanX = in.nextInt();
                final int humanY = in.nextInt();
                humans.add(new Human(humanX, humanY, humanId, x, y));
            }
            final List<Zombie> zombies = new ArrayList<>();
            final int zombieCount = in.nextInt();
            for (int i = 0; i < zombieCount; i++) {
                final int zombieId = in.nextInt();
                final int zombieX = in.nextInt();
                final int zombieY = in.nextInt();
                final int zombieXNext = in.nextInt();
                final int zombieYNext = in.nextInt();
                zombies.add(new Zombie(zombieX, zombieY, zombieId, x, y, zombieXNext, zombieYNext));
            }

            humans.forEach(h-> h.addZombies(zombies));
            humans.forEach(h-> System.err.println(h.id +" " + h.canBySaved()));
            humans.removeIf(h-> !h.canBySaved());
            zombies.forEach(z-> z.recalculate(humans));
            humans.forEach(h-> h.getZombiesDistance());
            humans.forEach(h-> System.err.println(h.id +" " + h.canBySaved()));
            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            humans.sort(Comparator.comparing(Human::getDistance));
            zombies.sort(Comparator.comparing(Zombie::getDistanceToNearest).thenComparing(Comparator.comparing(Zombie::getDistance)));
            if(!humans.isEmpty()) {
                final Human h = humans.get(0);
                Zombie z = null;
                if(!zombies.isEmpty()) {
                    z = zombies.get(0);
                }
                if(z != null) {
                    z.goTo();
                }else {
                    h.goTo();
                }
                
            }else {
                System.out.println("0 0"); // Your destination coordinates
            }
        }
    }
}
