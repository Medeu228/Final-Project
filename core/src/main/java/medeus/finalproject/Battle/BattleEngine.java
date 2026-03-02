package medeus.finalproject.Battle;

import medeus.finalproject.Battle.Combatant;

public class BattleEngine {

    private static BattleEngine instance;

    private BattleEngine() {}

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public void fight(Combatant a, Combatant b) {

        System.out.println("Battle started: " + a.getName() + " vs " + b.getName());

        while (a.isAlive() && b.isAlive()) {

            b.takeDamage(a.getAttack());
            System.out.println(a.getName() + " hits for " + a.getAttack());

            if (!b.isAlive()) break;

            a.takeDamage(b.getAttack());
            System.out.println(b.getName() + " hits for " + b.getAttack());
        }

        if (a.isAlive()) {
            System.out.println("Winner: " + a.getName());
        } else {
            System.out.println("Winner: " + b.getName());
        }
    }
}
