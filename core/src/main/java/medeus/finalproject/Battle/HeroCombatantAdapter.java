package medeus.finalproject.Battle;

import medeus.finalproject.Entities.Player;
import medeus.finalproject.Battle.Combatant;

public class HeroCombatantAdapter implements Combatant {

    private Player player;

    public HeroCombatantAdapter(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return "Hero";
    }

    @Override
    public int getHP() {
        return player.getHp();
    }

    @Override
    public int getAttack() {
        return player.getAttack();
    }

    @Override
    public void takeDamage(int damage) {
        player.takeDamage(damage);
    }

    @Override
    public boolean isAlive() {
        return player.getHp() > 0;
    }
}
